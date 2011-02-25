package com.freemedforms.openreact.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.freemedforms.openreact.db.DbUtil;
import com.freemedforms.openreact.servlet.MasterServlet;
import com.freemedforms.openreact.types.Drug;
import com.freemedforms.openreact.types.DrugInteraction;
import com.freemedforms.openreact.types.InteractionType;

public class InteractionLookup {

	static final Logger log = Logger.getLogger(InteractionLookup.class);

	public static int LOOKUP_LIMIT = 20;

	/**
	 * Query to lookup ATC codes for a drug id.
	 */
	public static String Q_ATC_LOOKUP = "SELECT " + " L.ATC_ID AS ATC_ID "
			+ " FROM DRUGS D "
			+ " LEFT OUTER JOIN COMPOSITION C ON D.DID = C.DID "
			+ " LEFT OUTER JOIN LK_MOL_ATC L ON L.MID = C.MID "
			+ " WHERE D.DID=? AND NOT ISNULL(L.ATC_ID)";

	/**
	 * Query to find interactions from ATC codes.
	 */
	public static String Q_ATC_INTERACTIONS = "SELECT " + " I.IAID AS ID, "
			+ " I.ATC_ID1 AS ATC_ID1, I.ATC_ID2 AS ATC_ID2, "
			+ " K.TYPE AS TYPE, K.WWW AS WWW " + " FROM INTERACTIONS I "
			+ " LEFT OUTER JOIN IA_IAK L ON L.IAID = I.IAID "
			+ " LEFT OUTER JOIN IAKNOWLEDGE K ON K.IAKID = L.IAKID "
			+ " WHERE ( FIND_IN_SET(ATC_ID1, ?) AND FIND_IN_SET(ATC_ID2, ?) );";

	/**
	 * Get ATC codes for a drug code.
	 * 
	 * @param drugId
	 * @return
	 */
	public static List<Integer> findAtcFromDrug(Long drugId) {
		List<Integer> result = new ArrayList<Integer>();
		Connection c = MasterServlet.getConnection();

		PreparedStatement q = null;
		try {
			q = c.prepareStatement(Q_ATC_LOOKUP);
			q.setLong(1, drugId);
		} catch (SQLException e) {
			log.error(e);
			DbUtil.closeSafely(q);
			DbUtil.closeSafely(c);
			return result;
		}

		ResultSet rs = null;
		try {
			rs = q.executeQuery();
		} catch (SQLException e) {
			log.error(e);
			DbUtil.closeSafely(rs);
			DbUtil.closeSafely(q);
			DbUtil.closeSafely(c);
			return result;
		}
		try {
			while (rs.next()) {
				result.add(rs.getInt("ATC_ID"));
			}
		} catch (SQLException e) {
			log.error(e);
			DbUtil.closeSafely(rs);
			DbUtil.closeSafely(q);
			DbUtil.closeSafely(c);
			return result;
		}

		DbUtil.closeSafely(q);
		DbUtil.closeSafely(c);
		return result;
	}

	/**
	 * Given a list of DRUG ids, resolve interactions.
	 * 
	 * @param drugIds
	 * @return
	 */
	public static List<DrugInteraction> findInteractionsFromDrugs(
			List<Long> drugIds) {
		List<DrugInteraction> result = new ArrayList<DrugInteraction>();

		// Create a mapping of all drug ids -> ATC codes
		Map<Integer, List<Long>> mapAtcToDrugs = new HashMap<Integer, List<Long>>();
		for (Long drugId : drugIds) {
			List<Integer> atcCodesForDrug = findAtcFromDrug(drugId);
			for (Integer atcCodeForDrug : atcCodesForDrug) {
				if (mapAtcToDrugs.containsKey(atcCodeForDrug)) {
					// If it contains an array already, append
					mapAtcToDrugs.get(atcCodeForDrug).add(drugId);
				} else {
					// Create
					List<Long> newList = new ArrayList<Long>();
					newList.add(drugId);
					mapAtcToDrugs.put(atcCodeForDrug, newList);
				}
			}
		}

		@SuppressWarnings("unchecked")
		List<DrugInteraction> atcInteractions = findInteractionsFromAtc((List<Integer>) mapAtcToDrugs
				.keySet());

		// Keep cache of drugs so we only look each one up once.
		Map<Long, Drug> drugCache = new HashMap<Long, Drug>();

		for (DrugInteraction atcInteraction : atcInteractions) {
			// Get the actual ATC codes which were found in the interaction
			Integer atc1 = Integer.parseInt(atcInteraction.getAtc1());
			Integer atc2 = Integer.parseInt(atcInteraction.getAtc2());

			// Resolve these to the list of possible drugs
			List<Long> atc1list = mapAtcToDrugs.get(atc1);
			List<Long> atc2list = mapAtcToDrugs.get(atc2);

			// More or less recurse through and populate
			for (Long atc1loop : atc1list) {
				if (!drugCache.containsKey(atc1loop)) {
					drugCache.put(atc1loop, DrugLookup.getDrugById(atc1loop));
				}
				for (Long atc2loop : atc2list) {
					if (!drugCache.containsKey(atc2loop)) {
						drugCache.put(atc2loop, DrugLookup
								.getDrugById(atc2loop));
					}

					DrugInteraction ni = new DrugInteraction();

					// Copy basic stuff from the object we're working with
					ni.setAtc1(atcInteraction.getAtc1());
					ni.setAtc2(atcInteraction.getAtc2());
					ni.setId(atcInteraction.getId());
					ni.setLevel(atcInteraction.getLevel());
					ni.setWebLink(atcInteraction.getWebLink());

					// ... and now drop the drugs in
					ni.setDrug1(drugCache.get(atc1loop));
					ni.setDrug2(drugCache.get(atc2loop));

					// ... and push it into the result list
					result.add(ni);
				}
			}
		}

		return result;
	}

	/**
	 * Get a list of potential drug interactions from a list of ATC ID codes.
	 * 
	 * @param atcIds
	 * @return
	 */
	public static List<DrugInteraction> findInteractionsFromAtc(
			List<Integer> atcIds) {
		List<DrugInteraction> result = new ArrayList<DrugInteraction>();
		Connection c = MasterServlet.getConnection();

		String findList = createSetForFind(atcIds);

		PreparedStatement q = null;
		try {
			q = c.prepareStatement(Q_ATC_LOOKUP);
			q.setString(1, findList);
			q.setString(2, findList);
		} catch (SQLException e) {
			log.error(e);
			DbUtil.closeSafely(q);
			DbUtil.closeSafely(c);
			return result;
		}

		ResultSet rs = null;
		try {
			rs = q.executeQuery();
		} catch (SQLException e) {
			log.error(e);
			DbUtil.closeSafely(rs);
			DbUtil.closeSafely(q);
			DbUtil.closeSafely(c);
			return result;
		}
		try {
			while (rs.next()) {
				DrugInteraction di = new DrugInteraction();
				di.setId(rs.getInt("ID"));
				di.setAtc1(rs.getString("ATC_ID1"));
				di.setAtc2(rs.getString("ATC_ID2"));
				di.setWebLink(rs.getString("WWW"));
				di.setLevel(InteractionType.valueOf(rs.getString("TYPE")));
				// NOTE: Do not set drug1 and drug2, those would need to be
				// resolved from the matching ATC codes, which should not be
				// done in this function.
				result.add(di);
			}
		} catch (SQLException e) {
			log.error(e);
			DbUtil.closeSafely(rs);
			DbUtil.closeSafely(q);
			DbUtil.closeSafely(c);
			return result;
		}

		DbUtil.closeSafely(q);
		DbUtil.closeSafely(c);
		return result;
	}

	/**
	 * Create a string usable for a FIND_IN_SET clause from a <List<Integer>>.
	 * 
	 * @param iS
	 * @return
	 */
	protected static String createSetForFind(List<Integer> iS) {
		StringBuilder sb = new StringBuilder();
		for (Integer i : iS) {
			if (sb.length() > 1) {
				sb.append(',');
			}
			sb.append(i.toString());
		}
		return sb.toString();
	}

}
