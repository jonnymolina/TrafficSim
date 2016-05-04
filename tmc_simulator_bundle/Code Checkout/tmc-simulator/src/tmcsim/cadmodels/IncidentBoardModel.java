package tmcsim.cadmodels;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tmcsim.common.ScriptException;
import tmcsim.common.CADEnums.CADScreenNum;
import tmcsim.common.CADEnums.CADScreenType;
import tmcsim.common.CADProtocol.CAD_COMMANDS;


/**
 * IncidentBoardModel is a CADScreenModel object containing data that is 
 * displayed in the IncidentBoard CAD Screen.  The addModelObject() method 
 * is used to update the model data with new information.<br/>
 * <br/>
 * This element parses and creates the following XML schema in its toXML() and
 * fromXML() methods.  The ROOT element is the parameter for those methods.  
 * See the class description for the CADScreenModel and 
 * IncidentBoardModel_obj Objects for their XML schema.<br/>
 * <ROOT>
 *    <INCIDENT_BOARD>
 *       <BASE_MODEL_INFO/>
 *       <MESSAGE/>
 *       ...
 *       <MESSAGE/>
 *    </INCIDENT_BOARD>
 * </ROOT>
 * 
 * @see IncidentBoardModel_obj
 * @see CADScreenModel
 * @author Matthew Cechini
 * @version
 */
public class IncidentBoardModel extends CADScreenModel {
    
    /**
     * Enumeration with XML tag names.
     * @author Matthew Cechini
     */
    private static enum XML_TAGS {
        MESSAGE ("MESSAGE");
        
        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }
    }
    
    /** Model data. List of IncidentBoardModel_obj objects. */
    private Vector<IncidentBoardModel_obj> messageList = null;
    
    /**
     * Constructor.
     *
     * @param num CADScreenNum for this model.
     */
    public IncidentBoardModel(CADScreenNum num) {
        super(CADScreenType.IB_INCIDENT_BOARD, num);
        
        messageList  = new Vector<IncidentBoardModel_obj>();
    }
    
    /**
     * Constructor.  Create model data from parsed parameter XML node.
     *
     * @param newNode XML node containing model data.
     * @throws ScriptException if there is an error in parsing the Node.
     */
    public IncidentBoardModel(Node newNode) throws ScriptException {
        super(CADScreenType.IB_INCIDENT_BOARD, CADScreenNum.ONE);
        
        messageList  = new Vector<IncidentBoardModel_obj>();
                
        fromXML(newNode);   
    }
        
    /**
     * Add a new model object to the list of messages.
     *
     * @param ismo IncidentBoardModel_obj
     * @throws ClassCastException if the parameter is not an 
     *         IncidentBoardModel_obj object.
     */
    public void addModelObject(Object ibmo) throws ClassCastException {
        
        if(ibmo instanceof IncidentBoardModel_obj)  
            messageList.add((IncidentBoardModel_obj)ibmo);  
        else 
            throw new ClassCastException();
    } 
    
    /**
     * Get the list of messages in this model.
     *
     * @return Vector of IncidentBoardModel_obj objects.
     */
    public Vector<IncidentBoardModel_obj> getModelObjects() {
        return messageList; 
    }   
    
    public void toXML(Element currElem) {
        
        Document theDoc = currElem.getOwnerDocument();
                
        Element modelElem = theDoc.createElement(CAD_COMMANDS.INCIDENT_BOARD.fullName);
        
        baseToXML(modelElem);               
        
        Element ibmoElem             = null;
        for(IncidentBoardModel_obj ibmo : messageList) {
            ibmoElem = theDoc.createElement(XML_TAGS.MESSAGE.tag);

            ibmo.toXML(ibmoElem);
            
            modelElem.appendChild(ibmoElem);
        }
        
        currElem.appendChild(modelElem);

    }

    public void fromXML(Node modelNode) throws ScriptException {    
        
        messageList.clear();
        
        modelNode = modelNode.getFirstChild();
        
        baseFromXML(modelNode);
        
        NodeList messageNodes = ((Element)modelNode).getElementsByTagName(XML_TAGS.MESSAGE.tag);
        
        for(int i = 0; i < messageNodes.getLength(); i++)
            messageList.add(new IncidentBoardModel_obj(messageNodes.item(i)));
        
        
    }  
}
