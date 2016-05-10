package tmcsim.cadsimulator.viewer.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import tmcsim.cadsimulator.viewer.CADSimulatorViewer;

/**
 * Abstract action to exit the CADSimulator. When the action is performed the
 * CADSimulatorView.closeViewer() method is called.
 *
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class ExitAction extends AbstractAction
{
    /**
     * Reference to the CADSimulatorViewer.
     */
    private CADSimulatorViewer theViewer;

    /**
     * Constructs an instance of this action class.
     * @param viewer the referred view
     */
    public ExitAction(CADSimulatorViewer viewer)
    {
        super("Exit");

        theViewer = viewer;
    }

    /**
     * Perform the closing action.
     * @param arg0 the action event
     */
    public void actionPerformed(ActionEvent arg0)
    {
        theViewer.closeViewer();
    }
}
