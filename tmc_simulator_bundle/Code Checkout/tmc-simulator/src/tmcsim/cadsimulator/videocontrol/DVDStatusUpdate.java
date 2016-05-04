package tmcsim.cadsimulator.videocontrol;

/**
 * DVDStatusUpdate is a container class used to notify observers of a DVD
 * Controller when a status update occurs.  The DVD's connection is used to 
 * uniquely identify a DVD player.  So this connection information String should
 * always be the same for all updates.  The Abstract DVDController defines a 
 * getConnectionInfo() method that may be used for this purpise.  The isConnected 
 * should also always be set.  However, the Throwable object may be unused (null)
 * if the DVDStatusUpdate is not reporting an exception.  Otherwise, the Throwable 
 * will be the cause of the status update.
 * 
 * @author Matthew Cechini
 * @version
 */
public class DVDStatusUpdate {

    /** DVD player connection info. */
    public String connectionInfo = null;
    
    /** Boolean flag to designate whether the DVD Controller is connected. */
    public boolean isConnected = false;
    
    /** An Exception that has been thrown from the DVD controller. (Optional) */
    public Throwable exception = null;

    /**
     * Constructor.
     * 
     * @param connInfo DVD connection info.
     * @param connected Boolean connected flag.
     * @param e Exception caught in DVD Controller.
     */
    public DVDStatusUpdate(String connInfo, boolean connected, Throwable e)
    {
        connectionInfo    = connInfo;
        isConnected       = connected;
        exception         = e;
        
    }

    /**
     * Constructor.
     * 
     * @param connInfo DVD connection info.
     * @param connected Boolean connected flag.
     */
    public DVDStatusUpdate(String connInfo, boolean connected)
    {
        connectionInfo    = connInfo;
        isConnected       = connected;
        exception         = null;
        
    }
    
}
