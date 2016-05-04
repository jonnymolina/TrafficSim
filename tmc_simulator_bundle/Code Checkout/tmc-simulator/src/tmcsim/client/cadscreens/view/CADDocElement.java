package tmcsim.client.cadscreens.view;

import tmcsim.common.CADEnums.TEXT_STYLES;

/**
 * CADDocElement is a container class used in creation of document elements 
 * for display in CADMainView Screens.  The CADDocElement contains a text 
 * and style pair.  The style TEXT_STYLES enumeration value is used to 
 * reference a specific style that has been initialized in the StylePane 
 * that will be used for displaying the text.
 * 
 * @author Matthew Cechini
 * @version
 */
public class CADDocElement {

    /** Document element text. */
    public String text;

    /** Document element style. */
    public TEXT_STYLES style;
    
    /**
     * Constructor. 
     * 
     * @param t New text String.
     * @param s New style.
     */
    public CADDocElement(String t, TEXT_STYLES s) {
        text  = t;
        style = s;
    }
    
}
