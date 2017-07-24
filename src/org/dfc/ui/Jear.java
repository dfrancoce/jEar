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
// File: Jear.java
// Description: Graphical User Interface of jEar.
// Author: dfc

package org.dfc.ui;

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
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.swing.BorderFactory;
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
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.dfc.model.Slink;
import org.dfc.net.Download;
import org.dfc.net.SearchUconnection;

/**
 * Extends JFrame and implements Observer. This class represents the GUI
 * of the application. It contains all the GUI components and how the application
 * should work when the user commit an action.
 */
public class Jear extends JFrame implements Observer {
    class SearchSlink implements Runnable {
        private Slink s;
        private String param;
        private HttpURLConnection hucSearch = null;
        private Boolean finish = false;

        SearchSlink(Slink s, String param) {
            this.s = s;
            this.param = param;
        }

        @Override
        public void run() {
            int page = 1;

            while (page > 0 && !finish) { // It connects to the URL, looks for links until it finishes
                s.setPage(page);
                s.generateSlinkCompleteUrl();
                SearchUconnection suc = new SearchUconnection(s.getSlinkurl());
                hucSearch = suc.doConnection();

                if (page <= s.getmax_page()) {
                    suc.searchData(hucSearch, param, page, s.getSource()); // Search
                    hucSearch.disconnect();
                    page++;
                } else {
                    page = 0;
                }
            }

            if (finish) {
                while (dtm.getRowCount() > 0) {
                    dtm.removeRow(0); // Remove search results
                }
            }
        }

        void setFinish() {
            this.finish = true;
        }
    }

    /**
     * It represents a user search, it runs a thread each time an user
     * do a search
     */
    class ShowSearch implements ActionListener, Runnable {
        private List<Thread> ssl_threads = new ArrayList<Thread>();
        private List<SearchSlink> ssl = new ArrayList<SearchSlink>();

        @Override
        public void actionPerformed(ActionEvent e) {
            for (SearchSlink s : ssl) {
                s.setFinish();
            }

            if (!txt_search.getText().equals("")) {
                while (dtm.getRowCount() > 0) {
                    dtm.removeRow(0); // Remove search results
                }

                Thread t = new Thread(this);
                t.start();
            }
        }

        /**
         * It runs the user search thread, running a thread that will contain
         * all the search Threads
         */
        @Override
        public void run() {
            String param = "";

            if (!(txt_search.getText().equals(""))) {
                param = txt_search.getText();
            }

            if (!Objects.equals(param, "")) {
                for (Slink s : search_links) {
                    s.setParam(param);
                    SearchSlink sl = new SearchSlink(s, param);
                    ssl.add(sl);
                    ssl_threads.add(new Thread(sl));
                }
            }

            for (Thread t : ssl_threads) {
                if (t.getState() == Thread.State.NEW) {
                    t.start();
                }
            }
        }
    }

    public static void main(String[] args) {
        Jear.jg = new Jear();
    }


    private JFrame jear_gui;
    private Download selected_download;
    private boolean clearing;

    // Sources we'll use for getting the results according to the search criteria
    private ArrayList<Slink> search_links = new ArrayList<Slink>();

    private ImageIcon[] icons = new ImageIcon[13];

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
    private JMenu[] menus = {new JMenu("jEar"), new JMenu("Help")};
    private JMenuBar mb = new JMenuBar();
    private JLabel lbl_search;
    private JTextField txt_search;
    private JButton btn_search;
    private JPanel panel_up;
    private JTabbedPane panel_center;
    private JPanel panel_search;
    private JPanel panel_downloads;
    private JTable downloads_table;
    private JTable search_table;
    private JScrollPane search_scroll_pane;
    private JScrollPane download_scroll_pane;
    private ProgressRenderer renderer;
    private Properties prop = new Properties();

    private static Jear jg;
    private static DownloadTableModel dotm;
    public static SearchTableModel dtm;
    public static String myMusic_folder = "myMusic";

    /**
     * Constructor. It instantiates the components of the GUI and prepare the
     * connections for the searches
     */
    private Jear() {
        loadProperties();

        loadIconImages();

        jear_gui = new JFrame("jEar");
        myMusic_folder = prop.getProperty("music_folder");

        // Obtain search links connections
        //search_links.add(new Slink("http://www.zapmusic.me/mp3/", "zapm", 10));
        search_links.add(new Slink("http://www.emp3world.com/search.php?", "emp3", 100));

        lbl_search = new JLabel("Search: ");
        txt_search = new JTextField(30);

        btn_search = new JButton(icons[4]);

        panel_up = new JPanel();
        panel_center = new JTabbedPane();
        panel_search = new JPanel();
        panel_downloads = new JPanel();

        dtm = new SearchTableModel();
        dotm = new DownloadTableModel();
        downloads_table = new JTable(dotm);
        search_table = new JTable(dtm);
        search_scroll_pane = new JScrollPane(search_table);
        download_scroll_pane = new JScrollPane(downloads_table);

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

        renderer = new ProgressRenderer(0, 100);

        setComponentsProperties();
        putComponents();
        checkListener();
    }

    private void loadIconImages() {
        String path = getClass().getResource("img").getPath();
        icons[0] = new ImageIcon(path + "/ClearAllMini.png");
        icons[1] = new ImageIcon(path + "/ExitMini.png");
        icons[2] = new ImageIcon(path + "/Cancel.png");
        icons[3] = new ImageIcon(path + "/MyMusic.png");
        icons[4] = new ImageIcon(path + "/Search.png");
        icons[5] = new ImageIcon(path + "/AboutMini.png");
        icons[6] = new ImageIcon(path + "/SearchResults.png");
        icons[7] = new ImageIcon(path + "/Downloads.png");
        icons[8] = new ImageIcon(path + "/ClearMini.png");
        icons[9] = new ImageIcon(path + "/CancelMini.png");
        icons[10] = new ImageIcon(path + "/OpenMini.png");
        icons[11] = new ImageIcon(path + "/OpenFolderMini.png");
        icons[12] = new ImageIcon(path + "/jEar.png");

    }

    /**
     * It loads the configuration from the properties file
     */
    private void loadProperties() {
        String fileName = getClass().getResource("config/jear.config").getPath();
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
    }

    /**
     * It obtains the info of the columns and adds a download
     *
     * @param row
     */
    private void actionAdd(int row) {
        String artist = (String) dtm.getValueAt(row, 2);
        String title = (String) dtm.getValueAt(row, 3);
        String url = (String) dtm.getValueAt(row, 1);

        dotm.addDownload(new Download(url, artist, title));
    }

    /**
     * It cancels the selected download
     */
    private void actionCancel() {
        selected_download.cancel();
    }

    /**
     * It clears an error, cancelled or completed download
     */
    private void actionClear() {
        clearing = true;
        dotm.clearDownload(downloads_table.getSelectedRow());
        clearing = false;
        selected_download = null;
        btn_menuOpen.setEnabled(false);
        btn_menuOpenFolder.setEnabled(false);
        btn_menuClear.setEnabled(false);
    }

    /**
     * It clears all error, cancelled or completed downloads
     */
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

    private void actionOpen() {
        int s = selected_download.getStatus(); // get the status
        String song = selected_download.getArtist() + "-" + selected_download.getTitle() + ".mp3";

        if (s == Download.COMPLETE) {
            File d = new File(myMusic_folder);
            String path = d.getAbsolutePath() + "\\" + song.trim();
            File awesomeSong = new File(path);

            try {
                Desktop.getDesktop().open(awesomeSong);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void actionOpenContainingFolder() {
        int s = selected_download.getStatus(); // get the status

        if (s == Download.COMPLETE) {
            openMyMusicFolder();
        }
    }

    private void checkListener() {
        txt_search.addActionListener(new ShowSearch());
        btn_search.addActionListener(new ShowSearch());

        btn_search.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn_search.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn_search.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        btn_menuAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // Open an about window
                // with version and
                // author info
                JFrame aw = new JFrame("About");
                JLabel lblTitle = new JLabel(" jEar version 0.6\n");
                JTextArea txtText = new JTextArea("");
                JPanel p = new JPanel();

                lblTitle.setFont(new Font("Arial", 1, 11));
                lblTitle.setIcon(icons[12]);
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
                        + "\n\n" + "Copyright 2013 Daniel Franco Cecilia";

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
                aw.setIconImage(icons[5].getImage());
                aw.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            }
        });

        search_table.setCursor(new Cursor(Cursor.HAND_CURSOR));
        search_table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
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
                    public void valueChanged(ListSelectionEvent e) {
                        tableSelectionChanged();
                    }
                });

        downloads_table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // menu
                if (e.getButton() == MouseEvent.BUTTON3) {
                    pum.show(e.getComponent(), e.getX(), e.getY());
                    pum.setVisible(true);
                }
            }
        });

        btn_clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // Clear the selected download
                actionClear();
            }
        });

        btn_menuClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // Clear the selected download
                actionClear();
            }
        });

        btn_menuClearAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // Clear all the downloads
                actionClearAll();
            }
        });

        btn_popUpCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // Cancel the selected download
                actionCancel();
            }
        });

        btn_popUpOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // Opens de selected file
                actionOpen();
            }
        });

        btn_popUpOpenFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // Opens the containing folder
                actionOpenContainingFolder();
            }
        });

        btn_menuOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // Opens de selected file
                actionOpen();
            }
        });

        btn_menuOpenFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // Opens the containing folder
                actionOpenContainingFolder();
            }
        });

        btn_menuExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // Opens the containing folder
                actionExit();
            }
        });

        btn_menuMyMusic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // Opens the containing folder
                openMyMusicFolder();
            }
        });
    }

    private void openMyMusicFolder() {
        File d = new File(myMusic_folder);

        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                String cmd = "rundll32 url.dll,FileProtocolHandler " + d.getCanonicalPath();
                Runtime.getRuntime().exec(cmd);
            } else {
                Desktop.getDesktop().edit(d);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Put components in panels
    private void putComponents() {
        panel_up.setLayout(new FlowLayout(FlowLayout.LEFT));

        panel_up.add(lbl_search);
        panel_up.add(txt_search);
        panel_up.add(btn_search);

        pum.add(btn_popUpOpen);
        pum.add(btn_popUpOpenFolder);
        pum.add(btn_clear);
        pum.add(btn_popUpCancel);

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

        panel_search.setLayout(new BorderLayout());
        panel_search.add(search_scroll_pane, BorderLayout.CENTER);

        panel_downloads.setLayout(new BorderLayout());
        panel_downloads.add(download_scroll_pane, BorderLayout.CENTER);

        panel_center.addTab("Search Results", panel_search);
        panel_center.addTab("Downloads", panel_downloads);
        panel_center.setIconAt(0, icons[6]);
        panel_center.setIconAt(1, icons[7]);
        panel_center.setFont(new Font("Arial", Font.PLAIN, 11));

        jear_gui.setJMenuBar(mb);
        jear_gui.setLayout(new BorderLayout());
        jear_gui.add(panel_up, BorderLayout.NORTH);
        jear_gui.add(panel_center, BorderLayout.CENTER);

        jear_gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jear_gui.setSize(800, 600);
        jear_gui.setResizable(true);
        jear_gui.setLocationRelativeTo(null);
        jear_gui.setVisible(true);
    }

    // Set the properties of the GUI components
    private void setComponentsProperties() {
        jear_gui.setIconImage(icons[12].getImage());

        lbl_search.setFont(new Font("Arial", Font.PLAIN, 11));

        txt_search.setFont(new Font("Arial", Font.PLAIN, 11));

        btn_search.setBorder(BorderFactory.createEmptyBorder());
        btn_search.setToolTipText("Search");
        btn_search.setBorderPainted(false);
        btn_search.setContentAreaFilled(false);
        btn_search.setFocusPainted(false);
        btn_search.setOpaque(false);

        dtm.addColumn("Prio");
        dtm.addColumn("URL");
        dtm.addColumn("Artist");
        dtm.addColumn("Title");

        search_table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        search_table.getColumnModel().getColumn(0).setPreferredWidth(50);
        search_table.getColumnModel().getColumn(1).setPreferredWidth(150);
        search_table.getColumnModel().getColumn(2).setPreferredWidth(150);
        search_table.getColumnModel().getColumn(3).setPreferredWidth(450);
        search_table.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 11));

        search_table.setFont(new Font("Arial", Font.PLAIN, 11));
        downloads_table.setFont(new Font("Arial", Font.PLAIN, 11));
        downloads_table.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 11));

        downloads_table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        downloads_table.getColumnModel().getColumn(0).setPreferredWidth(50);
        downloads_table.getColumnModel().getColumn(1).setPreferredWidth(150);
        downloads_table.getColumnModel().getColumn(2).setPreferredWidth(200);
        downloads_table.getColumnModel().getColumn(3).setPreferredWidth(100);
        downloads_table.getColumnModel().getColumn(4).setPreferredWidth(200);
        downloads_table.getColumnModel().getColumn(5).setPreferredWidth(100);

        renderer.setStringPainted(true);
        renderer.setBorderPainted(false);
        renderer.setFont(new Font("Arial", Font.BOLD, 11));
        renderer.setForeground(new Color(220, 200, 140));
        renderer.setBackground(Color.WHITE);
        renderer.setBorder(BorderFactory.createEmptyBorder());
        downloads_table.setDefaultRenderer(JProgressBar.class, renderer);
        downloads_table.setRowHeight((int) renderer.getPreferredSize().getHeight());
        downloads_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }


    /**
     * It deletes the observer of the old selection and it sets
     * the row selected to the selected_download variable and
     * adds the observer
     */
    private void tableSelectionChanged() {
        if (selected_download != null) {
            selected_download.deleteObserver(Jear.this);

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
            selected_download = dotm.getDownload(downloads_table.getSelectedRow());
            selected_download.addObserver(Jear.this);
        }
    }

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