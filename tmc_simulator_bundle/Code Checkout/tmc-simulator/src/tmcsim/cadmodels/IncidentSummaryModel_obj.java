package tmcsim.cadmodels;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tmcsim.common.ScriptException;

/*
 * The model object containing data for an entry in the IncidentSummary CAD Screen.
 * 
 * @author
 * @version
 */
public class IncidentSummaryModel_obj {
            
    private static enum XML_TAGS {
        /** Log number. */
        LOG_NUMBER    ("LOG_NUMBER"),
        /** Log status. */
        LOG_STATUS    ("LOG_STATUS"),
        /** Log creation date. */
        DATE          ("DATE"),
        /** Log creation time. */
        TIME          ("TIME"),
        /** Log priority. */
        PRIORITY      ("PRIORITY"),
        /** Log type. */
        TYPE          ("TYPE"),
        /** Log beat area. */
        BEAT_AREA     ("BEAT_AREA"),
        /** Log location. */
        LOCATION      ("LOCATION"),
        /** Log beat assigned. */
        BEAT_ASSIGNED ("BEAT_ASSIGNED");

        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }       
    }
    
    /** Unique log number */
    public Integer logNumber; 
    
    /** Log status */
    public String logStatus;       
    
    /** Log creation date */
    public String date;
    
    /** Log creation time */
    public String time;
    
    /** Log priority */
    public String priority;
    
    /** Log type */
    public String callType;
    
    /** Log beat area */
    public String beatArea;
    
    /** Log location */
    public String location;
    
    /** log beat assigned */
    public String beatAssigned;
    
        
    /**
     * Constructor.  Initialize all model data to empty strings.
     */
    public IncidentSummaryModel_obj() {
        logNumber    = new Integer(0);
        logStatus    = "";
        date         = "";
        time         = "";
        priority     = "";  
        callType     = "";
        beatArea     = "";
        location     = "";
        beatAssigned = "";
    }
    
    /**
     * Constructor.  Initialize all model data to parameter data.
     * 
     * @param newLogNumber     Incident log number
     * @param newLogStatus     Incident log status
     * @param newDate          Incident creation date
     * @param newTime          Incident create time
     * @param newPriority      Incident priority
     * @param newCallType      Incident type
     * @param newBeatArea      Incident beat area
     * @param newLocation      Incident location
     * @param newBeatAssigned  Incident beat assigned
     */    
    public IncidentSummaryModel_obj(Integer newLogNumber,
                                    String newLogStatus,
                                    String newDate,
                                    String newTime,
                                    String newPriority,
                                    String newCallType,
                                    String newBeatArea,
                                    String newLocation,
                                    String newBeatAssigned) {
        logNumber    = newLogNumber;
        logStatus    = newLogStatus;
        date         = newDate;
        time         = newTime;
        priority     = newPriority; 
        callType     = newCallType;
        beatArea     = newBeatArea;
        location     = newLocation;
        beatAssigned = newBeatAssigned;
    }
    
    /**
     * Constructor.  Initialize all model data with IncidentInquiryHeader.
     * 
     * @param newIncident IncidentInquiryHeader object containing data for
     *                    IncidentSummery model.
     */
    public IncidentSummaryModel_obj(IncidentInquiryHeader newIncident) {
        logNumber    = newIncident.logNumber;  
        logStatus    = newIncident.logStatus;   
        date         = newIncident.incidentDate;
        time         = newIncident.incidentTime;
        priority     = newIncident.priority;
        callType     = newIncident.type;
        beatArea     = newIncident.beat;
        location     = newIncident.fullLocation;
        beatAssigned = newIncident.beat; 
    }
    
    /**
     * Constructor.  Initialize all model data to data in parameter Node.
     * 
     * @param theNode Node containing data for IncidentSummary model.
     */
    public IncidentSummaryModel_obj(Node theNode) throws ScriptException {  
        
        fromXML(theNode);           
        
    }
    
    public void toXML(Element currElem) {
        
        Document theDoc    = currElem.getOwnerDocument();
        Element  tempElem  = null;
        
        currElem.setAttribute(XML_TAGS.LOG_NUMBER.tag, logNumber.toString());
        
        tempElem = theDoc.createElement(XML_TAGS.LOG_STATUS.tag);
        tempElem.appendChild(theDoc.createTextNode(logStatus));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.DATE.tag);
        tempElem.appendChild(theDoc.createTextNode(date));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.TIME.tag);
        tempElem.appendChild(theDoc.createTextNode(time));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.PRIORITY.tag);
        tempElem.appendChild(theDoc.createTextNode(priority));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.TYPE.tag);
        tempElem.appendChild(theDoc.createTextNode(callType));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.BEAT_AREA.tag);
        tempElem.appendChild(theDoc.createTextNode(beatArea));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.LOCATION.tag);
        tempElem.appendChild(theDoc.createTextNode(location));
        currElem.appendChild(tempElem);
        
        tempElem = theDoc.createElement(XML_TAGS.BEAT_ASSIGNED.tag);
        tempElem.appendChild(theDoc.createTextNode(beatAssigned));
        currElem.appendChild(tempElem);
    
    }
    

    public void fromXML(Node modelNode) throws ScriptException {   

        Node childNode = null;
        
        logNumber = Integer.parseInt(modelNode.getAttributes().getNamedItem(
                XML_TAGS.LOG_NUMBER.tag).getNodeValue());
        
        childNode = modelNode.getFirstChild();
        logStatus = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        date = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        time = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        priority = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        callType = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        beatArea = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        location = childNode.getTextContent();
        
        childNode = childNode.getNextSibling();
        beatAssigned = childNode.getTextContent();      
        
    }
}