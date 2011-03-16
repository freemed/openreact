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

import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;

import com.freemedforms.openreact.types.CodeSet;
import com.freemedforms.openreact.types.Drug;

public class InteractionsTest extends TestCase {

	public InteractionsTest(String testName) {
		super(testName);
	}

	public static junit.framework.Test suite() {
		return new TestSuite(InteractionsTest.class);
	}

	@Before
	@Override
	public void setUp() throws Exception {
		BasicConfigurator.configure();
		Configuration.loadConfiguration(InteractionsTest.class.getResource(
				"/openreact.properties").toString(), null);
		Configuration.createDbPool();
	}

	@Test
	public void testDrugLookupByName() throws Exception {
		List<Drug> foundDrugs = DrugLookup.findDrug(CodeSet.EN_US, "WARFARIN");
		assertNotNull("DrugLookup.findDrug NOT NULL", foundDrugs);
		boolean found = false;
		for (Drug d : foundDrugs) {
			if (d.getDrugName().contains("WARFARIN")) {
				found = true;
				continue;
			}
		}
		assertTrue("Found WARFARIN for CodeSet.EN_US", found);
	}

	@Test
	public void testDrugLookupById() {
		Long drugId = 62039L;
		Drug testDrug = DrugLookup.getDrugById(drugId);
		assertEquals("EN_US", testDrug.getCodeSet());
		assertEquals("WARFARIN SODIUM", testDrug.getDrugName());
	}

}
