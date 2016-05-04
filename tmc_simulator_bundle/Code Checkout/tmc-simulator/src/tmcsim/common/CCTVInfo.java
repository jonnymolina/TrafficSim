package tmcsim.common;

import java.io.Serializable;

/**
 * The CCTVInfo object holds information that is used during a simulation to 
 * toggle a CCTV camera (referenced by the ID and direction) to begin or 
 * stop showing incident data.
 * 
 * @author
 * @version 
 */
@SuppressWarnings("serial")
public class CCTVInfo implements Serializable {

    /** Paramics CCTV unique id. */
    public int cctv_id;

    /** Direction of CCTV. */
    public CCTVDirections direction;

    /** Flag to designate whether the CCTV is being toggled on(true) or off(false). */
    public boolean toggle;
    

    /**
     * Constructor.
     */
    public CCTVInfo() {
        cctv_id   = 0;
        direction = CCTVDirections.NORTH;
        toggle    = false;
    }
    
}
