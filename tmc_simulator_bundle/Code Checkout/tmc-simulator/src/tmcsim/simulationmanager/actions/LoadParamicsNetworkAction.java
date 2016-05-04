package tmcsim.simulationmanager.actions;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import tmcsim.common.ScriptException;
import tmcsim.common.SimulationException;
import tmcsim.common.CADEnums.PARAMICS_STATUS;
import tmcsim.simulationmanager.SimulationManagerView;

/**
 * LoadParamicsNetworkAction is an AbstractAction that is used for loading 
 * a Paramics traffic network.  When the action is performed, the action gets
 * the network ID from the NetworkIDSpinner in the SimulationManagerView.  The 
 * action sets the paramics status to SENDING_NETWORK_ID and calls the
 * SimulationManagerModel to load the Paramics network.
 * <br/>
 * <br/>
 * The NETWORK_ID_SPINNER key should be assigned the NetworkIDSpinner object
 * from the SimulationManagerView object.
 * @author Matthew Cechini
 */
@SuppressWarnings("serial")
public class LoadParamicsNetworkAction extends AbstractAction {
    
    /** 
     * This key string is used to reference the NetworkIDSpinner object
     * from the SimulationManagerView object.
     */
    public static final String NETWORK_ID_SPINNER = "NETWORK_ID_SPINNER";
    
    /** Reference to the SimulationManagerView object. */
    private SimulationManagerView theSimManagerView = null;
    
    /** 
     * Constructor.
     * @param view View class object for the Simulation Manager.
     */ 
    public LoadParamicsNetworkAction(SimulationManagerView view) {
        super("Load Network");
        
        theSimManagerView = view;
    }

    public void actionPerformed(ActionEvent evt) {
        if(getValue(NETWORK_ID_SPINNER) == null) {
            Logger.getLogger("tmcsim.simulationmanager.actions").logp(
                    Level.WARNING, "LoadParamicsNetworkAction", 
                    "actionPerformed", 
                    "Object for NETWORK_ID_SPINNER is null.");
            return;
        }
        
        Runnable loadNetworkRunnable = new Runnable(){      
            public void run() { 
                try { 
                
                    int id = ((SpinnerNumberModel) ((JSpinner) getValue(NETWORK_ID_SPINNER))
                            .getModel()).getNumber().intValue();
                    
                    theSimManagerView.setParamicsStatus(
                            PARAMICS_STATUS.SENDING_NETWORK_ID);
                            
                    theSimManagerView.getModel().loadParamicsNetwork(id);
                     
                }
                catch (NumberFormatException nfe) {
                    theSimManagerView.ScriptExceptionHandler(
                            new ScriptException("Invalid Network ID"));
                }
                catch (ScriptException se) {
                    theSimManagerView.ScriptExceptionHandler(se);
                }
                catch (SimulationException se) {
                    theSimManagerView.SimulationExceptionHandler(se);   
                }           
            }
        };

        Thread theThread = new Thread(loadNetworkRunnable);
        theThread.start();
    }

}
