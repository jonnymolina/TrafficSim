package tmcsim.cadmodels;


import java.io.Serializable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * IncidentInquiryDetails extends from IncidentInquiryLogEntry to provide a 
 * model object containing information used to display a detail log entry.
 * Data for a detail includes a String of text.<br/>
 * <br/>
 * This element parses and creates the following XML schema in its toXML() and
 * fromXML() methods.  The ROOT element is the parameter for those methods.<br/>
 * <ROOT><br/>
 *    <TEXT/><br/>
 *    <POSITION_INFO/><br/>
 *    <TIMESTAMP/><br/>
 * </ROOT><br/>
 * 
 * @author Matthew Cechini
 * @version 
 */
@SuppressWarnings("serial")
public class IncidentInquiryDetails extends IncidentInquiryLogEntry 
    implements Serializable {
        
    /**
     * Enumeration with XML tag names.
     * @author Matthew Cechini
     */
    private static enum XML_TAGS {
        /** Detail Text. */
        TEXT  ("TEXT");
        
        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }       
    }
    
    
    /** Detail text. */
    public String details;
    
    /**  */
    public Boolean sensitive;
    
    //public boolean highlighted;
    
    /** 
     * Constructor.  Initialize all detail data to empty strings or false.
     * 
     *  @param newPosInfo String containing position info for this log entry
     */    
    public IncidentInquiryDetails(String newPosInfo, Boolean sens) {
        super(newPosInfo, "0000");

        details   = "";
        sensitive = sens;
    }
    
    /** 
     * Constructor.  Initialize all detail data to the parameter.
     * 
     *  @param newPosInfo String containing position info for this log entry.
     *  @param newDetails String containing detail text for this log entry.
     */        
    public IncidentInquiryDetails(String newPosInfo, String newDetails, Boolean sens) {     
        super(newPosInfo, "0000");
        
        details   = newDetails;
        sensitive = sens;
    }
    
    /**
     * Constructor.  Parse parameter node for Detail log entry data.
     * 
     * @param theNode Node containing data for this Detail log entry
     */    
    public IncidentInquiryDetails(Node theNode) {   
        fromXML(theNode);
    }
    
    public void toXML(Element currElem) {       
        Document theDoc    = currElem.getOwnerDocument();
        Element  tempElem  = null;
        
        tempElem = theDoc.createElement(XML_TAGS.TEXT.tag);
        tempElem.appendChild(theDoc.createTextNode(details));
        currElem.appendChild(tempElem);

        super.toXML(currElem);
    }
    
    public void fromXML(Node modelNode) {   
        
        Node childNode = null;
        
        childNode = modelNode.getFirstChild();
        details = childNode.getTextContent();

        childNode = childNode.getNextSibling();
        super.fromXML(childNode);
        
    }
 
    /** 
     * Parse the details text into a list of endline terminated strings which are
     * no longer than the parameter width.  Words which would be split by an endline
     * are moved in whole to the next line.  Words longer than a line width are split 
     * accordingly.
     * 
     * @param width Line width used for parsing details
     * @return A vector of endline terminated detail strings.
     */
    public Vector<String> parseDetails(int width) {
        Vector<String> parsedDetails = new Vector<String>();
        
        int currPos  = 0;
        int spaceIdx = 0;
        
        while((details.length() - currPos) > width) {
            
            spaceIdx = details.substring(currPos, currPos + width).lastIndexOf(" ");
            
            if(spaceIdx > 0) {
                parsedDetails.add(details.substring(currPos, currPos + spaceIdx).trim() + "\n");
                currPos += spaceIdx;
            }
            else {
                parsedDetails.add(details.substring(currPos, currPos + width).trim() + "\n");   
                currPos += width;
            }                               
        }       
        
        //remainder
        parsedDetails.add(details.substring(currPos).trim() + "\n");        
        
        return parsedDetails;
    }    
    
    
    
}