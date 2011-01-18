package com.freemedforms.openreact.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.freemedforms.openreact.servlet.MasterServlet;

public class DbLoader {

	static final Logger log = Logger.getLogger(DbLoader.class);

	public DbLoader() {
	}

	public void loadTable(String database, String table, File file)
			throws Exception {
		loadTable(database, table, readFileToString(file));
	}

	/**
	 * Read the entire contents of a file into a single screen.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	protected String readFileToString(File file) throws IOException {
		StringBuilder text = new StringBuilder();
		String EOL = System.getProperty("line.separator");
		Scanner scanner = new Scanner(new FileInputStream(file));
		try {
			while (scanner.hasNextLine()) {
				text.append(scanner.nextLine() + EOL);
			}
		} finally {
			scanner.close();
		}
		return text.toString();
	}

	public void loadStockData() throws URISyntaxException, SQLException {
		if (!getApplicationConfig("initialDataLoad").equals("1")) {
			// Iterate through databases
			File dataLocation = new File(MasterServlet.class.getClassLoader()
					.getResource("/data").toURI());
			String[] databases = dataLocation.list(new FilenameFilter() {
				@Override
				public boolean accept(File file, String name) {
					log.debug("file = " + file + ", name = " + name);
					if (name.startsWith(".")) {
						log.debug("Skipping " + name + " (dot file)");
						return false;
					}
					if (!file.isDirectory()) {
						log.debug("Skipping " + name + " non directory");
						return false;
					}
					return true;
				}
			});
			for (String database : databases) {
				// Iterate through tables
				File tablesLocation = new File(MasterServlet.class
						.getClassLoader().getResource("/data/" + database)
						.toURI());
				String[] tables = tablesLocation.list(new FilenameFilter() {
					@Override
					public boolean accept(File file, String name) {
						log.debug("file = " + file + ", name = " + name);
						if (name.startsWith(".")) {
							log.debug("Skipping " + name + " (dot file)");
							return false;
						}
						if (!name.endsWith(".psv")) {
							log.debug("Skipping " + name
									+ " (needs to end with .psv)");
							return false;
						}
						return true;
					}
				});
				for (String table : tables) {
					File dataFile = new File(MasterServlet.class
							.getClassLoader().getResource(
									"/data/" + database + "/" + table).toURI());
					database = database.replace("drugs-", "");
					table = table.replace(".psv", "");
					log.info("Loading " + database + " / " + table + " from "
							+ dataFile.toString());
					try {
						DbUtil.logMemory();
						loadTable(database, table, dataFile);
						DbUtil.logMemory();
					} catch (Exception e) {
						log.error(database + "." + table + ": load error", e);
					}
				}
			}
		} else {
			log.info("Stock data already loaded.");
		}
	}

	/**
	 * Load data into table.
	 * 
	 * @param database
	 * @param table
	 * @param data
	 * @throws Exception
	 */
	public void loadTable(String database, String table, String data)
			throws Exception {
		Connection c = MasterServlet.getConnection();

		String finalTableName = (database.equals("iam") ? "" : database + "_")
				+ table;

		// First, wipe table to start from scratch.
		truncateTable(finalTableName);

		if (data == null || data.length() < 10) {
			throw new Exception("Not enough data passed.");
		}
		String eol = getLineSeparator(data);

		String[] lines = data.split(eol);
		log.info(finalTableName + ": Found " + lines.length
				+ " lines of data in " + data.length() + " bytes");

		// For the first line, determine field count
		/*
		 * int fieldCount = lines[0].split("\\|").length; if
		 * (lines[0].substring(lines[0].length() - 1, lines[0].length() - 1)
		 * .equals("|")) { fieldCount++; } log.info(finalTableName +
		 * ": Assuming " + fieldCount + " fields");
		 */
		int fieldCount = getFieldCount(finalTableName, lines[0]);

		// Create statement to reuse
		// Form query
		StringBuilder query = new StringBuilder("INSERT INTO ").append(
				finalTableName).append(" VALUES ( ");
		for (int i = 0; i < fieldCount; i++) {
			if (i > 0) {
				query.append(",");
			}
			query.append("?");
		}
		query.append(" );");
		log.trace("QUERY: " + query.toString());
		PreparedStatement insert = c.prepareStatement(query.toString());

		// And loop through all
		int counter = 0;
		for (String line : lines) {
			counter++;
			log.debug(finalTableName + ": Import line " + counter);
			try {
				if (counter % 500 == 0) {
					DbUtil.logMemory();
				}
				insertDataIntoTable(finalTableName, line, fieldCount, insert);
			} catch (Exception ex) {
				log.error("Error line " + counter + ": " + ex.toString());
			}
		}

		// Close connections
		DbUtil.closeSafely(insert);
		DbUtil.closeSafely(c);

		// Call the garbage collector to make sure we don't eat up too much with
		// the import.
		System.gc();

		log.info(finalTableName + ": Finished importing " + lines.length
				+ " records");
	}

	/**
	 * Find the EOL separator.
	 * 
	 * @param data
	 * @return
	 */
	protected String getLineSeparator(String data) {
		return "\n";
	}

	/**
	 * Get the number of fields represented in a single line of a pipe-delimited
	 * file.
	 * 
	 * @param tableName
	 * @param line
	 * @return
	 */
	protected int getFieldCount(String tableName, String line) {
		int fields = 1;
		for (int pos = 0; pos < line.length(); pos++) {
			if (line.charAt(pos) == '|') {
				fields++;
			}
		}
		log.info(tableName + ": Assuming " + fields + " fields");
		return fields;
	}

	/**
	 * Insert line of data into SQL.
	 * 
	 * @param tableName
	 * @param dataLine
	 *            Single line of data.
	 * @param columnCount
	 *            Total number of fields in this line of data.
	 * @param statement
	 * @throws NullPointerException
	 * @throws SQLException
	 */
	protected void insertDataIntoTable(String tableName, String dataLine,
			int columnCount, PreparedStatement statement)
			throws NullPointerException, SQLException {
		if (dataLine == null || dataLine.length() < 2) {
			log.warn(tableName + ": Skipping null or short length line.");
			return;
		}

		String[] fields = dataLine.trim().split("\\|");

		log.trace("'" + dataLine + "'");
		for (int i = 0; i < columnCount; i++) {
			String content = null;
			try {
				if (fields[i] != "") {
					content = fields[i].replace("\\n", "\n").replace("\\r",
							"\r").replace("\\\\", "\\");
				}
			} catch (ArrayIndexOutOfBoundsException ex) {
				log.debug(tableName + ": only " + fields.length + " columns");
			}
			if (content == null || content.length() == 0) {
				content = null;
			}
			statement.setString(i + 1, content);
		}
		statement.execute();

		// Don't close PreparedStatement, since it has to be reused for memory
		// sanity.
	}

	/**
	 * "Truncate" table data (remove all existing table data)
	 * 
	 * @param tableName
	 * @throws NullPointerException
	 * @throws SQLException
	 */
	protected void truncateTable(String tableName) throws NullPointerException,
			SQLException {
		Connection c = MasterServlet.getConnection();
		PreparedStatement truncate = c
				.prepareStatement("TRUNCATE " + tableName);
		ResultSet rs = safelyExecuteStatement(c, truncate);
		DbUtil.closeSafely(rs);
		DbUtil.closeSafely(truncate);
		DbUtil.closeSafely(c);
	}

	/**
	 * Get an application config variable value.
	 * 
	 * @param key
	 * @return
	 * @throws SQLException
	 */
	public String getApplicationConfig(String key) throws SQLException {
		Connection c = MasterServlet.getConnection();
		PreparedStatement q = c
				.prepareStatement("SELECT fValue FROM tApplicationState WHERE fKey = ?");
		q.setString(1, key);
		ResultSet rs = safelyExecuteStatement(c, q);
		rs.first();
		String result = rs.getString(1);
		DbUtil.closeSafely(rs);
		DbUtil.closeSafely(q);
		DbUtil.closeSafely(c);
		return result;
	}

	/**
	 * Set an application config variable value.
	 * 
	 * @param key
	 * @param value
	 * @throws SQLException
	 */
	public void setApplicationConfig(String key, String value)
			throws SQLException {
		Connection c = MasterServlet.getConnection();

		// Remove old entry, if it exists;
		PreparedStatement r = c
				.prepareStatement("DELETE FROM tApplicationState WHERE fKey = ?;");
		r.setString(1, key);
		r.execute();
		DbUtil.closeSafely(r);

		// Add new one
		PreparedStatement q = c
				.prepareStatement("INSERT INTO tApplicationState ( fKey, fValue ) VALUES ( ?, ? );");
		q.setString(1, key);
		q.setString(2, value);
		q.execute();
		DbUtil.closeSafely(q);

		// Close everything
		DbUtil.closeSafely(c);
	}

	/**
	 * Low level wrapper to execute a prepared statement.
	 * 
	 * @param c
	 * @param cStmt
	 * @throws NullPointerException
	 * @throws SQLException
	 */
	protected ResultSet safelyExecuteStatement(Connection c,
			PreparedStatement cStmt) throws NullPointerException, SQLException {
		try {
			cStmt.execute();
			return cStmt.getResultSet();
		} catch (NullPointerException npe) {
			log.error(npe);
			DbUtil.closeSafely(cStmt);
			throw npe;
		} catch (SQLException npe) {
			log.error(npe);
			DbUtil.closeSafely(cStmt);
			throw npe;
		} finally {
			// DbUtil.closeSafely(c);
		}
	}

}
