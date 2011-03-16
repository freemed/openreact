/***************************************************************************
 *  The FreeMedForms project is a set of free, open source medical         *
 *  applications.                                                          *
 *  (C) 2008-2011 by Eric MAEKER, MD (France) <eric.maeker@free.fr>        *
 *  All rights reserved.                                                   *
 *                                                                         *
 *  This program is free software: you can redistribute it and/or modify   *
 *  it under the terms of the GNU General Public License as published by   *
 *  the Free Software Foundation, either version 3 of the License, or      *
 *  (at your option) any later version.                                    *
 *                                                                         *
 *  This program is distributed in the hope that it will be useful,        *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of         *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the          *
 *  GNU General Public License for more details.                           *
 *                                                                         *
 *  You should have received a copy of the GNU General Public License      *
 *  along with this program (COPYING.FREEMEDFORMS file).                   *
 *  If not, see <http://www.gnu.org/licenses/>.                            *
 ***************************************************************************/
/***************************************************************************
 *   OpenReact Web Portal                                                  *
 *   Main Developer : Jeff Buchbinder <jeff@freemedsoftware.org>           *
 *   Contributors :                                                        *
 *       NAME <MAIL@ADDRESS>                                               *
 ***************************************************************************/

package com.freemedforms.openreact.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.freemedforms.openreact.db.DbUtil;
import com.freemedforms.openreact.types.CodeSet;
import com.freemedforms.openreact.types.Drug;

public class DrugLookup {

	static final Logger log = Logger.getLogger(DrugLookup.class);

	public static int LOOKUP_LIMIT = 20;

	public static String Q_DRUG_LOOKUP = "SELECT " + " D.NAME AS NAME, "
			+ " D.DID AS CODE " + " FROM DRUGS D "
			+ " LEFT OUTER JOIN SOURCES S ON S.SID = D.SID "
			+ " WHERE D.NAME LIKE ? AND S.LANG=? AND D.VALID=1 "
			+ " GROUP BY D.NAME " + " LIMIT " + LOOKUP_LIMIT;

	public static String Q_DRUG_BY_ID = "SELECT "
			+ " D.NAME AS NAME, D.DID AS CODE, S.LANG AS CS "
			+ " FROM DRUGS D " + " LEFT OUTER JOIN SOURCES S ON S.SID = D.SID "
			+ " WHERE D.DID = ? LIMIT 1";

	public static List<Drug> findDrug(CodeSet codeset, String name) {
		List<Drug> result = new ArrayList<Drug>();
		Connection c = Configuration.getConnection();

		PreparedStatement q = null;
		try {
			q = c.prepareStatement(Q_DRUG_LOOKUP);
			q.setString(1, name + "%");
			q.setString(2, codeset.getLang());
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

		DbUtil.closeSafely(rs);
		DbUtil.closeSafely(q);
		DbUtil.closeSafely(c);
		return result;
	}

	public static Drug getDrugById(Long drugId) {
		Drug result = new Drug();
		Connection c = Configuration.getConnection();

		PreparedStatement q = null;
		try {
			q = c.prepareStatement(Q_DRUG_BY_ID);
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
			rs.next();
			result.setDrugId(drugId);
			result.setDrugName(rs.getString("NAME"));
			result.setDrugCode(rs.getString("CODE"));
			result.setCodeSet(CodeSet.valueOf(rs.getString("CS")));
		} catch (SQLException e) {
			log.error(e);
			DbUtil.closeSafely(rs);
			DbUtil.closeSafely(q);
			DbUtil.closeSafely(c);
			return result;
		}

		DbUtil.closeSafely(rs);
		DbUtil.closeSafely(q);
		DbUtil.closeSafely(c);
		return result;
	}

}
