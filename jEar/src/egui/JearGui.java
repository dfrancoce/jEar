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
// File: JearGui.java
// Description: Graphical User Interface of jEar.
// Author: dfc

package egui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import emisc.Slink;
import ewire.Download;
import ewire.SearchUconnection;

// Extends JFrame and implements Observer. This class represents the GUI
// of the application. It contains all the GUI components and how the application
// should work when the user commit an action.
public class JearGui extends JFrame implements Observer {
	// Class which runs the different searchs from different sources
	class SearchSlink implements Runnable {
		private Slink s;
		private String param;
		private HttpURLConnection hucSearch = null;
		private Boolean finish = false;

		// Class constructor
		// @param s: Slink
		// @param param: search criteria
		public SearchSlink(Slink s, String param) {
			this.s = s;
			this.param = param;
		}

		@Override
		public void run() {
			int page = 1;
			while (page > 0 && !finish) { // It connects to the URL, looks for
				// links until it finishes
				s.setPage(page);
				s.generateSlinkCompleteUrl();
				suc = new SearchUconnection(s.getSlinkurl());
				hucSearch = suc.doConnection();
				if (page <= s.getmax_page()) {
					suc.searchData(hucSearch, param, page, s.getSource()); // Search
					hucSearch.disconnect();
					page++;
				} else {
					page = 0;
				}
			}
		}

		// It sets finish variable
		public void setFinish(Boolean finish) {
			this.finish = finish;
		}
	}

	// It represents a user search, it runs a thread each time an user
	// do a search
	class ShowSearch implements ActionListener, Runnable {			
		private List<Thread> ssl_threads = new ArrayList<Thread>();
		private List<SearchSlink> ssl = new ArrayList<SearchSlink>();

		// It starts a new Thread with the user search
		@Override
		public void actionPerformed(ActionEvent e) {			
			for (SearchSlink s: ssl) {
				s.setFinish(true);
			}

			if (txt_search.getText().equals(""))
				;
			else {
				// It starts all the threads
				while (dtm.getRowCount() > 0)
					dtm.removeRow(0); // Remove search results				
				Thread t = new Thread(this);
				t.start();
			}
		}

		// It runs the user search thread, running a thread that will contain
		// all the search
		// Threads
		@Override
		public void run() {
			String param = new String();
								

			if (!(txt_search.getText().equals("")))
				param = txt_search.getText();

			if (param != "") { // If we have a search criteria
				for (Slink s : search_links) { // For each source we prepare a
					// search thread				
					s.setParam(param);
					SearchSlink sl = new SearchSlink(s, param);
					ssl.add(sl);
					ssl_threads.add(new Thread(sl));										
				}
			}
			
			// It starts all the threads
			for (Thread t: ssl_threads) {
				if (t.getState() == Thread.State.NEW) {
					t.start();
				}				
			}
		}
	}

	private static final long serialVersionUID = 5731840687418582920L;

	// It gets the JearGui property
	public static JearGui getJg() {
		return jg;
	}

	// Main method. It starts the application seting the gui
	public static void main(String[] args) {
		setJg(new JearGui());
	}

	// It sets the JearGui property
	public static void setJg(JearGui jg) {
		JearGui.jg = jg;
	}

	private SearchUconnection suc; // Used for the search connection and search
	// the data

	private JFrame jear_gui;
	private Download selected_download;
	private boolean clearing;
	// Sources we'll use for getting the results according
	// to the search criteria
	private ArrayList<Slink> search_links = new ArrayList<Slink>();

	// Array of icons used in the application
		
	private static Icon[] icons = { new ImageIcon("img/ClearAllMini.png"),
		new ImageIcon("img/ExitMini.png"),
		new ImageIcon("img/Cancel.png"),
		new ImageIcon("img/MyMusic.png"),
		new ImageIcon("img/Search.png"),
		new ImageIcon("img/AboutMini.png"),
		new ImageIcon("img/SearchResults.png"),
		new ImageIcon("img/Downloads.png"),
		new ImageIcon("img/ClearMini.png"),
		new ImageIcon("img/CancelMini.png"),
		new ImageIcon("img/OpenMini.png"),
		new ImageIcon("img/OpenFolderMini.png") };;
	
	// Menus
	private JMenuItem btn_clear;
	private JMenuItem btn_popUpCancel;
	private JMenuItem btn_popUpOpen;
	private JMenuItem btn_popUpOpenFolder;
	private JMenuItem btn_menuOpen;
	private JMenuItem btn_menuOpenFolder;
	private JMenuItem btn_menuCancel;

	private JMenuItem btn_menuClear;
	private JMenuItem btn_menuClearAll;
	private JMenuItem btn_menuExit;

	private JMenuItem btn_menuAbout;
	private JMenuItem btn_menuMyMusic;
	private JPopupMenu pum;

	private JMenu[] menus = { new JMenu("jEar"), new JMenu("Help") };
	private JMenuBar mb = new JMenuBar();
	
	// JLabel
	private JLabel lbl_search;
	
	// JTextField
	private JTextField txt_search;

	// JButtons
	private JButton btn_search;
	
	// JPanels
	private JPanel panel_up;
	private JTabbedPane panel_center;
	private JPanel panel_search;
	private JPanel panel_downloads;
	
	// JTables
	public static SearchTableModel dtm; // TableModel for searchTable
	private static DownloadTableModel dotm; // TableModel for downloadsTable
	private JTable downloads_table;
	private JTable search_table;
	private JScrollPane search_scroll_pane;
	private JScrollPane download_scroll_pane;

	private static JearGui jg;

	// JProgressBar
	private ProgressRenderer renderer;
	
	private String myMusic_folder = "myMusic";

	// Constructor. It instantiates the components of the GUI and prepare the
	// connections for the searchs
	public JearGui() {
		// JFrame
		jear_gui = new JFrame("jEar");
		myMusic_folder = loadProperties();

		// Obtain search links connections
		search_links.add(new Slink("http://www.zapmusic.me/mp3/", "zapm", 10));
		search_links.add(new Slink("http://www.emp3world.com/search.php?",
				"emp3", 100));		

		// JLabel
		lbl_search = new JLabel("Search: ");
		// JTextField
		txt_search = new JTextField(30);

		// JButtons
		btn_search = new JButton(icons[4]);

		// JPanels
		panel_up = new JPanel();
		panel_center = new JTabbedPane();
		panel_search = new JPanel();
		panel_downloads = new JPanel();

		// JTables & TableModels
		dtm = new SearchTableModel();
		dotm = new DownloadTableModel();
		downloads_table = new JTable(dotm);
		search_table = new JTable(dtm);
		search_scroll_pane = new JScrollPane(search_table);
		download_scroll_pane = new JScrollPane(downloads_table);

		// JMenus
		pum = new JPopupMenu();
		btn_popUpOpen = new JMenuItem("Open File", icons[10]);
		btn_popUpOpen.setFont(new Font("Arial", Font.PLAIN, 11));
		btn_popUpOpen.setEnabled(false);
		btn_popUpOpenFolder = new JMenuItem("Open Containing Folder", icons[11]);
		btn_popUpOpenFolder.setFont(new Font("Arial", Font.PLAIN, 11));
		btn_popUpOpenFolder.setEnabled(false);
		btn_clear = new JMenuItem("Clear", icons[8]);
		btn_clear.setFont(new Font("Arial", Font.PLAIN, 11));
		btn_popUpCancel = new JMenuItem("Cancel", icons[9]);
		btn_popUpCancel.setFont(new Font("Arial", Font.PLAIN, 11));

		btn_menuOpen = new JMenuItem("Open", icons[10]);
		btn_menuOpen.setFont(new Font("Arial", Font.PLAIN, 11));
		btn_menuOpen.setEnabled(false);
		btn_menuOpenFolder = new JMenuItem("Open Containing Folder", icons[11]);
		btn_menuOpenFolder.setFont(new Font("Arial", Font.PLAIN, 11));
		btn_menuOpenFolder.setEnabled(false);
		btn_menuCancel = new JMenuItem("Cancel", icons[9]);
		btn_menuCancel.setFont(new Font("Arial", Font.PLAIN, 11));
		btn_menuCancel.setEnabled(false);
		btn_menuClear = new JMenuItem("Clear", icons[8]);
		btn_menuClear.setFont(new Font("Arial", Font.PLAIN, 11));
		btn_menuClear.setEnabled(false);
		btn_menuClearAll = new JMenuItem("Clear All", icons[0]);
		btn_menuClearAll.setFont(new Font("Arial", Font.PLAIN, 11));
		btn_menuClearAll.setEnabled(true);
		btn_menuExit = new JMenuItem("Exit", icons[1]);
		btn_menuExit.setFont(new Font("Arial", Font.PLAIN, 11));
		btn_menuAbout = new JMenuItem("About", icons[5]);
		btn_menuAbout.setFont(new Font("Arial", Font.PLAIN, 11));
		btn_menuMyMusic = new JMenuItem("My Music", icons[3]);
		btn_menuMyMusic.setFont(new Font("Arial", Font.PLAIN, 11));

		menus[0].setFont(new Font("Arial", Font.PLAIN, 11));
		menus[1].setFont(new Font("Arial", Font.PLAIN, 11));
		

		// ProgressBar
		renderer = new ProgressRenderer(0, 100);
		
		setComponentsProperties();
		putComponents();
		checkListener();
	}
	
	// It loads the configuration from the properties file
	public static String loadProperties() {		
		Properties prop = new Properties();
	    String fileName = System.getProperty("user.dir") + "/jear.config";
	    InputStream is = null;
	    
		try {
			is = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			prop.load(is);			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return prop.getProperty("music_folder");		
	}

	// It obtains the info of the columns and adds a download
	private void actionAdd(int row) {
		String artist = (String) dtm.getValueAt(row, 2);
		String title = (String) dtm.getValueAt(row, 3);
		String url = (String) dtm.getValueAt(row, 1);

		dotm.addDownload(new Download(url, artist, title));
	}

	// It cancels the selected download
	private void actionCancel() {
		selected_download.cancel();
	}

	// It clears an error, cancelled or completed download
	private void actionClear() {
		clearing = true;
		dotm.clearDownload(downloads_table.getSelectedRow());
		clearing = false;
		selected_download = null;
		btn_menuOpen.setEnabled(false);
		btn_menuOpenFolder.setEnabled(false);
		btn_menuClear.setEnabled(false);
	}

	// It clears all error, cancelled or completed downloads
	private void actionClearAll() {
		clearing = true;
		dotm.clearAllDownloads();
		btn_menuOpen.setEnabled(false);
		btn_menuOpenFolder.setEnabled(false);
		btn_menuClear.setEnabled(false);
		clearing = false;
	}

	private void actionExit() {
		dispose();
		System.exit(0);
	}

	// It opens a file.
	private void actionOpen() {
		int s = selected_download.getStatus(); // get the status
		String song = selected_download.getArtist() + "-"
				+ selected_download.getTitle() + ".mp3";

		if (s == Download.COMPLETE) {
			File d = new File(myMusic_folder);
			String path = d.getAbsolutePath() + "\\" + song.trim();			
			File awesomeSong = new File(path);
			try {
				Desktop.getDesktop().open(awesomeSong);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// It opens a containing folder
	private void actionOpenContainingFolder() {
		int s = selected_download.getStatus(); // get the status

		if (s == Download.COMPLETE) {
			File d = new File(myMusic_folder);
			try {
				Desktop.getDesktop().open(d);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// It Adds all the component listeners
	private void checkListener() {
		txt_search.addActionListener(new ShowSearch()); // Search action "enter"
		btn_search.addActionListener(new ShowSearch()); // Search action
		// "click the search icon"

		btn_search.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) { // It changes the cursor to
				// HAND_CURSOR
				btn_search.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) { // It changes the cursor to
				// DEFAULT_CURSOR
				btn_search.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});

		btn_menuAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // Open an about window
				// with version and
				// author info
				JFrame aw = new JFrame("About");
				JLabel lblTitle = new JLabel(" jEar version 0.03\n");
				JTextArea txtText = new JTextArea("");
				JPanel p = new JPanel();

				lblTitle.setFont(new Font("Arial", 1, 11));
				lblTitle.setIcon(new ImageIcon("img/jEar.png"));
				lblTitle.setAlignmentX(TOP_ALIGNMENT);

				String text = "jEar is free software: you can redistribute it and/or modify\n"
						+ "it under the terms of the GNU General Public License as published by\n"
						+ "the Free Software Foundation, either version 3 of the License, or\n"
						+ "(at your option) any later version. "
						+ "\n\n"
						+ "jEar is distributed in the hope that it will be useful,\n"
						+ "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
						+ "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the\n"
						+ "GNU General Public License for more details.\n"
						+ "\n\n"
						+ "You should have received a copy of the GNU General Public License\n"
						+ "along with jEar.  If not, see <http://www.gnu.org/licenses/>.\n"
						+ "\n\n" + "Copyright 2012 Daniel Franco Cecilia";

				txtText.setText(text);
				txtText.setFont(new Font("Arial", Font.PLAIN, 11));
				txtText.setOpaque(false);
				txtText.setAlignmentX(CENTER_ALIGNMENT);

				p.setLayout(new BorderLayout());
				p.add(lblTitle, BorderLayout.PAGE_START);
				p.add(txtText, BorderLayout.CENTER);
				aw.add(p);

				// aw.setSize(480, 320);
				aw.setResizable(false);
				aw.pack();
				aw.setLocationRelativeTo(null);
				aw.setVisible(true);
				aw.setIconImage(new ImageIcon("img/AboutMini.png").getImage());
				aw.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			}
		});

		search_table.setCursor(new Cursor(Cursor.HAND_CURSOR));
		search_table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) { // It adds a download
				// doubleclicking a row
				// of the search table
				if (e.getClickCount() == 2) { // if 2 clicks
					int row = search_table.rowAtPoint(e.getPoint());
					actionAdd(row);
				}
			}
		});

		downloads_table.setCursor(new Cursor(Cursor.HAND_CURSOR));
		downloads_table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) { // It calls
						// tableSelectionChanged
						// in
						// case
						// of a
						// value
						// changed
						tableSelectionChanged();
					}
				});

		downloads_table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) { // It shows right click
				// menu
				if (e.getButton() == MouseEvent.BUTTON3) {
					pum.show(e.getComponent(), e.getX(), e.getY());
					pum.setVisible(true);
				}
			}
		});

		btn_clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // Clear the selected
				// download
				actionClear();
			}
		});

		btn_menuClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // Clear the selected
				// download
				actionClear();
			}
		});

		btn_menuClearAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // Clear all the
				// downloads
				actionClearAll();
			}
		});

		btn_popUpCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // Cancel the selected
				// download
				actionCancel();
			}
		});

		btn_popUpOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // Opens de selected
				// file
				actionOpen();
			}
		});

		btn_popUpOpenFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // Opens the containing
				// folder
				actionOpenContainingFolder();
			}
		});

		btn_menuOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // Opens de selected
				// file
				actionOpen();
			}
		});

		btn_menuOpenFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // Opens the containing
				// folder
				actionOpenContainingFolder();
			}
		});

		btn_menuExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // Opens the containing
				// folder
				actionExit();
			}
		});

		btn_menuMyMusic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // Opens the containing
				// folder
				openMyMusicFolder();
			}
		});
	}

	private void openMyMusicFolder() {
		File d = new File(myMusic_folder);
		try {
			Desktop.getDesktop().open(d);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Put components in panels
	private void putComponents() {
		panel_up.setLayout(new FlowLayout(FlowLayout.LEFT));

		// Add buttons to the panelUp
		panel_up.add(lbl_search);
		panel_up.add(txt_search);
		panel_up.add(btn_search);

		// Add btnClear to the popUp menu
		pum.add(btn_popUpOpen);
		pum.add(btn_popUpOpenFolder);
		pum.add(btn_clear);
		pum.add(btn_popUpCancel);

		// Add menus to the MenuBar
		menus[0].add(btn_menuOpen);
		menus[0].add(btn_menuOpenFolder);
		menus[0].add(btn_menuClear);
		menus[0].add(btn_menuClearAll);
		menus[0].add(btn_menuCancel);
		menus[0].add(btn_menuMyMusic);
		menus[0].add(btn_menuExit);
		menus[1].add(btn_menuAbout);
		mb.add(menus[0]);
		mb.add(menus[1]);

		// Add search table to the search panel
		panel_search.setLayout(new BorderLayout());
		panel_search.add(search_scroll_pane, BorderLayout.CENTER);

		// Add download table to the downloads panel
		panel_downloads.setLayout(new BorderLayout());
		panel_downloads.add(download_scroll_pane, BorderLayout.CENTER);

		// Add search panel and download panel to the panelCeneter
		panel_center.addTab("Search Results", panel_search);
		panel_center.addTab("Downloads", panel_downloads);
		panel_center.setIconAt(0, icons[6]);
		panel_center.setIconAt(1, icons[7]);
		panel_center.setFont(new Font("Arial", Font.PLAIN, 11));

		// Add panels to the JFrame
		jear_gui.setJMenuBar(mb);
		jear_gui.setLayout(new BorderLayout());
		jear_gui.add(panel_up, BorderLayout.NORTH);
		jear_gui.add(panel_center, BorderLayout.CENTER);

		try {
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
		} catch (Exception ex) {
			Logger.getLogger(JearGui.class.getName()).log(Level.SEVERE, null,
					ex);
		}

		// JFrame properties
		jear_gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jear_gui.setSize(800, 600);
		jear_gui.setResizable(true);
		jear_gui.setLocationRelativeTo(null);
		jear_gui.setVisible(true);
	}

	// Set the properties of the GUI components
	private void setComponentsProperties() {
		jear_gui.setIconImage(new ImageIcon("img/jEar.png").getImage());

		// Label properties
		lbl_search.setFont(new Font("Arial", Font.PLAIN, 11));

		// Textfield properties
		txt_search.setFont(new Font("Arial", Font.PLAIN, 11));

		// JButton properties
		btn_search.setBorder(BorderFactory.createEmptyBorder());
		btn_search.setToolTipText("Search");
		btn_search.setBorderPainted(false);
		btn_search.setContentAreaFilled(false);
		btn_search.setFocusPainted(false);
		btn_search.setOpaque(false);

		// Add columns for searchTable
		dtm.addColumn("Prio");
		dtm.addColumn("URL");
		dtm.addColumn("Artist");
		dtm.addColumn("Title");

		// Set size for searchTable
		search_table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		search_table.getColumnModel().getColumn(0).setPreferredWidth(50);
		search_table.getColumnModel().getColumn(1).setPreferredWidth(150);
		search_table.getColumnModel().getColumn(2).setPreferredWidth(150);
		search_table.getColumnModel().getColumn(3).setPreferredWidth(450);
		search_table.getTableHeader()
		.setFont(new Font("Arial", Font.PLAIN, 11));

		// Set properties for searchTable and downloadsTable
		search_table.setFont(new Font("Arial", Font.PLAIN, 11));
		downloads_table.setFont(new Font("Arial", Font.PLAIN, 11));
		downloads_table.getTableHeader().setFont(
				new Font("Arial", Font.PLAIN, 11));

		// Set size for downloads_table
		downloads_table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		downloads_table.getColumnModel().getColumn(0).setPreferredWidth(50);
		downloads_table.getColumnModel().getColumn(1).setPreferredWidth(150);
		downloads_table.getColumnModel().getColumn(2).setPreferredWidth(200);
		downloads_table.getColumnModel().getColumn(3).setPreferredWidth(100);
		downloads_table.getColumnModel().getColumn(4).setPreferredWidth(200);
		downloads_table.getColumnModel().getColumn(5).setPreferredWidth(100);

		// Set properties for the progress bar
		renderer.setStringPainted(true);
		renderer.setBorderPainted(false);
		renderer.setFont(new Font("Arial", Font.BOLD, 11));
		renderer.setForeground(new Color(220, 200, 140));
		renderer.setBackground(Color.WHITE);
		renderer.setBorder(BorderFactory.createEmptyBorder());
		downloads_table.setDefaultRenderer(JProgressBar.class, renderer);
		downloads_table.setRowHeight((int) renderer.getPreferredSize()
				.getHeight());
		downloads_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	// It deletes the observer of the old selection and it sets
	// the row selected to the selected_download variable and
	// adds the observer
	private void tableSelectionChanged() {
		if (selected_download != null) {
			selected_download.deleteObserver(JearGui.this);
			if (selected_download.getStatus() == Download.COMPLETE) {
				btn_popUpOpen.setEnabled(true);
				btn_popUpOpenFolder.setEnabled(true);
				btn_menuOpen.setEnabled(true);
				btn_menuOpenFolder.setEnabled(true);
				btn_menuClear.setEnabled(true);
				btn_popUpCancel.setEnabled(false);
				btn_menuCancel.setEnabled(false);
			} else {
				btn_popUpOpen.setEnabled(false);
				btn_popUpOpenFolder.setEnabled(false);
				btn_menuOpen.setEnabled(false);
				btn_menuOpenFolder.setEnabled(false);
				if (selected_download.getStatus() != Download.DOWNLOADING) {
					btn_menuClear.setEnabled(true);
				} else {
					btn_menuClear.setEnabled(true);
				}
				btn_popUpCancel.setEnabled(true);
				btn_menuCancel.setEnabled(true);
			}
		}

		if (!clearing && downloads_table.getSelectedRow() > -1) {
			selected_download = dotm.getDownload(downloads_table
					.getSelectedRow());
			selected_download.addObserver(JearGui.this);
		}
	}

	// It makes updates in the gui when something happens
	@Override
	public void update(Observable o, Object arg) {
		if (selected_download.getStatus() == Download.COMPLETE) {
			btn_popUpOpen.setEnabled(true);
			btn_popUpOpenFolder.setEnabled(true);
			btn_menuOpen.setEnabled(true);
			btn_menuOpenFolder.setEnabled(true);
			btn_popUpCancel.setEnabled(false);
			btn_menuCancel.setEnabled(false);
		} else {
			btn_popUpOpen.setEnabled(false);
			btn_popUpOpenFolder.setEnabled(false);
			btn_menuOpen.setEnabled(false);
			btn_menuOpenFolder.setEnabled(false);
			btn_popUpCancel.setEnabled(true);
			btn_menuCancel.setEnabled(true);
		}

		if (selected_download.getStatus() != Download.DOWNLOADING) {
			btn_menuClear.setEnabled(true);
		} else {
			btn_menuClear.setEnabled(false);
		}
	}
}