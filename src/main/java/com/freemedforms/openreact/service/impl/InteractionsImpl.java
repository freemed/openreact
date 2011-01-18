package com.freemedforms.openreact.service.impl;

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.cxf.annotations.GZIP;

import com.freemedforms.openreact.engine.DrugLookup;
import com.freemedforms.openreact.service.Interactions;
import com.freemedforms.openreact.types.CodeSet;
import com.freemedforms.openreact.types.Drug;
import com.freemedforms.openreact.types.DrugInteraction;

@GZIP
@WebService(endpointInterface = "com.freemedforms.openreact.service.Interactions")
public class InteractionsImpl implements Interactions {

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@GET
	@Path("find/{codeset}/{name}")
	@Produces("application/json")
	public Drug[] findDrug(CodeSet codeset, String name) {
		return DrugLookup.findDrug(codeset, name).toArray(new Drug[0]);
	}

}
