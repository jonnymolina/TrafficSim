package tmcsim.cadsimulator.viewer.model;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import tmcsim.cadsimulator.videocontrol.DVDTitleUpdate;

/**
 * DVDStatusTableModel extends from DefaultTableModel to display DVDStatusUpdates that
 * are received from a DVDController. The addStatusUpdate() method is used to add new
 * update objects. The table may be cleared through the clearModelData() method.
 *
 * @author Matthew
 * @version
 */
@SuppressWarnings("serial")
public class DVDTitleTableModel extends DefaultTableModel
{
    /**
     * Enumeration containing column information for all columns shown in the DVDTitle
     * table.
     *
     * @author Matthew Cechini
     */
    public static enum TITLE_COLUMNS
    {
        /**
         * Time column enum.
         */
        TIME_COL("Time", 0, 60, 100, 80),
        /**
         * Update info enum.
         */
        UPDATE_INFO_COL("Title Info", 1, 140, 400, 420);
        private String colName;
        private int colNum;
        private int colMinWidth;
        private int colMaxWidth;
        private int colPrefWidth;

        private TITLE_COLUMNS(String name, int num, int min, int max, int pref)
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
            for (TITLE_COLUMNS column : TITLE_COLUMNS.values())
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
            for (TITLE_COLUMNS column : TITLE_COLUMNS.values())
            {   // check for target column
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
            for (TITLE_COLUMNS column : TITLE_COLUMNS.values())
            {   // check for target column
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
            for (TITLE_COLUMNS column : TITLE_COLUMNS.values())
            {   // target column
                if (column.colNum == num)
                {
                    return column.colPrefWidth;
                }
            }

            return 0;
        }

        /**
         * Get title of target column.
         * @param column target column
         * @return title status
         */
        public static TITLE_COLUMNS fromColNum(int column)
        {   // iterate through all columns
            for (TITLE_COLUMNS col : TITLE_COLUMNS.values())
            {   // target column
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
    private class TitleUpdateInfo
    {
        /**
         * Formatted time text of when the title update was received.
         */
        private String time;
        /**
         * Text for title update.
         */
        private String titleUpdate;

        public TitleUpdateInfo(String t, String u)
        {
            time = t;
            titleUpdate = u;
        }
    }
    /**
     * List of data displayed in the table.
     */
    private List<TitleUpdateInfo> titleUpdateList;
    /**
     * DateFormat object used to format time formatted text for display.
     */
    private DateFormat timeFormatter;

    /**
     * Constructor. Intialize the DateFormat object.
     */
    public DVDTitleTableModel()
    {
        titleUpdateList = new ArrayList<TitleUpdateInfo>();
        timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);
    }
    /**
     * Get row count.
     * @return row count
     */
    public int getRowCount()
    {   // no rows
        if (titleUpdateList == null)
        {
            return 0;
        }
        else
        {
            return titleUpdateList.size();
        }
    }
    /**
     * Get column count.
     * @return column count
     */
    public int getColumnCount()
    {
        return TITLE_COLUMNS.colCount();
    }
    /**
     * Get min column width
     * @param c target column
     * @return width
     */
    public int getColumnMinWidth(int c)
    {
        return TITLE_COLUMNS.columnMinWidth(c);
    }
    /**
     * Get max column width
     * @param c target column
     * @return width
     */
    public int getColumnMaxWidth(int c)
    {
        return TITLE_COLUMNS.columnMaxWidth(c);
    }
    /**
     * Get preferred column width
     * @param c target column
     * @return width
     */
    public int getColumnPrefWidth(int c)
    {
        return TITLE_COLUMNS.columnPrefWidth(c);
    }
    /**
     * Get cell value.
     * @param row target row
     * @param col target column
     * @return cell value
     */
    public Object getValueAt(int row, int col)
    {
        // get cell
        switch (TITLE_COLUMNS.fromColNum(col))
        {
            case TIME_COL:
                return titleUpdateList.get(row).time;
            case UPDATE_INFO_COL:
                return titleUpdateList.get(row).titleUpdate;
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
    {   // get cell
        switch (TITLE_COLUMNS.fromColNum(col))
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
        return TITLE_COLUMNS.columnName(col);
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
     * This method adds a new title update to the table. If the title has been repeated,
     * the text will be "Repeating title #<Number> <Addl Info>". If the title is being
     * played for the first time, the text will be "Playing title #<Number> <Addl Info>".
     * If the title is being played for an incident, the <Addl Info> will be "for
     * Incident #<Number>". Else, the title is being played for a speed range and the
     * <Addl Info>
     * will be "for Range <Min Speed> - <Max Speed> mph (<duration> secs)".
     *
     * @param update New update object.
     */
    public void addTitleUpdate(DVDTitleUpdate update)
    {

        StringBuffer updateBuf = new StringBuffer();
        // repeat
        if (update.isRepeat())
        {
            updateBuf.append("Repeating title #");
        }
        else
        {
            updateBuf.append("Playing title #");
        }
        // playing incident
        if (update.isPlayingIncident())
        {
            updateBuf.append(
                    update.getCurrentIncident().getDvdTitle() + " for Incident #");
            updateBuf.append(update.getCurrentIncident().getIncidentNumber());
        }
        else
        {
            updateBuf.append(update.getCurrentRange().getDvdTitle() + " for Range ");
            updateBuf.append(update.getCurrentRange().getMinSpeed() + " - ");
            updateBuf.append(update.getCurrentRange().getMaxSpeed() + " mph");
        }

        titleUpdateList.add(new TitleUpdateInfo(
                timeFormatter.format(new Date()),
                updateBuf.toString()));

        fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Remove all Status Updates from the table's data.
     */
    public void clearModelData()
    {
        titleUpdateList.clear();
        fireTableChanged(new TableModelEvent(this));
    }
}
