package tmcsim.client.cadscreens;


import javax.swing.text.Document;

import tmcsim.cadmodels.CADRoutedMessage;
import tmcsim.cadmodels.RoutedMessageModel;
import tmcsim.client.cadscreens.view.CADMainView;
import tmcsim.common.CADEnums.TEXT_STYLES;

/**
 * TO_RoutedMessage is the view component to the RoutedMessageModel
 * model object.  The screen is shown when the user views a queued message.
 * When the class is instantiated with reference to the model data and 
 * document, string and style pairs are added to the view document.  
 * This class extends from the CADMainView object, which
 * contains the common methods and data needed for display and user
 * interaction.  
 *
 * @see RoutedMessageModel
 * @author Matthew Cechini 
 * @version $Revision: 1.4 $ $Date: 2009/04/17 16:27:45 $
 */
public class TO_RoutedMessage extends CADMainView {

    /** Instance of the model, containing the message data for this view class. */
    private RoutedMessageModel theModel;
    
    /**
     * Constructor. Initializes the screen with the appropriate formatted text.
     *
     * @param newModel The model data object.
     * @param viewdoc  The Document object used for displaying the model data.
     */
    public TO_RoutedMessage(RoutedMessageModel newModel, Document viewDoc) {
        super(viewDoc);
        
        theModel    = newModel;     
        
        initialize();
    }   
    
    
    /**
     * This method initializes the screen's Document object with 
     * text and style pairs to create the correct screen format.
     */
    private void initialize() { 
        
        CADRoutedMessage displayMessage = theModel.getCurrentMessage();
        
        addDocElement("FROM POS " +  displayMessage.fromPosition
                + "     MESSAGE      " + "TO POS " 
                + displayMessage.toPosition + "\n", TEXT_STYLES.CYAN);
                        

        addDocElement(displayMessage.message + "\n",
                TEXT_STYLES.CYAN);
        
    }
}