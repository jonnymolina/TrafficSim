package tmcsim.simulationmanager;

import java.io.File;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import tmcsim.cadmodels.CMSDiversion;
import tmcsim.cadmodels.CMSInfo;
import tmcsim.client.cadclientgui.data.Incident;
import tmcsim.client.cadclientgui.data.IncidentEvent;
import tmcsim.common.ScriptException;
import tmcsim.common.SimulationException;
import tmcsim.common.CADEnums.PARAMICS_STATUS;
import tmcsim.common.CADEnums.SCRIPT_STATUS;
import tmcsim.interfaces.CoordinatorInterface;
import tmcsim.interfaces.SimulationManagerInterface;


/**
 * SimulationManagerModel is the model class for the Simulation Manager.  All communication
 * between the Coordinator and the Simulation Manager passes through this object.  The view
 * passes requests through local methods, and the Coordinator calls remote functions in this
 * object to update the viewed simulation data.
 * <br/> 
 * At construction, the SimulationManagerModel registers itself with the coordinator.  
 * Administrative commands for the simulation are received from the SimlationManagerView 
 * class, and then the appropriate Coordinator remote method is executed.  During a 
 * simulation, the Coordinator calls remote methods contained in the SimulationManagerInterface.  
 * For a description of those methods, see the interface's documentation.  After construction, 
 * this model class must have its setView() method called to set the reference to its view class.
 * 
 * @see SimulationManagerView
 * @see SimulationManagerInterface
 * @author Matthew Cechini 
 * @version $Revision: 1.3 $ $Date: 2006/06/06 20:46:41 $
 */
@SuppressWarnings("serial")
public class SimulationManagerModel extends UnicastRemoteObject 
    implements SimulationManagerInterface {
    
    /** Error Logger. */
    private Logger simManagerLogger = Logger.getLogger("tmcsim.simulationmanager");
    
    /** RMI interface for communication with the remote Coordinator. */ 
    private static CoordinatorInterface theCoorInt;
    
    /** The SimulationManagerView object. */
    private SimulationManagerView theSimManagerView;    
    

    /**
     * Constructor.  Establishes the RMI communication with the Coordinator.
     *
     * @param hostname Host name of the CAD Simulator.    
     * @param portNumber Port number of the CAD Simulator RMI communication. 
     * @throws RemoteException if error in RMI communication
     * @throws SimulationException if there is an error in registering RMI methods.
     */
    public SimulationManagerModel(String hostname, String portNumber) 
            throws RemoteException, SimulationException {
        super();                                
                                        
        connect(hostname, portNumber);
    }       
    
    /**
     * Connect to the Coordinator's RMI object, and register this object for
     * callback with the Coordinator.
     * @param hostname Host name of the CAD Simulator.    
     * @param portNumber Port number of the CAD Simulator RMI communication. 
     * @throws SimulationException if there is an error creating the RMI connection.
     */ 
    protected void connect(String hostname, String portNumber) 
        throws SimulationException {
        
        String coorIntURL = "";
        
        try {  
            coorIntURL = "rmi://" + hostname + ":" + portNumber + "/coordinator"; 
            
            theCoorInt = (CoordinatorInterface)Naming.lookup(coorIntURL);
            theCoorInt.registerForCallback(this);           
            
        }
        catch (Exception e) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "establishRMIConnection", "Unable to establish RMI " +
                    "communication with the CAD Simulator.  URL <" + coorIntURL + ">", e);
            
            throw new SimulationException(SimulationException.CAD_SIM_CONNECT, e);
        }   
    }
    
    /**
     * This method unregisters this SimulationManager from the Coordinator and closes
     * the RMI communication.
     */
    public void disconnect() {
        try {
            theCoorInt.unregisterForCallback(this);
            theCoorInt = null;
        }
        catch (Exception e) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "closeSimManager", "Exception in unregistering Simulation" +
                    "Manager from the CAD Simulator.", e);
        }
    }
    
    /**
     * Set the local reference to the SimulationManagerView object.  The view
     * is updated with the current simulation time, script status, and Paramics
     * connection status. 
     *
     * @param newView The instance of the SimulationManagerView class.
     * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
     */
    public void setView(SimulationManagerView newView) throws SimulationException {
        try {
            theSimManagerView = newView;
            
            theSimManagerView.tick(theCoorInt.getCurrentSimulationTime());  
            theSimManagerView.setScriptStatus(theCoorInt.getScriptStatus());
            theSimManagerView.setParamicsStatus(theCoorInt.getParamicsStatus());
            
            initialize();
        }
        catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "startSimulation", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }   
        
    }   
    
    /**
     * This method initializes the SimulationManager with all current simulation
     * data from the Coordinator.  If the simulation is running when the Simulation
     * Manager is initialized, all previously occured incidents, events, and current
     * diversion must be displayed.  This method first loads the list of current
     * incidents from the coordinator, then the triggered events, followed by 
     * all CMSInfo objects.  The SimulationManagerView is notified of any current
     * diversions within the CMSInfo objects.  
     * 
     * @throws SimulationException if there is an error in RMI communication or if the 
     *         SimulationManagerView reference has not been set.
     */
    protected void initialize() throws SimulationException {
        
        try {
        
            //Load all incidents from Coordinator
            loadIncidents(); 
            
            //Load all triggered incidents from Coordinator
            TreeMap<Integer, Vector<IncidentEvent>> tempEvents = theCoorInt.getTriggeredEvents();
            for(Integer key : tempEvents.keySet())  {           
                for(IncidentEvent ie : tempEvents.get(key)) 
                    eventOccured(key, ie);           
            }           
                  
            //Load all current diversions from the Coordinator
            TreeSet<String> cmsIDs = theCoorInt.getCMSIDs();
            CMSInfo cmsinfo  = null;
            for(String cms_id : cmsIDs) {
                cmsinfo = theCoorInt.getCMSDiversionInfo(cms_id);
                
                for(CMSDiversion div : cmsinfo.possibleDiversions) {
                    if(div.getCurrDiv() != 0) {                     
                        theSimManagerView.addDiversion(cmsinfo, div);
                    }       
                }
            }   
            
            //Send the list of CMS IDs to the View.
            theSimManagerView.setCMS_IDList(cmsIDs.toArray());
        }
        catch (Exception e) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "initialize", "Unable to initialize the SimulationManager.", e);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, e);
        }   
    }

    public void tick(long theTime) throws RemoteException {
        if(theSimManagerView != null)
            theSimManagerView.tick(theTime);    
    }
    
    public void incidentAdded(Incident newIncident) throws RemoteException {
        if(theSimManagerView != null) {
            Integer logNum = new Integer(newIncident.getLogNumber());
            
            theSimManagerView.addIncident(newIncident);         
            theSimManagerView.addIncidentTab(logNum);
        }
    }

    public void incidentStarted(Integer logNumber) throws RemoteException {
        if(theSimManagerView != null) {
            theSimManagerView.startIncident(logNumber);
        }
    }

    public void incidentRemoved(Integer logNumber) throws RemoteException {
        if(theSimManagerView != null) {
            theSimManagerView.removeIncident(logNumber);
            theSimManagerView.removeIncidentTab(logNumber);
        }
    }
    
    public void eventOccured(Integer logNumber, IncidentEvent theEvent) throws RemoteException {
        if(theSimManagerView != null) {
            theSimManagerView.addIncidentEvent(logNumber, theEvent);
        }
    }
    
    public void setScriptStatus(SCRIPT_STATUS newStatus) throws RemoteException {
        if(theSimManagerView != null)
            theSimManagerView.setScriptStatus(newStatus);       
    }

    public void setParamicsStatus(PARAMICS_STATUS newStatus) throws RemoteException {
        if(theSimManagerView != null)
            theSimManagerView.setParamicsStatus(newStatus);     
        
    }
    
    /**
     * This method passes the view's request to start the simulation 
     * on to the remote coordinator.
     *
     * @throws ScriptException if an error occurs in started the simulation.
     * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
     */  
    public void startSimulation() throws ScriptException, SimulationException {

        try {
            theCoorInt.startSimulation();
        }
        catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "startSimulation", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }   

    }
    
    /**
     * This method passes the view's request to reset the simulation 
     * on to the remote coordinator.  The View's simulation time is reset to 0.
     * The view's incident tabs are cleared and incident list reset and initialized
     * with the current list of incidents.  The list of diversions is also reset.
     * 
     * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
     */
    public void resetSimulation() throws SimulationException {      

        try {
            theCoorInt.resetSimulation();
            
            tick(0);
            theSimManagerView.resetIncidentTabs();
            theSimManagerView.resetIncidents();
            theSimManagerView.resetDiversions();

            loadIncidents(); 
        }
        catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "resetSimulation", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }   

    }
    
    /**
     * This method passes the view's request to pause the simulation 
     * on to the remote coordinator.
     *      
     * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
     */
    public void pauseSimulation() throws SimulationException {

        try {
            theCoorInt.pauseSimulation();
         }
        catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "pauseSimulation", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }   

    }
    
    /**
     * This method passes the view's request to goto a new simulation 
     * time on to the remote coordinator.
     * 
     * @param time Simulation time 
     * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
     */
    public void gotoSimulationTime(long time) throws SimulationException {
        
        try {
            theCoorInt.gotoSimulationTime(time);
        }
        catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "gotoSimulationTime", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }   
    }
    
    /**
     * This method passes the view's request to load a script file on to the 
     * remote Coordinator.  If this is successful, the View's incident tabs 
     * are cleared and incident list reset and initialized with the current 
     * list of incidents.  If the load is not successful, the View's incident tabs
     * and incident list are cleared.  The list of diversions is also reset.
     *
     * @param scriptFile the File chosen by the user in the open file dialog.
     * @throws ScriptException if an error occurs in reading the script file.
     * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
     */
    public void loadScript(File scriptFile) throws ScriptException, SimulationException{    
        
        try {
            theCoorInt.loadScriptFile(scriptFile);
            
            tick(0);
            theSimManagerView.resetIncidentTabs();
            theSimManagerView.resetIncidents();
            theSimManagerView.resetDiversions();
            
            loadIncidents();
         } 
         catch(ScriptException se) {
            theSimManagerView.resetIncidentTabs();
            theSimManagerView.resetIncidents();
            theSimManagerView.resetDiversions();
            throw se;
         }
        catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "loadScript", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }           
    }
    
     /**
      * This method passes the view's request to create a connection between
      * the CADSimulator and the Paramics Communicator on to the remote Coordinator. 
      *
      * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
      */
     public void connectToParamics() throws SimulationException {

        try {
            theCoorInt.connectToParamics();
        } 
        catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "connectToParamics", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }
     }
     
     /** 
      * This method passes the view's request to drop the connection between
      * the CADSimulator and the Paramics Communicator on to the
      * remote Coordinator. 
      *
      * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
      */
     public void disconnectFromParamics() throws SimulationException { 

        try {
            theCoorInt.disconnectFromParamics();
        } 
        catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "disconnectFromParamics", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }
     }
     
     /**
      * This method passes the view's request to load a paramics
      * network on to the remote Coordinator. 
      *
      * @param networkID The unique network ID that is being loaded
      *
      * @throws ScriptException if there is an error in loading the network
      * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
      */
     public void loadParamicsNetwork(int networkID) throws ScriptException, SimulationException {
        try {
             theCoorInt.loadParamicsNetwork(networkID);         
        } catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "loadParamicsNetwork", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }
     }
         
     /**
      * This method passes the view's request to get the value of the
      * currently loaded paramics network on to the remote Coordinator.
      * 
      * @return Value of the loaded paramics network.  
      * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
      */
     public int getParamicsNetworkLoaded() throws SimulationException {
         try {
            return theCoorInt.getParamicsNetworkLoaded();           
        } catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "getParamicsNetworkLoaded", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }
     }
    
    /**
     * This method passes the view's request to trigger an incident 
     * on to the remote Coordinator.
     *
     * @throws ScriptException if an error occurs in triggering an event.
     * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
     */
    public void triggerIncident(int logNumber) throws ScriptException, SimulationException {
        try {
            theCoorInt.triggerIncident(logNumber);
            
        } catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "startIncident", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }
    }

     /**
      * This method passes the view's request to add an incident into the 
      * simulation on to the remote Coordinator.
      *
      * @param newIncident Incident to add to the simulation
      * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
      */
     public void addIncident(final Incident newIncident) throws SimulationException {       

        try {
            theCoorInt.addIncident(newIncident);
    
        } catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "addIncident", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }       

    }
    
    /**
     * This method passes the view's request to delete an incident from the 
     * simulation on to the remote Coordinator.
     *
     * @throws ScriptException if an error occurs in deleting an event.
     * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
     */
    public void deleteIncident(int logNumber) throws ScriptException, SimulationException {

        try {
            theCoorInt.deleteIncident(logNumber);   
    
        } catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "deleteIncident", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }       
    }   
    
    
    /**
     * This method passes the view's request to reschedule an incident on to 
     * the remote Coordinator.
     *
     * @param newTime New simulation time (in seconds).
     * @throws ScriptException if the Incident has already started or the time for 
     *         recheduling has already passed.
     * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
     */
     public void rescheduleIncident(long newTime, int logNumber) throws ScriptException, SimulationException {
            
        try {
            theCoorInt.rescheduleIncident(logNumber, newTime);
        } catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "rescheduleIncident", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }               
     }
     
    /**
     * This method passes the view's request to get the current list of 
     * Incidents loaded into the simulation on to the remote Coordinator.
     *
     * @return Vector The Vector of currently loaded incidents.
     * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
     */
    public Vector<Incident> getIncidentList() throws SimulationException {

        try {
            return theCoorInt.getIncidentList();
            
        } catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "getIncidentList", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }
    }    
             
    /**
     * This method passes the view's request to get the CMSInfo object 
     * that corresponds to a unique CMS ID String on to the remote Coordinator.
     * 
     * @param cms_id Unique CMS ID String.
     * @return CMSInfo object corresponding to the parameter CMS id.
     * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
     */
    public CMSInfo getCMSDiversionInfo(String cms_id) throws SimulationException {
        
        try {
            return theCoorInt.getCMSDiversionInfo(cms_id);
        } catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "getCMSDiversionInfo", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }
    }
     
    /**
     * This method passes the view's request to update diversions for a
     * CMS on to the remote Coordinator.
     * 
     * @param diversion CMS diversions information to apply.
     * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
     */
    public void applyDiversions(CMSInfo diversion) throws SimulationException {
        
        try {
            theCoorInt.applyDiversions(diversion);

        } catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "applyDiversions", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }
    }
    
    /**
     * This method loads the current list of incidents from the Coordinator into the
     * SimulationManager.  For each incident in the simulation, notify the view to
     * add a new incident tab.  If the Incident has begun in the simulation, 
     * call the incidentStarted() method to update the view accordingly.
     * 
     * @throws SimulationException if there is an error in RMI communication to the CAD Simulator.
     */ 
    private void loadIncidents() throws SimulationException {       
        
        try {           
            for(Incident inc : theCoorInt.getIncidentList()) {
    
                Integer logNum = new Integer(inc.getLogNumber());
                
                theSimManagerView.addIncidentTab(logNum);
                theSimManagerView.addIncident(inc);
            
                if(inc.getSecondsToStart() < theCoorInt.getCurrentSimulationTime()) {
                    incidentStarted(logNum);
                }
            }
        } catch (RemoteException re) {
            simManagerLogger.logp(Level.SEVERE, "SimulationManagerModel", 
                    "loadIncidentListTable", "Unable to communicate with the " +
                    "CAD Simulator.", re);
            
            throw new SimulationException(SimulationException.CAD_SIM_COMM, re);
        }
        
    }
    

}