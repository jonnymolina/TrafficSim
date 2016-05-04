package tmcsim.cadsimulator.managers;

import java.io.File;
import java.io.FileInputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.xml.parsers.DocumentBuilderFactory;

import tmcsim.cadsimulator.db.DVDPlayerDB;
import tmcsim.cadsimulator.db.StillImagesDB;
import tmcsim.cadsimulator.stillimagecontrol.ImageController;
import tmcsim.cadsimulator.videocontrol.DVDController;
import tmcsim.cadsimulator.videocontrol.DVDStatusUpdate;
import tmcsim.cadsimulator.videocontrol.DVDTitleUpdate;
import tmcsim.cadsimulator.viewer.CADSimulatorViewer;
import tmcsim.cadsimulator.viewer.MediaStatusPanel;
import tmcsim.common.CCTVDirections;
import tmcsim.common.CCTVInfo;

/**
 * MediaManager manages the control of media associated with the speed information
 * received from the ParamicsCommunicator. The MediaManager instantiates the DVDPlayerDB
 * and StillImageDB objects, which are initialized from XML data files to hold the
 * information regarding which DVD titles and Still Images are available for display.
 * Each title or image is associated with a specific speed range or Incident log number.
 * The triggerIncident() method is called to notify the MediaManager that incident media
 * is to be displayed. The updateCameraInfo() method is called when speed information is
 * received for a specific camera. In both methods, a Controller object is found for the
 * unique camera ID number. This Controller is updated with the new speed data. If the
 * update causes a change in media to be displayed, the change is made.<br>
 * <br>
 * The MediaManager implements the Observer interface and observes the DVDPlayerDB and
 * StillImageDB for updates. These updates are received, action is taken, and then the
 * update is passed to the CADSimulatorViewer for display.
 *
 * @see tmcsim.cadsimulator.viewer.CADSimulatorViewer
 * @author Jonathan Molina
 * @author Matthew Cechini
 * @version
 */
public class MediaManager implements Observer
{
    /**
     * Error Logger.
     */
    private static Logger mediaLogger = Logger.getLogger("tmcsim.simulator");

    /**
     * Enumeration of property names.
     *
     * @author Matthew Cechini
     */
    private static enum MEDIA_PROPERTIES
    {
        /**
         * Filepath for xml file containing dvd control data.
         */
        DVD_XML_FILE("DVDPlayerXML"),
        /**
         * Filepath for xml file containing still image control data.
         */
        IMAGE_XML_FILE("StillImagesXML");
        public String name;

        private MEDIA_PROPERTIES(String n)
        {
            name = n;
        }
    };
    /**
     * Instance of the DVD Player Database.
     */
    private DVDPlayerDB theDVD_DB = null;
    /**
     * Instance of the Still Image Database.
     */
    private StillImagesDB theImage_DB = null;
    /**
     * Reference to the CADSimulatorViewer.
     */
    private MediaStatusPanel medialPanel;
    /**
     * Properties object for the Media portion of the CAD.
     */
    private Properties mediaProperties;

    /**
     * Constructor. The target properties file is loaded and the DVD and Still Image DB
     * objects are initialized with the XML data files containing the respective data.
     *
     * @param propertiesFile File path for target properties file.
     * @param theATMSManager ATMSManager object.
     * @param theMediaPanel MediaStatusPanel object.
     */
    public MediaManager(String propertiesFile, ATMSManager theATMSManager,
            MediaStatusPanel theMediaPanel)
    {
        theDVD_DB = new DVDPlayerDB();
        theImage_DB = new StillImagesDB();

        medialPanel = theMediaPanel;

        try
        {
            mediaProperties = new Properties();
            mediaProperties.load(new FileInputStream(new File(propertiesFile)));
        }
        catch (Exception e)
        {
            mediaLogger.logp(Level.SEVERE, "MediaManager", "Constructor",
                    "Exception in loading properties file.", e);
        }

        // Load DVD Player Information from the XML file
        try
        {
            // set dvd properties
            if (mediaProperties.getProperty(MEDIA_PROPERTIES.DVD_XML_FILE.name) != null)
            {
                theDVD_DB.addObserver(this);
                theDVD_DB.loadFromXML(DocumentBuilderFactory
                        .newInstance().newDocumentBuilder()
                        .parse(new File(mediaProperties.getProperty(
                            MEDIA_PROPERTIES.DVD_XML_FILE.name))));
            }
        }
        catch (Exception e)
        {
            mediaLogger.logp(Level.SEVERE, "MediaManager", "Constructor",
                    "Exception in parsing DVDPlayer xml file.", e);

            JOptionPane.showMessageDialog(new JWindow(), "An error occured "
                    + "opening and parsing the DVD xml data file: "
                    + mediaProperties.getProperty(MEDIA_PROPERTIES.DVD_XML_FILE.name),
                    "Initialization Error", JOptionPane.WARNING_MESSAGE);
        }

        // Load Still Image Information from the XML file
        try
        {
            // set image properties
            if (mediaProperties
                    .getProperty(MEDIA_PROPERTIES.IMAGE_XML_FILE.name) != null)
            {
                theImage_DB.addObserver(this);
                theImage_DB.loadFromXML(DocumentBuilderFactory
                        .newInstance().newDocumentBuilder()
                        .parse(new File(mediaProperties.getProperty(
                            MEDIA_PROPERTIES.IMAGE_XML_FILE.name))),
                            theATMSManager);
            }
        }
        catch (Exception e)
        {
            mediaLogger.logp(Level.SEVERE, "MediaManager", "Constructor",
                    "Exception in parsing StillImages xml file.", e);

            JOptionPane.showMessageDialog(new JWindow(), "An error occured "
                    + "opening and parsing the Image xml data file: "
                    + mediaProperties.getProperty(MEDIA_PROPERTIES.IMAGE_XML_FILE.name),
                    "Initialization Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Observer method. The update objects accepted and their associated actions are
     * shown below.<br>
     * <br>
     * <ul>
     * <li>DVDStatusUpdate - If the update contains an exception, log the exception to
     * the local logger. The update object is then sent to the CADSimulatorViewer for
     * display.</li>
     * <li>DVDTitleUpdate - The update object is sent to the CADSimulatorViewer for
     * display.</li>
     * </ul>
     * @param o the observable object
     * @param arg the passed object upon updates
     */
    public void update(Observable o, Object arg)
    {
        // update DVD status
        if (arg instanceof DVDStatusUpdate)
        {
            // log exception
            if (((DVDStatusUpdate) arg).exception != null)
            {
                mediaLogger.logp(Level.SEVERE, "MediaManager", "update",
                        "Exception from DVD Controller.",
                        ((DVDStatusUpdate) arg).exception);
            }
            medialPanel.updateDVDStatus((DVDStatusUpdate) arg);
        }
        // update DVD title
        else if (arg instanceof DVDTitleUpdate)
        {
            medialPanel.updateDVDTitle((DVDTitleUpdate) arg);
        }
    }

    /**
     * This method triggers the DVD and Image Controllers for a specific camera to
     * display an Incident title or image for the parameter Incident log number.
     *
     * @param info Information of the CCTV Camera to display incident images.
     * @param logNumber Log number of the Incident being triggered.
     */
    public void triggerIncident(CCTVInfo info, Integer logNumber)
    {
        try
        {
            DVDController dvdCntrl = theDVD_DB.getController(info.cctv_id,
                    info.direction);

            if (dvdCntrl != null)
            {
                dvdCntrl.toggleIncident(logNumber, info.toggle);
            }
        }
        catch (Exception e)
        {
            mediaLogger.logp(Level.SEVERE, "MediaManager", "triggerIncident",
                    "Exception in toggling an incident on the DVD controller.", e);
        }

        try
        {
            ImageController imageCntrl = theImage_DB.getController(info.cctv_id, info.direction);

            if (imageCntrl != null)
            {
                imageCntrl.toggleIncident(logNumber, info.toggle);
            }
        }
        catch (Exception e)
        {
            mediaLogger.logp(Level.SEVERE, "MediaManager", "triggerIncident",
                    "Exception in toggling an incident on the Image controller.", e);
        }

    }

    /**
     * This method receives updated speed information for a specific controller and
     * updates the DVD Title and Still Image being currently displayed for that camera.
     *
     * @param cameraID CCTV camera ID
     * @param avgSpeedNE Average speed of traffic flowing N or E
     * @param avgSpeedSW Average speed of traffic flowing S or W
     */
    public void updateCameraInfo(Integer cameraID, float avgSpeedNE, float avgSpeedSW)
    {
        updateDVD(cameraID, avgSpeedNE, avgSpeedSW);
        updateStillImage(cameraID, avgSpeedNE, avgSpeedSW);
    }

    /**
     * This method accepts updated speed information for a specific DVD player. The
     * DVDController object corresponding to the parameter camera ID is found and updated
     * with the new speed. If this update causes the dvd controller to change titles, the
     * new title is played.
     *
     * @param cameraID CCTV camera ID
     * @param avgSpeed_NE Average speed of traffic flowing N or E
     * @param avgSpeed_SW Average speed of traffic flowing S or W
     */
    protected void updateDVD(Integer cameraID, float avgSpeed_NE, float avgSpeed_SW)
    {

        for (CCTVDirections dir : CCTVDirections.values())
        {
            DVDController dvdCntrl = theDVD_DB.getController(cameraID, dir);

            //if Controller exists, update.
            if (dvdCntrl != null)
            {

                if (((dir == CCTVDirections.NORTH || dir == CCTVDirections.EAST)
                        && (dvdCntrl.updatePlayer(avgSpeed_NE)))
                        || ((dir == CCTVDirections.SOUTH || dir == CCTVDirections.WEST)
                        && (dvdCntrl.updatePlayer(avgSpeed_SW))))
                {

                    try
                    {
                        dvdCntrl.playCurrentTitle();
                    }
                    catch (Exception e)
                    {
                        mediaLogger.logp(Level.SEVERE, "ParamicsCameraStatusReader", "updateDVD",
                                "Exception in updating DVD player with new speed.", e);
                    }
                }
            }
        }
    }

    /**
     * This method accepts updated speed information for a specific CCTV camera. The
     * ImageController object corresponding to the parameter camera ID is found and
     * updated with the new speed. If this update causes the image controller to change
     * images, the new image is shown.
     *
     * @param cameraID Unique CCTV camera ID
     * @param avgSpeed_NE Average speed of traffic flowing N or E
     * @param avgSpeed_SW Average speed of traffic flowing S or W
     */
    protected void updateStillImage(Integer cameraID, float avgSpeed_NE, float avgSpeed_SW)
    {

        for (CCTVDirections dir : CCTVDirections.values())
        {
            ImageController imageCntrl = theImage_DB.getController(cameraID, dir);

            //if Controller exists, update.
            if (imageCntrl != null)
            {

                if (((dir == CCTVDirections.NORTH || dir == CCTVDirections.EAST)
                        && (imageCntrl.updateImage(avgSpeed_NE)))
                        || ((dir == CCTVDirections.SOUTH || dir == CCTVDirections.WEST)
                        && (imageCntrl.updateImage(avgSpeed_SW))))
                {

                    try
                    {
                        imageCntrl.showCurrentImage();
                    }
                    catch (Exception e)
                    {
                        mediaLogger.logp(Level.SEVERE, "ParamicsCameraStatusReader", "updateStillImage",
                                "Exception in updating image controller with new speed.", e);
                    }
                }
            }
        }
    }
}
