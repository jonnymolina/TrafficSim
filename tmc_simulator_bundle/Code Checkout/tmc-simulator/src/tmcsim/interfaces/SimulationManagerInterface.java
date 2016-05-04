package tmcsim.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import tmcsim.client.cadclientgui.data.Incident;
import tmcsim.client.cadclientgui.data.IncidentEvent;
import tmcsim.common.CADEnums.PARAMICS_STATUS;
import tmcsim.common.CADEnums.SCRIPT_STATUS;


/**
 * RMI Interface providing methods for the SimulationManager to be updated
 * with current simulation data.  The Simulation Manager is notified
 * when the simulation time changes, new Incidents and IncidentEvents
 * occur, new Incidents are added to the simulation, and when the Script 
 * and Paramics connection statuses change.
 *
 * @author Matthew Cechini
 * @version $Revision: 1.3 $ $Date: 2006/06/06 20:46:41 $
 */
public interface SimulationManagerInterface extends Remote {

    /**
     * Notifies the SimulationManager when current simulation time changes.
     *
     * @param theTime The value (in seconds) of the current simulation time.
     * @throws RemoteException if there is an error in the RMI communication.
     */
    public void tick(long theTime) throws RemoteException;
    
    /**
     * Notifies the SimulationManager that a new IncidentEvent has occured for
     * a current Incident.  
     *
     * @param logNumber The current log number being updated.
     * @param theEvent The most recently occured incident event.
     * @throws RemoteException if there is an error in the RMI communication
     */
    public void eventOccured(Integer logNumber, IncidentEvent theEvent) throws RemoteException;

    /**
     * Notifies the SimulationManager that an Incident has been added to the 
     * simulation.
     * 
     * @param newIncident Incident that has been added.
     * @throws RemoteException if there is an error in the RMI communication
     */
    public void incidentAdded(Incident newIncident) throws RemoteException;

    /**
     * Notifies the SimulationManager that an incident has begun.  This will
     * occur if the user triggers an incident, or the incident occurs according
     * to its assigned simulation time.  
     * 
     * @param logNumber The log number of the Incident that has started.
     * @throws RemoteException if there is an error in the RMI communication
     */
    public void incidentStarted(Integer logNumber) throws RemoteException;
    
    /**
     * Notifies the SimulationManager that an incident has been removed
     * from the simulation.
     * 
     * @param logNumber The log number of the Incident that has been removed.
     * @throws RemoteException if there is an error in the RMI communication
     */ 
    public void incidentRemoved(Integer logNumber) throws RemoteException;

    /**
     * Notifies the SimulationManager that the current status of the Script
     * has changed.  Possible status values are determined in the SCRIPT_STATUS 
     * enumeration.  
     * 
     * @see SCRIPT_STATUS
     * @param newStatus Updated SCRIPT_STATUS.
     * @throws RemoteException if there is an error in the RMI communication
     */
    public void setScriptStatus(SCRIPT_STATUS newStatus) throws RemoteException;

    /**
     * Notifies the SimulationManager that the current status of the Paramics
     * connection has changed.  Possible status values are determined in the
     * PARAMICS_STATUS enumeration.  
     * 
     * @see PARAMICS_STATUS
     * @param newStatus Updated PARAMICS_STATUS.
     * @throws RemoteException if there is an error in the RMI communication
     */
    public void setParamicsStatus(PARAMICS_STATUS newStatus) throws RemoteException;

}