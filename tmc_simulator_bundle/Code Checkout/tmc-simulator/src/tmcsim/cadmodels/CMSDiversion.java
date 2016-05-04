package tmcsim.cadmodels;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * CMSDiversion is a container class used to hold information for a single 
 * diversion that may be applied at a changeable message sign.  This 
 * information includes the original path, new diversion path, paramics 
 * diversion string, maximum diversion percentage, applied diversion 
 * percentage, and simulation time the diversion was applied.  The updated
 * and cleared flags are used to specify whether the diversion has been
 * recently changed or is cleared.
 * 
 * @author  Matthew Cechini
 * @version 
 */
@SuppressWarnings("serial")
public class CMSDiversion implements Serializable {

    /**
     * Enumeration with XML tag names.
     * @author Matthew Cechini
     */
    private static enum XML_TAGS {
        /** Diversion path. */
        DIVERSION_PATH ("Diversion_path"),
        /** Diversion percentage. */
        PERCENTAGE     ("Percentage"),
        /** Diversion identifier. */
        IDENTIFIER     ("Identifier");
        
        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }
    }
    
    /** Boolean flag to designate whether the diversion percentage has changed.*/
    private boolean updated;
    
    /** Boolean flag to designate whether the diversion percentage is 0.  */
    private boolean cleared;   
    
    /** Original path of traffic. */
    public String  originalPath;

    /** New path traffic will be diverted to. */
    public String  newPath;

    /** Paramics diversion path. */
    public String  diversionPath;

    /** Maximum diversion percentage. */
    public Integer maxDiversionPercent;

    /** Current diversion percentage. */
    private Integer currDiversionPercent;

    /** Time the current diversion percentage was applied. */
    public Long timeApplied;

    
    /**
     * Constructor.
     * 
     * @param oPath Original path traffic would take.
     * @param nPath New path traffic will take.
     * @param dPath Paramics diversion path.
     * @param maxPerc Maximum diversion percentage.
     */
    public CMSDiversion(String oPath, String nPath, String dPath, Integer maxPerc) {

        originalPath         = oPath;
        newPath              = nPath;
        diversionPath        = dPath;
        maxDiversionPercent  = maxPerc;
        currDiversionPercent = new Integer(0);
        timeApplied          = new Long(0);
        
        updated = false;
        cleared = (currDiversionPercent == 0);              
    }
    
    /**
     * Get the current diversion percentage.
     * 
     * @return Integer value of current diversion percentage.
     */
    public int getCurrDiv() { 
        return currDiversionPercent.intValue();
    }
    
    /**
     * Set the cleared flag to true if the new diversion is zero, false
     * if not.  Set the updated flag if the new diversion percentage is
     * differenct than the previous applied diversion, false if not.  
     * Set the current diversion percentage to the parameter value.
     * 
     * @param newDiv New applied diversion percentage.
     */
    public void setCurrDiv(int newDiv) {
        
        cleared = (newDiv == 0);
        updated = (newDiv != currDiversionPercent);
            
        currDiversionPercent = newDiv;          
    }
    
    /**
     * Determine whether this diversion has been updated in its last use.
     * 
     * @return True if diversion has been updated, false if not.
     */
    public boolean isUpdated() { return updated;}
    
    /**
     * Determine if the current diversion has been cleared.
     * 
     * @return True if diversion is zero, false if not.
     */
    public boolean isCleared() { return cleared;}       
    
    /**
     * Reset this diversion by ...
     */
    public void reset() {
        currDiversionPercent = 0;
        
        updated = false;
        cleared = (currDiversionPercent == 0);      
    }
    
    /**
     * Write the XML output for the diversion information 
     * represented in this object.  THe format is as follows:
     * <Diversion_path>
     *    <Identifier/>
     *    <Percentage/>
     * <Diversion_path>
     * 
     * @param xmlOut XMLWriter used for XML creation.
     */
    public void toXML(Element currElem) {
        
        Document theDoc = currElem.getOwnerDocument();
        
        Element divPathElem = theDoc.createElement(XML_TAGS.DIVERSION_PATH.tag);
        currElem.appendChild(divPathElem);
        
        Element idElem = theDoc.createElement(XML_TAGS.IDENTIFIER.tag);
        idElem.appendChild(theDoc.createTextNode(diversionPath));
        divPathElem.appendChild(idElem);

        Element pctElem = theDoc.createElement(XML_TAGS.PERCENTAGE.tag);
        pctElem.appendChild(theDoc.createTextNode(String.valueOf(currDiversionPercent)));
        divPathElem.appendChild(pctElem);
        
    }
}   
