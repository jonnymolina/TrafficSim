
package tmcsim.cadsimulator;


import java.rmi.RemoteException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tmcsim.cadmodels.BlankScreenModel;
import tmcsim.cadmodels.CADRoutedMessage;
import tmcsim.cadmodels.CADScreenModel;
import tmcsim.cadmodels.IncidentBoardModel;
import tmcsim.cadmodels.IncidentBoardModel_obj;
import tmcsim.cadmodels.IncidentInquiryDetails;
import tmcsim.cadmodels.IncidentInquiryModel;
import tmcsim.cadmodels.IncidentInquiryModel_obj;
import tmcsim.cadmodels.IncidentSummaryModel;
import tmcsim.cadmodels.IncidentSummaryModel_obj;
import tmcsim.cadmodels.RoutedMessageModel;
import tmcsim.client.cadclientgui.data.Incident;
import tmcsim.client.cadclientgui.data.IncidentEvent;
import tmcsim.common.ObserverMessage;
import tmcsim.common.CADEnums.CADScreenNum;
import tmcsim.common.CADEnums.CADScreenType;
import tmcsim.common.CADEnums.CAD_ERROR;
import tmcsim.common.CADEnums.CAD_KEYS;
import tmcsim.common.CADProtocol.CAD_FIELD_CODES;
import tmcsim.common.CADProtocol.DATA_TAGS;


/**
 * CADScreenManager is used to contain the current information for all four 
 * CAD screens that are available on a CAD Client.  The CADScreenManager
 * uniquely identifies a CAD Client with a unique CAD Position number and User 
 * ID.  The CADScreenManager object keeps track of the number of routed 
 * messages received and the screen update map.<br>
 * <br>
 * The CADScreenManager object observers the Coordinator for any updates to 
 * the simulation data. Whenever there is an event in the simulation, the 
 * CADScreenManager object determines if the update is relevant for this CAD 
 * position and the currently displayed CAD screens.<br>
 * <br> 
 * CADScreenManager handles all CAD commands, either through the receiveCommand() 
 * method or series of screen request methods.
 * 
 * A timer is instantiated to update the remote CAD client with the current 
 * time at each minute.
 * 
 * @author Matthew Cechini 
 * @version $Revision: 1.5 $ $Date: 2006/06/06 20:46:41 $
 */
public class CADScreenManager extends Observable implements Observer {

    
    /**
     * The CADScreenTimer is a timer task that calls the updateTime()
     * method everytime it expires.  This timer is used to keep the
     * CAD time current on the CAD screen.
     */
    private class CADScreenTimer extends TimerTask {
        public void run() {
            updateTime();
        }
    }       
        
    /** Reference to the Coordinator object. */
    private Coordinator theCoordinator;
    
    /** The current value of the CAD terminal's position. */
    private int CADPosition;    

    /** Unique CAD User ID for this set of CAD Screens. */
    private String CADUserID;
    
    /** The current CAD Screen number. */    
    private CADScreenNum currentCADScreenNum; 
        
    /**
     * Map of the CAD Screens.  The map's values are the CADScreenNum enumeration values,
     * which reference the current CADScreenModel object values.
     */
    private TreeMap<CADScreenNum, CADScreenModel> CADScreensMap;
    
    /** 
     * Map of CAD Screen updates.  The map's values are a boolean signifying whether there
     * is an update available for each CAD screen, keyed by the CADScreenNum objects. 
     */
    private TreeMap<CADScreenNum, Boolean> CADScreenUpdates;
        
    /** Map of CADRoutedMessages that have been received to this CAD terminal, and whether
     *  the message has been read. */
    private TreeMap<CADRoutedMessage, Boolean> messageMap = null;
    
    /**
     * Constructor.  Initialize CADScreen windows.  All screens are initialized 
     * to a blank screen.  The CADScreenUpdates map is also initialized with each
     * cad screen number being set to having a false update value.
     * The CADScreenTimer is created and scheduled to call the updateTime() 
     * method every minute to increment the current CAD time.
     */  
    public CADScreenManager(Coordinator coor) {
        
        theCoordinator = coor;
        
        CADPosition = 0;
        CADUserID   = "A00000";
        currentCADScreenNum = CADScreenNum.ONE;
    
        CADScreensMap    = new TreeMap<CADScreenNum, CADScreenModel>();
        CADScreenUpdates = new TreeMap<CADScreenNum, Boolean>();

        CADScreenNum screenNum = CADScreenNum.ONE;

        CADScreensMap.put(screenNum, new BlankScreenModel(screenNum));
        CADScreenUpdates.put(screenNum, false);
        screenNum = screenNum.next();
        
        CADScreensMap.put(screenNum, new BlankScreenModel(screenNum)); 
        CADScreenUpdates.put(screenNum, false);
        screenNum = screenNum.next();
        
        CADScreensMap.put(screenNum, new BlankScreenModel(screenNum)); 
        CADScreenUpdates.put(screenNum, false);
        screenNum = screenNum.next();
        
        CADScreensMap.put(screenNum, new BlankScreenModel(screenNum)); 
        CADScreenUpdates.put(screenNum, false);
        screenNum = screenNum.next();       

        CADScreenModel.theCADTime = CADSimulator.getCADTime();
        CADScreenModel.theCADDate = CADSimulator.getCADDate();  
            
        messageMap = new TreeMap<CADRoutedMessage, Boolean>();     

        Date d = new Date();
        long delay = (60 - ((d.getTime() / 1000) % 60)) * 1000;

        Timer timer             = new Timer();  
        CADScreenTimer cadTimer = new CADScreenTimer();   
        timer.scheduleAtFixedRate(cadTimer, new Date(d.getTime() + delay), (long)1000 * 60);        
        
    }

    /**
     * Called by the timer at the beginning of every minute.  When this method 
     * is called, notify observers with the new time.
     */  
    protected void updateTime() {
        CADScreenModel.theCADTime = CADSimulator.getCADTime();
        
        setChanged();
        notifyObservers(new ObserverMessage(ObserverMessage.messageType.TIME_UPDATE, 
                CADSimulator.getCADTime()));
    }   
 
    /**
     * Returns the current CAD model after updating it with any new screen 
     * updates and messages.
     * 
     * @return The current CAD model object.
     */
    public CADScreenModel getCurrentCADModel() {

        updateCADModel();
        
        return currentCADModel();   
    }
    
    /**
     * Set the CAD position for this terminal.  
     * @param position The new CAD position.
     */  
    public void setCADPosition(int position) {  
        CADPosition = position; 
    }
    
    /**
     * Set the CAD user id for this terminal.
     * @param userID The new CAD user id.
     */
    public void setCADUserID(String userID) {
        CADUserID = userID;
    }
    
    /**
     * Updates the current CAD model with the number of routed messages
     * that have been received by this terminal, and the current 
     * update map.
     */
    private void updateCADModel() {
        currentCADModel().numberRoutedMessages = messageMap.size();
        currentCADModel().unreadMessages = messageMap.values().contains(new Boolean(false));
        currentCADModel().screenUpdateMap.putAll(CADScreenUpdates);
    }
    
    /**
     * A helper method to reduce code.  The CADScreenModel object for the
     * current CAD screen is returned from the CADScreensMap.
     * @return The current CADScreenModel object.
     */
    private CADScreenModel currentCADModel() {
        
        return CADScreensMap.get(currentCADScreenNum);
    }
    
    /**
     * Returns the log info for this terminal in the following format: XXXYYYYY.
     * XXX is the cad position, left zero padded. YYYYYY is the CAD user id.   
     * @return Log info string.
     */
    private String getLogInfo() {
        StringBuffer logInfoBuf = new StringBuffer();
        
        while(Integer.toString(CADPosition).length() + logInfoBuf.length() < 3) 
            logInfoBuf.append("0");
        
        logInfoBuf.append(CADPosition);
        logInfoBuf.append(CADUserID);
        
        return logInfoBuf.toString();
    }
    
    /**
     * This method receives ObserverMessage updates.  The message types that
     * are responded to are:
     * 
     * INCIDENT_SUMMARY - Update all CADScreenManager that are showing the 
     *                    SA_INCIDENT_SUMMARY screen.  Add the parameter
     *                    object to the screen's model object, update the 
     *                    CADScreenUpdates map, update the current model,
     *                    and notify observers that there is a screen update.
     * 
     * INCIDENT_INQUIRY - Update all CADScreenManager that are showing the 
     *                    II_INCIDENT_INQUIRY screen with the same log number
     *                    as the parameter model object.  Add the parameter
     *                    object to the screen's model object, update the 
     *                    CADScreenUpdates map, update the current model,
     *                    and notify observers that there is a screen update.
     * 
     * INCIDENT_BOARD - Update all CADScreenManager that are showing the 
     *                    IB_INCIDENT_BOARD screen.  Add the parameter
     *                    object to the screen's model object, update the 
     *                    CADScreenUpdates map, update the current model,
     *                    and notify observers that there is a screen update.
     * 
     * ROUTED_MESSAGE - Only respond to this update if the routed message
     *                  has been routed to this CAD terminal posision.
     *                  Update all CADScreenManager that are showing the 
     *                  TO_ROUTED_MESSAGE screen.  Add the parameter
     *                  object to the screen's model object, update the current model,
     *                  and notify observers that there is a new routed message.
     * 
     * RESET_SIMULATION - Reset the CADScreensMap to contain the BlankScreenModel
     *                    for all CADScreenManager, reset all screen updates to false, 
     *                    and notify observers to refresh the view.  Update the
     *                    current CAD model and notify observers of the screen
     *                    update.  Finally, reset the current screen to screen ONE,
     *                    and notify observers to refresh.
     */
    public void update(Observable o, Object arg) {
        
        ObserverMessage oMessage = (ObserverMessage)arg;
        boolean updatedModel = false;
        
        switch(oMessage.type) {
            case INCIDENT_SUMMARY:
                
                for(CADScreenModel model : CADScreensMap.values()) {
                    if(model.getType() == CADScreenType.SA_INCIDENT_SUMMARY) {                          

                        model.addModelObject(oMessage.value);
                        updatedModel = true;
                        
                        CADScreenUpdates.put(model.getScreenNum(), true);
                    }
                }
        
                if(updatedModel) {      
                    updateCADModel();
                    
                    setChanged();
                    notifyObservers(new ObserverMessage(ObserverMessage.messageType.SCREEN_UPDATE, 
                            CADScreenModel.updateMapToString(currentCADModel().screenUpdateMap))); 
                }
            
                break;
                                                
            case INCIDENT_INQUIRY:

                for(CADScreenModel model : CADScreensMap.values()) {
                    if(model.getType() == CADScreenType.II_INCIDENT_INQUIRY) {                          

                        if(((IncidentInquiryModel)model).logNumMatches(((IncidentInquiryModel_obj)oMessage.value))) {
                            model.addModelObject(oMessage.value);
                            updatedModel = true;

                            CADScreenUpdates.put(model.getScreenNum(), true);
                        }
                    }
                }
                
                if(updatedModel) {
                    updateCADModel();
                    
                    setChanged();
                    notifyObservers(new ObserverMessage(ObserverMessage.messageType.SCREEN_UPDATE, 
                            CADScreenModel.updateMapToString(currentCADModel().screenUpdateMap))); 
                    
                    
                }
                break;
                
            case INCIDENT_BOARD:
            
                for(CADScreenModel model : CADScreensMap.values()) {
                    if(model.getType() == CADScreenType.IB_INCIDENT_BOARD) {                        

                        model.addModelObject(oMessage.value);           
                        updatedModel = true;
                    }
                }
            
                if(updatedModel) {
                    updateCADModel();
                    
                    setChanged();
                    notifyObservers(new ObserverMessage(ObserverMessage.messageType.SCREEN_UPDATE, 
                            CADScreenModel.updateMapToString(currentCADModel().screenUpdateMap))); 
                }
                
                break;              

            case ROUTED_MESSAGE:
            
                if(((CADRoutedMessage)oMessage.value).toPosition == CADPosition) { 
                    messageMap.put((CADRoutedMessage)oMessage.value, false); 
                    
                    for(CADScreenNum screen : CADScreensMap.keySet()) {
                        if(CADScreensMap.get(screen).getType() == CADScreenType.TO_ROUTED_MESSAGE) {
                            ((RoutedMessageModel)CADScreensMap.get(screen)).addModelObject(oMessage.value);                                     
                        }
                    }
                    
                    updateCADModel();

                    setChanged();
                    notifyObservers(new ObserverMessage(ObserverMessage.messageType.ROUTED_MESSAGE, null));

                }
                
                break;

            case RESET_SIMULATION:
                
                for(CADScreenNum num : CADScreensMap.keySet()) {
                    CADScreensMap.put(num, new BlankScreenModel(num));  

                    currentCADScreenNum = num;
                    CADScreenUpdates.put(num, false);
                    
                    setChanged();
                    notifyObservers(new ObserverMessage(ObserverMessage.messageType.REFRESH_VIEW, null));
                }

                updateCADModel();               
                setChanged();
                notifyObservers(new ObserverMessage(ObserverMessage.messageType.SCREEN_UPDATE, 
                        CADScreenModel.updateMapToString(currentCADModel().screenUpdateMap))); 

                //reset back to screen one
                currentCADScreenNum = CADScreenNum.ONE;
                setChanged();
                notifyObservers(new ObserverMessage(ObserverMessage.messageType.REFRESH_VIEW, null));
                break;
        }
    }
    

    /**
     * Set the command line in the current CAD model.
     * 
     * @param cmdLine New command line text.
     */
    public void receiveCommandLine(String cmdLine) {
        getCurrentCADModel().commandLine = cmdLine;
    }
  
   /**
    * Receive a command from the CAD Client.  This method determines which 
    * command was pushed, and then takes the correct action, determining 
    * if the current CAD Screen needs update.  Each command that is pressed 
    * is referenced by the keycode for the associated key.  These key codes 
    * are defined in the CADProtocol class.  The following table shows the
    * CAD Command and action.
    *
    *<table cellpadding="2" cellspacing="2" border="1"
    * style="text-align: left; width: 250px;">
    *  <tbody>
    *    <tr>
    *      <th>CAD Protocol Command<br></th>
    *      <th>Action Taken<br></th>
    *    </tr>
    *    <tr>
    *      <td>CYCLE</td>
    *      <td>Set the currentCADScreenNum to the next screen number, set the screen update
    *          value to false, update the current model, and notify observers to refresh the view.
    *      </td>
    *    </tr>
    *    <tr>
    *      <td>REFRESH</td>
    *      <td>Set the screen update value to false, update the current model, 
    *          and notify observers to refresh the view.</td>
    *    </tr>
    *    <tr>
    *      <td>NEXT_QUEUE</td>
    *      <td>If this terminal has not received any messages, do nothing.  If 
    *          messages have been received, and the current CADScreen is 
    *          showing the TO_ROUTED_MESSAGE screen, then call the model's 
    *          nextQueue() method and notify observers to refresh their view.  
    *          If messages have been received, but the current screen is not 
    *          showing a routed message, then set the current cad screen model 
    *          to the RoutedMessageModel with the received list of messages and 
    *          notify observers.  Update the message map to show that the 
    *          message has been viewd.  Update the unreadMessages flag to 
    *          designate whether unread messages still exist for this CAD 
    *          position.  Notify observers with the updated routed message info.</td>
    *    </tr>
    *    <tr>
    *      <td>DELETE_QUEUE<br></td>
    *      <td>If this terminal has not received any messages, do nothing.  
    *          If the current CADScreen is showing a routed message, get the 
    *          current message.  Remove this message from the current message map.
    *          Also remove the message from all RoutedMessageModels that are 
    *          being shown in a CAD Screen by calling the deleteQueue() method. 
    *          Notify observers to refresh their view. 
    *      </td>
    *    </tr>
    *    <tr>
    *      <td>PREV_QUEUE<br></td>
    *      <td>If this terminal has not received any messages, do nothing.  
    *          If messages have been received, and the current CADScreen is 
    *          showing the TO_ROUTED_MESSAGE screen, then call the model's 
    *          prevQueue() method and notify observers to refresh their view.  
    *          If messages have been received, but the current screen is not 
    *          showing a routed message, then set the current cad screen model 
    *          to the RoutedMessageModel with the received list of messages and 
    *          notify observers.  Update the message map to show that the 
    *          message has been viewd.  Update the unreadMessages flag to 
    *          designate whether unread messages still exist for this CAD 
    *          position.  Notify observers with the updated routed message info.
    *      </td>
    *    </tr>
    *    <tr>
    *      <td>SCREEN_CLEAR<br></td>
    *      <td>Set the current model to the BlankScreenModel and notify observers
    *          to refresh the view.</td>
    *   </tr>
    *  </tbody>
    *</table>
    *
    *
    * @param receivedCommand Integer value of the key that was pressed by client.
    * @return true if the current CAD Screen needs updating.
    */
   public void receiveCommand(CAD_KEYS key) {
      
      switch (key) {
       
        case CYCLE: 
           
            currentCADScreenNum = currentCADScreenNum.next();   

            CADScreenUpdates.put(currentCADScreenNum, false);
                        
            updateCADModel();
            
            setChanged();
            notifyObservers(new ObserverMessage(ObserverMessage.messageType.REFRESH_VIEW, null));
            break;
            
        case REFRESH:           

            CADScreenUpdates.put(currentCADScreenNum, false);
            
            updateCADModel();
            
            setChanged();
            notifyObservers(new ObserverMessage(ObserverMessage.messageType.REFRESH_VIEW, null));
            break;

        case  NEXT_QUEUE:

            if(messageMap.size() > 0) {
                if(currentCADModel().getType() == CADScreenType.TO_ROUTED_MESSAGE) {
                    ((RoutedMessageModel)currentCADModel()).nextQueue();
                }
                else {
                    LinkedList<CADRoutedMessage> messages = new LinkedList<CADRoutedMessage>();
                    messages.addAll(messageMap.keySet());
                    
                    CADScreensMap.put(currentCADScreenNum, new RoutedMessageModel(
                            currentCADScreenNum, messages));                    
                }
                
                setChanged();
                notifyObservers(new ObserverMessage(ObserverMessage.messageType.REFRESH_VIEW, null));

                
                messageMap.put(((RoutedMessageModel)currentCADModel()).getCurrentMessage(), true);
                currentCADModel().unreadMessages = messageMap.values().contains(new Boolean(false));

                setChanged();
                notifyObservers(new ObserverMessage(ObserverMessage.messageType.ROUTED_MESSAGE, null));
            }

            break;

        case  DELETE_QUEUE:  

            if(messageMap.size() > 0) {

                if(currentCADModel().getType() == CADScreenType.TO_ROUTED_MESSAGE) {
                    
                    CADRoutedMessage delMsg = ((RoutedMessageModel)currentCADModel()).getCurrentMessage();
                    
                    messageMap.remove(delMsg);
                    
                    for(CADScreenNum screen : CADScreensMap.keySet()) {
                        if(CADScreensMap.get(screen).getType() == CADScreenType.TO_ROUTED_MESSAGE) {
                            ((RoutedMessageModel)CADScreensMap.get(screen)).deleteQueue(delMsg);
                        }
                    }                       

                    setChanged();
                    notifyObservers(new ObserverMessage(ObserverMessage.messageType.REFRESH_VIEW, null)); 
                }
            }
            break;

 
        case  PREV_QUEUE:                         
            if(messageMap.size() > 0) {
                if(currentCADModel().getType() == CADScreenType.TO_ROUTED_MESSAGE) {
                    ((RoutedMessageModel)currentCADModel()).prevQueue();
                }
                else {
                    LinkedList<CADRoutedMessage> messages = new LinkedList<CADRoutedMessage>();
                    messages.addAll(messageMap.keySet());
                    
                    CADScreensMap.put(currentCADScreenNum, new RoutedMessageModel(
                            currentCADScreenNum, messages));        

                }
                
                setChanged();
                notifyObservers(new ObserverMessage(ObserverMessage.messageType.REFRESH_VIEW, null));


                messageMap.put(((RoutedMessageModel)currentCADModel()).getCurrentMessage(), true);
                currentCADModel().unreadMessages = messageMap.values().contains(new Boolean(false));
                
                setChanged();
                notifyObservers(new ObserverMessage(ObserverMessage.messageType.ROUTED_MESSAGE, null));
            }
            break;

        case  SCREEN_CLEAR: 
            CADScreensMap.put(currentCADScreenNum, new BlankScreenModel(currentCADScreenNum));   
            
            setChanged();
            notifyObservers(new ObserverMessage(ObserverMessage.messageType.REFRESH_VIEW, null));
            break;
        }

    }   
    
    /**
     * Handles a request for the current screen to show the IB_INCIDENT_BOARD screen.  
     * Create a new IncidentBoardModel object and set its data with the Incident Board
     * model data from the Coordinator.  Put the new model object into the CADScreensMap
     * and notify observers to refresh the view.
     * 
     * There are no additional tokens that are read from the parameter node 
     * when this command is received.
     * 
     * @param root (Not used)
     */
    public void incidentBoardRequest(Element root) {

        IncidentBoardModel tempIB = new IncidentBoardModel(currentCADScreenNum);                        
                    
        //update with new information                   
        for(IncidentBoardModel_obj ibmo : theCoordinator.getIncidentBoardModelObjects()) {
            tempIB.addModelObject(ibmo);
        }
            
        CADScreensMap.put(currentCADScreenNum, tempIB); 
        CADScreenUpdates.put(currentCADScreenNum, false);
            
        setChanged();
        notifyObservers(new ObserverMessage(ObserverMessage.messageType.REFRESH_VIEW, null));

    }
    
    /**
     * Handles a request to update an existing incident.  The parameter XML node is parsed
     * for the incident update data.  The following is an example of the received XML element.
     * 
     * <UPDATE_INCIDENT LOG_NUM="">
     *    <DETAILS/> 
     * </UPDATE_INCIDENT>
     * 
     * If the LOG_NUM attribute contains a value, then the user is attempting to update
     * an incident that may or may not be currently viewed.  If this value does not
     * match any current incidents, notify the observers with an INVALID_LOG error message.<br>
     *
     * If the LOG_NUM attribute is empty, then the user is attempting to modify the incident
     * on the current screen.  If the current screen is not an II_INCIDENT_INQUIRY, then
     * notify the observers with A NO_LOG_NUMBER error message. 
     * 
     * If the UPDATE_INCIDENT node contains <DETAILS> elements, parse the text content for
     * each of these detail elements, creating IncidentInquiryDetails objects and adding
     * them to a IncidentInquiryModel_obj, which is then sent to the Coordinator to update
     * the incident.
     * 
     * If the ROOT node does not contain any <DETAILS> elements, then notify observers
     * with an UNAUTH_CMD error message.
     * 
     * @param root XML document element containing incident update request data.
     */ 
    public void incidentUpdateRequest(Element root) {

        /* UI.  or UI.###
         * If it's a UI. check if we're in an II screen, if not error
         * If UI.### add it to the log... can we check against what current log we're looking at???
         * if so, we need to update it....
         */              
                
        //ascertain if the next token is a number, if so, we're updating a specific log, else 
        //updating the current log(assuming we're looking at one)
        
        IncidentInquiryModel_obj newIIobj = new IncidentInquiryModel_obj(CADPosition);
            
        String  parsedLogNumber = null;
        Integer logNumber       = null;

        NodeList detailList  = null;        
        
        parsedLogNumber = root.getAttribute(DATA_TAGS.LOG_NUM.tag);     
        
        if(parsedLogNumber.length() > 0) { //UI.### Format
        
            logNumber = Integer.parseInt(parsedLogNumber);
            
            //if doesn't exist, send INVALID_LOG error
            if(!theCoordinator.incidentExists(logNumber)) {
                setChanged();
                notifyObservers(new ObserverMessage(ObserverMessage.messageType.CAD_INFO_MESSAGE,
                                                    CAD_ERROR.INVALID_LOG.message));    
                                                    
                return;     
                                                    
            }

        }
        else {  //UI. format
            
            //not looking at II, error
            if(currentCADModel().getType() != CADScreenType.II_INCIDENT_INQUIRY) {                  
                setChanged();
                notifyObservers(new ObserverMessage(ObserverMessage.messageType.CAD_INFO_MESSAGE,
                                                        CAD_ERROR.NO_LOG_NUM.message));
                return;
            }
            //looking at II, continue to update
            else { 
                logNumber = ((IncidentInquiryModel)currentCADModel()).getLogNumber();                               
            }                       
        }
        
        //TODO more than just details...
        
        detailList = root.getElementsByTagName(CAD_FIELD_CODES.DETAILS.fullName);       
        if(detailList.getLength() > 0) {
            for(int i = 0; i < detailList.getLength(); i++) {
                Node detailNode = detailList.item(i);           
                newIIobj.getDetails().add(new IncidentInquiryDetails(
                        getLogInfo(), detailNode.getTextContent(), 
                        Boolean.parseBoolean(detailNode.getAttributes().getNamedItem(
                                DATA_TAGS.SENSITIVE.tag).getNodeValue())));
            }
            
            newIIobj.setLogNumber(logNumber);           
            
            theCoordinator.commandLineUpdate(newIIobj);
        }   
        else {
            setChanged();
            notifyObservers(new ObserverMessage(ObserverMessage.messageType.CAD_INFO_MESSAGE,
                                                CAD_ERROR.UNAUTH_CMD.message)); 
            return;
        }
        
    }
    
    /**
     * Handles a request for the current screen to show the II_INCIDENT_INQUIRY screen.  
     * The parameter XML node is parsed for the incident inquiry data.  The following 
     * is an example of the received XML element.  <br>
     * 
     * <INCIDENT_INQUIRY>###</INCIDENT_INQUIRY> <br>
     * 
     * Parse the text content of the INCIDENT_INQUIRY node for the incident number that 
     * is being inquired for.  If this incident does not exist, notify observers with an
     * INVALID_LOG error message.  If the incident does exist, create a new 
     * IncidentInquiryModel object and set its data with the Incident Inquiry 
     * model data from the Coordinator.  Put the new model object into the CADScreensMap
     * and notify observers to refresh the view.
     * 
     * @param root XML document element containing incident inquiry request data.
     */ 
    public void incidentInquiryRequest(Element root) {

        Integer incidentNum = Integer.parseInt(root.getTextContent());
                        
        if(theCoordinator.incidentExists(incidentNum)) {
                
            IncidentInquiryModel tempII = new IncidentInquiryModel(currentCADScreenNum, incidentNum);
            
            for(IncidentInquiryModel_obj iimo : 
                theCoordinator.getIncidentInquiryModelObjects(incidentNum))  {                          
                tempII.addModelObject(iimo);
            }
                            
            CADScreensMap.put(currentCADScreenNum, tempII);   
            CADScreenUpdates.put(currentCADScreenNum, false);
                
            setChanged();
            notifyObservers(new ObserverMessage(ObserverMessage.messageType.REFRESH_VIEW, null));             

        }
        else {
            setChanged();
            notifyObservers(new ObserverMessage(ObserverMessage.messageType.CAD_INFO_MESSAGE,
                                                CAD_ERROR.INVALID_LOG.message));
        }
    }
    
    /**
     * Handles a request for the current screen to show the SA_SUMMARY_BOARD screen. 
     * Create a new IncidentSummaryModel object and set its data with the Incident Summary
     * model data from the Coordinator.  Put the new model object into the CADScreensMap
     * and notify observers to refresh the view.
     * 
     * @param root (not used)
     */ 
    public void incidentSummaryRequest(Element root) {
        
        IncidentSummaryModel tempSA = new IncidentSummaryModel(currentCADScreenNum);
                
        for(IncidentSummaryModel_obj ismo : 
            theCoordinator.getIncidentSummaryModelObjects()) 
                tempSA.addModelObject(ismo);    
                
        CADScreensMap.put(currentCADScreenNum, tempSA);  
        CADScreenUpdates.put(currentCADScreenNum, false);
        
        setChanged();
        notifyObservers(new ObserverMessage(ObserverMessage.messageType.REFRESH_VIEW, null));                   

    }
    
    /**
     * (EI) - Enter Incident<br>
     * 
     * TODO  work on this.
     */
    public void enterIncidentRequest(Element root) {        

        try {
            Incident newIncident   = null;
            IncidentEvent newEvent = null;;
            
            Long currentSimTime     = 0L;
            Integer logNumber       = new Integer(0);
            
            NodeList detailList  = null;                    
    
            newEvent = new IncidentEvent(0);        
    
            detailList = root.getElementsByTagName(CAD_FIELD_CODES.DETAILS.fullName);       
            if(detailList.getLength() > 0) {
                for(int i = 0; i < detailList.getLength(); i++) {
                    Node detailNode = detailList.item(i);           
                    newEvent.eventInfo.getDetails().add(new IncidentInquiryDetails(getLogInfo(), 
                            detailNode.getTextContent(),
                            Boolean.parseBoolean(detailNode.getAttributes().getNamedItem(
                                    DATA_TAGS.SENSITIVE.tag).getNodeValue())));
                }               
            }   
            else {
                setChanged();
                notifyObservers(new ObserverMessage(ObserverMessage.messageType.CAD_INFO_MESSAGE,
                                                    CAD_ERROR.UNAUTH_CMD.message)); 
                return;
            }           
            
            for(Incident incident : theCoordinator.getIncidentList()) {
                if(incident.getLogNumber() > logNumber)
                    logNumber = incident.getLogNumber(); 
            }
            logNumber++;

            currentSimTime = theCoordinator.getCurrentSimulationTime();
    
            newIncident = new Incident(logNumber, "", currentSimTime);
            newIncident.addEvent(newEvent);
            
            //theCoordinator.addIncident(newIncident);      
            
        }
        catch (RemoteException re) { /*we're not remote*/ }

                    
        setChanged();
        notifyObservers(new ObserverMessage(ObserverMessage.messageType.REFRESH_VIEW, null));

    }

    /**
     * Handles a request to send a routed message.  The parameter XML node is 
     * parsed for the routed message data. The following is an example of 
     * the received XML element.
     * 
     * <ROUTED_MESSAGE>
     *    <DESTINATION/> 
     *    <MESSAGE/>
     * </ROUTED_MESSAGE>
     * 
     * Parse the DESTINATION element for the CAD Position to receive the message.
     * If a DESTINATION element does not exist, then notify observers with an UNAUTH_CMD 
     * error message.
     * 
     * Parse the MESSAGE element for the message text.  If the MESSAGE element
     * does not exist, then notify observers with an UNAUTH_CMD error message.
     * 
     * The parsed destination may be a comma-delimited string of CAD positions.
     * Tokenize through the parsed string for all destinations and route a message
     * to each of them with a new RoutedMessage object sent to the Coordinator.
     * 
     * Notify observers to refresh their current view.  If the current screen is an
     * II_INCIDENT_INQUIRY, then a detail is to be added to the log.  Create
     * an IncidentInquiryModel_obj and send it to the Coordinator as a command
     * line update.
     * 
     * @param root XML document element containing incident update request data.
     */ 
    public void routedMessageRequest(Element root) { 

        
        String parsed_dest = "";
        String message     = "";
        
        StringTokenizer destTok = null;
        Vector<Integer> destinations = new Vector<Integer>();
        boolean messageSent = false;
        
                
        NodeList destList = root.getElementsByTagName(DATA_TAGS.DESTINATION.tag);
        
        if(destList.getLength() > 0) {
            Node destNode = destList.item(0);               
            parsed_dest   = destNode.getTextContent();
        }   
        else {
            setChanged();
            notifyObservers(new ObserverMessage(ObserverMessage.messageType.CAD_INFO_MESSAGE,
                                                CAD_ERROR.UNAUTH_CMD.message));
            return;
        }           
        

        NodeList messageList = root.getElementsByTagName(DATA_TAGS.MESSAGE.tag);
        
        if(messageList.getLength() > 0) {
            Node messageNode = messageList.item(0);             
            message = messageNode.getTextContent();         
        }   
        else {
            setChanged();
            notifyObservers(new ObserverMessage(ObserverMessage.messageType.CAD_INFO_MESSAGE,
                                                CAD_ERROR.UNAUTH_CMD.message));
            return;
        }                   
        
        destTok = new StringTokenizer(parsed_dest, ",");
        
        while(destTok.hasMoreTokens()) {
            
            try {
                destinations.add(Integer.parseInt(destTok.nextToken()));
            }
            catch (NumberFormatException nfe) {
                setChanged();
                notifyObservers(new ObserverMessage(ObserverMessage.messageType.CAD_INFO_MESSAGE,
                            CAD_ERROR.UNAUTH_CMD.message)); 
                return;
            }
        }
        
        for(int dest : destinations) {
            
            if(CADPosition != dest) {

                CADRoutedMessage newMessage = new CADRoutedMessage(CADPosition,
                           dest, 
                           message, 
                           false);
                
                theCoordinator.routeMessage(newMessage);        
                
                messageSent = true;

            }
        }
        setChanged();
        notifyObservers(new ObserverMessage(ObserverMessage.messageType.REFRESH_VIEW, null));
        

        setChanged();
        notifyObservers(new ObserverMessage(ObserverMessage.messageType.CAD_INFO_MESSAGE,
                "0146: Routed message to " + parsed_dest + "."));
        
        
        //If we sent a message and are looking at a CAD log, add a DETAIL as well
        if(messageSent && getCurrentCADModel().getType() == CADScreenType.II_INCIDENT_INQUIRY) {
            IncidentInquiryModel_obj newIIobj = new IncidentInquiryModel_obj(CADPosition);
                        
            newIIobj.getDetails().add(new IncidentInquiryDetails(getLogInfo(), message, true));             
            newIIobj.setLogNumber(((IncidentInquiryModel)getCurrentCADModel()).getLogNumber());
                
            theCoordinator.commandLineUpdate(newIIobj);         
        }

    }
    
    /**
     * Handles a request to log off the terminal.  All screens are reset with 
     * a BlankScreenModel.  The current screen number is set to ONE.  All 
     * screen updates and messages are cleared.  The model is then updated
     * and Observers are notified ot refresh the view.
     * 
     * @param root (not used)
     */
    public void terminalOffRequest() {

        for(CADScreenNum num : CADScreensMap.keySet()) {
            CADScreensMap.put(num, new BlankScreenModel(num));
        }
            
        currentCADScreenNum = CADScreenNum.ONE;
        
        CADScreenUpdates.clear();
        
        messageMap.clear();
        
        updateCADModel();
        
        setChanged();
        notifyObservers(new ObserverMessage(ObserverMessage.messageType.REFRESH_VIEW, null));
        
    }
    
}