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
// File: SearchUconnection.java
// Description: Contains the class SearchUconnection that makes a search connection 
//               and search with the user criteria
// Author: dfc

package ewire;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import egui.JearGui;
import emisc.Dlink;

// It contains the methods used for making the search according to
// the user criteria
public class SearchUconnection extends Uconnection {
	private BufferedReader buff_reader;
	private String download_link;
	private HashMap<String, String> urls;

	// Class constructor with
	// @sturl: a single url for the search
	public SearchUconnection(String sturl) {
		super.sturl = sturl;
		urls = new HashMap<String, String>();
		urls.put("emp3", "http://www.emp3world.com/");
		urls.put("zapm", "http://www.zapmusic.me/");
	}

	private void addDlinks(String webpage, String criteria, int page,
			String source) {
		String download_link = "";
		Document doc = null;
		Elements links_download = null;
		Elements my_descriptions = null;
		Object[] arrayLinks = null;
		Object[] arrayDescriptions = null;

		criteria = criteria.toLowerCase();
		doc = Jsoup.parse(webpage);

		if (source == "zapm") {
			links_download = doc.select("a[rel^=nofollow]");
			my_descriptions = doc.select("div.well").select("h4");
			arrayLinks = links_download.toArray();
			arrayDescriptions = my_descriptions.toArray();

			for (Element e : links_download) {
				e.attr("href",
						e.attr("href").toString()
						.replace("http://adf.ly/1279576/", ""));
			}

			for (int i = 0; i < arrayDescriptions.length; i++) {
				arrayDescriptions[i] = arrayDescriptions[i].toString()
						.replace("<h4>", "").replace("</h4>", "")
						.replace(".mp3", "");
			}

		} else if (source == "emp3") {
			// links_download = doc.select("a[href^=/mp3/]");
			links_download = doc.select("a[rel^=nofollow]");
			links_download = links_download.not("a.red");
			my_descriptions = doc.select("span[id^=song_title]");
			arrayLinks = links_download.toArray();
			arrayDescriptions = my_descriptions.toArray();
			
			if (arrayDescriptions.length == arrayLinks.length) {
				for (int i = 0; i < arrayLinks.length; i++) {
					arrayDescriptions[i] = my_descriptions.get(i).text();
				}	
			}			
		}

		if (arrayLinks.length == arrayDescriptions.length) {
			for (int i = 0; i < arrayLinks.length; i++) {
				if (arrayDescriptions[i].toString().toLowerCase()
						.contains(criteria)) {
					download_link = searchDownloadLink(links_download.get(i)
							.attr("href"), source);
					Dlink d = new Dlink(download_link,
							arrayDescriptions[i].toString());
					addDownloadToTheList(d, page);
				}
			}
		}
	}

	private void addDownloadToTheList(Dlink d, int page) {
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

	// It connects to the search URL
	@Override
	public HttpURLConnection doConnection() {
		try {
			url = new URL(sturl);
			huc = (HttpURLConnection) url.openConnection();
			huc.setRequestMethod("POST");
			huc.connect();
		} catch (Exception ex) {
			Logger.getLogger(SearchUconnection.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		return (huc);
	}

	// It connects to the search URL passed by parameter
	// @param sturl: url which we'll connect to
	public HttpURLConnection doConnection(String sturl) {
		System.out.println(sturl);
		try {
			url = new URL(sturl);
			huc = (HttpURLConnection) url.openConnection();
			huc.setRequestMethod("POST");			
			huc.connect();
		} catch (Exception ex) {
			Logger.getLogger(SearchUconnection.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		return (huc);
	}

	// It opens a connection to the httpurlconnection passed by parameter
	// @param u: httpurlconnection where we'll connect
	public BufferedReader openConnection(HttpURLConnection u) {
		InputStream is = null;
		InputStreamReader dis = null;
		BufferedReader bis = null;

		try {
			is = u.getInputStream();
			dis = new InputStreamReader(is);
			bis = new BufferedReader(dis);
		} catch (IOException ex) {
			Logger.getLogger(SearchUconnection.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		return bis;
	}

	// It opens a stream for retrieving the information of the web and obtain
	// the links information
	// @param u: URLconnection
	// @param criteria: search criteria, used for filtering the information
	// @param page: page
	// @param source: source where we obtain the link
	public void searchData(HttpURLConnection u, String criteria, int page,
			String source) {
		download_link = "";
		String webpage = "";

		try {
			buff_reader = openConnection(u);
			while (buff_reader.ready()) {
				webpage = webpage + buff_reader.readLine();
			}
			addDlinks(webpage, criteria, page, source);
		} catch (IOException ex) {
			Logger.getLogger(SearchUconnection.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}

	// It opens a stream for retrieving the information of the web and obtain
	// the downloadlink information
	// @param u: URLconnection
	private String searchDownloadLink(String searchLink, String source) {
		download_link = "";
		try {
			/*
			 * buff_reader = openConnection(u); while(buff_reader.ready() &&
			 * download_link.length() == 0) { String webpage_line =
			 * buff_reader.readLine(); download_link =
			 * getDownloadLink(webpage_line, web);
			 */
			/*if (source == "emp3") {
				String[] my_ArrayAux = searchLink.split("/");
				download_link = "http://www.emp3world.com/to_download.php?id="
						+ my_ArrayAux[2];
			} else {*/
				download_link = searchLink;
			//}
		} catch (Exception ex) {
			Logger.getLogger(SearchUconnection.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		return download_link;
	}
}