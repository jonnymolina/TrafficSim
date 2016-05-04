package tmcsim.simulationmanager.dialogs;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import tmcsim.client.cadclientgui.data.Incident;
import tmcsim.simulationmanager.model.AddIncidentListTableModel;
import tmcsim.simulationmanager.model.IncidentTimeCellEditor;
import tmcsim.simulationmanager.model.IncidentTimeCellRenderer;
import tmcsim.simulationmanager.model.IncidentTimeSpinnerModel;
import tmcsim.simulationmanager.model.AddIncidentListTableModel.ADD_INCIDENT_LIST_COLUMNS;


/**
 * This dialog is used when the user chooses to add a new incident into the 
 * simulation.  To show the dialog, mthe showDialog() method is called with
 * the Incidents to display in the dialog.  These incidents are displayed to 
 * the user, who may then select which incidents to add to the simulation, 
 * and assign a time that each incident will occur.  
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/14 00:12:38 $ $Revision: 1.7 
 */
@SuppressWarnings("serial")
public class AddIncidentDialog extends JDialog implements ActionListener {
    
    /**
     * Constructor.  Initialize lists and GUI elements.
     */ 
    public AddIncidentDialog(Frame parent) {
        super(parent);

        setTitle("Add New Incidents");
        setModal(true);
        
        setSize(new Dimension(600, 350));
        setResizable(false);
    
        initComponents();   
    }
    
    /**
     * Populate the Incident Table with the parameter data.
     *
     * @param incidentList Vector of Incidents that have been read in from a
     * selected file.
     */
    public void setModelData(Vector<Incident> incidentList) {
        incidentListTableModel.clearModelData();
        
        for(Incident inc : incidentList) {
            incidentListTableModel.addIncident(inc);
        }

    }
    
    /**
     * Reposition the dialog to the center of the screen at set visible.
     */
    public void showDialog() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width/2 - getWidth()/2, screenSize.height/2 - getHeight()/2);
        
        setVisible(true);
    }
    
    /**
     * Method gets a map of incidents, and the scheduled start time
     * for all incidents that have been selected in the table.
     * 
     * @return Map of incident log numbers (key) and scheduled start time (value)
     */
    public TreeMap<Integer, Long> getSelectedIncidentTimes() {
        return incidentListTableModel.getSelectedIncidentTimes();
    }
    
    public void actionPerformed(ActionEvent evt) {
    
        if(evt.getSource().equals(okButton)) {
            incidentListTable.getColumnModel().getColumn(
                    ADD_INCIDENT_LIST_COLUMNS.SCHEDULED_COL.colNum)
                    .getCellEditor().stopCellEditing(); 
            
            setVisible(false);
        }
        else if(evt.getSource().equals(cancelButton)) {             
            incidentListTable.getColumnModel().getColumn(
                    ADD_INCIDENT_LIST_COLUMNS.SCHEDULED_COL.colNum)
                    .getCellEditor().cancelCellEditing();
            
            incidentListTableModel.clearModelData();

            setVisible(false);
        }
            
    }
    
    /**
     * Initialize GUI Swing elements and listeners.
     */
    private void initComponents() {

        initIncidentTable();
        initButtons();
              
        addIncidentBox = new Box(BoxLayout.Y_AXIS);
        addIncidentBox.add(Box.createVerticalStrut(10));
        addIncidentBox.add(incidentListPane);  
        addIncidentBox.add(Box.createVerticalStrut(5));        
        addIncidentBox.add(buttonBox);        
        addIncidentBox.add(Box.createVerticalStrut(10));
        
        getContentPane().add(addIncidentBox);
        pack();
        
    }   
    
    /**
     * Initialize Incident Table Swing elements
     */
    private void initIncidentTable() {
        
        incidentTimeModel = new IncidentTimeSpinnerModel();
        incidentSpinner   = new JSpinner(incidentTimeModel);
        
        incidentListTableModel = new AddIncidentListTableModel();        
        incidentListTable = new JTable(incidentListTableModel);
        incidentListTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        incidentListTable.setDragEnabled(false);      
                
        for(int c = 0; c < incidentListTable.getColumnCount(); c++) {
            incidentListTable.getColumnModel().getColumn(c).setMinWidth(
                    incidentListTableModel.getColumnMinWidth(c));
            incidentListTable.getColumnModel().getColumn(c).setMaxWidth(
                    incidentListTableModel.getColumnMaxWidth(c));
            incidentListTable.getColumnModel().getColumn(c).setPreferredWidth(
                    incidentListTableModel.getColumnPrefWidth(c));
            incidentListTable.getColumnModel().getColumn(c).setResizable(true);
            
            if(c == ADD_INCIDENT_LIST_COLUMNS.EVENT_LEN_COL.colNum) {
                incidentListTable.getColumnModel().getColumn(c).setCellRenderer(
                        new IncidentTimeCellRenderer());                
            }
            else if(c == ADD_INCIDENT_LIST_COLUMNS.SCHEDULED_COL.colNum) {
                incidentListTable.getColumnModel().getColumn(c).setCellRenderer(
                        new IncidentTimeCellRenderer());
                incidentListTable.getColumnModel().getColumn(c).setCellEditor(
                        new IncidentTimeCellEditor(incidentSpinner));
            }               
        }
        
                
        incidentListPane  = new JScrollPane();      
        incidentListPane.setMaximumSize(new Dimension(650, 400));       
        incidentListPane.setMinimumSize(new Dimension(525, 200));   
        incidentListPane.setPreferredSize(new Dimension(525, 200));
        incidentListPane.setViewportView(incidentListTable);
        incidentListPane.setAlignmentX(Box.CENTER_ALIGNMENT);       

        TitledBorder title = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                "Possible Incidents");
        title.setTitleJustification(TitledBorder.LEFT);
        incidentListPane.setBorder(title);                
        
    }
    
    /**
     * Intialize Ok and Cancel button Swing elements and listeners.
     */
    private void initButtons() {
        
        okButton = new JButton("OK");
        okButton.addActionListener(this);
        
        cancelButton = new JButton("Cancel");        
        cancelButton.addActionListener(this);
        
        buttonBox = new Box(BoxLayout.X_AXIS);
        buttonBox.add(okButton);
        buttonBox.add(Box.createHorizontalStrut(50));
        buttonBox.add(cancelButton);
        buttonBox.setAlignmentX(Box.CENTER_ALIGNMENT);      
    }   


    private JTable incidentListTable;
    private AddIncidentListTableModel incidentListTableModel;
    private JScrollPane incidentListPane;
    private JSpinner incidentSpinner;
    private IncidentTimeSpinnerModel incidentTimeModel; 
    
    private JButton cancelButton;;
    private JButton okButton;
    
    private Box addIncidentBox;
    private Box buttonBox;  
}