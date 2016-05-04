package tmcsim.simulationmanager.model;

import java.awt.Component;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * TableCellRender to display a multi-line cell.
 * 
 * http://www.roseindia.net/javatutorials/JTable_in_JDK.shtml
 * @author Dr. Heinz M. Kabutz
 */
@SuppressWarnings("serial")
public class LogEntryCellRenderer extends JTextArea implements
        TableCellRenderer {

    /** DefaultTableCellRenderer for displaying multi line cells*/
    private final DefaultTableCellRenderer adaptee;

    /** map from table to map of rows to map of column heights */
    private final Map<JTable, Map<Integer, Map<Integer, Integer>>> cellSizes;

    public LogEntryCellRenderer() {

        adaptee   = new DefaultTableCellRenderer();
        cellSizes = new HashMap<JTable, Map<Integer, Map<Integer, Integer>>>();
        
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object obj,
            boolean isSelected, boolean hasFocus, int row, int column) {

        // set the colours, etc. using the standard for that platform
        adaptee.getTableCellRendererComponent(table, obj, isSelected, hasFocus,
                row, column);
        setForeground(adaptee.getForeground());
        setBackground(adaptee.getBackground());
        setBorder(adaptee.getBorder());
        setFont(adaptee.getFont());
        setText(adaptee.getText());

        // This line was very important to get it working with JDK1.4
        TableColumnModel columnModel = table.getColumnModel();
        setSize(columnModel.getColumn(column).getWidth(), 100000);
        int height_wanted = (int) getPreferredSize().getHeight();
        addSize(table, row, column, height_wanted);
        height_wanted = findTotalMaximumRowSize(table, row);
        if (height_wanted != table.getRowHeight(row)) {
            table.setRowHeight(row, height_wanted);
        }
        
        return this;
    }

    private void addSize(JTable table, int row, int column, int height) {
        Map<Integer, Map<Integer, Integer>> rows = cellSizes.get(table);
        if (rows == null) {
            cellSizes.put(table, rows = new HashMap<Integer, Map<Integer, Integer>>());
        }
        
        Map<Integer, Integer> rowheights = rows.get(new Integer(row));
        if (rowheights == null) {
            rows.put(new Integer(row), rowheights = new HashMap<Integer, Integer>());
        }
        rowheights.put(new Integer(column), new Integer(height));
    }

    /**
     * Look through all columns and get the renderer.  If it is
     * also a TextAreaRenderer, we look at the maximum height in
     * its hash table for this row.
     */
    private int findTotalMaximumRowSize(JTable table, int row) {
        int maximum_height = 0;
        Enumeration columns = table.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn tc = (TableColumn) columns.nextElement();
            TableCellRenderer cellRenderer = tc.getCellRenderer();
            if (cellRenderer instanceof LogEntryCellRenderer) {
                LogEntryCellRenderer tar = (LogEntryCellRenderer) cellRenderer;
                maximum_height = Math.max(maximum_height, tar
                        .findMaximumRowSize(table, row));
            }
        }
        return maximum_height;
    }

    private int findMaximumRowSize(JTable table, int row) {
        Map<Integer, Map<Integer, Integer>> rows = cellSizes.get(table);
        if (rows == null)
            return 0;
        
        Map<Integer, Integer> rowheights = rows.get(new Integer(row));
        if (rowheights == null)
            return 0;
        int maximum_height = 0;
        
        for (Iterator it = rowheights.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            int cellHeight = ((Integer) entry.getValue()).intValue();
            maximum_height = Math.max(maximum_height, cellHeight);
        }
        return maximum_height;
    }
}
