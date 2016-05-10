package tmcsim.cadsimulator.videocontrol;

import java.io.IOException;
import java.util.Observable;
import java.util.Vector;

/**
 * DVDController is an abstract class used for controlling a remote DVD player. All
 * player specific control methods must be overloaded according to the requirements of
 * that device. This base class handles starting and repeating range or incident titles
 * according to current speed updates or incident toggles.
 *
 * The addRange() and addIncident() methods are used to register more titles with the
 * DVDController class. The updatePlayer() and toggleIncident() methods are used to
 * control which titles are being played. A title will continuously repeat until a new
 * title is chosen through one of these methods.
 *
 * @author Matthew Cechini
 * @version
 */
public abstract class DVDController extends Observable
{
    /**
     * Number of consecutive requests for change until the title will change.
     */
    private static final int kChangeTolerance = 2;
    /**
     * Boolean flag to designate whether a connection has been established with the DVD
     * player.
     */
    protected boolean isConnected = false;
    /**
     * Vector of all DVDRanges in the current simulation.
     */
    protected Vector<DVDRange> ranges = null;
    /**
     * Vector of all DVDIncidents in the current simulation.
     */
    protected Vector<DVDIncident> incidents = null;
    /**
     * Current range being played. Null if none have played.
     */
    protected DVDRange currentRange = null;
    /**
     * Current incident being played.
     */
    protected DVDIncident currentIncident = null;
    /**
     * Boolean flag to designate whether an incident is currently playing.
     */
    protected boolean isPlayingIncident = false;
    /**
     * DVDRange object to cache the last "new range" that the controller has chosen as a
     * result of a speed update. This allows for a tolerance value to be used to control
     * frequent title changes.
     */
    protected DVDRange changeRange;
    /**
     * Count value to count the number of consecutive updates that result in the same
     * DVDRange.
     */
    protected int changeRangeCounter;

    /**
     * Constructor. Initialize lists and title repeat timer.
     */
    public DVDController()
    {
        ranges = new Vector<DVDRange>();
        incidents = new Vector<DVDIncident>();

        changeRange = null;
        changeRangeCounter = 0;
    }

    /**
     * Add a new DVDRange to the local list.
     *
     * @param newRange DVDRange to add.
     */
    public void addRange(DVDRange newRange)
    {
        ranges.add(newRange);
    }

    /**
     * Add a new DVDIncident to the local list.
     *
     * @param newIncident DVDIncident to add.
     */
    public void addIncident(DVDIncident newIncident)
    {
        incidents.add(newIncident);
    }

    /**
     * Toggle the dvd player to start or stop playing an incident track. The boolean
     * parameter is used to designate whether the incident is being toggled to
     * start(true) or stop (false). If the toggle flag is true, then the DVDIncident with
     * log number equal to the parameter log_num will be played. The currentIncident and
     * isPlayeingIncident member data objects are updated.
     *
     * If the toggle flag is false, and the incident being toggled is playing, then it is
     * stopped.
     *
     * @param logNum Incident log number to toggle.
     * @param toggle Boolean flag. True = start incident, false = stop incident.
     * @throws Exception if the method is unable to toggle the incident.
     */
    public void toggleIncident(int logNum, boolean toggle) throws Exception
    {
        // starting incident
        if (toggle)
        {
            // for every incident
            for (DVDIncident incident : incidents)
            {
                // find the target incident
                if (incident.getIncidentNumber() == logNum)
                {
                    currentIncident = incident;
                    isPlayingIncident = true;

                    playCurrentTitle();
                }
            }
        }
        // stop incident
        else if (currentIncident != null 
                && currentIncident.getIncidentNumber() == logNum)
        {
            isPlayingIncident = false;

            playCurrentTitle();
        }
        else
        {
            throw new Exception("DVDController: Unable to toggle incident #" + logNum);
        }
    }

    /**
     * Update this DVD player with a new traffic speed after the tolerance of range
     * changes has been met. If the parameter speed falls within a different DVDRange
     * than is being played, remember the new range. If this new range was not detected
     * during the last update, reset the change counter to 0. If this range was detected
     * during the last update, increment the change counter. If the change counter is
     * greater than the tolerance value specified, set the current range to the new
     * range, reset the change counter, and set the updated flag to true. If the range
     * has been updated, and an incident is not being shown, return true. Else return
     * false.
     *
     * @param newSpeed New traffic speed for this DVD's camera location.
     * @return True if a new DVDRange is to be played, false if not.
     */
    public boolean updatePlayer(float newSpeed)
    {

        boolean updated = false;
        DVDRange newRange = null;
        // for every dvd range
        for (DVDRange range : ranges)
        {   // check for within speed
            if (range.isWithin(newSpeed))
            {   // set range
                if (currentRange == null || !currentRange.equals(range))
                {
                    newRange = range;
                    break;
                }
            }
        }
        // do nothing
        if (newRange == null)
        {
            //do nothing
        }
        // change the range
        else if (changeRange == null || !changeRange.equals(newRange))
        {
            changeRange = newRange;
            changeRangeCounter = 0;
        }
        else
        {
            changeRangeCounter++;
        }
        // update current range
        if (currentRange == null
                || changeRangeCounter >= kChangeTolerance)
        {
            changeRangeCounter = 0;
            currentRange = changeRange;
            updated = true;
        }


        //even if the currentRange is updated, an incident trumps this
        return updated & !isPlayingIncident;
    }

    /**
     * This method starts a new title. If the DVD Controller is playing an incident, then
     * the incident title will be started. Else, if a DVD range has been found, then it's
     * title will be started in repeat mode.
     *
     * @throws Exception if the method is not able to play the current title.
     */
    public void playCurrentTitle() throws Exception
    {
        // check connection
        if (!isConnected)
        {
            throw new Exception("Cannot play title, "
                    + "connection has not been established to DVD Controller");
        }
        // check incident playing
        else if (isPlayingIncident)
        {
            repeatTitle(currentIncident.getDvdTitle());

            setChanged();
            notifyObservers(new DVDTitleUpdate(
                    getConnectionInfo(), currentRange,
                    currentIncident, isPlayingIncident, false));
        }
        // check for current range
        else if (currentRange != null)
        {
            repeatTitle(currentRange.getDvdTitle());

            setChanged();
            notifyObservers(new DVDTitleUpdate(
                    getConnectionInfo(), currentRange,
                    currentIncident, isPlayingIncident, false));
        }

    }
    /**
     * Set the connection info.
     * @param host the host
     * @param port the port
     */
    public abstract void setConnectionInfo(String host, int port);

    /**
     * Get the connection info.
     * @return the connection information
     */
    public abstract String getConnectionInfo();

    /**
     * Get the connection status.
     * @return true if connected, false if not
     */
    public abstract boolean isConnected();

    /**
     * Establish a connection with the dvd player through socket communication.  
     * @throws IOException if communication with the dvd player fails.
     */
    public abstract void connect() throws IOException;

    /**
     * Disconnect from the dvd player.
     */
    public abstract void disconnect();

    /**
     * If the command is sent while the player is in the Park mode, the dvd
     * ejects and the player enters the Open mode. After the tray is ejected, 
     * player returns a completed status message.
     * @throws IOException if communication with the dvd player fails.
     */
    public abstract void open() throws IOException;

    /**
     * If the command is sent while the player door is open, the door 
     * closes then the player enters the Park mode. After the door closes, the 
     * player returns the completed status message.
     *
     * If the player is in any mode other than Open or if the player door is already 
     * closed, an error message is returned.
     * @throws IOException if communication with the dvd player fails.
     */
    public abstract void close() throws IOException;

    /**
     * If the command is sent while the player is in Open, Park or Reject 
     * mode, the player immediately enters Setup and the disc begins spinning up.
     * The player is ready for playback when the device reaches the beginning of
     * the program (DVD, CD or VCD disc pauses or stills at the first Track). The 
     * player returns the completed status when the disc pauses or stills. 
     *
     * If the player receives the command while playing a menu, the player returns 
     * an error message. However, if the disc program does not allow new 
     * commands once playback begins, the player ignores the command.
     * 
     * @throws IOException if communication with the dvd player fails.
     */
    public abstract void start() throws IOException;

    /**
     * Play.
     * @throws IOException if communication with the dvd player fails.
     */
    public abstract void play() throws IOException;

    /**
     * Play the chapter
     * @param chapter the chapter
     * @throws IOException if communication with the dvd player fails.
     */
    public abstract void playChapter(int chapter) throws IOException;

    /**
     * Play the title
     * @param title the title
     * @throws IOException if communication with the dvd player fails.
     */
    public abstract void playTitle(int title) throws IOException;

    /**
     * Repeat the title
     * @param title the title
     * @throws IOException if communication with the dvd player fails.
     */
    public abstract void repeatTitle(int title) throws IOException;

    /**
     * Pause.
     * @throws IOException if communication with the dvd player fails.
     */
    public abstract void pause() throws IOException;

    /**
     * Still.
     * @throws IOException if communication with the dvd player fails.
     */
    public abstract void still() throws IOException;

    /**
     * Step forward.
     * @throws IOException if communication with the dvd player fails.
     */
    public abstract void stepForward() throws IOException;

    /**
     * Step reverse.
     * @throws IOException if communication with the dvd player fails.
     */
    public abstract void stepReverse() throws IOException;

    /**
     * Scan forward.
     * @throws IOException if communication with the dvd player fails.
     */
    public abstract void scanForward() throws IOException;

    /**
     * Scan reverse.
     * @throws IOException if communication with the dvd player fails.
     */
    public abstract void scanReverse() throws IOException;

    /**
     * Scan stop.
     * @throws IOException if communication with the dvd player fails.
     */
    public abstract void scanStop() throws IOException;
    //multi-speed forward/reverse
    //speed
    //search
    //search&play
    //frame
    //time
    //track
}