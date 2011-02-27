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
