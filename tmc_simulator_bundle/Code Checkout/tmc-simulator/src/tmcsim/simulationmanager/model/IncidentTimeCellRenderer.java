package tmcsim.simulationmanager.model;

import javax.swing.table.DefaultTableCellRenderer;

/**
 * IncidentTimeCellRenderer is a DefaultCellEditor overriding the necessary 
 * base methods to render the Long incident time value as a string of format
 * H:MM:SS.  If the incident time value is -1, this means the incident has 
 * started, and the renderer returns "Started" as the display value.
 * 
 * @author Matthew
 * @version
 */
@SuppressWarnings("serial")
public class IncidentTimeCellRenderer extends DefaultTableCellRenderer {

    public IncidentTimeCellRenderer() { }
    
    protected void setValue(Object value) {

        if(value != null && value instanceof Number) {
            String time_str = new String();
            Long   time_val = (Long)value;
                        
            if(time_val == -1) {
                super.setValue("Started");
                return;
            }
                        
            time_str += String.valueOf(time_val / 3600) + ":";      
            
            time_val = time_val % 3600;
            
            if(time_val / 60 < 10)
                time_str += "0";
            
            time_str += String.valueOf(time_val / 60) + ":";        
            time_val = time_val % 60;   
            
            if(time_val < 10)
                time_str += "0";
            
            time_str += String.valueOf(time_val);

            super.setValue(time_str);
        }
    }
}
