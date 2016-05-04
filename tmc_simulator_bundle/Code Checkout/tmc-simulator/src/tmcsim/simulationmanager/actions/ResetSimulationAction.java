package tmcsim.simulationmanager.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import tmcsim.common.SimulationException;
import tmcsim.simulationmanager.SimulationManagerView;

/**
 * ResetSimulationAction is an AbstractAction that is used for resetting
 * the simulation. When the action is performed, the action calls the 
 * SimulationManagerModel to reset the simulation.
 * @author Matthew Cechini
 */ 
@SuppressWarnings("serial")
public class ResetSimulationAction extends AbstractAction {
    
    /** Reference to the SimulationManagerView object. */
    private SimulationManagerView theSimManagerView = null;
    
    /**
     * Constructor.
     * @param view View class object for the Simulation Manager.
     */
    public ResetSimulationAction(SimulationManagerView view) {
        super("Reset");
        
        theSimManagerView = view;
    }

    public void actionPerformed(ActionEvent evt) {
        Runnable resetRunnable = new Runnable(){        
            public void run() { 
                try {
                    theSimManagerView.getModel().resetSimulation();
                }
                catch (SimulationException se) {
                    theSimManagerView.SimulationExceptionHandler(se);   
                }
            }
        };

        Thread theThread = new Thread(resetRunnable);
        theThread.start();
    }

}
