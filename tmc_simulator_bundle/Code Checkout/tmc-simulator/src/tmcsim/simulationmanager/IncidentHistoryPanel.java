package tmcsim.simulationmanager;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import tmcsim.client.cadclientgui.data.IncidentEvent;
import tmcsim.simulationmanager.model.IncidentHistoryTableModel;
import tmcsim.simulationmanager.model.IncidentTimeCellRenderer;
import tmcsim.simulationmanager.model.LogEntryCellRenderer;
import tmcsim.simulationmanager.model.IncidentHistoryTableModel.EVENT_HIST_COLUMNS;

/**
 * 
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class IncidentHistoryPanel extends JPanel {

    /**  */
    private JTable eventHistoryTable = null;
    
    /**  */
    private IncidentHistoryTableModel eventHistoryTableModel;
    
    
    public IncidentHistoryPanel() {
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    
        incTypeLbl = new JLabel("Type:");
        incTypeLbl.setAlignmentX(Box.LEFT_ALIGNMENT);
        
        incTypeTF = new JTextField();
        incTypeTF.setAlignmentX(Box.LEFT_ALIGNMENT);
        //incTypeBox.setMinimumSize(new Dimension(50, 20));
        //incTypeBox.setPreferredSize(new Dimension(100, 20));
        incTypeTF.setMaximumSize(new Dimension(200, 20));
        incTypeTF.setEditable(false);
        
        Box incTypeBox = Box.createVerticalBox();
        incTypeBox.setAlignmentY(Box.CENTER_ALIGNMENT);
        incTypeBox.add(incTypeLbl);
        incTypeBox.add(incTypeTF);
        incTypeBox.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));

        
        incLocLbl = new JLabel("Location:");
        incLocLbl.setAlignmentX(Box.LEFT_ALIGNMENT);
        
        incLocTF = new JTextField();
        incLocTF.setAlignmentX(Box.LEFT_ALIGNMENT);
        incLocTF.setMaximumSize(new Dimension(800, 20));
        incLocTF.setEditable(false);
        
        Box incLocBox = Box.createVerticalBox();
        incLocBox.setAlignmentY(Box.CENTER_ALIGNMENT);
        incLocBox.add(incLocLbl);
        incLocBox.add(incLocTF);
                
        Box incidentInfoBox = Box.createHorizontalBox();
        incidentInfoBox.setAlignmentX(Box.CENTER_ALIGNMENT);
        incidentInfoBox.setMaximumSize(new Dimension(1000, 75));
        incidentInfoBox.add(Box.createHorizontalStrut(10));
        incidentInfoBox.add(incTypeBox);
        incidentInfoBox.add(Box.createHorizontalStrut(10));
        incidentInfoBox.add(incLocBox);
        incidentInfoBox.add(Box.createHorizontalStrut(10));
        incidentInfoBox.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), 
                "Incident Information"));
        
        

        eventHistoryTableModel = new IncidentHistoryTableModel();
        eventHistoryTable = new JTable(eventHistoryTableModel);  
        eventHistoryTable.getTableHeader().setReorderingAllowed(false);  
        
        for(int c = 0; c < eventHistoryTable.getColumnCount(); c++) {
            eventHistoryTable.getColumnModel().getColumn(c).setMinWidth(
                    eventHistoryTableModel.getColumnMinWidth(c));
            eventHistoryTable.getColumnModel().getColumn(c).setMaxWidth(
                    eventHistoryTableModel.getColumnMaxWidth(c));
            eventHistoryTable.getColumnModel().getColumn(c).setPreferredWidth(
                    eventHistoryTableModel.getColumnPrefWidth(c));
            eventHistoryTable.getColumnModel().getColumn(c).setResizable(true);
            
            if(c == EVENT_HIST_COLUMNS.TIME_COL.colNum)
                eventHistoryTable.getColumnModel().getColumn(c).setCellRenderer(
                        new IncidentTimeCellRenderer());
            else if (c == EVENT_HIST_COLUMNS.EVENT_DESC_COL.colNum)
                eventHistoryTable.getColumnModel().getColumn(c).setCellRenderer(
                        new LogEntryCellRenderer());            
                
        }       
        
        logScrollPane     = new JScrollPane(new JLabel(""), 
                javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        logScrollPane.setAlignmentX(Box.CENTER_ALIGNMENT);      
        logScrollPane.setViewportView(eventHistoryTable);
        logScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Log Entries"));
        
    
        add(incidentInfoBox);
        add(Box.createVerticalStrut(10));
        add(logScrollPane);
    }
    
    public void updateIncidentHistory(IncidentEvent event) {
        
        if(event.eventInfo.getHeader().type.trim().length() > 0 &&
            !incTypeTF.getText().trim().equals(event.eventInfo.getHeader().type.trim())) 
        {
            incTypeTF.setText(event.eventInfo.getHeader().type.trim());         
        }

        if(event.eventInfo.getHeader().fullLocation.trim().length() > 0&&
            !incLocTF.getText().trim().equals(event.eventInfo.getHeader().fullLocation.trim()))
        {
            incLocTF.setText(event.eventInfo.getHeader().fullLocation.trim());
        }

        eventHistoryTableModel.addEvent(event);
    }
        
    private JLabel incTypeLbl;
    private JLabel incLocLbl;
    
    private JTextField incTypeTF;
    private JTextField incLocTF;
    
    private JScrollPane logScrollPane;

}
