package tmcsim.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import tmcsim.client.cadclientgui.data.CADData;

/**This interface exists for CADClient to communicate with Coordinator through the RMI. RMI requires that objects sent through implement
 * an interface that extends Remote.
 * @author Vincent
 *
 */

public interface CADClientInterface extends Remote  {
    
    public void refresh() throws RemoteException;
    
}
