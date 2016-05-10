package tmcsim.cadsimulator.viewer.model;

import java.util.Observable;
import tmcsim.common.CADEnums.PARAMICS_STATUS;
import tmcsim.common.CADEnums.SCRIPT_STATUS;

/**
 * The class that models the simulation status panel.
 *
 * @author Jonathan Molina
 */
public class SimulationStatusPanelModel extends Observable
{
    /**
     * Count of how many CAD clients have connected.
     */
    private int numClientsConnected;
    /**
     * Connected to the Simulation Manager
     */
    private boolean simManagerConnected;
    /**
     * Current simulation time
     */
    private long timeSegment;
    /**
     * Current Paramics connection status
     */
    private PARAMICS_STATUS paramicsStatus;
    /**
     * Current simulation status
     */
    private SCRIPT_STATUS scriptStatus;
    /**
     * The paramics network
     */
    private String networkLoaded;

    /**
     * Instantiates this model class.
     */
    public SimulationStatusPanelModel()
    {
        numClientsConnected = 0;
        simManagerConnected = false;
        timeSegment = 0;
        paramicsStatus = PARAMICS_STATUS.DISCONNECTED;
        scriptStatus = SCRIPT_STATUS.NO_SCRIPT;
        networkLoaded = "None";
    }

    /**
     * Increment the number of connected CAD clients.
     */
    public void connectClient()
    {
        numClientsConnected++;
        setChanged();
        notifyObservers();
    }

    /**
     * Decrement the number of connected CAD clients. Does not go less than 0.
     */
    public void disconnectClient()
    {
        // don't go below 0
        if (numClientsConnected > 0)
        {
            numClientsConnected--;
        }
        setChanged();
        notifyObservers();
    }
    
    /**
     * Set the current status of the connection to the Simulation Manager.
     * @param connection true for connected, false otherwise
     */
    public void setSimManagerStatus(boolean connection)
    {   
        // check for change
        simManagerConnected = connection;
        setChanged();
        notifyObservers();
    }
    
    /**
     * Set the current simulation time as a long value in seconds.
     * @param seconds the current simulation time
     */
    public void setTime(long seconds)
    {
        timeSegment = seconds;
        setChanged();
        notifyObservers();
    }
    
    /**
     * Set the current script status.
     * @param newStatus the new script status
     */
    public void setScriptStatus(SCRIPT_STATUS newStatus)
    {
        scriptStatus = newStatus;
        setChanged();
        notifyObservers();
    }
    
    /**
     * Set the current status of the connection to the Paramics.
     * @param newStatus the Paramics status
     */
    public void setParamicsStatus(PARAMICS_STATUS newStatus)
    {
        paramicsStatus = newStatus;
        setChanged();
        notifyObservers();
    }
    
    /**
     * Set the current paramics network.
     * @param networkID the network ID for the Paramics network
     */
    public void setParamicsNetworkLoaded(String networkID)
    {
        networkLoaded = networkID;
        setChanged();
        notifyObservers();
    }

    /**
     * Get the current number of connected CAD clients.
     * @return the number of connected CAD clients
     */
    public int getNumClients()
    {
        return numClientsConnected;
    }

    /**
     * Check if the Simulation Manager is connected.
     * @return true if the Simulation Manager is connected, false otherwise
     */
    public boolean isSimManagerConnected()
    {
        return simManagerConnected;
    }

    /**
     * Get the current simulation time in seconds.
     * @return a long representing the current simulation time in seconds
     */
    public long getTimeSegment()
    {
        return timeSegment;
    }
    
    /**
     * Get the Paramics status.
     * @return the enum representing the Paramics status
     */
    public PARAMICS_STATUS getParamicsStatus()
    {
        return paramicsStatus;
    }

    /**
     * Get the script status.
     * @return the enum representing the script status
     */
    public SCRIPT_STATUS getScriptStatus()
    {
        return scriptStatus;
    }
    
    /**
     * Get the network ID of the Paramics network.
     * @return the network ID of the Paramics network
     */
    public String getNetworkLoaded()
    {
        return networkLoaded;
    }
}
