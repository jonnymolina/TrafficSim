package tmcsim.interfaces;

import java.rmi.Remote;

import tmcsim.client.cadclientgui.data.CADData;

/**
 * The CoordinatorInterface extends from all interfaces that the
 * Coordinator implements in order to provide remote method functionality.
 * 
 * @author Matthew Cechini
 * @version
 */
public interface CoordinatorInterface extends SimulationControlInterface, 
    SimulationStatusInterface, ParamicsControlInterface, CADInterface, Remote {


}
