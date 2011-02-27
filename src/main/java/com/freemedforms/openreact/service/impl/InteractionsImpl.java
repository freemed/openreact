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

package com.freemedforms.openreact.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.cxf.annotations.GZIP;
import org.apache.log4j.Logger;

import com.freemedforms.openreact.engine.DrugLookup;
import com.freemedforms.openreact.engine.InteractionLookup;
import com.freemedforms.openreact.service.Interactions;
import com.freemedforms.openreact.types.CodeSet;
import com.freemedforms.openreact.types.Drug;
import com.freemedforms.openreact.types.DrugInteraction;

@GZIP
@WebService(endpointInterface = "com.freemedforms.openreact.service.Interactions")
public class InteractionsImpl implements Interactions {

	private static final Logger log = Logger.getLogger(InteractionsImpl.class);

	private static long PROTOCOL_VERSION = 1L;

	@Override
	@GET
	@Path("protocolversion")
	@Produces("application/json")
	public Long getProtocolVersion() {
		return PROTOCOL_VERSION;
	}

	@Override
	@GET
	@Path("interactions/{codeset}/{drugs}")
	@Produces("application/json")
	public DrugInteraction[] findInteractions(CodeSet requestedCodeset,
			Drug[] drugs) {
		log.info("findInteractions(" + requestedCodeset + ", "
				+ (drugs != null ? drugs.length : 0) + ")");
		// Extract all ids from drugs
		List<Long> drugIds = new ArrayList<Long>();
		for (Drug drug : drugs) {
			log.info("Adding drug id " + drug.getDrugId() + " to stack");
			drugIds.add(drug.getDrugId());
		}
		return InteractionLookup.findInteractionsFromDrugs(requestedCodeset,
				drugIds).toArray(new DrugInteraction[0]);
	}

	@Override
	@GET
	@Path("interactionsByIds/{codeset}/{drugs}")
	@Produces("application/json")
	public DrugInteraction[] findInteractionsByIds(CodeSet requestedCodeset,
			Long[] drugs) {
		log.info("findInteractionsByIds(" + requestedCodeset + ", "
				+ (drugs != null ? drugs.length : 0) + ")");
		return InteractionLookup.findInteractionsFromDrugs(requestedCodeset,
				Arrays.asList(drugs)).toArray(new DrugInteraction[0]);
	}

	@Override
	@GET
	@Path("find/{codeset}/{name}")
	@Produces("application/json")
	public Drug[] findDrug(CodeSet codeset, String name) {
		log.info("findDrug(" + codeset + ", " + name + ")");
		return DrugLookup.findDrug(codeset, name).toArray(new Drug[0]);
	}

}
