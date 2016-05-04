package tmcsim.cadmodels;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.Vector;

import tmcsim.cadmodels.IncidentEvent.EVENT_STATUS;
import tmcsim.common.ParamicsLocation;

/**
 * Incident is the container class used to information relating to a 
 * simulation incident.  An incident is identified by a unique integer log 
 * number. Additional descriptive information includes a short description, 
 * an IncidentInquiryHeader containing CAD related data, and a Map of
 * IncidentLocations used for XMLIncident location referencing.  This object holds 
 * counters to keep track of the simulation time when the incident is scheduled 
 * to occur and to retain the time when it is does occur.  The Incident object 
 * has a list of IncidentEvents that will occur during the simulation.  
 * 
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/06 20:46:40 $ $Revision: 1.4 $
 */
@SuppressWarnings("serial")
public class Incident implements Serializable {
    
    /** A brief description of the incident that is displayed in the Simulation Manager GUI. */
    public String description;
    
    /** The CHP Incident Log Number assigned to this incident */
    public Integer logNumber;

    /**  Object containing the IncidentInquiry header information. */
    public IncidentInquiryHeader header;

    /**  
     * TreeMap mapping IncidentLocation objects(values) to a String identifier(keys) 
     * read as during script parsing. 
     */
    public TreeMap<String, ParamicsLocation> locationMap;   
    
    /** 
     * The time, in seconds, in a simulation that the incident 'begins.'  
     * This does not necessarily correspond to first event. 
     */
    private long startTime;
    
    /**
     * Holds the time (number of seconds) that this incident occured. This may
     * be different than the scriptIncidentStartTime if the user manually triggers
     * the incident.
     */     
    private long secondsIncidentStarted = 0;
 
    /** Boolean flag to designate whether the incident has occured or not. */    
    private boolean incidentOccured = false;
    
    /** The incidents list of IncidentEvents that will occur in successive order. */
    private Vector<IncidentEvent> eventList = null; 
    
        
    /**
     * Constructor.  Initialize local lists and Incident identifying data.
     * 
     * @param log   Unique log number for this incident.
     * @param desc  Short description of this incident.
     * @param start Simulation time (in seconds) for this incident to start.
     */
    public Incident(/*NetworkID*/ Integer log, String desc, long start)
    {
        logNumber   = log;
        description = desc;
        startTime   = start;
        header      = new IncidentInquiryHeader();      
        locationMap = new TreeMap<String, ParamicsLocation>();
        
        eventList    = new Vector<IncidentEvent>();
        
    }
    
    /**
     * Method checks if the Incident can be triggered.  If the simulation
     * time is equal to or greater than this Incident's start time, and the 
     * Incident has not previously been triggered, then trigger the incident.
     * The start time is recorded and true returned.  Else the Incident is
     * already started or not ready, return false.
     *
     * @param scriptSeconds Current value of simulation time, in seconds.
     * @return boolean Return true when this incident is first triggered, else return false.
     */
    public boolean tick(long scriptSeconds) {
        
        if(scriptSeconds >= startTime && !incidentOccured) {            
            incidentOccured        = true;
            secondsIncidentStarted = scriptSeconds;
            
            return true;
        }
        else
            return false;
    }   
    
    /**
     * Method iterates through the list of events associated with this incident and returns 
     * a list of those that are ready to be triggered.  
     *
     * @param simTime Current simulation time, in seconds.
     * @return Vector of IncidentEvents that have triggered as a result of this tick().
     */
    public Vector<IncidentEvent> getTriggeredEvents(long simTime) {
        
        Vector<IncidentEvent> triggered = new Vector<IncidentEvent>();
        
        if(incidentOccured) {       
            for(IncidentEvent evt : eventList) {
                if(evt.triggerEvent(secondsIncidentStarted, simTime)) {
                    triggered.add(evt);
                }   
            }                               
        }
        
        return triggered;
    }
    
    
    /**
     * This method returns a Vector of IncidentEvents which had previously been
     * triggered and queued while it's associated audio file was being played.
     * When the audio file has completed, this method will remove that event from
     * the queuedEvents Vector. 
     * 
     * @return Vector of IncidentEvents which can be inserted into the simulation.
     */
    public Vector<IncidentEvent> getCompletedEvents() {
        
        Vector<IncidentEvent> completed = new Vector<IncidentEvent>();
        
        for(IncidentEvent evt : eventList) {
            if(evt.eventStatus == EVENT_STATUS.COMPLETED) {             
                completed.add(evt);
            }       
        }
        
        return completed;
    }
    
    /**
     * Get the script log number associated with this incident.
     *
     * @return String The script log number.
     */
    public Integer getLogNumber() {
        return logNumber;   
    }
    

    /**
     * Manually trigger this incident.  Sets the variable that keeps track of
     * when the incident occured to the parameter value, and sets the 
     * incidentOccurred variable to true.  
     *
     * @param newtime The time in the script when the incident is being triggered.
     */
    public void manualTrigger(long newtime) {
        secondsIncidentStarted = newtime;   
        incidentOccured        = true; 
    }
    
    /**
     * Gets the time (in seconds) that the incident will occur.
     *
     * @return long Time(in seconds) that the incident will occur.
     */
    public long getSecondsToStart() {
        return startTime;   
    }
    
    /**
     * Sets the time( in seconds) that the incident will occur.
     *
     * @param  newStartTime Time(in seconds) that the incident will occur.
     */
    public void setSecondsToStart(long newStartTime) {
        startTime = newStartTime;   
    }   
    
    
    /**
     * Called to calculate the length of this incident.  The length is the first
     * event's time subtracted from the last event's time.  
     *
     * @return Length of the simulation incident (in seconds).
     */
    public Long getIncidentLength() {       
        return eventList.lastElement().secondsToOccurInIncident -
            eventList.firstElement().secondsToOccurInIncident;                  
    }
    
    /**
     * Check to see if incident has occured.
     * @return true if incident has occured, false if not.
     */
    public boolean hasOccured() {
        return incidentOccured; 
    }

    /**
     * Reset the simulation time counters and incidentOccured flag.  Iterate
     * through the event list and reset each event as well.
     */
    public void resetSimulation() {
        secondsIncidentStarted    = 0;
        incidentOccured           = false;
            
        for(IncidentEvent ie : eventList) 
            ie.resetSimulation();
                                
    }
    
    /**
     * Add an Incident Event to the Incident's list of events.  The new 
     * event will also receive the current header data associated with this 
     * incident.
     * 
     * @param newEvent The IncidentEvent to be added.
     */
    public void addEvent(IncidentEvent newEvent) {
        newEvent.eventInfo.setHeader(header);
        eventList.add(newEvent);    
    }   

}