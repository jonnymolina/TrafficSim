package tmcsim.client.cadscreens;


import javax.swing.text.Document;

import tmcsim.cadmodels.IncidentBoardModel;
import tmcsim.cadmodels.IncidentBoardModel_obj;
import tmcsim.client.cadscreens.view.CADMainView;
import tmcsim.common.CADEnums.TEXT_STYLES;

/**
 * IB_IncidentBoard is the view component to the IncidentBoardModel
 * model object.  The screen is shown when the user requests to view the
 * Incident Board. When the class is instantiated with reference
 * to the model data and document, string and style pairs are added to the 
 * view document.  This class extends from the CADMainView object, which
 * contains the common methods and data needed for display and user
 *
 * @see IncidentBoardModel
 * @author Matthew Cechini 
 * @version $Revision: 1.4 $ $Date: 2009/04/17 16:27:45 $
 */
public class IB_IncidentBoard extends CADMainView {

    /**
     * Reference to the Model class for the Incident Board data.
     */
    private IncidentBoardModel theModel;
    
    /**
     * Constructor.  Initializes the screen with the appropriate formatted text.
     *
     * @param newModel The model data object.
     * @param viewdoc  The Document object used for displaying the model data.
     */
    public IB_IncidentBoard(IncidentBoardModel newModel, Document viewDoc) {
        super(viewDoc);
        
        theModel         = newModel;
            
        initialize();
    }   
    
    
    /**
     * This method initializes the screen's Document object with 
     * text and style pairs to create the correct screen format.
     */
    private void initialize() {
                        
        for(IncidentBoardModel_obj ibmo : theModel.getModelObjects()) {

            addDocElement("------------------ BULLETIN " 
                    + String.valueOf(ibmo.bulletinNum) 
                    + " "  + ibmo.date 
                    + "  " + ibmo.time
                    + "           ---------------------\n", 
                    TEXT_STYLES.CYAN);
                
            addDocElement(ibmo.message + "\n", 
                    TEXT_STYLES.CYAN);
        }

    }   
}