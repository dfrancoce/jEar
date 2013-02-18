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
// File: Slink.java
// Description: This file contains the class Slink which represents a
//              complete search link
// Author: dfc

package emisc;

// It represents a complete search link ready for make a connection and obtain
// results and it gives us pretty good information about its source, max_page... 
public class Slink {
	private String sturl;
	private String param;
	private int page;
	private String source; // source where we obtain the links for downloading
	private int max_page; // variable which represents the quality of the site
	private String slink_url;

	public Slink() {
	} // Empty class constructor

	// Class constructor with
	// @source
	// @max_page
	public Slink(String sturl, String source, int max_page) {
		this.sturl = sturl;
		this.source = source;
		this.max_page = max_page;
	}

	// It generates the complete url that we need to make the search
	public void generateSlinkCompleteUrl() {
		if (source == "zapm") {
			param = param.replace(" ", "-");
			slink_url = sturl + param + "-" + page;
		} else if (source == "emp3") {
			param = param.replace(" ", "%20");
			slink_url = sturl + "page=" + page + "&phrase=" + param;
		} else {
			slink_url = null;
		}
	}

	// @return the max_page
	public int getmax_page() {
		return max_page;
	}

	// @return the page
	public int getPage() {
		return page;
	}

	// @return the param
	public String getParam() {
		return param;
	}

	// @return the slinkurl
	public String getSlinkurl() {
		return slink_url;
	}

	// @return the source
	public String getSource() {
		return source;
	}

	// @return the sturl
	public String getSturl() {
		return sturl;
	}

	// @param max_page the max_page to set
	public void setmax_page(int max_page) {
		this.max_page = max_page;
	}

	// @param page the page to set
	public void setPage(int page) {
		this.page = page;
	}

	// @param param the param to set
	public void setParam(String param) {
		this.param = param;
	}

	// @param source the source to set
	public void setSource(String source) {
		this.source = source;
	}

	// @param sturl the sturl to set
	public void setSturl(String sturl) {
		this.sturl = sturl;
	}
}
