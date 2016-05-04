package tmcsim.simulationmanager.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import tmcsim.cadmodels.IncidentInquiryDetails;
import tmcsim.client.cadclientgui.data.IncidentEvent;

/** 
 * IncidentHistoryTableModel is a DefaultTableModel used to display the 
 * list of current incident events in the Event History portion of the
 * Simulation Manager.  The columns in this table show the event source,
 * the simulation time the event occured, and the event's description.
 * 
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class IncidentHistoryTableModel extends DefaultTableModel {
    
    /**
     * Enumeration of columns for this table.
     * @author Matthew Cechini
     */ 
    public static enum EVENT_HIST_COLUMNS {
        POSITION_COL    ("Position", 0, 70, 110, 80),
        TIME_COL        ("Time", 1, 50, 100, 60),
        EVENT_DESC_COL  ("Event Description", 2, 250, 600, 300);
        
        public String colName;
        public int colNum;
        public int colMinWidth;
        public int colMaxWidth;
        public int colPrefWidth;
        
        private EVENT_HIST_COLUMNS (String name, int num, int min, int max, int pref) {
            colName      = name;
            colNum       = num;
            colMinWidth  = min;
            colMaxWidth  = max;
            colPrefWidth = pref;
        }
        
        public static int colCount() {
            return values().length;
        }
        
        public static String columnName(int num) {
            
            for(EVENT_HIST_COLUMNS column : EVENT_HIST_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colName;
            }
            
            return "";
        }
        
        public static int columnMinWidth(int num) {
            
            for(EVENT_HIST_COLUMNS column : EVENT_HIST_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colMinWidth;
            }
            
            return 0;
        }
        
        public static int columnMaxWidth(int num) {
            
            for(EVENT_HIST_COLUMNS column : EVENT_HIST_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colMaxWidth;
            }
            
            return 0;
        }
        
        public static int columnPrefWidth(int num) {
            
            for(EVENT_HIST_COLUMNS column : EVENT_HIST_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colPrefWidth;
            }
            
            return 0;
        }
        
        public static EVENT_HIST_COLUMNS fromColNum(int column) {
            for(EVENT_HIST_COLUMNS c : EVENT_HIST_COLUMNS.values()) {
                if(c.colNum == column)
                    return c;
            }
            
            return POSITION_COL;
        }
    }  
    
    /** 
     * Container class to hold information displayed in the table.
     * @author Matthew Cechini
     */
    protected class EventHistoryInfo {

        public String source;
        public Long   secondsOccured;
        public String details;
        
        public EventHistoryInfo(String src, Long seconds, String d) {
            source         = src;
            secondsOccured = seconds;
            details        = d;
        }
    }
    

    /** List of data displayed in the table. */
    private List<EventHistoryInfo> event_list;
    
    /** Constructor. */
    public IncidentHistoryTableModel() {
        event_list = new ArrayList<EventHistoryInfo>();
    }

    public int getRowCount() {
        if(event_list == null) {
            return 0;
        }
        else
            return event_list.size();
    }

    public int getColumnCount() {
        return EVENT_HIST_COLUMNS.colCount();
    }

    public String getColumnName(int col) {
        return EVENT_HIST_COLUMNS.columnName(col);
    }

    public int getColumnMinWidth(int c){
        return EVENT_HIST_COLUMNS.columnMinWidth(c);
    }

    public int getColumnMaxWidth(int c){
        return EVENT_HIST_COLUMNS.columnMaxWidth(c);
    }

    public int getColumnPrefWidth(int c){
        return EVENT_HIST_COLUMNS.columnPrefWidth(c);
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }   
    
    public Object getValueAt(int row, int col) {
        switch (EVENT_HIST_COLUMNS.fromColNum(col)) {
            case EVENT_DESC_COL:
                return event_list.get(row).details;
            case POSITION_COL:
                return event_list.get(row).source;
            case TIME_COL:
                return event_list.get(row).secondsOccured;
            default:
                return "";
        }        
    }
    
    /**
     * Method adds a new incident event into the table.
     * @param logNumber Incident log number corresponding to the parameter event.
     * @param newEvent Incident event object to add.
     */    
    public void addEvent(IncidentEvent newEvent) {
        
        for(IncidentInquiryDetails detail : newEvent.eventInfo.getDetails()) {
            event_list.add(new EventHistoryInfo(
                    newEvent.eventInfo.getSource(),
                    newEvent.secondsOccuredInSimulation,
                    detail.details));       
        }
        fireTableChanged(new TableModelEvent(this));
    }
    
    /** 
     * Remove all events from the table's data.
     */    
    public void clearModelData() {
        event_list.clear();
        fireTableChanged(new TableModelEvent(this));
    }
    
}
