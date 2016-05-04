package tmcsim.cadsimulator.paramicscontrol;

import java.util.Iterator;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tmcsim.cadmodels.CMSInfo;
import tmcsim.common.XMLIncident;

/**
 * ParamicsIncidentWriter extends from ParamicsWriter to manage the current 
 * simulation data and transmit update XML Document messages to the 
 * ParamicsCommunicator for simulation control.  The updateIncident() and 
 * updateDiversion() methods are used to notify this class of changes in current 
 * incidents and diversions.  This data is converted to an XML Document and 
 * transmitted to the ParamicsCommunicator everytime the sendUpdate() method is 
 * called.  This method will not transmit simulation updates until a network is 
 * loaded.  To load a network, call the loadNetwork() method to transmit the 
 * registration information to paramics, and when paramics has finished warming up, 
 * call the networkLoaded() method to begin update transmission.
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/06 20:46:40 $ $Revision: 1.4 $
 */
public class ParamicsIncidentWriter extends ParamicsWriter {

    /**
     * Enumeration with XML tag names.
     * @author Matthew Cechini
     */
    private static enum XML_TAGS {
        /** CAD incident update. */
        CAD_DATA      ("CAD_DATA"),
        /** Basic update information. */
        BASIC         ("Basic"),
        /** Current simulation incidents. */
        CAD_INCIDENTS ("CAD_Incidents"),
        /** Current simulation data. */
        SIMULATION_DATA ("Simulation_Data"),
        /** Current CMS diversions. */
        MANAGEMENT    ("Management"),
        /** Current communications interval. */
        COMM_INTERVAL ("Comm_Interval"),
        /** Current loaded paramics network. */
        NETWORK_ID    ("Network_ID"),
        /** Boolean value of whether the simulation has started. */
        SIMULATION    ("Simulation"),
        /** Boolean value of whether an incident is current active. */
        INCIDENT      ("Incident"),
        /** Current CAD simulation time. */
        CAD_CLOCK     ("CAD_clock"),
        /** Current simulation speed. */
        SIM_SPEED       ("Simulation_speed"),
        /** Current CAD clock hours value. */
        HOUR            ("hour"),
        /** Current CAD clock minutes value. */
        MINUTE          ("minute"),
        /** Current CAD clock seconds value. */
        SECOND          ("second");
        
        /** Tag name */
        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }

    }
        
    
    /** Flag to designate whether a network has been loaded */
    private boolean networkLoaded = false;
    
    /** Flag to designate whether the simulation is running. */
    private boolean simulation   = false;
    
    /** Flag to designate whether incidents have occured in the simulation. */
    private boolean incident     = false;
    
    /** Flag to designate whether CMS diversions have been set. */
    private boolean management   = false;
    
    /** Loaded Paramics network ID. */
    private int     networkID    = 1;
        
    /** Communications interval for paramics to read incident status files. (in seconds) */
    private int     commInterval = 0;
    
    /** Current simulation speed */
    private int     speed        = 0;
    
    /** Current simulation time. */
    private long    simTime      = 0;

    /**
     * A map containing XMLIncident objects for each incident that is being updated.
     */
    private TreeMap<String, XMLIncident> incidentsMap;
    
    /**
     * A map containing the current active CMS diversions.
     */
    private TreeMap<String, CMSInfo> diversionsMap;         
    
    /**
     * A Map containing queued Incident Events.  It is possible to queue one incident event
     * per incident. This happens if an incident update is received, but the previous has not
     * been transmitted yet.  The new updated is queued until the current is transmitted.  There
     * is a possibility that more than one incident event can be received and added to the map, and 
     * the first would be lost.  This would be inidcative of other problems, or bad script creation.
     * Either increase the interval frequencyl, or make more time in script between events.
     */
    private TreeMap<String, XMLIncident> queuedIncidentEvents;
    

    /**
     * Constructor.  Initializes private members.
     * 
     * @param interval Number of seconds beetween transmission of XML update.
     */ 
    public ParamicsIncidentWriter(Integer interval) {
        super();
        
        commInterval = interval;

        incidentsMap         = new TreeMap<String, XMLIncident>();
        diversionsMap        = new TreeMap<String, CMSInfo>();      
        queuedIncidentEvents = new TreeMap<String, XMLIncident>();  
    
    }
    
    /**
     * This method sets the local network ID to the parameter value and sends
     * an updated XML message to load the network.
     * 
     * @param newID Paramics network id.
     */
    public void loadNetwork(int newID) {
        networkID = newID;

        transmitXMLUpdate();
    }
    
    /**
     * Updates the networkLoaded flag to true.
     */
    public void networkLoaded() {
        networkLoaded = true;
    }
    
    /**
     * Set the simulation started flag to true.  If a network has been
     * loaded, send an update to the ParamicsCommunicator.
     */
    public void startSimulation() {
        
        simulation = true;

        if(networkLoaded) {
            sendUpdate(0);      
        }
    }
    
    /**
     * This method resets current simulation data.  All maps of incident data
     * are cleared.  The incident, simulation, and management flags are set to 
     * false.  If a network has been previously loaded, send an update to the 
     * ParamicsCommunicator.
     */
    public void resetSimulation() {
        
        incidentsMap.clear();
        diversionsMap.clear();      
        queuedIncidentEvents.clear();

        simulation = false;
        incident   = false;
        management = false;     
        
        if(networkLoaded) {
            sendUpdate(0);
        }
    }   
    
    /**
     * This method is transmits an XML update object to the ParamicsCommunicator
     * if a network has ben loaded.  After the update object has been formed and 
     * sent, the incident list is updated to remove all cleared Incidents.
     * Any queued IncidentEvents are placed into the map of incidents that will 
     * be used for update creation.  The incident flag is set to true if there
     * are still ongoing incidents, false if not.
     */ 
    public void sendUpdate(long simulationTime) {
        simTime = simulationTime;       

        if(!networkLoaded) return;      
                
        transmitXMLUpdate();
    
        updateIncidentList();
        
        for(String key : queuedIncidentEvents.keySet()) {
            incidentsMap.put(key, queuedIncidentEvents.remove(key));
        }
        
        incident = incidentsMap.size() != 0;
        
    }
    
    /**
     * This method updates the map of XMLIncident objects that are used for 
     * updating the Paramics modeler.  If the parameter XMLIncident is not 
     * currently in the local map of incidents, add it.  If it is in the 
     * map of incidents, queue the new XMLIncident to be processed later.  
     * Set the incident flag to true to signify that an incident exists in the 
     * system.
     * 
     * @param newXML The XMLIncident object that will be used to update
     *               an existing incident.
     */
    public void updateIncident(XMLIncident newXML) {
        
        if(incidentsMap.get(newXML.getIdentifier()) == null ) {  
            incidentsMap.put(newXML.getIdentifier(), newXML);               
        }
        else {
             queuedIncidentEvents.put(newXML.getIdentifier(), newXML);
        }       

        incident = true;                
    }
    
    /**
     * This method updates the map of CMSInfo diversion objects that are used
     * for updating the Paramics modeler.  If the diversion is being cleared, 
     * remove it from the map of diversions.  If it is not being cleared, 
     * update the map with the new CMSInfo object. Set the management flag to be 
     * true if there are diversions remaining, false if not.
     * 
     * @param theDiversion A new CMSDiversion object containing new diversion info.
     */
    public void updateDiversion(CMSInfo theDiversion) {
                
        if(theDiversion.isCleared())
            diversionsMap.remove(theDiversion.cmsID);
        else
            diversionsMap.put(theDiversion.cmsID, theDiversion);
            
        management = diversionsMap.size() > 0;  
    
    }   
    
    /**
     * Update the current map of incidents. If an incident
     * has been cleared, remove it from the list.  Otherwise,
     * call its update() method to set its status to ON_GOING.
     *
     * Set the incident flag to true if there are incidents still
     * in the incident map, false if not.
     */
    private void updateIncidentList() {
        
        Iterator<XMLIncident> incIter = incidentsMap.values().iterator();
        
        while(incIter.hasNext()) {
            XMLIncident inc = incIter.next();
            
            if(inc.isCleared()) 
                incIter.remove();
            else
                inc.update();
        }
        
        incident = incidentsMap.size() != 0;
    }
    
    
    /**
     * Creates an XML Document containing all Paramics update information.  
     * This XML object will then be written to the parent class' writeXML method
     * for transmission to the Paramics Communicator.  Helper methods are used 
     * to create each individual section.  The simulation, incident, and 
     * diversion secions will only appear if they are currently active in the 
     * simulation.  The format of the resulting XML object is as follows:<br>
     * <br>
     * <CAD_DATA><br>
     *    <Basic><br>
     *       <Comm_Interval/><br/>
     *       <Network_ID/><br/>
     *       <Simulation/><br/>
     *       <Incident/><br/>
     *    </Basic>
     *
     *    <Simulation_Data>
     *       <Simulation_speed/><br/>
     *       <CAD_clock><br/>
     *          <hour/><br/>
     *          <minute/><br/>
     *          <second/><br/>
     *          <Location><br/>
     *       </CAD_clock><br/>
     *    </Simulation_Data>
     *    
     *    <CAD_Incidents>
     *       <Incident><br/>
     *          <Identifier/><br/>
     *          <Status/><br/>
     *          <Location><br/>
     *              <Route/><br/>
     *              <Direction/><br/>
     *              <Location_type/><br/>
     *              <Postmile/><br/>
     *          </Location><br/>
     *          <Incident_type/><br/>
     *          <Lanes><br/>
     *             <Lane_number/><br/>
     *             ...
     *          </Lanes><br/>
     *       </Incident><br/>
     *       ...
     *    </CAD_Incidents>   
     *    
     *    <Management>
     *       <Diversion>
     *          <Diversion_path>
     *             <Identifier/>
     *             <Percentage/>
     *          <Diversion_path>
     *          ...
     *       </Diversion>
     *       ...
     *    </Management>
     *  
     * </CAD_DATA>
     * 
     */
    private void transmitXMLUpdate() {

        try {
            
            Document theDoc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
            
            Element cadDataElement = theDoc.createElement(XML_TAGS.CAD_DATA.tag);
            theDoc.appendChild(cadDataElement);
            
            //***** BASIC *****//
            writeBasicXML(cadDataElement);      
            
            //***** SIMULATION *****//
            if(simulation)
                writeSimulationXML(cadDataElement);
            
            
            //***** INCIDENTS *****//
            if(incident)
                writeIncidents(cadDataElement);             
            
            
            //***** MANAGEMENT *****//
            if(management)
                writeManagement(cadDataElement);
            
            writeXML(theDoc);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Appends the <Basic> XML Element to the parameter Element root.
     * The format for this section is as follows:<br>
     * 
     * <Basic>
     *    <Comm_Interval/><br/>
     *    <Network_ID/><br/>
     *    <Simulation/><br/>
     *    <Incident/><br/>
     * </Basic>
     * 
     * @param currElem XML Element to use as a root.
     */
    private void writeBasicXML(Element currElem) {      
                    
        Document theDoc = currElem.getOwnerDocument();
        
        Element basicElem = theDoc.createElement(XML_TAGS.BASIC.tag);
        currElem.appendChild(basicElem);

        Element intervalElement = theDoc.createElement(XML_TAGS.COMM_INTERVAL.tag);
        intervalElement.appendChild(theDoc.createTextNode( String.valueOf(commInterval)));
        basicElem.appendChild(intervalElement);

        Element networkElement = theDoc.createElement(XML_TAGS.NETWORK_ID.tag);
        networkElement.appendChild(theDoc.createTextNode(String.valueOf(networkID)));
        basicElem.appendChild(networkElement);

        Element simElement = theDoc.createElement(XML_TAGS.SIMULATION.tag);
        simElement.appendChild(theDoc.createTextNode(String.valueOf(simulation).toUpperCase()));
        basicElem.appendChild(simElement);

        Element incidentElement = theDoc.createElement(XML_TAGS.INCIDENT.tag);
        incidentElement.appendChild(theDoc.createTextNode(String.valueOf(incident).toUpperCase()));
        basicElem.appendChild(incidentElement);

    }
    
    /**
     * Appends the <Simulation_Data> XML Element to the parameter Element root.
     * The format for this section is as follows:<br>
     * 
     * <Simulation_Data>
     *    <Simulation_speed/><br/>
     *    <CAD_clock><br/>
     *       <hour/><br/>
     *       <minute/><br/>
     *       <second/><br/>
     *       <Location><br/>
     *    </CAD_clock><br/>
     * </Simulation_Data>
     * 
     * @param currElem XML Element to use as a root.
     */
    private void writeSimulationXML(Element currElem) {

        Document theDoc = currElem.getOwnerDocument();
        
        Element simDataElem = theDoc.createElement(XML_TAGS.SIMULATION_DATA.tag);
        currElem.appendChild(simDataElem);

        Element speedElement = theDoc.createElement(XML_TAGS.SIM_SPEED.tag);
        speedElement.appendChild(theDoc.createTextNode(String.valueOf(speed)));
        simDataElem.appendChild(speedElement);    

        Element clockElement = theDoc.createElement(XML_TAGS.CAD_CLOCK.tag);
        simDataElem.appendChild(clockElement); 

        Element hourElement = theDoc.createElement(XML_TAGS.HOUR.tag);
        hourElement.appendChild(theDoc.createTextNode(String.valueOf(6 + (long)simTime/3600)));
        clockElement.appendChild(hourElement); 

        Element minuteElement = theDoc.createElement(XML_TAGS.MINUTE.tag);
        minuteElement.appendChild(theDoc.createTextNode(String.valueOf((long)((simTime%3600)/60))));
        clockElement.appendChild(minuteElement); 

        Element secondElement = theDoc.createElement(XML_TAGS.SECOND.tag);
        secondElement.appendChild(theDoc.createTextNode(String.valueOf((long)(simTime%60))));
        clockElement.appendChild(secondElement);    

    }
    
    /**
     * Appends the <Simulation_Data> XML Element to the parameter Element root.
     * This is done by converting all current XMLIncident objects
     * to their XML output.  The format for this section is as follows:<br>
     * 
     * <CAD_Incidents>
     *    <Incident><br/>
     *       <Identifier/><br/>
     *       <Status/><br/>
     *       <Location><br/>
     *           <Route/><br/>
     *           <Direction/><br/>
     *           <Location_type/><br/>
     *           <Postmile/><br/>
     *       </Location><br/>
     *       <Incident_type/><br/>
     *       <Lanes><br/>
     *          <Lane_number/><br/>
     *          ...
     *       </Lanes><br/>
     *    </Incident><br/>
     *    ...
     * </CAD_Incidents>
     * 
     * @param currElem XML Element to use as a root.
     */
    private void writeIncidents(Element currElem) {

        Document theDoc = currElem.getOwnerDocument();
        
        Element incidentElem = theDoc.createElement(XML_TAGS.CAD_INCIDENTS.tag);
        currElem.appendChild(incidentElem);

        for(XMLIncident incident : incidentsMap.values()) {
            incident.toXML(incidentElem);
        }

    }

    /**
     * Appends the <Simulation_Data> XML Element to the parameter Element root.
     * This is done by converting all current CMS Diversion objects
     * to their XML output.  The format for this section is as follows:<br>
     * 
     * <Management>
     *    <Diversion>
     *       <Diversion_path>
     *          <Identifier/>
     *          <Percentage/>
     *       <Diversion_path>
     *       ...
     *    </Diversion>
     *    ...
     * </Management>
     * 
     * @param currElem XML Element to use as a root.
     */
    private void writeManagement(Element currElem) {

        Document theDoc = currElem.getOwnerDocument();
        
        Element mgmtElem = theDoc.createElement(XML_TAGS.MANAGEMENT.tag);
        currElem.appendChild(mgmtElem);

        for(CMSInfo div : diversionsMap.values()) {
            div.toXML(mgmtElem);
        }                       
    }
        
}

