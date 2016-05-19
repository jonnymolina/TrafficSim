package tmcsim.paramicscommunicator;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tmcsim.common.CADProtocol.PARAMICS_ACTIONS;
import tmcsim.common.CADProtocol.PARAMICS_COMM_TAGS;
import tmcsim.paramicscommunicator.FileIOUpdate.IO_TYPE;
import tmcsim.paramicscommunicator.FileRegUpdate.REG_TYPE;
import tmcsim.paramicscommunicator.gui.ParamicsCommunicatorGUI;
import tmcsim.simulationmanager.SimulationManager;


/**
 * ParamicsCommunicator is the main class for this module.  The Paramics 
 * Communicator is used to provide communication between the CAD Simulator
 * and the Paramics traffic modeler.  While the application is running, data 
 * is received on a socket from the CAD Simulator.  Transmitted data are
 * XML documents containing information and action commands. The CAD Simulator 
 * registers readers and writers with the ParamicsCommunicator.  Any data read 
 * by a ParamicsReader is sent back to the CAD Simulator.  All data to be 
 * written by a ParamicsWriter is received through the socket.<br><br>  
 * The properties file for the ParamicsCommunicator class contains the following data.<br>
 * <code>
 * -----------------------------------------------------------------------------<br>
 * Socket Port          The port number to use for socket communication.<br>
 * Working Directory    The working directory use for Paramics file communication.<br>
 * Error File           The target file to use for error logging.<br>
 * -----------------------------------------------------------------------------<br>
 * Example File: <br>
 * SocketPort          = 4450 <br>
 * WorkingDirectory    = c:\\tmc_simulator\\ <br>
 * ErrorFile           = sim_mgr_error.xml <br>
 * -----------------------------------------------------------------------------<br>
 * </code>
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2009/04/17 16:27:46 $ $Revision: 1.7 
 */
public class ParamicsCommunicator extends Observable implements Observer, Runnable {

    /** Error logger. */
    private static Logger paramLogger = Logger.getLogger("tmcsim.paramicscommunicator");
    
    /**
     * Enumeration containing property names.
     * @author Matthew Cechini
     */
    private static enum PROPERTIES {
        
        SOCKET_PORT ("SocketPort"),
        WORKING_DIR ("WorkingDirectory");       
        
        public String name;
        
        private PROPERTIES(String n) {
            name = n;
        }
    }
    
        
    /** Properties object. */
    private Properties paramicsCommProp = null; 
    
    /** Current working directory where files will be read and written */
    private String workingDirectory = null;
    
    /** Socket used to create socket communication with the CAD Simulator.  */
    private ServerSocket serverSocket = null;
    
    /** Soccket used to communicate with CAD Simulator.*/
    private Socket paramicsSocket = null;   
    
    /** Input Stream for reading data from the CAD Simulator. */
    private ObjectInputStream in = null;
    
    /** Output Stream for writing data to the CAD Simulator.  */
    private ObjectOutputStream out = null;
    
    /** Map of all current ParamicsFileWriters referenced by I/O ID. */
    private TreeMap<String, ParamicsFileWriter> writers = null;
    
    /** Map of all current ParamicsFileReaders referenced by I/O ID.  */
    private TreeMap<String, ParamicsFileReader> readers = null;
    
    /** The view class for the ParamicsCommunicator. */
    private ParamicsCommunicatorGUI theGUI;
    
    /**
     * Constructor.  Read in the property values.  If the properties file 
     * does not contain a value for the working directory, open a dialog
     * to prompt the user for the path of the Paramics working directory.
     * An empty string is not accepted.  A null signifies that the user
     * pressed cancel.  Prompt the user to accept the cancel and exit the
     * application if confirmed.  Continue until a valid directory has been
     * entered, that exists, and append a '\' to the end of the directory
     * if necessary.
     * 
     * Initialize the Sockets
     * and begin communication.
     *
     * @param propertiesFilePath File Path of ParamicsCommunicator properties file.
     */
    public ParamicsCommunicator (String propertiesFile) 
    {
        
        writers = new TreeMap<String, ParamicsFileWriter>();
        readers = new TreeMap<String, ParamicsFileReader>();

        theGUI = new ParamicsCommunicatorGUI();
        addObserver(theGUI);
        theGUI.addWindowListener(new WindowListener() {         
            public void windowActivated(WindowEvent arg0) {};
            public void windowClosed(WindowEvent arg0) {};
            public void windowClosing(WindowEvent arg0) {
                System.exit(0);
            }
            public void windowDeactivated(WindowEvent arg0) {};
            public void windowDeiconified(WindowEvent arg0) {};
            public void windowIconified(WindowEvent arg0) {};
            public void windowOpened(WindowEvent arg0) {};
        });
        
        try {
            paramicsCommProp = new Properties();
            paramicsCommProp.load(new FileInputStream(propertiesFile));
            
            if(paramicsCommProp.getProperty(PROPERTIES.SOCKET_PORT.name) == null) 
            {
                JOptionPane.showMessageDialog(theGUI, 
                        "Properties file missing CAD Simulator Port information.", 
                        "Invalid Configuration", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            else if(paramicsCommProp.getProperty(PROPERTIES.WORKING_DIR.name) == null || 
                    paramicsCommProp.getProperty(PROPERTIES.WORKING_DIR.name).length() == 0) {
                
                try {
                    String workingDir = null;
                    
                    while (workingDir == null || workingDir.length() == 0) {                        
                        workingDir = JOptionPane.showInputDialog(null,
                                "Please set the output directory for Paramics communication.",
                                "Paramics Working Directory", JOptionPane.QUESTION_MESSAGE);
                        
                        if(workingDir == null) {
                            
                        }
                        else if(!new File(workingDir).exists()) {
                            JOptionPane.showMessageDialog(null,
                                    "Directory does not exist.",
                                    "Invalid Working Directory", JOptionPane.WARNING_MESSAGE);
                            
                            workingDir = null;
                        }
                        else if(!new File(workingDir).isDirectory()) {
                            JOptionPane.showMessageDialog(null,
                                    workingDir + " is not a directory.",
                                    "Invalid Working Directory", JOptionPane.WARNING_MESSAGE);
                            
                            workingDir = null;
                        }                       
                    }

                    if(workingDir.lastIndexOf("\\") != workingDir.length()-1) {
                        workingDir = workingDir + "\\";
                    }
                    
                    paramicsCommProp.setProperty(PROPERTIES.WORKING_DIR.name, workingDir);      
                    paramicsCommProp.store(new FileOutputStream(propertiesFile), "");
                } catch (IOException ioe) {
                    paramLogger.logp(Level.SEVERE, "ParamicsCommunicator", "Constructor", 
                            "Exception in writing properties file.", ioe);
                }
                
            }

            workingDirectory = paramicsCommProp.getProperty(
                    PROPERTIES.WORKING_DIR.name).trim();                    

        } catch (Exception e) {
            paramLogger.logp(Level.SEVERE, "ParamicsCommunicator", "Constructor", 
                    "Exception in reading properties file.", e);
        }
        
        
        try {
            initializeSockets(Integer.parseInt(paramicsCommProp.getProperty(
                    PROPERTIES.SOCKET_PORT.name).trim()));
        }
        catch (Exception e) {
            paramLogger.logp(Level.SEVERE, "ParamicsCommunicator", "Constructor", 
                    "Exception in initializing sockets.", e);
        }

    }
    
    /**
     * Transmits a message XML document object to the CAD Simulator.
     *
     * @param mess The ParamicsCommMessage to be transmitted.
     */
    private void write(Document mess) {
        
        synchronized(paramicsSocket) {
            try {
                out.writeObject(mess);
                out.flush();
            }
            catch (Exception e) {
                paramLogger.logp(Level.SEVERE, "ParamicsCommunicator", "write", 
                        "Exception in writing to the socket.", e);
            }
        }
    }
    
    /**
     * Observer/Observable update method.  The Paramics Communicator observers
     * registered ParamicsReaders.  When messages are to be sent, they are sent
     * through this method.  All messages are ParamicsCommMessage objects.  Send 
     * these messages to the write() method for transmission on the socket.
     */
    public void update(Observable o, Object arg) {      

        if(arg instanceof Document) {
            write((Document)arg);               
        }           
    }
    
    /**
     * Runnable method.  While this thread is not interrupted, read in an 
     * object from the socket input stream.  If an object exists, call
     * doMessage() to parse and perform the received action in the message.  
     */
    public void run() {
        
        while(true) {                       
            try {
                doMessage((Document)in.readObject());
            } 
            catch(SocketTimeoutException ste) {
                //just try again
            }   
            catch(EOFException eofe) {
                paramLogger.logp(Level.SEVERE, "ParamicsCommunicator", 
                        "run", "EOF Exception in reading data from the socket.", eofe);
            }
            catch(Exception e) {
                paramLogger.logp(Level.SEVERE, "ParamicsCommunicator", 
                        "run", "Exception in reading data from the socket.", e);

                JOptionPane.showMessageDialog(theGUI, 
                        "Connection has been lost to the CAD Simulator.  " +
                        "Paramics Communicator will now shutdown.", 
                        "Dropped Connection", JOptionPane.ERROR_MESSAGE);
                break;
            }                   
        }
        
        
        try { in.close(); } catch (Exception e) {}
        try { out.close(); } catch (Exception e) {}
        try { serverSocket.close(); } catch (Exception e) {}
        try { paramicsSocket.close(); } catch (Exception e) {}
        
    }
    
    /**
     * Perform the action represented in the received XML document message.
     * First determine if the action is from a READER, WRITER, and RESET.  
     * If the paramics action is REGISTER, add a new ParamicsFileReader/Writer 
     * to the local list of readers/writers and update the GUI with a 
     * FileRegUpdate object. If the paramics action is UNREGISTER, remove the 
     * ParamicsFileReader/Writer from the local list of readers/writers and update
     * the GUI with a FileRegUpdate object.  If RESET is received, clear all 
     * readers and writers. 
     * 
     * @param mess Received XML document message.
     */
    private void doMessage (Document mess) {
        
        Element rootElement = mess.getDocumentElement();

        String id     = null;
        String action = null;
        
        switch(PARAMICS_COMM_TAGS.fromString(rootElement.getNodeName())) {
            case READER:
                id     = rootElement.getAttribute(PARAMICS_COMM_TAGS.ID.tag);
                action = rootElement.getAttribute(PARAMICS_COMM_TAGS.ACTION.tag);
                
                switch(PARAMICS_ACTIONS.fromString(action)) {
                    case REGISTER:
                        Integer interval   = Integer.parseInt(rootElement.getChildNodes().item(0).getTextContent());
                        String  targetFile = rootElement.getChildNodes().item(1).getTextContent();
                        
                        readers.put(id, new ParamicsFileReader(workingDirectory, id, 
                                interval, targetFile));
                        readers.get(id).addObserver(this);
                        readers.get(id).addObserver(theGUI);
                        
                        setChanged();
                        notifyObservers(new FileRegUpdate(IO_TYPE.READ, 
                                REG_TYPE.REGISTER, id, targetFile, interval));
                        break;
                    case UNREGISTER: 
                        readers.get(id).deleteObserver(this);
                        readers.get(id).deleteObserver(theGUI);
                        readers.remove(id);

                        setChanged();
                        notifyObservers(new FileRegUpdate(IO_TYPE.READ, 
                                REG_TYPE.UNREGISTER, id, null, null));                      
                        break;
                }
                break;
            case WRITER:
                id     = rootElement.getAttribute(PARAMICS_COMM_TAGS.ID.tag);
                action = rootElement.getAttribute(PARAMICS_COMM_TAGS.ACTION.tag);
                
                switch(PARAMICS_ACTIONS.fromString(action)) {
                    case REGISTER:
                        String targetFile = rootElement.getChildNodes().item(0).getTextContent(); 
                        
                        writers.put(id, new ParamicsFileWriter(id, 
                                workingDirectory, targetFile));
                        writers.get(id).addObserver(theGUI);
                        
                        setChanged();
                        notifyObservers(new FileRegUpdate(IO_TYPE.WRITE, 
                                REG_TYPE.REGISTER, id, targetFile, null));      
                        break;
                    case UNREGISTER: 
                        writers.remove(id);
                        
                        writers.get(id).deleteObserver(theGUI);

                        setChanged();
                        notifyObservers(new FileRegUpdate(IO_TYPE.WRITE, 
                                REG_TYPE.UNREGISTER, id, null, null));
                        break;
                    case WRITE_FILE:
                        writers.get(id).writeMessage((Element)rootElement.getChildNodes().item(0));
                        break;
                }
                break;
            case RESET:
                readers.clear();
                writers.clear();
                break;
        }   
    }
    
    /**
     * Method waits to accept a socket connection from the CAD Simulator.  
     * When a connection has been established the method exits.  The input and
     * output streams are created on the new socket.
     * 
     * @param socketPort Socket port to use for establishing Socket communication.
     * @throws IOException if there is an exception in establishing Socket communication.
     */
    private void initializeSockets(Integer socketPort) throws IOException {
    
        boolean waiting = true;
        
        try {
            serverSocket = new ServerSocket(socketPort);            
            //delay for accept timeout(milliseconds)        
            serverSocket.setSoTimeout(10 * 1000);  
        }
        catch (IOException ioe) {
            throw new IOException("Exception in creating " +
                    "the server socket on port " + socketPort);
        }
        
        while(waiting) {            
            try{
                paramicsSocket = serverSocket.accept();
                waiting = false;
            } 
            catch(SocketTimeoutException ste) {
                System.out.println("...waiting...");
            }   
            catch(IOException ioe) {
                throw new IOException("Exception in creating " +
                        "the receiving socket on port " + socketPort);
            }
        }

        
        //** out must be performed before in to unlock for connecting socket **//
        try {       
            out     = new ObjectOutputStream(paramicsSocket.getOutputStream());
            in      = new ObjectInputStream(paramicsSocket.getInputStream());
        }
        catch (IOException ioe) {
            throw new IOException("Exception in creating input " +
                    "and output streams on socket.");
        }
            
    }

    /**
     * Construct the ParamicsCommunicator with the properties file path, 
     * either from the command line arguments or default.
     * 
     * @param args Command line arguments.192.168.251.45
     */
    public static void main(String[] args) {
        System.setProperty("PARAMICS_COMM_PROPERTIES",  "config/paramics_communicator_config.properties");
        try
        {
            if(System.getProperty("PARAMICS_COMM_PROPERTIES") != null)
            {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    
                new Thread(new ParamicsCommunicator(System.getProperty(
                        "PARAMICS_COMM_PROPERTIES"))).start();
            }
            else
            {
                throw new Exception ("PARAMICS_COMM_PROPERTIES system property not defined.");
            }
        } 
        catch (Exception e) 
        {
            paramLogger.logp(Level.SEVERE, "ParamicsCommunicator", "Main", 
                    "Error occured initializing application", e);

            JOptionPane.showMessageDialog(null, e.getMessage(), 
                    "Error - Program Exiting", JOptionPane.ERROR_MESSAGE);  
            
            System.exit(-1);
        }
    
        
    }   
    
}