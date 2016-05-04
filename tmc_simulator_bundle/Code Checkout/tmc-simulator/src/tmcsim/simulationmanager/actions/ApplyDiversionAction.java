package tmcsim.simulationmanager.actions;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;

import tmcsim.cadmodels.CMSDiversion;
import tmcsim.common.SimulationException;
import tmcsim.simulationmanager.SimulationManagerView;
import tmcsim.simulationmanager.dialogs.CMSDiversionDialog;


/**
 * ApplyDiversionAction is an AbstractAction that is used for settings traffic
 * diversions in the simulation.  When the action is performed, the action gets 
 * the selected CMS ID from the CMSIDComboBox in the SimulationManagerView.
 * The action then calls the SimulationManagerModel to get the current CMSInfo
 * for this id.  The CMSDiversionDialog is then shown with this CMSInfo.  If
 * the user updates the diversions at this CMS, the new info is sent to the
 * SimulationManagerModel to update the current diversions.
 * <br/>
 * <br/>
 * The CMS_ID_COMBO_BOX key should be assigned the CMSID JComboBox object
 * from the SimulationManagerView object.
 */
@SuppressWarnings("serial")
public class ApplyDiversionAction extends AbstractAction {
    
    /** 
     * This key string is used to reference the CMSID JComboBox object
     * from the SimulationManagerView object.
     */
    public static final String CMS_ID_COMBO_BOX = "CMS_ID_COMBO_BOX";
    
    /** Reference to the SimulationManagerView object. */
    private SimulationManagerView theSimManagerView = null;

    /** CMSDiversionDialog used for updating diversions in the simulation. */
    private CMSDiversionDialog theCMSDiversionDialog;
    
    /** 
     * Constructor.
     * @param view View class object for the Simulation Manager.
     */         
    public ApplyDiversionAction(SimulationManagerView view) {
        super("Divert Traffic");
        
        theSimManagerView     = view;
        theCMSDiversionDialog = new CMSDiversionDialog(theSimManagerView);
    }

    public void actionPerformed(ActionEvent evt) {
        if(getValue(CMS_ID_COMBO_BOX) == null) {
            Logger.getLogger("tmcsim.simulationmanager.actions").logp(
                    Level.WARNING, "ApplyDiversionAction", 
                    "actionPerformed", 
                    "Object for CMS_ID_COMBO_BOX is null.");
            return;
        }
        

        showDiversionDialog((String)((JComboBox)getValue(CMS_ID_COMBO_BOX)).getSelectedItem());
    }

    public void showDiversionDialog(final String cms_id) {

        Runnable divertRunnable = new Runnable(){       
            public void run() { 
    
                try{
                    
                    theCMSDiversionDialog.showDialog(
                            theSimManagerView.getModel().getCMSDiversionInfo(cms_id));
                    
                    long currentSimTime = theSimManagerView.getCurrentSimTime(); 

                    if(theCMSDiversionDialog.theCMSInfo.isUpdated()) {  

                        for(CMSDiversion div : theCMSDiversionDialog.theCMSInfo.possibleDiversions) {
                            if(div.isUpdated()) {
                                div.timeApplied = currentSimTime;
                                
                                theSimManagerView.removeDiversion(theCMSDiversionDialog.theCMSInfo, div);
                        
                                if(!div.isCleared()) {
                                    theSimManagerView.addDiversion(theCMSDiversionDialog.theCMSInfo, div);
                                }
                            }
                        }
                        
                        theSimManagerView.getModel().applyDiversions(theCMSDiversionDialog.theCMSInfo);
                    }   
                    
                }
                catch (SimulationException se) {
                    theSimManagerView.SimulationExceptionHandler(se);   
                }
            }
        };

        Thread theThread = new Thread(divertRunnable);
        theThread.start();
        
    }
    
}
