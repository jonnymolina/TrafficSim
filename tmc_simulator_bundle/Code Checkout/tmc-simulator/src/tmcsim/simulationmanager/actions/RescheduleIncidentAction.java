package tmcsim.simulationmanager.actions;

import java.awt.event.ActionEvent;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import tmcsim.common.ScriptException;
import tmcsim.common.SimulationException;
import tmcsim.simulationmanager.SimulationManagerView;
import tmcsim.simulationmanager.model.IncidentTimeSpinnerModel;
import tmcsim.simulationmanager.model.IncidentListTableModel.INCIDENT_LIST_COLUMNS;


/**
 * RescheduleIncidentAction is an AbstractAction that is used for rescheduling
 * an incident that has been loaded into the simulation, but not started. 
 * When the action is performed, the incident selected in the incident list 
 * table on the SimulationManagerView is chosen for triggering.  If the user has
 * not selected an incident to trigger, an information window is shown to prompt
 * the user to select an incident.  If an incident has been selected, the
 * action calls the SimulationManagerModel with the Incident Log Number and
 * the time parsed from the IncidentTimeModel object.
 * <br/>
 * <br/>
 * The INCIDENT_LIST_TABLE key should be assigned the IncidentListTable object
 * from the SimulationManagerView object.
 * <br/>
 * <br/>
 * The INCIDENT_TIME_MODEL key should be assigned the IncidentTimeModel object
 * from the SimulationManagerView object.
 * @author Matthew Cechini
 */     
@SuppressWarnings("serial")
public class RescheduleIncidentAction extends AbstractAction {
    
    /** 
     * This key string is used to reference the IncidentListTable object
     * from the SimulationManagerView object.
     */
    public static final String INCIDENT_LIST_TABLE = "INCIDENT_LIST_TABLE";
    
    /** 
     * This key string is used to reference the IncidentTimeModel object
     * from the SimulationManagerView object.
     */
    public static final String INCIDENT_TIME_MODEL = "INCIDENT_TIME_MODEL";
    
    /** Reference to the SimulationManagerView object. */
    private SimulationManagerView theSimManagerView = null;
    
    /**
     * Constructor.
     * @param view View class object for the Simulation Manager.
     */
    public RescheduleIncidentAction(SimulationManagerView view) {
        super("Reschedule");
        
        theSimManagerView = view;
    }
    
    public void actionPerformed(ActionEvent evt) {
        if(getValue(INCIDENT_LIST_TABLE) == null) {
            Logger.getLogger("tmcsim.simulationmanager.actions").logp(
                    Level.WARNING, "RescheduleIncidentAction", 
                    "actionPerformed", 
                    "Object for INCIDENT_LIST_TABLE is null.");
            return;
        }
        if(getValue(INCIDENT_TIME_MODEL) == null) {
            Logger.getLogger("tmcsim.simulationmanager.actions").logp(
                    Level.WARNING, "RescheduleIncidentAction", 
                    "actionPerformed", 
                    "Object for INCIDENT_TIME_MODEL is null.");
            return;
        }
        
        Runnable reschedRunnable = new Runnable(){      
            public void run() { 
                try {
                    if(((JTable)getValue(INCIDENT_LIST_TABLE)).getSelectedRowCount() == 0) 
                        throw new ScriptException("Please select the incident you wish to reschedule.");        

                    int selectedRow        = ((JTable)getValue(INCIDENT_LIST_TABLE)).getSelectedRow();
                    Integer incidentLogNum = (Integer)((JTable)getValue(INCIDENT_LIST_TABLE)).getValueAt(
                            selectedRow, INCIDENT_LIST_COLUMNS.LOG_NUM_COL.colNum);
                    

                    StringTokenizer strTok = new StringTokenizer(
                            (String)((IncidentTimeSpinnerModel)getValue(INCIDENT_TIME_MODEL)).getValue(), ":");
                    
                    Long newTime = Long.parseLong(strTok.nextToken()) * 3600  +
                                   Long.parseLong(strTok.nextToken()) * 60  +
                                   Long.parseLong(strTok.nextToken());
                    
                    theSimManagerView.getModel().rescheduleIncident(newTime, incidentLogNum);
                    
                    ((JTable)getValue(INCIDENT_LIST_TABLE)).setValueAt(newTime, selectedRow, 
                            INCIDENT_LIST_COLUMNS.SCHEDULED_COL.colNum);
                    
                }
                catch (ScriptException se) {
                    theSimManagerView.ScriptExceptionHandler(se);           
                }
                catch (SimulationException se) {
                    theSimManagerView.SimulationExceptionHandler(se);   
                }
            }
        };

        Thread theThread = new Thread(reschedRunnable);
        theThread.start();
    }

}
