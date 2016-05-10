package tmcsim.cadsimulator.viewer;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import tmcsim.cadsimulator.videocontrol.DVDStatusUpdate;
import tmcsim.cadsimulator.videocontrol.DVDTitleUpdate;
import tmcsim.cadsimulator.viewer.model.DVDStatusTableModel;
import tmcsim.cadsimulator.viewer.model.DVDTitleTableModel;

/**
 * DVDInfoPanel is a GUI component used in the CADSimulatorViewer. The panel displays
 * information regarding the DVD player's connection information. One table on the panel
 * shows all DVD title that have been played or repeated. A second table shows all DVD
 * status updates that have been received from the controller.
 *
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class DVDInfoPanel extends JPanel
{
    /**
     * DVD player connection info.
     */
    private String connInfo = null;
    /**
     * Table model for the title table.
     */
    private DVDTitleTableModel titleTableModel;
    /**
     * Table to display DVD title plays and repeats.
     */
    private JTable titleTable;
    /**
     * Table model for the Status table.
     */
    private DVDStatusTableModel statusTableModel;
    /**
     * Table to display DVD status updates.
     */
    private JTable statusTable;

    /**
     * Constructor. Initialize the panel GUI components.
     *
     * @param connectionInfo DVD player connection info.
     */
    public DVDInfoPanel(String connectionInfo)
    {
        connInfo = connectionInfo;

        initComponents();
    }

    /**
     * This method updates the DVD status table with the new update object.
     *
     * @param update Update DVD Status update.
     */
    public void updateDVDStatus(DVDStatusUpdate update)
    {
        statusTableModel.addStatusUpdate(update);
    }

    /**
     * This method updates the DVD title table with the new update object.
     *
     * @param update Update DVD Status update.
     */
    public void updateDVDTitle(DVDTitleUpdate update)
    {
        titleTableModel.addTitleUpdate(update);
    }

    /**
     * Initialize the GUI components.
     */
    private void initComponents()
    {

        connInfoLbl = new JLabel("Connection Info:");
        connInfoLbl.setAlignmentX(Box.LEFT_ALIGNMENT);
        connInfoTF = new JTextField(connInfo);
        connInfoTF.setColumns(30);
        connInfoTF.setAlignmentX(Box.LEFT_ALIGNMENT);
        connInfoTF.setEditable(false);

        Box connInfoBox = Box.createVerticalBox();
        connInfoBox.add(connInfoLbl);
        connInfoBox.add(connInfoTF);
        connInfoBox.setAlignmentX(Box.CENTER_ALIGNMENT);

        titleTableModel = new DVDTitleTableModel();
        titleTable = new JTable(titleTableModel);
        titleTable.getTableHeader().setReorderingAllowed(false);

        // set up titles
        for (int c = 0; c < titleTable.getColumnCount(); c++)
        {
            titleTable.getColumnModel().getColumn(c).setMinWidth(
                    titleTableModel.getColumnMinWidth(c));
            titleTable.getColumnModel().getColumn(c).setMaxWidth(
                    titleTableModel.getColumnMaxWidth(c));
            titleTable.getColumnModel().getColumn(c).setPreferredWidth(
                    titleTableModel.getColumnPrefWidth(c));
            titleTable.getColumnModel().getColumn(c).setResizable(true);
        }

        titlePane = new JScrollPane();
        titlePane.setAlignmentX(Box.CENTER_ALIGNMENT);
        //titlePane.setMinimumSize(new Dimension(,));
        titlePane.setPreferredSize(new Dimension(425, 225));
        titlePane.setViewportView(titleTable);
        titlePane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createRaisedBevelBorder(), "Title Updates"));

        statusTableModel = new DVDStatusTableModel();
        statusTable = new JTable(statusTableModel);
        statusTable.getTableHeader().setReorderingAllowed(false);
        // set up statuses
        for (int c = 0; c < statusTable.getColumnCount(); c++)
        {
            statusTable.getColumnModel().getColumn(c).setMinWidth(
                    statusTableModel.getColumnMinWidth(c));
            statusTable.getColumnModel().getColumn(c).setMaxWidth(
                    statusTableModel.getColumnMaxWidth(c));
            statusTable.getColumnModel().getColumn(c).setPreferredWidth(
                    statusTableModel.getColumnPrefWidth(c));
            statusTable.getColumnModel().getColumn(c).setResizable(true);
        }

        statusPane = new JScrollPane();
        statusPane.setAlignmentX(Box.CENTER_ALIGNMENT);
        //statusPane.setMinimumSize(new Dimension(,));
        statusPane.setPreferredSize(new Dimension(425, 150));
        statusPane.setViewportView(statusTable);
        statusPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createRaisedBevelBorder(), "Status Updates"));

        Box panelBox = Box.createVerticalBox();
        panelBox.add(connInfoBox);
        panelBox.add(Box.createVerticalStrut(10));
        panelBox.add(titlePane);
        panelBox.add(Box.createVerticalStrut(10));
        panelBox.add(statusPane);
        panelBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(panelBox);
    }
    private JScrollPane titlePane;
    private JScrollPane statusPane;
    private JLabel connInfoLbl;
    private JTextField connInfoTF;
}
