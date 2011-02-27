package com.freemedforms.openreact.service;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.PathParam;

import com.freemedforms.openreact.types.CodeSet;
import com.freemedforms.openreact.types.Drug;
import com.freemedforms.openreact.types.DrugInteraction;

@WebService
public interface Interactions {

	public Long getProtocolVersion();

	public DrugInteraction[] findInteractions(
			@PathParam("codeset") @WebParam(name = "codeset") CodeSet requestedCodeset,
			@PathParam("drugs") @WebParam(name = "drugs") Drug[] drugs);

	public DrugInteraction[] findInteractionsByIds(
			@PathParam("codeset") @WebParam(name = "codeset") CodeSet requestedCodeset,
			@PathParam("drugs") @WebParam(name = "drugs") Long[] drugs);
	
	public Drug[] findDrug(
			@PathParam("codeset") @WebParam(name = "codeset") CodeSet codeset,
			@PathParam("name") @WebParam(name = "name") String name);
}
