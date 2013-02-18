// This file is part of jEar.

// jEar is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// jEar is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with jEar.  If not, see <http://www.gnu.org/licenses/>.

// Copyright 2011 Daniel Franco Cecilia
// File: DownloadLinkUconnection.java
// Description: This file contains a class used for getting the download link.
// Author: dfc

package ewire;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import emisc.FormatLinks;

// Class used for getting the download
public class DownloadLinkUconnection extends Uconnection {
	private InputStream is;
	private InputStreamReader dis;
	private BufferedReader bis;

	// Class constructor
	// @param sturl: url
	public DownloadLinkUconnection(String sturl) {
		super.sturl = sturl;
	}

	// It connects to the URL
	@Override
	public HttpURLConnection doConnection() {
		try {
			url = new URL(sturl);
			huc = (HttpURLConnection) url.openConnection();
			huc.setRequestMethod("GET");
			huc.connect();
		} catch (Exception ex) {
			Logger.getLogger(DownloadLinkUconnection.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		return (huc);
	}

	public void doUnconnect(HttpURLConnection u) {
		u.disconnect();
	}

	// This method returns the formated download link
	public String getDownloadLink(HttpURLConnection u, FormatLinks f) {
		String formated_string = new String("");
		try {
			is = u.getInputStream();
			dis = new InputStreamReader(is);
			bis = new BufferedReader(dis);
			while (bis.ready()) {
				String r = bis.readLine();
				formated_string = f.doDlinkFormat(r);
				if (!formated_string.equals("") & !formated_string.equals(null)) {
					break;
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(DownloadLinkUconnection.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		return formated_string;
	}
}
