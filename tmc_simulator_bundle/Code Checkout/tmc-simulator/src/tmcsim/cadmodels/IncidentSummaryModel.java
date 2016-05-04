package tmcsim.cadmodels;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tmcsim.common.ScriptException;
import tmcsim.common.CADEnums.CADScreenNum;
import tmcsim.common.CADEnums.CADScreenType;
import tmcsim.common.CADProtocol.CAD_COMMANDS;

/**
 * IncidentSummaryModel is a CADScreenModel object containing data that is 
 * displayed in the IncidentSummary CAD Screen.  The addModelObject() method 
 * is used to update the model data with new information.<br/>
 * <br/>
 * This element parses and creates the following XML schema in its toXML() and
 * fromXML() methods.  The ROOT element is the parameter for those methods.  
 * See the class description for the CADScreenModel and 
 * IncidentSummaryModel_obj Objects for their XML schema.<br/>
 * <ROOT>
 *    <INCIDENT_SUMMARY>
 *       <BASE_MODEL_INFO/>
 *       <INCIDENT/>
 *       ...
 *       <INCIDENT/>
 *    </INCIDENT_SUMMARY>
 * </ROOT>
 * 
 * @see IncidentSummaryModel_obj
 * @see CADScreenModel
 * @author Matthew Cechini
 * @version
 */
public class IncidentSummaryModel extends CADScreenModel {
    
    /**
     * Enumeration with XML tag names.
     * @author Matthew Cechini
     */
    private static enum XML_TAGS {  
        /** Incident information. */
        INCIDENT  ("INCIDENT");

        public String tag;
        
        private XML_TAGS(String t) {
            tag = t;
        }
    }
    
    /** String containing an string of areas, used in the CAD Client IncidentSummary screen. */
    public static final String areas            = "3  5  9  14  18  29  51 "; //do we read this in?    
    
    /** Model data. List of IncidentsSummaryModel_obj objects. */
    private Vector<IncidentSummaryModel_obj> incidentsList;    
    
    /**
     * Constructor.
     *
     * @param num CADScreenNum for this model.
     */
    public IncidentSummaryModel(CADScreenNum num) {
        super(CADScreenType.SA_INCIDENT_SUMMARY, num);

        incidentsList = new Vector<IncidentSummaryModel_obj>(); 
    }
        
    /**
     * Constructor.  Create model data from parsed parameter XML node.
     *
     * @param newNode XML node containing model data.
     * @throws ScriptException if there is an error in parsing the Node.
     */
    public IncidentSummaryModel(Node newNode) throws ScriptException {
        super(CADScreenType.SA_INCIDENT_SUMMARY, CADScreenNum.ONE);
        
        incidentsList = new Vector<IncidentSummaryModel_obj>(); 
        
        fromXML(newNode);   
    }



    /**
     * Add a new model object to the list of incidents.
     *
     * @param ismo IncidentSummaryModel_obj
     * @throws ClassCastException if the parameter is not an 
     *         IncidentSummaryModel_obj object.
     */
    public void addModelObject(Object ismo) throws ClassCastException {
        
        if(ismo instanceof IncidentSummaryModel_obj) {
            incidentsList.add((IncidentSummaryModel_obj)ismo);
        }
        else {
            throw new ClassCastException();
        }       
    } 
    
    /**
     * Get the list of incidents in this model.
     *
     * @return Vector of IncidentSummaryModel_obj objects.
     */
    public Vector<IncidentSummaryModel_obj> getModelObjects() {
        return incidentsList;   
    }
    
    public void toXML(Element currElem) {
                        
        Document theDoc = currElem.getOwnerDocument();
                
        Element modelElem = theDoc.createElement(CAD_COMMANDS.INCIDENT_SUMMARY.fullName);
        
        baseToXML(modelElem);       
                
        Element ismoElem             = null;
        for(IncidentSummaryModel_obj ismo : incidentsList) {
            ismoElem = theDoc.createElement(XML_TAGS.INCIDENT.tag);

            ismo.toXML(ismoElem);
            
            modelElem.appendChild(ismoElem);
        }
        
        currElem.appendChild(modelElem);        
    }
   
    public void fromXML(Node modelNode) throws ScriptException {    
        
        incidentsList.clear();
        
        modelNode = modelNode.getFirstChild();
        
        baseFromXML(modelNode);
        
        modelNode = modelNode.getNextSibling();
        
        while(modelNode != null) {      
            incidentsList.add(new IncidentSummaryModel_obj(modelNode));
            
            modelNode = modelNode.getNextSibling();
        }
    }

}