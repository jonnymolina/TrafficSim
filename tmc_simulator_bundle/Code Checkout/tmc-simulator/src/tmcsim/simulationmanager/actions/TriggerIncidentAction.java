package tmcsim.simulationmanager.actions;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import tmcsim.common.ScriptException;
import tmcsim.common.SimulationException;
import tmcsim.simulationmanager.SimulationManagerView;

/**
 * TriggerIncidentAction is an AbstractAction that is used for triggering
 * a simulation incident.  When the action is performed, the incident selected
 * in the incident list table on the Simulation Manager is chosen for triggering.
 * This is done by sending the incident's log number to the Simulation Manager
 * model.  If an incident is not selected in the table, a notification
 * window is shown to the user prompting for an incident to be selected.
 * <br/>
 * <br/>
 * The INCIDENT_LIST_TABLE key must be assigned the IncidentListTable object
 * from the SimulationManagerView object.
 */     
@SuppressWarnings("serial")
public class TriggerIncidentAction extends AbstractAction {

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
    public TriggerIncidentAction(SimulationManagerView view) {
        super("Trigger");
        
        theSimManagerView = view;
    }
    
    public void actionPerformed(ActionEvent evt) {
        if(getValue(INCIDENT_LIST_TABLE) == null) {
            Logger.getLogger("tmcsim.simulationmanager.actions").logp(
                    Level.WARNING, "TriggerIncidentAction", "actionPerformed", 
                    "Object for INCIDENT_LIST_TABLE is null.");
            return;
        }
        
        Runnable triggerRunnable = new Runnable(){      
            public void run() { 
                try {
                    if(((JTable)getValue(INCIDENT_LIST_TABLE)).getSelectedRowCount() == 0) 
                        throw new ScriptException("Please select the incident you wish to trigger.");           
                        
                    theSimManagerView.getModel().triggerIncident(
                            (Integer)((JTable)getValue(INCIDENT_LIST_TABLE)).getValueAt(
                                    ((JTable)getValue(INCIDENT_LIST_TABLE)).getSelectedRow(), 0));
                }
                catch (ScriptException se) {
                    theSimManagerView.ScriptExceptionHandler(se);           
                }
                catch (SimulationException se) {
                    theSimManagerView.SimulationExceptionHandler(se);   
                }
            }
        };

        Thread theThread = new Thread(triggerRunnable);
        theThread.start();
    }

}
