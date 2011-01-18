package com.freemedforms.openreact.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.freemedforms.openreact.db.DbUtil;
import com.freemedforms.openreact.servlet.MasterServlet;
import com.freemedforms.openreact.types.CodeSet;
import com.freemedforms.openreact.types.Drug;

public class DrugLookup {

	static final Logger log = Logger.getLogger(DrugLookup.class);

	public static int LOOKUP_LIMIT = 20;

	public static String Q_DRUG_LOOKUP = "SELECT " + " DRUGS.NAME AS NAME "
			+ " FROM DRUGS "
			+ " LEFT OUTER JOIN SOURCES ON SOURCES.SID = DRUGS.SID "
			+ " WHERE NAME LIKE ? AND LANG=? AND VALID=1 " + " LIMIT "
			+ LOOKUP_LIMIT;

	public static List<Drug> findDrug(CodeSet codeset, String name) {
		List<Drug> result = new ArrayList<Drug>();
		Connection c = MasterServlet.getConnection();

		PreparedStatement q = null;
		try {
			q = c.prepareStatement(Q_DRUG_LOOKUP);
			q.setString(1, name);
			q.setString(2, codeset.getValue());
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
				Drug drug = new Drug();
				drug.setDrugName(rs.getString("NAME"));
				drug.setDrugCode(rs.getString("CODE"));
				drug.setCodeSet(codeset);
				result.add(drug);
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

}
