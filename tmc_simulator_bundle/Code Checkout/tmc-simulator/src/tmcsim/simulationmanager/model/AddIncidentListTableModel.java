package tmcsim.simulationmanager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import tmcsim.client.cadclientgui.data.Incident;

/** 
 * AddIncidentListTableModel is a DefaultTableModel used to display the 
 * list of current incidents that may be added as a part of the 
 * AddIncidentDialog.  The columns in this table show the incident log number, 
 * description, length, a boolean checkbox, and scheduled time. 
 * 
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class AddIncidentListTableModel extends DefaultTableModel {

    /**
     * Enumeration of columns for this table.
     * @author Matthew Cechini
     */ 
    public static enum ADD_INCIDENT_LIST_COLUMNS {
        LOG_NUM_COL    ("Log #", 0, 50, 100, 50),
        EVENT_DESC_COL ("Event Description", 1, 120, 300, 120),
        EVENT_LEN_COL  ("Event Length", 2, 80, 100, 80),
        ADD_COL        ("Add?", 3, 60, 80, 60),
        SCHEDULED_COL  ("Scheduled Time", 4, 80, 120, 80);
        
        public String colName;
        public int colNum;
        public int colMinWidth;
        public int colMaxWidth;
        public int colPrefWidth;
        
        private ADD_INCIDENT_LIST_COLUMNS (String name, int num, int min, int max, int pref) {
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
            
            for(ADD_INCIDENT_LIST_COLUMNS column : ADD_INCIDENT_LIST_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colName;
            }
            
            return "";
        }
        
        public static int columnMinWidth(int num) {
            
            for(ADD_INCIDENT_LIST_COLUMNS column : ADD_INCIDENT_LIST_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colMinWidth;
            }
            
            return 0;
        }
        
        public static int columnMaxWidth(int num) {
            
            for(ADD_INCIDENT_LIST_COLUMNS column : ADD_INCIDENT_LIST_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colMaxWidth;
            }
            
            return 0;
        }
        
        public static int columnPrefWidth(int num) {
            
            for(ADD_INCIDENT_LIST_COLUMNS column : ADD_INCIDENT_LIST_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colPrefWidth;
            }
            
            return 0;
        }
        
        public static ADD_INCIDENT_LIST_COLUMNS fromColNum(int column) {
            for(ADD_INCIDENT_LIST_COLUMNS c : ADD_INCIDENT_LIST_COLUMNS.values()) {
                if(c.colNum == column)
                    return c;
            }
            
            return LOG_NUM_COL;
        }
    }  

    /** 
     * Container class to hold information displayed in the table.
     * @author Matthew Cechini
     */ 
    protected class AddIncidentEventInfo {
    
        public Integer logNumber;
        public String  description;
        public Long    length;
        public Boolean addFlag;
        public Long    scheduleTime;
        
        public AddIncidentEventInfo(Integer log, String desc, Long len, Boolean add, Long time) {
            logNumber    = log;
            description  = desc;
            length       = len;
            addFlag      = add;
            scheduleTime = time;
        }
    }

    /** List of data displayed in the table. */
    private List<AddIncidentEventInfo> event_list;
    
    /** Constructor. */
    public AddIncidentListTableModel() {
        event_list = new ArrayList<AddIncidentEventInfo>();
    } 

    public String getColumnName(int col) {
        return ADD_INCIDENT_LIST_COLUMNS.columnName(col);
    }
    
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public int getRowCount() {
        if(event_list == null) {
            return 0;
        }
        else
            return event_list.size();
    }

    public int getColumnCount() {
        return ADD_INCIDENT_LIST_COLUMNS.colCount();
    }


    public int getColumnMinWidth(int c){
        return ADD_INCIDENT_LIST_COLUMNS.columnMinWidth(c);
    }


    public int getColumnMaxWidth(int c){
        return ADD_INCIDENT_LIST_COLUMNS.columnMaxWidth(c);
    }


    public int getColumnPrefWidth(int c){
        return ADD_INCIDENT_LIST_COLUMNS.columnPrefWidth(c);
    }

    public boolean isCellEditable(int row, int col) {
        switch (ADD_INCIDENT_LIST_COLUMNS.fromColNum(col)) {
            case ADD_COL:
            case SCHEDULED_COL:
                return true;
            case LOG_NUM_COL:
            case EVENT_DESC_COL:
            case EVENT_LEN_COL:
            default:
                return false;
        }        

    }      
    
    public Object getValueAt(int row, int col) {
        switch (ADD_INCIDENT_LIST_COLUMNS.fromColNum(col)) {
            case ADD_COL:
                return event_list.get(row).addFlag;
            case LOG_NUM_COL:
                return event_list.get(row).logNumber;
            case SCHEDULED_COL:
                return event_list.get(row).scheduleTime;
            case EVENT_DESC_COL:
                return event_list.get(row).description;
            case EVENT_LEN_COL:
                return event_list.get(row).length;
            default: 
                return "";
        }  
    }
       
    public void setValueAt(Object value, int row, int col) {
        switch (ADD_INCIDENT_LIST_COLUMNS.fromColNum(col)) {
            case ADD_COL:
                event_list.get(row).addFlag = (Boolean)value;
                break;
            case SCHEDULED_COL:
                event_list.get(row).scheduleTime = (Long)value;
                break;
        }    
        fireTableChanged(new TableModelEvent(this));
    }
    
    /**
     * Method adds a new incident into the table.
     * @param newIncident Incident object to add.
     */
    public void addIncident(Incident newIncident) {
        event_list.add(new AddIncidentEventInfo(
                newIncident.getLogNumber(),
                newIncident.description,
                newIncident.getIncidentLength(),
                new Boolean(false), new Long(0)));
        fireTableChanged(new TableModelEvent(this));
    }    
    

    /**
     * Method gets a map of incidents, and the scheduled start time
     * for all incidents that have been selected in the table.
     * 
     * @return Map of incident log numbers (key) and scheduled start time (value)
     */
    public TreeMap<Integer, Long> getSelectedIncidentTimes() {
        TreeMap<Integer, Long> selectedIncMap = new TreeMap<Integer, Long>();
        
        for(AddIncidentEventInfo info : event_list) {
            if(info.addFlag)
                selectedIncMap.put(info.logNumber, info.scheduleTime);
        }   
        
        return selectedIncMap;
    }
    
    /** 
     * Remove all incidents from the table's data.
     */    
    public void clearModelData() {
        event_list.clear();
        fireTableChanged(new TableModelEvent(this));
    }
}
