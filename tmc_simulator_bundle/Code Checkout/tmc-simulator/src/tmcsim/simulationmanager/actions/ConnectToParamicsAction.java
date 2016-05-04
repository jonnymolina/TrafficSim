package tmcsim.simulationmanager.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import tmcsim.common.SimulationException;
import tmcsim.common.CADEnums.PARAMICS_STATUS;
import tmcsim.simulationmanager.SimulationManagerView;

/**
 * ConnectToParamicsAction is an AbstractAction that is used for connecting
 * and disconnecting to/from the Paramics traffic modeler.  When the action 
 * is performed, the action determines whether a connection currently exists.
 * If no connection has been made, the paramics status is set to CONNECTING and
 * the SimulationManagerModel is called to connect to the Paramics communicator.
 * If a connection has been made, the action prompts the user to confirm the
 * disconnection and then calls the SimulationManagerModel to disconnect from
 * Paramics.
 * @author Matthew Cechini
 */
@SuppressWarnings("serial")
public class ConnectToParamicsAction extends AbstractAction {

    /** Reference to the SimulationManagerView object. */
    private SimulationManagerView theSimManagerView = null;
    
    /** 
     * Constructor.
     * @param view View class object for the Simulation Manager.
     */     
    public ConnectToParamicsAction(SimulationManagerView view) {
        super("Connect to Paramics");
        
        theSimManagerView = view;
    }
    
    public void actionPerformed(ActionEvent evt) {
        Runnable connectRunnable = new Runnable(){      
            public void run() { 
                if(!theSimManagerView.isConnectedToParamics()) {
                    try{ 
                        theSimManagerView.setParamicsStatus(PARAMICS_STATUS.CONNECTING);
                        
                        theSimManagerView.getModel().connectToParamics();               
                    }
                    catch (SimulationException se) {
                        theSimManagerView.SimulationExceptionHandler(se);   
                    }
                }
                else {   
                    if(JOptionPane.showConfirmDialog(null, 
                            "Disconnecting from paramics will require \n" +
                            "  restarting the Paramics Communicator.  \n" +
                            "           Do you wish to continue?") == JOptionPane.YES_OPTION) {
                        
                        try { 
                            theSimManagerView.getModel().disconnectFromParamics();                  
                        }
                        catch (SimulationException se) {
                            theSimManagerView.SimulationExceptionHandler(se);   
                        }                   
                    }
                }           
            }
        };

        Thread theThread = new Thread(connectRunnable);
        theThread.start();
    }

}
