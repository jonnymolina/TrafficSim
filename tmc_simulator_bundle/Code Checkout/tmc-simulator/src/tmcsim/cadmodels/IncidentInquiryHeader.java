package tmcsim.cadmodels;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * IncidentInquiryHeader contains model data containing information used to 
 * display the CAD log Header inofrmation.  Data for the header includes the
 * following Incident data:
 * <ul>
 * <li>Log number</li>
 * <li>Log status</li>
 * <li>Priority</li>
 * <li>Type</li>
 * <li>Callbox Number</li>
 * <li>Beat</li>
 * <li>Full Location</li>
 * <li>Truncated Location</li>
 * <li>Origin</li>
 * <li>Date</li>
 * <li>Time</li>
 * <li>Dispatcher</li>
 * <ul>
 * <br/>
 * This element parses and creates the following XML schema in its toXML() and
 * fromXML() methods.  The ROOT element is the parameter for those methods.  
 * See the class description for the CADScreenModel and 
 * IncidentSummaryModel_obj Objects for their XML schema.<br/>
 * <ROOT>
 *    <LOG_NUMBER/>
 *    <LOG_STATUS/>
 *    <PRIORITY/>
 *    <TYPE/>
 *    <CALLBOX_NUM/>
 *    <BEAT/>
 *    <FULL_LOC/>
 *    <TRUNC_LOC/>
 *    <ORIGIN/>
 *    <INCIDENT_DATE/>
 *    <INCIDENT_TIME/>
 *    <DISPATCHER/>
 * </ROOT>
 * 
 * @see IncidentSummaryModel_obj
 * @see CADScreenModel
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class IncidentInquiryHeader implements Serializable {
    
    /**
     * Enumeration with XML tag names.
     * @author Matthew Cechini
     */
    private static enum XML_TAGS {
        /** Incident log number. */ 
        LOG_NUMBER    ("LOG_NUMBER"),
        /** Incident log status. */ 
        LOG_STATUS    ("LOG_STATUS"),
        /** Incident priority. */   
        PRIORITY      ("PRIORITY"),
        /** Incident type. */   
        TYPE          ("TYPE"),
        /** Call box number. */ 
        CALLBOX_NUM   ("CALLBOX_NUM"),
        /** Incident beat. */   
        BEAT          ("BEAT"),
        /** Incident full location. */  
        FULL_LOC      ("FULL_LOC"),
        /** Incident truncated location. */ 
        TRUNC_LOC     ("TRUNC_LOC"),
        /** Log origin. */  
        ORIGIN        ("ORIGIN"),
        /** Incident date. */   
        INCIDENT_DATE ("INCIDENT_DATE"),
        /** Incident time. */   
        INCIDENT_TIME ("INCIDENT_TIME"),
        /** Assigned dispatcher. */ 
        DISPATCHER    ("DISPATCHER");
        
        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }       
    }

    /** Incident log number.  */
    public Integer logNumber;
    
    /** Incident log status.  */
    public String logStatus;
    
    /** Incident log priority.  */
    public String priority;
    
    /** Incident log type.  */
    public String type;
    
    /** Call Box number. */
    public String callBoxNumber;
    
    /** Incident beat. */
    public String beat;
    
    /** Incident full location. */
    public String fullLocation;
    
    /** Incident truncated location.  */
    public String truncLocation;
    
    /** 
     *  Incident log origin. Format: XXAYYYYY.  
     *  XX = log position.  YYYYY = CAD user id.
     */
    public String origin;
    
    /** Incident log creation date. Format: MMDD */
    public String incidentDate;
    
    /** Incident log create time. Format: HHMM */
    public String incidentTime;
    
    /** 
     *Incident log dispatcher.  Format: XXAYYYYY.  
     *  XX = log position.  YYYYY = CAD user id.
     */
    public String dispatcher;
    
    /** Constructor.  Initialize all class variables to 0 or empty string. */
    public IncidentInquiryHeader() {
        logNumber     = new Integer(0);
        logStatus     = "";
        priority      = "";
        type          = "";
        callBoxNumber = "";
        beat          = "";
        fullLocation  = "";
        truncLocation = "";
        origin        = "";
        incidentDate  = "";
        incidentTime  = "";        
        dispatcher    = "";     
        
    }    
    
    /**
     * Copy constructor.
     *
     * @param newIIH Object containing header information used to initialize
     *               the new instance.
     */
    public IncidentInquiryHeader(IncidentInquiryHeader newIIH) {
        logNumber     = new Integer(newIIH.logNumber);
        logStatus     = new String(newIIH.logStatus);
        priority      = new String(newIIH.priority);
        type          = new String(newIIH.type);
        callBoxNumber = new String(newIIH.callBoxNumber);
        beat          = new String(newIIH.beat);
        fullLocation  = new String(newIIH.fullLocation);
        truncLocation = new String(newIIH.truncLocation);
        origin        = new String(newIIH.origin);
        incidentDate  = new String(newIIH.incidentDate);
        incidentTime  = new String(newIIH.incidentTime);
        dispatcher    = new String(newIIH.dispatcher);
    }    
    
    /**
     * Parse the paramater node ad assign all member data values.
     *
     * @param theNode Node containing XML data to parse header information from.
     */
    public IncidentInquiryHeader(Node theNode) {    
        fromXML(theNode);
    }
    
    
    /**
     * Updates the local member data with any member data in the parameter
     * Header object that is not 0 or an empty string.
     *
     * TODO look at his method
     * @param newHeader Object containing header information for update.
     */
    public void update(IncidentInquiryHeader newHeader) {
        
        if(newHeader.logNumber != 0) 
            logNumber     = new Integer(newHeader.logNumber);
        if(newHeader.logStatus.trim().length() > 0) 
            logStatus     = new String(newHeader.logStatus);
        if(newHeader.priority.trim().length() > 0) 
            priority      = new String(newHeader.priority);     
        if(newHeader.type.trim().length() > 0) 
            type          = new String(newHeader.type);
        if(newHeader.beat.trim().length() > 0) 
            beat          = new String(newHeader.beat);
        if(newHeader.fullLocation.trim().length() > 0) 
            fullLocation  = new String(newHeader.fullLocation);
        if(newHeader.truncLocation.trim().length() > 0) 
            truncLocation = new String(newHeader.truncLocation);
        if(newHeader.incidentDate.trim().length() > 0) 
            incidentDate  = new String(newHeader.incidentDate);             
        if(newHeader.incidentTime.trim().length() > 0) 
            incidentTime  = new String(newHeader.incidentTime);         
    
    }    
    
    
    public void toXML(Element currElem) {
        
        Document theDoc    = currElem.getOwnerDocument();
        Element  tempElem  = null;
        
        tempElem = theDoc.createElement(XML_TAGS.LOG_NUMBER.tag);
        tempElem.appendChild(theDoc.createTextNode(logNumber.toString()));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.LOG_STATUS.tag);
        tempElem.appendChild(theDoc.createTextNode(logStatus));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.PRIORITY.tag);
        tempElem.appendChild(theDoc.createTextNode(priority));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.TYPE.tag);
        tempElem.appendChild(theDoc.createTextNode(type));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.CALLBOX_NUM.tag);
        tempElem.appendChild(theDoc.createTextNode(callBoxNumber));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.BEAT.tag);
        tempElem.appendChild(theDoc.createTextNode(beat));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.FULL_LOC.tag);
        tempElem.appendChild(theDoc.createTextNode(fullLocation));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.TRUNC_LOC.tag);
        tempElem.appendChild(theDoc.createTextNode(truncLocation));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.ORIGIN.tag);
        tempElem.appendChild(theDoc.createTextNode(origin));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.INCIDENT_DATE.tag);
        tempElem.appendChild(theDoc.createTextNode(incidentDate));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.INCIDENT_TIME.tag);
        tempElem.appendChild(theDoc.createTextNode(incidentTime));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.DISPATCHER.tag);
        tempElem.appendChild(theDoc.createTextNode(dispatcher));
        currElem.appendChild(tempElem);
    }    
    
    public void fromXML(Node modelNode) {   
        
        Node childNode = null;
        
        childNode = modelNode.getFirstChild();
        logNumber = Integer.parseInt(childNode.getTextContent());
        
        childNode = childNode.getNextSibling();
        logStatus = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        priority = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        type = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        callBoxNumber = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        beat = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        fullLocation = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        truncLocation = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        origin = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        incidentDate = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        incidentTime = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        dispatcher = childNode.getTextContent();
            
    }      
}