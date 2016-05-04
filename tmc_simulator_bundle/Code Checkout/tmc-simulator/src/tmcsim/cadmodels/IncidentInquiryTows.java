package tmcsim.cadmodels;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * IncidentInquiryTows extends from IncidentInquiryLogEntry to provide a model 
 * object containing information used to display an assigned tow unit.
 * Data for a tow includes the Tow Company's name, the confidential and public 
 * phone numbers, assigned beat, and current status.<br/>
 * <br/>
 * This element parses and creates the following XML schema in its toXML() and
 * fromXML() methods.  The ROOT element is the parameter for those methods.<br/>
 * <ROOT><br/>
 *    <COMPANY/><br/>
 *    <CONF_PHONE_NUM/>/<br/>
 *    <PUB_PHONE_NUM/><br/>
 *    <BEAT/><br/>
 *    <STATUS/><br/>
 *    <POSITION_INFO/><br/>
 *    <TIMESTAMP/><br/>
 * </ROOT><br/>
 * 
 * @author Matthew Cechini
 * @version 
 */
@SuppressWarnings("serial")
public class IncidentInquiryTows extends IncidentInquiryLogEntry 
    implements Serializable {
    
    /**
     * Enumeration with XML tag names.
     * @author Matthew Cechini
     */
    private static enum XML_TAGS {  
        /** Tow company's name. */
        COMPANY        ("COMPANY"),
        /** Tow company's confidential phone number. */
        CONF_PHONE_NUM ("CONF_PHONE_NUM"),      
        /** Tow company's public phone number. */
        PUB_PHONE_NUM  ("PUB_PHONE_NUM"),       
        /** Tow vehicle's assigned beat. */
        BEAT           ("BEAT"),    
        /** Tow vehicle's status. */
        STATUS         ("STATUS");
        
        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }
    }
    
    /** Tow's company name */
    public String towCompany;
    
    /** Tow's confidential phone number */
    public String confPhoneNum;
    
    /** Tow's public phone number */
    public String publicPhoneNum;
    
    /** Tow's assigned beat */
    public String beat;
    
    /** Tow's status */
    public String statusInfo;
        
    /** 
     * Constructor.  Initialize all Tow data to empty strings.
     * 
     *  @param newPosInfo String containing position info for this log entry
     */
    public IncidentInquiryTows(String newPosInfo) {
        super(newPosInfo, "0000");
        
        towCompany     = "";
        confPhoneNum   = "";
        publicPhoneNum = "";
        beat           = "";
        statusInfo     = "";        
    }
    
    /**
     * Constructor.  Parse parameter node for Tow log entry data.
     * 
     * @param theNode Node containing data for this Tow log entry
     */
    public IncidentInquiryTows(Node theNode) {  
        fromXML(theNode);
    }
    
    public void toXML(Element currElem) {
        
        Document theDoc    = currElem.getOwnerDocument();
        Element  tempElem  = null;
        
        tempElem = theDoc.createElement(XML_TAGS.COMPANY.tag);
        tempElem.appendChild(theDoc.createTextNode(towCompany));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.CONF_PHONE_NUM.tag);
        tempElem.appendChild(theDoc.createTextNode(confPhoneNum));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.PUB_PHONE_NUM.tag);
        tempElem.appendChild(theDoc.createTextNode(publicPhoneNum));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.BEAT.tag);
        tempElem.appendChild(theDoc.createTextNode(beat));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.STATUS.tag);
        tempElem.appendChild(theDoc.createTextNode(statusInfo));
        currElem.appendChild(tempElem);

        super.toXML(currElem);
    }    
    
    public void fromXML(Node modelNode) {   
        
        Node childNode = null;
        
        childNode = modelNode.getFirstChild();
        towCompany      = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        confPhoneNum = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        publicPhoneNum = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        beat = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        statusInfo = childNode.getTextContent();

        childNode = childNode.getNextSibling();
        super.fromXML(childNode);
        
    }  
}