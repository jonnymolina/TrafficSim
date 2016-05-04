package tmcsim.cadsimulator.videocontrol;

/**
 * DVDIncident is a container class used within video control.  This class is 
 * used by the DVDController to determine which DVD title is to be played
 * when an incident is toggled.
 *
 * @author Matthew Cechini
 * @version
 */
public class DVDIncident {

    /** The incident log number. */
    public int   incidentNumber;

    /** The DVD title to play for this incident. */
    public int   dvdTitle;

    /**  
     * Construct.  Initialize member data with parameter values.
     *
     * @param incident Incident number.
     * @param title DVD title number. 
     */
    public DVDIncident(int incident, int title) {
        incidentNumber = incident;
        dvdTitle       = title;
    }
    
}
