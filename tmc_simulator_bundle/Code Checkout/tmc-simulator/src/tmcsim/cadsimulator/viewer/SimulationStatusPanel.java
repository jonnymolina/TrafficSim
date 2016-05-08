package tmcsim.cadsimulator.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import tmcsim.cadsimulator.viewer.model.CADSimulatorModel;
import tmcsim.cadsimulator.viewer.model.SimulationStatusPanelModel;

import tmcsim.common.CADEnums.PARAMICS_STATUS;
import tmcsim.common.CADEnums.SCRIPT_STATUS;

/**
 * SimulationStatusPanel is a GUI object used for displaying information for the current
 * simulation. This information includes:
 *
 * <ul>
 * <li>Current simulation time.</li>
 * <li>Current simulation status.</li>
 * <li>Number of remote CAD Clients connected.</li>
 * <li>Status of Simulation Manager connection.</li>
 * <li>Status of Paramics connection.</li>
 * <li>Paramics Network Loaded</li>
 * <li>Information log messages.</li>
 * <li>Error log messages</li>
 * </ul>
 *
 * @author Jonathan Molina
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class SimulationStatusPanel extends JPanel
{
    /**
     * Logging Handler to listen for Information and Error messages logged for the CAD
     * Simulator. Received LogRecords are displayed in the info or error message Text
     * Area.
     *
     * @author Matthew Cechini
     */
    private class SimulatorErrorHandler extends Handler
    {
        public void close() throws SecurityException
        {
        }

        public void flush()
        {
        }

        public void publish(LogRecord rec)
        {
            StringBuffer msgBuffer = new StringBuffer();

            msgBuffer.append(rec.getSourceClassName() + "."
                    + rec.getSourceMethodName() + " = "
                    + rec.getMessage());

            if (rec.getLevel() == Level.INFO)
            {
                infoMessagesTA.setText(infoMessagesTA.getText()
                        + msgBuffer.toString() + "\n");
            }
            else
            {
                errorMessagesTA.setText(errorMessagesTA.getText()
                        + msgBuffer.toString() + "\n");
            }
        }
    }

    /**
     * Constructor. Initialize GUI Objects. Register the logging handler to listen for
     * log records from all loggers that exist in the "tmcsim.cadsimulator" package
     * structure.
     */
    public SimulationStatusPanel()
    {
        initTimeAndStatus();
        initAdditionalInfo();
        initMessagesPanes();

        errorHandler = new SimulatorErrorHandler();
        Logger.getLogger("tmcsim.cadsimulator").addHandler(errorHandler);

        // Project 2.2 added code (start)
        simulationStatusText.setName("simulationStatusText");
        termConnectedTF.setName("termConnectedTF");
        managerConnectedTF.setName("managerConnectedTF");
        infoMessagesTA.setName("infoMessagesTA");
        infoMessagesPane.setName("infoMessagesPane");

        paramicsConnectedTF.setName("paramicsConnectedTF");
        networkLoadedTF.setName("networkLoadedTF");
        errorMessagesPane.setName("errorMessagesPane");
        errorMessagesTA.setName("errorMessagesTA");
        simulationClockLabel.setName("simulationClockLabel");
        // Project 2.2 added code (end)


        CADSimulatorViewerBox = Box.createVerticalBox();
        CADSimulatorViewerBox.add(simulationTimeAndStatusBox);
        CADSimulatorViewerBox.add(additionalInfoBox);
        CADSimulatorViewerBox.add(infoMessagesPane);
        CADSimulatorViewerBox.add(errorMessagesPane);

        add(CADSimulatorViewerBox);
    }

    /**
     * Method is called when Simulation Manager connects or disconnects.
     *
     * @param connection True if simulation manager is connected, false if not.
     */
    private void setSimManagerStatus(boolean connection)
    {
        if (connection)
        {
            managerConnectedTF.setText("Yes");
        }
        else
        {
            managerConnectedTF.setText("No");
        }

    }

    /**
     * Method called to convert current simulation time (parameter long value) to a
     * string of format H:MM:SS. Time is then updated on GUI.
     *
     * @param seconds Long value of current time
     */
    private void setTime(long seconds)
    {
        String time = new String();
        long timeSegment;

        timeSegment = seconds / 3600;
        time += String.valueOf(timeSegment) + ":";

        seconds = seconds % 3600;

        timeSegment = seconds / 60;
        // Format time
        if (timeSegment < 10)
        {
            time += "0";
        }

        time += String.valueOf(timeSegment) + ":";
        seconds = seconds % 60;

        timeSegment = seconds;
        // Format time
        if (timeSegment < 10)
        {
            time += "0";
        }

        time += String.valueOf(timeSegment);

        simulationClockLabel.setText(time);

    }

    /**
     * This method is called within the CADSimulator whenever an error occurs. The
     * message is then displayed to the user in the "Error Messages" portion of the CAD
     * Simulator Viewer. Invoke method with null parameter to clear messages.
     *
     * @param errorMessage String message that will be displayed
     */
    protected void displayError(String errorMessage)
    {
        if (errorMessage == null)
        {
            errorMessagesTA.setText("");
        }
        else
        {
            errorMessagesTA.append(errorMessage + "\n");
        }
    }

    /**
     * Method is called to display the current status of the simulation.
     *
     * @param newStatus Current status of simulation. The following table describes each
     * possible status and what is displayed. Each status code is found as a public
     * static int in the Coordinator Class.
     *
     * <table cellpadding="2" cellspacing="2" border="1" style="text-align: left; width:
     * 250px;">
     * <tbody>
     * <tr>
     * <th>Status<br></th>
     * <th>Actions Taken<br></th>
     * </tr>
     * <tr>
     * <td>NO_SCRIPT<br></td>
     * <td>Set the simulation status text to a black "No Script". <br></td>
     * </tr>
     * <tr>
     * <td>SCRIPT_STOPPED_NOT_STARTED<br></td>
     * <td>Set the simulation status text to a red "Ready". <br></td>
     * </tr>
     * <tr>
     * <td>SCRIPT_PAUSED_STARTED<br></td>
     * <td>Set the simulation status text to a red "Paused". <br></td>
     * </tr>
     * <tr>
     * <td>SCRIPT_RUNNING<br></td>
     * <td>Set the simulation status text to a green "Running". <br></td>
     * </tr>
     * <tr>
     * <td>ATMS_SYNCHRONIZATION<br></td>
     * <td>Set the simulation status text to an orange "Synchronizing". <br></td>
     * </tr>
     * </tbody>
     * </table>
     */
    private void setScriptStatus(SCRIPT_STATUS newStatus)
    {
        // set status depending on script status
        switch (newStatus)
        {
            case NO_SCRIPT:
                simulationStatusText.setText("No Script");
                simulationStatusText.setForeground(Color.BLACK);
                break;          
                
            case SCRIPT_STOPPED_NOT_STARTED:
                simulationStatusText.setText("Ready");
                simulationStatusText.setForeground(Color.RED);          
                break;          
                
            case SCRIPT_PAUSED_STARTED:
                simulationStatusText.setText("Paused");
                simulationStatusText.setForeground(Color.RED);
                break;
                
            case SCRIPT_RUNNING:
                simulationStatusText.setText("Running");
                simulationStatusText.setForeground(Color.GREEN);
                break;
            case ATMS_SYNCHRONIZATION:
                simulationStatusText.setText("Synchronizing");
                simulationStatusText.setForeground(Color.ORANGE);
                break;
            default:
                break;
        }
    }

    /**
     * Method is called when a connection to paramics is made or dropped.
     *
     * @param newStatus The status denoting whether a connection has been made or
     * dropped.
     */
    private void setParamicsStatus(PARAMICS_STATUS newStatus)
    {

        switch (newStatus)
        {
            case CONNECTED:
                paramicsConnectedTF.setText("Yes");
                break;
            case DISCONNECTED:
                paramicsConnectedTF.setText("No");
                break;
        }
    }

    /**
     * Method is called when a paramics network is loaded.
     *
     * @param networkID Unique ID for Paramics network that has been loaded.
     */
    private void setParamicsNetworkLoaded(String networkID)
    {
        // Set text to None for no network
        networkLoadedTF.setText(networkID);
    }

    /**
     * Sets the label for the number of connected CAD terminals.
     *
     * @param connectedClients number of connected CAD clients
     */
    private void setTerminalsConnected(int connectedClients)
    {
        termConnectedTF.setText(String.valueOf(connectedClients));
    }

    /**
     * Initialize Time and Status GUI Components
     */
    private void initTimeAndStatus()
    {

        simulationTime = new JPanel();
        simulationClock = new JPanel();
        simulationStatus = new JLabel("Simulation Status");
        simulationStatusText = new JLabel("No Script");

        simulationTime.setLayout(new BorderLayout());
        simulationClock.setPreferredSize(new Dimension(100, 60));
        simulationTimeAndStatusBox = new Box(BoxLayout.X_AXIS);
        simulationStatusBox = new Box(BoxLayout.Y_AXIS);
        simulationTimeBox = new Box(BoxLayout.Y_AXIS);
        simulationClockBox = new Box(BoxLayout.X_AXIS);

        simulationStatus.setAlignmentX(Box.CENTER_ALIGNMENT);
        simulationStatusText.setAlignmentX(Box.CENTER_ALIGNMENT);

        TitledBorder title = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Status");
        title.setTitleJustification(TitledBorder.LEFT);
        simulationStatusBox.setBorder(title);

        simulationStatusBox.setMaximumSize(new Dimension(140, 60));
        simulationStatusBox.setAlignmentX(Box.CENTER_ALIGNMENT);

        simulationStatusBox.add(Box.createHorizontalStrut(120));
        simulationStatusBox.add(Box.createVerticalGlue());
        simulationStatusBox.add(simulationStatusText);
        simulationStatusBox.add(Box.createVerticalGlue());

        simulationClockLabel = new JLabel("0:00:00");
        simulationClockLabel.setFont(new Font("Geneva", Font.BOLD, 70));
        simulationClockLabel.setForeground(Color.BLACK);
        simulationClockLabel.setBackground(Color.BLACK);
        simulationClockBox.setForeground(Color.BLACK);
        simulationClockBox.setBackground(Color.BLACK);
        simulationClockBox.add(simulationClockLabel);
        simulationClockBox.setAlignmentX(Box.CENTER_ALIGNMENT);
        simulationTimeBox.add(simulationClockBox);

        simulationTimeAndStatusBox.add(Box.createHorizontalStrut(20));
        simulationTimeAndStatusBox.add(simulationTimeBox);
        simulationTimeAndStatusBox.add(Box.createHorizontalStrut(20));
        simulationTimeAndStatusBox.add(simulationStatusBox);
        simulationTimeAndStatusBox.add(Box.createHorizontalStrut(20));
    }

    /**
     * Initialize Additional Info Label GUI Components
     */
    private void initAdditionalInfo()
    {

        terminalsConnectedLabel = new JLabel("Connected CAD Terminals: ");
        termConnectedTF = new JTextField("   " + String.valueOf(numClientsConnected));
        termConnectedTF.setEditable(false);

        termConnectedBox = new Box(BoxLayout.X_AXIS);
        termConnectedBox.add(terminalsConnectedLabel);
        termConnectedBox.add(Box.createHorizontalGlue());
        termConnectedBox.add(termConnectedTF);


        managerConnectedLabel = new JLabel("Simulation Manager Connected: ");
        managerConnectedTF = new JTextField("  No");
        managerConnectedTF.setEditable(false);

        managerConnectedBox = new Box(BoxLayout.X_AXIS);
        managerConnectedBox.add(managerConnectedLabel);
        managerConnectedBox.add(Box.createHorizontalGlue());
        managerConnectedBox.add(managerConnectedTF);


        paramicsConnectedLabel = new JLabel("Connected to Paramics: ");
        paramicsConnectedTF = new JTextField("  No");
        paramicsConnectedTF.setEditable(false);

        paramicsConnectedBox = new Box(BoxLayout.X_AXIS);
        paramicsConnectedBox.add(paramicsConnectedLabel);
        paramicsConnectedBox.add(Box.createHorizontalGlue());
        paramicsConnectedBox.add(paramicsConnectedTF);


        networkLoadedLabel = new JLabel("Network Loaded: ");
        networkLoadedTF = new JTextField("");
        networkLoadedTF.setEditable(false);

        networkLoadedBox = new Box(BoxLayout.X_AXIS);
        networkLoadedBox.add(networkLoadedLabel);
        networkLoadedBox.add(Box.createHorizontalGlue());
        networkLoadedBox.add(networkLoadedTF);


        additionalInfoBox = new Box(BoxLayout.Y_AXIS);
        additionalInfoBox.setMinimumSize(new Dimension(300, 150));

        additionalInfoBox.add(Box.createVerticalStrut(10));
        additionalInfoBox.add(termConnectedBox);
        additionalInfoBox.add(Box.createVerticalStrut(10));
        additionalInfoBox.add(managerConnectedBox);
        additionalInfoBox.add(Box.createVerticalStrut(10));
        additionalInfoBox.add(paramicsConnectedBox);
        additionalInfoBox.add(Box.createVerticalStrut(10));
        additionalInfoBox.add(networkLoadedBox);
        additionalInfoBox.add(Box.createVerticalStrut(20));


    }

    /**
     * Initialize Info & Error Messages GUI Components
     */
    private void initMessagesPanes()
    {

        infoMessagesTA = new JTextArea(6, 30);
        infoMessagesTA.setEditable(true);
        infoMessagesPane = new JScrollPane(infoMessagesTA);
        infoMessagesPane.setPreferredSize(new Dimension(300, 100));

        infoMessagesPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                "Info Messages"));


        errorMessagesTA = new JTextArea(6, 30);
        errorMessagesTA.setForeground(Color.RED);
        errorMessagesTA.setEditable(false);
        errorMessagesPane = new JScrollPane(errorMessagesTA);
        errorMessagesPane.setPreferredSize(new Dimension(300, 150));

        errorMessagesPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                "Error Messages"));

    }
    
    /**
     * Updates all fields in this class from the CADSimulatorModel.
     * @param simModel models the CADSimulator
     */
    public void refresh(SimulationStatusPanelModel simModel)
    {
        setTerminalsConnected(simModel.getNumClients());
        setSimManagerStatus(simModel.isSimManagerConnected());
        setTime(simModel.getTimeSegment());
        setParamicsStatus(simModel.getParamicsStatus());
        setScriptStatus(simModel.getScriptStatus());
        setParamicsNetworkLoaded(simModel.getNetworkLoaded());
    }
    
    /**
     * Count of how many CAD clients have connected.
     */
    private int numClientsConnected = 0;
    /**
     * Logging ErrorHandler.
     */
    private SimulatorErrorHandler errorHandler;
    private Box additionalInfoBox;
    private Box termConnectedBox;
    private Box managerConnectedBox;
    private Box paramicsConnectedBox;
    private Box networkLoadedBox;
    private Box CADSimulatorViewerBox;
    private Box simulationTimeAndStatusBox;
    private Box simulationStatusBox;
    private Box simulationTimeBox;
    private Box simulationClockBox;
    private JLabel managerConnectedLabel;
    private JLabel paramicsConnectedLabel;
    private JLabel simulationStatus;
    private JLabel simulationClockLabel;
    private JLabel simulationStatusText;
    private JLabel terminalsConnectedLabel;
    private JLabel networkLoadedLabel;
    private JPanel simulationTime;
    private JPanel simulationClock;
    private JTextField managerConnectedTF;
    private JTextField paramicsConnectedTF;
    private JTextField termConnectedTF;
    private JTextField networkLoadedTF;
    private JScrollPane infoMessagesPane;
    private JScrollPane errorMessagesPane;
    private JTextArea infoMessagesTA;
    private JTextArea errorMessagesTA;
}
