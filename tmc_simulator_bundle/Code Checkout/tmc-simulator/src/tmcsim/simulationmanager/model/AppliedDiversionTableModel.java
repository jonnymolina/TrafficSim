package tmcsim.simulationmanager.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import tmcsim.cadmodels.CMSDiversion;
import tmcsim.cadmodels.CMSInfo;

/** 
 * AppliedDiversionTableModel is a DefaultTableModel used to display the 
 * list of current diversions in the Simulation Manager.  The columns in 
 * this table show the divesion cms id, old path, new path, diversion pct,
 * and simulation time it was applied. 
 * 
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class AppliedDiversionTableModel extends DefaultTableModel implements
    Comparator<AppliedDiversionTableItem> {
    
    /**
     * Enumeration of columns for this table.
     * @author Matthew Cechini
     */     
    public static enum APPLIED_DIV_COLUMNS {
        CMS_ID_COL     ("ID", 0, 140, 280, 180),
        OLD_PATH_COL   ("Old Path", 1, 70, 120, 70),
        NEW_PATH_COL   ("New Path", 2, 70, 120, 70),
        PERCENT_COL    ("%", 3, 30, 60, 30),
        APPLIED_COL    ("Applied", 4, 70, 120, 70);
        
        public String colName;
        public int colNum;
        public int colMinWidth;
        public int colMaxWidth;
        public int colPrefWidth;
        
        private APPLIED_DIV_COLUMNS (String name, int num, int min, int max, int pref) {
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
            
            for(APPLIED_DIV_COLUMNS column : APPLIED_DIV_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colName;
            }
            
            return "";
        }
        
        public static int columnMinWidth(int num) {
            
            for(APPLIED_DIV_COLUMNS column : APPLIED_DIV_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colMinWidth;
            }
            
            return 0;
        }
        
        public static int columnMaxWidth(int num) {
            
            for(APPLIED_DIV_COLUMNS column : APPLIED_DIV_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colMaxWidth;
            }
            
            return 0;
        }
        
        public static int columnPrefWidth(int num) {
            
            for(APPLIED_DIV_COLUMNS column : APPLIED_DIV_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colPrefWidth;
            }
            
            return 0;
        }
        
        public static APPLIED_DIV_COLUMNS fromColNum(int column) {
            for(APPLIED_DIV_COLUMNS c : APPLIED_DIV_COLUMNS.values()) {
                if(c.colNum == column)
                    return c;
            }
            
            return CMS_ID_COL;
        }
    }  


    /** List of data displayed in the table. */
    private List<AppliedDiversionTableItem> diversion_list;
    
    /** Constructor. */
    public AppliedDiversionTableModel() {
        diversion_list = new ArrayList<AppliedDiversionTableItem>();
    }

    public String getColumnName(int col) {
        return APPLIED_DIV_COLUMNS.columnName(col);
    }

    public int getRowCount() {
        if(diversion_list == null) {
            return 0;
        }
        else
            return diversion_list.size();
    }

    public int getColumnCount() {
        return APPLIED_DIV_COLUMNS.colCount();
    }

    public int getColumnMinWidth(int c){
        return APPLIED_DIV_COLUMNS.columnMinWidth(c);
    }

    public int getColumnMaxWidth(int c){
        return APPLIED_DIV_COLUMNS.columnMaxWidth(c);
    }

    public int getColumnPrefWidth(int c){
        return APPLIED_DIV_COLUMNS.columnPrefWidth(c);
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }          
    
    public Object getValueAt(int row, int col) {
        switch (APPLIED_DIV_COLUMNS.fromColNum(col)) {
            case APPLIED_COL:
                return diversion_list.get(row).timeApplied;
            case NEW_PATH_COL:
                return diversion_list.get(row).newPath;
            case CMS_ID_COL:
                return diversion_list.get(row).cmsID;
            case OLD_PATH_COL:
                return diversion_list.get(row).originalPath;
            case PERCENT_COL:
                return diversion_list.get(row).currentDiv;
            default:
                return "";
        }        
    }
    
    public int compare(AppliedDiversionTableItem item0, AppliedDiversionTableItem item1) {
        return item0.compareTo(item1);
    }
    
    /**
     * Find the insertion point for the argument in a sorted list.
     * 
     * @param element find this object's insertion point in the sorted list
     * @return the index of the insertion point
     */
    private int findInsertionPoint(AppliedDiversionTableItem element) {

        // Find the new element's insertion point.
        int insertionPoint = Collections.binarySearch(diversion_list, element, this);
        if (insertionPoint < 0) {
            insertionPoint = -(insertionPoint + 1);
        }
        return insertionPoint;
    }
    
    /**
     * Method adds a new diversion into the table.
     * @param theCMSInfo CMSInfo that the diversion is set for.
     * @param theDiversion The new diversion data.
     */        
    public void addDiversion(CMSInfo theCMSInfo, CMSDiversion theDiversion) {
        
        AppliedDiversionTableItem newDiv = new AppliedDiversionTableItem(
                theCMSInfo.cmsID,
                theDiversion.originalPath,
                theDiversion.newPath,
                theDiversion.getCurrDiv(),
                theDiversion.timeApplied);
        
        diversion_list.add(findInsertionPoint(newDiv), newDiv);
        
        fireTableChanged(new TableModelEvent(this));
            
    }
    
    /**
     * Method removes a diversion from the table.
     * @param theCMSInfo CMSInfo that the diversion is set for.
     * @param theDiversion The new diversion data.
     */        
    public void removeDiversion(CMSInfo theCMSInfo, CMSDiversion theDiversion) {
        
        //loop through all rows.  Move down a row until the cmsID for the current diversion
        //is greater ("04 - CMS 4" > "03 - CMS 3") than the current row, or the matching diversion row is found
        //If the matching row is found, delete the current row for the new info will be added below. 
        
        AppliedDiversionTableItem divInfo = null;
        for(Iterator<AppliedDiversionTableItem> divInfoIter = diversion_list.iterator();
            divInfoIter.hasNext();) {
            
            divInfo = divInfoIter.next();
            
            if(divInfo.cmsID.equals(theCMSInfo.cmsID)) {
                if(divInfo.originalPath.equals(theDiversion.originalPath) &&
                   divInfo.newPath.equals(theDiversion.newPath)) {
                    divInfoIter.remove();
                }
            }
        }

        fireTableChanged(new TableModelEvent(this));
    }
    
    /** 
     * Remove all diversions from the table's data.
     */    
    public void clearModelData() {
        diversion_list.clear();
        fireTableChanged(new TableModelEvent(this));
    }
}
