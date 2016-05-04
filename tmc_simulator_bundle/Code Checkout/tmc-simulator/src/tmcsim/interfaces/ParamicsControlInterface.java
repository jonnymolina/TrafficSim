package tmcsim.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import tmcsim.common.SimulationException;

/**
 * RMI interface to provide methods to control the Paramics connection.
 * Provided functionality allows the connection to the remote Paramics
 * Communicator to be established and dropped.  Also, the 
 * loadParamicsNetwork() method sends a command to Paramics to begin
 * loading a traffic network.
 * 
 * @author Matthew Cechini
 * @version
 */
public interface ParamicsControlInterface extends Remote {

    /**
     *Establishes a connection to the remote Paramics Communicator.
     *
     * @throws RemoteException if there is an error in the RMI communication.
     */
    public void connectToParamics() throws RemoteException;


    /**
     * Drops the connection to the remote Paramics Communicator.
     *
     * @throws RemoteException if there is an error in the RMI communication.
     */
    public void disconnectFromParamics() throws RemoteException;


    /**
     * Sends a command to Paramics to begin loading a traffic network.
     *
     * @param networkID The unique indentifier for the network load request.
     * @throws RemoteException if there is an error in the RMI communication.
     * @throws SimulationException if there is an error loading the Paramics network.
     */
    public void loadParamicsNetwork(int networkID) throws RemoteException, SimulationException;
    
}
