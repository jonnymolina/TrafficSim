package tmcsim.simulationmanager.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import tmcsim.common.SimulationException;
import tmcsim.simulationmanager.SimulationManagerView;
import tmcsim.simulationmanager.dialogs.GotoTimeIndexDialog;

/**
 * GotoTimeIndexAction is an AbstractAction that is used for moving the 
 * simulation to a new time mark.  When the action is performed, the action 
 * shows the GotoTimeIndexDialog to prompt the user for a new time mark.
 * If the user chooses a new time, the action asks the user to confirm 
 * the Goto action and then calls the SimulationManagerModel to pause,
 * reset, and then goto the new simulation time.  This is is done
 * to ensure the current simulation is paused, to clear all current
 * incident info from the view, and then to reset the simulation time.
 * @author Matthew Cechini
 */
@SuppressWarnings("serial")
public class GotoTimeIndexAction extends AbstractAction {
    
    /** Reference to the SimulationManagerView object. */
    private SimulationManagerView theSimManagerView = null;
    
    /** 
     * Constructor.
     * @param view View class object for the Simulation Manager.
     */
    public GotoTimeIndexAction(SimulationManagerView view) {
        super("Goto");
        
        theSimManagerView = view;       
    }
    
    public void actionPerformed(ActionEvent evt) {
        Runnable gotoRunnable = new Runnable() {
            public void run() {

                try {                   
                    GotoTimeIndexDialog gotoDialog = 
                        new GotoTimeIndexDialog(null, theSimManagerView.getCurrentSimTime());
                    
                    if(gotoDialog.gotoApplied) {
                        
                        String gotoValue = gotoDialog.getGotoTime();
                        
                        if(JOptionPane.showConfirmDialog(null, 
                                "Do you wish to reposition the simulation time to " + gotoValue,
                                "Confirm Goto",
                                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) 
                        {       
                            theSimManagerView.getModel().pauseSimulation();
                            theSimManagerView.getModel().resetSimulation();                 
                            theSimManagerView.getModel().gotoSimulationTime(
                                    SimulationManagerView.stringTimeToLong(gotoValue));
                        }
                    }
                }
                catch (SimulationException se) {
                    theSimManagerView.SimulationExceptionHandler(se);
                }
            }
        };
        
        Thread theThread = new Thread(gotoRunnable);
        theThread.start();
    }

}
