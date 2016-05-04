package tmcsim.cadsimulator.videocontrol;


/**
 * DVDRange is a container class used within video control.  It contains a 
 * minimum and maximum speed range, an associated title number and duration for
 * that title.  The DVDController uses the DVDRange to determine
 * which DVD title is to be played according to current traffic speed.
 *
 * @author Matthew Cechini
 * @version
 */
public class DVDRange {

    /** Minimum speed for this range. */
    public float minSpeed;

    /** Maximum speed for this range. */
    public float maxSpeed;

    /** DVD title to play for this range. */
    public int   dvdTitle;

    /**  
     * Constructor.  Initialize member data with parameter values.
     *
     * @param min Minimum speed in range.
     * @param max Maximum speed in range.
     * @param title Title to play for range.
     * @param duration Length (in seconds) of range title.
     */
    public DVDRange(float min, float max, int title) {
        minSpeed      = min;
        maxSpeed      = max;
        dvdTitle      = title;
    }

    /** 
     * Tests if the parameter speed is >= the minimum speed and 
     * < the maximum speed for this range.
     *
     * @param speed Speed value to test for inclusion in range.
     * @return True if the parameter is within the range, false if not.
     */ 
    public boolean isWithin(float speed) {
        return speed >= minSpeed && speed < maxSpeed;
    }               

    public boolean equals(DVDRange range) {
        return dvdTitle == range.dvdTitle &&
               minSpeed == range.minSpeed &&
               maxSpeed == range.maxSpeed;
    }
}
