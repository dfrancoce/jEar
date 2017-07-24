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
// File: SearchTableModel.java
// Description: Contains search table TableModel class.
// Author: dfc

package org.dfc.ui;

import javax.swing.table.DefaultTableModel;

// Extends DefaultTableModel. It represents the table where
// we'll show the results of the search
public class SearchTableModel extends DefaultTableModel {
	private static final long serialVersionUID = -8258792007375095960L;

	@Override
	public boolean isCellEditable(int row, int colum) {
		return false;
	}
}
