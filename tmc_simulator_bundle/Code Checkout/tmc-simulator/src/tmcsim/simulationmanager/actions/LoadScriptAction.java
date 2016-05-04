package tmcsim.simulationmanager.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import tmcsim.common.ScriptException;
import tmcsim.common.SimulationException;
import tmcsim.simulationmanager.SimulationManagerView;
import tmcsim.simulationmanager.model.XmlFilter;

/**
 * LoadScriptAction is an AbstractAction that is used for loading a
 * simulation script.  When the action is performed, the action opens
 * a file chooser dialog.  If the user chooses a file, the action calls the 
 * SimulationMAnagerModel to load the script file.
 * @author Matthew Cechini
 */ 
@SuppressWarnings("serial")
public class LoadScriptAction extends AbstractAction {
    
    /** Reference to the SimulationManagerView object. */
    private SimulationManagerView theSimManagerView = null;
    
    /** 
     * Constructor.
     * @param view View class object for the Simulation Manager.
     */ 
    public LoadScriptAction(SimulationManagerView view) {
        super("Load Script");
        
        theSimManagerView = view;       
    }
    
    public void actionPerformed(ActionEvent evt) {
        SwingUtilities.invokeLater(new Runnable(){      
            public void run() { 
                JFileChooser chooser = new JFileChooser(
                        SimulationManagerView.SCRIPT_DIR);
                
                chooser.setDialogTitle("Open Simulation Script File");
                chooser.setMultiSelectionEnabled(false);        
                chooser.setFileFilter(new XmlFilter());
        
                int result = chooser.showOpenDialog(null);
                
                File selectedFile = chooser.getSelectedFile();
                
                if(result == JFileChooser.APPROVE_OPTION) {
                
                    try{ 
                        theSimManagerView.getModel().loadScript(selectedFile);
                    }
                    catch (ScriptException se) {
                        theSimManagerView.ScriptExceptionHandler(se);
                    }
                    catch (SimulationException se) {
                        theSimManagerView.SimulationExceptionHandler(se);   
                    }
                }
            }
        });
    }

}
