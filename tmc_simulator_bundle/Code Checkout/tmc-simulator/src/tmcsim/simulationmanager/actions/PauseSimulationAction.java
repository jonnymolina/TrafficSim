package tmcsim.simulationmanager.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import tmcsim.common.SimulationException;
import tmcsim.simulationmanager.SimulationManagerView;

/**
 * PauseSimulationAction is an AbstractAction that is used for pausing
 * the simulation. When the action is performed, the action
 * calls the SimulationManagerModel to pause the simulation.
 */
@SuppressWarnings("serial")
public class PauseSimulationAction extends AbstractAction {
    
    /** Reference to the SimulationManagerView object. */
    private SimulationManagerView theSimManagerView = null;

    /** 
     * Constructor.
     * @param view View class object for the Simulation Manager.
     */
    public PauseSimulationAction(SimulationManagerView view) {
        super("Pause");
        
        theSimManagerView = view;
    }

    public void actionPerformed(ActionEvent evt) {
        Runnable pauseRunnable = new Runnable(){        
            public void run() { 
                try {
                    theSimManagerView.getModel().pauseSimulation();
                }
                catch(SimulationException se) {
                    theSimManagerView.SimulationExceptionHandler(se);
                }
            }
        };

        Thread theThread = new Thread(pauseRunnable);
        theThread.start();
    }

}
