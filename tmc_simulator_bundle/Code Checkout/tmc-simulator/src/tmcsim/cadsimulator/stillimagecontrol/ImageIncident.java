package tmcsim.cadsimulator.stillimagecontrol;


/**
 * ImageIncident is a container class used within still image control.  This 
 * class is used by the ImageController to determine which DVD title is to be 
 * played when an incident is toggled.
 *
 * @author Matthew Cechini
 * @version
 */
public class ImageIncident {

    /** The incident log number. */
    public int   incidentNumber;

    /** Filename of the image to be shown for this range. */
    public String fileName;
    
    /**  
     * Construct.  Initialize member data with parameter values.
     *
     * @param incident Incident number.
     * @param file Filename if associated image file.
     */ 
    public ImageIncident(int incident, String file) {
        incidentNumber = incident;
        fileName       = file;
    }
        
}
