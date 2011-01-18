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
