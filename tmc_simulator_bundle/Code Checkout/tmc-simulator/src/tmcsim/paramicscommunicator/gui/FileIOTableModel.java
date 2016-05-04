package tmcsim.paramicscommunicator.gui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import tmcsim.paramicscommunicator.FileIOUpdate;

/**
 * FileIOTableModel is a DefaultTableModel used to display the 
 * list of I/O operations that have been performed by a 
 * Paramics FileWriter or FileReader. The columns in this table 
 * show the I/O time and bytes written or read. 
 * 
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class FileIOTableModel extends DefaultTableModel {

    /**
     * Enumeration of columns for this table.
     * @author Matthew Cechini
     */     
    public static enum FILE_IO_COLUMNS {
        TIME_COL       ("Time", 0, 100),
        NUM_BYTES_COL  ("Num Bytes", 1, 60);
        
        public String colName;
        public int colNum;
        public int colWidth;
        
        private FILE_IO_COLUMNS (String name, int num, int width) {
            colName  = name;
            colNum   = num;
            colWidth = width;
        }
        
        public static int colCount() {
            return values().length;
        }
        
        public static String columnName(int num) {
            
            for(FILE_IO_COLUMNS column : FILE_IO_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colName;
            }
            
            return "";
        }
        
        public static int columnWidth(int num) {
            
            for(FILE_IO_COLUMNS column : FILE_IO_COLUMNS.values()) {
                if(column.colNum == num)
                    return column.colWidth;
            }
            
            return 0;
        }
    }
    
    /** 
     * Container class to hold information displayed in the table.
     * @author Matthew Cechini
     */     
    protected class FileIOTableItem {
        String  ioTime;
        Long    ioBytes;
        
        public FileIOTableItem(String time, Long bytes) {
            ioTime  = time;
            ioBytes = bytes;
        }
    }
    

    /** List of data displayed in the table. */
    protected List<FileIOTableItem> tableData;
    
    /** DateFormat used to format time table values. */
    protected DateFormat timeFormatter;
    
    /** Constructor. */
    public FileIOTableModel() {
        tableData = new ArrayList<FileIOTableItem>();
        
        timeFormatter = DateFormat.getTimeInstance();
    }   

    /**
     *  Gets the rowCount attribute of the RideTableModel object
     *
     *@return    The rowCount value
     */
    public int getRowCount() {
        if(tableData == null)
            return 0;
        else
            return tableData.size();
    }



    /**
     *  Gets the columnCount attribute of the RideTableModel object
     *
     *@return    The columnCount value
     */
    public int getColumnCount() {
        return FILE_IO_COLUMNS.colCount();
    }



    /**
     *  Gets the valueAt attribute of the RideTableModel object
     *
     *@param  row  Description of the Parameter
     *@param  col  Description of the Parameter
     *@return      The valueAt value
     */
    public Object getValueAt(int row, int col) {
        
        if(col == FILE_IO_COLUMNS.TIME_COL.colNum)
            return tableData.get(row).ioTime;
        else if(col == FILE_IO_COLUMNS.NUM_BYTES_COL.colNum)
            return tableData.get(row).ioBytes;
        else
            return "";
    }


    public String getColumnName(int col) {
        return FILE_IO_COLUMNS.columnName(col);
    }

    public int getColumnWidth(int col){
        return FILE_IO_COLUMNS.columnWidth(col);
    }
    
    /**
     *  Gets the cellEditable attribute of the RideTableModel object
     *
     *@param  row  Description of the Parameter
     *@param  col  Description of the Parameter
     *@return      The cellEditable value
     */
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    
    /**
     * Add a new IOUpdate into the table model. Update the table.
     * @param update New update object.
     */
    public void addIOUpdate(FileIOUpdate update) {
 
        tableData.add(new FileIOTableItem(timeFormatter.format(new Date()), 
                update.ioBytes));
        
        fireTableChanged(new TableModelEvent(this));        
    }
    

}
