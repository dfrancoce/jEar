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
 * File: Download.java
 * Description: This class open a url connection to the download link
 and download the file.
 * Author: dfc
 * Date: 22/11/2009 */

package ewire;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import egui.JearGui;

public class Download extends Observable implements Runnable {
	private static final int MAX_BUFFER_SIZE = 1024;

	// State names
	public static final String STATUSES[] = { "Downloading", "Complete",
		"Canceled", "Error" };

	// State images
	public static final ImageIcon IMG_STATUSES[] = {
		new ImageIcon("img\\Downloading.png"),
		new ImageIcon("img\\Complete.png"),
		new ImageIcon("img\\Canceled.png"), new ImageIcon("img\\Error.png") };

	// State codes
	public static final int DOWNLOADING = 0;
	public static final int COMPLETE = 1;
	public static final int CANCELLED = 2;
	public static final int ERROR = 3;

	private ImageIcon icon;
	private String sturl;
	private String artist;
	private String title;
	private int status;
	private int downloaded;
	private int size;
	private URL url;
	private HttpURLConnection huc;

	// Streams
	private InputStream is;
	private DataInputStream dis;
	private BufferedInputStream bis;

	// Files variables
	private RandomAccessFile file = null;
	private String myMusic_path = JearGui.myMusic_folder;
	private File directory = new File(myMusic_path);
	private String filename;

	public Download(String sturl, String artist, String title) {
		this.sturl = sturl;
		this.artist = artist;
		this.title = title;
		size = -1;
		downloaded = 0;
		status = DOWNLOADING;
		this.icon = IMG_STATUSES[DOWNLOADING];

		download(); // Start a download
	}

	public void cancel() {
		this.status = CANCELLED;
		this.icon = IMG_STATUSES[CANCELLED];
		stateChanged();
	}

	public void doUnconnect(HttpURLConnection u) {
		u.disconnect();
	}

	private void download() {
		Thread t = new Thread(this);
		t.start();
	}

	private void error() {
		File file_error;
		
		this.status = ERROR;
		this.icon = IMG_STATUSES[ERROR];
		stateChanged();
		
		if (!filename.equals(null)) {
			file_error = new File(directory.getPath() + "\\" + filename);
			file_error.delete();
		}		
	}

	public String getArtist() {
		return artist;
	}

	public RandomAccessFile getFile() {
		return file;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public float getProgress() {
		return ((float) downloaded / size) * 100;
	}

	public int getSize() {
		return size;
	}

	public int getStatus() {
		return status;
	}

	public String getTitle() {
		return title;
	}

	public URL getUrl() {
		return url;
	}

	@Override
	public void run() {
		try {
			// If MyMusic directory doesn't exists, create it
			if (!directory.exists())
				directory.mkdir();

			// Connect to the download link
			url = new URL(sturl);
			huc = (HttpURLConnection) url.openConnection();
			huc.setRequestProperty("Range", "bytes=" + downloaded + "-");
			huc.connect();
			if (huc.getResponseCode() / 100 != 2)
				error();

			int contentLength = huc.getContentLength();
			if (contentLength < 1)
				error();

			// Set size of the download
			if (size == -1) {
				size = contentLength;
				stateChanged();
			}

			is = huc.getInputStream();
			dis = new DataInputStream(is);
			bis = new BufferedInputStream(dis);

			// Filename comprobations
			filename = new String(artist + "-" + title);
			// Check filename length
			/*if (filename.length() > 50)
				filename = filename.substring(1, 50);*/
			filename = filename + ".mp3";

			file = new RandomAccessFile(directory.getPath() + "/" + filename,
					"rw");
			file.seek(downloaded);

			// Download file
			while (status == DOWNLOADING && downloaded != size) {
				byte buffer[];

				// Create buffer
				if (size - downloaded > MAX_BUFFER_SIZE)
					buffer = new byte[MAX_BUFFER_SIZE];
				else
					buffer = new byte[size - downloaded];

				int read = bis.read(buffer);
				if (read == -1)
					break;

				// Write file
				file.write(buffer, 0, read);
				downloaded += read;
				stateChanged();
			}

			if (status == DOWNLOADING) {
				status = COMPLETE;
				this.icon = IMG_STATUSES[COMPLETE];
				stateChanged();
			}

		} catch (Exception ex) {
			Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null,
					ex);
			error();
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException ex) {
					Logger.getLogger(Download.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}

			if (bis != null) {
				try {
					bis.close();
				} catch (IOException ex) {
					Logger.getLogger(Download.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		}
	}

	private void stateChanged() {
		setChanged();
		notifyObservers();
	}
}