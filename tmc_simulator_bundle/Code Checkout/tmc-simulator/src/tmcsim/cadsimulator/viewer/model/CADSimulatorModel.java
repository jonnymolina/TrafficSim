package tmcsim.cadsimulator.viewer.model;
import java.util.Observer;
import tmcsim.cadsimulator.videocontrol.DVDStatusUpdate;
import tmcsim.cadsimulator.videocontrol.DVDTitleUpdate;
import tmcsim.common.CADEnums;

/**
 * CADSimulatorModel represents the state of the simulation status panel and the media
 * status panel for the CADSimulator.
 * @author Jonathan Molina
 */
public class CADSimulatorModel
{
    private SimulationStatusPanelModel simStatusPanelModel;
    private MediaStatusPanelModel mediaStatusPanelModel;
    
    /**
     * Constructs an instance of this class and the corresponding model classes
     * representing the simulation status panel and media status panel.
     */
    public CADSimulatorModel()
    {
        simStatusPanelModel = new SimulationStatusPanelModel();
        mediaStatusPanelModel = new MediaStatusPanelModel();
    }
    
    /**
     * Increment the number of connected CAD clients.
     * @see SimulationStatusPanelModel
     */
    public void connectClient()
    {
        simStatusPanelModel.connectClient();
    }
    
    /**
     * Decrement the number of connected CAD clients. Does not go less than 0.
     * @see SimulationStatusPanelModel
     */
    public void disconnectClient()
    {
        simStatusPanelModel.disconnectClient();
    }
    
    /**
     * Get the current number of connected CAD clients.
     * @return the number of connected CAD clients
     * @see SimulationStatusPanelModel
     */
    public int getNumClients()
    {
        return simStatusPanelModel.getNumClients();
    }
    
    /**
     * Check if the Simulation Manager is connected.
     * @return true if the Simulation Manager is connected, false otherwise
     * @see SimulationStatusPanelModel
     */
    public boolean isSimManagerConnected()
    {
        return simStatusPanelModel.isSimManagerConnected();
    }
    
    /**
     * Set the current paramics network.
     * @param networkID the network ID for the Paramics network
     * @see SimulationStatusPanelModel
     */
    public void setParamicsNetworkLoaded(String networkID)
    {
        simStatusPanelModel.setParamicsNetworkLoaded(networkID);
    }
    
    /**
     * Set the current status of the connection to the Paramics.
     * @param newStatus the Paramics status
     * @see SimulationStatusPanelModel
     */
    public void setParamicsStatus(CADEnums.PARAMICS_STATUS newStatus)
    {
        simStatusPanelModel.setParamicsStatus(newStatus);
    }
    
    /**
     * Set the current script status.
     * @param newStatus the new script status
     * @see SimulationStatusPanelModel
     */
    public void setScriptStatus(CADEnums.SCRIPT_STATUS newStatus)
    {
        simStatusPanelModel.setScriptStatus(newStatus);
    }
    
    /**
     * Set the current status of the connection to the Simulation Manager.
     * @param connection true for connected, false otherwise
     * @see SimulationStatusPanelModel
     */
    public void setSimManagerStatus(boolean connection)
    {
        simStatusPanelModel.setSimManagerStatus(connection);
    }

    /**
     * Set the current simulation time as a long value in seconds.
     * @param seconds the current simulation time
     * @see SimulationStatusPanelModel
     */
    public void setTime(long seconds)
    {
        simStatusPanelModel.setTime(seconds);
    }
    
    // The following may have empty method bodies
    /**
     * Updates the current DVDInfoPanel with the status update.
     * If a panel does not current exist, create one and add
     * a new tab.
     * 
     * @param update DVD status update.
     * @see MediaStatusPanelModel
     */
    public void updateDVDStatus(DVDStatusUpdate update)
    {
        mediaStatusPanelModel.updateDVDStatus(update);
    }
    
    /**
     * Updates the current DVDInfoPanel with the title update.
     * If a panel does not current exist, create one and add
     * a new tab.
     * 
     * @param update DVD title update.
     * @see MediaStatusPanelModel
     */
    public void updateDVDTitle(DVDTitleUpdate update)
    {
        mediaStatusPanelModel.updateDVDTitle(update);
    }
    
    /**
     * Connects the given Observer to the Observable models that this class represents.
     * @param observer the observer
     */
    public void addObserver(Observer observer)
    {
        simStatusPanelModel.addObserver(observer);
        mediaStatusPanelModel.addObserver(observer);
    }
 }