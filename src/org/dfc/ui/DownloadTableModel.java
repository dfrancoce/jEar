
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
// File: DownloadTableModel.java
// Description: Contains downloads table TableModel class.
// Author: dfc


package org.dfc.ui;

import org.dfc.net.Download;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;

/**
 * Extends AbstractTableModel and implements Observer. This class represents
 * the table of downloads and it contains the methods for working with it like
 * addDownload, getDownload, clearDownload...
 */
public class DownloadTableModel extends AbstractTableModel implements Observer {
    private ArrayList<Download> downloadList = new ArrayList<Download>();
    private final String[] columns = {"", "Artist", "Title", "Size", "Progress", "Status"};
    private final Class[] columnClasses = {ImageIcon.class, String.class, String.class, String.class, JProgressBar.class, String.class};

    void addDownload(Download download) {
        download.addObserver(this);
        downloadList.add(download);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    void clearAllDownloads() {
        int count = downloadList.size();
        for (int i = 0; i < count; i++) {
            if (downloadList.get(i).getStatus() != Download.DOWNLOADING) {
                downloadList.remove(i);
                fireTableRowsDeleted(i, i);
                i = i - 1;
                count = count - 1;
            }
        }
    }

    void clearDownload(int row) {
        Download my_row = downloadList.get(row);
        if (my_row.getStatus() != Download.DOWNLOADING) {
            downloadList.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return columnClasses[col];
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int i) {
        return columns[i];
    }

    Download getDownload(int row) {
        return downloadList.get(row);
    }

    @Override
    public int getRowCount() {
        return downloadList.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        Download d = downloadList.get(row);

        switch (col) {
            case 0:
                return d.getIcon();
            case 1:
                return d.getArtist();
            case 2:
                return d.getTitle();
            case 3:
                return (d.getSize() == -1) ? "" : Integer.toString(d.getSize());
            case 4:
                return d.getProgress();
            case 5:
                return Download.STATUSES[d.getStatus()];
        }

        return "";
    }

    @Override
    public void update(Observable o, Object arg) {
        int index = downloadList.indexOf(o);
        fireTableRowsUpdated(index, index);
    }
}