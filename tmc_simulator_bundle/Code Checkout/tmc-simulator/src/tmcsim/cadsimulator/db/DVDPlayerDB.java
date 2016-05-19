package tmcsim.cadsimulator.db;


import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tmcsim.cadsimulator.videocontrol.DVDController;
import tmcsim.cadsimulator.videocontrol.DVDIncident;
import tmcsim.cadsimulator.videocontrol.DVDRange;
import tmcsim.common.CCTVDirections;
import tmcsim.common.ScriptException;

/**
 * DVDPlayerDB acts as a database containing the information 
 * for each dvd player and the cameras whose playback is found on that 
 * player.  The "Database" of dvd players and information is created by
 * calling the loadFromXML() method with a valid XML document.  DVD players
 * are "queried" according to the corresponding CCTV camera ID and direction
 * whose video is represented on that DVD.  
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/06 22:33:20 $ $Revision: 1.4 $
 */
public class DVDPlayerDB extends Observable implements Observer {   

    /** Error Logger. */
    private static Logger dvdLogger = Logger.getLogger("tmcsim.cadsimulator.db");
    
    /**
     * Enumeration containing XML tag names used to parse the XML
     * document containing information about DVD Title control.
     * @author Matthew Cechini
     */
    private static enum DVD_TAGS {      
        /** Top level tag. */
        DVD_PLAYERS ("DVD_PLAYERS"),
        /** DVD player info. */ 
        DVD_PLAYER ("DVD_PLAYER"),
        /** DVD player type. */ 
        TYPE         ("type"),
        /** DVD player address. */
        HOST         ("host"),
        /** DVD player port. */
        PORT         ("port"),
        /** CCTV camera info. */
        CCTV         ("CCTV"),
        /** CCTV camera id. */
        ID           ("id"),
        /** CCTV camera direction. */
        DIRECTION    ("dir"),
        /** Incident title info. */
        INCIDENT     ("INCIDENT"),
        /** Log number of an incident title. */
        LOG_NUM      ("log_num"),       
        /** Speed range mapping to a title number. */
        RANGE        ("RANGE"),
        /** Minimum speed in a range. */
        MIN_SPEED    ("min_speed"),
        /** Maximum speed in a range. */
        MAX_SPEED    ("max_speed"),
        /** Title name. */
        TITLE        ("title");
        
        /** XML Tag Name */
        public String tag;
        
        private DVD_TAGS(String t) {
            tag = t;
        }
    }
    

    /**
     * The map containing all current CCTV Camera IDs(key) and the corresponding 
     * DVD Controller(value) that have been registered with the system.
     */
    private TreeMap<CCTVInfo, DVDController> dvdPlayerMap = null;
    
    /**
     * Constructor.  Initialize the DVD Controller map.
     */
    public DVDPlayerDB() {
        dvdPlayerMap = new TreeMap<CCTVInfo, DVDController>();  
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
     * Load the DVD map from an xml document, adhering to the 
     * following schema.  When parsing has finished, the map of 
     * DVDController and CCTVInfo objects will contain objects 
     * representing all control objects specified in the XML.  
     * A connection to each DVD controller will be automatically
     * started after parsing has completed.<br>
     * 
     * <DVD_PLAYERS> <br>
     *   <DVD_PLAYER type="" host="" port="">
     *      <CCTV id="" dir=""/>
     *      <INCIDENT log_num="" title="" duration=""/>
     *      <RANGE min_speed="" max_speed="" title="" duration=""/>
     (   </DVD_PLAYER>
     *</DVD_PLAYERS> <br>
     *
     * @param newDoc The XML document containing the DVD player 
     *               registration information.
     * @throws ScriptException if there is an error in parsing the node.
     */
    public void loadFromXML(Document newDoc) throws ScriptException {
        
        //temporary variables
        Element rootElement     = newDoc.getDocumentElement();              
        Element playerElement   = null;
        Element cctvElement     = null;
        Element rangeElement    = null;
        Element incidentElement = null;
        
        Integer cctvID                = null;
        CCTVDirections  cctvDirection = null;
        DVDController theController   = null;
        
        NodeList players   = rootElement.getElementsByTagName(DVD_TAGS.DVD_PLAYER.tag);
        NodeList ranges    = null;
        NodeList incidents = null;
        
    
        for(int i = 0; i < players.getLength(); i++) {
        
            playerElement = (Element)players.item(i);
            
            try {
                theController = (DVDController)Class.forName(
                    "tmcsim.cadsimulator.videocontrol." + playerElement.getAttribute(
                            DVD_TAGS.TYPE.tag)).newInstance();
                theController.addObserver(this);
            }
            catch (Exception e) {
                dvdLogger.logp(Level.SEVERE, "DVDPlayerDB", "loadFromXML", 
                        "Error in instantiating DVD Controller", e);
                continue;
            }
            
            theController.setConnectionInfo(playerElement.getAttribute(
                    DVD_TAGS.HOST.tag),
                    Integer.parseInt(playerElement.getAttribute(
                            DVD_TAGS.PORT.tag)));
                    
            cctvElement   = (Element)playerElement.getElementsByTagName(
                    DVD_TAGS.CCTV.tag).item(0);
            cctvID        = Integer.parseInt(cctvElement.getAttribute(
                    DVD_TAGS.ID.tag));
            cctvDirection = CCTVDirections.fromChar(cctvElement.getAttribute(
                    DVD_TAGS.DIRECTION.tag).charAt(0));
            
            ranges    = playerElement.getElementsByTagName(DVD_TAGS.RANGE.tag);     
            incidents = playerElement.getElementsByTagName(DVD_TAGS.INCIDENT.tag);
                        
            for(int j = 0; j < ranges.getLength(); j++) {
                rangeElement = (Element)ranges.item(j);
                
                theController.addRange(new DVDRange(
                        Float.parseFloat(rangeElement.getAttribute(
                                DVD_TAGS.MIN_SPEED.tag)),
                        Float.parseFloat(rangeElement.getAttribute(
                                DVD_TAGS.MAX_SPEED.tag)),
                        Integer.parseInt(rangeElement.getAttribute(
                                DVD_TAGS.TITLE.tag))));
            }
            
            for(int j = 0; j < incidents.getLength(); j++) {
                incidentElement = (Element)incidents.item(j);
                
                theController.addIncident(new DVDIncident(
                        Integer.parseInt(incidentElement.getAttribute(
                                DVD_TAGS.LOG_NUM.tag)),
                        Integer.parseInt(incidentElement.getAttribute(
                                DVD_TAGS.TITLE.tag))));
            }
            
            dvdPlayerMap.put(new CCTVInfo(cctvID, cctvDirection), theController);
            
        }               
        
        //Thread the connection of DVD controllers so that initialization does not hang.
        Runnable connect = new Runnable() {

            public void run() {
                for(DVDController controller : dvdPlayerMap.values()) {
                    try {
                        controller.connect();
                        throw new IOException();
                    }
                    catch (IOException ioe) {
                        dvdLogger.logp(Level.SEVERE, "DVDPlayerDB", "loadFromXML:runnable", 
                                "IOException in connecting DVD " + 
                                controller.getConnectionInfo(), ioe);
                    }   
                }           
            }                       
        };
        
        Thread connectThread = new Thread(connect);
        connectThread.start();
    }

    /**
     * Gets the DVDController for a registered camera.  DVDControllers
     * are referenced by the Camera ID and direction.
     *
     * @param cctv_id CCTV camera id
     * @param dir CCTV camera direction
     * 
     * @return Returns the DVDController object corresponding with the parameter 
     * camera values.  Returns null if no corresponding DVD Controller is found.
     */ 
    public DVDController getController(Integer cctv_id, CCTVDirections dir) {
        return dvdPlayerMap.get(new CCTVInfo(cctv_id, dir));
    }
    
        
}
