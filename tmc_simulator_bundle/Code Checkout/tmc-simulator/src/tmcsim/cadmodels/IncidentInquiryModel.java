package tmcsim.cadmodels;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tmcsim.common.ScriptException;
import tmcsim.common.CADEnums.CADScreenNum;
import tmcsim.common.CADEnums.CADScreenType;
import tmcsim.common.CADProtocol.CAD_COMMANDS;


/**
 * IncidentInquiryModel is a CADScreenModel object containing data that is 
 * displayed in the IncidentInquiry CAD Screen.  The addModelObject() method 
 * is used to update the model data with new information.<br/>
 * <br/>
 * This element parses and creates the following XML schema in its toXML() and
 * fromXML() methods.  The ROOT element is the parameter for those methods.  
 * See the class description for the CADScreenModel and 
 * IncidentInquiryModel_obj Objects for their XML schema.<br/>
 * <ROOT>
 *    <INCIDENT_INQUIRY>
 *       <BASE_MODEL_INFO/>
 *       ... IncidentInquiryModel_obj ...
 *    </INCIDENT_INQUIRY>
 * </ROOT>
 * 
 * @see IncidentInquiryModel_obj
 * @see CADScreenModel
 * @author Matthew Cechini
 * @version
 */
public class IncidentInquiryModel extends CADScreenModel {

    /** Object containing model data. */
    private IncidentInquiryModel_obj theModelObj = null;    
    
    /**
     * Constructor. Initialize the model data and set the log number.
     * This constructor is used when creating an IncidentInquiry
     * model object where the source is the script.
     * 
     * @param num CADScreen number for this model object.
     * @param logNum Unique log number for this model object.
     */
    public IncidentInquiryModel(CADScreenNum num, Integer logNum) {
        super(CADScreenType.II_INCIDENT_INQUIRY, num);
        
        theModelObj = new IncidentInquiryModel_obj();
        theModelObj.setLogNumber(logNum);
    }
    
    /**
     * Constructor. Initialize the model data and set the log number.
     * This constructor is used when creating an IncidentInquiry
     * model object where the source is a CAD position.
     * 
     * @param CADPostion CAD Position number for where data originated.
     * @param num CADScreen number for this model object.
     * @param logNum Unique log number for this model object.
     */    
    public IncidentInquiryModel(int CADPosition, CADScreenNum num, Integer logNum) {
        super(CADScreenType.II_INCIDENT_INQUIRY, num);
        
        theModelObj = new IncidentInquiryModel_obj(CADPosition);
        theModelObj.setLogNumber(logNum);
    }    
    
    /**
     * Constructor. Initialize the model data from the parameter Node.
     * 
     * @param newNode Node containing model data.
     * @throws ScriptException if there is an error in parsing the Node.
     */    
    public IncidentInquiryModel(Node newNode) throws ScriptException {
        super(CADScreenType.II_INCIDENT_INQUIRY, CADScreenNum.ONE);
        
        fromXML(newNode);   
    }
    
    /**
     * Updates the private IncidentInquiryModel_obj object with the parameter.
     * 
     * @param iimo An IncidentInquiryModel_obj object to be added
     */
    public void addModelObject(Object iimo) {                       
        theModelObj.update((IncidentInquiryModel_obj)iimo);      
    }                   
    
    /** 
     * Returns the private IncidentInquiryModel object.
     * 
     * @return The model's data object.
     */
    public IncidentInquiryModel_obj getModelObject() {        
        return theModelObj; 
    } 
    
    /**
     * Sets the model object's log number.
     * 
     * @param newLogNumber The new log number.
     */
    public void setLogNumber(Integer newlogNumber) {
        theModelObj.setLogNumber(newlogNumber);
    }    
    
    /**
     * Gets the unique log number for this model's data.
     * 
     * @return Integer log number.
     */
    public Integer getLogNumber() {
        return theModelObj.getLogNumber();
    }    
    
    /**
     * Compares a parameter IncidentInquiry model object's log number against
     * this object's log number.
     * 
     * @param iimo Model object whose log number will be compared with
     *             this object's log number.
     * @return true if log numbers match, false if not.
     */
    public boolean logNumMatches(IncidentInquiryModel_obj iimo) {
        return iimo.getHeader().logNumber.equals(getLogNumber());
    }
        
    public void toXML(Element currElem) {
        
        Document theDoc = currElem.getOwnerDocument();
        
        Element modelElem = theDoc.createElement(CAD_COMMANDS.INCIDENT_INQUIRY.fullName);
        
        baseToXML(modelElem);
        
        theModelObj.toXML(modelElem);
                
        currElem.appendChild(modelElem);
    
    }    
    
    public void fromXML(Node modelNode) throws ScriptException {    
        
        theModelObj = new IncidentInquiryModel_obj();
        
        modelNode = modelNode.getFirstChild();
        
        baseFromXML(modelNode);     
        
        modelNode = modelNode.getNextSibling();
        
        theModelObj.fromXML(modelNode);
        
    }    
    

}