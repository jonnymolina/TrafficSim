package tmcsim.cadmodels; 

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tmcsim.common.ScriptException;
import tmcsim.common.CADEnums.CADScreenNum;
import tmcsim.common.CADEnums.CADScreenType;
import tmcsim.common.CADProtocol.CAD_COMMANDS;
import tmcsim.common.CADProtocol.DATA_TAGS;


/**
 * RoutedMessageModel is a CADScreenModel object containing data that is 
 * displayed in the RoutedMessage CAD Screen.  Data included in this
 * object includes a list of CADRoutedMessages, the index of the current
 * message item, and a boolean flag to designate whether the current message 
 * has been deleted.  The addModelObject() method is used to update the model 
 * data with new information.  The nextQueue(), deleteQueue(), and prevQueue() 
 * methods are used to control which message is the "current" message. <br/>
 * <br/>
 * This element parses and creates the following XML schema in its toXML() and
 * fromXML() methods.  The ROOT element is the parameter for those methods.  
 * See the class description for the CADScreenModel XML schema.<br/>
 * <ROOT>
 *    <ROUTED_MESSAGE>
 *       <BASE_MODEL_INFO/>
 *       <ORIGIN/>
 *       <DESTINATION/>
 *       <MESSAGE/>
 *    </ROUTED_MESSAGE>
 * </ROOT>
 * 
 * @see CADScreenModel
 * @author Matthew Cechini
 * @version
 */
public class RoutedMessageModel extends CADScreenModel {
    
    /** LinkedList containing received routed messages. */
    private LinkedList<CADRoutedMessage> messageList = null;
    
    /**
     * A manually incremented and decremented iterating count.  Initial value
     * is 0 to point to the first element in the messageList.  As the list is 
     * traversed, added to, and removed from, this count always contains the value
     * of the current message that is being displayed.
     */
    private int currentListItem = 0;
    
    /**
     * Flag to designate whether the current message has been deleted by the
     * user.  The currentListItem counter is not updated when a queue is
     * deleted until nextQueue() or prevQueue() is called.  This allows a
     * message to remain on the screen after it has been deleted.
     */
    private boolean messageDeleted = false;
    
    /**
     * Constructor.
     * 
     * @param num CADScreenNum for this model.
     * @param messages New list of messags.
     */
    public RoutedMessageModel(CADScreenNum num, LinkedList<CADRoutedMessage> messages) {
        super(CADScreenType.TO_ROUTED_MESSAGE, num);
        
        messageList = new LinkedList<CADRoutedMessage>();
        messageList.addAll(messages);
    }
    
    /**
     * Constructor.  Parse the model data from the parameter node.
     * 
     * @param newNode XML node containing model data.
     * @throws ScriptException if there is an error in parsing the node.
     */
    public RoutedMessageModel(Node newNode) throws ScriptException {
        super(CADScreenType.TO_ROUTED_MESSAGE, CADScreenNum.ONE);

        messageList = new LinkedList<CADRoutedMessage>();
        
        fromXML(newNode);   
    }

    /**
     * Returns the number of routed messages received to this CAD Client model.
     *
     * @return int number of messages that have been sent to this terminal and
     * retreived from the coordinator.
     */
    public int getMessageCount() {      
        return messageList.size();  
    }
    
    /**
     * Append a new message to the local linked list of CADRoutedMessages.
     *
     * @param updateMessages Vector of CADRoutedMessage objects that have been
     * sent to this CAD terminal.
     */
    public void addModelObject(Object o) {              
        messageList.add((CADRoutedMessage)o);
    } 

    /**
     * Method returns the routed message that was last viewed, or if this is the first
     * time a method is being viewed, then the first message will be returned.
     * This message may only be called if the numberOfMessages() returns a value
     * greater than 0.  If there are no messages, this method will throw an 
     * IndexOutOfBoundsException.
     *
     * @return CADRoutedMessage
     */ 
    public CADRoutedMessage getCurrentMessage() throws IndexOutOfBoundsException {      
        
        return messageList.get(currentListItem);    
    }
        
    /**
     * Method is called to advance to the next queued message object.  Update the 
     * message list and current message counter if a message has been deleted.  If the
     * currentListItem count is at the end of the message list, then start over
     * at the beginning, otherwise increase the count by one.
     * This method is only to be called if numberOfMessages() returns a value
     * greater than 0.
     */
    public boolean nextQueue() {

        //update the counter if a the previous queue message was deleted
        if(messageDeleted) {
            messageList.remove(currentListItem);
            currentListItem--;
            
            if(currentListItem > messageList.size() - 1 ||
               currentListItem < 0) {
                currentListItem = 0;
            }
            
            messageDeleted = false;
        }
        
        if(messageList.size() > 0) {
            if(currentListItem == messageList.size() - 1) 
                currentListItem = 0;
            else 
                currentListItem++;
                
            return true;
        }
        else
            return false;
    }
    
    /**
     * If the message being deleted is currently being viewed, set the messageDeleted
     * flag to true so that the queued message will be deleted when nextQueue()
     * or prevQueue() is called next.  Else, if the message being deleted
     * does exist in this model's list of messages, remove that message.  Update
     * the currentListItem counter if the message deleted is at a position
     * prior to the current message.
     * 
     * @param delMsg Queue message being deleted
     */
    public void deleteQueue(CADRoutedMessage delMsg) {
        
        int msgIndex = messageList.indexOf(delMsg);
        
        if(msgIndex == currentListItem) {
            messageDeleted = true;
        }
        else if(msgIndex != -1) {           
            messageList.remove(delMsg);

            if(currentListItem > msgIndex)
                currentListItem--;
        }
        //else msgIndex == -1  Msg already removed
    }
    
    /**
     * Method is called to back up to the previous queued message object.  
     * Update the message list and current message counter if a message
     * has been deleted.  If the currentListItem count is at the beginning 
     * of the message list, then move to the end, otherwise decrease the 
     * count by one. This method is only to be called if numberOfMessages() 
     * returns a value greater than 0.
     */
    public boolean prevQueue() {
        
        //update the counter if a the previous queue message was deleted
        if(messageDeleted) {
            messageList.remove(currentListItem);
            currentListItem--;
            
            if(currentListItem > messageList.size() - 1 ||
               currentListItem < 0){
                currentListItem = 0;
            }
            
            messageDeleted = false;
        }
        
        if(messageList.size() > 0) {
            if(currentListItem == 0) 
                currentListItem = messageList.size() - 1;
            else 
                currentListItem--;
                    
            return true;
        }
        else
            return false;           
    }
    
    /**
     * Determine if this routed message is a forwarded incident update.
     * @return True if this message is an incident update, false if not.
     */
    public boolean isIncidentUpdate() {
        return messageList.get(currentListItem).incidentUpdate;
    }
    
    public void toXML(Element currElem) {
        
        //if this is an update, then the xmloutput is contained within
        //the message text
        if(getCurrentMessage().incidentUpdate) {

        }
        //else send the current message only
        else {          
            
            Document theDoc = currElem.getOwnerDocument();
                    
            Element modelElem = theDoc.createElement(CAD_COMMANDS.ROUTED_MESSAGE.fullName);
            
            baseToXML(modelElem);
            
            Element originElem = theDoc.createElement(DATA_TAGS.ORIGIN.tag);
            originElem.appendChild(theDoc.createTextNode(String.valueOf(getCurrentMessage().fromPosition)));
            modelElem.appendChild(originElem);

            Element destElem = theDoc.createElement(DATA_TAGS.DESTINATION.tag);
            destElem.appendChild(theDoc.createTextNode(String.valueOf(getCurrentMessage().toPosition)));
            modelElem.appendChild(destElem);

            Element msgElem = theDoc.createElement(DATA_TAGS.ORIGIN.tag);
            msgElem.appendChild(theDoc.createTextNode(getCurrentMessage().message));
            modelElem.appendChild(msgElem);
            
            currElem.appendChild(modelElem);
        }
    }
    
    public void fromXML(Node modelNode) throws ScriptException {
        messageList.clear();
        currentListItem = 0;
        
        CADRoutedMessage newMessage = new CADRoutedMessage(0, 0, "", false);
        
        modelNode = modelNode.getFirstChild();
        
        baseFromXML(modelNode);
        
        modelNode = modelNode.getNextSibling();
        newMessage.fromPosition = Integer.parseInt(modelNode.getTextContent());

        modelNode = modelNode.getNextSibling();
        newMessage.toPosition = Integer.parseInt(modelNode.getTextContent());

        modelNode = modelNode.getNextSibling();
        newMessage.message = modelNode.getTextContent();

        
        messageList.add(newMessage);
        
    }

}