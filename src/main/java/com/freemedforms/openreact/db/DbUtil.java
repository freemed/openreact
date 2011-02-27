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

package com.freemedforms.openreact.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class DbUtil {

	static final Logger log = Logger.getLogger(DbUtil.class);

	public static void closeSafely(Connection c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception ex) {
			}
		}
	}

	public static void closeSafely(Statement c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception ex) {
			}
		}
	}

	public static void closeSafely(PreparedStatement c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception ex) {
			}
		}
	}

	public static void closeSafely(CallableStatement c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception ex) {
			}
		}
	}

	public static void closeSafely(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception ex) {
			}
		}
	}

	public static void logMemory() {
		log.info("MEM: used = "
				+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
						.freeMemory()) + " / free = "
				+ Runtime.getRuntime().freeMemory());
	}

}
