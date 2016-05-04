package tmcsim.client;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.UIManager;

import tmcsim.client.cadclientgui.CADClientGUI;
import tmcsim.client.cadclientgui.CardfileReader;
import tmcsim.client.cadclientgui.GUIScriptReader;
import tmcsim.client.cadclientgui.data.CADData;
import tmcsim.client.cadclientgui.screens.Login;
import tmcsim.client.cadclientgui.screens.ScreenManager;
import tmcsim.common.CADEnums;
import tmcsim.common.SimulationException;
import tmcsim.interfaces.CADClientInterface;
import tmcsim.interfaces.CoordinatorInterface;

/**
 * CADClient is the main class for the CAD Client application. The main method
 * instantiates an instance of the CADClient object with the default properties
 * file "..\config\CADClient.properties" or the first argument fom the command
 * line invocation. Properties data values are used to bind socket communication
 * between the CAD Client and the CAD Simulator. The CADClientModel object is
 * instantiated and the CAD Client registers itself with the CAD Simulator.
 * Finally, the CADClientView is initialized, the model-view and observer
 * relationships are established, and the view is shown.<br>
 * <br>
 * The properties file contains the following data: <br>
 * <code>
 * -----------------------------------------------------------------------------<br>
 * Host Name     The host name where the CAD Simulator is located.<br>
 * Port Number   The port number that the CAD Simulator is bound on.<br>
 * CAD Position  The integer (>= 0) position for this CAD Client.<br>
 * CAD User ID   The unique user id for this CAD Client.<br>
 * Error File    Filename of error logging file.<br>
 * -----------------------------------------------------------------------------<br>
 * Example File: <br>
 * CADSimulatorHost       = localhost<br>
 * CADSimulatorSocketPort = 4444<br>
 * CADPosition = 1 <br>
 * CADUserID   = A12345<br>
 * ErrorFile   = cad_client_err.txt<br>
 * </code>
 * 
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2009/04/17 16:27:47 $ $Revision: 1.8 $
 */
/*Hai :3  yeeeee*/
public class CADClient extends UnicastRemoteObject implements
        CADClientInterface {

    /** Error logger. */
    private static Logger cadClientLogger = Logger.getLogger("tmcsim.client");

    /**
     * Enumeration containing properties name values. See CADClient class
     * description for more information.
     * 
     * @author Matthew Cechini
     * @see CADClient
     */
    private static enum PROPERTIES {
        CAD_SIM_HOST("CADSimulatorHost"), CAD_SIM_PORT("CADSimulatorSocketPort"), CAD_RMI_PORT(
                "CADRmiPort"), CLIENT_CAD_POS("CADPosition"), CLIENT_USER_ID(
                "CADUserID"), KEYBOARD_TYPE("KeyboardType"), DISPLAY_TYPE(
                "DisplayType");

        public String name;

        private PROPERTIES(String n) {
            name = n;
        }
    }

    /**
     * CADClientSocket Object to handle socket communication between the Client
     * and CAD Simulator.
     */
    private CADClientSocket theClientSocket;

    /** Instance of the CADClientModel. */
    private CADClientModel theClientScreenModel;

    /** Instance of the CADClientView. */
    private CADClientView theClientScreenView;

    /**
     * Instance of the CADCLientGUI Replaces CADClientView
     */
    private CADClientGUI theClientGUI;

    /** Properties object for the CADClient class. */
    private Properties cadClientProp;

    /** RMI interface for communication with the remote Coordinator. */
    private static CoordinatorInterface theCoorInt;

    /** reference to itself to be used for disconnecting from CADSimulator */
    private CADClientInterface client = this;

    /**
     * Constructor. Initialize data from parsed properties file. Create a socket
     * connection to the CADSimulator. The ClientScreenModel is initialized with
     * the input and output I/O streams for socket communication. The
     * ClientScreenModel registers with the CAD Simulator, using CAD position
     * and userID read in from the properties file. The ClientScreenView is then
     * created and initialized and set as an observer of the model.
     * 
     * A thread is created with the runnable ClientScreenModel and is started.
     * When this thread is no longer alive, or the ClientScrenView and
     * CADClientSocket are closed. The program then exits.
     * 
     * @param propertiesFile
     *            File path (absolute or relative) to the properties file
     *            containing configuration data.
     */
    public CADClient(String propertiesFile) throws SimulationException,
            RemoteException {
        if (!verifyProperties(propertiesFile))
            System.exit(0);

        connect(cadClientProp.getProperty(PROPERTIES.CAD_SIM_HOST.name).trim(),
                cadClientProp.getProperty(PROPERTIES.CAD_RMI_PORT.name).trim());

        // Instantiate the Socket and Model Objects.
        theClientSocket = new CADClientSocket(cadClientProp.getProperty(
                PROPERTIES.CAD_SIM_HOST.name).trim(),
                Integer.parseInt(cadClientProp.getProperty(
                        PROPERTIES.CAD_SIM_PORT.name).trim()));
        theClientScreenModel = new CADClientModel();
        theClientScreenModel.initializeScreen(theClientSocket.getInputStream(),
                theClientSocket.getOutputStream());

        // Register this CAD Client with the Simulation Manager
        theClientScreenModel.register(Integer.parseInt(cadClientProp
                .getProperty(PROPERTIES.CLIENT_CAD_POS.name)), cadClientProp
                .getProperty(PROPERTIES.CLIENT_USER_ID.name));

        // Instantiate the CADScreenView and set up the model-view observer
        // relationship.
        theClientScreenView = new CADClientView(theClientScreenModel);
        theClientScreenView.setVisible(false);

        // TODO: set up model-view relationship similar to ClientView and
        // ScreenView
        // Can repurpose the old model, but may be better to copy over and
        // modify in parallel
        // This is required to perform powerline commands on the data
        theClientGUI = new CADClientGUI();

        // Each screen of the UI should have a reference to either it's parent
        // object or the main client
        // This ensures they all have access to each other and the data model
        theClientGUI.screen = new ScreenManager(theCoorInt);
        theClientGUI.login = new Login();
        theClientGUI.client = this;

        // setup keyboard settings for CAD Client
        if (cadClientProp.getProperty(PROPERTIES.KEYBOARD_TYPE.name).trim()
                .equals("CAD")) {
            CADEnums.CAD_KEYS.setupCADKeyboard();
        }
        // STD
        else {
            CADEnums.CAD_KEYS.setupStandardKeyboard();
        }

        theClientScreenModel.addObserver(theClientScreenView);

        // Initialize the display
        if (cadClientProp.getProperty(PROPERTIES.DISPLAY_TYPE.name).equals(
                "FULL_SCREEN")) {

            theClientScreenView.addWindowListener(new WindowListener() {
                public void windowClosed(WindowEvent e) {
                }

                public void windowOpened(WindowEvent e) {
                }

                public void windowIconified(WindowEvent e) {
                }

                public void windowDeiconified(WindowEvent e) {
                }

                public void windowActivated(WindowEvent e) {
                }

                public void windowDeactivated(WindowEvent e) {
                }

                public void windowClosing(WindowEvent e) {

                    try {
                        theClientSocket.closeSocket();
                    } catch (SimulationException se) {
                    }

                    System.exit(0);
                }
            });

            theClientScreenView.initWindow();
            theClientScreenView.setVisible(false);
        } else {
            JFrame cadFrame = new JFrame("CAD Client");
            cadFrame.add(theClientScreenView.initBox());
            cadFrame.setSize(800, 600);

            cadFrame.addWindowListener(new WindowListener() {
                public void windowClosed(WindowEvent e) {
                }

                public void windowOpened(WindowEvent e) {
                }

                public void windowIconified(WindowEvent e) {
                }

                public void windowDeiconified(WindowEvent e) {
                }

                public void windowActivated(WindowEvent e) {
                }

                public void windowDeactivated(WindowEvent e) {
                }

                public void windowClosing(WindowEvent e) {

                    try {
                        theClientSocket.closeSocket();
                    } catch (SimulationException se) {
                    }

                    System.exit(0);
                }
            });

            cadFrame.setVisible(true);
        }

        // Create the CAD Client thread to run the CADClientModel Object.
        Thread clientThread = new Thread(theClientScreenModel);
        clientThread.start();

        ensureProperShutdown();
    }

    /**
     * Connect to the Coordinator's RMI object, and register this object for
     * callback with the Coordinator.
     * 
     * @param hostname
     *            Host name of the CAD Simulator.
     * @param portNumber
     *            Port number of the CAD Simulator RMI communication.
     * @throws SimulationException
     *             if there is an error creating the RMI connection.
     */
    protected void connect(String hostname, String portNumber)
            throws SimulationException {

        String coorIntURL = "";

        try {
            coorIntURL = "rmi://" + hostname + ":" + portNumber
                    + "/coordinator";
            theCoorInt = (CoordinatorInterface) Naming.lookup(coorIntURL);
            theCoorInt.registerForCallback(this);
        } catch (Exception e) {
            throw new SimulationException(SimulationException.CAD_SIM_CONNECT,
                    e);
        }
    }

    /**
     * This method verifies that the CAD Simulator Host and Port values are not
     * null. Also, if a CAD Position or User ID do not exist in the properties
     * file, the user is prompted to enter values. These values are written to
     * the properties file. If the user cancels the process of entering these
     * values, the verification fails.
     * 
     * @param propertiesFile
     *            File path (absolute or relative) to the properties file
     *            containing configuration data.
     * @return True if the properties file is valid, false if not.
     * @throws SimulationException
     *             if there is an exception in verifying the properties file, or
     *             if the user cancels input.
     */
    private boolean verifyProperties(String propertiesFile)
            throws SimulationException {

        // Load the properties file.
        try {
            cadClientProp = new Properties();
            cadClientProp.load(new FileInputStream(propertiesFile));
        } catch (Exception e) {
            cadClientLogger.logp(Level.SEVERE, "SimulationManager",
                    "Constructor", "Exception in reading properties file.", e);

            throw new SimulationException(SimulationException.INITIALIZE_ERROR,
                    e);
        }

        // Ensure that the properties file does not have null values for the
        // CAD Simulator's connection information.
        if (cadClientProp.getProperty(PROPERTIES.CAD_SIM_HOST.name) == null
                || cadClientProp.getProperty(PROPERTIES.CAD_SIM_PORT.name) == null) {
            cadClientLogger.logp(Level.SEVERE, "SimulationManager",
                    "Constructor", "Null value in properties file.");
            throw new SimulationException(SimulationException.INITIALIZE_ERROR);
        }

        try {
            // If the properties file does not specify a CAD position, prompt
            // the
            // user to select one. If the user selects a position, write the
            // new properties values to the file. If the user cancels, else
            // throw an exception.
            if (cadClientProp.getProperty(PROPERTIES.CLIENT_CAD_POS.name) == null) {
                if (getCADPosition())
                    cadClientProp.store(new FileOutputStream(propertiesFile),
                            "");
                else
                    throw new SimulationException(
                            SimulationException.INITIALIZE_ERROR);
            }

            // If the properties file does not specifiy a CAD User ID, prompt
            // the
            // user to enter a value. If the user enters a valid ID, write the
            // new properties values to the file. If the user cancels, else
            // throw an exception.
            if (cadClientProp.getProperty(PROPERTIES.CLIENT_USER_ID.name) == null) {
                if (getUserID())
                    cadClientProp.store(new FileOutputStream(propertiesFile),
                            "");
                else
                    throw new SimulationException(
                            SimulationException.INITIALIZE_ERROR);
            }
        } catch (IOException ioe) {
            cadClientLogger.logp(Level.SEVERE, "SimulationManager",
                    "Constructor",
                    "Exception in writing to the properties file.");
            throw new SimulationException(SimulationException.INITIALIZE_ERROR);
        }

        // Ensure that the properties file has a valid display type
        if (cadClientProp.getProperty(PROPERTIES.DISPLAY_TYPE.name) == null
                || (!cadClientProp.getProperty(PROPERTIES.DISPLAY_TYPE.name)
                        .equals("FULL_SCREEN") && !cadClientProp.getProperty(
                        PROPERTIES.DISPLAY_TYPE.name).equals("FRAME"))) {
            cadClientLogger.logp(Level.SEVERE, "SimulationManager",
                    "Constructor", "Invalid display type.");
            throw new SimulationException(SimulationException.INITIALIZE_ERROR);
        }

        return true;
    }

    /**
     * This method prompts the user to select a value for the CAD position. If
     * the user cancels the method returns false, else the Properties object is
     * updated and true is returned.
     * 
     * @return True if the user successfully selected a CAD position, false if
     *         not.
     */
    private boolean getCADPosition() {

        Vector<Integer> positions = new Vector<Integer>();
        for (int i = 0; i < 10; i++)
            positions.add(i);

        Object cadPos = null;

        while (true) {
            cadPos = JOptionPane.showInputDialog(null,
                    "Please assign this workstation a CAD position number.",
                    "CAD Position Asignment", JOptionPane.QUESTION_MESSAGE,
                    null, positions.toArray(), positions.get(0));

            // If the user pressed cancel, confirm the exit and return false.
            if (cadPos == null) {
                if (JOptionPane
                        .showConfirmDialog(
                                null,
                                "CAD Client cannot load until a valid CAD "
                                        + "position has been selected.  Do you wish to "
                                        + "cancel loading the CAD Client?",
                                "Confirm Exit", JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
                    return false;
            }
            // Else the user selected a CAD position, exit the loop.
            else {
                break;
            }
        }

        cadClientProp.setProperty(PROPERTIES.CLIENT_CAD_POS.name,
                cadPos.toString());
        return true;
    }

    /**
     * This method prompts the user to enter a 5-character User ID. If the user
     * cancels the method returns false, else the Properties object is updated
     * and true is returned.
     * 
     * @return True if the user successfully selected a CAD position, false if
     *         not.
     */
    private boolean getUserID() {
        String cadUID = null;

        while (true) {
            cadUID = JOptionPane.showInputDialog(null,
                    "Please assign this workstation a 6-character CAD "
                            + "User ID.", "CAD User ID Asignment",
                    JOptionPane.QUESTION_MESSAGE);

            // /If the user pressed cancel, confirm the exit and return false.
            if (cadUID == null) {
                if (JOptionPane.showConfirmDialog(null,
                        "CAD Client cannot load until a valid User ID "
                                + "has been entered.  Do you wish to "
                                + "cancel loading the CAD Client?",
                        "Confirm Exit", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
                    return false;
            }
            // If the user does not enter a valid User ID, notify and reprompt.
            else if (cadUID.length() != 6) {
                JOptionPane.showMessageDialog(null,
                        "The User ID must be 6 characters.", "Invalid User ID",
                        JOptionPane.WARNING_MESSAGE);
            }
            // Else the user entered a valid value, exit the loop.
            else {
                break;
            }
        }

        cadClientProp.setProperty(PROPERTIES.CLIENT_USER_ID.name, cadUID);
        return true;
    }

    /**
     * Construct the CADClient with the properties file path, either from the
     * command line arguments or default.
     * 
     * @param args
     *            Command line arguments.
     */
    public static void main(String[] args) {
        System.setProperty("CAD_CLIENT_PROPERTIES",
                "config/cad_client_config.properties");

        try {
            if (System.getProperty("CAD_CLIENT_PROPERTIES") != null) {
                UIManager.setLookAndFeel(UIManager
                        .getSystemLookAndFeelClassName());

                new CADClient(System.getProperty("CAD_CLIENT_PROPERTIES"));
            } else {
                throw new Exception(
                        "CAD_CLIENT_PROPERTIES system property not defined.");
            }
        } catch (Exception e) {
            cadClientLogger.logp(Level.SEVERE, "SimulationManager", "Main",
                    "Error initializing application.");

            JOptionPane.showMessageDialog(new JWindow(), e.getMessage(),
                    "Error - Program Exiting", JOptionPane.ERROR_MESSAGE);

            System.exit(-1);
        }

    }

    public void refresh() {
        theClientGUI.screen.refreshScreens();
    }

    public void ensureProperShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    theCoorInt.unregisterForCallback(client);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
