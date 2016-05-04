package tmcsim.cadmodels;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * IncidentBoardModel_obj is the model object containing all model 
 * information for an IncidentBoard CAD Screen.  The model data includes
 * a unique number, creation date and time, and message text.<br/>
 * <br/>
 * This element parses and creates the following XML schema in its toXML() and
 * fromXML() methods.  The ROOT element is the parameter for those methods.  The
 * specific XML schema for each IncidentInquiry model can be found in its
 * class header.<br/>
 * <ROOT>
 *    <NUM/>
 *    <DATE/>
 *    <TIME/>
 *    <MESSAGE/>
 * </ROOT>
 * 
 * @author Matthew Cechini
 * @version 
 */
public class IncidentBoardModel_obj {
    
    /**
     * Enumeration with XML tag names.
     * @author Matthew Cechini
     */
    private static enum XML_TAGS {      
        /** Bulletin's number. */
        BULLETIN_NUM ("NUM"),
        /** Bulletin's create date. */
        DATE         ("DATE"),
        /** Bulletin's creation time. */
        TIME         ("TIME"),
        /** Bulletin's message text. */
        MESSAGE      ("MESSAGE");
        
        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }
        
    }
    
    /** Bulletin number. */
    public int bulletinNum;
    
    /** Bulletin creation date. */
    public String date;
    
    /** Bulletin creation time. */
    public String time;
    
    /** Bulletin message text. */   
    public String message;
    
    
    /**
     * Constructor.
     */
    public IncidentBoardModel_obj() {
        bulletinNum = 0;
        date        = null;
        time        = null;
        message     = null;     
    }   
    
    /**
     * Constructor.
     * 
     * @param newBulletinNum Bulletin number.
     * @param newDate Bulletin creation date.
     * @param newTime Bulletin create time.
     * @param newMessage Bulletin message text.
     */
    public IncidentBoardModel_obj(int newBulletinNum, 
                                  String newDate, 
                                  String newTime,
                                  String newMessage) {
        bulletinNum = newBulletinNum;
        date        = newDate;
        time        = newTime;
        message     = newMessage;       
        
    }
    
    public IncidentBoardModel_obj(Node modelNode) {
        fromXML(modelNode);
    }
    
    public void toXML(Element currElem) {
        
        Document theDoc    = currElem.getOwnerDocument();
        Element  tempElem  = null;
        
        tempElem = theDoc.createElement(XML_TAGS.BULLETIN_NUM.tag);
        tempElem.appendChild(theDoc.createTextNode(String.valueOf(bulletinNum)));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.DATE.tag);
        tempElem.appendChild(theDoc.createTextNode(date));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.TIME.tag);
        tempElem.appendChild(theDoc.createTextNode(time));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.MESSAGE.tag);
        tempElem.appendChild(theDoc.createTextNode(message));
        currElem.appendChild(tempElem);
    }    
    
    public void fromXML(Node modelNode) {   
        
        Node childNode = null;
        
        childNode = modelNode.getFirstChild();
        bulletinNum = Integer.parseInt(childNode.getTextContent());
        
        childNode = childNode.getNextSibling();
        date = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        time = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        message = childNode.getTextContent();
            
    }  
    
}