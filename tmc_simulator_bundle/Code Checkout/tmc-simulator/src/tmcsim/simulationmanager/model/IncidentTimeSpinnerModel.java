package tmcsim.simulationmanager.model;

import javax.swing.AbstractSpinnerModel;

/**
 * IncidentTimeSpinnerModel is an AbstractSpinnerModel used in rescheduling 
 * and adding incidents into the simulation. The spinner shows a simulation time
 * of format H:MM:SS.  The next and previous values are calculated by adding or 
 * subtracting 30 seconds from the current value.  
 * 
 * @author Matthew Cechini
 * @version
 */
public class IncidentTimeSpinnerModel extends AbstractSpinnerModel {

    /** Current simulation time value (seconds). */
    private long currentTime = 0;
    
    /**
     * Constructor.
     */
    public IncidentTimeSpinnerModel() { }
    
    /**
     * Method returns the next time value by adding 30 seconds
     * to the current time value.  The returned value is a string
     * of format H:MM:SS
     * 
     * @return H:MM:SS formatted time
     */
    public Object getNextValue() {
        currentTime += 30 - (currentTime % 30);
        
        return longToTime();
    }   

    /**
     * Method returns the previous time value by subtracting 30 seconds
     * to the current time value.  If the current value is less than 0:00:30,
     * the time is set to 0.  The returned value is a string of format H:MM:SS.
     * 
     * @return H:MM:SS formatted time
     */ 
    public Object getPreviousValue() {
        if(currentTime > 30)
            currentTime -= 30 + (currentTime % 30);
        else 
            currentTime = 0;
        
        return longToTime();
    }
    
    /**
     * Method returns the current time value as a string of format H:MM:SS.
     * 
     * @return H:MM:SS formatted time
     */     
    public Object getValue() {
        return longToTime();
    }
    
    /**
     * Sets the current time.  If the parameter is a Long, set
     * the current time to this value and call this class again
     * with the String representation to update the spinner.
     */
    public void setValue(Object o) {
        if(o.getClass() == String.class)
            fireStateChanged();
        else if (o.getClass() == Long.class) {
            currentTime = ((Long)o).longValue();
            setValue(longToTime());
        }
    }
    
    /** 
     * Converts the currentTime value into string of format H:MM:SS.
     * @return H:MM:SS format of the current simulation time.
     */
    private String longToTime() {
        
        String time = new String();     
        long tempTime = currentTime;    
        
        time += String.valueOf(tempTime / 3600) + ":";      
        
        tempTime = tempTime % 3600;
        
        if(tempTime / 60 < 10)
            time += "0";
        
        time += String.valueOf(tempTime / 60) + ":";        
        tempTime = tempTime % 60;   
        
        if(tempTime < 10)
            time += "0";
        
        time += String.valueOf(tempTime);
        return time;        
    }   

}
