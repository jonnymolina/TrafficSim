package tmcsim.simulationmanager.model;


import java.awt.Component;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.JTable;

/**
 * A DefaultCellEditor overriding the necessary base methods to 
 * provide editor capability for changing the incident time to occur.
 * This editor uses a JSpinner as its editor component.
 * 
 * @author Matthew
 * @version
 */
@SuppressWarnings("serial")
public class IncidentTimeCellEditor extends DefaultCellEditor {

    public IncidentTimeCellEditor(JSpinner spinner) {
          super(new JCheckBox());

        editorComponent = spinner;
        editorComponent.setOpaque(true);
        editorComponent.setBorder(BorderFactory.createEmptyBorder());
        setClickCountToStart(2);
    }

    public Object getCellEditorValue() {
        
        StringTokenizer strTok = new StringTokenizer(
                (String)((JSpinner)editorComponent).getModel().getValue(), ":");
        
        
        return Long.parseLong(strTok.nextToken()) * 3600  +
                    Long.parseLong(strTok.nextToken()) * 60  + 
                    Long.parseLong(strTok.nextToken());

    }

        
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
      
        ((JSpinner)editorComponent).getModel().setValue(value);
        
        return editorComponent;
    }
}
