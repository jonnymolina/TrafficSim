package tmcsim.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import tmcsim.cadmodels.CMSInfo;
import tmcsim.client.cadclientgui.data.Incident;
import tmcsim.client.cadclientgui.data.IncidentEvent;
import tmcsim.common.CADEnums.PARAMICS_STATUS;
import tmcsim.common.CADEnums.SCRIPT_STATUS;

/**
 * RMI Interface providing methods to get the current status information
 * for the simulation.  This data includes a list of all Incidents 
 * loaded into the simulation, all IncidentEvents that have
 * previously occured, and the current simulation time.  The status of the 
 * Script and Paramics connection, along with the current Paramics network
 * loaded, are also through methods in this interface.  Diversion information 
 * provided includes a list of all CMS ID strings and the CMSInfo objects 
 * for these IDs.
 * 
 * @author Matthew Cechini
 * @version
 */
public interface SimulationStatusInterface extends Remote {

    /**
     * Gets the list of all Incident objects in the current simulation.
     *
     * @return Vector of Incident current objects.
     * @throws RemoteException if there is a problem in the RMI communication.
     */
    public Vector<Incident> getIncidentList() throws RemoteException;

    /**
     * Gets a map of IncidentEvents that have occured in this simulation.  The
     * map keys are the log numbers for triggered Incidents.  Map values are 
     * Vectors of the IncidentEvents that are triggered for each Incident.
     *
     * @return Map of IncidentEvent objects for the corresponding Incidents.
     * @throws RemoteException if there is a problem in RMI Communication.
     */
    public TreeMap<Integer, Vector<IncidentEvent>> getTriggeredEvents() throws RemoteException;

    /**
     * Gets the current simulation time (in seconds).
     *
     * @return Value of current simulation time(in seconds).
     * @throws RemoteException if there is a problem in RMI Communication
     */
    public long getCurrentSimulationTime() throws RemoteException;

    /**
     * Get the current status of the script.  
     *
     * @return SCRIPT_STATUS enumeration value.
     * @throws RemoteException if there is a problem in RMI Communication.
     * @see SCRIPT_STATUS
     */
    public SCRIPT_STATUS getScriptStatus() throws RemoteException;

    /**
     * Get the current status of the paramics connection.  
     *
     * @return PARAMICS_STATUS enumeration value.
     * @throws RemoteException if there is a problem in RMI Communication.
     * @see PARAMICS_STATUS
     */
    public PARAMICS_STATUS getParamicsStatus() throws RemoteException;

    /**
     * Get the value of the Paramics network that is loaded.
     *
     * @return Value of the Network ID loaded.  Returns -1 if no network is loaded.  *
     * @throws RemoteException if there is an error in the RMI communication.
     */
    public int getParamicsNetworkLoaded() throws RemoteException;

    /**
     * Gets the current list of CMSInfo ID strings.
     *
     * @return Set of CMSInfo ID strings.
     * @throws RemoteException if there is an error in the RMI communication
     */
    public TreeSet<String> getCMSIDs() throws RemoteException;
    
    /**
     * Gets the current CMSInfo diversion object for the parameter unique
     * CMS ID string.
     * @param theCMSID A unique CMS ID string.
     * @return The CMSInfo object found for the parameter ID.  Returns null
     *         if an invalid ID string is given.
     * @throws RemoteException if there is an error in the RMI communication
     */
    public CMSInfo getCMSDiversionInfo(String theCMSID) throws RemoteException;

    
}
