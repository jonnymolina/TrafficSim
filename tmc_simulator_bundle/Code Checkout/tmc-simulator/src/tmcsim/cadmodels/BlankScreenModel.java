package tmcsim.cadmodels;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tmcsim.common.ScriptException;
import tmcsim.common.CADEnums.CADScreenNum;
import tmcsim.common.CADEnums.CADScreenType;
import tmcsim.common.CADProtocol.CAD_COMMANDS;


/**
 * BlankScreenModel is a CADScreenModel object used to display a blank CAD Screen.  
 * <br/>
 * This element parses and creates the following XML schema in its toXML() and
 * fromXML() methods.  The ROOT element is the parameter for those methods.  
 * See the class description for the CADScreenModel XML schema.<br/>
 * <ROOT>
 *    <BLANK_SCREEN>
 *       <BASE_MODEL_INFO/>
 *    </BLANK_SCREEN>
 * </ROOT>
 * 
 * @see CADScreenModel
 * @author Matthew Cechini
 * @version
 */
public class BlankScreenModel extends CADScreenModel {
    
    
    /**
     * Constructor.  Initialize base class with screen model type and
     * current cad screen number.
     * 
     * @param num Current CADScreen number.
     */
    public BlankScreenModel(CADScreenNum num) {
        super(CADScreenType.BLANKSCREEN, num);
        
    }
    
    /**
     * Constructor.  Initialize base class with screen model type and
     * current cad screen number.
     * 
     * @param newNode Node containing XML data for this model object.
     * @throws ScriptException if there is an error in parsing the Node.
     */    
    public BlankScreenModel(Node newNode) throws ScriptException {
        super(CADScreenType.BLANKSCREEN, CADScreenNum.ONE);
        
        fromXML(newNode);   
    }
        
    public void addModelObject(Object o) {}
  
    public void toXML(Element currElem) {
        
        Document theDoc = currElem.getOwnerDocument();

        Element modelElem = theDoc.createElement(CAD_COMMANDS.BLANK_SCREEN.fullName);
        
        baseToXML(modelElem);   

        currElem.appendChild(modelElem);
    }
    
    public void fromXML(Node modelNode) throws ScriptException {    
        
        modelNode = modelNode.getFirstChild();
        baseFromXML(modelNode);
        
    }  
    
}
