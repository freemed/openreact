package com.freemedforms.openreact.servlet;

import java.beans.PropertyVetoException;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.util.log.Log;

import com.freemedforms.openreact.db.DbLoader;
import com.freemedforms.openreact.db.DbSchema;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MasterServlet extends HttpServlet {

	private static final long serialVersionUID = 7386618131921561761L;

	static final Logger logger = Logger.getLogger(MasterServlet.class);

	protected static CompositeConfiguration compositeConfiguration = null;

	protected static ComboPooledDataSource comboPooledDataSource = null;

	public static String DEFAULT_CONFIG = "/WEB-INF/openreact.properties";
	public static String OVERRIDE_CONFIG = System
			.getProperty("openreact.properties");

	/**
	 * Initialize everything.
	 */
	@Override
	public void init() throws ServletException {
		// Start up logging
		loggerInit();

		// Load configuration
		logger.info("Loading configuration");
		loadConfiguration();

		// Start up data pools
		logger.info("Creating database pool");
		createDbPool();

		// Database initialization/patching cycle
		try {
			DbSchema.dbPatcher(this.getClass().getClassLoader().getResource(
					"/schema").toURI().getPath());
		} catch (Exception ex) {
			throw new ServletException(ex);
		}

		// Initial data load, if stock data hasn't been loaded yet.
		DbLoader dbLoader = new DbLoader();
		String initialLoad = "0";
		try {
			initialLoad = dbLoader.getApplicationConfig("initialDataLoad");
		} catch (SQLException e) {
			logger.error(e);
		}
		try {
			if (initialLoad == null || initialLoad.equals("0")) {
				dbLoader.loadStockData();
				dbLoader.setApplicationConfig("initialDataLoad", "1");
			} else {
				Log.info("Initial data load has already been done.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize application-wide log4j logging.
	 * 
	 * @throws ServletException
	 */
	protected void loggerInit() throws ServletException {
		System.out.println("LogggerServlet init() starting.");

		// Attempt to divine base install, and if we're using jetty, shim it
		if (System.getProperty("jetty.home") != null
				&& System.getProperty("jetty.home") != "") {
			System.setProperty("catalina.home", System
					.getProperty("jetty.home"));
		}
		String log4jfile = getInitParameter("log4j-properties");
		System.out.println("log4j-properties: " + log4jfile);
		if (log4jfile != null) {
			String propertiesFilename = getServletContext().getRealPath(
					log4jfile);
			System.out.println("Using file " + propertiesFilename);
			PropertyConfigurator.configure(propertiesFilename);
			logger.info("logger configured.");
		} else {
			String propertiesFilename = getServletContext().getRealPath(
					"/WEB-INF/log4j.properties");
			System.out.println("Using file " + propertiesFilename);
			PropertyConfigurator.configure(propertiesFilename);
			logger.info("logger configured.");
		}
		System.out.println("LoggerServlet init() done.");
	}

	/**
	 * Load configuration from both template and override properties files.
	 */
	protected void loadConfiguration() {
		logger.trace("Entered loadConfiguration");
		if (compositeConfiguration == null) {
			logger.info("Configuration object not present, instantiating");
			compositeConfiguration = new CompositeConfiguration();

			PropertiesConfiguration defaults = null;
			try {
				logger
						.info("Attempting to create PropertiesConfiguration object for DEFAULT_CONFIG");
				defaults = new PropertiesConfiguration(getServletContext()
						.getRealPath(DEFAULT_CONFIG));
				logger.info("Loading default configuration from "
						+ getServletContext().getRealPath(DEFAULT_CONFIG));
				defaults.load();
			} catch (ConfigurationException e) {
				logger.error("Could not load default configuration from "
						+ getServletContext().getRealPath(DEFAULT_CONFIG));
				logger.error(e);
			}
			if (OVERRIDE_CONFIG != null) {
				PropertiesConfiguration overrides = null;
				try {
					logger
							.info("Attempting to create PropertiesConfiguration object for OVERRIDE_CONFIG");
					overrides = new PropertiesConfiguration();
					logger.info("Setting file for OVERRIDE_CONFIG");
					overrides.setFile(new File(OVERRIDE_CONFIG));
					logger.info("Setting reload strategy for OVERRIDE_CONFIG");
					overrides
							.setReloadingStrategy(new FileChangedReloadingStrategy());
					logger.info("Loading OVERRIDE_CONFIG");
					overrides.load();
				} catch (ConfigurationException e) {
					logger.error("Could not load overrides", e);
				} catch (Exception ex) {
					logger.error(ex);
				}
				if (overrides != null) {
					compositeConfiguration.addConfiguration(overrides);
				}
			}
			// Afterwards, add defaults so they're read second.
			compositeConfiguration.addConfiguration(defaults);
		}
	}

	protected void createDbPool() throws ServletException {
		String jdbcUrl = null;
		String jdbcDriver = null;
		System.out.println("Creating db connections");
		try {
			jdbcUrl = getConfiguration().getString("db.url");
			logger.debug("Found db.url string = " + jdbcUrl);
			jdbcDriver = getConfiguration().getString("db.driver");
			logger.debug("Found db.driver string = " + jdbcDriver);
		} catch (Exception ex) {
			logger.error("Could not get db.url", ex);
			throw new ServletException();
		}

		try {
			Class.forName(jdbcDriver).newInstance();
		} catch (Exception ex) {
			logger.error("Unable to load driver.", ex);
			throw new ServletException();
		}

		// Connection pool
		comboPooledDataSource = new ComboPooledDataSource();
		try {
			comboPooledDataSource.setDriverClass(jdbcDriver);
		} catch (PropertyVetoException e) {
			logger.error(e);
			throw new ServletException();
		}
		comboPooledDataSource.setJdbcUrl(jdbcUrl);
		comboPooledDataSource.setDataSourceName("jdbc/openreact");

		comboPooledDataSource.setMinPoolSize(getConfiguration().getInt(
				"c3p0.minPoolSize"));
		comboPooledDataSource.setMaxPoolSize(getConfiguration().getInt(
				"c3p0.maxPoolSize"));

		// Set settings from configuration file
		comboPooledDataSource.setMaxStatements(getConfiguration().getInt(
				"c3p0.maxStatements"));
		comboPooledDataSource.setMaxIdleTime(getConfiguration().getInt(
				"c3p0.maxIdleTime"));
	}

	public CompositeConfiguration getConfiguration() {
		return compositeConfiguration;
	}

	/**
	 * Get an unused database connection from the <ComboPooledDataSource> pool
	 * of db connections.
	 * 
	 * @return
	 */
	public static Connection getConnection() {
		try {
			return comboPooledDataSource.getConnection();
		} catch (SQLException e) {
			logger.error(e);
			return null;
		}
	}

}
