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

package com.freemedforms.openreact.types;

import java.io.Serializable;

public class Drug implements Serializable {

	private static final long serialVersionUID = 2786066280795042789L;

	private long drugId;
	private String drugCode;
	private CodeSet codeSet;
	private Boolean ignoreForInteraction = Boolean.FALSE;
	private String route;
	private String drugName;
	private ActingDuration actingDuration = ActingDuration.UNSPECIFIED_DURATION;

	public String getDrugCode() {
		return drugCode;
	}

	public void setDrugCode(String drugCode) {
		this.drugCode = drugCode;
	}

	public CodeSet getCodeSet() {
		return codeSet;
	}

	public void setCodeSet(CodeSet codeSet) {
		this.codeSet = codeSet;
	}

	public void setIgnoreForInteraction(Boolean ignoreForInteraction) {
		this.ignoreForInteraction = ignoreForInteraction;
	}

	public Boolean getIgnoreForInteraction() {
		return ignoreForInteraction;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getRoute() {
		return route;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugId(long drugId) {
		this.drugId = drugId;
	}

	public long getDrugId() {
		return drugId;
	}

	public void setActingDuration(ActingDuration actingDuration) {
		this.actingDuration = actingDuration;
	}

	public ActingDuration getActingDuration() {
		return actingDuration;
	}

}
