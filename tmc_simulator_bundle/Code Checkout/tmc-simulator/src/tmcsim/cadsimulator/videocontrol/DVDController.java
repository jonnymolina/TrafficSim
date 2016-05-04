package tmcsim.cadsimulator.videocontrol;

import java.io.IOException;
import java.util.Observable;
import java.util.Vector;

/**
 * DVDController is an abstract class used for controlling a remote DVD player.
 * All player specific control methods must be overloaded according to the 
 * requirements of that device.  This base class handles starting and repeating 
 * range or incident titles according to current speed updates or incident toggles. 
 *
 * The addRange() and addIncident() methods are used to register more titles with
 * the DVDController class.  The updatePlayer() and toggleIncident() methods are
 * used to control which titles are being played.  A title will continuously
 * repeat until a new title is chosen through one of these methods.
 *
 * @author Matthew Cechini
 * @version
 */
public abstract class DVDController extends Observable {
    
    /** Number of consecutive requests for change until the title will change. */
    private static final int CHANGE_TOLERANCE = 2;
    
    /** 
     *  Boolean flag to designate whether a connection has been established with
     *  the DVD player.
     */
    protected boolean isConnected = false;
    
    /** Vector of all DVDRanges in the current simulation.  */
    protected Vector<DVDRange> ranges = null;
    
    /**  Vector of all DVDIncidents in the current simulation. */
    protected Vector<DVDIncident> incidents = null;
    
    /** Current range being played.  Null if none have played. */
    protected DVDRange currentRange = null;
    
    /** Current incident being played. */
    protected DVDIncident currentIncident = null;
    
    /** Boolean flag to designate whether an incident is currently playing. */
    protected boolean isPlayingIncident = false;
    
    /** 
     * DVDRange object to cache the last "new range" that the controller
     * has chosen as a result of a speed update.  This allows for a tolerance
     * value to be used to control frequent title changes.  
     */
    protected DVDRange changeRange;
    
    /** 
     * Count value to count the number of consecutive updates that
     * result in the same DVDRange. 
     */
    protected int changeRangeCounter;
    
    /**
     * Constructor.  Initialize lists and title repeat timer.
     */
    public DVDController() {
        ranges    = new Vector<DVDRange>();
        incidents = new Vector<DVDIncident>();
        
        changeRange        = null;
        changeRangeCounter = 0;
    }
    
    /**  
     * Add a new DVDRange to the local list.
     * 
     * @param newRange DVDRange to add.
     */
    public void addRange(DVDRange newRange) {
        ranges.add(newRange);
    }
    
    /**  
     * Add a new DVDIncident to the local list.
     * 
     * @param newRange DVDIncident to add.
     */ 
    public void addIncident(DVDIncident newIncident) {
        incidents.add(newIncident);
    }
    
    /**
     * Toggle the dvd player to start or stop playing an incident track.  The
     * boolean parameter is used to designate whether the incident is being
     * toggled to start(true) or stop (false).  If the toggle flag is true,
     * then the DVDIncident with log number equal to the parameter log_num
     * will be played.  The currentIncident and isPlayeingIncident member data
     * objects are updated.  
     *
     * If the toggle flag is false, and the incident being toggled is playing,
     * then it is stopped.
     *
     * @param log_num Incident log number to toggle.
     * @param toggle Boolean flag.  True = start incident, false = stop incident.
     * @throws Exception if the method is unable to toggle the incident.
     */
    public void toggleIncident(int log_num, boolean toggle) throws Exception {
        if(toggle) {
            for(DVDIncident incident : incidents) {
                if(incident.incidentNumber == log_num) {
                    currentIncident   = incident;                   
                    isPlayingIncident = true;
                    
                    playCurrentTitle();
                }
            }
        }
        else if(currentIncident != null && currentIncident.incidentNumber == log_num) {
            isPlayingIncident = false;          
            
            playCurrentTitle();
        }
        else {
            throw new Exception("DVDController: Unable to toggle incident #" + log_num);
        }
    }
    
    /**
     * Update this DVD player with a new traffic speed after the tolerance of
     * range changes has been met.  If the parameter speed falls within a 
     * different DVDRange than is being played, remember the new range.  If this 
     * new range was not detected during the last update, reset the change 
     * counter to 0.  If this range was detected during the last update, 
     * increment the change counter.  If the change counter is greater than
     * the tolerance value specified, set the current range to the new range, 
     * reset the change counter, and set the updated flag to true.  If the range
     * has been updated, and an incident is not being shown, return true.  Else
     * return false.
     *
     * @param newSpeed New traffic speed for this DVD's camera location.
     * @return True if a new DVDRange is to be played, false if not.
     */
    public boolean updatePlayer(float newSpeed) {
        
        boolean updated   = false;
        DVDRange newRange = null;
        
        for(DVDRange range : ranges) {
            if(range.isWithin(newSpeed)) {
                if(currentRange == null || !currentRange.equals(range)) {
                    newRange = range;
                    break;
                }
            }
        }
        
        if(newRange == null) {
            //do nothing
        }
        else if(changeRange == null || !changeRange.equals(newRange)) {
            changeRange = newRange;
            changeRangeCounter = 0;
        }
        else {
            changeRangeCounter++;
        }
        
        if(currentRange == null ||
           changeRangeCounter >= CHANGE_TOLERANCE) 
        {
            changeRangeCounter = 0;
            currentRange       = changeRange;
            updated            = true;
        }
        
        
        //even if the currentRange is updated, an incident trumps this
        return updated & !isPlayingIncident;
    }
    
    /**
     * This method starts a new title.  If the DVD Controller is playing an incident,
     * then the incident title will be started.  Else, if a DVD range has been found, 
     * then it's title will be started in repeat mode.  
     *
     * @throws Exception if the method is not able to play the current title.
     */
    public void playCurrentTitle() throws Exception {
        
        if(!isConnected) {
            throw new Exception("Cannot play title, connection has not been established to DVD Controller");
        }
        else if(isPlayingIncident) {
            repeatTitle(currentIncident.dvdTitle);

            setChanged();
            notifyObservers(new DVDTitleUpdate(
                    getConnectionInfo(), currentRange, 
                    currentIncident, isPlayingIncident, false));            
        }       
        else if(currentRange != null) {
            repeatTitle(currentRange.dvdTitle);

            setChanged();
            notifyObservers(new DVDTitleUpdate(
                    getConnectionInfo(), currentRange, 
                    currentIncident, isPlayingIncident, false));        
        }

    }
    
    public abstract void setConnectionInfo(String host, int port);
    public abstract String getConnectionInfo();
    public abstract boolean isConnected();
    public abstract void connect() throws IOException; 
    public abstract void disconnect();  

    
    public abstract void open() throws IOException;
    public abstract void close() throws IOException;
    
    public abstract void start() throws IOException;
    public abstract void play() throws IOException;
    public abstract void playChapter(int chapter) throws IOException;
    public abstract void playTitle(int title) throws IOException;
    public abstract void repeatTitle(int title) throws IOException;
    public abstract void pause() throws IOException;
    public abstract void still() throws IOException;
        
    public abstract void stepForward() throws IOException;
    public abstract void stepReverse() throws IOException;
    public abstract void scanForward() throws IOException;
    public abstract void scanReverse() throws IOException;
    public abstract void scanStop() throws IOException;
    
    //multi-speed forward/reverse
    
    //speed
    //search
    //search&play
    
    //frame
    
    //time
    //track
    
}