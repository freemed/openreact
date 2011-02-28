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

import java.beans.PropertyVetoException;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class Configuration {

	static final Logger logger = Logger.getLogger(Configuration.class);

	protected static CompositeConfiguration compositeConfiguration = null;

	protected static ComboPooledDataSource comboPooledDataSource = null;

	public static void loadConfiguration(String defaultConfig,
			String overrideConfig) {
		logger.trace("Entered loadConfiguration");
		if (compositeConfiguration == null) {
			logger.info("Configuration object not present, instantiating");
			compositeConfiguration = new CompositeConfiguration();

			PropertiesConfiguration defaults = null;
			try {
				logger
						.info("Attempting to create PropertiesConfiguration object for DEFAULT_CONFIG");
				defaults = new PropertiesConfiguration(defaultConfig);
				logger.info("Loading default configuration from "
						+ defaultConfig);
				defaults.load();
			} catch (ConfigurationException e) {
				logger.error("Could not load default configuration from "
						+ defaultConfig);
				logger.error(e);
			}
			if (overrideConfig != null) {
				PropertiesConfiguration overrides = null;
				try {
					logger
							.info("Attempting to create PropertiesConfiguration object for OVERRIDE_CONFIG");
					overrides = new PropertiesConfiguration();
					logger.info("Setting file for OVERRIDE_CONFIG");
					overrides.setFile(new File(overrideConfig));
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

	public static CompositeConfiguration getConfiguration() {
		return compositeConfiguration;
	}

	public static void createDbPool() throws ServletException {
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
