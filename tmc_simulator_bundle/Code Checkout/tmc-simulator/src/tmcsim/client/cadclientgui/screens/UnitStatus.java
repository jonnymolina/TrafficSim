package tmcsim.client.cadclientgui.screens;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import tmcsim.client.cadclientgui.enums.CADDataEnums;
import tmcsim.client.cadclientgui.enums.CADScriptTags.UNIT_TAGS;
import tmcsim.client.cadclientgui.enums.TableHeaders;
import tmcsim.client.cadclientgui.enums.UnitStatusEnums;

/**
 * This class contains the view and controller for the UnitStatus screen. The view was not built using a GUI builder plug-in
 * (but may want to consider doing so in the future), and the controller uses listeners to control how the view and data act. 
 * 
 * @author Vincent
 */

public class UnitStatus extends JFrame {

    private final int ONE_SECOND = 1000;
    
    private final String SCREEN_TITLE = "(Shift + F4)  Unit Status";

    private final Dimension SCREEN_DIMENSIONS = new Dimension(1400, 250);

    private final Dimension DROP_DOWN_MENU_LABEL_DIMENSIONS = new Dimension(
            170, 20);

    private final Dimension DROP_DOWN_MENU_DIMENSIONS = new Dimension(170, 300);

    private final Dimension BUTTON_DIMENSIONS = new Dimension(100, 25);

    private final int COLUMN_WIDTH = 120;

    private final String[] LABELS = { "10-8", "OFC", "OOS",
            "Open Unit Details", "Open Unit Activity Log", "Map",
            "Change Vehicle", "Unit Poll", "Quick Note...",
            "Activate User Timer...", "Filters", "Page...", "Roster System",
            "10-10" };

    private final String[] WITH_ASSIGNED_INC_LABELS = { "STAGE", "10-97",
            "10-8", "ASSIGNED ALT...", "ENRT ALT...", "10-97 ALT...",
            "Open Incident", "Recall Incident", "Open Unit Details",
            "Open Unit Activity Log", "Map", "Recall Linked Incidents",
            "Cancel", "Reassign", "Duplicate", "Unit Poll", "Quick Note...",
            "Activate User Timer...", "Filters", "Page...", "Fax..." };

    private final String LABEL_SPACING = "     ";

    // this box holds both the table and the buttons below
    private Box unitStatusFrame;

    private JTable unitStatusTable;
    private JFrame unitStatusMenu;
    private JFrame unitStatusWithAssignedIncMenu;

    // labels for the drop down menu
    private JLabel[] dropDownLabels = new JLabel[LABELS.length];
    private JLabel[] dropDownWithAssignedIncLabels = new JLabel[WITH_ASSIGNED_INC_LABELS.length];

    private JButton buttonEnrt;
    private JButton buttonStage;
    private JButton button1097;
    private JButton buttonCode4;
    private JButton buttonDash1;
    private JButton buttonDash2;
    private JButton buttonDash3;
    private JButton button108;
    private JButton buttonOFC;
    private JButton buttonOOS;

    public UnitStatus() {
        initComponents();
    }

    private void initComponents() {
        initializeTable();
        initControllers();
        initializeDropDownMenus();

        JScrollPane scrollpane = new JScrollPane(unitStatusTable);
        scrollpane.getViewport().setBackground(Color.WHITE);

        Box bottomButtons = new Box(BoxLayout.X_AXIS);
        initializeBottomButtons(bottomButtons);

        unitStatusFrame = new Box(BoxLayout.Y_AXIS);
        unitStatusFrame.add(bottomButtons);

        unitStatusFrame.add(scrollpane);// holds the table
        unitStatusFrame.add(bottomButtons);

        setTitle(SCREEN_TITLE);
        setPreferredSize(SCREEN_DIMENSIONS);
        getContentPane().add(unitStatusFrame);
        setResizable(true);
        pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(0, (int) (dim.getHeight() * 2 / 4));
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setVisible(false);
    }

    /**
     * Initializes the table and prepares the cell renderer for color
     * management. It initializes the default settings and handles the drag and
     * drop feature.
     */
    private void initializeTable() {
        unitStatusTable = new JTable() {
            /**
             * Custom renderer to set different background/foreground colors
             * 
             * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer,
             *      int, int)
             */
            public Component prepareRenderer(TableCellRenderer renderer,
                    int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);

                int unitNumColumn = 2;
                try {
                    switch (ScreenManager.theCoordinator
                            .getCadDataUnitStatus((String) unitStatusTable
                                    .getValueAt(row, unitNumColumn))) {
                    case Assignable:
                        comp.setForeground(Color.GREEN);
                        comp.setBackground(Color.BLACK);
                        break;
                    case Arrived:
                        comp.setForeground(Color.YELLOW);
                        comp.setBackground(Color.BLACK);
                        break;
                    case NotAssignable:
                        comp.setForeground(Color.BLACK);
                        comp.setBackground(Color.GRAY);
                        break;
                    case Enroute:
                        comp.setForeground(Color.CYAN);
                        comp.setBackground(Color.BLACK);
                        break;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                if (getSelectedRow() == row) {
                    comp.setForeground(Color.WHITE);
                    comp.setBackground(Color.BLUE);
                }

                return comp;
            }
        };

        unitStatusTable.setOpaque(true);
        unitStatusTable.setIntercellSpacing(new Dimension(1, 0));
        unitStatusTable.setGridColor(Color.WHITE);
        unitStatusTable.getTableHeader().setReorderingAllowed(false);
        unitStatusTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        unitStatusTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        unitStatusTable.setAutoCreateRowSorter(true);
        unitStatusTable.setDragEnabled(true);

        unitStatusTable.setModel(new DefaultTableModel());
        ((DefaultTableModel) unitStatusTable.getModel())
                .setColumnIdentifiers(TableHeaders.UNIT_STATUS_HEADERS);

        unitStatusTable.setTransferHandler(new TransferHandler() {

            public boolean canImport(TransferHandler.TransferSupport info) {
                if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    return false;
                }
                return true;
            }

            public int getSourceActions(JComponent c) {
                return TransferHandler.COPY_OR_MOVE;
            }

            protected Transferable createTransferable(JComponent c) {
                return new StringSelection((String) unitStatusTable.getValueAt(
                        unitStatusTable.getSelectedRow(), 2));
            }

        });

        for (int i = 0; i < unitStatusTable.getColumnCount(); i++) {
            unitStatusTable.getColumnModel().getColumn(i)
                    .setPreferredWidth(COLUMN_WIDTH);
        }
    }

    /*
     * Adds the key and mouse listeners for the table and component listener for
     * screen.
     */
    private void initControllers() {
        unitStatusTable.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                int unitColumn = 2;
                String unitNum = (String) unitStatusTable.getValueAt(
                        unitStatusTable.getSelectedRow(), unitColumn);
                if (SwingUtilities.isRightMouseButton(e)) {
                    try {
                        if (ScreenManager.theCoordinator
                                .getCadDataUnitStatus(unitNum) == UnitStatusEnums.Enroute
                                || ScreenManager.theCoordinator
                                        .getCadDataUnitStatus(unitNum) == UnitStatusEnums.Arrived) {
                            openDropDownWithAssignedIncMenu(e);
                        } else {
                            openDropDownMenu(e);
                        }
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    closeDropDownMenu();
                    closeDropDownWithAssignedIncMenu();
                }
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }
        });

        addComponentListener(new ComponentListener() {
            public void componentHidden(ComponentEvent e) {
            }

            public void componentMoved(ComponentEvent e) {
                closeDropDownMenu();
            }

            public void componentResized(ComponentEvent e) {
            }

            public void componentShown(ComponentEvent e) {
            }
        });
    }

    /*
     * Creates the drop down menu that appears when a right click is performed
     * on the table.
     */
    private void initializeDropDownMenus() {
        Box menu = new Box(BoxLayout.Y_AXIS);
        initializeDropDownLabels();
        addLabelsToBox(menu);

        setMenuHighlightedBackground(Color.BLUE);

        unitStatusMenu = new JFrame();
        unitStatusMenu.getContentPane().add(menu);
        unitStatusMenu.setPreferredSize(DROP_DOWN_MENU_DIMENSIONS);
        unitStatusMenu.setUndecorated(true);
        unitStatusMenu.pack();
        unitStatusMenu.setVisible(false);

        menu = new Box(BoxLayout.Y_AXIS);
        initializeDropDownWithAssignedIncLabels();
        addWithAssignedIncLabelsToBox(menu);

        setWithAssignedIncMenuHighlightedBackground(Color.BLUE);

        unitStatusWithAssignedIncMenu = new JFrame();
        unitStatusWithAssignedIncMenu.getContentPane().add(menu);
        unitStatusWithAssignedIncMenu
                .setPreferredSize(DROP_DOWN_MENU_DIMENSIONS);
        unitStatusWithAssignedIncMenu.setUndecorated(true);
        unitStatusWithAssignedIncMenu.pack();
        unitStatusWithAssignedIncMenu.setVisible(false);
    }

    /*
     * Sets the text and size and adds a listener to each label. Currently,
     * labels are not implemented so no listeners are added.
     */
    private void initializeDropDownLabels() {
        for (int i = 0; i < dropDownLabels.length; i++) {
            dropDownLabels[i] = new JLabel(LABEL_SPACING + LABELS[i]);
            dropDownLabels[i].setMaximumSize(DROP_DOWN_MENU_LABEL_DIMENSIONS);
            dropDownLabels[i].setForeground(Color.GRAY);
        }
    }

    /*
     * Sets the text and size and adds a listener to each activated label.
     */
    private void initializeDropDownWithAssignedIncLabels() {
        for (int i = 0; i < dropDownWithAssignedIncLabels.length; i++) {
            dropDownWithAssignedIncLabels[i] = new JLabel(LABEL_SPACING
                    + WITH_ASSIGNED_INC_LABELS[i]);
            dropDownWithAssignedIncLabels[i]
                    .setMaximumSize(DROP_DOWN_MENU_LABEL_DIMENSIONS);
            dropDownWithAssignedIncLabels[i].setForeground(Color.GRAY);
        }
        dropDownWithAssignedIncLabels[6].setForeground(Color.BLACK);
        addMouseListenersToWithAssignedIncLabel(dropDownWithAssignedIncLabels[6]);

    }

    /*
     * Add the labels to the box in order with separators and spacings in
     * between.
     */
    private void addLabelsToBox(Box menu) {
        menu.add(dropDownLabels[0]);
        menu.add(dropDownLabels[1]);
        menu.add(dropDownLabels[2]);

        menu.add(Box.createVerticalStrut(5));
        menu.add(new JSeparator());
        menu.add(Box.createVerticalStrut(5));

        menu.add(dropDownLabels[3]);
        menu.add(dropDownLabels[4]);
        menu.add(dropDownLabels[5]);

        menu.add(Box.createVerticalStrut(5));
        menu.add(new JSeparator());
        menu.add(Box.createVerticalStrut(5));

        menu.add(dropDownLabels[6]);
        menu.add(dropDownLabels[7]);
        menu.add(dropDownLabels[8]);

        menu.add(Box.createVerticalStrut(5));
        menu.add(new JSeparator());
        menu.add(Box.createVerticalStrut(5));

        menu.add(dropDownLabels[9]);
        menu.add(dropDownLabels[10]);

        menu.add(Box.createVerticalStrut(5));
        menu.add(new JSeparator());
        menu.add(Box.createVerticalStrut(5));

        menu.add(dropDownLabels[11]);

        menu.add(Box.createVerticalStrut(5));
        menu.add(new JSeparator());
        menu.add(Box.createVerticalStrut(5));

        menu.add(dropDownLabels[12]);

        menu.add(Box.createVerticalStrut(5));
        menu.add(new JSeparator());
        menu.add(Box.createVerticalStrut(5));

        menu.add(dropDownLabels[13]);

        menu.add(Box.createVerticalStrut(5));
    }

    /*
     * Add the labels to the box in order with separators and spacings in
     * between.
     */
    private void addWithAssignedIncLabelsToBox(Box menu) {
        menu.add(dropDownWithAssignedIncLabels[0]);
        menu.add(dropDownWithAssignedIncLabels[1]);
        menu.add(dropDownWithAssignedIncLabels[2]);
        menu.add(dropDownWithAssignedIncLabels[3]);
        menu.add(dropDownWithAssignedIncLabels[4]);
        menu.add(dropDownWithAssignedIncLabels[5]);

        menu.add(Box.createVerticalStrut(5));
        menu.add(new JSeparator());
        menu.add(Box.createVerticalStrut(5));

        menu.add(dropDownWithAssignedIncLabels[6]);
        menu.add(dropDownWithAssignedIncLabels[7]);
        menu.add(dropDownWithAssignedIncLabels[8]);
        menu.add(dropDownWithAssignedIncLabels[9]);
        menu.add(dropDownWithAssignedIncLabels[10]);
        menu.add(dropDownWithAssignedIncLabels[11]);

        menu.add(Box.createVerticalStrut(5));
        menu.add(new JSeparator());
        menu.add(Box.createVerticalStrut(5));

        menu.add(dropDownWithAssignedIncLabels[12]);
        menu.add(dropDownWithAssignedIncLabels[13]);
        menu.add(dropDownWithAssignedIncLabels[14]);

        menu.add(Box.createVerticalStrut(5));
        menu.add(new JSeparator());
        menu.add(Box.createVerticalStrut(5));

        menu.add(dropDownWithAssignedIncLabels[15]);
        menu.add(dropDownWithAssignedIncLabels[16]);

        menu.add(Box.createVerticalStrut(5));
        menu.add(new JSeparator());
        menu.add(Box.createVerticalStrut(5));

        menu.add(dropDownWithAssignedIncLabels[17]);
        menu.add(dropDownWithAssignedIncLabels[18]);

        menu.add(Box.createVerticalStrut(5));
        menu.add(new JSeparator());
        menu.add(Box.createVerticalStrut(5));

        menu.add(dropDownWithAssignedIncLabels[19]);
        menu.add(dropDownWithAssignedIncLabels[20]);

        menu.add(Box.createVerticalStrut(5));
    }

    /*
     * Creates each button and handles the action performed by the button.
     */
    public void initializeBottomButtons(Box bottomButtons) {
        bottomButtons.add(buttonEnrt = makeButton("ENRT", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    ScreenManager.theCoordinator.setCadDataUnitValue(
                            (String) unitStatusTable.getValueAt(
                                    unitStatusTable.getSelectedRow(), 2),
                            UNIT_TAGS.STATUS, "ENRT");
                    ScreenManager.theCoordinator.setCadDataUnitValue(
                            (String) unitStatusTable.getValueAt(
                                    unitStatusTable.getSelectedRow(), 2),
                            UNIT_TAGS.UNIT_STATUS, UnitStatusEnums.Enroute);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                refreshTable();
            }
        }));
        bottomButtons.add(buttonStage = makeButton("STAGE",
                new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        try {
                            ScreenManager.theCoordinator.setCadDataUnitValue(
                                    (String) unitStatusTable
                                            .getValueAt(unitStatusTable
                                                    .getSelectedRow(), 2),
                                    UNIT_TAGS.STATUS, "STAGE");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        refreshTable();
                    }
                }));
        bottomButtons.add(button1097 = makeButton("10-97",
                new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        try {
                            ScreenManager.theCoordinator.setCadDataUnitValue(
                                    (String) unitStatusTable
                                            .getValueAt(unitStatusTable
                                                    .getSelectedRow(), 2),
                                    UNIT_TAGS.STATUS, "10-97");
                            ScreenManager.theCoordinator.setCadDataUnitValue(
                                    (String) unitStatusTable
                                            .getValueAt(unitStatusTable
                                                    .getSelectedRow(), 2),
                                    UNIT_TAGS.UNIT_STATUS,
                                    UnitStatusEnums.Arrived);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        refreshTable();
                    }
                }));
        bottomButtons.add(buttonCode4 = makeButton("CODE 4",
                new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        try {
                            ScreenManager.theCoordinator.setCadDataUnitValue(
                                    (String) unitStatusTable
                                            .getValueAt(unitStatusTable
                                                    .getSelectedRow(), 2),
                                    UNIT_TAGS.STATUS, "CODE 4");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        refreshTable();
                    }
                }));
        bottomButtons.add(buttonDash1 = makeButton("-", null));
        bottomButtons.add(buttonDash2 = makeButton("-", null));
        bottomButtons.add(buttonDash3 = makeButton("-", null));
        bottomButtons.add(button108 = makeButton("10-8", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    ScreenManager.theCoordinator.setCadDataUnitValue(
                            (String) unitStatusTable.getValueAt(
                                    unitStatusTable.getSelectedRow(), 2),
                            UNIT_TAGS.STATUS, "10-8");
                    ScreenManager.theCoordinator.setCadDataUnitValue(
                            (String) unitStatusTable.getValueAt(
                                    unitStatusTable.getSelectedRow(), 2),
                            UNIT_TAGS.UNIT_STATUS, UnitStatusEnums.Assignable);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                refreshTable();
            }
        }));
        bottomButtons.add(buttonOFC = makeButton("OFC", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    ScreenManager.theCoordinator.setCadDataUnitValue(
                            (String) unitStatusTable.getValueAt(
                                    unitStatusTable.getSelectedRow(), 2),
                            UNIT_TAGS.STATUS, "OFC");
                    ScreenManager.theCoordinator.setCadDataUnitValue(
                            (String) unitStatusTable.getValueAt(
                                    unitStatusTable.getSelectedRow(), 2),
                            UNIT_TAGS.UNIT_STATUS, UnitStatusEnums.Arrived);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                refreshTable();
            }
        }));
        bottomButtons.add(buttonOOS = makeButton("OOS", new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    ScreenManager.theCoordinator.setCadDataUnitValue(
                            (String) unitStatusTable.getValueAt(
                                    unitStatusTable.getSelectedRow(), 2),
                            UNIT_TAGS.STATUS, "OOS");
                    ScreenManager.theCoordinator.setCadDataUnitValue(
                            (String) unitStatusTable.getValueAt(
                                    unitStatusTable.getSelectedRow(), 2),
                            UNIT_TAGS.UNIT_STATUS,
                            UnitStatusEnums.NotAssignable);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                refreshTable();
            }
        }));
    }

    /*
     * Makes a JButton with an text and listener.
     * 
     * @param text the text to be displayed
     * 
     * @param listener the action listener for this button.
     * 
     * @return the JButton.
     */
    public JButton makeButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(Color.GRAY);
        button.setOpaque(false);
        Dimension size = BUTTON_DIMENSIONS;
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
        button.addActionListener(listener);
        button.setFocusable(false);

        return button;
    }

    /*
     * Sets the highlighted color(when the mouse is over it) of the JLabels.
     * Note: the color is not shown until .setOpaque(true) is called.
     * 
     * @param color the highlighted color
     */
    public void setMenuHighlightedBackground(Color color) {
        for (int i = 0; i < dropDownLabels.length; i++) {
            dropDownLabels[i].setBackground(color);
        }
    }

    /*
     * Sets the highlighted color(when the mouse is over it) of the JLabels.
     * Note: the color is not shown until .setOpaque(true) is called.
     * 
     * @param color the highlighted color
     */
    public void setWithAssignedIncMenuHighlightedBackground(Color color) {
        for (int i = 0; i < dropDownWithAssignedIncLabels.length; i++) {
            dropDownWithAssignedIncLabels[i].setBackground(color);
        }
    }

    /*
     * Sets all JLabels to not display a highlighted background
     */
    public void unSelectAllLabels() {
        for (int i = 0; i < dropDownLabels.length; i++) {
            dropDownLabels[i].setOpaque(false);
        }
    }

    /*
     * Sets all JLabels to not display a highlighted background
     */
    public void unSelectAllWithAssignedIncLabels() {
        for (int i = 0; i < dropDownWithAssignedIncLabels.length; i++) {
            dropDownWithAssignedIncLabels[i].setOpaque(false);
        }
    }

    /*
     * Sets the label to have a highlighted background.
     * 
     * @param label the label that is selected/highlighted
     */
    public void selectLabel(Object label) {
        ((JLabel) label).setOpaque(true);
    }

    /*
     * Performs the label action depending on which label was clicked.
     */
    public void performLabelAction(Object label) {
        if (label.equals(dropDownLabels[0])) { // 10-8

        } else if (label.equals(dropDownLabels[1])) { // OFC

        } else if (label.equals(dropDownLabels[2])) { // OOS

        } else if (label.equals(dropDownLabels[3])) { // Open Unit Details

        } else if (label.equals(dropDownLabels[4])) { // Open Unit Activity Log

        } else if (label.equals(dropDownLabels[5])) { // Map

        } else if (label.equals(dropDownLabels[6])) { // Change Vehicle

        } else if (label.equals(dropDownLabels[7])) { // Unit Poll

        } else if (label.equals(dropDownLabels[8])) { // Quick Notes

        } else if (label.equals(dropDownLabels[9])) { // Activate User Timer

        } else if (label.equals(dropDownLabels[10])) { // Filters

        } else if (label.equals(dropDownLabels[11])) { // Page

        } else if (label.equals(dropDownLabels[12])) { // Roster System

        } else if (label.equals(dropDownLabels[13])) { // 10-10

        }
    }

    /*
     * Performs the label action depending on which label was clicked.
     */
    public void performWithAssignedIncLabelAction(Object label) {
        if (label.equals(dropDownWithAssignedIncLabels[0])) { // STAGE

        } else if (label.equals(dropDownWithAssignedIncLabels[1])) { // 10-97

        } else if (label.equals(dropDownWithAssignedIncLabels[2])) { // 10-8

        } else if (label.equals(dropDownWithAssignedIncLabels[3])) { // ASSIGN
                                                                        // ALT...

        } else if (label.equals(dropDownWithAssignedIncLabels[4])) { // ENRT
                                                                        // ALT...

        } else if (label.equals(dropDownWithAssignedIncLabels[5])) { // 10-97
                                                                        // ALT...

        } else if (label.equals(dropDownWithAssignedIncLabels[6])) { // Open
                                                                        // Incident
            int idColumn = 0;
            ScreenManager.openIncidentViewer(Integer
                    .parseInt((String) unitStatusTable.getValueAt(
                            unitStatusTable.getSelectedRow(), idColumn)));
        } else if (label.equals(dropDownWithAssignedIncLabels[7])) { // Recall
                                                                        // Incident

        } else if (label.equals(dropDownWithAssignedIncLabels[8])) { // Open
                                                                        // Unit
                                                                        // Details

        } else if (label.equals(dropDownWithAssignedIncLabels[9])) { // Open
                                                                        // Unit
                                                                        // Activity
                                                                        // Log

        } else if (label.equals(dropDownWithAssignedIncLabels[10])) { // Map

        } else if (label.equals(dropDownWithAssignedIncLabels[11])) { // Recall
                                                                        // Linked
                                                                        // Incidents

        } else if (label.equals(dropDownWithAssignedIncLabels[12])) { // Cancel

        } else if (label.equals(dropDownWithAssignedIncLabels[13])) { // Reassign

        } else if (label.equals(dropDownWithAssignedIncLabels[14])) { // Duplicate

        } else if (label.equals(dropDownWithAssignedIncLabels[15])) { // Unit
                                                                        // Poll

        } else if (label.equals(dropDownWithAssignedIncLabels[16])) { // Quick
                                                                        // Note...

        } else if (label.equals(dropDownWithAssignedIncLabels[17])) { // Activate
                                                                        // User
                                                                        // Timer...

        } else if (label.equals(dropDownWithAssignedIncLabels[18])) { // Filters

        } else if (label.equals(dropDownWithAssignedIncLabels[19])) { // Page...

        } else if (label.equals(dropDownWithAssignedIncLabels[20])) { // Fax...

        }
    }

    /*
     * Factory method. Adds a mouse listeners to the label. The
     * MouseMotionListener detects the mouse's location to highlight the label.
     * The MouseListener detects for clicks and performs the action of the label
     * designates.
     */
    public void addMouseListenersToLabel(JLabel label) {
        label.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
            }

            public void mouseMoved(MouseEvent e) {
                unSelectAllLabels();
                selectLabel(e.getSource());
                unitStatusMenu.revalidate();
                unitStatusMenu.repaint();
            }
        });
        label.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                performLabelAction(e.getSource());
                unSelectAllLabels();
                unitStatusMenu.revalidate();
                unitStatusMenu.repaint();
                unitStatusMenu.setVisible(false);
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }
        });
    }

    /*
     * Factory method. Adds a mouse listeners to the label. The
     * MouseMotionListener detects the mouse's location to highlight the label.
     * The MouseListener detects for clicks and performs the action of the label
     * designates.
     */
    public void addMouseListenersToWithAssignedIncLabel(JLabel label) {
        label.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
            }

            public void mouseMoved(MouseEvent e) {
                unSelectAllLabels();
                selectLabel(e.getSource());
                unitStatusWithAssignedIncMenu.revalidate();
                unitStatusWithAssignedIncMenu.repaint();
            }
        });
        label.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                performWithAssignedIncLabelAction(e.getSource());
                unSelectAllLabels();
                unitStatusWithAssignedIncMenu.revalidate();
                unitStatusWithAssignedIncMenu.repaint();
                unitStatusWithAssignedIncMenu.setVisible(false);
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }
        });
    }

    /*
     * Displays the menu where the right click occurred.
     */
    public void openDropDownMenu(MouseEvent e) {
        unitStatusMenu.setLocation(e.getX() + this.getX(),
                e.getY() + this.getY());
        unitStatusMenu.setVisible(true);
    }

    /*
     * Displays the menu where the right click occurred.
     */
    public void openDropDownWithAssignedIncMenu(MouseEvent e) {
        unitStatusWithAssignedIncMenu.setLocation(e.getX() + this.getX(),
                e.getY() + this.getY());
        unitStatusWithAssignedIncMenu.setVisible(true);
    }

    /*
     * Hides the menu.
     */
    public void closeDropDownMenu() {
        unSelectAllLabels();
        unitStatusMenu.revalidate();
        unitStatusMenu.repaint();
        unitStatusMenu.setVisible(false);
    }

    /*
     * Hides the menu.
     */
    public void closeDropDownWithAssignedIncMenu() {
        unSelectAllLabels();
        unitStatusWithAssignedIncMenu.revalidate();
        unitStatusWithAssignedIncMenu.repaint();
        unitStatusWithAssignedIncMenu.setVisible(false);
    }

    /*
     * Refreshes the data in the table by updating all data and repainting the
     * screen. It saves user preferences(like column sizes, selected row, sorted preferences) 
     * and applies them to the updated model it receives from the server.
     */
    public void refreshTable() {
        if(unitStatusTable.getTableHeader().getResizingColumn() == null){//only update info if resize not in progress
              try {
                int index = unitStatusTable.getSelectedRow();
                List<? extends SortKey> keys = unitStatusTable.getRowSorter().getSortKeys();
                int[] columnWidths = new int[20];
                for(int i = 0; i < unitStatusTable.getColumnCount(); i++){
                    columnWidths[i] = unitStatusTable.getColumnModel().getColumn(i).getWidth();
                }
                
                unitStatusTable.setModel(ScreenManager.theCoordinator.getCadDataTable(CADDataEnums.TABLE.UNIT_STATUS));
                
                for(int i = 0; i < unitStatusTable.getColumnCount(); i++){
                    unitStatusTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
                }
                unitStatusTable.getRowSorter().setSortKeys(keys);
                unitStatusTable.getSelectionModel().setSelectionInterval(index, index);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                revalidate();
                repaint();
          }
    }

    /*
     * Makes screen visible.
     */
    public void open() {
        setVisible(true);
    }

    /*
     * Hides screen.
     */
    public void close() {
        setVisible(false);
    }

    /**
     * This method is called every second in ScreenManger to update the display
     * time every second.
     */
    public void handleUpdateTime() {
        int timerColumn = 13;
        int unitColumn = 2;
        for (int i = 0; i < unitStatusTable.getModel().getRowCount(); i++) {
            try {
                unitStatusTable.getModel().setValueAt(
                        ScreenManager.theCoordinator.getCadDataUnitValue(
                                (String) unitStatusTable.getModel().getValueAt(
                                        i, unitColumn), UNIT_TAGS.TIMER), i,
                        timerColumn);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Removes drag and drop feature/button clicking.
     */
    public void removeDispatcherAuthority() {
        unitStatusTable.setTransferHandler(new TransferHandler(""));
        buttonEnrt.setEnabled(false);
        buttonStage.setEnabled(false);
        button1097.setEnabled(false);
        buttonCode4.setEnabled(false);
        buttonDash1.setEnabled(false);
        buttonDash2.setEnabled(false);
        buttonDash3.setEnabled(false);
        button108.setEnabled(false);
        buttonOFC.setEnabled(false);
        buttonOOS.setEnabled(false);
    }

}