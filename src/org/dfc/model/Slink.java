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

package org.dfc.model;

import java.util.Objects;

/**
 * It represents a complete search link ready for make a connection and obtain
 * results and it gives us pretty good information about its source, max_page...
 */
public class Slink {
    private String sturl;
    private String param;
    private int page;
    private String source;
    private int max_page;
    private String slink_url;

    public Slink(String sturl, String source, int max_page) {
        this.sturl = sturl;
        this.source = source;
        this.max_page = max_page;
    }

    public void generateSlinkCompleteUrl() {
        if (Objects.equals(source, "zapm")) {
            param = param.replace(" ", "-");
            slink_url = sturl + param + "-" + page;
        } else if (Objects.equals(source, "emp3")) {
            param = param.replace(" ", "%20");
            slink_url = sturl + "page=" + page + "&phrase=" + param;
        } else {
            slink_url = null;
        }
    }

    public int getmax_page() {
        return max_page;
    }

    public int getPage() {
        return page;
    }

    public String getParam() {
        return param;
    }

    public String getSlinkurl() {
        return slink_url;
    }

    public String getSource() {
        return source;
    }

    public String getSturl() {
        return sturl;
    }

    public void setmax_page(int max_page) {
        this.max_page = max_page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setSturl(String sturl) {
        this.sturl = sturl;
    }
}
