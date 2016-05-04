package tmcsim.cadsimulator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import tmcsim.common.CADProtocol;
import tmcsim.common.ObserverMessage;
import tmcsim.common.CADEnums.CAD_KEYS;
import tmcsim.common.CADProtocol.CAD_CLIENT_CMD;
import tmcsim.common.CADProtocol.CAD_COMMANDS;
import tmcsim.common.CADProtocol.CAD_SIMULATOR_CMD;


/**
 * CADSimulatorClient handles communication between the CAD Simulator and 
 * remote CAD Clients.  Each instance of this class communicates with 
 * a CAD Client through a socket.  The run() method continuously checks to see 
 * if data has been received from the client.  If there is data, it is parsed,
 * and the resulting action is performed by the CADScreenManager.  See the
 * receiveObject() method description for more information.  The 
 * CADSimulatorClient is set up as an Observer of the CADScreenManager to listen 
 * for ObserverMessage objects.  For each object received, the appropriate
 * action is taken, resulting in data being transmitted to the CAD Client.
 * See the update() method description for more information.  The 
 * CADScreenManager is set up as an observer of the Coordinator to listen
 * for simulation data updates.
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/14 00:12:38 $ $Revision: 1.5 $
 */
public class CADSimulatorClient extends Thread implements Observer {
    
    /** Error Logger. */
    private static Logger cadLogger = Logger.getLogger("tmcsim.cadsimulator");
    
    /** CADScreenManager object containing the data for managing the CAD Client's view information. */
    private CADScreenManager screenManager;
    
    /** Socket used for communication with the CAD Client. */
    private Socket theSocket;
    
    /** ObjectOutputStream for writing objects to the socket. */
    private ObjectOutputStream out;
    
    /** ObjectInputStream for reading objects from the socket. */
    private ObjectInputStream in;
    
    /**
     * Constructor.  A CADScreenManager is instantiated to manage the output
     * transmitted to the remote CAD Client.  This object is set up as an
     * observer to that manager to listen for data that will be transmitted
     * across the socket.  The CADScreenManager is set up as an observer
     * of the Coordinator.  At construction, streams are created to handle
     * reading and writing to the Socket.  When complete, the sendScreenRefresh()
     * method is called to initialize the client.
     * 
     *
     * @param newSocket The socket to use for data transmission
     * @throws IOException if there is an error in getting the output or input streams
     * from the socket.
     */
    CADSimulatorClient(Socket newSocket) throws IOException{
        
        screenManager = new CADScreenManager(CADSimulator.theCoordinator);      
        CADSimulator.theCoordinator.addObserver(screenManager);
        screenManager.addObserver(this);
        
        
        theSocket = newSocket;        
        out       = new ObjectOutputStream(theSocket.getOutputStream());
        in        = new ObjectInputStream(theSocket.getInputStream());   
   
        //initialize the CAD client
        sendScreenRefresh();
    }
    
    /**
     * Method declaration for the Thread.run() method.  While the thread is not 
     * interrupted, read Objects from the socket and call the receiveObject()
     * method to parse the data.  If there is an IOException in communicating
     * with the client, interrupt this thread and close the streams and Socket.
     */
    public void run() {
        
        try { 
        
            while(!isInterrupted()) {
                receiveObject(in.readObject());         
            }
        } 
        catch (ClassCastException cce) {
            cce.printStackTrace();
        }
        catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        catch (IOException ioe) {                   
            cadLogger.logp(Level.SEVERE, "CADSimulatorClient", "run", 
                    "Error in reading from Client socket: " + 
                    theSocket.getInetAddress() + ", dropping connection.", ioe);
            
            //disconnectClient();
        }
    }
    
    /**
     * This method is called to disconnect from the remote CAD Client.
     * This object's thread is interrupted and the streams and socket
     * are closed.  This object is removed as an observer of the 
     * CADScreenManager and the CADScreenManager is removed as an 
     * observer of the Coordinator.  The viewer is then notified
     * of a disconnecting client.
     */
    protected void disconnectClient() {
        this.interrupt();
        
        try { out.close(); } catch (Exception e) {}
        try { in.close(); } catch (Exception e) {}
        try { theSocket.close(); } catch (Exception e) {}

        screenManager.deleteObserver(this);
        CADSimulator.theCoordinator.removeObserver(screenManager);
        //CADSimulator.theViewer.disconnectClient();
    }
    
    

    /**
     * Observer method.  The update argument is cast to an ObserverMessage 
     * object and the message type is used to define the action taken by the
     * CADSimulatorClient.  Command messages are created in the form of XML
     * Document. The root Element name is value from the CAD_SIMULATOR_CMD 
     * enumeration.  The root text content is the value Object from the
     * received Observer argument. 
     * 
     * The following table describes the messages sent for the ObserverMessage 
     * types.<br>
     * 
     *<table cellpadding="2" cellspacing="2" border="1"
    * style="text-align: left; width: 250px;">
    *  <tbody>
    *    <tr>
    *      <th>Observer Message Type<br></th>
    *      <th>Command Message Type<br></th>
    *      <th>Data Content<br></th>
    *    </tr>
    *    <tr>
    *      <td>SCREEN_UPDATE<br></td>
    *      <td>UPDATE_STATUS</td>
    *      <td>Screen update map String.</td>
    *    </tr>
    *    <tr>
    *      <td>TIME_UPDATE<br></td>
    *      <td>UPDATE_TIME</td>
    *      <td>CAD time String.  (HHMM)</td>
    *    </tr>
    *    <tr>
    *      <td>ROUTED_MESSAGE<br></td>
    *      <td>UPDATE_MSG_COUNT</td>
    *      <td>Number of messages.</td>
    *    </tr>
    *    <tr>
    *      <td><br></td>
    *      <td>UPDATE_MSG_UNREAD</td>
    *      <td>Boolean flag to designate unread messages.</td>
    *    </tr>
    *    <tr>
    *      <td>CAD_INFO_MESSAGE<br></td>
    *      <td>CAD_INFO</td>
    *      <td></td>
    *    </tr>
    *    <tr>
    *      <td>REFRESH_VIEW<br></td>
    *      <td>UPDATE_SCREEN</td>
    *      <td>Current CAD model XML data.</td>
    *    </tr>    
    *  </tbody>
    *</table>
     *
     * @see ObserverMessage
     * @see CAD_SIMULATOR_CMD
     */ 
    public void update(Observable o, Object arg) {
        
        ObserverMessage oMessage = (ObserverMessage)arg;
        CAD_SIMULATOR_CMD simCmd = null;
        
        switch(oMessage.type) {
            case SCREEN_UPDATE:
                simCmd = CAD_SIMULATOR_CMD.UPDATE_STATUS;
                break;
                        
            case TIME_UPDATE:
                simCmd = CAD_SIMULATOR_CMD.UPDATE_TIME;
                break;
                
            case ROUTED_MESSAGE:
                sendRoutedMessageUpdate();
                break;
                
            case CAD_INFO_MESSAGE:
                simCmd = CAD_SIMULATOR_CMD.CAD_INFO;
                break;      
                
            case REFRESH_VIEW:
                sendScreenRefresh();
                break;
        }
        

        if(simCmd != null) {
            try {
                
                Document cmdDoc = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder().newDocument();
                cmdDoc.appendChild(cmdDoc.createElement(
                        simCmd.type));
                cmdDoc.getDocumentElement().appendChild(
                        cmdDoc.createTextNode(oMessage.value.toString()));
                transmitCommand(cmdDoc);
    
            } catch (Exception e) {
                cadLogger.logp(Level.SEVERE, "CADSimulatorClient", "update", 
                        "Error in transmitting a command to client.", e);
            }           
        }
        
    }
    
    /**
     * This method acts as a helper method to create a XML Document
     * update messages with the number of routed messages and 
     * whether there are unread messages for this client.  These
     * two messages are sent separately due to the defined
     * command protocol.
     */
    private void sendRoutedMessageUpdate() {

        try {           
            Document cmdDoc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
            Element docElem = cmdDoc.createElement(
                    CAD_SIMULATOR_CMD.UPDATE_MSG_COUNT.type);
            docElem.appendChild(cmdDoc
                    .createTextNode(String.valueOf(screenManager
                            .getCurrentCADModel().numberRoutedMessages)));
            cmdDoc.appendChild(docElem);
            transmitCommand(cmdDoc);
            

            cmdDoc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
            docElem = cmdDoc.createElement(
                    CAD_SIMULATOR_CMD.UPDATE_MSG_UNREAD.type);
            docElem.appendChild(cmdDoc
                    .createTextNode(String.valueOf(screenManager
                            .getCurrentCADModel().unreadMessages)));
            cmdDoc.appendChild(docElem);
            transmitCommand(cmdDoc);            

        } catch (Exception e) {
            cadLogger.logp(Level.SEVERE, "CADSimulatorClient", 
                    "sendRoutedMessageUpdate", 
                    "Error in transmitting a command to client.", e);
        }   
    }
    
    /**
     * This method acts as a helper method to create an XML Document
     * update message with the current CAD Model's XML information.
     */
    private void sendScreenRefresh() {
        
        try {           
            Document cmdDoc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
            Element docElem = cmdDoc.createElement(
                    CAD_SIMULATOR_CMD.UPDATE_SCREEN.type);
            
            screenManager.getCurrentCADModel().toXML(docElem);
            
            cmdDoc.appendChild(docElem);
            transmitCommand(cmdDoc);

        } catch (Exception e) {
            cadLogger.logp(Level.SEVERE, "CADSimulatorClient", 
                    "sendScreenRefresh", 
                    "Error in transmitting a command to client.", e);
        }               
    }
    
    /**
     * This method parses the data that has been received on the socket.  The
     * Data is cast to an XML Document and the root element determines the
     * data content.  The possible root elements and the corresponding action
     * are explained below. <br>
     * <code>
     * -----------<br>
     * TERMINAL_REGISTER<br>
     * 
     * The CAD position and user ID are parsed from the Element and these 
     * values are sent to the CADScreenManager for use.<br>
     * -----------<br>
     * SAVE_COMMAND_LINE<br>
     * 
     * The current command line text is parsed from the Element and sent
     * to the CADScreenManager for user.
     * <br>
     * -----------<br>
     * TERMINAL_CMD_LINE<br>
     * The CAD command is parsed from the Element and converted to a 
     * CAD_CLIENT_CMD enumeration that is used to call the correct
     * method in the CADScreenManager to perform the command.
     * <br>
     * -----------<br>
     * TERMINAL_FUNCTION<br>
     * 
     * The key value is parsed from the Element and converted to a CAD_KEYS
     * enumeration that is sent to the CADScreenManager to perform the
     * associated action.
     * <br>
     * -----------<br>
     * TERMINATE<br>
     * 
     * <br>
     * -----------<br>
     * </code>
     * @param receivedData String of data received on the socket.
     *
     * @see CADProtocol
     */
    private void receiveObject(Object rxData) throws IOException {
        
        try {   
            
            Element root  = ((Document)rxData).getDocumentElement();
        
            switch(CAD_CLIENT_CMD.fromString(root.getNodeName())) {
            
                case TERMINAL_REGISTER:
                    Node positionNode = root.getChildNodes().item(0);
                    screenManager.setCADPosition(Integer.parseInt(positionNode.getTextContent()));
                    
                    Node userIDNode   = root.getChildNodes().item(1);
                    screenManager.setCADUserID(userIDNode.getTextContent());                            
                    break;          
                 
                case SAVE_COMMAND_LINE:
                    screenManager.receiveCommandLine(root.getTextContent());
                    break;
                case TERMINAL_CMD_LINE:
                    
                    Node commandNode = root.getChildNodes().item(0);

                    switch(CAD_COMMANDS.fromFullName(commandNode.getNodeName())) {
                        case INCIDENT_BOARD:
                            screenManager.incidentBoardRequest((Element)commandNode);
                            break;
                        case INCIDENT_UPDATE:       
                            screenManager.incidentUpdateRequest((Element)commandNode);
                            break;
                        case INCIDENT_INQUIRY:                  
                            screenManager.incidentInquiryRequest((Element)commandNode);
                            break;
                        case INCIDENT_SUMMARY:              
                            screenManager.incidentSummaryRequest((Element)commandNode);
                            break;
                        case ROUTED_MESSAGE:            
                            screenManager.routedMessageRequest((Element)commandNode);
                            break;
                        case ENTER_INCIDENT:            
                            screenManager.enterIncidentRequest((Element)commandNode);
                            break;
                        case TERMINAL_OFF:              
                            screenManager.terminalOffRequest();
                            break;
                        case APP_CLOSE:             
                            
                            try {
                                Document cmdDoc = DocumentBuilderFactory.newInstance()
                                        .newDocumentBuilder().newDocument();
                                cmdDoc.appendChild(cmdDoc.createElement(CAD_SIMULATOR_CMD.
                                        APP_CLOSE.type));                               transmitCommand(cmdDoc);
                    
                            } catch (Exception e) {
                                cadLogger.logp(Level.SEVERE, "CADSimulatorClient", "update", 
                                        "Error in transmitting a command to client.", e);
                            }   

                            //disconnectClient();
                            break;
                        case UNKNOWN:
                            //TODO
                            break;
                    }
                    break;
            
                case TERMINAL_FUNCTION:             
                    screenManager.receiveCommand(CAD_KEYS.fromValue(
                            root.getTextContent().substring(
                                    0, root.getTextContent().indexOf(":")),                         
                            new Integer(root.getTextContent().substring(
                                    root.getTextContent().indexOf(":") + 1))));
                    break;
            }     
        }
        catch (ClassCastException cce) {
            cadLogger.logp(Level.SEVERE, "CADSimulatorClient", 
                    "receiveObject", 
                    "Incorrect object received from client.", cce);
        }
    }   
    
    /**
     * This method transmits the Document command message to the remote 
     * CAD Client.  If an exception occurs in writing to the socket, an 
     * Exception is thrown and socket communication is closed.
     *
     * @param data The data being transmitted
     * @throws IOException if there is an exception in writing to the socket.
     */
    private void transmitCommand(Document data) throws IOException {
  
        try {
            out.writeObject(data);
            out.flush();
        } catch (IOException ioe) {
            cadLogger.logp(Level.SEVERE, "CADSimulatorClient", 
                    "transmitCommand",  "Error writing to Client socket: " + 
                    theSocket.getInetAddress() + ", dropping connection.", ioe);

            //disconnectClient();
        }
            
    }
}  
