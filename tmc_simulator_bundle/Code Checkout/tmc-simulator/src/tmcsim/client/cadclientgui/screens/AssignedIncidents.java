package tmcsim.client.cadclientgui.screens;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import tmcsim.client.cadclientgui.enums.CADDataEnums;
import tmcsim.client.cadclientgui.enums.CADScriptTags.UNIT_TAGS;
import tmcsim.client.cadclientgui.enums.IncidentEnums;
import tmcsim.client.cadclientgui.enums.TableHeaders;
import tmcsim.client.cadclientgui.enums.UnitStatusEnums;

/**
 * This class contains the view and controller for the AssignedIncidents screen. The view was not built using a GUI builder plug-in
 * (but may want to consider doing so in the future), and the controller uses listeners to control how the view and data act. 
 * 
 * @author Vincent
 */

public class AssignedIncidents extends JFrame {

    private final String SCREEN_TITLE = "(Shift + F3)  Assigned Incidents";

    private final Dimension SCREEN_DIMENSIONS = new Dimension(1400, 250);

    private final Dimension DROP_DOWN_MENU_LABEL_DIMENSIONS = new Dimension(
            170, 20);

    private final Dimension DROP_DOWN_MENU_DIMENSIONS = new Dimension(170, 230);

    private final int COLUMN_WIDTH = 120;

    private final String[] LABELS = { "Add Resources...", "Greater Alarm...",
            "Reconfigure", "Open", "Recall Incident", "Cancel", "Reassign",
            "Link/Append", "Map", "Recall Linked Incidents", "Read Notes",
            "Page...", "Mail...", "Fax..." };

    private final String LABEL_SPACING = "     ";

    private JTable assignedIncidentsTable;
    private JFrame assignedIncidentsMenu;

    // labels for the drop down menu
    private JLabel[] dropDownLabels = new JLabel[LABELS.length];

    private long lastLeftClick;// used for double clicking feature

    public AssignedIncidents() {
        initComponents();
    }

    private void initComponents() {
        initializeTable();
        initController();
        initializeDropDownMenu();

        JScrollPane scrollpane = new JScrollPane(assignedIncidentsTable);
        scrollpane.getViewport().setBackground(Color.WHITE);

        setTitle(SCREEN_TITLE);
        setPreferredSize(SCREEN_DIMENSIONS);
        getContentPane().add(scrollpane);
        setResizable(true);
        pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(0, (int) (dim.getHeight() / 4));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setVisible(false);
    }

    /*
     * Initializes the table and prepares the cell renderer for color
     * management. It initializes the default settings and handles the drag and
     * drop feature.
     */
    private void initializeTable() {
        assignedIncidentsTable = new JTable(){
            /*
             * Custom renderer to set different background/foreground colors
             * @see javax.swing.JTable#prepareRenderer(javax.swing.table.TableCellRenderer, int, int)
             */
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
                Component comp = super.prepareRenderer(renderer, row, column);
                
                comp.setForeground(Color.BLACK);
                comp.setBackground(Color.CYAN);
                if(assignedIncidentsTable.getModel().getColumnName(column).equals("Unit/s")){//4 is the column for "Unit/s"
                  System.out.println("Commenting this line breaks the client");
                    comp.setBackground(Color.BLACK);
                    int primaryColumn = 3;
                    try {
                        switch(ScreenManager.theCoordinator.getCadDataUnitStatus(
                               (String) assignedIncidentsTable.getValueAt(row,primaryColumn))){
                            case Assignable:
                                comp.setForeground(Color.GREEN);
                                break;
                            case Arrived:
                                comp.setForeground(Color.YELLOW);
                                break;
                            case Enroute:
                                comp.setForeground(Color.CYAN);
                                break;
                        } 
                    }catch (RemoteException e){
                      e.printStackTrace();
                    }
                }
                
                if (getSelectedRow() == row){
                    comp.setForeground(Color.WHITE);
                    comp.setBackground(Color.BLUE);
                }
                
                return comp;
            }
            
            public Class getColumnClass(int c) {
                return getValueAt(0, c).getClass();
            }
            
        };
        
        assignedIncidentsTable.setOpaque(true);
        assignedIncidentsTable.setIntercellSpacing(new Dimension(1, 0));
        assignedIncidentsTable.setGridColor(Color.WHITE);
        assignedIncidentsTable.getTableHeader().setReorderingAllowed(false);
        assignedIncidentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        assignedIncidentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        assignedIncidentsTable.setAutoCreateRowSorter(true);
        assignedIncidentsTable.setModel(new DefaultTableModel());
        
        ((DefaultTableModel) assignedIncidentsTable.getModel()).setColumnIdentifiers(TableHeaders.ASSIGNED_INCIDENTS_HEADERS);
        
        assignedIncidentsTable.setTransferHandler(new TransferHandler(){
            
            public boolean canImport(TransferHandler.TransferSupport info) {
                // Check for String flavor
                if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    return false;
                }
                return true;
           }
            
            public boolean importData(TransferHandler.TransferSupport info) {
                if (!info.isDrop()) {
                    return false;
                }
         
                DefaultTableModel tableModel = (DefaultTableModel)assignedIncidentsTable.getModel();
                JTable.DropLocation dl = (JTable.DropLocation)info.getDropLocation();
                int index = dl.getRow();
         
                // Get the string that is being dropped.
                Transferable t = info.getTransferable();
                String data;
                try {
                    data = (String)t.getTransferData(DataFlavor.stringFlavor);
                }
                catch (Exception e) { return false; }
                                         
                // Perform the actual import
                int incidentId = (Integer)assignedIncidentsTable.getValueAt(dl.getRow(), 0);
                try {
                        ScreenManager.theCoordinator.setCadDataUnitAssignedId(data, incidentId);
                        ScreenManager.theCoordinator.setCadDataUnitValue(data,
                      UNIT_TAGS.UNIT_STATUS, UnitStatusEnums.Arrived);
                     ScreenManager.theCoordinator.addCadDataIncidentAssignedUnitNum(incidentId, data);
                     ScreenManager.theCoordinator.setCadDataIncidentStatus(incidentId, IncidentEnums.Assigned);
                     } catch (RemoteException e) {
                        e.printStackTrace();
                     }
                ScreenManager.refreshScreens();
                return true;
            }
        });
        
        for(int i = 0; i < assignedIncidentsTable.getColumnCount(); i++){
            assignedIncidentsTable.getColumnModel().getColumn(i).setPreferredWidth(120);
        }
    }
    
    /*
     * Adds the key and mouse listeners for the table and component listener for
     * screen.
     */
    private void initController() {
        assignedIncidentsTable.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (System.currentTimeMillis() - lastLeftClick < 1000) {
                        int idColumn = 0;
                        ScreenManager
                                .openIncidentViewer((Integer) assignedIncidentsTable
                                        .getValueAt(assignedIncidentsTable
                                                .getSelectedRow(), idColumn));
                    } else {
                        lastLeftClick = System.currentTimeMillis();
                    }
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    openDropDownMenu(e);
                } else {
                    closeDropDownMenu();
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
    private void initializeDropDownMenu() {
        Box menu = new Box(BoxLayout.Y_AXIS);
        initializeDropDownLabels();
        addLabelsToBox(menu);

        // Sets the highlighted background color, note that it does not become
        // "Highlighted" until opaque(true) is called
        setMenuHighlightedBackground(Color.BLUE);

        assignedIncidentsMenu = new JFrame();
        assignedIncidentsMenu.getContentPane().add(menu);
        assignedIncidentsMenu.setPreferredSize(DROP_DOWN_MENU_DIMENSIONS);
        assignedIncidentsMenu.setUndecorated(true);
        assignedIncidentsMenu.pack();
        assignedIncidentsMenu.setVisible(false);
    }

    /*
     * Sets the text and size and adds a listener to each activated label.
     */
    private void initializeDropDownLabels() {
        for (int i = 0; i < dropDownLabels.length; i++) {
            dropDownLabels[i] = new JLabel(LABEL_SPACING + LABELS[i]);
            dropDownLabels[i].setMaximumSize(DROP_DOWN_MENU_LABEL_DIMENSIONS);
            dropDownLabels[i].setForeground(Color.GRAY);
        }
        dropDownLabels[3].setForeground(Color.BLACK);
        addMouseListenersToLabel(dropDownLabels[3]);
        dropDownLabels[10].setForeground(Color.BLACK);
        addMouseListenersToLabel(dropDownLabels[10]);
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
        menu.add(dropDownLabels[6]);
        menu.add(dropDownLabels[7]);

        menu.add(Box.createVerticalStrut(5));
        menu.add(new JSeparator());
        menu.add(Box.createVerticalStrut(5));

        menu.add(dropDownLabels[8]);
        menu.add(dropDownLabels[9]);
        menu.add(dropDownLabels[10]);

        menu.add(Box.createVerticalStrut(5));
        menu.add(new JSeparator());
        menu.add(Box.createVerticalStrut(5));

        menu.add(dropDownLabels[11]);
        menu.add(dropDownLabels[12]);
        menu.add(dropDownLabels[13]);

        menu.add(Box.createVerticalStrut(5));
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
     * Sets all JLabels to not display a highlighted background
     */
    public void unSelectAllLabels() {
        for (int i = 0; i < dropDownLabels.length; i++) {
            dropDownLabels[i].setOpaque(false);
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
        if (label.equals(dropDownLabels[0])) {// Add Resources

        } else if (label.equals(dropDownLabels[1])) {// Greater Alarm

        } else if (label.equals(dropDownLabels[2])) {// Reconfigure

        } else if (label.equals(dropDownLabels[3])) {// Open
            int idColumn = 0;
            ScreenManager.openIncidentViewer((Integer) assignedIncidentsTable
                    .getValueAt(assignedIncidentsTable.getSelectedRow(),
                            idColumn));
        } else if (label.equals(dropDownLabels[4])) {// Recall Incident

        } else if (label.equals(dropDownLabels[5])) {// Cancel

        } else if (label.equals(dropDownLabels[6])) {// Reassign

        } else if (label.equals(dropDownLabels[7])) {// Link append

        } else if (label.equals(dropDownLabels[8])) {// Map

        } else if (label.equals(dropDownLabels[9])) {// Recall Linked Incidents

        } else if (label.equals(dropDownLabels[10])) {// Read Notes
            int idColumn = 0;
            ScreenManager.openIncidentViewer((Integer) assignedIncidentsTable
                    .getValueAt(assignedIncidentsTable.getSelectedRow(),
                            idColumn));
        } else if (label.equals(dropDownLabels[11])) {// Page

        } else if (label.equals(dropDownLabels[12])) {// Mail

        } else if (label.equals(dropDownLabels[13])) {// Fax

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
                assignedIncidentsMenu.revalidate();
                assignedIncidentsMenu.repaint();
            }
        });
        label.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                performLabelAction(e.getSource());
                unSelectAllLabels();
                assignedIncidentsMenu.revalidate();
                assignedIncidentsMenu.repaint();
                assignedIncidentsMenu.setVisible(false);
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
        assignedIncidentsMenu.setLocation(e.getX() + this.getX(), e.getY()
                + this.getY());
        assignedIncidentsMenu.setVisible(true);
    }

    /*
     * Hides the menu.
     */
    public void closeDropDownMenu() {
        unSelectAllLabels();
        assignedIncidentsMenu.revalidate();
        assignedIncidentsMenu.repaint();
        assignedIncidentsMenu.setVisible(false);
    }

    /*
     * Refreshes the data in the table by updating all data and repainting the
     * screen. It saves user preferences(like column sizes, selected row, sorted preferences) 
     * and applies them to the updated model it receives from the server.
     */
    public void refreshTable() {
        
        if(assignedIncidentsTable.getTableHeader().getResizingColumn() == null){//only update info if resize not in progress
              try {
                int index = assignedIncidentsTable.getSelectedRow();
                int[] columnWidths = new int[20];
                List<? extends SortKey> keys = assignedIncidentsTable.getRowSorter().getSortKeys();
                for(int i = 0; i < assignedIncidentsTable.getColumnCount(); i++){
                    columnWidths[i] = assignedIncidentsTable.getColumnModel().getColumn(i).getWidth();
                }
                
                assignedIncidentsTable.setModel(ScreenManager.theCoordinator.getCadDataTable(CADDataEnums.TABLE.ASSIGNED_INCIDENTS));
                
                for(int i = 0; i < assignedIncidentsTable.getColumnCount(); i++){
                    assignedIncidentsTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
                }
                assignedIncidentsTable.getRowSorter().setSortKeys(keys);
                assignedIncidentsTable.getSelectionModel().setSelectionInterval(index, index);
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

}
