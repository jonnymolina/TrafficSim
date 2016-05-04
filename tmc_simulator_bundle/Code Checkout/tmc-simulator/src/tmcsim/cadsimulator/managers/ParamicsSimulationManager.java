package tmcsim.cadsimulator.managers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import tmcsim.cadmodels.CMSInfo;
import tmcsim.cadsimulator.Coordinator;
import tmcsim.cadsimulator.paramicscontrol.ParamicsCameraStatusReader;
import tmcsim.cadsimulator.paramicscontrol.ParamicsCommunicator;
import tmcsim.cadsimulator.paramicscontrol.ParamicsIncidentWriter;
import tmcsim.cadsimulator.paramicscontrol.ParamicsStatusReader;
import tmcsim.common.SimulationException;
import tmcsim.common.XMLIncident;
import tmcsim.common.CADEnums.PARAMICS_STATUS;

/**
 * ParamicsSimulationManager is a CAD Simulator Manager used to handle all
 * communication between the CAD Simulator and the remote ParamicsCommunicator.
 * Upon construction all ParamicsReaders and ParamicsWriters are initialized
 * with data parsed from the Properties file. When a connection is established
 * the ParamicsStatusReader is registered to read Paramics Status updates.  
 * These updates are received through the updateParamicsStatus() method, which 
 * notifies the Coordinator of the new status.  The loadParamicsNetwork() must 
 * be called to register the ParamicsIncidentWriter which will write the
 * information to cause Paramics to begin loading a traffic network.  When the
 * status becomes LOADED, the ParamicsCameraStatusReader is registered and
 * begins reading.  All camera speed updates are received through the
 * updateCameraInfo() method.  The startSimulation(), resetSimulation(), and 
 * sendIncidentUpdate() methods are used to control the flow of Incident 
 * update information to Paramics.  The updateIncident() and updateDiversion() 
 * methods are used to update the information that is sent to Paramics.
 *  
 *      
 * @see PARAMICS_STATUS
 * @author Matthew Cechini
 * @version
 */
public class ParamicsSimulationManager {

    /**  Error logger.  */
    private static Logger paramLogger = Logger.getLogger("tmcsim.cadsimulator.paramicscontrol");
    
    /**
     * Enumeration containing property names for Properties parsing.
     * @author Matthew Cechini
     */
    private static enum PROPERTIES {
        PARAMICS_HOST        ("ParamicsCommHost"),
        PARAMICS_PORT        ("ParamicsCommPort"),
        INCIDENT_UPDATE_INT  ("IncidentUpdateInterval"),
        INCIDENT_UPDATE_FILE ("IncidentUpdateFile"),
        PARAMICS_STATUS_INT  ("ParamicsStatusInterval"),
        PARAMICS_STATUS_FILE ("ParamicsStatusFile"),
        CAMERA_STATUS_INT    ("CameraStatusInterval"),
        CAMERA_STATUS_FILE   ("CameraStatusFile");
        
        String name;
        
        private PROPERTIES(String n) {
            name = n;
        }
    };
    
    /** ParamicsCommunicator Object used for communication. */
    private ParamicsCommunicator theCommunicator;

    /** Reference to the Coordinator Object. */
    private Coordinator theCoordinator;

    /** Reference to the MediaManager Object. */
    private MediaManager theMediaMgr;
    
    /** ParamicsIncidentWriter used to send incident updates to Paramics. */
    private ParamicsIncidentWriter paramicsIncidentWriter;    
    
    /** ParamicsStatusReader used to read status information from Paramics. */
    private ParamicsStatusReader paramicsStatusReader;
    
    /** ParamicsCameraStatusReader used to read speed information from Paramics. */
    private ParamicsCameraStatusReader paramicsCameraStatusReader;

    /** Properties file. */
    private Properties paramicsProperties;
    
    
    /**
     * Constructor.  Loads the Properties file and initializes all Paramics
     * Writers and Readers with the parsed data.
     * 
     * @param propertiesFile Target file path of properties file.
     * @param coor Coordinator Object.
     * @param mediaMgr MediaManager Object.
     */
    public ParamicsSimulationManager(String propertiesFile, Coordinator coor, MediaManager mediaMgr) {
        
        try {
            paramicsProperties = new Properties();
            paramicsProperties.load(new FileInputStream(propertiesFile));
            
            theCoordinator = coor;
            theMediaMgr    = mediaMgr;
                  
            theCommunicator = new ParamicsCommunicator(this, 
                    paramicsProperties.getProperty(PROPERTIES.PARAMICS_HOST.name),
                    Integer.parseInt(paramicsProperties.getProperty(
                            PROPERTIES.PARAMICS_PORT.name).trim()));
                    
            paramicsIncidentWriter = new ParamicsIncidentWriter(Integer.parseInt(
                    paramicsProperties.getProperty(PROPERTIES.INCIDENT_UPDATE_INT.name).trim()));  
            paramicsIncidentWriter.writerID   = theCommunicator.nextID();
            paramicsIncidentWriter.targetFile = paramicsProperties.getProperty(
                    PROPERTIES.INCIDENT_UPDATE_FILE.name);      
            
            paramicsStatusReader = new ParamicsStatusReader(this);
            paramicsStatusReader.readerID   = theCommunicator.nextID();
            paramicsStatusReader.interval   = paramicsProperties.getProperty(
                    PROPERTIES.PARAMICS_STATUS_INT.name).trim();
            paramicsStatusReader.targetFile = paramicsProperties.getProperty(
                    PROPERTIES.PARAMICS_STATUS_FILE.name);
            
            paramicsCameraStatusReader = new ParamicsCameraStatusReader(this);
            paramicsCameraStatusReader.readerID   = theCommunicator.nextID();
            paramicsCameraStatusReader.interval   = paramicsProperties.getProperty(
                    PROPERTIES.CAMERA_STATUS_INT.name).trim();
            paramicsCameraStatusReader.targetFile = paramicsProperties.getProperty(
                    PROPERTIES.CAMERA_STATUS_FILE.name);    
        }
        catch (Exception e) {
            paramLogger.logp(Level.SEVERE, "ParamicsSimulationManager", 
                    "Constructor", "Exception in parsing properties file.", e);
        }
        
    }
   
    /**
     * Returns whether a connection exists to the remote ParamicsCommunicator.
     * @return true if a connection exists, false if not.
     */
    public boolean isConnected() {
        return theCommunicator.isConnected();
    }
    
    /**
     * Returns the integer Network ID that has been loaded into Paramics.
     * @return Network ID.
     */
    public int getParamicsNetworkLoaded() {
        return paramicsStatusReader.getNetworkID();
    }
    
    /**
     * Returns the current status of the Paramics traffic modeler.
     * @return Current PARAMICS_STATUS 
     */
    public PARAMICS_STATUS getParamicsStatus() {
        return paramicsStatusReader.getStatus();
    }
    
    /**
     * Updates the ParamicsIncidentWriter with new Incident information.
     * @param xmlInc Updated Incident information.
     */
    public void updateIncident(XMLIncident xmlInc) {
        paramicsIncidentWriter.updateIncident(xmlInc);
    }
    
    /**
     * Updates the ParamicsIncidentWriter with the new Diversion information.
     * @param theDiversion Updated diversion information.
     */
    public void updateDiversion(CMSInfo theDiversion) {
        paramicsIncidentWriter.updateDiversion(theDiversion);
    }
    
    /**
     * Notifies the ParamicsIncidentWriter to start the simulation.
     */
    public void startSimulation() {
        paramicsIncidentWriter.startSimulation();
    }
    
    /**
     * Notifies the ParamicsIncidentWriter to reset the simulation.
     */
    public void resetSimulation() {
        paramicsIncidentWriter.resetSimulation();
    }

    /**
     * Notifies the ParamicsIncidentWriter to send an Incident update.
     * @param currentSimTime Current simulation time (in seconds).
     */
    public void sendIncidentUpdate(long currentSimTime) {
        paramicsIncidentWriter.sendUpdate(currentSimTime);
    }
    
    /**
     * Establish a connection to the remote ParamicsCommunicator.  Register
     * the ParamicsStatusReader and update the current status to CONNECTED.
     * If an exception occurs in connecting to the ParamicsCommunicator,
     * update the current status to UNREACHABLE.
     */
    public void connectToParamics() {
        try {           
            theCommunicator.connect();          
            theCommunicator.registerReader(paramicsStatusReader);
            
            updateParamicsStatus(PARAMICS_STATUS.CONNECTED);
        }
        catch (IOException ioe) {
            paramLogger.logp(Level.SEVERE, "Coordinator", "connectToParamics", 
                    "Communication error in connecting to Paramics.", ioe);

            updateParamicsStatus(PARAMICS_STATUS.UNREACHABLE);
        }       
    }

    /**
     * Close the connection to the remote ParamicsCommunicator.  Unregister
     * all Readers and Wrtiers from the ParamicsCommunicator.  Reset the status
     * information in the ParamicsStatusReader.  Update the current status to 
     * DISCONNECTED.
     */
    public void disconnectFromParamics() { 
        
        theCommunicator.disconnect();
        theCommunicator.unregisterReader(paramicsStatusReader);
        theCommunicator.unregisterWriter(paramicsIncidentWriter);   
        theCommunicator.unregisterReader(paramicsCameraStatusReader);
                    
        paramicsStatusReader.resetStatusInfo();

        updateParamicsStatus(PARAMICS_STATUS.DISCONNECTED);
    }
    
    /**
     * Updates the current paramics status.  If the new status is LOADED,
     * then notify the ParamicsIncidentWriter that the network has been loaded
     * and register the ParamicsCameraStatusReader with the ParamicsCommunicator.
     * Notify the Coordinator of all Paramics status updates.
     *
     * @param  newStatus New Paramics status.
     */ 
    public void updateParamicsStatus(PARAMICS_STATUS newStatus) {
                        
        //the network has finished loading
        if(newStatus == PARAMICS_STATUS.LOADED) {
        
            paramicsIncidentWriter.networkLoaded();
        
            theCommunicator.registerReader(paramicsCameraStatusReader);                     
        }
        else if(newStatus == PARAMICS_STATUS.DROPPED) {
            paramLogger.logp(Level.WARNING, "Coordinator", "updateParamicsStatus", 
                    "Connection to Paramics has been dropped.");
        }
        
        theCoordinator.setParamicsStatus(newStatus);        

    } 
    
    /**
     * If a connection has been made, register the ParamicsIncidentWriter
     * and use it to notify Paramics of the Network ID to load.
     * 
     * @param networkID Network ID to load.
     * @throws SimulationException if a connection has not been made to the 
     *         remote ParamicsCommunicator.
     */
    public void loadParamicsNetwork(int networkID) throws SimulationException {
            
        if(theCommunicator.isConnected()) {
            theCommunicator.registerWriter(paramicsIncidentWriter);
            paramicsIncidentWriter.loadNetwork(networkID);
        }
        else 
            throw new SimulationException(SimulationException.PARAMICS_NOT_CONNECTED);
    }
    
    /**
     * Receive updated camera speed information. Notify the MediaManager with 
     * the updated data. 
     * 
     * @param cameraID CCTV camera ID
     * @param avgSpeed_NE Average speed of traffic flowing N or E
     * @param avgSpeed_SW Average speed of traffic flowing S or W
     */
    public void updateCameraInfo(Integer cameraID, float avgSpeed_NE, float avgSpeed_SW) {
        theMediaMgr.updateCameraInfo(cameraID, avgSpeed_NE, avgSpeed_SW);
    }
}
