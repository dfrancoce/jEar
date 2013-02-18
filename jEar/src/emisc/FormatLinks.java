/*  This file is part of jEar.

    jEar is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    jEar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with jEar.  If not, see <http://www.gnu.org/licenses/>.

 */

/*
 * File: FormatLinks.java
 * Description: This class format all the links used in the application
 * Author: dfc
 * Date: 22/11/2009 */

package emisc;

import egui.JearGui;

public class FormatLinks {

	public String doDlinkFormat(String r) {
		// Format the download link
		String formatedString = new String("");
		int j = 0;
		int k = 1;

		for (int i = 0; i < r.length() & k == 1; i++) {
			if (r.charAt(i) == 'h') {
				for (j = i + 3; r.charAt(j) != '"'; j++) {
				}
				formatedString = r.substring(i + 3, j);
				k++;
			}
		}
		return formatedString;
	}

	public void doSearchFormat(String ufs, int page) {
		/*
		 * This method format the String received from the searchUconnection.
		 * Obtain all the necessary links for create a Dlink and add the search
		 * element to the searchList
		 */

		String des = new String(); // Description
		String link = new String();

		if (ufs.contains("href") & ufs.contains("rel=\"nofollow\"")) {
			link = ufs.substring(ufs.indexOf("href") + 6,
					ufs.indexOf("title") - 2);
			des = ufs.substring(ufs.indexOf(">") + 1, ufs.indexOf("</a>"));

			Dlink d = new Dlink(link, des);
			d.createArtistandTitle();
			d.setPriority(page);

			Object[] dl = new Object[4];
			dl[0] = d.getPriority();
			dl[1] = d.getDownload_link();
			dl[2] = d.getArtist();
			dl[3] = d.getTitle();

			// Add element to the searchTable
			JearGui.dtm.addRow(dl);
		}
	}

	public String doSearchLinkFormat(String r) {
		// Format the search link
		String formatedString = new String("");
		int j;

		for (int i = 0; i < r.length(); i++)
			if (r.substring(i, i + 4).equals("src=")) {
				for (j = i + 1; r.charAt(j) != '?'; j++) {
				}
				formatedString = r.substring(i + 5, j + 3);
				break;
			}
		return formatedString;
	}

	public int isSearchFinished(String ufs) {
		int res = 0;

		buc: for (int i = ufs.length() - 20; i + 4 < ufs.length(); i++) {
			if (ufs.substring(i, i + 4).equals("Back")) {
				res = 0;
				break buc;
			}

			else if (ufs.substring(i, i + 4).equals("Next")) {
				res = 1;
				break buc;
			}
		}
		return res;
	}
}
