package tmcsim.cadsimulator.db;

import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tmcsim.cadsimulator.managers.ATMSManager;
import tmcsim.cadsimulator.stillimagecontrol.ImageController;
import tmcsim.cadsimulator.stillimagecontrol.ImageIncident;
import tmcsim.cadsimulator.stillimagecontrol.ImageRange;
import tmcsim.common.CCTVDirections;
import tmcsim.common.ScriptException;

/**
 * StilImagesDB acts as a repository for all StillImage data read during 
 * initialization.  An xml file read at startup details which CCTV cameras 
 * will be monitored during the simulator.  An ImageController object is 
 * created for each camera, uniquely identified by an ID and direction.
 * Each camera is also registered with a list of speed ranges and/or incidents 
 * for which the ImageController will perform needed action to swap images on 
 * the ATMS server.
 * 
 * TODO Observer/Observable 
 * 
 * @author Matthew Cechini
 * @version
 */
public class StillImagesDB extends Observable implements Observer {

    /**
     * Enumeration containing XML tag names used to parse the XML
     * document containing information about Still Image control.
     * @author Matthew Cechini
     */
    private static enum IMAGE_DB_TAGS { 
        /** This is the top level tag. */
        STILL_IMAGES ("STILL_IMAGES"),
        /** CCTV camera. */
        CCTV         ("CCTV"),
        /** CCTV camera unique id number. */
        CCTV_ID      ("id"),
        /** CCTV camera unique id string on ATMS server. */ 
        ATMS_CCTV_ID ("atms_id"),
        /** CCTV camera direction value. */ 
        DIRECTION    ("dir"),
        /** Speed range for associated image. */
        RANGE        ("RANGE"),
        /** Minimum value of speed range. */
        MIN_SPEED    ("min_speed"),
        /** Maximum value of speed range. */
        MAX_SPEED    ("max_speed"), 
        /** Incident associated image. */
        INCIDENT     ("INCIDENT"),  
        /** Log number for incident. */
        LOG_NUM      ("log_num"),
        /** Image filename. */
        FILENAME     ("filename");

        /** XML Tag name. */
        public String tag;
        
        private IMAGE_DB_TAGS(String t) {
            tag = t;
        }
    }
    
    
    /**
     * The map containing all current CCTV Camera IDs(key) and the corresponding 
     * Still Image Controller(value) that have been registered with the system.
     */
    private TreeMap<CCTVInfo, ImageController> stillImageMap = null;
    
    /**
     * Constructor.  Initialize the map of ImageControllers.
     */
    public StillImagesDB() {
        stillImageMap = new TreeMap<CCTVInfo, ImageController>();   
    }
    
    /**
     * Implementation of the Observer update() method.  All received update
     * objects are sent to Observers of this DB.
     */
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }
    
    /**
     * Load the Still Images map from an xml document, adhering to the 
     * following schema.  When parsing has finished, the map of 
     * ImageController and CCTVInfo objects will contain objects 
     * representing all control objects specified in the XML<br>
     *
     * <STILL_IMAGES> <br>
     *   <IMAGE_PLAYER type="" username="" pwd="" host="" basedir ="">
     *     <CCTV id="" atms_id="" dir="">
     *       <RANGE min_speed="" max_speed="" filename=""/>
     *       <INCIDENT log_num="" filename=""/>
     *     </CCTV>
     *   </IMAGE_PLAYER>
     *</STILL_IMAGES> <br>
     *
     * @throws ScriptException if there is an error in parsing the Node.
     */
    public void loadFromXML(Document newDoc, ATMSManager theATMSManager) 
        throws ScriptException 
    {
        
        Element rootElement     = newDoc.getDocumentElement();
        Element cameraElement   = null;
        Element rangeElement    = null;
        Element incidentElement = null;

        NodeList cameras   = rootElement.getElementsByTagName(
                IMAGE_DB_TAGS.CCTV.tag);
        NodeList ranges    = null;
        NodeList incidents = null;
        
        Integer cctvID                = null;
        Integer ATMS_cctvID           = null;
        CCTVDirections  cctvDirection = null;
        ImageController theController = null;
        
        //for all CCTV camera elements, parse out data
        for(int j = 0; j < cameras.getLength(); j++) {      
            cameraElement = (Element)cameras.item(j);
            
            cctvID        = Integer.parseInt(cameraElement.getAttribute(
                    IMAGE_DB_TAGS.CCTV_ID.tag));
            ATMS_cctvID   = Integer.parseInt(cameraElement.getAttribute(
                    IMAGE_DB_TAGS.ATMS_CCTV_ID.tag));
            cctvDirection = CCTVDirections.fromChar(cameraElement.getAttribute(
                    IMAGE_DB_TAGS.DIRECTION.tag).charAt(0));
                                    
            ranges    = cameraElement.getElementsByTagName(
                    IMAGE_DB_TAGS.RANGE.tag);
            incidents = cameraElement.getElementsByTagName(
                    IMAGE_DB_TAGS.INCIDENT.tag);
            
            theController = new ImageController(ATMS_cctvID, theATMSManager);
                        
            for(int k = 0; k < ranges.getLength(); k++) {
                rangeElement = (Element)ranges.item(k);
                
                theController.addRange(new ImageRange(
                        Float.parseFloat(rangeElement.getAttribute(
                                IMAGE_DB_TAGS.MIN_SPEED.tag)), 
                        Float.parseFloat(rangeElement.getAttribute(
                                IMAGE_DB_TAGS.MAX_SPEED.tag)), 
                        rangeElement.getAttribute(
                                IMAGE_DB_TAGS.FILENAME.tag)));
            }
            
            for(int k = 0; k < incidents.getLength(); k++) {
                incidentElement = (Element)incidents.item(k);
                
                theController.addIncident(new ImageIncident( 
                        Integer.parseInt(incidentElement.getAttribute(
                                IMAGE_DB_TAGS.LOG_NUM.tag)), 
                        incidentElement.getAttribute(
                                IMAGE_DB_TAGS.FILENAME.tag)));
            }           
            
            stillImageMap.put(new CCTVInfo(cctvID, cctvDirection), theController);
        }           
    }   
    
    /**
     * Gets the ImageController associated with the parameter CCTV_ID number and direction.
     * 
     * @param cctv_id Integer value of the CCTV camera.
     * @param dir Direction value of the CCTV camera.
     * @return ImageController object for CCTV_ID and direction, or null if no object is found.
     */
    public ImageController getController(Integer cctv_id, CCTVDirections dir) {
        return stillImageMap.get(new CCTVInfo(cctv_id, dir));
    }
}
