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

package com.freemedforms.openreact.web;

import javax.servlet.http.HttpServletRequest;

public class MobileDetection {

	public static final String[] mobileBrowserLookup = new String[] { "iPhone",
			"iPod" /* Apple iPod touch */
			, "Android" /* 1.5+ Android */
			, "dream" /* Pre 1.5 Android */
			, "CUPCAKE" /* 1.5+ Android */
			, "blackberry9500" /* Storm */
			, "blackberry9530" /* Storm */
			, "blackberry9520" /* Storm v2 */
			, "blackberry9550" /* Storm v2 */
			, "blackberry 9800" /* Torch */
			, "webOS" /* Palm Pre Experimental */
			, "incognito" /* Other iPhone browser */
			, "webmate" /* Other iPhone browser */
			, "s8000" /* Samsung Dolphin browser */
			, "bada" /* Samsung Dolphin browser */
	};

	/**
	 * Determine if the request was made from a mobile browser (phone) or not.
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isMobileBrowser(HttpServletRequest request) {
		String ua = request.getHeader("User-Agent");
		for (String lookup : mobileBrowserLookup) {
			if (ua.contains(lookup))
				return true;
		}
		return false;
	}

}
