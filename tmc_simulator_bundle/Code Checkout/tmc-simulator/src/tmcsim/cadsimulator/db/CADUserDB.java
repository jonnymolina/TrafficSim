package tmcsim.cadsimulator.db;

import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tmcsim.common.ScriptException;

/**
 * (Not used)
 * @author Matthew Cechini
 * @version
 */
public class CADUserDB {

    public static enum PERMISSION_LEVEL { 
        TMC     ("TMC"), 
        CHP     ("CHP"),
        UNKNOWN ("");
    
        private String level;
    
        private PERMISSION_LEVEL(String l) {
            level = l;
        }
        
        public static PERMISSION_LEVEL fromString(String l) {
            for(PERMISSION_LEVEL pLevel : values()) {
                if(pLevel.level.equals(l))
                    return pLevel;
            }
            
            return UNKNOWN;
        }
        
    };
    
    private static enum DVD_TAGS {
        
        /** Top level tag. */
        CAD_USERS  ("CAD_USERS"),
        /** CAD User info. */   
        USER       ("USER"),
        /** User ID. */ 
        ID         ("ID"),
        /** DVD player address. */
        PERMISSION ("PERMISSION_LEVEL");
        
        public String tag;
        
        private DVD_TAGS(String t) {
            tag = t;
        }
    }

    /** Error Logger. */
    //private Logger userLogger = Logger.getLogger("tmcsim.cadsimulator.db");
    
    private TreeMap<String, PERMISSION_LEVEL> userPermissionsMap;
    
    public CADUserDB() {
        userPermissionsMap = new TreeMap<String, PERMISSION_LEVEL>();
    }
    
    public PERMISSION_LEVEL getUserPermissionLevel(String userID) {
        //exists??
        
        return userPermissionsMap.get(userID);
    }
    /**
     * Load the DVD Player map from an xml document, adhering to the 
     * following schema: <br>
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
        Element rootElement       = newDoc.getDocumentElement();                
        Element userElement       = null;
        Element idElement         = null;
        Element permissionElement = null;
        
        String userID             = null;
        PERMISSION_LEVEL pLevel   = null;
        
        NodeList users = rootElement.getElementsByTagName(DVD_TAGS.USER.tag);
    
        for(int i = 0; i < users.getLength(); i++) {
        
            userElement = (Element)users.item(i);
                                
            idElement = (Element)userElement.getFirstChild();
            userID    = idElement.getTextContent();
            
            permissionElement = (Element)idElement.getNextSibling();
            pLevel            = PERMISSION_LEVEL.fromString(permissionElement.getTextContent());

            if(pLevel != PERMISSION_LEVEL.UNKNOWN)
                userPermissionsMap.put(userID, pLevel);
            
        }               
        
    }
}
