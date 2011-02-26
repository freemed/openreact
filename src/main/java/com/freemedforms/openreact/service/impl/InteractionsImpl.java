package com.freemedforms.openreact.service.impl;

import java.util.ArrayList;
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
	@Path("find/{codeset}/{name}")
	@Produces("application/json")
	public Drug[] findDrug(CodeSet codeset, String name) {
		log.info("findDrug(" + codeset + ", " + name + ")");
		return DrugLookup.findDrug(codeset, name).toArray(new Drug[0]);
	}

}
