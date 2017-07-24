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
// File: Dlink.java
// Description: This class save all the Links info.
// Author: dfc

package org.dfc.model;

/**
 * It represents a download link with all the properties including artist
 * title, description, priority...
 */
public class Dlink {
    private String description;
    private String download_link;
    private String artist;
    private String title;
    private int priority;

    /**
     * Constructor
     *
     * @param download_link: It contains the link for the download
     * @param description:   It contains the description, usually artist - title
     */
    public Dlink(String download_link, String description) {
        this.download_link = download_link;
        this.description = description;
    }

    public void createArtistandTitle() {
        if (description.contains("-")) {
            artist = description.substring(0, description.indexOf("-"));
            title = description.substring(description.indexOf("-") + 1);
        } else {
            artist = description;
            title = description;
        }
    }

    public String getArtist() {
        return artist;
    }

    public String getDescription() {
        return description;
    }

    public String getDownload_link() {
        return download_link;
    }

    public int getPriority() {
        return priority;
    }

    public String getTitle() {
        return title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDownload_link(String download_link) {
        this.download_link = download_link;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
