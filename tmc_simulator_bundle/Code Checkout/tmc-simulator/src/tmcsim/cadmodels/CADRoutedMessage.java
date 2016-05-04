package tmcsim.cadmodels;

import java.io.Serializable;
import java.util.Calendar;

/**
 * CADRoutedMessage is used to contain information for a routed message
 * between CAD positions.  Message information includes origin and destination
 * CAD positions, message date and time, message text, and a flag to specify
 * that this message is a incident update.
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/06 20:46:40 $ $Revision: 1.4 $
 */
@SuppressWarnings("serial")
public class CADRoutedMessage implements Comparable<CADRoutedMessage>, Serializable {

    /** CAD Position that sent this routed message. */
    public int fromPosition;

    /** CAD Position tha tthis routed message is being sent to. */
    public int toPosition;

    /** String representation of the current data in format DDMMYYYY. */
    public String date;

    /** String representation of the current time in format HH:MM:SS */
    public String time;

    /** Routed message text. */
    public String message; 
    
    /** Flag to designate whether this is a routed IncidentInquiry page */
    public boolean incidentUpdate;
    
    /**
     * Constructor.  Initializes source, destination, and message from 
     * method parameters.  Initializes date and time to current values.
     *
     * @param from CAD position message has been sent from.
     * @param to CAD Position to route the message to
     * @param newMessage Routed message that has been entered
     */
    public CADRoutedMessage(int from, int to, String newMessage, boolean isIncidentUpdate) {
        fromPosition = from;
        toPosition   = to;
        date         = "";
        time         = "";
            
            
        Calendar rightNow = Calendar.getInstance();
        
        /*** BUILD TIME ***/
        
        //String currentTime = "";
        if(rightNow.get(Calendar.HOUR_OF_DAY) < 10)
            time += "0";
        
        time += (String.valueOf(rightNow.get(Calendar.HOUR_OF_DAY))) + ":";
        
        if(rightNow.get(Calendar.MINUTE) < 10)
            time += "0";
        
        time += (String.valueOf(rightNow.get(Calendar.MINUTE))) + ":";      
        
        
        if(rightNow.get(Calendar.SECOND) < 10)
            time += "0";
        
        time += (String.valueOf(rightNow.get(Calendar.SECOND)));
        
        
        /*** BUILD DATE ***/
        
        if(rightNow.get(Calendar.MONTH) < 10)
            date += "0";
        date += String.valueOf(rightNow.get(Calendar.MONTH));
        
        if(rightNow.get(Calendar.DAY_OF_MONTH) < 10)
            date += "0";
        date += String.valueOf(rightNow.get(Calendar.DAY_OF_MONTH));
        
        date += String.valueOf(rightNow.get(Calendar.YEAR));
        
        message        = newMessage;
        incidentUpdate = isIncidentUpdate;
    }

    

    /**
     * Overloaded Comparable.compareTo() method to allow this object to be 
     * added to an order collection.
     */
    public int compareTo(CADRoutedMessage o) {
        return time.compareTo(o.time);
    }
    
    /**
     * Overloaded equals method, determining equality by checking all data members.
     */
    public boolean equals(Object o) {
        return ((CADRoutedMessage)o).toPosition == toPosition &&
               ((CADRoutedMessage)o).fromPosition == fromPosition &&
               ((CADRoutedMessage)o).date.equals(date) &&
               ((CADRoutedMessage)o).message.equals(message);
         
    }
    
}