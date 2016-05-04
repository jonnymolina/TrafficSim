package tmcsim.cadsimulator.db;

import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tmcsim.cadmodels.CMSInfo;

/**
 * CMSDiversionDB is a singleton object that acts as a database containing 
 * the CMS Diversion information.  The initial data is read in from an XML 
 * file.  This object is accessed to get current CMS diversion information
 * and to be updated with new diversions as they are set by the user.  
 * Diversions may be reset to their original values (0) through the
 * resetDiversions() method. 
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/14 00:12:38 $ $Revision: 1.4 $
 */
public class CMSDiversionDB {

    /**
     * Enumeration containing XML tag names used to parse the XML
     * document containing information about CMS diversion control.
     * @author Matthew Cechini
     */
    private static enum CMS_DB_TAGS {   
        /** This is the top level tag. */
        CMS           ("CMS"),
        /** Individual diversion data. */
        DIVERSION     ("DIVERSION"),
        /** CMS ID. */
        ID            ("ID"),
        /** CMS Postmile. */
        POSTMILE      ("POSTMILE"),
        /** Diversion initial route. */
        INIT_ROUTE    ("INIT_ROUTE"),
        /** Diversion original path. */
        ORIG_PATH     ("ORIG_PATH"),
        /** Diversion new path. */
        NEW_PATH      ("NEW_PATH"),
        /** Diversion path. */
        DIV_PATH      ("DIV_PATH"), 
        /** Max diversion percentage. */
        MAX_DIVERSION ("MAX_DIVERSION");
        
        /** XML Tag name. */
        public String tag;
        
        private CMS_DB_TAGS(String t) {
            tag = t;
        }
    }
    
    /** The CMSDiversionDB Singleton. */
    private static CMSDiversionDB theDB;
    
    /**
     * The map of all CMSInfo Objects (value), referenced by a 
     * CMS ID String(key).
     */
    private TreeMap<String, CMSInfo> diversionMap = null;
    
    
    /** Constructor.  Initialize the diversion map. */
    private CMSDiversionDB() {
        diversionMap = new TreeMap<String, CMSInfo>();  
    }
    
    /**
     * Get the current instance of the CMS Diversion DB.  Construct the
     * singleton if it has not yete been constructed.
     */
    public synchronized static CMSDiversionDB getInstance() {
        
        if(theDB == null)
            theDB = new CMSDiversionDB();
            
        return theDB;
    }
    
    /**
     * Load the CMS Diversion map from an xml document, adhering to the 
     * following schema: <br/>
     * <CMS><br/>
     *      <ID/><br/>
     *      <POSTMILE/><br/>
     *      <INIT_ROUTE/><br/>
     *      <DIVERSION><br/>
     *           <ORIG_PATH/><br/>
     *           <NEW_PATH/><br/>
     *           <DIV_PATH/><br/>
     *           <MAX_DIVERSION/><br/>
     *      </DIVERSION><br/>
     *      
     *      <DIVERSION/>...<br/>
     * </CMS>
     */
    public void loadFromXML(Document newDoc) {
        
        Element rootElement      = newDoc.getDocumentElement();
        Element cmsElement       = null;
        Element diversionElement = null;
        NodeList diversions      = null;
        
        CMSInfo cmsInfo = null;
        String  cmsID   = null;
        
        
        NodeList cms_signs  = rootElement.getElementsByTagName(CMS_DB_TAGS.CMS.tag);
        
        for(int i = 0; i < cms_signs.getLength(); i++) {
            
            cmsElement = (Element)cms_signs.item(i);
            
            cmsID = cmsElement.getElementsByTagName(CMS_DB_TAGS.ID.tag).item(0)
                    .getTextContent();
            
            cmsInfo = diversionMap.get(cmsID);
            
            if (cmsInfo == null) {
                cmsInfo = new CMSInfo(cmsID, new Float(cmsElement
                        .getElementsByTagName(CMS_DB_TAGS.POSTMILE.tag).item(0)
                        .getTextContent()), cmsElement.getElementsByTagName(
                        CMS_DB_TAGS.INIT_ROUTE.tag).item(0).getTextContent());
            }                   
            
            //parse diversions for CMS
            diversions = cmsElement.getElementsByTagName(CMS_DB_TAGS.DIVERSION.tag);

            for(int j = 0; j < diversions.getLength(); j++) {

                diversionElement = (Element)diversions.item(j);
                
                cmsInfo.addNewDiversion(
                        diversionElement.getElementsByTagName(CMS_DB_TAGS.ORIG_PATH.tag).item(0).getTextContent(),
                        diversionElement.getElementsByTagName(CMS_DB_TAGS.NEW_PATH.tag).item(0).getTextContent(),
                        diversionElement.getElementsByTagName(CMS_DB_TAGS.DIV_PATH.tag).item(0).getTextContent(),
                        new Integer(diversionElement.getElementsByTagName(CMS_DB_TAGS.MAX_DIVERSION.tag).item(0).getTextContent()));
                    
            }
            
            diversionMap.put(cmsInfo.cmsID, cmsInfo);
            
        }               
    }
    
    /**
     * Get the current diversion information for the Diversion ID
     *
     * @param ID CMS ID key to get the corresponding CMSInfo object.
     * @returns A CMSInfo object containing diversion info.  Returns null if the
     * parameter ID does not exist.
     */
    public CMSInfo getDiversion(String ID) {
        return diversionMap.get(ID);
    }       
    
    /**
     * Update the current diversions with the new CMSInfo object 
     * by putting the parameter CMSInfo object into the local map.
     * 
     * @param theDiversion CMSInfo object containing updated diversions.
     */
    public void updateDiversions(CMSInfo theDiversion) {
        diversionMap.put(theDiversion.cmsID, theDiversion);
    }

    /**
     * Reset all CMSInfo objects.
     */
    public void resetDiversions() {
        for(CMSInfo cms : diversionMap.values()) {
            cms.reset();
        }
    }
    
    /**
     * Get the CMS Diversion map.
     *
     * @returns The CMSInfo map.
     */
    public TreeMap<String, CMSInfo> getAllDiversions() {
        return diversionMap;
    }           
}