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
// File: ProgressRenderer.java
// Description: Contains class ProgressRenderer for the JProgressBar.
// Author: dfc

package egui;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

// Extends JProgressBar and implements TableCellRenderer. This class represents
// the progress bar of the download table which indicates the progress of the
// download.
public class ProgressRenderer extends JProgressBar implements TableCellRenderer {
	private static final long serialVersionUID = -5792948812180557229L;

	// ProgressRenderer constructor. It receives the interval of the
	// progress bar, usually [0,100]
	public ProgressRenderer(int min, int max) {
		super(min, max);
	}

	// Returns the component used for drawing the cell.
	// This method is used to configure the renderer appropriately before
	// drawing.
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setValue((int) ((Float) value).floatValue());
		return this;
	}
}
