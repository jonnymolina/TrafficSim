package tmcsim.simulationmanager.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import tmcsim.common.ScriptException;
import tmcsim.common.SimulationException;
import tmcsim.simulationmanager.SimulationManagerView;

/**
 * StartSimulationAction is an AbstractAction that is used for starting
 * the simulation. When the action is performed, the action checks
 * whether the a connection has been made to Paramics.  If not, the user
 * is prompted whether they want to continue without a connection to Paramics.
 * If not, the action returns.  If there is a connection, or the user wishes to
 * continue without, the action calls the SimulationManagerModel to start the 
 * simulation.
 * @author Matthew Cechini
 */
@SuppressWarnings("serial")
public class StartSimulationAction extends AbstractAction {
    
    /** Reference to the SimulationManagerView object. */
    private SimulationManagerView theSimManagerView = null;
    
    /**
     * Constructor.
     * @param View class object for the Simulation Manager.
     */
    public StartSimulationAction(SimulationManagerView view) {
        super("Start");
        
        theSimManagerView = view;
    }
    
    public void actionPerformed(ActionEvent evt) {
        Runnable startRunnable = new Runnable(){        
            public void run() {             
                if(!theSimManagerView.isConnectedToParamics()) {
                    if (JOptionPane.showConfirmDialog(null, "Connection has not been " +
                            "made to the Paramics Traffic Modeler.  Do you wish " +
                            "to continue?", "Paramics Connection", 
                            JOptionPane.YES_NO_OPTION, 
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
                                return;
                            }
                }
                
                try { 
                    theSimManagerView.getModel().startSimulation();
                }
                catch (ScriptException se) {
                    theSimManagerView.ScriptExceptionHandler(se);
                }
                catch (SimulationException se) {
                    theSimManagerView.SimulationExceptionHandler(se);
                }
            }       
        };      

        Thread theThread = new Thread(startRunnable);
        theThread.start();
    }

}
