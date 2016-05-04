package tmcsim.cadsimulator.managers;

import java.util.TreeMap;
import java.util.Vector;

import tmcsim.cadmodels.IncidentBoardModel_obj;
import tmcsim.cadmodels.IncidentInquiryModel_obj;
import tmcsim.cadmodels.IncidentSummaryModel_obj;
import tmcsim.cadsimulator.CADSimulator;
import tmcsim.cadsimulator.Coordinator;
import tmcsim.cadsimulator.SoundPlayer;
import tmcsim.client.cadclientgui.data.Incident;
import tmcsim.client.cadclientgui.data.IncidentEvent;
import tmcsim.common.ScriptException;

/**
 * IncidentManager is a CAD Simulator Manager containing the Incident data 
 * that has been loaded and occured during the simulation.  The IncidentManager
 * is used to load, remove, trigger, and reschedule Incidents in the simulation.  
 * Methods are provided to clear all Incident data from the simulation and to reset
 * the Incidents to begin a new simulation.  The IncidentBoard, IncidentInquiry, 
 * IncidentSummary, and IncidentEvent object lists are viewable through accessor methods.
 * The tick() method is called when the simulation time changes.  This method will then
 * update all loaded Incidents. Any events that occur will be enqueued on the SoundPlayer.
 * Any events that have completed are updated into the simulation data.  The Coordinator
 * is notified when an Incident starts or is updated.
 * 
 * @author Matthew Cechini
 * @version
 */
public class IncidentManager {
    
    /** Reference to the Coordinator Object. */
    private Coordinator theCoordinator;

    /** Reference to the SoundPlayer Object. */
    private SoundPlayer theSoundPlayer;
    
    /**
     * Synchronization lock object used to avoid race conditions in accessing the
     * incidentList Vector.
     */
    private Object lock = new Object();    
    
    /**
     * Map containing a vector of IncidentEvent objects(values) that have 
     * completed for each Incident log number(key).
     */
    private TreeMap<Integer, Vector<IncidentEvent>> completedEvents;       
    
    /**
     * Vector of Incident objects that exist in the simulation.
     */     
    private Vector<Incident> incidentList;    
    
    /**
     * Vector of IncidentBoardModel_obj objects containing the data for
     * incident board messages.
     */
    private Vector<IncidentBoardModel_obj> IncidentBoardModelObjects;

    /**
     * Vector of IncidentInquiryModel_obj objects containing the data
     * for incident inquiry requests.
     */    
    private Vector<IncidentInquiryModel_obj> IncidentInquiryModelObjects;

    /**
     * Vector of IncidentSummaryModel_obj objects containing the data 
     * for incident summary requests.
     */    
    private Vector<IncidentSummaryModel_obj> IncidentSummaryModelObjects;
    
    /** Boolean flag to designate whether incidents are loaded. */
    private boolean incidentsLoaded;
    
    
    /**
     * Constructor. Initialize data members.
     * 
     * @param coor Coordinator Object.
     * @param soundPlayer SoundPlayer Object.
     */
    public IncidentManager(Coordinator coor, SoundPlayer soundPlayer) {
        theCoordinator = coor;
        theSoundPlayer = soundPlayer;
        
        incidentList                = new Vector<Incident>();
        completedEvents             = new TreeMap<Integer, Vector<IncidentEvent>>();    
        IncidentBoardModelObjects   = new Vector<IncidentBoardModel_obj>();
        IncidentInquiryModelObjects = new Vector<IncidentInquiryModel_obj>();
        IncidentSummaryModelObjects = new Vector<IncidentSummaryModel_obj>();
        
        incidentsLoaded = false;
    }
    
    /**
     * Returns whether Incidents have been loaded into the simulation.
     * @return true if incidents are loaded, false if not.
     */
    public boolean areIncidentsLoaded() {
        return incidentsLoaded;
    }
    
    /**
     * Clears IncidentBoard, IncidentInquiry, and IncidentSummary
     * lists.  The IncidentEvent list is also cleared.  All
     * Incidents are removed from the simulation and the 
     * incidentsLoaded flag is reset to false.
     */
    public void clearIncidents() {
        IncidentBoardModelObjects.clear();
        IncidentInquiryModelObjects.clear();
        IncidentSummaryModelObjects.clear();    
        
        incidentList.clear();
            
        completedEvents.clear();   

        incidentsLoaded = false;
    }
    
    /**
     * Clears IncidentBoard, IncidentInquiry, and IncidentSummary
     * lists.  The IncidentEvent list is also cleared.  All
     * Incidents are reset, but not removed from the simulation.  The 
     * incidentsLoaded flag is not changed.
     */
    public void resetIncidents() {
        IncidentBoardModelObjects.clear();
        IncidentInquiryModelObjects.clear();
        IncidentSummaryModelObjects.clear();    
        
        for(Incident inc : incidentList) {
            inc.resetSimulation();
        }
        
        completedEvents.clear();        
    }
    
    /**
     * This method removes an Incident from the list of Incidents that
     * have been loaded into the simulation.  The incidentsLoaded flag
     * is updated accordingly to whether there are Incidents remaining.
     * 
     * @param incidentNumber Log number of Incident to remove.
     * @throws ScriptException if the Incident has already occured.
     */
    public void deleteIncident(Integer incidentNumber) throws ScriptException {

        synchronized (lock) {
            Vector<Incident> incToRemove = new Vector<Incident>();

            for (Incident inc : incidentList) {

                if (inc.getLogNumber().compareTo(incidentNumber) == 0) {
                    if (inc.hasOccured())
                        throw new ScriptException(
                                ScriptException.INCIDENT_ALREADY_STARTED);

                    incToRemove.add(inc);
                }
            }

            for (Incident inc : incToRemove)
                incidentList.remove(inc);
        }

        incidentsLoaded = incidentList.size() > 0;
    }

    /**
     * This method reschedules a loaded Incident to occur at a new simulation 
     * time.
     * 
     * @param incidentNumber Log number of Incident to remove.
     * @param newTime New simulation time for Incident to occur.
     * @throws ScriptException if the Incident has already occured.
     */
    public void rescheduleIncident(Integer incidentNumber, long newTime)
            throws ScriptException {

        synchronized (lock) {

            for (Incident inc : incidentList) {

                if (inc.getLogNumber().equals(incidentNumber)) {
                    if (inc.hasOccured()) {
                        throw new ScriptException(
                                ScriptException.INCIDENT_ALREADY_STARTED);
                    }
                    inc.setSecondsToStart(newTime);
                }
            }
        }
    }

    /**
     * This method adds a new Incident to the simulation.  The incidentsLoaded
     * flag is set to true.
     * 
     * @param newIncident New Incident.
     */
    public void addIncident(Incident newIncident) {

        synchronized (lock) {
            incidentList.add(newIncident);
            incidentsLoaded = true;
        }
    }

    /**
     * This method adds a list of new Incidents to the simulation.  The 
     * incidentsLoaded flag is set to true.
     * 
     * @param newIncident New Incident.
     */
    public void addIncidents(Vector<Incident> vector) {

        synchronized (lock) {
            incidentList.addAll(vector);
            incidentsLoaded = true;
        }
    }
    
    /**
     * This method calls the tick() method on all loaded Incidents.  If the 
     * clock tick causes IncidentEvents to be triggered, the events are 
     * enqueued in the SoundPlayer.  If any IncidentEvents have completed,
     * they are finalized with the current simulation and CAD time.  The local
     * IncidentInquiry and IncidentSummary lists are updated with the completed
     * IncidentEvent Objects and then the Coordinator is notified with the
     * IncidentEvent.  
     * 
     * @param currentSimTime Current simulation time (in seconds).
     */
    public void tick(long currentSimTime) {
        
        for(Incident inc : incidentList) {

            inc.tick(currentSimTime);
            
            //For all events that occur with this tick, enqueue them in the sound player.
            for(IncidentEvent event : inc.getTriggeredEvents(currentSimTime)) {
                theSoundPlayer.enqueueClip(event);   
            }                   
                
            for(IncidentEvent event : inc.getCompletedEvents()) {
                event.finalizeEvent(currentSimTime, CADSimulator.getCADTime());
                updateIncident(inc.getLogNumber(), event);
                theCoordinator.updateIncidentInGUI(inc.getLogNumber(), event);
            }                       
        }       
    }
    
    /**
     * This method forces an Incident to trigger.  If the Incident corresponding
     * to the parameter log number has not occured it is manually triggered.
     * 
     * @param incidentNumber Log number of Incident to trigger.
     * @param currentSimTime Current simulation time (in seconds).
     */
    public void triggerIncident(Integer incidentNumber, Long currentSimTime) {

        for(Incident inc : incidentList) {
            
            if(inc.getLogNumber().equals(incidentNumber) 
                && !inc.hasOccured()) {                                         

                inc.manualTrigger(currentSimTime);
            }   
        }

    }

    /**
     * This method updates the completed events, IncidentInquiry, and 
     * IncidentSummary lists with the completed IncidentEvent Object.
     * If the IncidentEvent is the first event in an Incident,
     * notify the Coordinator that the Incident has started.
     * Regardles of whether the IncidentEvent is first or not,
     * notify the Coordinator that the Incident has been updated.
     * 
     * @param incidentNumber Log number of Incident to trigger.
     * @param triggeredEvent Completed IncidentEvent.
     */
    public void updateIncident(Integer incidentNumber, IncidentEvent completedEvent) {

        IncidentInquiryModel_obj targetIncident = null;
        
        if(completedEvents.containsKey(incidentNumber)) {
            completedEvents.get(incidentNumber).add(completedEvent);
        }
        else {
            Vector<IncidentEvent> temp = new Vector<IncidentEvent>();
            temp.add(completedEvent);
            
            completedEvents.put(incidentNumber, temp);
        }
        
        for(IncidentInquiryModel_obj iimo : IncidentInquiryModelObjects) {

            if(iimo.getLogNumber().compareTo(incidentNumber) == 0) {
                targetIncident = iimo;
                
                targetIncident.update(completedEvent.eventInfo);                
                break;
            }
        }
        
        if(targetIncident == null) {
            completedEvent.eventInfo.getHeader().logStatus    = "A";             
            completedEvent.eventInfo.getHeader().incidentDate = CADSimulator.getCADDate().substring(0,4);  
            completedEvent.eventInfo.getHeader().incidentTime = CADSimulator.getCADTime();
            
            targetIncident = new IncidentInquiryModel_obj(completedEvent.eventInfo);
            IncidentInquiryModelObjects.add(targetIncident);    
            IncidentSummaryModelObjects.add(new IncidentSummaryModel_obj(completedEvent.eventInfo.getHeader()));    
            
            theCoordinator.incidentStarted(completedEvent);     
        }           
            
        theCoordinator.incidentUpdated(completedEvent);         
    }
    
    /**
     * Returns whether an Incident exists in the simulation with a log number
     * that matches the parameter value.
     * 
     * @param incidentNumber Log number of Incident to trigger.
     * @return true if Incident exists, false if not.
     */
    public boolean incidentExists(Integer incidentNumber) {
        boolean found = false;
        
        for(IncidentInquiryModel_obj iimo : IncidentInquiryModelObjects) {
            if(iimo.getLogNumber().compareTo(incidentNumber) == 0) {
                found = true;
                break;
            }           
        }

        return found;
    }
    
    /**
     * Returns the Vector of Incidents loaded into the simulation.
     * @return Vector of loaded Incidents.
     */
    public Vector<Incident> getIncidentList() {
        return incidentList;
    }
    
    /**
     * Returns the map of triggered IncidentEvents referenced by eac
     * Incident log number.
     * 
     * @return Map of triggered IncidentEvents for all Incidents.
     */
    public TreeMap<Integer, Vector<IncidentEvent>> getTriggeredEvents() {
        return completedEvents;
    }   
    
    /**
     * Returns the list of all IncidentBoardModel_obj objects. 
     *
     * @returns A Vector of all IncidentBoardModel_obj objects.
     */
    public Vector<IncidentBoardModel_obj> getIncidentBoardModelObjects() {
        return IncidentBoardModelObjects;               
    }       
    
    /**
     * Returns a list of all IncidentInquiryModel_obj objects that match the parameter
     * Incident log number.
     *
     * @param logNumber The log number to get all incident inquiry data for.
     * @returns A Vector of IncidentInquiryModel_obj objects.
     */    
    public Vector<IncidentInquiryModel_obj> getIncidentInquiryModelObjects(Integer logNumber) {
        Vector<IncidentInquiryModel_obj> modelObjs = new Vector<IncidentInquiryModel_obj>();

        synchronized(lock) {
            
            for(IncidentInquiryModel_obj iimo : IncidentInquiryModelObjects) {
                
                //Find the model object in the private vector of incidents that matches
                //the incident in the parameter's model.
                
                if(iimo.getLogNumber().compareTo(logNumber) == 0) {
                    modelObjs.add(iimo);
                }
            }    
        }
        

        return modelObjs;
    }    
    
    /**
     * Returns the list of all IncidentSummaryModel_obj objects. 
     *
     * @returns A Vector of all IncidentSummaryModel_obj objects.
     */
    public Vector<IncidentSummaryModel_obj> getIncidentSummaryModelObjects() {
        return IncidentSummaryModelObjects;
    }      
    
}
