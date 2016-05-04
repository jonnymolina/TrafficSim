package tmcsim.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tmcsim.cadmodels.BlankScreenModel;
import tmcsim.cadmodels.CADScreenModel;
import tmcsim.cadmodels.IncidentBoardModel;
import tmcsim.cadmodels.IncidentInquiryModel;
import tmcsim.cadmodels.IncidentSummaryModel;
import tmcsim.cadmodels.RoutedMessageModel;
import tmcsim.common.ObserverMessage;
import tmcsim.common.SimulationException;
import tmcsim.common.CADProtocol.CAD_CLIENT_CMD;
import tmcsim.common.CADProtocol.CAD_COMMANDS;
import tmcsim.common.CADProtocol.CAD_SIMULATOR_CMD;
import tmcsim.common.CADProtocol.DATA_TAGS;


/**
 * CADClientModel handles data transmission between the CAD Client and CAD
 * Simulator.  Data is read from the input stream and parsed for message updates
 * from the CAD Simulator.  These updates are described in the receiveObject()
 * method description.  The transmit() method is called to send Document objects
 * to the CAD Simulator.
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2009/04/20 17:58:27 $ $Revision: 1.5 $
 */
public class CADClientModel extends Observable implements Runnable {

    /** Error Logger. */
    private Logger clientLogger = Logger.getLogger("tmcsim.client");
    
    /** Output stream for writing data to the CAD Simulator. */    
    private ObjectOutputStream out = null;

    /** Input Stream for reading data to the CAD Simulator. */
    private ObjectInputStream in = null;    
   
    /** Boolean flag to designate if Runnable object is running. */
    private boolean running = true;


    /**
     * Constructor.
     */
    public CADClientModel() {  }
    
    /**
     * This method sets the streams that are used for communication
     * to and from the CAD Simulator.
     *
     * @param theIS The input stream to read packets from.
     * @param theOS The output stream to write packets to.
     * @throws SimulationException if there is an exception in 
     * creating the Object(Input/Output)Streams.
     */
    public void initializeScreen(InputStream theIS,
                                 OutputStream theOS) throws SimulationException {
        
        try  {
            out = new ObjectOutputStream(theOS);
            in  = new ObjectInputStream(theIS);
        }
        catch (Exception e) {
            throw new SimulationException(SimulationException.CAD_SIM_COMM, e);
        }
    
    }
    
    /**
     * This method register this CAD Client with the CAD Simulator.  The client 
     * is registered with its CAD position number and user ID.  
     * 
     * @param CADPosition CAD Position number.
     * @param userID User ID.
     * @throws SimulationException if there is an exception in registering the client.
     */
    public void register(int CADPosition, String userID) throws SimulationException {
        try {
            Document cmdDoc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
            Element cmdElem = cmdDoc.createElement(CAD_CLIENT_CMD.TERMINAL_REGISTER.type);  
            
            Element posElem = cmdDoc.createElement(DATA_TAGS.POSITION_NUM.tag);
            posElem.appendChild(cmdDoc.createTextNode(String.valueOf(CADPosition)));
            cmdElem.appendChild(posElem);
            
            Element userIDElem = cmdDoc.createElement(DATA_TAGS.USER_ID.tag);
            userIDElem.appendChild(cmdDoc.createTextNode(userID));
            cmdElem.appendChild(userIDElem);

            cmdDoc.appendChild(cmdElem);

            transmitCommand(cmdDoc);

        } catch (Exception e) {
            clientLogger.logp(Level.SEVERE, "CADClientModel", "register()", 
                    "Exception in registering client.", e);
            throw new SimulationException(SimulationException.REGISTER_ERROR, e);
        }
    }
    
    /**
     * Run method that must be defined in Runnable interface.  
     * This method continuously calls the receiveObject() method 
     * to check the input stream for data.  If an exception occurs
     * reading from the input stream, the streams are closed and 
     * observers are notified with a null object that the model has
     * disconnected.
     */
    public void run() {
        
        while(running)  {
            
            try {               
                Thread.sleep(250);
                receiveObject(in.readObject());                 
            }
            catch (EOFException eofe) {
                clientLogger.logp(Level.SEVERE, "CADClientModel", "run()", 
                        "Exception in reading object from input stream.", eofe);
            }
            catch (Exception e) {
                clientLogger.logp(Level.SEVERE, "CADClientModel", "run()", 
                        "Exception in reading object from input stream. " +
                        "Shutting down client.", e);
                
                running = false;
                
                closeStreams();
                
                setChanged();
                notifyObservers();
            }
        }
    }

    
    /**
     * This method writes the parameter Document object to the 
     * ObjectOutputStream which is transmitted eo the CAD Simulator.
     * If there is an Exception in writing to the Socket, the streams
     * are closed and observers are notified with a null object to
     * signify that the connect has been lost.
     * 
     * @param command The Document being transmitted.
     */
    public void transmitCommand(Document command) {     
        
        try {
            out.writeObject(command);
            out.flush();
        }
        catch (IOException ioe) {
            clientLogger.logp(Level.SEVERE, "CADClientModel", "transmitObject()", 
                    "Exception in writing object to the input stream. " +
                    "Shutting down client.", ioe);
            
            running = false;
            
            closeStreams();
            
            setChanged();
            notifyObservers();
        }
    }
    
    /**
     *<table cellpadding="2" cellspacing="2" border="1"
     * style="text-align: left; width: 250px;">
     *  <tbody>
     *    <tr>
     *      <th>CAD Protocol Command<br></th>
     *      <th>Action Taken<br></th>
     *    </tr>
     *    <tr>
     *      <td>UPDATE_SCREEN<br></td>
     *      <td>A completely new window has been received.  Parse the received 
     *          data into an XML Document.  The root node is used to determine
     *          which type of CAD Screen is being shown.  Construct the new
     *          CADScreenModel for that CAD Screem amd notify the view observer with the new
     *          screen type and model.<br></td>
     *    </tr>
     *    <tr>
     *      <td>UPDATE_STATUS</td>
     *      <td>Notify the view observer with the new screen update status value.<br></td>
     *    </tr>
     *    <tr>
     *      <td>UPDATE_TIME</td>
     *      <td>Notify the view observer with the new CAD Time value.<br> </td>
     *    </tr>
     *    <tr>
     *      <td>UPDATE_MSG_COUNT</td>
     *      <td>Notify the view observer with the new queued message count value.<br> </td>
     *    </tr>
     *    <tr>
     *      <td>UPDATE_MSG_UNREAD</td>
     *      <td>Notify the view observer with the new boolean unread messages value.<br> </td>
     *    </tr>
     *    <tr>
     *      <td>CAD_INFO</td>
     *      <td>Notify the view observer with the received informational message value.<br> </td>
     *    </tr>
     *  </tbody>
     *</table>
     *
     */
    private void receiveObject(Object rxData) throws IOException {
        
        try {
            
            CADScreenModel theCADScreenModel = null;
            
            Element root  = ((Document)rxData).getDocumentElement();
        
            switch(CAD_SIMULATOR_CMD.fromString(root.getNodeName())) {
        
                case UPDATE_SCREEN:
                                            
                    Node screenNode = root.getChildNodes().item(0);

                    if(screenNode.getNodeName().equals(CAD_COMMANDS.INCIDENT_INQUIRY.fullName)) {
                        theCADScreenModel = new IncidentInquiryModel(screenNode);               
                        
                        setChanged();
                        notifyObservers(new ObserverMessage(ObserverMessage.messageType.INCIDENT_INQUIRY, theCADScreenModel));  
                    }
                    else if(screenNode.getNodeName().equals(CAD_COMMANDS.INCIDENT_SUMMARY.fullName)) {                  
                        theCADScreenModel = new IncidentSummaryModel(screenNode);
                        
                        setChanged();
                        notifyObservers(new ObserverMessage(ObserverMessage.messageType.INCIDENT_SUMMARY, theCADScreenModel)); 
            
                    }
                    else if(screenNode.getNodeName().equals(CAD_COMMANDS.INCIDENT_BOARD.fullName)) {
                        theCADScreenModel = new IncidentBoardModel(screenNode);
                        
                        setChanged();
                        notifyObservers(new ObserverMessage(ObserverMessage.messageType.INCIDENT_BOARD, theCADScreenModel)); 
                    }   
                    else if(screenNode.getNodeName().equals(CAD_COMMANDS.ROUTED_MESSAGE.fullName)) {
                        theCADScreenModel = new RoutedMessageModel(screenNode);
                        
                        setChanged();
                        notifyObservers(new ObserverMessage(ObserverMessage.messageType.ROUTED_MESSAGE, theCADScreenModel)); 
                    }                       
                    else if(screenNode.getNodeName().equals(CAD_COMMANDS.BLANK_SCREEN.fullName)) {
                        theCADScreenModel = new BlankScreenModel(screenNode);
                    
                        setChanged();
                        notifyObservers(new ObserverMessage(ObserverMessage.messageType.BLANK_SCREEN, theCADScreenModel)); 
                    }
                    break;   
    
                case UPDATE_STATUS:
                    setChanged();
                    notifyObservers(new ObserverMessage(ObserverMessage.messageType.SCREEN_UPDATE, 
                            root.getTextContent()));
                    break;
                
                case UPDATE_TIME:
                    setChanged();
                    notifyObservers(new ObserverMessage(ObserverMessage.messageType.TIME_UPDATE, 
                            root.getTextContent()));
                    break;
                
                case UPDATE_MSG_COUNT:
                    setChanged();
                    notifyObservers(new ObserverMessage(ObserverMessage.messageType.ROUTED_MESSAGE_COUNT_UPDATE,  
                            Integer.parseInt(root.getTextContent())));
                    break;
                
                case UPDATE_MSG_UNREAD:
                    setChanged();
                    notifyObservers(new ObserverMessage(ObserverMessage.messageType.ROUTED_MESSAGE_UNREAD_UPDATE,  
                            Boolean.parseBoolean(root.getTextContent())));
                    break;
               
                case CAD_INFO:
                    setChanged();
                    notifyObservers(new ObserverMessage(ObserverMessage.messageType.CAD_INFO_MESSAGE,  
                            root.getTextContent()));
                    break;
                    
                case APP_CLOSE:
                    closeStreams();
                    System.exit(0);
                    break;
            }           
        } 
        catch (Exception e) 
        {
            clientLogger.logp(Level.SEVERE, "CADClientModel", "receiveObject()", 
                    "Exception in parsing xml object.", e);
        }
    }
    
    /**
     * Close the ouput and input streams.
     */
    private void closeStreams() {
        
        
        try {
            out.close();
        }
        catch (Exception e) {
            clientLogger.logp(Level.SEVERE, "CADClientModel", "closeStreams()", 
                    "Exception in closing output stream.", e);
        }
        
        try {
            in.close();
        }
        catch (Exception e) {
            clientLogger.logp(Level.SEVERE, "CADClientModel", "closeStreams()", 
                    "Exception in closing input stream.", e);
        }
        
    }
}    