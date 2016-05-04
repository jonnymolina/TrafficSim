package tmcsim.cadmodels;


import java.util.Observable;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tmcsim.common.ScriptException;
import tmcsim.common.CADEnums.CADScreenNum;
import tmcsim.common.CADEnums.CADScreenType;

/**
 * CADScreenModel is the base model object for all CAD Screen models.  The 
 * information contained within this base class includes the CAD time and date,
 * number of routed messages, whether any messages are unread, CAD screen 
 * number, and the screen update status values.  This data is used for every 
 * CAD Screen during display.<br/>
 * <br/>
 * This element parses and creates the following XML schema in its toXML() and
 * fromXML() methods.  The ROOT element is the parameter for those methods.<br/>
 * <ROOT>
 *    <BASE_MODEL_INFO>
 *       <COMMAND_LINE/>
 *       <FOOTER>
 *          <CAD_TIME/>
 *          <CAD_DATE/>
 *          <ROUTED_MESSAGES/>
 *          <UNREAD_MESSAGES/>
 *          <SCREEN_NUM/>
 *          <SCREEN_UPDATES>
 *             <HAS_UPDATE Screen_Num=""/>
 *             <HAS_UPDATE Screen_Num=""/>
 *             <HAS_UPDATE Screen_Num=""/>
 *             <HAS_UPDATE Screen_Num=""/>
 *          </SCREEN_UPDATES>
 *       </FOOTER>
 *    </BASE_MODEL_INFO>
 * </ROOT>
 * 
 * @author Matthew Cechini
 * @version 
 */
public abstract class CADScreenModel extends Observable {   

    /**
     * Enumeration with XML tag names.
     * @author Matthew Cechini
     */
    private static enum XML_TAGS {
        /** CAD screen mnodel. */
        CAD_SCREEN_MODEL  ("CAD_SCREEN_MODEL"),
        /** CAD model update. */
        CAD_MODEL_UPDATE  ("CAD_MODEL_UPDATE"),
        /** CAD model type. */
        MODEL_TYPE        ("MODEL_TYPE"),
        /** Base model information. */
        BASE_MODEL_INFO   ("BASE_MODEL_INFO"),
        /** Current command line. */
        COMMAND_LINE      ("COMMAND_LINE"),
        /** CAD screen footer. */
        FOOTER            ("FOOTER"),
        /** Current CAD time. */
        CAD_TIME          ("CAD_TIME"),
        /** Current CAD date. */
        CAD_DATE          ("CAD_DATE"),
        /** Screen number. */
        SCREEN_NUM        ("SCREEN_NUM"),
        /** Number of routed messages. */
        ROUTED_MESSAGES   ("ROUTED_MESSAGES"),
        /** Boolean to designate if unread messages are in the queue. */
        UNREAD_MESSAGES   ("UNREAD_MESSAGES"),
        /** Screen update stati. */
        SCREEN_UPDATES    ("SCREEN_UPDATES"),
        /** Page number attribute. */
        UPDATE_SCREEN_NUM ("Screen_Number"),
        /** Screen update's stats.. */
        HAS_UPDATE        ("HAS_UPDATE");

        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }
        
    }   
    
    /** Current command line. */
    public String commandLine;    
    
    /** Screen type for this model. */
    public CADScreenType screenType;
    
    /** Screen number for this model. */    
    public CADScreenNum  screenNum;
    
    /** Number of routed messages for this CAD terminal. */
    public int numberRoutedMessages;
    
    /** Boolean flag to designate whether there are unread routed messages. */
    public boolean unreadMessages;
    
    /** Current update status for all CAD screens. */
    public TreeMap<CADScreenNum, Boolean> screenUpdateMap;
    
    /** Current CAD Date value. MMYY*/
    static public String theCADDate = "";
    
    /** Current CAD Time value. HHMM*/   
    static public String theCADTime = "";   
    

    /**
     * Constructor.  Initiailze data.  Set screenUpdateMap to be
     * false for all screens.
     * 
     * @param type CADScreenType for this model.
     * @param num CADScreenNum for this model.
     */
    public CADScreenModel(CADScreenType type, CADScreenNum num) { 
        
        screenNum   = num;
        screenType  = type;
        commandLine = "";    
        numberRoutedMessages = 0;
        unreadMessages       = false;
                    
        screenUpdateMap = new TreeMap<CADScreenNum, Boolean>();

        for(CADScreenNum screen : CADScreenNum.orderedList()) {
            screenUpdateMap.put(screen, false);
        }
    }        
    
    public abstract void addModelObject(Object o);
    
    /**
     * Creates XML tags with the model data and adds them to the parameter 
     * Element.  The baseToXML() method is called to add the base
     * XML tags for CAD model data.  See clss description for XML schema.
     *
     * @param currElem XML Element used as a root for XML tag appending.
     */
    public abstract void toXML(Element currElem);
    
    /**
     * Parses model data from the parameter Node.  The fromToXML() method is 
     * called to parse the base XML tags for CAD model data.  See clss 
     * description for XML schema.
     * 
     * @param modelNode XML Node containing model information.
     * @throws ScriptException if there is an error in parsing the Node.
     */
    public abstract void fromXML(Node modelNode) throws ScriptException;

    /**
     * Writes the base model data to the XMLWriter.  See the class
     * description for the base model XML schema.
     * 
     * @param currElem XML Element used as a root for XML tag appending.
     */
    public void baseToXML(Element currElem) {

        Document theDoc = currElem.getOwnerDocument();
        
        Element baseModelElem = theDoc.createElement(XML_TAGS.BASE_MODEL_INFO.tag);
        
        Element cmdLineElem = theDoc.createElement(XML_TAGS.COMMAND_LINE.tag);
        cmdLineElem.appendChild(theDoc.createTextNode(commandLine));
        baseModelElem.appendChild(cmdLineElem);
        
        Element footerElem = theDoc.createElement(XML_TAGS.FOOTER.tag);
        baseModelElem.appendChild(footerElem);
                
        Element timeElem   = theDoc.createElement(XML_TAGS.CAD_TIME.tag);
        timeElem.appendChild(theDoc.createTextNode(theCADTime));
        footerElem.appendChild(timeElem);

        Element dateElem   = theDoc.createElement(XML_TAGS.CAD_DATE.tag);
        dateElem.appendChild(theDoc.createTextNode(theCADDate));
        footerElem.appendChild(dateElem);

        Element routedMsgsElem = theDoc.createElement(XML_TAGS.ROUTED_MESSAGES.tag);
        routedMsgsElem.appendChild(theDoc.createTextNode(String.valueOf(numberRoutedMessages)));
        footerElem.appendChild(routedMsgsElem);

        Element unreadMsgsElem = theDoc.createElement(XML_TAGS.UNREAD_MESSAGES.tag);
        unreadMsgsElem.appendChild(theDoc.createTextNode(String.valueOf(unreadMessages)));
        footerElem.appendChild(unreadMsgsElem);

        Element screenNumElem   = theDoc.createElement(XML_TAGS.SCREEN_NUM.tag);
        screenNumElem.appendChild(theDoc.createTextNode(String.valueOf(screenNum.intNum)));
        footerElem.appendChild(screenNumElem);
        
        Element screenUpdatesElem = theDoc.createElement(XML_TAGS.SCREEN_UPDATES.tag);
        footerElem.appendChild(screenUpdatesElem);

        Element updateElem = null;
        for(CADScreenNum screen : CADScreenNum.orderedList()) {
            updateElem = theDoc.createElement(XML_TAGS.HAS_UPDATE.tag);         
            updateElem.appendChild(theDoc.createTextNode(String.valueOf(screenUpdateMap.get(screen))));
            updateElem.setAttribute(XML_TAGS.UPDATE_SCREEN_NUM.tag, String.valueOf(screen.intNum));

            screenUpdatesElem.appendChild(updateElem);
        }
        
        currElem.appendChild(baseModelElem);
        
    }   
    
    /**
     * Read in the base model data from the XML node. See the class
     * description for the base model XML schema.
     * 
     * @param modelNode XML Node containing model information.
     * @throws ScriptException if there is an error in parsing the node.
     */
    public void baseFromXML(Node modelNode) throws ScriptException {
        //COMMAND_LINE node
        Node node = modelNode.getFirstChild();
        commandLine = node.getTextContent();
        
        //FOOTER node
        node = node.getNextSibling();       

        //CAD_TIME node
        node = node.getFirstChild();
        theCADTime = node.getTextContent();

        //CAD_DATE node
        node = node.getNextSibling();
        theCADDate = node.getTextContent();
        
        //ROUTED_MESSAGES node
        node = node.getNextSibling();
        numberRoutedMessages = Integer.valueOf(node.getTextContent()).intValue();
        
        //UNREAD_MESSAGES node
        node = node.getNextSibling();
        unreadMessages = Boolean.valueOf(node.getTextContent()).booleanValue();
        
        //SCREEN_NUM node
        node = node.getNextSibling();
        screenNum = CADScreenNum.fromValue(Integer.valueOf(node.getTextContent()).intValue());      
        
        //SCREEN_UPDATES node
        node = node.getNextSibling();
        NodeList updates = node.getChildNodes();
        
        screenUpdateMap.clear();
        Integer pageNumber   = null;
        Boolean updateStatus = null;
        for(int i = 0; i < updates.getLength(); i++) {
            
            pageNumber   = Integer.valueOf(updates.item(i).getAttributes().
                    getNamedItem(XML_TAGS.UPDATE_SCREEN_NUM.tag).getTextContent());
            updateStatus = Boolean.valueOf(updates.item(i).getTextContent());
            
            screenUpdateMap.put(CADScreenNum.fromValue(pageNumber), updateStatus);
        }
    }

    /**
     * Get the CAD screen type for this model.
     * @return Current CADScreenType object.
     */
    public CADScreenType getType() {
        return screenType;
    }
    
    /**
     * Get the CAD screen number for this model.
     * @return Current CADScreenNum object.
     */
    public CADScreenNum getScreenNum() {
        return screenNum;
    }
    
    /**
     * This static method converts the parameter CAD screen status update
     * map into a String representation.  The resulting String is in the
     * following format:  "<#>=true,<#>=false,<#>=true,<#>=false" where <#>
     * is the CAD screen number.  
     * 
     * @param updateMap Map containing screen update flags.
     * @return A String concatenation of screen update information.
     */
    public static String updateMapToString(TreeMap<CADScreenNum, Boolean> updateMap) {
        
        StringBuffer mapBuf = new StringBuffer();
        
        for(CADScreenNum screen : updateMap.keySet()) {
            mapBuf.append(screen.intNum);
            mapBuf.append("=");
            mapBuf.append(updateMap.get(screen).toString());
            mapBuf.append(",");
        }       
        
        mapBuf.setLength(mapBuf.length()-1);
        
        return mapBuf.toString();
    }
    
    /**
     * This static method converts the parameter CAD screen status update
     * String into an update Map. The parameter String must have the 
     * following format:  "<#>=true,<#>=false,<#>=true,<#>=false" where <#>
     * is the CAD screen number.  
     * 
     * @param updateString String containing CAD screen status update info. 
     * @return Map containing CAD Screen update info.
     */
    public static TreeMap<CADScreenNum, Boolean> updateStringToMap(String updateString) {
        
        TreeMap<CADScreenNum, Boolean> updateMap = new TreeMap<CADScreenNum, Boolean>();
        
        StringTokenizer strTok = new StringTokenizer(updateString, ",");
        String token = null;
        
        CADScreenNum screen  = null;
        Boolean screenUpdate = null;
        
        while(strTok.hasMoreTokens()) {
            token = strTok.nextToken();
            
            screen = CADScreenNum.fromValue(Integer.parseInt(
                        token.substring(0, token.indexOf("="))));
            screenUpdate = Boolean.parseBoolean(token.substring(
                    token.indexOf("=")+1, token.length()));
            
            updateMap.put(screen, screenUpdate);
        }
        
        return updateMap;
    }   

}