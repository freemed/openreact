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

package com.freemedforms.openreact.servlet;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.util.log.Log;

import com.freemedforms.openreact.db.DbLoader;
import com.freemedforms.openreact.db.DbSchema;
import com.freemedforms.openreact.engine.Configuration;

public class MasterServlet extends HttpServlet {

	private static final long serialVersionUID = 7386618131921561761L;

	static final Logger logger = Logger.getLogger(MasterServlet.class);

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
		Configuration.loadConfiguration(getServletContext().getRealPath(
				DEFAULT_CONFIG), OVERRIDE_CONFIG);

		// Start up data pools
		logger.info("Creating database pool");
		Configuration.createDbPool();

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

}
