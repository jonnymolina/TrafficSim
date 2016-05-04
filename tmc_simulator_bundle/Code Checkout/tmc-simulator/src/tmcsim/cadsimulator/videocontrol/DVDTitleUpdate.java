package tmcsim.cadsimulator.videocontrol;

/**
 * DVDTitleUpdate is a container class used to notify observers of a DVD
 * Controller when a title update occurs.  The DVD's connection is used to 
 * uniquely identify a DVD player.  So this connection information String should
 * always be the same for all updates.  The Abstract DVDController defines a 
 * getConnectionInfo() method that may be used for this purpise.  The data members
 * of this object are used to hold the current state of the DVD player when the
 * update occurs.
 * 
 * @author Matthew Cechini
 * @version
 */
public class DVDTitleUpdate {

    /** DVD player's connection info. */
    public String connectionInfo = null;
    
    /** Current range being played.  Null if none have played. */
    public DVDRange currentRange = null;
    
    /** Current incident being played. */
    public DVDIncident currentIncident = null;
    
    /** Boolean flag to designate whether an incident is currently playing. */
    public boolean isPlayingIncident = false;
    
    /** 
     *  Boolean flag to designate whether the title is being repeated(true) or 
     *  played for the first time(false). 
     */ 
    public boolean isRepeat = false;
    
    /**
     * Constructor.
     * 
     * @param connInfo  DVD player connection info.
     * @param range Current range being played. (may be null)
     * @param incident Current Incident being played. (may be null)
     * @param playingInc Playing incident flag.
     * @param repeat Title repeated flag.
     */
    public DVDTitleUpdate(String connInfo, DVDRange range, 
            DVDIncident incident, boolean playingInc, boolean repeat)
    {
        connectionInfo    = connInfo;
        currentRange      = range;
        currentIncident   = incident;
        isPlayingIncident = playingInc;
        isRepeat          = repeat;
        
    }
}
