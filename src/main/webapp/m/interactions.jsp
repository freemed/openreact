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
<link rel="stylesheet" href="css/jquery.mobile-1.0a4.1.min.css" />
<script src="js/jquery-1.5.2.min.js"></script>
<script src="js/jquery.mobile-1.0a4.1.min.js"></script>
</head>
<body>

<div data-role="page" id="startMenu">

<div data-role="header">
<h1>OpenReact: Drug Lookup</h1>
</div>
<!-- /header -->

<div data-role="content">

<%

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

// Only show if there are ids on the stack.
if (drugIds.size() > 0) {
	out.println("<ul data-role=\"listview\" data-inset=\"true\" data-theme=\"c\" data-dividertheme=\"b\">");
	out.println("<li data-role=\"list-divider\">Remove drug</li>");

	for (Long drugId : drugIds) {
		Drug thisDrug = DrugLookup.getDrugById(drugId);
		List<String> withoutThisList = new ArrayList<String>();
		for (Long iter : drugIds) {
			if (iter != drugId) {
				withoutThisList.add(iter.toString());
			}
		}
		String withoutThis = "";
		for (String item : withoutThisList) {
			if (!withoutThis.equals("")) {
				withoutThis += ",";
			}
			withoutThis += item;
		}
		
		out.println("<li><a href=\"interactions.jsp?drugs=" +
				withoutThis + "\">" + thisDrug.getDrugName() + "</a></li>");
	}
	out.println("</ul>");
}

if (drugIds.size() >= 2) {
	%>
		<ul data-role="listview" data-inset="true" data-theme="c" data-dividertheme="b"> 
			<li><a href="interactionslookup.jsp?drugs=<%= rawDrugIds %>">Search Interactions</a></li> 
		</ul> 	
	<%
}

// Lookup drug form

%>

<form action="interactionsdruglookup.jsp" method="post">
<%
// If there are drugs already, pass them along
if (drugIds.size() > 0) {
	out.println("<input type=\"hidden\" name=\"drugs\" value=\"" + rawDrugIds + "\" />");
}
%>
<fieldset>
<div data-role="fieldcontain"><label for="codeset" class="select">Code
Set:</label> <select name="codeset" id="codeset">
	<option value="EN_US">EN_US</option>
	<option value="EN_CA">EN_CA</option>
	<option value="EN_ZA">EN_ZA</option>
	<option value="FR_FR">FR_FR</option>
</select></div>

<div data-role="fieldcontain"><label for="drugname">Drug
Input:</label> <input type="text" name="drugname" id="drugname" value="" /></div>
<button type="submit" data-theme="a" name="submit" value="Submit">Submit</button>
</fieldset>
</form>

</div>
<!-- /content -->

<div data-role="footer">
<h4>&copy; 2011</h4>
</div>
<!-- /footer --></div>
<!-- /page -->

</body>
</html>
