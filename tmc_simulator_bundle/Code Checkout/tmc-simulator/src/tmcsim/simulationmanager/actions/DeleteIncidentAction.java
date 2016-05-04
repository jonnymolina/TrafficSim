package tmcsim.simulationmanager.actions;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import tmcsim.common.ScriptException;
import tmcsim.common.SimulationException;
import tmcsim.simulationmanager.SimulationManagerView;
import tmcsim.simulationmanager.model.IncidentListTableModel.INCIDENT_LIST_COLUMNS;


/**
 * DeleteIncidentAction is an AbstractAction that is used for removing 
 * an incident from the simulation that has not been started.  When the action 
 * is performed, the incident selected in the incident list table on the 
 * SimulationManagerView is chosen for triggering.  If the user has not 
 * selected an incident to trigger, an information window is shown to prompt
 * the user to select an incident.  If an incident has been selected, the 
 * action calls the SimulationManagerModel with the Incident Log Number to remove 
 * the incident.
 * <br/>
 * <br/>
 * The NETWORK_ID_SPINNER key should be assigned the IncidentListTable object
 * from the SimulationManagerView object.
 */     
@SuppressWarnings("serial")
public class DeleteIncidentAction extends AbstractAction {

    /** 
     * This key string is used to reference the IncidentListTable object
     * from the SimulationManagerView object.
     */
    public static final String INCIDENT_LIST_TABLE = "INCIDENT_LIST_TABLE";
    
    /** Reference to the SimulationManagerView object. */
    private SimulationManagerView theSimManagerView = null;
        
    /** 
     * Constructor.
     * @param view View class object for the Simulation Manager.
     */     
    public DeleteIncidentAction(SimulationManagerView view) {
        super("Delete");
        
        theSimManagerView = view;
    }
    
    
    public void actionPerformed(ActionEvent evt) {
        if(getValue(INCIDENT_LIST_TABLE) == null) {
            Logger.getLogger("tmcsim.simulationmanager.actions").logp(
                    Level.WARNING, "LoadParamicsNetworkAction", 
                    "actionPerformed", 
                    "Object for INCIDENT_LIST_TABLE is null.");
            return;
        }
        
        Runnable deleteRunnable = new Runnable(){       
            public void run() { 
                try {                   
                    if(((JTable)getValue(INCIDENT_LIST_TABLE)).getSelectedRowCount() == 0)  {
                        throw new ScriptException("Please select the incident you wish to delete.");
                    }
                        
                    int selectedRow        = ((JTable)getValue(INCIDENT_LIST_TABLE)).getSelectedRow(); 
                    Integer incidentLogNum = (Integer)((JTable)getValue(INCIDENT_LIST_TABLE)).getValueAt(
                            selectedRow, INCIDENT_LIST_COLUMNS.LOG_NUM_COL.colNum);
                    
                    theSimManagerView.getModel().deleteIncident(incidentLogNum);                        
                                        
                }
                catch (ScriptException se) {
                    theSimManagerView.ScriptExceptionHandler(se);           
                }
                catch (SimulationException se) {
                    theSimManagerView.SimulationExceptionHandler(se);   
                }   
            }
        };

        Thread theThread = new Thread(deleteRunnable);
        theThread.start();
    }

}
