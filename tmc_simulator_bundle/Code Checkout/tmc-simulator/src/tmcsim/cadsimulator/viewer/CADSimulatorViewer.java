package tmcsim.cadsimulator.viewer;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Observable;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import tmcsim.cadsimulator.viewer.actions.ExitAction;
import tmcsim.cadsimulator.viewer.model.SimulationStatusPanelModel;
import tmcsim.interfaces.CADViewer;

/**
 * This class provides a GUI to view current status information for the CAD Simulator.
 *
 * @see SimulationStatusPanel
 * @see MediaStatusPanel
 * @author Jonathan Molina
 * @author Matthew Cechini
 * @version $Revision: 1.3 $ $Date: 2006/06/06 20:46:41 $
 */
@SuppressWarnings("serial")
public class CADSimulatorViewer extends JFrame implements CADViewer
{
    /**
     * Panel to display simulation information.
     */
    private SimulationStatusPanel simulationPanel;

    /**
     * Constructor.
     */
    public CADSimulatorViewer()
    {
        super("CAD Simulator");
        initComponents();
    }

    /**
     * Method calls the processEvent() method with a WINDOW_CLOSING WindowEvent to start
     * the application closing process.
     */
    public void closeViewer()
    {
        processEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Overloads the processEvent method. If the AWTEvent is a WINDOW_CLOSING event,
     * prompt the user to confirm the action. If confirmed, close the application.
     */
    protected void processEvent(AWTEvent evt)
    {
        if (evt.getID() == WindowEvent.WINDOW_CLOSING)
        {
            int option = JOptionPane.showConfirmDialog(null,
                    "Closing the CAD Simulator will stop the current "
                    + "simulation.  Do you wish to continue exiting?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION);

            if (option != JOptionPane.NO_OPTION)
            {
                System.exit(0);
            }
        }
    }

    /**
     * Initialize GUI Components
     */
    private void initComponents()
    {
        simulationPanel = new SimulationStatusPanel();

        cadSimTabbedPane = new JTabbedPane();
        cadSimTabbedPane.addTab("Status", simulationPanel);

        add(cadSimTabbedPane);

        menubar = new JMenuBar();

        fileMenu = new JMenu("File");
        menubar.add(fileMenu);

        exitMenuItem = new JMenuItem(new ExitAction(this));
        fileMenu.add(exitMenuItem);

        setJMenuBar(menubar);

        setPreferredSize(new Dimension(500, 575));
        pack();
        setResizable(false);
    }
    private JTabbedPane cadSimTabbedPane;
    private JMenuBar menubar;
    private JMenu fileMenu;
    private JMenuItem exitMenuItem;

    /**
     * Enables or disables this view. Adds the listener to close the program upon
     * exiting the window.
     * @param state true to enable, false to disable
     */
    @Override
    public void setVisible(boolean state)
    {
        super.setVisible(state);
        this.addWindowListener(new WindowListener()
        {
            public void windowClosed(WindowEvent e)
            {
            }

            public void windowOpened(WindowEvent e)
            {
            }

            public void windowIconified(WindowEvent e)
            {
            }

            public void windowDeiconified(WindowEvent e)
            {
            }

            public void windowActivated(WindowEvent e)
            {
            }

            public void windowDeactivated(WindowEvent e)
            {
            }

            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
    }

    /**
     * Updates this GUI view. Updates only if the obs parameter is of type
     * SimulationStatusPanelModel. This should be called after construction to add the
     * Media tab, represented by MediaStatusPanel.
     * @param obs updates if instanceof SimulationStatusPanelModel
     * @param obj adds the Media tab if instanceof MediaStatusPanel
     */
    @Override
    public void update(Observable obs, Object obj)
    {
        // should only be called once upon initialization
        if (obj instanceof MediaStatusPanel
                && cadSimTabbedPane.indexOfTab("Media") < 0)
        {
            cadSimTabbedPane.addTab("Media", (MediaStatusPanel) obj);
        }
        // updates the status panel tab
        else if (obs instanceof SimulationStatusPanelModel)
        {
            SimulationStatusPanelModel panelModel = (SimulationStatusPanelModel) obs;

            simulationPanel.setTerminalsConnected(panelModel.getNumClientsConnected());
            simulationPanel.setSimManagerStatus(panelModel.isSimManagerConnected());
            simulationPanel.setTime(panelModel.getTimeSegment());
            simulationPanel.setParamicsStatus(panelModel.getParamicsStatus());
            simulationPanel.setScriptStatus(panelModel.getScriptStatus());
            simulationPanel.setParamicsNetworkLoaded(panelModel.getNetworkLoaded());
        }
    }
}