package tmcsim.cadmodels;

import java.io.Serializable;
import java.util.Vector;

import tmcsim.common.CCTVInfo;
import tmcsim.common.XMLIncident;

/**
 * IncidentEvent class contains data for an event that occurs during a 
 * simulation.  Incident event information includes the following:<br/>
 * <ul>
 * <li>Second after Incident begins Event is schedule to occur.</li>
 * <li>Simulation time Event occurs.</li>
 * <li>IncidentInquiry model data.</li>
 * <li>Audio file name and duration.</li>
 * <li>XMLIncident Objects</li>
 * <li>CCTVInfo Objects</li>
 * <li>Current Event status.</li>
 * </ul>
 * <br/>
 * <br/>
 * The EVENT_STATUS is used to keep track of what state this event is in.  An 
 * event is WAITING until its time to occur is reached in the simulation.  The 
 * event will then enter the TRIGGERED state if it has a wave file length 
 * greater than 0, otherwise it will enter the COMPLETED state.  When an 
 * audio file is completed, the wavePlayed() method is called, and the Event 
 * enters the COMPLETED state.  The finalizeEvent method finalizes the Event by
 * timestamping the model data and setting the number of seconds the event 
 * occured in the simulation.  The EVENT_STATUS is then set to FINALIZED.
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/06 20:46:40 $ $Revision: 1.4 $
 */
@SuppressWarnings("serial")
public class IncidentEvent implements Comparable<IncidentEvent>, Serializable {
    
    /**
     * Enumeration
     * @author Matthew Cechini
     */
    public static enum EVENT_STATUS {
        /** Waiting to be triggered. */
        WAITING,
        /** Event has been triggered. */
        TRIGGERED,  
        /** Completed audio playing, if necessary. */
        COMPLETED,   
        /** 
         * Incident has been triggered, audio file has been played, 
         * and model data timestamped. 
         */
        FINALIZED
    };

    /** Number of seconds after the incident begins that this event will trigger. */    
    public long secondsToOccurInIncident;
    
    /** Number of seconds after the simulation started that this event occured. */  
    public long secondsOccuredInSimulation;
    
    /** Model object containing the data that will be added to the incident when this event occurs. */  
    public IncidentInquiryModel_obj eventInfo = null;
    
    /** Audio wav file associated with this event. */   
    public String waveFile;
    
    /** Duration(in seconds) of the waveFile. */    
    public int waveLength;
            
    /** Vector of XMLIncident objects which are associated with this incident event. */
    public Vector<XMLIncident> XMLIncidents;
    
    /** Vector of CCTV objects which are associated with this incident event. */
    public Vector<CCTVInfo> cctvInfos;
    
    /** Current Incident Event status. */
    public EVENT_STATUS eventStatus;
    

    /**  
     * Constructor.
     *
     * @param timeToOccur Time (in seconds) in simulation for this incident event to occur.
     */
    public IncidentEvent(long timeToOccur) {
            
        eventInfo       = new IncidentInquiryModel_obj();   
        waveFile        = "";
        waveLength      = 0;
        eventStatus     = EVENT_STATUS.WAITING;
        XMLIncidents    = new Vector<XMLIncident>();
        cctvInfos       = new Vector<CCTVInfo>();

        secondsToOccurInIncident   = timeToOccur;
        secondsOccuredInSimulation = 0;   
            
    }    
     
   /**
    * Constructor.
    *
    * @param timeToOccur      Time (in seconds) in simulation for this incident event to occur.
    * @param info             IncidentInquiry model data.
    * @param newWaveFile      Filename of audio wav file to be played with this event.
    * @param newWaveLength    Duration (in seconds) of audio wav file
    * @param newXMLIncidents  List of XMLIncidents for this event.
    * @param newCCTVInfos     List of CCTVInfos for this event.
    */
    public IncidentEvent(long timeToOccur, 
                         IncidentInquiryModel_obj info, 
                         String newWaveFile, 
                         int newWaveLength,
                         Vector<XMLIncident> newXMLIncidents,
                         Vector<CCTVInfo> newCCTVInfos){
        
        eventInfo       = info;
        eventStatus     = EVENT_STATUS.WAITING;
        waveFile        = newWaveFile;
        waveLength      = newWaveLength;

        XMLIncidents    = new Vector<XMLIncident>();
        XMLIncidents.addAll(newXMLIncidents);
        
        cctvInfos       = new Vector<CCTVInfo>();
        cctvInfos.addAll(newCCTVInfos);     
        
        secondsToOccurInIncident   = timeToOccur;
        secondsOccuredInSimulation = 0;     
    }
    
    /**
     * Copy Constructor.
     *
     * @param timeToOccur      Time (in seconds) in simulation for this incident event to occur.
     * @param info             IncidentInquiry model data.
     * @param newWaveFile      Filename of audio wav file to be played with this event.
     * @param newWaveLength    Duration (in seconds) of audio wav file
     * @param newXMLIncidents  List of XMLIncidents for this event.
     * @param newCCTVInfos     List of CCTVInfos for this event.
     */
     public IncidentEvent(final IncidentEvent copyEvent){
        
        eventInfo       = copyEvent.eventInfo;
        eventStatus     = copyEvent.eventStatus;
        waveFile        = copyEvent.waveFile;
        waveLength      = copyEvent.waveLength;

        XMLIncidents    = new Vector<XMLIncident>();
        XMLIncidents.addAll(copyEvent.XMLIncidents);
        
        cctvInfos       = new Vector<CCTVInfo>();
        cctvInfos.addAll(copyEvent.cctvInfos);      
        
        secondsToOccurInIncident   = copyEvent.secondsToOccurInIncident;
        secondsOccuredInSimulation = copyEvent.secondsOccuredInSimulation;      
     }    
    
    /** Compare objects according o comparison of secondsToOccurInIncident member data. */
    public int compareTo(IncidentEvent o) {
        if(secondsToOccurInIncident < o.secondsToOccurInIncident)
            return -1;
            
        else if(secondsToOccurInIncident > o.secondsToOccurInIncident)
            return 1;
        
        else 
            return 0;
    }
    
    /**
     * Method determines if the Event is ready to be triggered.  This is 
     * determined by assessing the current Event status and simulation
     * time.  The Event will be triggered if the Event is WAITING and the
     * Event's time to start has been reached.  The Event's relative start 
     * time is added to the Incident start time parameter and compared
     * against the current simulation time parameter.  If the audio file 
     * duration is greater than  zero seconds, then the Event status is set to 
     * TRIGGERED, and true is returned.  If the audio file is zero, the status 
     * is set to COMPLETED and false is returned.
     *
     * @param incidentStartTime Simulation time that Event's Incident began (in seconds).
     * @param simulationTime Current simulation time (in seconds).
     * @return true if incident enters the TRIGGERED state, false if it is not 
     * ready to be triggered or has entered the COMPLETED state.
     */
    public boolean triggerEvent(long incidentStartTime, long simulationTime) {
        boolean retVal = false;
        
        if(eventStatus == EVENT_STATUS.WAITING &&
           simulationTime > (incidentStartTime + secondsToOccurInIncident) )
        {               
            if(waveLength == 0) {
                eventStatus = EVENT_STATUS.COMPLETED;
            }
            else {
                retVal = true;
                eventStatus = EVENT_STATUS.TRIGGERED;
            }
        }
        
        return retVal;
    }        
    
  
    /**
     * Method is called when the Event's audio file completes.  The Event 
     * status is set to COMPLETED.
     */
    public void wavePlayed() {
        eventStatus = EVENT_STATUS.COMPLETED;
    }
    
    /**
     * Method is called to finalize and Event.  The Event info
     * is timestamped with the parameter and the  secondsInSimulationOccured
     * member is set to the current time in the simulation.  The Event
     * status is set to FINALIZED.
     *
     * @param occured Current simulation time (in seconds). 
     * @param timestamp CAD time stamp for model data. 
     */
    public void finalizeEvent(long occured, String timestamp) {
        //set time stamp
        eventInfo.timeStamp(timestamp);     
        secondsOccuredInSimulation = occured;
        
        eventStatus = EVENT_STATUS.FINALIZED;
    }
    
    /**
     * Called to reset the simulation.  The Event status is set to WAITING and
     * the secondsOccuredinSimulation is reset to 0.
     */
    public void resetSimulation() {
        eventStatus = EVENT_STATUS.WAITING;
        secondsOccuredInSimulation = 0;
    }

} 