package tmcsim.client.cadclientgui;

import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import tmcsim.client.cadclientgui.data.Incident;
import tmcsim.client.cadclientgui.data.IncidentEvent;
import tmcsim.client.cadclientgui.data.Unit;
import tmcsim.cadmodels.IncidentInquiryDetails;
import tmcsim.cadmodels.IncidentInquiryHeader;
import tmcsim.cadmodels.IncidentInquiryModel_obj;
import tmcsim.cadmodels.IncidentInquiryServices;
import tmcsim.cadmodels.IncidentInquiryTows;
import tmcsim.cadmodels.IncidentInquiryUnitsAssigned;
import tmcsim.cadmodels.IncidentInquiryWitnesses;
import tmcsim.client.cadclientgui.enums.CADScriptTags.SCRIPT_LEVEL_TAGS;
import tmcsim.client.cadclientgui.enums.CADScriptTags.UNIT_TAGS;
import tmcsim.client.cadclientgui.enums.CADScriptTags.SCRIPT_EVENT_TAGS;
import tmcsim.common.CADScriptTags.AUDIO_TAGS;
import tmcsim.common.CADScriptTags.CAD_INCIDENT_DATA_TAGS;
import tmcsim.common.CADScriptTags.CCTV_TAGS;
import tmcsim.common.CADScriptTags.INCIDENT_HEADER_TAGS;
import tmcsim.common.CADScriptTags.LOCATION_INFO_TAGS;
import tmcsim.common.CADScriptTags.PARAMICS_TAGS;
import tmcsim.common.CADScriptTags.SERVICE_TAGS;
import tmcsim.common.CADScriptTags.TOW_TAGS;
import tmcsim.common.CADScriptTags.WITNESS_TAGS;
import tmcsim.common.CCTVDirections;
import tmcsim.common.CCTVInfo;
import tmcsim.common.ParamicsLocation;
import tmcsim.common.XMLIncident;


/**
 * SAX Handler that parses a script file and creates a list of Incident objects 
 * containing all script information that will be used by the CAD.
 * 
 * @author Matthew Cechini
 * @version
 */
public class ScriptHandler extends DefaultHandler { 

    /** Error Logger. */
    private Logger scriptLogger = Logger.getLogger("tmcsim.common");
    
    /** 
     * Enumeration used to keep track of the current tab level that the
     * parser is reading data in.
     */
    private static enum LEVEL { NONE, TMC_SCRIPT, SCRIPT_EVENT, SCRIPT_DATA,
        NEW_UNIT, CAD_DATA, LOCATION_INFO, CAD_INCIDENT_EVENT, PARAMICS, HEADER_INFO};
    
    /** Buffer used to hold parsed tag content */
    private StringBuffer parsedValue = new StringBuffer();
    
    /** Current Tag level within the script that is being parsed */
    private LEVEL currentLevel       = LEVEL.NONE;
    
    /** Log number for the current ScriptEvent being parsed */
    private Integer currentLogNumber     = 0;
    
    /** Incident description for the current ScriptEvent being parsed */
    private String  currentIncidentDesc  = "";
    
    /** Time index value (in seconds) for the current ScriptEvent being parsed */
    private long    currentEventTime     = 0;
    
    /** ParamicsLocation object for current script event */
    private ParamicsLocation currLoc      = null;
    
    /** IncidentInquiryHeader object for current script event */
    private IncidentInquiryHeader currIIH = null;
    
    /** IncidentEvent object for current script event */
    private IncidentEvent currEvent       = null;
    
    /** XMLIncident object for current script event */
    private XMLIncident currXMLInc        = null;
    
    private TreeMap<String, Unit> unitMap;
    
    private TreeMap<Integer, Incident> incidentMap;
    
    private String currUnitNum;
    
    private Unit currUnit;
    
    private Incident currentIncident;
    
    private long currentIncidentTime;
    
    private String cadDataTag;
    
    
    
    /** Constructor.  Initializes incident map. */
    public ScriptHandler() {
        unitMap = new TreeMap<String, Unit>();
        incidentMap = new TreeMap<Integer, Incident>();
    }
    
    public Vector<Unit> getUnits() {
        return new Vector<Unit>(unitMap.values());
    }   
    
    /**  
     * Get the list of incidents that have been parsed from the script file.
     * 
     * @returns Vector of Incident objects.
     */
    public Vector<Incident> getIncidents() {
        return new Vector<Incident>(incidentMap.values());
    }       
    
    /** SAX Handler method.  Clear incident map and reset the error flag. */
    public void startDocument() {       
        unitMap.clear();
        incidentMap.clear();        
    }   
    
    /** SAX Handler method.   */
    public void startElement(String uri, String localName, String qName, Attributes attributes)  {
        
        try {
            if(qName.equals(SCRIPT_LEVEL_TAGS.TMC_SCRIPT.tag)) {
                currentLevel = LEVEL.TMC_SCRIPT;
            }   
            else if(qName.equals(SCRIPT_LEVEL_TAGS.SCRIPT_DATA.tag))
            {
                currentLevel = LEVEL.SCRIPT_DATA;
            }
            else if(qName.equals(SCRIPT_LEVEL_TAGS.NEW_UNIT.tag))
            {
                currUnitNum = attributes.getValue(UNIT_TAGS.UNIT_NUM.tag);
                currentLevel = LEVEL.NEW_UNIT;
                if(unitMap.containsKey(currUnitNum))
                {
                    currUnit = unitMap.get(currUnitNum);
                }
                else
                {
                    currUnit = new Unit(currUnitNum);
                }
            }
            else if(qName.equals(SCRIPT_LEVEL_TAGS.SCRIPT_EVENT.tag)) {
                currentLevel = LEVEL.SCRIPT_EVENT;
            }
            else if(qName.equals(SCRIPT_LEVEL_TAGS.CAD_DATA.tag)) {
                currentLevel = LEVEL.CAD_DATA;              
            }
            else if(qName.equals(SCRIPT_LEVEL_TAGS.HEADER_INFO.tag)) {
                currIIH           = new IncidentInquiryHeader();
                currIIH.logNumber = currentLogNumber;
                
                currentLevel = LEVEL.HEADER_INFO;               
            }
            else if(qName.equals(SCRIPT_LEVEL_TAGS.LOCATION_INFO.tag)) {
                currLoc      = new ParamicsLocation(attributes.getValue(
                        LOCATION_INFO_TAGS.ID.tag));
                currentLevel = LEVEL.LOCATION_INFO;             
            }
            else if(qName.equals(SCRIPT_LEVEL_TAGS.CAD_INCIDENT_EVENT.tag)) {
                currEvent    = new IncidentEvent(currentEventTime - incidentMap.get(
                        currentLogNumber).getSecondsToStart());         
                currentLevel = LEVEL.CAD_INCIDENT_EVENT;
            }
            else if(qName.equals(SCRIPT_LEVEL_TAGS.PARAMICS.tag)) {
                String locationID = attributes.getValue(
                        PARAMICS_TAGS.LOCATION_ID.tag);
                
                currXMLInc = new XMLIncident(locationID, incidentMap.get(
                        currentLogNumber).locationMap.get(locationID));
                currentLevel = LEVEL.PARAMICS;
            }
            else if(qName.equals(SCRIPT_EVENT_TAGS.INCIDENT.tag)) {
                try {
                    currentLogNumber = Integer.parseInt(attributes.getValue(
                            SCRIPT_EVENT_TAGS.LOG_NUMBER.tag));
                }
                catch (Exception e)  { 
                    scriptLogger.logp(Level.SEVERE, "ScriptHandler", "startElement", 
                            "Invalid LogNumber " + attributes.getValue(
                                    SCRIPT_EVENT_TAGS.LOG_NUMBER.tag), e);
                    currentLogNumber = 0;
                }               
            }
            else if(qName.equals(CAD_INCIDENT_DATA_TAGS.UNIT.tag)) {
                IncidentInquiryUnitsAssigned iiu = new IncidentInquiryUnitsAssigned(IncidentInquiryModel_obj.SCRIPT_POS_INFO);
                
                iiu.beat = attributes.getValue(tmcsim.common.CADScriptTags.UNIT_TAGS.UNIT_NUMBER.tag);
                iiu.statusType = attributes.getValue(tmcsim.common.CADScriptTags.UNIT_TAGS.UNIT_STATUS.tag);
                iiu.isPrimary  = new Boolean(attributes.getValue(tmcsim.common.CADScriptTags.UNIT_TAGS.UNIT_PRIMARY.tag)).booleanValue();
                iiu.isActive   = new Boolean(attributes.getValue(tmcsim.common.CADScriptTags.UNIT_TAGS.UNIT_ACTIVE.tag)).booleanValue();
                
                currEvent.eventInfo.addUnit(iiu);
            }
            else if(qName.equals(CAD_INCIDENT_DATA_TAGS.WITNESS.tag)) {
                IncidentInquiryWitnesses iiw = new IncidentInquiryWitnesses(IncidentInquiryModel_obj.SCRIPT_POS_INFO);
    
                iiw.reportingParty = attributes.getValue(WITNESS_TAGS.WITNESS_NAME.tag);
                iiw.telephoneNum   = attributes.getValue(WITNESS_TAGS.WITNESS_PHONE.tag);
                iiw.address        = attributes.getValue(WITNESS_TAGS.WITNESS_ADDRESS.tag);
                                
                currEvent.eventInfo.addWitness(iiw);
            }
            else if(qName.equals(CAD_INCIDENT_DATA_TAGS.TOW.tag)) {
                IncidentInquiryTows iit = new IncidentInquiryTows(IncidentInquiryModel_obj.SCRIPT_POS_INFO);
                
                iit.towCompany     = attributes.getValue(TOW_TAGS.TOW_COMPANY_NAME.tag);
                iit.confPhoneNum   = attributes.getValue(TOW_TAGS.CONF_PHONE_NUM.tag);
                iit.publicPhoneNum = attributes.getValue(TOW_TAGS.PUBLIC_PHONE_NUM.tag);
                iit.beat           = attributes.getValue(TOW_TAGS.BEAT.tag);
                
                currEvent.eventInfo.addTow(iit);                
            }
            else if(qName.equals(CAD_INCIDENT_DATA_TAGS.SERVICE.tag)) {
                IncidentInquiryServices iis = new IncidentInquiryServices(IncidentInquiryModel_obj.SCRIPT_POS_INFO);
                
                iis.serviceName    = attributes.getValue(SERVICE_TAGS.SERVICE_NAME.tag);
                iis.confPhoneNum   = attributes.getValue(SERVICE_TAGS.CONF_PHONE_NUM.tag);
                iis.publicPhoneNum = attributes.getValue(SERVICE_TAGS.PUBLIC_PHONE_NUM.tag);
                
                currEvent.eventInfo.addService(iis);        
            }       
            else if(qName.equals(CAD_INCIDENT_DATA_TAGS.AUDIO.tag)) {
                currEvent.waveFile   = attributes.getValue(AUDIO_TAGS.FILE_PATH.tag);
                currEvent.waveLength = new Integer(attributes.getValue(AUDIO_TAGS.FILE_LENGTH.tag)).intValue();
            }
            else if(qName.equals(CAD_INCIDENT_DATA_TAGS.CCTV_INFO.tag)) {
                CCTVInfo newInfo = new CCTVInfo();
                
                try {
                    newInfo.cctv_id   = Integer.parseInt(attributes.getValue(CCTV_TAGS.CCTV_ID.tag));
                    newInfo.direction = CCTVDirections.fromChar(attributes.getValue(CCTV_TAGS.CCTV_DIR.tag).charAt(0));
                    newInfo.toggle    = Boolean.parseBoolean(attributes.getValue(CCTV_TAGS.CCTV_TOGGLE.tag));
                
                    currEvent.cctvInfos.add(newInfo);
                } 
                catch (Exception e) {
                    scriptLogger.logp(Level.SEVERE, "ScriptHandler", "startElement", 
                            "Exception in parsing CCTV_INFO node.", e);
                }
                            
            }
            else if(currentLevel == LEVEL.CAD_DATA)
            {
                if(cadDataTag == null)
                {
                    cadDataTag = qName;
                }
            }

        } catch (Exception e) {
            scriptLogger.logp(Level.SEVERE, "ScriptHandler", "startElement", 
                    "Exception in starting element <" + qName + ">.", e);
        }
    }

    /** SAX Handler method.  Append read characters to local buffer. */
    public void characters(char[] ch, int start, int length) {      
        parsedValue.append(new String(ch, start, length).trim());               
    }

    /** SAX Handler method. */
    public void endElement(String uri, String localName, String qName)  {

        try {
            if(qName.equals(SCRIPT_LEVEL_TAGS.SCRIPT_DATA.tag))
            {
                currentLevel = LEVEL.TMC_SCRIPT;
            }
            else if(qName.equals(SCRIPT_LEVEL_TAGS.NEW_UNIT.tag))
            {
                unitMap.put(currUnitNum, currUnit);
                currentLevel = LEVEL.SCRIPT_DATA;
            }
            else if(qName.equals(SCRIPT_LEVEL_TAGS.SCRIPT_EVENT.tag)) {
                currentLevel = LEVEL.TMC_SCRIPT;
            }
            else if(qName.equals(SCRIPT_LEVEL_TAGS.CAD_DATA.tag)) {
                currentLevel = LEVEL.SCRIPT_EVENT;
            }
            else if(qName.equals(SCRIPT_LEVEL_TAGS.HEADER_INFO.tag)) {
                incidentMap.get(currentLogNumber).header = currIIH;
                currentLevel = LEVEL.CAD_DATA;
            }
            else if(qName.equals(SCRIPT_LEVEL_TAGS.LOCATION_INFO.tag)) {
                incidentMap.get(currentLogNumber).locationMap.put(currLoc.locationID, currLoc);
                currentLevel = LEVEL.CAD_DATA;
            }
            else if(qName.equals(SCRIPT_LEVEL_TAGS.CAD_INCIDENT_EVENT.tag)) {
                incidentMap.get(currentLogNumber).addEvent(currEvent);
                currentLevel = LEVEL.CAD_DATA;
            }
            else if(qName.equals(SCRIPT_LEVEL_TAGS.PARAMICS.tag)) {     
                currEvent.XMLIncidents.add(currXMLInc);
                currentLevel = LEVEL.CAD_INCIDENT_EVENT;
            }
            else if(qName.equals(SCRIPT_EVENT_TAGS.INCIDENT.tag)) {
                currentIncidentDesc = parsedValue.toString();
                
                if(incidentMap.get(currentLogNumber) == null) {             
                
                    incidentMap.put(currentLogNumber, 
                            new Incident(currentLogNumber, 
                                         currentIncidentDesc, 
                                         currentEventTime));
                }
                currentIncident = incidentMap.get(currentLogNumber);
            }
            else if(qName.equals(SCRIPT_EVENT_TAGS.TIME_INDEX.tag)) {
                currentEventTime = timeBytesToSeconds(parsedValue.toString().trim());
            }
            else if(qName.equals(INCIDENT_HEADER_TAGS.TYPE.tag)){
                currIIH.type = parsedValue.toString();
            }
            else if(qName.equals(INCIDENT_HEADER_TAGS.BEAT.tag)){
                currIIH.beat = parsedValue.toString();
            }
            else if(qName.equals(INCIDENT_HEADER_TAGS.FULL_LOCATION.tag)){
                currIIH.fullLocation = parsedValue.toString();
            }
            else if(qName.equals(INCIDENT_HEADER_TAGS.TRUNC_LOCATION.tag)){
                currIIH.truncLocation = parsedValue.toString();
            }
            else if(qName.equals(INCIDENT_HEADER_TAGS.LOG_NUMBER.tag)){
                currIIH.logNumber = Integer.parseInt(parsedValue.toString());
            }
            else if(qName.equals(CAD_INCIDENT_DATA_TAGS.DETAIL.tag)) {
                currEvent.eventInfo.addDetail(new IncidentInquiryDetails(
                        IncidentInquiryModel_obj.SCRIPT_POS_INFO, 
                        parsedValue.toString(), false));
            }   
            else if(currentLevel == LEVEL.PARAMICS) {
                try {
                    currXMLInc.readXMLNode(qName, parsedValue.toString());
                }
                catch (Exception e) {
                    scriptLogger.logp(Level.SEVERE, "ScriptHandler", "endElement", 
                            "Exception in parsing PARAMICS node.", e);
                }
            }
            else if(currentLevel == LEVEL.LOCATION_INFO) {
                currLoc.readXMLNode(qName, parsedValue.toString());
            }
            else if(currentLevel == LEVEL.NEW_UNIT)
            {
                currUnit.readXMLNode(qName, parsedValue.toString());
            }
            else if(currentLevel == LEVEL.CAD_DATA)
            {
                currentIncident.readXMLNode(cadDataTag, qName, parsedValue.toString());
            }
            if(qName.equals(cadDataTag)) 
            {
                cadDataTag = null;
            }
    
            parsedValue.setLength(0);
        } catch (Exception e) {
            scriptLogger.logp(Level.SEVERE, "ScriptHandler", "endElement", 
                    "Exception in ending element <" + qName + ">.", e);
        }
    }   
    
    public void endDocument() {
    }
    
    public void error(SAXParseException e) {
        scriptLogger.logp(Level.SEVERE, "ScriptHandler", "error", 
                "SAX Parsing error.", e);
    }
    
    public void fatalError(SAXParseException e) {
        scriptLogger.logp(Level.SEVERE, "ScriptHandler", "fatalError", 
                "SAX Parsing fatal error.", e);
    }
    
    public void warning(SAXParseException e) {
        scriptLogger.logp(Level.SEVERE, "ScriptHandler", "warning", 
                "SAX Parsing warning.", e);
    }
    

    /**
     * Private method to convert a time object from format HH:MM:SS to a long value of the
     * corresponding number of seconds.
     *
     * @param time String time representation of format HH:MM:SS
     * @return long Number of seconds
     * @throws StringIndexOutOfBoundsException if the input parameter is not valid
     */
    private long timeBytesToSeconds(String time) 
        throws StringIndexOutOfBoundsException
    {
        long seconds = 0;

        seconds =  ((long) Character.digit(time.charAt(0), 10) * 36000 +
                        Character.digit(time.charAt(1), 10) * 3600 +
                        Character.digit(time.charAt(3), 10) * 600 + 
                        Character.digit(time.charAt(4), 10) * 60 + 
                        Character.digit(time.charAt(6), 10) * 10 +
                        Character.digit(time.charAt(7), 10));

              
        return seconds;
    }       
    
    
}