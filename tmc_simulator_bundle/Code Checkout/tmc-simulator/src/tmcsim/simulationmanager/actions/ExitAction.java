package tmcsim.simulationmanager.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import tmcsim.simulationmanager.SimulationManagerView;
/**
 * ExitAction is an AbstractAction that is used for exiting the Simulation
 * Manager application.  When the action is performed, the action prompts the 
 * user to confirm the exit, and then disposes of the SimulationManagerView class. 
 * @author Matthew Cechini
 */
@SuppressWarnings("serial")
public class ExitAction extends AbstractAction {    
    
    /** Reference to the SimulationManagerView object. */
    private SimulationManagerView theSimManagerView = null;
    
    /** 
     * Constructor.
     * @param view View class object for the Simulation Manager.
     */
    public ExitAction(SimulationManagerView view) {
        super("Exit");
        
        theSimManagerView = view;       
    }
    

    public void actionPerformed(ActionEvent evt) {
        theSimManagerView.dispose();
//        if(JOptionPane.showConfirmDialog(
//                null, "Exit Simulation Manager?", "Confirm Exit",
//                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) 
//        {   
//                theSimManagerView.dispose();
//        }   
    }
}
