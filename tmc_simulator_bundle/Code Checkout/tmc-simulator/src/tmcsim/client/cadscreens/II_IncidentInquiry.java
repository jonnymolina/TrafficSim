package tmcsim.client.cadscreens;

import javax.swing.text.Document;

import tmcsim.cadmodels.IncidentInquiryDetails;
import tmcsim.cadmodels.IncidentInquiryModel;
import tmcsim.cadmodels.IncidentInquiryModel_obj;
import tmcsim.cadmodels.IncidentInquiryServices;
import tmcsim.cadmodels.IncidentInquiryTows;
import tmcsim.cadmodels.IncidentInquiryUnitsAssigned;
import tmcsim.cadmodels.IncidentInquiryWitnesses;
import tmcsim.client.cadscreens.view.CADMainView;
import tmcsim.common.CADEnums.TEXT_STYLES;

/**
 * II_IncidentInquiry is the view component to the IncidentInquiryModel
 * model object.  The screen is shown when the user requests a "Incident
 * Inquiry."  When the class is instantiated with reference
 * to the model data and document, string and style pairs are added to the 
 * view document.  This class extends from the CADMainView object, which
 * contains the common methods and data needed for display and user
 * interaction.  
 *
 * @see IncidentInquiryModel
 * @author Matthew Cechini 
 * @version $Revision: 1.4 $ $Date: 2009/04/17 16:27:45 $
 */
public class II_IncidentInquiry extends CADMainView {


    /** Reference to the Model class for the Incident Inquiry data. */
    private IncidentInquiryModel theModel;
    
    /**
     * Constructor.  Initializes the screen with the appropriate formatted text.
     *
     * @param newModel The model data object.
     * @param viewdoc  The Document object used for displaying the model data.
     */
    public II_IncidentInquiry(IncidentInquiryModel newModel, Document viewDoc) {
        super(viewDoc);
        
        theModel         = newModel;
                
        initialize();
    }   
    
    /**
     * This method initializes the screen's Document object with 
     * text and style pairs to create the correct screen format.
     * 
     * The IncidentInquiry model data strings are created in the following
     * order:
     *
     * <li>Header Info</li>
     * <li>Details</li>
     * <li>Units Assigned</li>
     * <li>Tows</li>
     * <li>Services</li>
     * <li>Witnesses</li>
     *
     * The resulting text/style strings are set into the base CADMainView class for display.
     */
    private void initialize() {
        
        int counter = 1;
        
        IncidentInquiryModel_obj tempModelObj = theModel.getModelObject();      
        
        addDocElement("LOG: ", TEXT_STYLES.CYAN);
          
        addDocElement(rPad(tempModelObj.getHeader().logNumber.toString(), 3) + 
                           rPad(tempModelObj.getHeader().logStatus, 5),
                           TEXT_STYLES.YELLOW);
        
        addDocElement("PRI: ", TEXT_STYLES.CYAN);
        
        addDocElement(rPad(tempModelObj.getHeader().priority, 3),
                TEXT_STYLES.YELLOW);            
        
        addDocElement("TYPE: ", TEXT_STYLES.CYAN);
        
        addDocElement(rPad(tempModelObj.getHeader().type, 7), 
                TEXT_STYLES.YELLOW);    
        
        addDocElement("CB: ", TEXT_STYLES.CYAN);
        
        addDocElement(rPad(tempModelObj.getHeader().callBoxNumber, 18),
                TEXT_STYLES.YELLOW);   
        
        addDocElement("BEAT: ", TEXT_STYLES.CYAN);
        
        addDocElement(rPad(tempModelObj.getHeader().beat, 7) + "\n",
                TEXT_STYLES.YELLOW);     
        
        addDocElement("LOC: ", TEXT_STYLES.CYAN);
        
        addDocElement(rPad(tempModelObj.getHeader().fullLocation, 45),
                TEXT_STYLES.YELLOW);                                        
        
        addDocElement("CS: " + "\n", TEXT_STYLES.CYAN);
        
        addDocElement(lPad("(" + tempModelObj.getHeader().truncLocation + ")", 23)  + "\n",
                TEXT_STYLES.YELLOW); 
        
        addDocElement("ORI: ", TEXT_STYLES.CYAN);
                    
        addDocElement(rPad(tempModelObj.getHeader().origin, 8) 
                           + rPad(tempModelObj.getHeader().incidentDate, 5)
                           + rPad(tempModelObj.getHeader().incidentTime, 5),
                           TEXT_STYLES.YELLOW);            
        
        addDocElement("DISP: ", TEXT_STYLES.CYAN);
        
        addDocElement(rPad(tempModelObj.getHeader().dispatcher, 9),
                TEXT_STYLES.YELLOW);    
        
        addDocElement(rPad("FILED:", 18),
                TEXT_STYLES.CYAN);
    
        addDocElement("X: \n", TEXT_STYLES.CYAN);
        
        addDocElement(rPad("ORI ACTION:", 34), TEXT_STYLES.CYAN);
        
        addDocElement(rPad("R/S:", 15), TEXT_STYLES.CYAN);             
        
        addDocElement(rPad("DUP:",14), TEXT_STYLES.CYAN);
        
        addDocElement(rPad("CBT:", 14), TEXT_STYLES.CYAN);
        
        addDocElement("Z:", TEXT_STYLES.CYAN);
        
        addDocElement("Y\n", TEXT_STYLES.YELLOW);             
                      
        
        if(tempModelObj.getDetails().size() > 0){ 
            addDocElement("-------------------------------------",  
                    TEXT_STYLES.CYAN);
            
            //if(tempModelObj.getDetailsUpdated())
            //  addDocElement(" DETAIL ", TEXT_STYLES.REVERSE_CYAN);
            //else
                addDocElement(" DETAIL ", TEXT_STYLES.CYAN);

            addDocElement("-----------------------------------\n", 
                    TEXT_STYLES.CYAN);
                                
        
            boolean firstLine = true;
            counter = 1;            
            for(IncidentInquiryDetails detail : tempModelObj.getDetails()) {

                for(String detail_string : detail.parseDetails(63)) {
                    if(firstLine) {
                        addDocElement(rPad(detail.getLogInfo(), 14),
                                TEXT_STYLES.AQUA);    
                        
                        addDocElement(rPad(String.valueOf(counter++), 3),
                                TEXT_STYLES.CYAN);

                        firstLine = false;
                    }
                    else {
                        addDocElement(rPad("", 17),
                                TEXT_STYLES.GRAY);    
                    }               
                    addDocElement(detail_string,
                            TEXT_STYLES.YELLOW);
                }
                firstLine = true;
            }
        }                     
            
        if(tempModelObj.getUnits().size() > 0) {
            addDocElement("--------------------------------", 
                    TEXT_STYLES.CYAN);
            
            //if(tempModelObj.getUnitsUpdated())
            //  addDocElement(" UNITS ASSIGNED ", TEXT_STYLES.REVERSE_CYAN);
            //else
                addDocElement(" UNITS ASSIGNED ", TEXT_STYLES.CYAN);

            addDocElement("--------------------------------\n", 
                    TEXT_STYLES.CYAN);        
      
          
            for(IncidentInquiryUnitsAssigned unit : tempModelObj.getUnits()) {
            
                addDocElement(rPad(unit.getLogInfo(), 14),
                        TEXT_STYLES.AQUA);    
            
                if(unit.isPrimary) 
                    addDocElement(rPad("<", 3), TEXT_STYLES.CYAN);
                else 
                    addDocElement(rPad("-", 3), TEXT_STYLES.CYAN);
            
                addDocElement(rPad(unit.beat, 6), TEXT_STYLES.YELLOW);    
            
                if(unit.isActive) 
                    addDocElement(unit.statusType + "\n", TEXT_STYLES.RED);                            
                else 
                    addDocElement(unit.statusType + "\n", TEXT_STYLES.GREEN); 
            
            }       
        }

                      
        if(tempModelObj.getTows().size() > 0){
            addDocElement("-------------------------------------", 
                    TEXT_STYLES.CYAN);
            
            //if(tempModelObj.getTowsUpdated())
            //  addDocElement(" TOWS ", TEXT_STYLES.REVERSE_CYAN);
            //else
                addDocElement(" TOWS ", TEXT_STYLES.CYAN);

            addDocElement("-------------------------------------\n", 
                    TEXT_STYLES.CYAN);              
        
            counter = 1;
            for(IncidentInquiryTows tow : tempModelObj.getTows()) {
                    
                addDocElement(rPad(tow.getLogInfo(), 14),
                        TEXT_STYLES.AQUA);    
            
                addDocElement(rPad(String.valueOf(counter++), 3),
                        TEXT_STYLES.CYAN);                 
                                        
                addDocElement(rPad(tow.towCompany, 40),
                        TEXT_STYLES.YELLOW);  
  
                addDocElement("BEAT: ", TEXT_STYLES.CYAN);      
            
                addDocElement(tow.beat + "\n", TEXT_STYLES.YELLOW);                 
            
                addDocElement(lPad("CONF: ", 23), TEXT_STYLES.CYAN);
            
                addDocElement(rPad(tow.confPhoneNum, 22), TEXT_STYLES.YELLOW); 
            
                addDocElement("PUB: ", TEXT_STYLES.CYAN); 
            
                addDocElement(rPad(tow.publicPhoneNum, 14), TEXT_STYLES.YELLOW); 
            
                addDocElement(tow.statusInfo + "\n", TEXT_STYLES.YELLOW);                 
            }            
        }    
            
        if(tempModelObj.getServices().size() > 0){
            addDocElement("-----------------------------------", 
                    TEXT_STYLES.CYAN);
            
            //if(tempModelObj.getServicesUpdated())
            //  addDocElement(" SERVICES ", TEXT_STYLES.REVERSE_CYAN);
            //else
                addDocElement(" SERVICES ", TEXT_STYLES.CYAN);

            addDocElement("-----------------------------------\n", 
                    TEXT_STYLES.CYAN);              
            
            counter = 1;
            for(IncidentInquiryServices service : tempModelObj.getServices()) {
            
                addDocElement(rPad(service.getLogInfo(), 14), TEXT_STYLES.AQUA);    
            
                addDocElement(rPad(String.valueOf(counter++), 3), TEXT_STYLES.CYAN);                 
                                        
                addDocElement(service.serviceName + "\n", TEXT_STYLES.YELLOW);  
            
                addDocElement(lPad("CONF: ", 23), TEXT_STYLES.CYAN);
            
                addDocElement(rPad(service.confPhoneNum, 25), TEXT_STYLES.YELLOW); 
            
                addDocElement("PUB: ", TEXT_STYLES.CYAN); 
            
                addDocElement(service.publicPhoneNum + "\n", TEXT_STYLES.YELLOW); 
            }
        }
                     
        if(tempModelObj.getWitnesses().size() > 0){
            addDocElement("-----------------------------------", 
                    TEXT_STYLES.CYAN);
            
            //if(tempModelObj.getWitnessesUpdated())
            //  addDocElement(" WITNESSES ", TEXT_STYLES.REVERSE_CYAN);
            //else
                addDocElement(" WITNESSES ", TEXT_STYLES.CYAN);

            addDocElement("----------------------------------\n", 
                    TEXT_STYLES.CYAN);          
        
            counter = 1;
            for(IncidentInquiryWitnesses witness : tempModelObj.getWitnesses()) {
            
                addDocElement(rPad(witness.getLogInfo(), 14), TEXT_STYLES.AQUA);    
            
                addDocElement(rPad(String.valueOf(counter++), 3), TEXT_STYLES.CYAN);                 
                                        
                addDocElement(rPad(witness.reportingParty, 20), TEXT_STYLES.YELLOW);  
            
                addDocElement("TEL: ", TEXT_STYLES.CYAN); 
                
                addDocElement(witness.telephoneNum + "\n", TEXT_STYLES.YELLOW);                 
            
                addDocElement(lPad("ADDR: ", 23), TEXT_STYLES.CYAN);
            
                addDocElement(witness.address + "\n", TEXT_STYLES.YELLOW);               

            }           
        }
        
    }   
     
}