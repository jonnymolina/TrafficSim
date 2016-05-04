package tmcsim.cadmodels;


import java.io.Serializable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * IncidentInquiryModel_obj is the model object containing all model 
 * information for an IncidentInquiry CAD Screen.  The model data includes
 * a single IncidentInquiryHeader, and lists of IncidentInquiryDetails, 
 * IncidentInquiryAssignedUnits, IncidentInquiryTows, IncidentInquiryWitnesses,
 * and IncidentInquiryServices.  The add???() methods append a new 
 * IncidentInquiry model object to the associated internal list.<br/>
 * <br/>
 * This element parses and creates the following XML schema in its toXML() and
 * fromXML() methods.  The ROOT element is the parameter for those methods.  The
 * specific XML schema for each IncidentInquiry model can be found in its
 * class header.<br/>
 * <ROOT>
 *    <HEADER>
 *    <DETAIL/>
 *    ...
 *    <DETAIL/>
 *    <UNIT/>
 *    ...
 *    <UNIT/>
 *    <WITNESS/>
 *    ...
 *    <WITNESS/>
 *    <TOW/>
 *    ...
 *    <TOW/>
 *    <SERVICE/>
 *    ...
 *    <SERVICE/>
 * </ROOT>
 * 
 * @see IncidentInquiryHeader
 * @see IncidentInquiryDetails
 * @see IncidentInquiryAssignedUnits
 * @see IncidentInquiryTows
 * @see IncidentInquiryWitnesses
 * @see IncidentInquiryServices
 * @author Matthew Cechini
 * @version 
 */
@SuppressWarnings("serial")
public class IncidentInquiryModel_obj implements Serializable {
    
    /** Log position info for a log entry.  */
    public static final String SCRIPT_POS_INFO   = "000A17661";     
    
    /**
     * Enumeration with XML tag names.
     * @author Matthew Cechini
     */
    private static enum XML_TAGS {
        /** Header information. */
        HEADER  ("HEADER"),
        /** Detail log entry. */
        DETAIL  ("DETAIL"),
        /** Unit log entry. */
        UNIT    ("UNIT"),
        /** Witness log entry. */
        WITNESS ("WITNESS"),
        /** Tow log entry. */
        TOW     ("TOW"),
        /** Service log entry. */
        SERVICE ("SERVICE"),
        /** Updated entries. */
        UPDATES  ("UPDATES");
    
        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }       
    }
    
    /**  Header data. */
    private IncidentInquiryHeader                header    = null;
    
    /** List of Detail objects. */
    private Vector<IncidentInquiryDetails>       details   = null;
    
    /** List of UnitsAssigned objects. */
    private Vector<IncidentInquiryUnitsAssigned> units     = null;
    
    /** List of Detail objects. */
    private Vector<IncidentInquiryWitnesses>     witnesses = null;
    
    /** List of Tow objects. */
    private Vector<IncidentInquiryTows>          tows      = null;
    
    /** List of Service objects. */
    private Vector<IncidentInquiryServices>      services  = null;  
    
    /*
    private boolean detailsUpdated   = false;
    private boolean unitsUpdated     = false;
    private boolean towsUpdated      = false;
    private boolean witnessesUpdated = false;
    private boolean servicesUpdated  = false;
    */
    
    /** 
     * Origin of this object's data.  Options include "Script" or "CAD #", 
     * where # is a CAD position 
     */
    private String source = "";            
    
    /** 
     * Construtor.  Initialize all local lists of IncidentInquiry objects.  
     * Set source to "Script". 
     */
    public IncidentInquiryModel_obj() {
        source        = "Script";

        header        = new IncidentInquiryHeader();
        details       = new Vector<IncidentInquiryDetails>();
        units         = new Vector<IncidentInquiryUnitsAssigned>();
        witnesses     = new Vector<IncidentInquiryWitnesses>();
        tows          = new Vector<IncidentInquiryTows>();
        services      = new Vector<IncidentInquiryServices>(); 
                      
    }
    
    /**  
     * Constructor.  Initialie all local lists of IncidentInquiry objects.
     * Set srouce to "CAD #".
     * 
     * @param CADPosition Integer value for the originating CAD position of this object's data.
     */
    public IncidentInquiryModel_obj(int CADPosition) {
        source        = "CAD " + CADPosition;

        header        = new IncidentInquiryHeader();
        details       = new Vector<IncidentInquiryDetails>();
        units         = new Vector<IncidentInquiryUnitsAssigned>();
        witnesses     = new Vector<IncidentInquiryWitnesses>();
        tows          = new Vector<IncidentInquiryTows>();
        services      = new Vector<IncidentInquiryServices>(); 
                      
    }    

    /**  
     * Copy Constructor.
     * 
     * @param newIIMO Object containing data to be copied into constructed object.
     */
    public IncidentInquiryModel_obj(IncidentInquiryModel_obj newIIMO) {
        source        = newIIMO.source;
        
        header        = new IncidentInquiryHeader(newIIMO.header);
        details       = new Vector<IncidentInquiryDetails>(newIIMO.details);
        units         = new Vector<IncidentInquiryUnitsAssigned>(newIIMO.units);
        witnesses     = new Vector<IncidentInquiryWitnesses>(newIIMO.witnesses);
        tows          = new Vector<IncidentInquiryTows>(newIIMO.tows);
        services      = new Vector<IncidentInquiryServices>(newIIMO.services); 
    }

    /**
     * This method updates the private lists of IncidentInquiry data with `
     * data contained in the parameter object.  
     * 
     * @param newObject Object containing IncidentInquiry data that will
     *                  be used to update local IncidentInquiry lists.
     */
    public void update(IncidentInquiryModel_obj newObject) {
                        
        header.update(newObject.header);        
        details.addAll(newObject.getDetails());
        
        //Do not want duplicate units in the vector
        for(IncidentInquiryUnitsAssigned unit : newObject.getUnits()) {
            if(units.contains(unit)) {
                IncidentInquiryUnitsAssigned oldUnit = units.get(units.indexOf(unit));
                /*
                if(oldUnit.isActive != unit.isActive ||
                   oldUnit.isPrimary != unit.isPrimary ||
                   oldUnit.statusType != unit.statusType) 
                {
                    unitsUpdated |= true;
                }    
                */
                units.remove(oldUnit);
            }           
            units.add(unit);            
        }
        
        witnesses.addAll(newObject.getWitnesses());
        tows.addAll(newObject.getTows());
        services.addAll(newObject.getServices());        

        /*
        detailsUpdated   |= newObject.getDetails().size() > 0;
        witnessesUpdated |= newObject.getWitnesses().size() > 0;
        towsUpdated      |= newObject.getTows().size() > 0;
        servicesUpdated  |= newObject.getServices().size() > 0;
        */
        
    }
    
    public void toXML(Element currElem) {
        
        Document theDoc = currElem.getOwnerDocument();
        /*
        Element updatesElem = theDoc.createElement(UPDATES);
        currElem.appendChild(updatesElem);
        
        Element updateElem  = theDoc.createElement(DETAIL);
        updateElem.appendChild(theDoc.createTextNode(String.valueOf(detailsUpdated)));
        updatesElem.appendChild(updateElem);
        
        updateElem  = theDoc.createElement(UNIT);
        updateElem.appendChild(theDoc.createTextNode(String.valueOf(unitsUpdated)));
        updatesElem.appendChild(updateElem);
        
        updateElem  = theDoc.createElement(WITNESS);
        updateElem.appendChild(theDoc.createTextNode(String.valueOf(witnessesUpdated)));
        updatesElem.appendChild(updateElem);
        
        updateElem  = theDoc.createElement(TOW);
        updateElem.appendChild(theDoc.createTextNode(String.valueOf(towsUpdated)));
        updatesElem.appendChild(updateElem);
        
        updateElem  = theDoc.createElement(SERVICE);
        updateElem.appendChild(theDoc.createTextNode(String.valueOf(servicesUpdated)));
        updatesElem.appendChild(updateElem);
        */
                
        Element headerElem = theDoc.createElement(XML_TAGS.HEADER.tag);
        header.toXML(headerElem);
        currElem.appendChild(headerElem);
        
        Element detailElem = null;
        for(IncidentInquiryDetails detail : details) {
            detailElem = theDoc.createElement(XML_TAGS.DETAIL.tag);
            detail.toXML(detailElem);
            currElem.appendChild(detailElem);
        }
        
        Element unitElem = null;        
        for(IncidentInquiryUnitsAssigned unit : units) {
            unitElem = theDoc.createElement(XML_TAGS.UNIT.tag);         
            unit.toXML(unitElem);
            currElem.appendChild(unitElem);
        }
        
        Element witnessElem = null;     
        for(IncidentInquiryWitnesses witness : witnesses) {
            witnessElem = theDoc.createElement(XML_TAGS.WITNESS.tag);
            witness.toXML(witnessElem);
            currElem.appendChild(witnessElem);
        }
        
        Element towElem = null;     
        for(IncidentInquiryTows tow : tows) {
            towElem = theDoc.createElement(XML_TAGS.TOW.tag);           
            tow.toXML(towElem);
            currElem.appendChild(towElem);          
        }
        
        Element serviceElem = null;     
        for(IncidentInquiryServices service : services) {
            serviceElem = theDoc.createElement(XML_TAGS.SERVICE.tag);               
            service.toXML(serviceElem);
            currElem.appendChild(serviceElem);
        }  
        
    }
    
    public void fromXML(Node modelNode) {

        Node     currentNode = modelNode;
        //Node     updateNode  = null;
        String   nodeName    = null;
        
        details.clear();
        units.clear();
        witnesses.clear();
        tows.clear();
        services.clear();
        
        /*
        updateNode = currentNode.getFirstChild();
        detailsUpdated = Boolean.parseBoolean(updateNode.getTextContent());
        updateNode = updateNode.getNextSibling();
        unitsUpdated = Boolean.parseBoolean(updateNode.getTextContent());
        updateNode = updateNode.getNextSibling();
        witnessesUpdated = Boolean.parseBoolean(updateNode.getTextContent());
        updateNode = updateNode.getNextSibling();
        towsUpdated = Boolean.parseBoolean(updateNode.getTextContent());
        updateNode = updateNode.getNextSibling();
        servicesUpdated = Boolean.parseBoolean(updateNode.getTextContent());
        */
        
        header = new IncidentInquiryHeader(currentNode);
        
        while((currentNode = currentNode.getNextSibling()) != null) {
            nodeName    = currentNode.getNodeName();    
            
            if(nodeName.equals(XML_TAGS.DETAIL.tag)) { 
                details.add(new IncidentInquiryDetails(currentNode));
            }
            else if(nodeName.equals(XML_TAGS.UNIT.tag)) { 
                units.add(new IncidentInquiryUnitsAssigned(currentNode));
            }
            else if(nodeName.equals(XML_TAGS.WITNESS.tag)) { 
                witnesses.add(new IncidentInquiryWitnesses(currentNode));
            }
            else if(nodeName.equals(XML_TAGS.TOW.tag)) { 
                tows.add(new IncidentInquiryTows(currentNode));
            }
            else if(nodeName.equals(XML_TAGS.SERVICE.tag)) { 
                services.add(new IncidentInquiryServices(currentNode));
            }
       }        
    }
    
    /**
     * Applies the parameter timestamp to all IncidentInquiry objects.
     * 
     * @param timeStamp String containing timestamp
     */
    public void timeStamp(String timeStamp) {
        for(IncidentInquiryDetails detail : details)
            detail.timeStamp(timeStamp);
        
        for(IncidentInquiryUnitsAssigned unit : units) {            
            unit.timeStamp(timeStamp);
        }
        
        for(IncidentInquiryWitnesses witness : witnesses) {
            witness.timeStamp(timeStamp);
        }       
        for(IncidentInquiryTows tow : tows) {           
            tow.timeStamp(timeStamp);           
        }
        for(IncidentInquiryServices service : services) {               
            service.timeStamp(timeStamp);
        }
    }

    /** 
     * Gets the log number value for this IncidentInquiry object.
     * 
     * @return Integer Log number value
     */
    public Integer getLogNumber() {
        return header.logNumber;
    }
    
    /**
     * Sets this objects log number to a new value.
     * 
     * @param logNum New log number.
     */
    public void setLogNumber(Integer logNum) {
        header.logNumber = logNum;
    }
        
    public IncidentInquiryHeader                getHeader()    {return header;}
    public Vector<IncidentInquiryDetails>       getDetails()   {return details;}
    public Vector<IncidentInquiryUnitsAssigned> getUnits()     {return units;}  
    public Vector<IncidentInquiryWitnesses>     getWitnesses() {return witnesses;}
    public Vector<IncidentInquiryTows>          getTows()      {return tows;}
    public Vector<IncidentInquiryServices>      getServices()  {return services;}   
    
    public void setHeader(IncidentInquiryHeader newHeader)      {header = newHeader;}
    public void addDetail(IncidentInquiryDetails newDetail)     {details.add(newDetail);}
    public void addUnit(IncidentInquiryUnitsAssigned newUnit)   {
        if(units.contains(newUnit)) {
            units.remove(newUnit);
        }
        
        units.add(newUnit); 
    }  
    public void addWitness(IncidentInquiryWitnesses newWitness) {witnesses.add(newWitness);}
    public void addTow(IncidentInquiryTows newTow)              {tows.add(newTow);}
    public void addService(IncidentInquiryServices newService)  {services.add(newService);}
    
    /*
    public boolean getDetailsUpdated()   { return detailsUpdated; }
    public boolean getUnitsUpdated()     { return unitsUpdated; }
    public boolean getTowsUpdated()      { return towsUpdated; }
    public boolean getWitnessesUpdated() { return witnessesUpdated; }
    public boolean getServicesUpdated()  { return servicesUpdated; }
    */  
    
    public String getSource() {
        return source;
    }   
}
