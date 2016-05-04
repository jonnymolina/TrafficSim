package tmcsim.simulationmanager.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.SAXParserFactory;

import tmcsim.client.cadclientgui.data.Incident;
import tmcsim.common.ScriptException;
import tmcsim.client.cadclientgui.ScriptHandler;
import tmcsim.common.SimulationException;
import tmcsim.common.CADEnums.SCRIPT_STATUS;
import tmcsim.simulationmanager.SimulationManagerView;
import tmcsim.simulationmanager.dialogs.AddIncidentDialog;


/**
 * AddIncidentAction is an AbstractAction that is used for adding an incident
 * into the current simulation.  When the action is performed, a file chooser
 * is shown and the user is prompted to select which file they want to load
 * script incidents from.  If the user selects a file, the script file is parsed
 * and available incidents are displayed in the AddIncidentDialog.  When the dialog
 * window is closed, the action checks for selected incidents.  If a selected incident
 * has a conflicting log number with a current incident, or if the scheduled time
 * is prior to the current simulation time, the dialog is reshown with an error.
 * Once the user has selected 0 or more incidents that can be added to the 
 * simulation, the SimulationManagerModel is called with the new incidents to add.
 * If incidents were added and the simulation has not been started, the 
 * ScriptStatus is set to SCRIPT_STOPPED_NOT_STARTED.
 */
@SuppressWarnings("serial")
public class AddIncidentAction extends AbstractAction {
    
    /** Reference to the SimulationManagerView object. */
    private SimulationManagerView theSimManagerView = null;
    
    /** AddIncidentDialog used for adding new incidents into the simulation. */
    private AddIncidentDialog theAddIncidentDialog; 
    
    /** 
     * Constructor.
     * @param view View class object for the Simulation Manager.
     */     
    public AddIncidentAction(SimulationManagerView view) {
        super("Add New Incident");
        
        theSimManagerView = view;

        theAddIncidentDialog  = new AddIncidentDialog(
                theSimManagerView);
    }

    public void actionPerformed(ActionEvent evt) {
        Runnable addRunnable = new Runnable(){      
            public void run() { 
                try{                
                    JFileChooser chooser   = new JFileChooser(
                            SimulationManagerView.SCRIPT_DIR);
                    
                    chooser.setDialogTitle("Open Simulation Script File");
                    chooser.setMultiSelectionEnabled(false);
            
                    if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        
                        File selectedFile = chooser.getSelectedFile();                              
                        ScriptHandler sh  = null;
                        
                        try {                   
                            sh = new ScriptHandler();
                            SAXParserFactory.newInstance().newSAXParser().parse(selectedFile, sh);
                            
                        } catch (Exception e) {
                            throw new SimulationException(SimulationException.INVALID_SCRIPT_FILE);
                        }
                        
                        boolean incidentsAdded     = false;
                        boolean incidentsValidated = true;
                        Vector<Incident> currIncidents   = theSimManagerView.getModel().getIncidentList();
                        Vector<Incident> parsedIncidents = sh.getIncidents();
                        Vector<Integer>  duplicateIncNum = new Vector<Integer>();
                        Vector<Integer>  invalidIncTime  = new Vector<Integer>();
                                                
                        //Show the dialog with the initialized list of incidents.
                        theAddIncidentDialog.setModelData(parsedIncidents);
                        
                        //Loop until the user selects a list(may be empty) of valid incidents.
                        do {                
                            incidentsValidated = true;
                            theAddIncidentDialog.showDialog();
                            
                            duplicateIncNum.clear();
                            invalidIncTime.clear();
                            
                            //Validate the selected incidents.  Validation fails if:
                            //  +  A selected incident's log number matches a log number already in the simulation.
                            //  +  A selected incident has been scheduled to occur at a time that has passed in the simulation.
                            for(Integer incidentNum : theAddIncidentDialog.getSelectedIncidentTimes().keySet()) {
                                
                                for(Incident inc : currIncidents) {
                                    if(inc.logNum.equals(incidentNum)) {
                                        duplicateIncNum.add(incidentNum);
                                        incidentsValidated = false;
                                    }
                                }
                                
                                long incSchedTime = theAddIncidentDialog.getSelectedIncidentTimes().get(incidentNum); 
                                if(incSchedTime < theSimManagerView.getCurrentSimTime()) 
                                {
                                    invalidIncTime.add(incidentNum);
                                    incidentsValidated = false;
                                }                               
                            }
                            
                            if(duplicateIncNum.size() > 0) {
                                JOptionPane.showMessageDialog(null, 
                                        "Duplicate incidents selected: " + 
                                        duplicateIncNum.toString().substring(1, 
                                                duplicateIncNum.toString().length()-1),
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            else if(invalidIncTime.size() > 0) {
                                JOptionPane.showMessageDialog(null,
                                        "Simulation time already passed for incidents: " + 
                                        invalidIncTime.toString().substring(1, 
                                                invalidIncTime.toString().length()-1),
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            
                        } while(!incidentsValidated);
                        
                
                        //Loop through all selected incidents, set the new start time, and add the Incident
                        //to simulation.
                        for(Integer incidentNum : theAddIncidentDialog.getSelectedIncidentTimes().keySet()) {
                            for(Incident inc : parsedIncidents) {
                                if(inc.logNum.equals(incidentNum)) {
                                    incidentsAdded = true;
                                    inc.setSecondsToStart(theAddIncidentDialog
                                            .getSelectedIncidentTimes().get(incidentNum));
                                    
                                    theSimManagerView.getModel().addIncident(inc);
                                }
                            }
                        }
                
                        
                        if(incidentsAdded && !theSimManagerView.isSimulationStarted()) 
                        {
                            theSimManagerView.setScriptStatus(
                                    SCRIPT_STATUS.SCRIPT_STOPPED_NOT_STARTED);
                        }
                    } 
                } 
                catch (SimulationException se) {
                    theSimManagerView.SimulationExceptionHandler(se);   
                }
            }
        };
        
        Thread theThread = new Thread(addRunnable);
        theThread.start();

    }

}
