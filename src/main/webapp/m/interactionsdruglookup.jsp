<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%-- 
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
 --%>
<%@ page import="com.freemedforms.openreact.engine.DrugLookup"%>
<%@ page import="com.freemedforms.openreact.types.CodeSet"%>
<%@ page import="com.freemedforms.openreact.types.Drug"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>OpenReact Mobile: Interactions Drug Lookup</title>
<link rel="stylesheet" href="css/jquery.mobile-1.0a3.min.css" />
<script src="js/jquery-1.5.min.js"></script>
<script src="js/jquery.mobile-1.0a3.min.js"></script>
</head>
<body>

<div data-role="page" id="startMenu">

<div data-role="header">
<h1>OpenReact: Drug Lookup</h1>
</div>
<!-- /header -->

<div data-role="content">

<%

// Process existing drugs passed

String rawDrugIds = null;
List<Long> drugIds = new ArrayList<Long>(); 
try {
	rawDrugIds = request.getParameter("drugs");
	String[] drugIdStrings = rawDrugIds.split(",");
	for (String drugIdString : drugIdStrings) {
		if (drugIdString != null && !drugIdString.equals("")) {
			drugIds.add(Long.parseLong(drugIdString));
		}
	}
} catch (Exception ex) {
	// no drugs, ignore
}



// Do the actual lookup
CodeSet codeset = CodeSet.valueOf(request.getParameter("codeset"));
String drugquery = request.getParameter("drugname");
List<Drug> drugs = DrugLookup.findDrug(codeset, drugquery);

String rawDrugs = "";
if (drugIds != null && drugIds.size() > 0) {
	for (Long drugId : drugIds) {
		if (!rawDrugs.equals("")) {
			rawDrugs += ",";
		}
		rawDrugs += drugId.toString();
	}
}

%>
<ul data-role="listview" data-inset="true" data-theme="c" data-dividertheme="b"> 
<li data-role="list-divider">Select Drug</li> 
<%
for (Drug drug : drugs) {
	// Only show if there are ids on the stack.
	String thisDrugList = "";
	if (!rawDrugs.equals("")) {
		thisDrugList = rawDrugs + "," + drug.getDrugId();
	} else {
		thisDrugList = drug.getDrugId() + "";
	}
	out.println("<li><a href=\"interactions.jsp?drugs=" + thisDrugList +
			"\">" + drug.getDrugName() + "</a></li>"); 
}
%>
</ul>
<%

// Lookup drug form

%>

</div>
<!-- /content -->

<div data-role="footer">
<h4>&copy; 2011</h4>
</div>
<!-- /footer --></div>
<!-- /page -->

</body>
</html>