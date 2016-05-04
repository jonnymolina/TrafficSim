package tmcsim.client.cadscreens;


import javax.swing.text.Document;

import tmcsim.cadmodels.IncidentSummaryModel;
import tmcsim.cadmodels.IncidentSummaryModel_obj;
import tmcsim.client.cadscreens.view.CADMainView;
import tmcsim.common.CADEnums.TEXT_STYLES;


/**
 * SA_IncidentSummary is the view component to the IncidentSummaryModel
 * model object.  The screen is shown when the user requests a "Summary
 * of Incidents by Area."  When the class is instantiated with reference
 * to the model data and document, string and style pairs are added to the 
 * view document.  This class extends from the CADMainView object, which
 * contains the common methods and data needed for display and user
 * interaction.  
 *
 * @see IncidentSummaryModel
 * @author Matthew Cechini 
 * @version $Revision: 1.4 $ $Date: 2009/04/17 16:27:45 $
 */
public class SA_IncidentSummary extends CADMainView {
    
    /**
     * Reference to the Model class for the Incident Summary data.
     */
    private IncidentSummaryModel theModel;
    
    
    /**
     * Constructor. Initializes the screen with the appropriate formatted text.
     *
     * @param newModel The model data object.
     * @param viewdoc  The Document object used for displaying the model data.
     */
    public SA_IncidentSummary(IncidentSummaryModel newModel, Document viewDoc) {
        super(viewDoc);                    
                    
        theModel = newModel;
        
        initialize();
    }   
    
    
    /**
     * This method initializes the screen's Document object with 
     * text and style pairs to create the correct screen format.
     */
    private void initialize() {     
    
        addDocElement("AREA(S)   ", TEXT_STYLES.CYAN);
        
        addDocElement(IncidentSummaryModel.areas, TEXT_STYLES.RED);
        
        addDocElement("- INCIDENT SUMMARY ------------ STATE: ", TEXT_STYLES.CYAN);
        
        addDocElement("I P A ", TEXT_STYLES.RED);
        
        addDocElement("F\n", TEXT_STYLES.CYAN);
        
                                        
        for(IncidentSummaryModel_obj ismo : theModel.getModelObjects()) {       
                
            addDocElement(lPad(rPad(ismo.logNumber.toString(), 4), 6) 
                               + rPad(ismo.date, 4) + "-"
                               + rPad(ismo.time, 4) 
                               + rPad(ismo.priority, 3) 
                               + rPad(ismo.callType, 5) 
                               + rPad(ismo.beatArea, 6) 
                               + rPad(ismo.location, 35) 
                               + rPad(ismo.beatAssigned, 5) + "\n",
                               TEXT_STYLES.YELLOW);
        }
            
    }   
}