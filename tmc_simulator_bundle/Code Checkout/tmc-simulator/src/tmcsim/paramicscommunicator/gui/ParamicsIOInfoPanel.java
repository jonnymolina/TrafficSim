package tmcsim.paramicscommunicator.gui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import tmcsim.paramicscommunicator.FileRegUpdate;
import tmcsim.paramicscommunicator.FileIOUpdate.IO_TYPE;
import tmcsim.paramicscommunicator.FileRegUpdate.REG_TYPE;

/**
 * ParamicsIOInfoPanel is a JPanel used in the ParamicsCommunicatorGUI to 
 * display registration and I/O operations performed for a specific 
 * Paramics FileReader or FileWriter.
 * 
 * @author Matthew Cechini
 * @version 
 */
@SuppressWarnings("serial")
public class ParamicsIOInfoPanel extends JPanel {
    
    /**
     * Constructor. Initialie the GUI elements for this panel.
     * 
     * @param update Initial FileRegUpdate object.
     * @param tableModel TableModel to be displayed on panel.
     */
    public ParamicsIOInfoPanel(FileRegUpdate update, FileIOTableModel tableModel) {
        
        Box infoBox = new Box(BoxLayout.Y_AXIS);
        infoBox.setAlignmentY(Box.CENTER_ALIGNMENT);
        infoBox.add(Box.createVerticalGlue());

        idLbl = new JLabel("ID:");
        idLbl.setAlignmentX(Box.LEFT_ALIGNMENT);
        idTF  = new JTextField(update.ioID);
        idTF.setAlignmentX(Box.LEFT_ALIGNMENT);
        idTF.setMinimumSize(new Dimension(150, 25));
        idTF.setPreferredSize(new Dimension(150, 25));
        idTF.setMaximumSize(new Dimension(200, 25));
        idTF.setEditable(false);
        infoBox.add(idLbl);
        infoBox.add(idTF);
        infoBox.add(Box.createVerticalStrut(5));
        
        targetFileLbl = new JLabel("Target File:");
        targetFileLbl.setAlignmentX(Box.LEFT_ALIGNMENT);
        targetFileTF  = new JTextField(update.targetFile);
        targetFileTF.setAlignmentX(Box.LEFT_ALIGNMENT);
        targetFileTF.setMinimumSize(new Dimension(150, 25));
        targetFileTF.setPreferredSize(new Dimension(150, 25));
        targetFileTF.setMaximumSize(new Dimension(200, 25));
        targetFileTF.setEditable(false);
        infoBox.add(targetFileLbl);
        infoBox.add(targetFileTF);
        
        if(update.ioType == IO_TYPE.READ) {
            intervalLbl = new JLabel("Interval: ");
            intervalLbl.setAlignmentX(Box.LEFT_ALIGNMENT);
            intervalTF  = new JTextField(String.valueOf(update.ioInterval));
            intervalTF.setAlignmentX(Box.LEFT_ALIGNMENT);
            intervalTF.setMinimumSize(new Dimension(150, 25));
            intervalTF.setPreferredSize(new Dimension(150, 25));
            intervalTF.setMaximumSize(new Dimension(200, 25));
            intervalTF.setEditable(false);

            infoBox.add(Box.createVerticalStrut(5));
            infoBox.add(intervalLbl);
            infoBox.add(intervalTF);
        }
        infoBox.add(Box.createVerticalGlue());
        infoBox.setMinimumSize(new Dimension(150, 200));
        infoBox.setPreferredSize(new Dimension(150, 200));
        infoBox.setMaximumSize(new Dimension(200, 200));
        infoBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createRaisedBevelBorder(), "Information"),
                    BorderFactory.createEmptyBorder(5,5,5,5)));
        
        ioUpdateTable = new JTable(tableModel);     
        ioUpdateTable.getTableHeader().setReorderingAllowed(false);  
        ioUpdatePane  = new JScrollPane();
        ioUpdatePane.setMinimumSize(new Dimension(200, 400));
        ioUpdatePane.setPreferredSize(new Dimension(200, 400));
        ioUpdatePane.setMaximumSize(new Dimension(200, 400));
        ioUpdatePane.setViewportView(ioUpdateTable);
        ioUpdatePane.setAlignmentY(Box.CENTER_ALIGNMENT);
        
        Box panelBox = new Box(BoxLayout.X_AXIS);
        panelBox.add(ioUpdatePane);
        panelBox.add(Box.createHorizontalStrut(20));
        panelBox.add(infoBox);
        panelBox.add(Box.createHorizontalGlue());
        
        add(panelBox);
    }
    
    
    public IO_TYPE  ioType; 
    public REG_TYPE regType;
    public String   ioID;
    public String   targetFile;
    public Integer  ioInterval;
    
    private JLabel idLbl;
    private JLabel targetFileLbl;
    private JLabel intervalLbl;
    
    private JTextField idTF;
    private JTextField targetFileTF;
    private JTextField intervalTF;
    
    private JTable ioUpdateTable;
    private JScrollPane ioUpdatePane;
}
