package tmcsim.cadsimulator.videocontrol;

/**
 * DVDTitleUpdate is a container class used to notify observers of a DVD Controller when
 * a title update occurs. The DVD's connection is used to uniquely identify a DVD player.
 * So this connection information String should always be the same for all updates. The
 * Abstract DVDController defines a getConnectionInfo() method that may be used for this
 * purpise. The data members of this object are used to hold the current state of the DVD
 * player when the update occurs.
 *
 * @author Matthew Cechini
 * @version
 */
public class DVDTitleUpdate
{
    /**
     * DVD player's connection info.
     */
    private String connectionInfo = null;
    /**
     * Current range being played. Null if none have played.
     */
    private DVDRange currentRange = null;
    /**
     * Current incident being played.
     */
    private DVDIncident currentIncident = null;
    /**
     * Boolean flag to designate whether an incident is currently playing.
     */
    private boolean isPlayingIncident = false;
    /**
     * Boolean flag to designate whether the title is being repeated(true) or played for
     * the first time(false).
     */
    private boolean isRepeat = false;

    /**
     * Constructor.
     *
     * @param connInfo DVD player connection info.
     * @param range Current range being played. (may be null)
     * @param incident Current Incident being played. (may be null)
     * @param playingInc Playing incident flag.
     * @param repeat Title repeated flag.
     */
    public DVDTitleUpdate(String connInfo, DVDRange range,
            DVDIncident incident, boolean playingInc, boolean repeat)
    {
        connectionInfo = connInfo;
        currentRange = range;
        currentIncident = incident;
        isPlayingIncident = playingInc;
        isRepeat = repeat;

    }

    /**
     * Returns the connection info.
     * @return the connection info
     */
    public String getConnectionInfo()
    {
        return connectionInfo;
    }

    /**
     * Returns the current range
     * @return the current range
     */
    public DVDRange getCurrentRange()
    {
        return currentRange;
    }

    /**
     * Returns the current incident.
     * @return the current incident
     */
    public DVDIncident getCurrentIncident()
    {
        return currentIncident;
    }

    /**
     * Returns whether the incident is playing
     * @return true if playing, false if not
     */
    public boolean isPlayingIncident()
    {
        return isPlayingIncident;
    }

    /**
     * Returns whether this is on repeat.
     * @return true if on repeat, false if not
     */
    public boolean isRepeat()
    {
        return isRepeat;
    }
}
