 package tmcsim.simulationmanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import tmcsim.cadmodels.CMSDiversion;
import tmcsim.cadmodels.CMSInfo;
import tmcsim.client.cadclientgui.data.Incident;
import tmcsim.client.cadclientgui.data.IncidentEvent;
import tmcsim.common.ScriptException;
import tmcsim.common.SimulationException;
import tmcsim.common.CADEnums.PARAMICS_STATUS;
import tmcsim.common.CADEnums.SCRIPT_STATUS;
import tmcsim.simulationmanager.actions.AddIncidentAction;
import tmcsim.simulationmanager.actions.ApplyDiversionAction;
import tmcsim.simulationmanager.actions.ConnectToParamicsAction;
import tmcsim.simulationmanager.actions.DeleteIncidentAction;
import tmcsim.simulationmanager.actions.ExitAction;
import tmcsim.simulationmanager.actions.GotoTimeIndexAction;
import tmcsim.simulationmanager.actions.LoadParamicsNetworkAction;
import tmcsim.simulationmanager.actions.LoadScriptAction;
import tmcsim.simulationmanager.actions.PauseSimulationAction;
import tmcsim.simulationmanager.actions.RescheduleIncidentAction;
import tmcsim.simulationmanager.actions.ResetSimulationAction;
import tmcsim.simulationmanager.actions.StartSimulationAction;
import tmcsim.simulationmanager.actions.TriggerIncidentAction;
import tmcsim.simulationmanager.model.AppliedDiversionTableModel;
import tmcsim.simulationmanager.model.IncidentListTableModel;
import tmcsim.simulationmanager.model.IncidentTimeCellRenderer;
import tmcsim.simulationmanager.model.IncidentTimeSpinnerModel;
import tmcsim.simulationmanager.model.AppliedDiversionTableModel.APPLIED_DIV_COLUMNS;
import tmcsim.simulationmanager.model.IncidentListTableModel.INCIDENT_LIST_COLUMNS;

/**
 * This class is the view component to the SimlationManager.  User 
 * interaction with the SimulationManager is handled in this object.  
 * The data contained within the SimlationManagerModel class is 
 * displayed in the JTables contained in this class.  The view class 
 * does not validate the user input.  Exception handling
 * takes care of error in the user input.
 *
 * @author Matthew Cechini 
 * @version $Revision: 1.6 $ $Date: 2006/06/06 22:52:56 $
 */
@SuppressWarnings("serial")
public class SimulationManagerView extends JFrame { 

    /** Default Paramics network ID */
    private static int DEFAULT_NETWORK_ID = 1;

    /** Maximum Paramics network ID (100) */
    private static int MAX_NETWORK_ID    = 100;

    /** Default directory for script loading. */
    public static String SCRIPT_DIR      = ".";

    /** Reference to the simulation manager model. */
    private SimulationManagerModel theSimManagerModel;
        
    /** The Table Model for the incident list table. */
    private IncidentListTableModel incidentListTableModel;
    
    /** The Table Model for the applied diversions table. */    
    private AppliedDiversionTableModel appliedDiversionTableModel;
    
    /**
     * A map of DefaultTableModel objects associated with each incident
     * in the simulation.  Key values are the log numberes of incidents.  
     */
    //private TreeMap<Integer, IncidentHistoryTableModel> incidentTableMap = null;
    private TreeMap<Integer, IncidentHistoryPanel> incidentTableMap = null;
    
    /** The current simulation time. */
    private long currentSimulationTime = 0;
    
    /**
     * Flag to designate whether the simulation has started. Initialized to false.
     * Becomes true when the user presses the start button.  Becomes false again
     * when the simulation is "reset" or another script is loaded.
     */
    private boolean simulationStarted = false;
    
    /** 
     * Flag to designate whether a connection has been made to the remote 
     * Paramics Communicator.
     */
    private boolean connectedToParamics = false;
    
    
    /**
     * Constructor. This class sets the local reference to the simulation manager
     * model to the parameter object.  It then initializes all of the swing
     * components that are used in this user interface.
     *
     * @param newModel The reference to the SimulationManagerModel object
     */
    public SimulationManagerView(SimulationManagerModel newModel) {
        super("Simulation Manager");
        
        theSimManagerModel   = newModel;    
        
        incidentTableMap = new TreeMap<Integer, IncidentHistoryPanel>();
        
        initComponents();

    }
 
    /**
     * Method gets the model object for the SimulationManagerView class.
     * @return SimulationManagerModel object.
     */
    public SimulationManagerModel getModel() {
        return theSimManagerModel;
    }
    
    /**
     * Method returns the boolean value designating whether a current
     * connection has been made to the Paramics Communicator.
     * 
     * @return True if connected, false if not.
     */
    public Boolean isConnectedToParamics() {
        return connectedToParamics;
    }
    
    /**
     * Method returns the boolean value designating whether the 
     * simulation has started or not.
     * 
     * @return True if started, false if not.
     */
    public Boolean isSimulationStarted() {
        return simulationStarted;
    }
    
    /**
     * Method returns the value of the current simulation time.
     * 
     * @return Current simulation time (in seconds).
     */
    public Long getCurrentSimTime() {
        return currentSimulationTime;
    }
    
    /**
     * Creates a local JTable for the event history portion of the user interface
     * with the table model passed in as a parameter.  The table has three columns.
     * Addition and subtraction to this table are handled in the model clas.
     *
     * @param logNumber Log number for this incident.
     */ 
    public void addIncidentTab(Integer logNumber) {
        
        IncidentHistoryPanel newHistoryPanel = new IncidentHistoryPanel();
    
        incidentTableMap.put(logNumber, newHistoryPanel);

        eventHistoryPane.addTab(String.valueOf(logNumber), newHistoryPanel);
        
    }
    
    /**
     * Remove the incident tab whose log number matches the parameter.
     * 
     * @param logNumber Incident log number.
     */
    public void removeIncidentTab(Integer logNumber) {
        for(int i = 0; i < eventHistoryPane.getTabCount(); i++) {
            if(eventHistoryPane.getTitleAt(i).compareTo(String.valueOf(logNumber)) == 0) {
                eventHistoryPane.remove(i);
                break;  
            }
        }
    }
    
    /**
     * Remove all incident tabs from the event history pane.
     */
    public void resetIncidentTabs() {
        eventHistoryPane.removeAll();
    }
    
    /**
     * Method is called to add a new incident to the IncidentManagement
     * panel.  The incident object is added to the IncidentListTableModel.
     * 
     * @param newIncident The incident object being added to the simulation. 
     */ 
    public void addIncident(Incident newIncident) {
        incidentListTableModel.addIncident(newIncident);
    }   
    
    /**
     * Method starts an incident found in the IncidentListTableModel. 
     * @param logNumber Incident log number.
     */
    public void startIncident(Integer logNumber) {
        incidentListTableModel.startIncident(logNumber);        
    }   
    
    /**
     * Method removes an incident by removing it from the 
     * IncidentListTableModel.  If there are no more incidents
     * in the simulation, reset the start, pause, and reset buttons
     * to be disabled, and enable the load script button.
     * 
     * @param logNumber Incident log number.
     */
    public void removeIncident(Integer logNumber) {
        incidentListTableModel.removeIncident(logNumber);       
        
        //TODO check if simulation started??    
        if(incidentListTableModel.getRowCount() == 0 && !simulationStarted) 
        {
            startButton.setEnabled(false);
            pauseButton.setEnabled(false);
            resetButton.setEnabled(false);  
            
            loadScriptButton.setEnabled(true);
        }       
    }
    
    /**
     * Method resets the current list of Incidents by clearing the 
     * IncidentListTableModel. 
     */ 
    public void resetIncidents() {
        incidentListTableModel.clearModelData();
    }
    
    /**
     * Method adds a new Incident event to the IncidentHistoryTableModel
     * that corresponds to the Incident log number.  If the IncidentEvent
     * has been received from a terminal, the tab containing the 
     * incident's table will be colored red to notify the user.
     * 
     * @param logNumber Incident log number.
     * @param newEvent Incident event to add.
     */
    public void addIncidentEvent(Integer logNumber, IncidentEvent newEvent) {
        //IncidentHistoryTableModel historyTableModel = incidentTableMap.get(logNumber);
        IncidentHistoryPanel historyPanel = incidentTableMap.get(logNumber);
        
        //historyTableModel.addEvent(logNumber, newEvent);
        historyPanel.updateIncidentHistory(newEvent);
    }
    
    /**
     * Method is called to add a new diversion to the Diversions panel.  
     * The CMSInfo and CMSDiversion objects are added to the 
     * AppliedDiversionTableModel, which creates its needed view data.
     * 
     * @param theCMSInfo The CMSInfo that the new diversion corresponds to.
     * @param theDiversion A new diversion to be applied.
     */
    public void addDiversion(CMSInfo theCMSInfo, CMSDiversion theDiversion) {       
        appliedDiversionTableModel.addDiversion(theCMSInfo, theDiversion);
    }       

    /**
     * Method is called to remove an existing diversion from the Diversions panel.  
     * The diversion information is removed from the AppliedDiversionTableModel, 
     * which removes the corresponding row.
     * 
     * @param theCMSInfo The CMSInfo that the new diversion corresponds to.
     * @param theDiversion A new diversion to be applied.
     */
    public void removeDiversion(CMSInfo theCMSInfo, CMSDiversion theDiversion) {
        appliedDiversionTableModel.removeDiversion(theCMSInfo, theDiversion);

    }
    
    /**
     * Method resets the list of diversions by clearing the 
     * AppliedDiversionTableModel.
     */
    public void resetDiversions() {
        appliedDiversionTableModel.clearModelData();
    }
    
    /**
     * Build the CMS ID combo box with the list of parameter ids.
     * 
     * @param cmsIDArray Array of cms IDs
     */
    public void setCMS_IDList(Object[] cmsIDArray) {
        for(int i = 0; i < cmsIDArray.length; i++) {
            cmsIDComboBox.addItem(cmsIDArray[i]);   
        }
    }
    
    /**
     * Method is called by the model everytime a second has passed during the simulation.
     * The time in the parameter is shown in the simulationClockLabel.  The parameter
     * is formatted HH:MM:SS.
     *
     * @param time The new time.
     */
    public void tick(long time) {
        currentSimulationTime = time;
        
        String newTime = longToTime(currentSimulationTime);
        
        simulationClockLabel.setText(newTime);
        CADClientClockLabel.setText(newTime);
    }
    
    /**
     * Method updates the simulation control and 'load script' buttons' enabled status
     * and the status text according to the parameter SCRIPT_STATUS.
     * The following table describes each status and the actions taken. <br>
     *
     *<table cellpadding="2" cellspacing="2" border="1"
     * style="text-align: left; width: 250px;">
     *  <tbody>
     *    <tr>
     *      <th>Status<br></th>
     *      <th>Actions Taken<br></th>
     *    </tr>
     *    <tr>
     *      <td>NO_SCRIPT<br></td>
     *      <td>Set the simulation status text to a black "No Script".  Then 
     *          disable all simulation buttons.  Enable the 'Load Script' button.<br></td>
     *    </tr>
     *    <tr>
     *      <td>SCRIPT_STOPPED_NOT_STARTED<br></td>
     *      <td>Set the simulation status text to a red "Not Started".  Then 
     *          enable the 'Start' and 'Reset' buttons, and disable the 'Pause'
     *          button. Enable the 'Load Script' button.<br></td>
     *    </tr>
     *    <tr>
     *      <td>SCRIPT_STOPPED_STARTED<br></td>
     *      <td>Set the simulation status text to a red "Paused".  Then 
     *          enable the 'Start' and 'Reset' buttons, and disable the 'Pause'
     *          button. Enable the 'Load Script' button. Set the simulationStarted
     *          flag to true.<br></td>
     *    </tr>
     *    <tr>
     *      <td>SCRIPT_RUNNING<br></td>
     *      <td>Set the simulation status text to a green "Running".  Then 
     *          disable the 'Start' and 'Reset' buttons, and enable the 'Pause'
     *          button. Disable the 'Load Script' button.  Set the simulationStarted
     *          flag to true.<br></td>
     *    </tr>
     *    <tr>
     *      <td>ATMS_SYNCHRONIZATION<br></td>
     *      <td>Set the simulation status text to an orange "Synchronizing".  Then 
     *          disable all simulation buttons and the 'Load Script' button.<br></td>
     *    </tr>
     *  </tbody>
     *</table>
     *
     * @param status Script status value.
     */
    public void setScriptStatus(SCRIPT_STATUS status) {
        
        switch(status) {
            case NO_SCRIPT:
                simulationStatusText.setText("No Script");
                simulationStatusText.setForeground(Color.BLACK);
                    
                startButton.setEnabled(false);
                pauseButton.setEnabled(false);
                resetButton.setEnabled(false);                                              
                break;          
                
            case SCRIPT_STOPPED_NOT_STARTED:
                
                simulationStatusText.setText("Ready");
                simulationStatusText.setForeground(Color.RED);
                    
                startButton.setEnabled(true);
                pauseButton.setEnabled(false);
                resetButton.setEnabled(true);       
                
                loadScriptButton.setEnabled(true);
                
                //simulationClockLabel.setText("0:00:00");
                //CADClientClockLabel.setText("0:00:00");               
                break;          
                
            case SCRIPT_PAUSED_STARTED:
                simulationStatusText.setText("Paused");
                simulationStatusText.setForeground(Color.RED);
                    
                startButton.setEnabled(true);
                pauseButton.setEnabled(false);
                resetButton.setEnabled(true);               
                simulationStarted = true;

                loadScriptButton.setEnabled(true);
                break;
                
            case SCRIPT_RUNNING:
                simulationStatusText.setText("Running");
                simulationStatusText.setForeground(Color.GREEN.darker());
                
                startButton.setEnabled(false);
                pauseButton.setEnabled(true);
                resetButton.setEnabled(false);  
                simulationStarted = true;

                loadScriptButton.setEnabled(false);
                break;
            case ATMS_SYNCHRONIZATION:
                simulationStatusText.setText("Synchronizing");
                simulationStatusText.setForeground(Color.ORANGE);
                
                startButton.setEnabled(false);
                pauseButton.setEnabled(false);
                resetButton.setEnabled(false);  
                simulationStarted = false;

                loadScriptButton.setEnabled(false);
                break;
        }       
    }
    

    /**
     * Method updates the paramics control buttons' enabled status
     * and the paramics status text according to the parameter PARAMICS_STATUS.
     * The following table describes each status and the actions taken. <br>
     *
     *<table cellpadding="2" cellspacing="2" border="1"
     * style="text-align: left; width: 250px;">
     *  <tbody>
     *    <tr>
     *      <th>Status<br></th>
     *      <th>Actions Taken<br></th>
     *    </tr>
     *    <tr>
     *      <td>UNREACHABLE<br></td>
     *      <td>Show a message dialog to the user notifying them that a 
     *          connection to Paramics could not be made.  Reset the
     *          connect button to be labled 'Connect to Paramics' and
     *          set the status text to 'Unreachable'.  Enable the connect
     *          button and disable the network load button.  Set the connected
     *          to paramics flag to false.<br></td>
     *    </tr>
     *    <tr>
     *      <td>DROPPED<br></td>
     *      <td>Show a message dialog to the user notifying them that the 
     *          connection to Paramics has been dropped.  Reset the
     *          connect button to be labled 'Connect to Paramics' and
     *          set the status text to 'Dropped'.  <br></td>
     *    </tr>
     *    <tr>
     *      <td>CONNECTING<br></td>
     *      <td>Set the status text to 'Connecting'.  Disable the connect
     *          button and network load button.  <br></td>          
     *    </tr>
     *    <tr>
     *      <td>CONNECTED<br></td>
     *      <td>Set the connect button to be labled 'Disconnect from Paramics' 
     *          and set the status text to 'Connected'.  Enable the disconnect
     *          button and network load button.  Set the connected to paramics flag 
     *          to true.<br></td>
     *    </tr>
     *    <tr>
     *      <td>DISCONNECTED<br></td>
     *      <td>Set the connect button to be labled 'Connect to Paramics' 
     *          and set the status text to 'Disconnected'.  Enable the connect
     *          button and disable the network load button.  Set the connected 
     *          to paramics flag to false.<br></td>
     *    </tr>
     *    <tr>
     *      <td>SENDING_NETWORK_ID<br></td>
     *      <td>Set the status text to 'Sending Network ID'.  Disable the 
     *          network load button.<br></td>      
     *    </tr>
     *    <tr>
     *      <td>WARMING<br></td>
     *      <td>Set the status text to 'Warming'.<br></td>
     *    </tr>
     *    <tr>
     *      <td>LOADING<br></td>
     *      <td>Set the status text to 'Loading'.<br></td>
     *    </tr>
     *    <tr>
     *      <td>LOADED<br></td>
     *      <td>Get the paramics network that has been been loaded from
     *          the model.   Set the status text to 'Network # Loaded'.
     *          Enable the connect button and disable the network
     *          load button<br></td>
     *    </tr>
     *  </tbody>
     *</table>
     *
     * @param status Paramics status value.
     */
    public void setParamicsStatus(PARAMICS_STATUS status) {
        
        switch(status) {
        
            case UNREACHABLE:           
                JOptionPane.showMessageDialog(this, 
                "Unable to connect to Paramics.", 
                "Communication Error", JOptionPane.ERROR_MESSAGE);
        
                connectToParamicsButton.setText("Connect to Paramics");
                paramicsStatusInfoLabel.setText("Unreachable");

                connectToParamicsButton.setEnabled(true);
                loadParamicsNetworkButton.setEnabled(false);
                //networkIDSpinner.setEnabled(false);       
                connectedToParamics = false;
                
            break;
            case DROPPED:
            
                JOptionPane.showMessageDialog(this, 
                "      Connection to Paramics has been dropped.\n" +
                "Restart the Paramics Communicator and reconnect.", 
                "Communication Error", JOptionPane.ERROR_MESSAGE);
        
                connectToParamicsButton.setText("Connect to Paramics");
                paramicsStatusInfoLabel.setText("Dropped");
                
                loadParamicsNetworkButton.setEnabled(false);
                //networkIDSpinner.setEnabled(false);       
                connectedToParamics = false;
            break;          
            case CONNECTING:
                paramicsStatusInfoLabel.setText("Connecting");
                
                connectToParamicsButton.setEnabled(false);
                loadParamicsNetworkButton.setEnabled(false);
                //networkIDSpinner.setEnabled(true);    
            break;
            case CONNECTED:
                connectToParamicsButton.setText("Disconnect from Paramics");                
                paramicsStatusInfoLabel.setText("Connected");
                
                connectToParamicsButton.setEnabled(true);
                loadParamicsNetworkButton.setEnabled(true);
                //networkIDSpinner.setEnabled(true);    
                connectedToParamics = true;         
                break;          
                
            case DISCONNECTED:
                connectToParamicsButton.setText("Connect to Paramics");                             
                paramicsStatusInfoLabel.setText("Disconnected");

                connectToParamicsButton.setEnabled(true);
                loadParamicsNetworkButton.setEnabled(false);
                //networkIDSpinner.setEnabled(false);       
                connectedToParamics = false;
                        
                break;  
            case SENDING_NETWORK_ID:
                paramicsStatusInfoLabel.setText("Sending Network ID");
                
                loadParamicsNetworkButton.setEnabled(false);
            break;      
            case WARMING:
                paramicsStatusInfoLabel.setText("Warming Up");
            break;
            case LOADING:
                paramicsStatusInfoLabel.setText("Loading Network");
            break;      
            case LOADED:            
                String network = "";
                try {                   
                    int networkLoaded = theSimManagerModel.getParamicsNetworkLoaded();
                    
                    if(networkLoaded != -1)
                        network = String.valueOf(networkLoaded);
                }
                catch (SimulationException se) {
                    SimulationExceptionHandler(se);
                }
                
                paramicsStatusInfoLabel.setText("Network " + network + " Loaded");

                connectToParamicsButton.setEnabled(true);
                loadParamicsNetworkButton.setEnabled(false);
                
            break;
        }   
    }

    /**
     * Method is used to convert a String containing a time in format H:MM:SS to a long
     * value of the number of seconds represented. 
     *
     * @param time String containing a time in format H:MM:SS
     * @throws StringIndexOutOfBoundsException if parameter is not in format H:MM:SS
     * @return long Number of seconds in parameter time  
     */
    public static long stringTimeToLong(String time) throws StringIndexOutOfBoundsException{
        long seconds = 0;   
        
        seconds =  ((long) Character.digit(time.charAt(0), 10) * 3600 +
                        Character.digit(time.charAt(2), 10) * 600 + 
                        Character.digit(time.charAt(3), 10) * 60 + 
                        Character.digit(time.charAt(5), 10) * 10 +
                        Character.digit(time.charAt(6), 10));

              
        return seconds;
    }
    
    /**
     * Converts a the long representation of the time, to the H:MM:SS String format
     *
     * @param seconds number of seconds.
     * @return String H:MM:SS time representation.
     */
    public static String longToTime(long seconds) {
        String time = new String();     
        long timeSegment;   
        
        timeSegment = seconds / 3600;
        time += String.valueOf(timeSegment) + ":";      
        
        seconds = seconds % 3600;
        
        timeSegment = seconds / 60;
        if(timeSegment < 10)
            time += "0";
        
        time += String.valueOf(timeSegment) + ":";      
        seconds = seconds % 60; 
        
        timeSegment = seconds;
        if(timeSegment < 10)
            time += "0";
        
        time += String.valueOf(timeSegment);
        
        return time;        
    }

    /**
     * This method is used to disply a message dialog with the received 
     * ScriptException's information.  The possible exceptions are found in the 
     * ScriptException class information.
     *
     * @param se The ScriptException to display
     * @see ScriptException
     */
    public void ScriptExceptionHandler(ScriptException se) {
        JOptionPane.showMessageDialog(this, se.getMessage(), "Script Error", JOptionPane.ERROR_MESSAGE);    
    }

/**
     * This method is used to disply a message dialog with the received 
     * SimulationException's information.  The possible exceptions are found in the 
     * SimulationException class information.
     *
     * @param se The SimulationException to display
     * @see SimulationException
     */
    public void SimulationExceptionHandler(SimulationException se) {
        JOptionPane.showMessageDialog(this, se.getMessage(), "Simulation Error", JOptionPane.ERROR_MESSAGE);    
    }
    

      
    /**
     * Initilize the GUI swing components.
     */  
    private void initComponents() {

        createMenuBar();
        createTimeAndStatus();
        createSimulationControl();
        createParamicsControl();
        createIncidentManagement();
        createCMSTrafficDiversion();
        createAdmin();
        createEventHistory();   

        addSimulationTab();     

        setMinimumSize(new Dimension(1024, 768));
        setMaximumSize(new Dimension(1600, 1200));
        setPreferredSize(new Dimension(1024, 768)); 
        
        pack();
    }
    
    /**
     * Create the menu bar components.
     */
    private void createMenuBar() {

        gotoMenuItem = new JMenuItem(new GotoTimeIndexAction(this));        
        exitMenuItem = new JMenuItem(new ExitAction(this));
        
        fileMenu = new JMenu("File");
        fileMenu.add(gotoMenuItem);
        fileMenu.add(exitMenuItem);
        
        simManagerMenuBar = new JMenuBar();
        simManagerMenuBar.add(fileMenu);
        
        setJMenuBar(simManagerMenuBar);
    }
    
    /**
     * Create the time and status boxes, buttons, and listeners.
     */
    private void createTimeAndStatus() {
        
        simulationTime       = new JPanel();
        simulationClock      = new JPanel();
        simulationStatus     = new JLabel("Simulation Status");
        simulationStatusText = new JLabel("No Script");
        simulationStatusText.setFont(new Font("Geneva", Font.BOLD, 14));
        simulationStatusText.setName("simulationStatusText");
        
        simulationTime.setLayout(new BorderLayout());       
        simulationClock.setPreferredSize(new Dimension(100, 60));
        
        startButton = new JButton(new StartSimulationAction(this));
        
        pauseButton  = new JButton(new PauseSimulationAction(this));
        resetButton = new JButton(new ResetSimulationAction(this));
        
        startButton.setEnabled(false);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(false);

        simulationStatusBox     = new Box(BoxLayout.Y_AXIS);        
        simulationTimeBox       = new Box(BoxLayout.Y_AXIS);
        simulationClockBox      = new Box(BoxLayout.X_AXIS);
        simulationTimeButtonBox = new Box(BoxLayout.X_AXIS);
        
        simulationTimeButtonBox.add(startButton);
        simulationTimeButtonBox.add(pauseButton);
        simulationTimeButtonBox.add(resetButton);
        
        simulationStatus.setAlignmentX(Box.CENTER_ALIGNMENT);
        simulationStatusText.setAlignmentX(Box.CENTER_ALIGNMENT);
        
        TitledBorder title = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Status");
        title.setTitleJustification(TitledBorder.LEFT);
        simulationStatusBox.setBorder(title);
        
        simulationStatusBox.setMaximumSize(new Dimension(140, 60));
        simulationStatusBox.setAlignmentX(Box.CENTER_ALIGNMENT);                    
        
        simulationStatusBox.add(Box.createVerticalGlue());
        simulationStatusBox.add(simulationStatusText);
        simulationStatusBox.add(Box.createVerticalGlue());
        
        scriptBox = new Box(BoxLayout.Y_AXIS);
        scriptBox.setAlignmentX(Box.CENTER_ALIGNMENT);
        
        loadScriptButton = new JButton(new LoadScriptAction(this));
        loadScriptButton.setAlignmentX(Box.CENTER_ALIGNMENT);
        
        scriptBox.add(simulationStatusBox);
        scriptBox.add(Box.createVerticalStrut(5));
        scriptBox.add(loadScriptButton);
        
        simulationClockLabel = new JLabel("0:00:00");
        simulationClockLabel.setFont(new Font("Geneva", Font.BOLD, 70));
        simulationClockLabel.setForeground(Color.BLACK);
        simulationClockLabel.setBackground(Color.BLACK);
        simulationClockBox.setForeground(Color.BLACK);
        simulationClockBox.setBackground(Color.BLACK);
        simulationClockBox.add(simulationClockLabel);
        simulationClockBox.setAlignmentX(Box.CENTER_ALIGNMENT); 
        simulationTimeButtonBox.setAlignmentX(Box.CENTER_ALIGNMENT);
        simulationTimeBox.add(simulationClockBox);
        simulationTimeBox.add(simulationTimeButtonBox);

        simulationTimeAndStatusBox = Box.createHorizontalBox();
        simulationTimeAndStatusBox.setAlignmentX(Box.LEFT_ALIGNMENT);
        simulationTimeAndStatusBox.setMaximumSize(new Dimension(640, 200));
        simulationTimeAndStatusBox.setPreferredSize(new Dimension(640, 160));
        simulationTimeAndStatusBox.add(Box.createHorizontalStrut(5));
        simulationTimeAndStatusBox.add(simulationTimeBox);
        simulationTimeAndStatusBox.add(Box.createHorizontalStrut(20));
        simulationTimeAndStatusBox.add(scriptBox);
        simulationTimeAndStatusBox.add(Box.createHorizontalStrut(5));
        
        //*** Clock for CAD Tab ***//
        CADClientClockLabel = new JLabel("0:00:00");
        CADClientClockLabel.setFont(new Font("Geneva", Font.BOLD, 60));
        CADClientClockLabel.setForeground(Color.BLACK);
        CADClientClockLabel.setBackground(Color.BLACK);
        CADClientClockLabel.setAlignmentX(Box.CENTER_ALIGNMENT);        
        
    }
    
    
    /**
     * Create Simulation Control Box
     */
    private void createSimulationControl() {
        
        simulationControlBox = new Box(BoxLayout.Y_AXIS);
        simulationControlBox.add(simulationTimeAndStatusBox);
                
        CompoundBorder cBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createRaisedBevelBorder(), "Simulation Control"),
                BorderFactory.createEmptyBorder(5,5,5,5));

        
        simulationControlBox.setBorder(cBorder);    
    }
    
    /**
     * Create ParamicsControl Box
     */
    private void createParamicsControl() {
        
        paramicsControlBox = new Box(BoxLayout.Y_AXIS);
        paramicsControlBox.setAlignmentX(Box.LEFT_ALIGNMENT);
        paramicsControlBox.setMaximumSize(new Dimension(650, 150));     
        paramicsControlBox.setMinimumSize(new Dimension(250, 95));  
        paramicsControlBox.setPreferredSize(new Dimension(380, 95));
        
        paramicsStatusBox       = new Box(BoxLayout.X_AXIS);        
        paramicsStatusLabel     = new JLabel("Status:");
        paramicsStatusInfoLabel = new JLabel("Unknown");
        paramicsStatusInfoLabel.setName("paramicsStatusInfoLabel");
        paramicsStatusBox.add(paramicsStatusLabel);
        paramicsStatusBox.add(Box.createHorizontalStrut(10));
        paramicsStatusBox.add(paramicsStatusInfoLabel);
        paramicsStatusBox.add(Box.createHorizontalGlue());
        
        networkIDSpinner = new JSpinner(
            new SpinnerNumberModel(DEFAULT_NETWORK_ID, 1, MAX_NETWORK_ID, 1));
        networkIDSpinner.setEnabled(false);     
        networkIDSpinner.setMaximumSize(new Dimension(60, 30));
        
        connectToParamicsButton = new JButton(new ConnectToParamicsAction(this));
        connectToParamicsButton.setName("connectToParamicsButton");

        loadParamicsNetworkButton = new JButton(new LoadParamicsNetworkAction(this));
        loadParamicsNetworkButton.setName("loadParamicsNetworkButton");
        loadParamicsNetworkButton.setEnabled(false);
        loadParamicsNetworkButton.getAction().putValue(
                LoadParamicsNetworkAction.NETWORK_ID_SPINNER, networkIDSpinner);
        

        paramicsButtonBox = new Box(BoxLayout.X_AXIS);
        paramicsButtonBox.add(Box.createHorizontalGlue());
        paramicsButtonBox.add(connectToParamicsButton);
        paramicsButtonBox.add(Box.createHorizontalGlue());
        paramicsButtonBox.add(loadParamicsNetworkButton);
        paramicsButtonBox.add(Box.createHorizontalStrut(10));
        paramicsButtonBox.add(networkIDSpinner);
        paramicsButtonBox.add(Box.createHorizontalGlue());

        paramicsControlBox.add(Box.createVerticalStrut(5));
        paramicsControlBox.add(paramicsStatusBox);
        paramicsControlBox.add(Box.createVerticalStrut(5));
        paramicsControlBox.add(paramicsButtonBox);  
        paramicsControlBox.add(Box.createVerticalStrut(5));
                
        CompoundBorder cBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createRaisedBevelBorder(), "Paramics Control"),
                BorderFactory.createEmptyBorder(5,5,5,5));

        
        paramicsControlBox.setBorder(cBorder);      
    
    }   
    
    /**
     * Create the Incident Management portion of the Simulation 
     * Management Box
     */        
    private void createIncidentManagement() {
    
        createIncidentListTable();
        
        incidentTimeModel = new IncidentTimeSpinnerModel();
        reschedSpinner = new JSpinner(incidentTimeModel);        
        reschedSpinner.setEnabled(false);
        reschedSpinner.setMaximumSize(new Dimension(70,20));
                
        
        triggerButton     = new JButton(new TriggerIncidentAction(this));
        triggerButton.getAction().putValue(
                TriggerIncidentAction.INCIDENT_LIST_TABLE, incidentListTable);
        
        deleteIncButton   = new JButton(new DeleteIncidentAction(this));
        deleteIncButton.getAction().putValue(
                DeleteIncidentAction.INCIDENT_LIST_TABLE, incidentListTable);
    
        reschedButton     = new JButton(new RescheduleIncidentAction(this));
        reschedButton.getAction().putValue(
                RescheduleIncidentAction.INCIDENT_LIST_TABLE, incidentListTable);
        reschedButton.getAction().putValue(
                RescheduleIncidentAction.INCIDENT_TIME_MODEL, incidentTimeModel);
        
        addIncidentButton = new JButton(new AddIncidentAction(this));
        
        timeScheduledLabel  = new JLabel("Time Scheduled: ");   
                    
        triggerButton.setEnabled(false);
        deleteIncButton.setEnabled(false);
        reschedButton.setEnabled(false);

        Box temp = new Box(BoxLayout.X_AXIS);
        temp.setAlignmentX(Box.CENTER_ALIGNMENT);       
        temp.add(timeScheduledLabel);
        temp.add(Box.createHorizontalStrut(10));
        temp.add(reschedSpinner);
        temp.add(Box.createHorizontalStrut(20));
        temp.add(reschedButton);        

        incidentManagementReSchedBox = new Box(BoxLayout.Y_AXIS);
        incidentManagementReSchedBox.setAlignmentX(Box.CENTER_ALIGNMENT);       
        incidentManagementReSchedBox.add(temp);     
                
        Box temp2 = new Box(BoxLayout.X_AXIS);
        temp2.setAlignmentX(Box.CENTER_ALIGNMENT);
        temp2.add(triggerButton);
        temp2.add(Box.createHorizontalStrut(20));
        temp2.add(deleteIncButton);
        temp2.add(Box.createHorizontalStrut(20));
        temp2.add(addIncidentButton);                   

        incidentManagementButtonsBox = new Box(BoxLayout.Y_AXIS);
        incidentManagementButtonsBox.setAlignmentY(Box.CENTER_ALIGNMENT);
        incidentManagementButtonsBox.add(temp2);
        
        incidentManagementActionBox = new Box(BoxLayout.Y_AXIS);
        incidentManagementActionBox.setMaximumSize(new Dimension(500, 100));
        incidentManagementActionBox.setAlignmentX(Box.LEFT_ALIGNMENT);
        incidentManagementActionBox.add(incidentManagementReSchedBox);      
        incidentManagementActionBox.add(Box.createVerticalStrut(10));       
        incidentManagementActionBox.add(incidentManagementButtonsBox);      

        incidentManagementBox = new Box(BoxLayout.Y_AXIS);
        incidentManagementBox.setAlignmentX(Box.LEFT_ALIGNMENT);
        incidentManagementBox.setMaximumSize(new Dimension(650, 500));      
        incidentManagementBox.setMinimumSize(new Dimension(250, 240));  
        incidentManagementBox.setPreferredSize(new Dimension(380, 240));
        incidentManagementBox.add(Box.createVerticalStrut(5));
        incidentManagementBox.add(incidentListPane);        
        incidentManagementBox.add(Box.createVerticalStrut(10));     
        incidentManagementBox.add(incidentManagementActionBox);         
        incidentManagementBox.add(Box.createVerticalStrut(5));
        
        CompoundBorder cBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createRaisedBevelBorder(), "Incident Management"),
                BorderFactory.createEmptyBorder(5,5,5,5));

        
        incidentManagementBox.setBorder(cBorder);       
    
    }
    
    private void createIncidentListTable() {
    
        incidentListTableModel = new IncidentListTableModel();    
        incidentListTableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent arg0) {

                boolean incidentToTrigger = false;
                
                for(int i = 0; i < incidentListTableModel.getRowCount(); i++) {
                    
                    incidentToTrigger |= (((Long)incidentListTableModel.
                            getValueAt(i, INCIDENT_LIST_COLUMNS.SCHEDULED_COL.colNum)) != -1); 
                }                   

                triggerButton.setEnabled(incidentToTrigger);
                deleteIncButton.setEnabled(incidentToTrigger);
                reschedButton.setEnabled(incidentToTrigger);
                reschedSpinner.setEnabled(incidentToTrigger);           
                
            }
        });
            
        incidentListTable = new JTable(incidentListTableModel);     
        incidentListTable.getTableHeader().setReorderingAllowed(false);  
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
            
            if(c == INCIDENT_LIST_COLUMNS.SCHEDULED_COL.colNum)
                incidentListTable.getColumnModel().getColumn(c).setCellRenderer(
                        new IncidentTimeCellRenderer());
                
        }

        incidentListPane = new JScrollPane();
        incidentListPane.setAlignmentX(Box.LEFT_ALIGNMENT);
        incidentListPane.setMaximumSize(new Dimension(650, 400));       
        incidentListPane.setMinimumSize(new Dimension(200, 100));   
        incidentListPane.setPreferredSize(new Dimension(380, 100));
        incidentListPane.setViewportView(incidentListTable);
        
        incidentListTable.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}           
            public void mouseExited(MouseEvent e) {}            
            public void mousePressed(MouseEvent e) {}           
            public void mouseReleased(MouseEvent e)  {
                long incTime = (Long)incidentListTable.getValueAt(incidentListTable.getSelectedRow(), 
                        INCIDENT_LIST_COLUMNS.SCHEDULED_COL.colNum);
                if(incTime != -1) {
                    incidentTimeModel.setValue(incTime);
                    triggerButton.setEnabled(true);
                    deleteIncButton.setEnabled(true);
                    reschedButton.setEnabled(true);
                    reschedSpinner.setEnabled(true);
                }
                else {
                    triggerButton.setEnabled(false);
                    deleteIncButton.setEnabled(false);
                    reschedButton.setEnabled(false);
                    reschedSpinner.setEnabled(false);       
                }
                
            }           
        });
    }

    /**
     * Create the cms traffic diversion box.
     */    
    private void createCMSTrafficDiversion() {
    
        cmsTrafficDiversionBox = new Box(BoxLayout.Y_AXIS);
        cmsTrafficDiversionBox.setAlignmentX(Box.LEFT_ALIGNMENT);
        cmsTrafficDiversionBox.setMaximumSize(new Dimension(650, 350));     
        cmsTrafficDiversionBox.setMinimumSize(new Dimension(200, 145)); 
        cmsTrafficDiversionBox.setPreferredSize(new Dimension(380, 250));   
        
        CompoundBorder cBorder = BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), "CMS Diversion Management"),
                                                                    BorderFactory.createEmptyBorder(5,5,5,5));

        cmsTrafficDiversionBox.setBorder(cBorder);                  
                        
        appliedDiversionTableModel = new AppliedDiversionTableModel();
        appliedDiversionTable = new JTable(appliedDiversionTableModel);
        appliedDiversionTable.getTableHeader().setReorderingAllowed(false);
        appliedDiversionTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        appliedDiversionTable.setCellSelectionEnabled(false);
        appliedDiversionTable.setRowSelectionAllowed(true);
        appliedDiversionTable.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent evt)  {
                if(evt.getClickCount() >= 2) {
                    ((ApplyDiversionAction)divertButton.getAction()).showDiversionDialog(
                            (String)appliedDiversionTableModel.getValueAt(
                                    appliedDiversionTable.getSelectedRow(),
                                    APPLIED_DIV_COLUMNS.CMS_ID_COL.colNum));
                }
            };
            public void mouseEntered(MouseEvent arg0)  {};
            public void mouseExited(MouseEvent arg0)   {};
            public void mousePressed(MouseEvent arg0)  {};
            public void mouseReleased(MouseEvent arg0) {};          
        });
        
        for(int c = 0; c < appliedDiversionTable.getColumnCount(); c++) {
            appliedDiversionTable.getColumnModel().getColumn(c).setMinWidth(
                    appliedDiversionTableModel.getColumnMinWidth(c));
            appliedDiversionTable.getColumnModel().getColumn(c).setMaxWidth(
                    appliedDiversionTableModel.getColumnMaxWidth(c));
            appliedDiversionTable.getColumnModel().getColumn(c).setPreferredWidth(
                    appliedDiversionTableModel.getColumnPrefWidth(c));
            appliedDiversionTable.getColumnModel().getColumn(c).setResizable(false);
            
            if(c == APPLIED_DIV_COLUMNS.APPLIED_COL.colNum) {
                appliedDiversionTable.getColumnModel().getColumn(c).setCellRenderer(
                        new IncidentTimeCellRenderer());
            }       
        }       

        appliedDiversionPane = new JScrollPane();
        appliedDiversionPane.setAlignmentX(Box.CENTER_ALIGNMENT);       
        appliedDiversionPane.setMaximumSize(new Dimension(650, 800));       
        appliedDiversionPane.setMinimumSize(new Dimension(200, 75));    
        appliedDiversionPane.setPreferredSize(new Dimension(380,100));              
        appliedDiversionPane.setViewportView(appliedDiversionTable);    
        
        cmsIDComboBox = new JComboBox();
        cmsIDComboBox.setMaximumSize(new Dimension(40, 25));
        
        divertButton = new JButton(new ApplyDiversionAction(this));
        divertButton.getAction().putValue(
                ApplyDiversionAction.CMS_ID_COMBO_BOX, cmsIDComboBox);
                
        appliedDiversionButtonBox = new Box(BoxLayout.X_AXIS);
        appliedDiversionButtonBox.setAlignmentX(Box.CENTER_ALIGNMENT);
        appliedDiversionButtonBox.setMaximumSize(new Dimension(1010, 40));
        appliedDiversionButtonBox.add(Box.createGlue());
        appliedDiversionButtonBox.add(new JLabel("CMS ID:  "));
        appliedDiversionButtonBox.add(cmsIDComboBox);
        appliedDiversionButtonBox.add(Box.createHorizontalStrut(15));
        appliedDiversionButtonBox.add(divertButton);    
        appliedDiversionButtonBox.add(Box.createGlue());

        cmsTrafficDiversionBox.add(Box.createVerticalStrut(5));
        cmsTrafficDiversionBox.add(appliedDiversionPane);       
        cmsTrafficDiversionBox.add(Box.createVerticalStrut(5));
        cmsTrafficDiversionBox.add(appliedDiversionButtonBox);      
        cmsTrafficDiversionBox.add(Box.createVerticalStrut(5));             
    
    }
        
    /**
     * Create the main admin box.
     */            
    private void createAdmin() {        
        
        adminInteractionBox = new Box(BoxLayout.Y_AXIS);
        adminInteractionBox.setAlignmentX(Box.LEFT_ALIGNMENT);  
        adminInteractionBox.setMinimumSize(new Dimension(425, 740));    
        adminInteractionBox.setPreferredSize(new Dimension(425, 740));  
        adminInteractionBox.setMaximumSize(new Dimension(625, 1975));       
        
        adminInteractionBox.add(Box.createVerticalStrut(10));       
        adminInteractionBox.add(simulationControlBox);
        adminInteractionBox.add(Box.createVerticalStrut(10));   
        adminInteractionBox.add(paramicsControlBox);
        adminInteractionBox.add(Box.createVerticalStrut(10));   
        adminInteractionBox.add(incidentManagementBox);
        adminInteractionBox.add(Box.createVerticalStrut(10));   
        adminInteractionBox.add(cmsTrafficDiversionBox);        
        adminInteractionBox.add(Box.createVerticalStrut(10));   
        
    }

    /**
     * Create the event history box.
     */    
    private void createEventHistory() {
    
        eventHistoryPane = new JTabbedPane();
        
        eventHistoryBox = new Box(BoxLayout.Y_AXIS);
        eventHistoryBox.setAlignmentX(Box.CENTER_ALIGNMENT);
        eventHistoryBox.setMinimumSize(new Dimension(525, 700));    
        eventHistoryBox.setPreferredSize(new Dimension(525, 700));
        eventHistoryBox.setMaximumSize(new Dimension(925, 1975));   
        
        eventHistoryBox.add(eventHistoryPane);
        eventHistoryBox.add(Box.createVerticalStrut(10));   
        
        CompoundBorder cBorder = BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), "Event History"),
                                                                    BorderFactory.createEmptyBorder(5,5,5,5));
        
        eventHistoryBox.setBorder(cBorder);     
        
    }
    

    /**
     * Create the administrator tab
     */            
    private void addSimulationTab() {
        
        simulationRightSideBox = new Box(BoxLayout.Y_AXIS);             
        simulationRightSideBox.add(Box.createVerticalStrut(10));
        simulationRightSideBox.add(eventHistoryBox);
        simulationRightSideBox.add(Box.createVerticalStrut(10));    
                
        simulationBox = new Box(BoxLayout.X_AXIS);      
        simulationBox.add(Box.createHorizontalStrut(20));
        simulationBox.add(adminInteractionBox);
        simulationBox.add(Box.createHorizontalStrut(20));   
        simulationBox.add(simulationRightSideBox);
        simulationBox.add(Box.createHorizontalStrut(20));
                
        getContentPane().add(simulationBox);
    }   
    
    private JTabbedPane eventHistoryPane;
    
    private JScrollPane incidentListPane; 
    private JScrollPane appliedDiversionPane;

    private JTable incidentListTable;
    private JTable appliedDiversionTable;
    
    private JPanel simulationTime;
    private JPanel simulationClock;
            
    private JLabel paramicsStatusLabel;
    private JLabel paramicsStatusInfoLabel;
    private JLabel simulationStatus;
    private JLabel simulationClockLabel;
    private JLabel simulationStatusText;
    private JLabel timeScheduledLabel;
    private JLabel CADClientClockLabel;
            
    private Box adminInteractionBox;    
    private Box simulationRightSideBox;
    private Box simulationBox;          
    private Box appliedDiversionButtonBox;  
    private Box cmsTrafficDiversionBox;     
    private Box eventHistoryBox;    
    private Box incidentManagementActionBox;
    private Box incidentManagementBox;  
    private Box incidentManagementButtonsBox;
    private Box incidentManagementReSchedBox;
    private Box scriptBox;      
    private Box paramicsButtonBox;
    private Box paramicsControlBox; 
    private Box paramicsStatusBox;
    private Box simulationTimeAndStatusBox;
    private Box simulationStatusBox;        
    private Box simulationTimeBox;
    private Box simulationClockBox;
    private Box simulationControlBox;
    private Box simulationTimeButtonBox;

    private IncidentTimeSpinnerModel incidentTimeModel;
    private JSpinner reschedSpinner;
    private JSpinner networkIDSpinner;
    
    private JButton addIncidentButton;          
    private JButton deleteIncButton;
    private JButton loadScriptButton;
    private JButton divertButton;
    private JButton reschedButton;
    private JButton resetButton;
    private JButton startButton;
    private JButton pauseButton;
    private JButton triggerButton;  
    private JButton connectToParamicsButton;
    private JButton loadParamicsNetworkButton;
    
    private JComboBox cmsIDComboBox;                    
        
    private JMenuBar simManagerMenuBar;
    private JMenu fileMenu;
    private JMenuItem gotoMenuItem;
    private JMenuItem exitMenuItem;
    
}

