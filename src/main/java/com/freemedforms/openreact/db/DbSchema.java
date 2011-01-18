package com.freemedforms.openreact.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.freemedforms.openreact.servlet.MasterServlet;

public class DbSchema {

	static final Logger log = Logger.getLogger(DbSchema.class);

	/**
	 * Attempt to run a database patch.
	 * 
	 * @param patchFilename
	 * @return Success.
	 * @throws SQLException 
	 */
	public static boolean applyPatch(String patchFilename) throws SQLException {
		Connection c = MasterServlet.getConnection();

		String patch = null;

		Scanner scanner;
		try {
			scanner = new Scanner(new File(patchFilename)).useDelimiter("\\Z");
			patch = scanner.next();
			scanner.close();
		} catch (FileNotFoundException ex) {
			log.error(ex);
			return false;
		}

		Statement cStmt = c.createStatement();
		boolean status = false;
		try {
			log.debug("Using patch length = " + patch.length());
			cStmt.execute(patch);
			//cStmt = c.prepareStatement(patch);
			//cStmt.execute();
			log.info("Patch succeeded");
			status = true;
		} catch (NullPointerException npe) {
			log.error("Caught NullPointerException", npe);
		} catch (Throwable e) {
			log.error(e.toString());
		} finally {
			DbUtil.closeSafely(cStmt);
			DbUtil.closeSafely(c);
		}

		return status;
	}

	/**
	 * Determine if a patch has been applied yet.
	 * 
	 * @param patchName
	 * @return Success.
	 */
	public static boolean isPatchApplied(String patchName) {
		Connection c = MasterServlet.getConnection();

		int found = 0;

		PreparedStatement cStmt = null;
		try {
			cStmt = c.prepareStatement("SELECT COUNT(*) FROM tPatch "
					+ " WHERE patchName = ? " + ";");
			cStmt.setString(1, patchName);

			boolean hadResults = cStmt.execute();
			if (hadResults) {
				ResultSet rs = cStmt.getResultSet();
				rs.next();
				found = rs.getInt(1);
				rs.close();
			}
		} catch (NullPointerException npe) {
			log.error("Caught NullPointerException", npe);
		} catch (Throwable e) {
		} finally {
			DbUtil.closeSafely(cStmt);
			DbUtil.closeSafely(c);
		}

		return (boolean) (found > 0);
	}

	/**
	 * Record record of patch into tPatch table so that patches only run once.
	 * 
	 * @param patchName
	 * @return Success.
	 */
	public static boolean recordPatch(String patchName) {
		Connection c = MasterServlet.getConnection();

		boolean status = false;
		PreparedStatement cStmt = null;
		try {
			cStmt = c.prepareStatement("INSERT INTO tPatch "
					+ " ( patchName, stamp ) " + " VALUES ( ?, NOW() ) " + ";");
			cStmt.setString(1, patchName);

			cStmt.execute();
			status = true;
		} catch (NullPointerException npe) {
			log.error("Caught NullPointerException", npe);
		} catch (SQLException sq) {
			log.error("Caught SQLException", sq);
		} catch (Throwable e) {
		} finally {
			DbUtil.closeSafely(cStmt);
			DbUtil.closeSafely(c);
		}

		return status;
	}

	public static void dbPatcher(String patchLocation) {
		log.info("Database patching started for " + patchLocation);

		File patchDirectoryObject = new File(patchLocation);
		String[] children = patchDirectoryObject.list(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				log.debug("file = " + file + ", name = " + name);
				if (name.startsWith(".")) {
					log.debug("Skipping " + name + " (dot file)");
					return false;
				}
				if (!name.endsWith(".sql")) {
					log.debug("Skipping " + name + " (doesn't end with .sql)");
					return false;
				}
				return true;
			}
		});
		if (children != null) {
			// Sort all patches into name order.
			Arrays.sort(children);

			// Process patches
			log.info("Found " + children.length + " patches to process");
			for (String patchFilename : children) {
				String patchName = FilenameUtils.getBaseName(patchFilename);
				if (DbSchema.isPatchApplied(patchName)) {
					log.info("Patch " + patchName + " already applied.");
					continue;
				} else {
					log.info("Applying patch " + patchName + ", source file = "
							+ patchFilename);
					boolean success;
					try {
						success = DbSchema.applyPatch(patchDirectoryObject
								.getAbsolutePath()
								+ File.separatorChar + patchFilename);
					} catch (SQLException e) {
						log.error(e);
						success = false;
					}
					if (success) {
						DbSchema.recordPatch(patchName);
					} else {
						log.error("Failed to apply " + patchName
								+ ", stopping patch sequence.");
						return;
					}
				}
			}
		}
		log.info("Database patching completed");
	}

}
