package tmcsim.cadsimulator.viewer.model;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import tmcsim.cadsimulator.videocontrol.DVDStatusUpdate;


/**
 * DVDStatusTableModel extends from DefaultTableModel to display 
 * DVDStatusUpdates that are received from a DVDController.  The
 * addStatusUpdate() method is used to add new update objects.
 * The table may be cleared through the clearModelData() method.
 * 
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class DVDStatusTableModel extends DefaultTableModel {

    /**
     * Enumeration containing column information for all columns shown in the 
     * DVDStatus table.
     * @author Matthew Cechini
     */
    public static enum STATUS_COLUMNS {
        TIME_COL           ("Time", 0, 60, 100, 80),
        UPDATE_INFO_COL    ("Status Info", 1, 140, 400, 420);
        
        public String colName;
        public int colNum;
        public int colMinWidth;
        public int colMaxWidth;
        public int colPrefWidth;
        
        private STATUS_COLUMNS (String name, int num, int min, int max, int pref) {
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
            
            for(STATUS_COLUMNS column : STATUS_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colName;
            }
            
            return "";
        }
        
        public static int columnMinWidth(int num) {
            
            for(STATUS_COLUMNS column : STATUS_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colMinWidth;
            }
            
            return 0;
        }
        
        public static int columnMaxWidth(int num) {
            
            for(STATUS_COLUMNS column : STATUS_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colMaxWidth;
            }
            
            return 0;
        }
        
        public static int columnPrefWidth(int num) {
            
            for(STATUS_COLUMNS column : STATUS_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colPrefWidth;
            }
            
            return 0;
        }
        
        public static STATUS_COLUMNS fromColNum(int column) {
            for(STATUS_COLUMNS c : STATUS_COLUMNS.values()) {
                if(c.colNum == column)
                    return c;
            }
            
            return TIME_COL;
        }
    }  
    
    
    /** 
     * Container class to hold information displayed in the table.
     * @author Matthew Cechini
     */ 
    private class StatusUpdateInfo {
        
        /** Formatted time text of when the status update was received. */
        public String time;

        /** Text of status update. */
        public String statusText;
        
        public StatusUpdateInfo(String t, String s) {
            time       = t;
            statusText = s;
        }
    }
    

    /** List of data displayed in the table. */
    private List<StatusUpdateInfo> status_update_list;

    /** DateFormat object used to format time formatted text for display. */
    private DateFormat timeFormatter;

    
    /** Constructor.  Intialize the DateFormat object. */
    public DVDStatusTableModel() {
        status_update_list = new ArrayList<StatusUpdateInfo>();
        timeFormatter      = DateFormat.getTimeInstance(DateFormat.SHORT);
    }

    public int getRowCount() {
        if(status_update_list == null) {
            return 0;
        }
        else
            return status_update_list.size();
    }

    public int getColumnCount() {
        return STATUS_COLUMNS.colCount();
    }
    
    public int getColumnMinWidth(int c){
        return STATUS_COLUMNS.columnMinWidth(c);
    }


    public int getColumnMaxWidth(int c){
        return STATUS_COLUMNS.columnMaxWidth(c);
    }


    public int getColumnPrefWidth(int c){
        return STATUS_COLUMNS.columnPrefWidth(c);
    }
    
    public Object getValueAt(int row, int col) {
        switch (STATUS_COLUMNS.fromColNum(col)) {
            case TIME_COL:
                return status_update_list.get(row).time;
            case UPDATE_INFO_COL:
                return status_update_list.get(row).statusText; 
            default:
                return "";
        }        
    }
    
    @SuppressWarnings("unchecked")
    public void setValueAt(Object value, int row, int col) {
        switch (STATUS_COLUMNS.fromColNum(col)) {
        /*
            case NAME_COL:
                reader_list.get(row).readerName;
            case ADDRESS_COL:
                reader_list.get(row).readerAddress;
            case READER_TYPE_COL:
                reader_list.get(row).readerFullType;
            case CONN_TYPE_COL:
                reader_list.get(row).connType;
            default:
                return "";
        */
        }        
    }    

    public String getColumnName(int col) {
        return STATUS_COLUMNS.columnName(col);
    }

    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }
    
    /**
     * This method adds a new status update to the table.  If the parameter
     * update contains an exception, the status text will be "Exception: 
     * <Message>".  The status text will be "Connected" or "Disconnected" 
     * depending on the value of the isConnected data member.  The current
     * time is used to time stamp the received update.
     * 
     * @param update New update object.
     */
    public void addStatusUpdate(DVDStatusUpdate update) {
       
        StringBuffer updateBuf = new StringBuffer();
        
        if(update.exception != null) {
            updateBuf.append("Exception: " + update.exception.getMessage());
        }
        else if(update.isConnected) {
            updateBuf.append("Connected");
        }
        else {
            updateBuf.append("Disconnected");
        }
        
        status_update_list.add(new StatusUpdateInfo(
                timeFormatter.format(new Date()), 
                updateBuf.toString()));
        
        fireTableChanged(new TableModelEvent(this));
    }
    
    /** 
     * Remove all Status Updates from the table's data.
     */       
    public void clearModelData() {
        status_update_list.clear();
        fireTableChanged(new TableModelEvent(this));
    }   
    
}
