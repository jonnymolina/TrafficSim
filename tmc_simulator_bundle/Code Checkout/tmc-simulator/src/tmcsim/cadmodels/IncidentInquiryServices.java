package tmcsim.cadmodels;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * IncidentInquiryServices extends from IncidentInquiryLogEntry
 * to provide a model object containing information used to display a service
 * unit assigned.  Data for a service includes the service's name and 
 * the confidential and public phone numbers.<br/>
 * <br/>
 * This element parses and creates the following XML schema in its toXML() and
 * fromXML() methods.  The ROOT element is the parameter for those methods.<br/>
 * <ROOT><br/>
 *    <SERVICE/><br/>
 *    <CONF_PHONE_NUM/>/<br/>
 *    <PUB_PHONE_NUM/><br/>
 *    <POSITION_INFO/><br/>
 *    <TIMESTAMP/><br/>
 * </ROOT><br/>
 * 
 * @author Matthew Cechini
 * @version 
 */
@SuppressWarnings("serial")
public class IncidentInquiryServices extends IncidentInquiryLogEntry 
    implements Serializable {
    
    /**
     * Enumeration with XML tag names.
     * @author Matthew Cechini
     */
    private static enum XML_TAGS {
        /** Service's name. */
        SERVICE        ("SERVICE"),
        /** Service's confidential phone number. */
        CONF_PHONE_NUM ("CONF_PHONE_NUM"),
        /** Service's public phone number. */
        PUB_PHONE_NUM  ("PUB_PHONE_NUM");
        
        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }
    }

    /** Service's name. */
    public String serviceName;  
    
    /** Service's confidential phone number. */
    public String confPhoneNum; 
    
    /** Service's public phone number. */
    public String publicPhoneNum;   
    
    /** 
     * Constructor.  Initialize all Service data to empty strings.
     * 
     *  @param newPosInfo String containing position info for this log entry
     */
    public IncidentInquiryServices(String newPosInfo) {
        super(newPosInfo, "0000");

        serviceName    = "";
        confPhoneNum   = "";
        publicPhoneNum = "";    
    }
    
    /**
     * Constructor.  Parse parameter node for Service log entry data.
     * 
     * @param theNode Node containing data for this Service log entry
     */
    public IncidentInquiryServices(Node theNode) {  
        fromXML(theNode);
    }
    
    public void toXML(Element currElem) {
        
        Document theDoc    = currElem.getOwnerDocument();
        Element  tempElem  = null;
        
        tempElem = theDoc.createElement(XML_TAGS.SERVICE.tag);
        tempElem.appendChild(theDoc.createTextNode(serviceName));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.CONF_PHONE_NUM.tag);
        tempElem.appendChild(theDoc.createTextNode(confPhoneNum));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.PUB_PHONE_NUM.tag);
        tempElem.appendChild(theDoc.createTextNode(publicPhoneNum));
        currElem.appendChild(tempElem);

        super.toXML(currElem);
    }    
    
    public void fromXML(Node modelNode) {   
        
        Node childNode = null;
        
        childNode = modelNode.getFirstChild();
        serviceName = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        confPhoneNum = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        publicPhoneNum = childNode.getTextContent();

        childNode = childNode.getNextSibling();
        super.fromXML(childNode);
    }    
}