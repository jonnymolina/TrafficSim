package tmcsim.interfaces;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

import tmcsim.cadmodels.CMSInfo;
import tmcsim.client.cadclientgui.data.Incident;
import tmcsim.common.ScriptException;

/**
 * RMI Interface providing methods for control of the simulation data.  
 * This interface allows for registration of a remote SimulationManager.
 * This object is notified of simulation information as the simulation
 * runs.  Simulation control functionality includes starting, pausing, 
 * resetting, and moving the repositioning the simulation.  The
 * SimulationControlInterface also provides methods to load a new 
 * script into the simulation, manually trigger incidents, delete
 * current incidents, reschedule current incidents, and add new
 * incidents.  The final control functionality allows new diversions
 * to be applied to the simulation. 
 *  
 * @author Matthew Cechini
 * @version
 */
public interface SimulationControlInterface extends Remote {

    /**
     * Registers a remote SimulationManager for callback.  Only one 
     * SimulationManager can be connected at a time. The last one to 
     * connect will be referenced in the callback RMI.
     *
     * @param simManInt Interface to the SimulationManager for callback RMI
     * @throws RemoteException if there is an error in the RMI communication.
     */
    public void registerForCallback(SimulationManagerInterface simManInt)
        throws RemoteException;

    /**
     * Unregisters a remote SimulationManager from callback.  The coordinator
     * will cease calling that SimulationManager with the updated simulation data.
     *
     * @param simManInt Interface to the SimulationManager for callback RMI
     * @throws RemoteException if there is an error in the RMI communication.
     */
    public void unregisterForCallback(SimulationManagerInterface simManInt)
        throws RemoteException;
    
    /**
     * Starts the simualation.  Simulation time begins.
     *
     * @throws RemoteException if there is an error in the RMI communication.
     * @throws ScriptException if a script has not been loaded.
     */
    public void startSimulation() throws RemoteException, ScriptException;

    /**
     * Pauses the simulation.  Simulation time is suspended.
     *
     * @throws RemoteException if error occurs in RMI
     */
    public void pauseSimulation() throws RemoteException;

    /**
     * Reset the simulation.  Counters are reset and local lists
     * of simulation objects are cleared.
     *
     * throws RemoteException if error occurs in RMI
     */
    public void resetSimulation() throws RemoteException;

    /**
     * Reposition the simulation to a new time mark.  After repositioning,
     * all incidents and events will exist in the simulation data.
     * 
     * @param time The number of seconds to 'fast-forward' the simulation to.
     * @throws RemoteException
     */
    public void gotoSimulationTime(long time) throws RemoteException;
    
    /**
     * Loads a new script into the simulation.  Any existing script data
     * is replaced by the new script data.
     *
     * @param scriptFile The script file to read into the coordinator
     *
     * @throws RemoteException if there is a problem in the RMI communication
     * @throws ScriptException if there is an error in reading the script file
     */
    public void loadScriptFile(File scriptFile) throws RemoteException, ScriptException;
    
    /**
     * Manually triggers an incident that has not yet occured in the simulation.
     * 
     * @param incidentNumber Integer value of the incident number that is to be
     * manually triggered.
     *
     * @throws RemoteException if there is an error in the RMI communication.
     * @throws ScriptException if the simulation has not been started, or if a
     * script has not been loaded.
     */
    public void triggerIncident(Integer incidentNumber) throws RemoteException, ScriptException;

    /**
     * Deletes an incident that has not yet occured from the simulation.
     *
     * @param incidentNumber Integer value of the incident number that is to be manually triggered.
     *
     * @throws RemoteException if there is an error in the RMI communication.
     * @throws ScriptException if the incident that is being deleted has already occurred.
     */
    public void deleteIncident(Integer incidentNumber) throws RemoteException, ScriptException;

    /**
     * Reschedule an incident that has not yet occured in the simulation.
     *
     * @param incidentNumber Integer value of the incident number that is to be rescheduled.
     * @param newTime Value (in seconds) of the new time the incident is to be scheduled for.
     *
     * @throws RemoteException if there is an error in the RMI communication.
     * @throws ScriptException if the new time for this incident has already passed in the simulation
     */
    public void rescheduleIncident(Integer incidentNumber, long newTime)
        throws RemoteException, ScriptException;

    /**
     * Add a new incident into the current simulation.
     *
     * @param newIncident Incident that is to be added to the simulation.
     * @throws RemoteException if there is an error in the RMI communication.
     */
    public void addIncident(Incident newIncident) throws RemoteException;

    /**
     * Applies a new set of diversions for a specific CMS.  The new CMSInfo
     * object is passed on to the CMSDiversionDB and updated information sent
     * to the Paramics Communicator.
     *
     * @param theDiversion The CMSDiversion object containing the diversion information for a CMS.
     * @throws RemoteException if there is an error in the RMI communication
     */
    public void applyDiversions(CMSInfo theDiversion) throws RemoteException;
    
}
