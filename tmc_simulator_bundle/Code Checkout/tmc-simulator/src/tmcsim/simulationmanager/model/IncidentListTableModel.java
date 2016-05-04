package tmcsim.simulationmanager.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import tmcsim.client.cadclientgui.data.Incident;

/** 
 * IncidentListTableModel is a DefaultTableModel used to display the 
 * list of current incidents in the Simulation Manager.  The columns in 
 * this table show the incident log number, it's schedule time, and the
 * incident description. The startIncident() method is used to set
 * an incident in the table to 'started.'  See the method description for
 * more information.
 * 
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class IncidentListTableModel extends DefaultTableModel implements
        Comparator<IncidentListTableItem> {
    
    /**
     * Enumeration of columns for this table.
     * @author Matthew Cechini
     */
    public static enum INCIDENT_LIST_COLUMNS {
        LOG_NUM_COL    ("Log #", 0, 70, 100, 70),
        SCHEDULED_COL  ("Scheduled", 1, 50, 80, 80),
        INC_DESC_COL   ("Incident Description", 2, 130, 460, 200);
        
        public String colName;
        public int colNum;
        public int colMinWidth;
        public int colMaxWidth;
        public int colPrefWidth;
        
        private INCIDENT_LIST_COLUMNS (String name, int num, int min, int max, int pref) {
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
            
            for(INCIDENT_LIST_COLUMNS column : INCIDENT_LIST_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colName;
            }
            
            return "";
        }
        
        public static int columnMinWidth(int num) {
            
            for(INCIDENT_LIST_COLUMNS column : INCIDENT_LIST_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colMinWidth;
            }
            
            return 0;
        }
        
        public static int columnMaxWidth(int num) {
            
            for(INCIDENT_LIST_COLUMNS column : INCIDENT_LIST_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colMaxWidth;
            }
            
            return 0;
        }
        
        public static int columnPrefWidth(int num) {
            
            for(INCIDENT_LIST_COLUMNS column : INCIDENT_LIST_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colPrefWidth;
            }
            
            return 0;
        }
        
        public static INCIDENT_LIST_COLUMNS fromColNum(int column) {
            for(INCIDENT_LIST_COLUMNS c : INCIDENT_LIST_COLUMNS.values()) {
                if(c.colNum == column)
                    return c;
            }
            
            return LOG_NUM_COL;
        }
    }  


    /** List of data displayed in the table. */
    private List<IncidentListTableItem> event_list;
    
    /** 
     * Constructor.
     */
    public IncidentListTableModel() {
        event_list = new ArrayList<IncidentListTableItem>();
    } 

    public String getColumnName(int col) {
        return INCIDENT_LIST_COLUMNS.columnName(col);
    }

    public int getRowCount() {
        if(event_list == null) {
            return 0;
        }
        else
            return event_list.size();
    }

    public int getColumnCount() {
        return INCIDENT_LIST_COLUMNS.colCount();
    }


    public int getColumnMinWidth(int c){
        return INCIDENT_LIST_COLUMNS.columnMinWidth(c);
    }


    public int getColumnMaxWidth(int c){
        return INCIDENT_LIST_COLUMNS.columnMaxWidth(c);
    }


    public int getColumnPrefWidth(int c){
        return INCIDENT_LIST_COLUMNS.columnPrefWidth(c);
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }        
    
    public Object getValueAt(int row, int col) {
        switch (INCIDENT_LIST_COLUMNS.fromColNum(col)) {
            case INC_DESC_COL:
                return event_list.get(row).description;
            case LOG_NUM_COL:
                return event_list.get(row).logNumber;
            case SCHEDULED_COL:
                return event_list.get(row).scheduleTime;
            default:
                return "";
        }        
    }
   
    
    public void setValueAt(Object arg, int row, int col) {
        switch (INCIDENT_LIST_COLUMNS.fromColNum(col)) {
            case INC_DESC_COL:
                event_list.get(row).description  = (String)arg;
                break;
            case LOG_NUM_COL:
                event_list.get(row).logNumber    = (Integer)arg;
                break;
            case SCHEDULED_COL:
                event_list.get(row).scheduleTime = (Long)arg;
                break;
        }        

        fireTableChanged(new TableModelEvent(this));
    }
    
    public int compare(IncidentListTableItem item0, IncidentListTableItem item1) {
        return item0.compareTo(item1);
    }
    
    /**
     * Find the insertion point for the argument in a sorted list.
     * 
     * @param element find this object's insertion point in the sorted list
     * @return the index of the insertion point
     */
    private int findInsertionPoint(IncidentListTableItem element) {

        // Find the new element's insertion point.
        int insertionPoint = Collections.binarySearch(event_list, element, this);
        if (insertionPoint < 0) {
            insertionPoint = -(insertionPoint + 1);
        }
        return insertionPoint;
    }
    
    /**
     * Method 'starts' the incident from the table whose log number
     * matches the parameter value.  When an incident is 'started,' its
     * scheduled time is set to -1.  This specific value is used by
     * the CellRenderer to display "Started" in the table.
     * @param logNumber Unique log number of an incident to 'start.'
     */
    public void startIncident(Integer logNumber) {
        for(IncidentListTableItem eventInfo : event_list) {
            if(eventInfo.logNumber.equals(logNumber)) {
                eventInfo.scheduleTime = -1L;
                fireTableChanged(new TableModelEvent(this));
            }
        }
    }

    /**
     * Method adds a new incident into the table.
     * @param newIncident Incident object to add.
     */
    public void addIncident(Incident newIncident) {
        
        IncidentListTableItem newItem = new IncidentListTableItem(
                newIncident.getLogNumber(),
                newIncident.getSecondsToStart(),
                newIncident.description);
        
        event_list.add(findInsertionPoint(newItem), newItem);
        fireTableChanged(new TableModelEvent(this));
    }
    
    /**
     * Method removes the incident from the table whose log number
     * matches the parameter value.
     * @param logNumber Unique log number of incident to remove.
     */
    public void removeIncident(Integer logNumber) {
        for(Iterator<IncidentListTableItem> eventInfoIter = event_list.iterator();
            eventInfoIter.hasNext();) {
            
            if(eventInfoIter.next().logNumber.equals(logNumber)) {
                eventInfoIter.remove(); 
                fireTableChanged(new TableModelEvent(this));
            }
        }       
    }
   
    /** 
     * Remove all incidents from the table's data.
     */
    public void clearModelData() {
        event_list.clear();
        fireTableChanged(new TableModelEvent(this));
    }


}
