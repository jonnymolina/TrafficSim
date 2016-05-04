package tmcsim.cadsimulator.stillimagecontrol;

/**
 * ImageRange is a container class used within the CAD still image control.  
 * It contains a minimum and maximum speed range and an associated image 
 * file name to be shown.  The ImageController class uses the ImageRange 
 * to determine which image is to be shown on the ATMS according to 
 * current traffic speed.
 *
 * @author Matthew Cechini
 * @version
 */
public class ImageRange {

    /** Minimum speed for this range. */
    public float minSpeed;

    /** Maximum speed for this range. */
    public float maxSpeed;
    
    /** Filename of the image to be shown for this range. */
    public String fileName;

    /**  
     * Constructor.  Initialize member data with parameter values.
     *
     * @param min Minimum speed in range.
     * @param max Maximum speed in range.
     * @param file Filename if associated image file.
     */
    public ImageRange(float min, float max, String file) {
        minSpeed = min;
        maxSpeed = max;
        fileName = file;
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
    
}
