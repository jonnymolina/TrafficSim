package tmcsim.cadsimulator.viewer.model;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import tmcsim.cadsimulator.videocontrol.DVDStatusUpdate;

/**
 * DVDStatusTableModel extends from DefaultTableModel to display DVDStatusUpdates that
 * are received from a DVDController. The addStatusUpdate() method is used to add new
 * update objects. The table may be cleared through the clearModelData() method.
 *
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class DVDStatusTableModel extends DefaultTableModel
{
    /**
     * Enumeration containing column information for all columns shown in the DVDStatus
     * table.
     *
     * @author Matthew Cechini
     */
    public static enum STATUS_COLUMNS
    {
        /**
         * Time column enum.
         */
        TIME_COL("Time", 0, 60, 100, 80),
        /**
         * Update info enum.
         */
        UPDATE_INFO_COL("Status Info", 1, 140, 400, 420);
        private String colName;
        private int colNum;
        private int colMinWidth;
        private int colMaxWidth;
        private int colPrefWidth;

        private STATUS_COLUMNS(String name, int num, int min, int max, int pref)
        {
            colName = name;
            colNum = num;
            colMinWidth = min;
            colMaxWidth = max;
            colPrefWidth = pref;
        }

        /**
         * Count the columns.
         * @return the column count
         */
        public static int colCount()
        {
            return values().length;
        }

        /**
         * Get the column name at a certain index.
         * @param num indexed column
         * @return column name
         */
        public static String columnName(int num)
        {
            // iterate through all columns
            for (STATUS_COLUMNS column : STATUS_COLUMNS.values())
            {
                // check for target column
                if (column.colNum == num)
                {
                    return column.colName;
                }
            }

            return "";
        }

        /**
         * Get column min width
         * @param num the target column
         * @return the width
         */
        public static int columnMinWidth(int num)
        {
            // iterate through all columns
            for (STATUS_COLUMNS column : STATUS_COLUMNS.values())
            {
                // check for target column
                if (column.colNum == num)
                {
                    return column.colMinWidth;
                }
            }

            return 0;
        }

        /**
         * Get column max width.
         * @param num target column
         * @return the width
         */
        public static int columnMaxWidth(int num)
        {
            // iterate through all columns
            for (STATUS_COLUMNS column : STATUS_COLUMNS.values())
            {
                // check for target column
                if (column.colNum == num)
                {
                    return column.colMaxWidth;
                }
            }

            return 0;
        }

        /**
         * Get column preferred width.
         * @param num target column
         * @return the width
         */
        public static int columnPrefWidth(int num)
        {
            // iterate through all columns
            for (STATUS_COLUMNS column : STATUS_COLUMNS.values())
            {   // target column
                if (column.colNum == num)
                {
                    return column.colPrefWidth;
                }
            }

            return 0;
        }

        /**
         * Get status of a column.
         * @param column target column
         * @return status column
         */
        public static STATUS_COLUMNS fromColNum(int column)
        {
            // iterate through all columns
            for (STATUS_COLUMNS col : STATUS_COLUMNS.values())
            {
                // target column
                if (col.colNum == column)
                {
                    return col;
                }
            }

            return TIME_COL;
        }
    }

    /**
     * Container class to hold information displayed in the table.
     *
     * @author Matthew Cechini
     */
    private class StatusUpdateInfo
    {
        /**
         * Formatted time text of when the status update was received.
         */
        private String time;
        /**
         * Text of status update.
         */
        private String statusText;

        public StatusUpdateInfo(String t, String s)
        {
            time = t;
            statusText = s;
        }
    }
    /**
     * List of data displayed in the table.
     */
    private List<StatusUpdateInfo> statusUpdateList;
    /**
     * DateFormat object used to format time formatted text for display.
     */
    private DateFormat timeFormatter;

    /**
     * Constructor. Intialize the DateFormat object.
     */
    public DVDStatusTableModel()
    {
        statusUpdateList = new ArrayList<StatusUpdateInfo>();
        timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);
    }

    /**
     * Get row count.
     * @return row count
     */
    public int getRowCount()
    {   // no rows
        if (statusUpdateList == null)
        {
            return 0;
        }
        else
        {
            return statusUpdateList.size();
        }
    }

    /**
     * Get column count.
     * @return column count
     */
    public int getColumnCount()
    {
        return STATUS_COLUMNS.colCount();
    }

    /**
     * Get min column width
     * @param c target column
     * @return width
     */
    public int getColumnMinWidth(int c)
    {
        return STATUS_COLUMNS.columnMinWidth(c);
    }

    /**
     * Get max column width
     * @param c target column
     * @return width
     */
    public int getColumnMaxWidth(int c)
    {
        return STATUS_COLUMNS.columnMaxWidth(c);
    }

    /**
     * Get preferred column width
     * @param c target column
     * @return width
     */
    public int getColumnPrefWidth(int c)
    {
        return STATUS_COLUMNS.columnPrefWidth(c);
    }

    /**
     * Get cell value.
     * @param row target row
     * @param col target column
     * @return cell value
     */
    public Object getValueAt(int row, int col)
    {   // get cell
        switch (STATUS_COLUMNS.fromColNum(col))
        {
            case TIME_COL:
                return statusUpdateList.get(row).time;
            case UPDATE_INFO_COL:
                return statusUpdateList.get(row).statusText;
            default:
                return "";
        }
    }

    /**
     * Set the cell value.
     * @param value value to be inserted
     * @param row target row
     * @param col target column
     */
    @SuppressWarnings("unchecked")
    public void setValueAt(Object value, int row, int col)
    {
        // get cell
        switch (STATUS_COLUMNS.fromColNum(col))
        {
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
            default:
                break;
        }
    }

    /**
     * Get column name.
     * @param col target column
     * @return name
     */
    public String getColumnName(int col)
    {
        return STATUS_COLUMNS.columnName(col);
    }

    /**
     * Get column class.
     * @param c target column
     * @return column class
     */
    public Class<?> getColumnClass(int c)
    {
        return getValueAt(0, c).getClass();
    }

    /**
     * Determine if cell is editable.
     * @param row target row
     * @param col target column
     * @return true if editable, false if not
     */
    public boolean isCellEditable(int row, int col)
    {
        return false;
    }

    /**
     * This method adds a new status update to the table. If the parameter update
     * contains an exception, the status text will be "Exception:
     * <Message>". The status text will be "Connected" or "Disconnected" depending on the
     * value of the isConnected data member. The current time is used to time stamp the
     * received update.
     *
     * @param update New update object.
     */
    public void addStatusUpdate(DVDStatusUpdate update)
    {

        StringBuffer updateBuf = new StringBuffer();
        // show exception
        if (update.exception != null)
        {
            updateBuf.append("Exception: " + update.exception.getMessage());
        }
        // show connected
        else if (update.isConnected())
        {
            updateBuf.append("Connected");
        }
        else
        {
            updateBuf.append("Disconnected");
        }

        statusUpdateList.add(new StatusUpdateInfo(
                timeFormatter.format(new Date()),
                updateBuf.toString()));

        fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Remove all Status Updates from the table's data.
     */
    public void clearModelData()
    {
        statusUpdateList.clear();
        fireTableChanged(new TableModelEvent(this));
    }
}
