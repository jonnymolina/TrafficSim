package tmcsim.cadsimulator.viewer;

import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import tmcsim.cadsimulator.videocontrol.DVDStatusUpdate;
import tmcsim.cadsimulator.videocontrol.DVDTitleUpdate;

/**
 * MediaStatusPanel is a GUI object used for displaying information for DVD connections
 * created by the CAD Simulator. Tabs for each DVD are created and information is
 * displayed on a DVDInfoPanel. All status and title updates are sent to the
 * corresponding DVDInfoPanel. The DVDs are referenced by connection info, which is
 * unique to each DVD player.
 *
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class MediaStatusPanel extends JPanel
{
    /**
     * Map of DVDInfoPanels(values) referenced by a DVD's connection info(key).
     */
    private TreeMap<String, DVDInfoPanel> dvdPanels = null;

    /**
     * Constructor. Initialize data and GUI components.
     */
    public MediaStatusPanel()
    {

        dvdPanels = new TreeMap<String, DVDInfoPanel>();

        initComponents();
    }

    /**
     * Updates the current DVDInfoPanel with the status update. If a panel does not
     * current exist, create one and add a new tab.
     *
     * @param update DVD status update.
     */
    public void updateDVDStatus(DVDStatusUpdate update)
    {
        // if no connection info
        if (dvdPanels.get(update.getConnectionInfo()) == null)
        {
            dvdPanels.put(update.getConnectionInfo(), new DVDInfoPanel(
                    update.getConnectionInfo()));

            mediaTabs.addTab(
                    "DVD " + (Integer.parseInt(update.getConnectionInfo()
                    .substring(update.getConnectionInfo()
                        .indexOf(":") + 1)) % 3000),
                    dvdPanels.get(update.getConnectionInfo()));
        }


        dvdPanels.get(update.getConnectionInfo()).updateDVDStatus(update);
    }

    /**
     * Updates the current DVDInfoPanel with the title update. If a panel does not
     * current exist, create one and add a new tab.
     *
     * @param update DVD title update.
     */
    public void updateDVDTitle(DVDTitleUpdate update)
    {
        // if no connection info
        if (dvdPanels.get(update.getConnectionInfo()) == null)
        {
            dvdPanels.put(update.getConnectionInfo(),
                    new DVDInfoPanel(update.getConnectionInfo()));

            mediaTabs.addTab("DVD " + dvdPanels.size(),
                    dvdPanels.get(update.getConnectionInfo()));
        }

        dvdPanels.get(update.getConnectionInfo()).updateDVDTitle(update);
    }

    /**
     * Initialize GUI components.
     */
    private void initComponents()
    {
        mediaTabs = new JTabbedPane();

        add(mediaTabs);
    }
    private JTabbedPane mediaTabs;
}
