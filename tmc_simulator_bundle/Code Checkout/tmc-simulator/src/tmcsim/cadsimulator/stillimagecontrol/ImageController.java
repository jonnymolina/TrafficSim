package tmcsim.cadsimulator.stillimagecontrol;

import java.util.Observable;
import java.util.Vector;

import tmcsim.cadsimulator.managers.ATMSManager;

/**
 * ImageController is used for controlling which images are shown on the ATMS 
 * Server.  Communication with the ATMS server is performed through the
 * ATMSManager.  
 *
 * The addRange() and addIncident() methods are used to register more images with
 * the ImageController class.  The updateImage() and toggleIncident() methods are
 * used to control which images are being shown.  An image will remain showing 
 * until a new image is chosen through one of these methods. 
 *
 * @author Matthew Cechini
 * @version
 */
public class ImageController extends Observable  {
    
    /** Number of consecutive requests for change until the image will change. */
    private static final int CHANGE_TOLERANCE = 2;
    
    /** ATMS CCTV unique ID. */
    protected Integer atms_cctv_id      = null;

    /** Vector of ImageRange objects handled by this controller. */
    protected Vector<ImageRange> ranges = null;

    /** Vector of ImageIncident objects handled by this controller. */
    protected Vector<ImageIncident> incidents = null;
    
    /** Current range being shown.  Null if none have been shown. */
    protected ImageRange currentRange = null;

    /** Current incident iamge being shown.  Null if none have been shown. */
    protected ImageIncident currentIncident = null;
    
    /** Boolean flag determining whether an incident is currently being shown. */
    protected boolean isShowingIncident = false;
    
    /** 
     * ImageRange object to cache the last "new range" that the controller
     * has chosen as a result of a speed update.  This allows for a tolerance
     * value to be used to control frequent title changes.  
     */
    protected ImageRange changeRange;
    
    /** 
     * Count value to count the number of consecutive updates that
     * result in the same ImageRange. 
     */
    protected int changeRangeCounter;   
    
    /** Manager used to handle communication with the ATMS. */
    protected ATMSManager theATMSManager;
    
    /**
     * Constructor.
     * 
     * @param cctv_id ATMS CCTV ID.
     */
    public ImageController(Integer cctv_id, ATMSManager atmsManager) {
        theATMSManager = atmsManager;
        
        ranges       = new Vector<ImageRange>();
        incidents    = new Vector<ImageIncident>();
        atms_cctv_id = cctv_id;
        
        changeRange        = null;
        changeRangeCounter = 0;
    }
    
    /**  
     * Add a new ImageRange to the local list.
     * 
     * @param newRange ImageRange to add.
     */
    public void addRange(ImageRange newRange) {
        ranges.add(newRange);
    }
    
    /**  
     * Add a new ImageIncident to the local list.
     * 
     * @param newRange ImageIncident to add.
     */ 
    public void addIncident(ImageIncident newIncident) {
        incidents.add(newIncident);
    }
    
    /**
     * Toggle the image controller to begin or stop showing the image associated
     * with an incident.  The boolean parameter is used to designate whether the 
     * incident is being toggled to start(true) or stop (false).  
     * If the toggle flag is true, then the ImageIncident with log number equal 
     * to the parameter log_num will be shown.  The currentIncident and 
     * isShowingIncident member data objects are updated.  
     *
     * If the toggle flag is false, and the incident being toggled is shown,
     * then it is stopped.
     *
     * @param log_num Incident log number to toggle.
     * @param toggle Boolean flag.  True = start incident, false = stop incident.
     * @throws Exception if the method is unable to toggle the incident.
     */
    public void toggleIncident(int log_num, boolean toggle) throws Exception {
        if(toggle) {
            for(ImageIncident incident : incidents) {
                if(incident.incidentNumber == log_num) {
                    currentIncident   = incident;                   
                    isShowingIncident = true;
                    
                    showCurrentImage();
                }
            }
        }
        else if(currentIncident != null && currentIncident.incidentNumber == log_num) {
            isShowingIncident = false;

            showCurrentImage();
        }
        else {
            throw new Exception("ImageController: Unable to toggle incident #" + log_num);
        }
    }   
    
    /**
     * Update the image with a new traffic speed after the tolerance of
     * range changes has been met.  If the parameter speed falls within a 
     * different ImageRange than is being shown, remember the new range.  If this 
     * new range was not detected during the last update, reset the change 
     * counter to 0.  If this range was detected during the last update, 
     * increment the change counter.  If the change counter is greater than
     * the tolerance value specified, set the current range to the new range, 
     * reset the change counter, and set the updated flag to true.  If the range
     * has been updated, and an incident is not being shown, return true.  Else
     * return false.
     *
     * @param newSpeed New traffic speed for this Image's camera location.
     * @return True if a new ImageRange is to be shown, false if not.
     */
    public boolean updateImage(float newSpeed) {
        
        boolean updated    = false;
        ImageRange newRange = null;
        
        for(ImageRange range : ranges) {
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
        
        if(changeRangeCounter >= CHANGE_TOLERANCE) {
            changeRangeCounter = 0;
            currentRange       = changeRange;
            updated            = true;
        }
        
        
        //even if the currentRange is updated, an incident trumps this
        return updated & !isShowingIncident;
    }
    
    /**
     * This method shows a new image.  If the Image Controller is showing an incident,
     * then the incident image will be shown.  Else, if an Image range has been found, 
     * then it's image will be shown.  Image control is done through the ATMSCommunicator
     * singleton instance.
     *
     * @throws Exception if the method is not able to play the current title.
     */
    public void showCurrentImage() throws Exception {
        //TODO  THIS IS HACK??!!
        if (isShowingIncident) {
            theATMSManager.showImage(atms_cctv_id, currentIncident.fileName);
        } else if (currentRange != null) {
            theATMSManager.showImage(atms_cctv_id, currentRange.fileName);
        }
    }


}