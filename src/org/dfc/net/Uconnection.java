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
// File: Uconnection.java
// Description: Uconnection structure
// Author: dfc

package org.dfc.net;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Abstract class that defines the connections basics
 */
public abstract class Uconnection {
    URL url;
    String sturl;
    HttpURLConnection huc;

    Uconnection() {
    }

    public abstract HttpURLConnection doConnection();
}