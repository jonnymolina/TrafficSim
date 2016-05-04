package tmcsim.cadmodels;


import java.io.Serializable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * CMSInfo is a container class to contain the id, postmile, initial route, 
 * and diversion information for a single changeable message sign.  The 
 * isCleared() method is used to determine if all diversions for this sign are 
 * cleared.  The isUpdated() methods determines if any of the diversions for 
 * this sign have been updated from user input. 
 *
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class CMSInfo implements Serializable {
    
    /**
     * Enumeration with XML tag names.
     * @author Matthew Cechini
     */
    private static enum XML_TAGS {
        /** Diversion info. */
        DIVERSION ("DIVERSION");

        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }
    }

    /** The unique ID for this CMS. */
    public String  cmsID;

    /** The postmile for this CMS. */
    public Float   postmile;

    /** The initial route where this CMS is located. */
    public String  initialRoute;

    /** Vector of all possible diversions for this CMS. */
    public Vector<CMSDiversion>  possibleDiversions;

    /**
     * Constructor.  
     */
    public CMSInfo() {
        cmsID              = new String();
        postmile           = new Float(0);
        initialRoute       = new String();
        possibleDiversions = new Vector<CMSDiversion>();    
    }
    
    /**
     * Constructor.
     * 
     * @param newID CMS ID
     * @param newPM CMS postmile
     * @param newInitRt CMS initial route
     */
    public CMSInfo(String newID, Float newPM, String newInitRt) {       
        cmsID              = newID;
        postmile           = newPM;
        initialRoute       = newInitRt;
        possibleDiversions = new Vector<CMSDiversion>();    
    }
    
    /**
     * Add a new CMS diversion option.  A new diversion includes information
     * regarding the old and new paths that traffic will be diverted from and to.
     * The diversion also includes the Paramics diversion string and the maximum
     * percentage of traffic that can be diverted.
     * 
     * @param oPath Old path.
     * @param nPath New path.
     * @param dPath Paramics diversion path.
     * @param perc Maximum diversion percentage.
     */
    public void addNewDiversion(String oPath, String nPath, String dPath, Integer perc) {
        possibleDiversions.add(new CMSDiversion(oPath, nPath, dPath, perc));    
    }

    /**
     * Determines if all possible diversions for this CMS sign are clear.  If
     * so, true is returned, else false is returned.
     * 
     * @return True if all diversions are clear, false if not.
     */
    public boolean isCleared() {
        
        for(CMSDiversion theDiv : possibleDiversions) {                     
            if(!theDiv.isCleared()) 
                return false;           
        }
        return true;        
    }
    
    /**
     * Determines if any possible diversions for this CMS sign have 
     * been upted.  If so, true is returned, else false is returned.
     * 
     * @return True if a diversions has been updated, false if not.
     */    
    public boolean isUpdated() {
        
        for(CMSDiversion div : possibleDiversions) {
            if(div.isUpdated()) 
                return true;            
        }
        return false;       
    }
    
    /**
     * Reset all possible diversions.
     */
    public void reset() {
        for(CMSDiversion div : possibleDiversions) {
            div.reset();
        }
    }
    
    /**
     * Write the XML output for all the diversions represented
     * in this object.  The format is as follows:
     * 

     * 
     * @param xmlOut XMLWriter used for XML creation.

    /**
     * Creates XML tags with the diversion data represented in this object.
     * The XML is creaetd with the following format.  The ROOT element is the 
     * parameter for this method.<br/>
     * <ROOT>
     *    <DIVERSION>
     *       <Diversion_path>
     *          <Identifier/>
     *          <Percentage/>
     *       </Diversion_path>
     *       ...
     *       <Diversion_path/>
     *    </DIVERSION>
     * </ROOT>
     *
     * @param currElem XML Element used as a root for XML tag appending.
     */    
    public void toXML(Element currElem) {           
        
        Document theDoc = currElem.getOwnerDocument();
        
        Element divElem = null;
        
        for(CMSDiversion theDiv : possibleDiversions) {
                        
            if(!theDiv.isCleared() && theDiv.isUpdated()) {

                divElem = theDoc.createElement(XML_TAGS.DIVERSION.tag);
                currElem.appendChild(divElem);

                theDiv.toXML(divElem);              
                
            }
        }                 
    }
}