package tmcsim.cadsimulator.viewer.model;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import tmcsim.cadsimulator.videocontrol.DVDTitleUpdate;

/**
 * DVDStatusTableModel extends from DefaultTableModel to display 
 * DVDStatusUpdates that are received from a DVDController.  The
 * addStatusUpdate() method is used to add new update objects.
 * The table may be cleared through the clearModelData() method.
 * 
 * @author Matthew
 * @version
 */
@SuppressWarnings("serial")
public class DVDTitleTableModel extends DefaultTableModel {
    
    
    /**
     * Enumeration containing column information for all columns shown in the 
     * DVDTitle table.
     * @author Matthew Cechini
     */ 
    public static enum TITLE_COLUMNS {
        TIME_COL           ("Time", 0, 60, 100, 80),
        UPDATE_INFO_COL    ("Title Info", 1, 140, 400, 420);
        
        public String colName;
        public int colNum;
        public int colMinWidth;
        public int colMaxWidth;
        public int colPrefWidth;
        
        private TITLE_COLUMNS (String name, int num, int min, int max, int pref) {
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
            
            for(TITLE_COLUMNS column : TITLE_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colName;
            }
            
            return "";
        }
        
        public static int columnMinWidth(int num) {
            
            for(TITLE_COLUMNS column : TITLE_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colMinWidth;
            }
            
            return 0;
        }
        
        public static int columnMaxWidth(int num) {
            
            for(TITLE_COLUMNS column : TITLE_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colMaxWidth;
            }
            
            return 0;
        }
        
        public static int columnPrefWidth(int num) {
            
            for(TITLE_COLUMNS column : TITLE_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colPrefWidth;
            }
            
            return 0;
        }
        
        public static TITLE_COLUMNS fromColNum(int column) {
            for(TITLE_COLUMNS c : TITLE_COLUMNS.values()) {
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
    private class TitleUpdateInfo {

        /** Formatted time text of when the title update was received. */
        public String time;

        /** Text for title update. */
        public String titleUpdate;
        
        public TitleUpdateInfo(String t, String u) {
            time        = t;
            titleUpdate = u;
        }
    }

    /** List of data displayed in the table. */
    private List<TitleUpdateInfo> title_update_list;

    /** DateFormat object used to format time formatted text for display. */
    private DateFormat timeFormatter;

    
    /** Constructor.  Intialize the DateFormat object. */
    public DVDTitleTableModel() {
        title_update_list = new ArrayList<TitleUpdateInfo>();
        timeFormatter     = DateFormat.getTimeInstance(DateFormat.SHORT);
    }

    public int getRowCount() {
        if(title_update_list == null) {
            return 0;
        }
        else
            return title_update_list.size();
    }

    public int getColumnCount() {
        return TITLE_COLUMNS.colCount();
    }
    
    public int getColumnMinWidth(int c){
        return TITLE_COLUMNS.columnMinWidth(c);
    }


    public int getColumnMaxWidth(int c){
        return TITLE_COLUMNS.columnMaxWidth(c);
    }


    public int getColumnPrefWidth(int c){
        return TITLE_COLUMNS.columnPrefWidth(c);
    }
    
    
    public Object getValueAt(int row, int col) {
        switch (TITLE_COLUMNS.fromColNum(col)) {
            case TIME_COL:
                return title_update_list.get(row).time;
            case UPDATE_INFO_COL:
                return title_update_list.get(row).titleUpdate; 
            default:
                return "";
        }        
    }
    
    @SuppressWarnings("unchecked")
    public void setValueAt(Object value, int row, int col) {
        switch (TITLE_COLUMNS.fromColNum(col)) {
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
        return TITLE_COLUMNS.columnName(col);
    }

    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    
    public boolean isCellEditable(int row, int col) {
        return false;
    }
    
    /**
     * This method adds a new title update to the table.  If the title has
     * been repeated, the text will be "Repeating title #<Number> <Addl Info>".
     * If the title is being played for the first time, the text will be 
     * "Playing title #<Number> <Addl Info>".  If the title is being played 
     * for an incident, the <Addl Info> will be "for Incident #<Number>".
     * Else, the title is being played for a speed range and the <Addl Info>
     * will be "for Range <Min Speed> - <Max Speed> mph (<duration> secs)".
     * 
     * @param update New update object.
     */    
    public void addTitleUpdate(DVDTitleUpdate update) {
       
        StringBuffer updateBuf = new StringBuffer();
        
        if(update.isRepeat)
            updateBuf.append("Repeating title #");
        else
            updateBuf.append("Playing title #");        
        
        if(update.isPlayingIncident) {
            updateBuf.append(update.currentIncident.dvdTitle + " for Incident #");
            updateBuf.append(update.currentIncident.incidentNumber);
        }
        else {
            updateBuf.append(update.currentRange.dvdTitle + " for Range ");
            updateBuf.append(update.currentRange.minSpeed + " - ");
            updateBuf.append(update.currentRange.maxSpeed + " mph");
        }
        
        title_update_list.add(new TitleUpdateInfo(
                timeFormatter.format(new Date()), 
                updateBuf.toString()));
        
        fireTableChanged(new TableModelEvent(this));
    }
    
    /** 
     * Remove all Status Updates from the table's data.
     */     
    public void clearModelData() {
        title_update_list.clear();
        fireTableChanged(new TableModelEvent(this));
    }   

    
}
