package tmcsim.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tmcsim.cadmodels.BlankScreenModel;
import tmcsim.cadmodels.CADScreenModel;
import tmcsim.cadmodels.IncidentBoardModel;
import tmcsim.cadmodels.IncidentInquiryModel;
import tmcsim.cadmodels.IncidentSummaryModel;
import tmcsim.cadmodels.RoutedMessageModel;
import tmcsim.client.cadscreens.IB_IncidentBoard;
import tmcsim.client.cadscreens.II_IncidentInquiry;
import tmcsim.client.cadscreens.SA_IncidentSummary;
import tmcsim.client.cadscreens.TO_RoutedMessage;
import tmcsim.client.cadscreens.view.CADCommandLineView;
import tmcsim.client.cadscreens.view.CADFooterView;
import tmcsim.client.cadscreens.view.CADMainView;
import tmcsim.common.ObserverMessage;
import tmcsim.common.CADEnums.ARROW;
import tmcsim.common.CADEnums.CADScreenNum;
import tmcsim.common.CADEnums.CAD_ERROR;
import tmcsim.common.CADEnums.CAD_KEYS;
import tmcsim.common.CADProtocol.CAD_CLIENT_CMD;

/**
 * The CADClientView class is the view component to the CAD Client application.
 * The CADSCreen is displayed with three separate view components: Command Line,
 * Main Text Area, and Footer.  User input is handled by the Command Line Pane.
 * Any commands are sent to the model which are then transmitted to the
 * CAD Simulator.  The view keeps track of current CAD Screen Number and the
 * current page being displayed on each screen.  This allows for the screen
 * refresh and cycle commands to return the screen to its previous page. 
 * 
 * This view class observers the CADClientModel, listening for updates
 * from the CAD Simulator.  Updates include new CADScreenModel objects notifying 
 * that a new screen is to be displayed.  Other update data includes the current 
 * CAD time, number of routed messages, screen update map, and information messages.  
 * These are all displayed in the CAD Footer.
 * 
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2009/04/20 17:58:27 $ $Revision: 1.7 $
 */
@SuppressWarnings("serial")
public class CADClientView extends JFrame implements KeyListener, Observer {
    
    /** Error Logger. */
    private static Logger cadLogger = Logger.getLogger("tmcsim.client");
    
    /** Reference to the CADClient model object. */
    private CADClientModel theModel = null;
      
    /** View pane for the CAD Screen command line area. */
    private CADCommandLineView CADCommandLinePane = null;
      
    /** View pane for the CAD Screen main area. */
    private CADMainView        CADMainPane        = null;
      
    /** View pane for the CAD Screen footer area. */
    private CADFooterView      CADFooterPane      = null;    
    
    /** CAD Command Line Parser.  */
    private CADCommandParser cmdParser;

    /** Boolean flag to designate whether the shift key is being pressed. */
    private boolean shiftKeyPressed = false;
          
    /** Current CAD Screen number. */
    private CADScreenNum currentScreenNum = null;
    
    /** Map of CADScreen numbers and the current page displayed on that screen. */
    private TreeMap<CADScreenNum, Integer> pageLocationMap = null;
    
    /** Flag designating whether the screen's page location has been saved. */
    private boolean pageLocationSaved = false;

    /**
     * Constructor. Build panes, add key listeners, and set up observer
     * relationship between the footer and main panes.
     * 
     * @param position
     *            The CAD position for this client terminal.
     */
    public CADClientView(CADClientModel mod) {
        super("CAD Client");
        theModel = mod;
        
        
        cmdParser = new CADCommandParser();
        
        //Build and initialize all initial screens to the first page.
        pageLocationMap = new TreeMap<CADScreenNum, Integer>();
        for(CADScreenNum screen : CADScreenNum.values()) 
        {
            pageLocationMap.put(screen, 1);
        }
        
        currentScreenNum = CADScreenNum.orderedList().get(0);
        
        buildPanes();       
        buildPanels();

        cmdLineTextPane.addKeyListener(this);
        mainTextPane.addKeyListener(this);
        footerTextPane.addKeyListener(this);
    

        CADMainPane.addObserver(CADFooterPane);
        
    }
    
    /**
     * Build the command line, main, and footer text panes.
     */
    private void buildPanes() {

        cmdLineTextPane    = new JTextPane();
        cmdLineTextPane.setSize(new Dimension(730, 50)); 
        cmdLineTextPane.setMinimumSize(new Dimension(730, 50));
        cmdLineTextPane.setMaximumSize(new Dimension(730, 50));
        cmdLineTextPane.setBorder(BorderFactory.createLineBorder(Color.black));

        cmdLineTextPane.setBackground(Color.black);
        cmdLineTextPane.setEditable(false);
        CADCommandLinePane = new CADCommandLineView(cmdLineTextPane);
        
        mainTextPane = new JTextPane();
        mainTextPane.setSize(new Dimension(730, 455));
        mainTextPane.setMaximumSize(new Dimension(730, 455));
        mainTextPane.setMinimumSize(new Dimension(730, 455));
        mainTextPane.setBorder(BorderFactory.createLineBorder(Color.black));   
        mainTextPane.setBackground(Color.black);
        mainTextPane.setEditable(false);  
        CADMainPane  = new CADMainView(mainTextPane.getDocument());      

        footerTextPane = new JTextPane();
        footerTextPane.setSize(new Dimension(730, 95)); 
        footerTextPane.setMaximumSize(new Dimension(730, 95));
        footerTextPane.setMinimumSize(new Dimension(730, 95));
        footerTextPane.setBorder(BorderFactory.createLineBorder(Color.black));    
        footerTextPane.setBackground(Color.black);
        footerTextPane.setEditable(false);
        CADFooterPane  = new CADFooterView(footerTextPane.getDocument());
    }
    
    
    /**
     * Build the command line, main, and footer panels.
     */
    private void buildPanels() {
        
        cmdLinePanel = new JPanel();
        cmdLinePanel.setSize(new Dimension(800, 50)); 
        cmdLinePanel.setMinimumSize(new Dimension(800, 50));
        cmdLinePanel.setMaximumSize(new Dimension(800, 50));
        cmdLinePanel.setBackground(Color.black);
        
        Box cmdLineBox = new Box(BoxLayout.Y_AXIS);
        cmdLineBox.add(Box.createHorizontalStrut(35));
        cmdLineBox.add(cmdLineTextPane);
        cmdLineBox.add(Box.createHorizontalStrut(35));
        
        cmdLinePanel.add(cmdLineBox);
        
        //***************************************************//
        
        mainPanel    = new JPanel();
        mainPanel.setSize(new Dimension(800, 455)); 
        mainPanel.setMaximumSize(new Dimension(800, 455));
        mainPanel.setMinimumSize(new Dimension(800, 455));
        mainPanel.setBackground(Color.black);
       
        
        Box mainBox = new Box(BoxLayout.Y_AXIS);
        mainBox.add(Box.createHorizontalStrut(35));
        mainBox.add(mainTextPane);
        mainBox.add(Box.createHorizontalStrut(35));
        
        mainPanel.add(mainBox);

        //***************************************************//
        
        footerPanel  = new JPanel();        
        footerPanel.setSize(new Dimension(800, 95)); 
        footerPanel.setMaximumSize(new Dimension(800, 95));
        footerPanel.setMinimumSize(new Dimension(800, 95));
        footerPanel.setBackground(Color.black);
        
        Box footerBox = new Box(BoxLayout.Y_AXIS);
        footerBox.add(Box.createHorizontalStrut(35));
        footerBox.add(footerTextPane);
        footerBox.add(Box.createHorizontalStrut(35));
        
        footerPanel.add(footerBox);
        
    }
        
    
    /**
     * Method adds the three CAD Screen components (CommandLine, MainTextArea, 
     * Footer) to this view class.  The background is set to black, resized
     * to 800x600, and shown to the screen.
     */
    public void initWindow() {
        add(initBox());

        addKeyListener(this);
        setBackground(Color.black);     
        setSize(new Dimension(800, 600));
        setUndecorated(true);       
        setVisible(true);       
                
    }
    
    /**
     * Method contructs a box out of the three CAD Screen components:
     * CommandLine, MainTextArea, Footer.  The Command Line panel is placed
     * above the main text area panel, which is above the footer panel.
     *
     * @return Box Box containing all three panels.
     */
    public Box initBox() {
        Box theBox = new Box(BoxLayout.Y_AXIS);
        theBox.add(cmdLinePanel);
        theBox.add(mainPanel);
        theBox.add(footerPanel);    
        theBox.setBackground(Color.black);
        
        return theBox;
    }

    /**  
     * This method is called by the implemented KeyListener whenever a key is pressed.  The following
     * keystrokes are listened for in this method.  Each keystroke pressed is referenced by the keycode 
     * for the associated key.  These key codes are defined in the CADProtocol class.
     *
     * 
     *<table cellpadding="2" cellspacing="2" border="1"
     * style="text-align: left; width: 250px;">
     *  <tbody>
     *    <tr>
     *      <th>CAD Protocol Command<br></th>
     *      <th>Action Taken<br></th>
     *    </tr>
     *    <tr>
     *      <td>SHIFT_KEY<br></td>
     *      <td>Set shiftKeyPressed flag to true.<br></td>
     *    </tr>
     *    <tr>
     *      <td>COMMAND_LINE_CLEAR<br></td>
     *      <td>If the shift key is pressed, clear the current CAD Screen's command line.<br></td>
     *    </tr>
     *    <tr>
     *      <td>SCREEN_CLEAR<br></td>
     *      <td>If the shift key is pressed, transmit the SCREEN_CLEAR
     *          command as a TERMINAL_FUNCTION to the CAD Simulator<br></td>
     *   </tr>
     *  </tbody>
     *</table>
     *
     * @param e KeyEvent
     */
    public void keyPressed(KeyEvent evt) { 
       //System.out.println("keyPressed" + evt.getKeyCode());   
       
        switch(CAD_KEYS.fromValue(CAD_KEYS.keyboard_type, evt.getKeyCode())) {  
            case  SHIFT_KEY: 
                shiftKeyPressed = true;
                break; 
                
            case COMMAND_LINE_CLEAR:        
                if(shiftKeyPressed)  {
                    CADCommandLinePane.clearCommandLine();
                }
                break;
    
            case SCREEN_CLEAR:     
                if(shiftKeyPressed) {          
                    try {
                        Document cmdDoc = DocumentBuilderFactory.newInstance()
                            .newDocumentBuilder().newDocument();
                        Element cmdElem = cmdDoc.createElement(
                                CAD_CLIENT_CMD.TERMINAL_FUNCTION.type);                 
                        cmdElem.appendChild(cmdDoc.createTextNode(
                                CAD_KEYS.keyboard_type + ":" + 
                                String.valueOf(evt.getKeyCode())));                 
                        cmdDoc.appendChild(cmdElem);            
                        theModel.transmitCommand(cmdDoc);                           
                    } catch (Exception e) {
                        cadLogger.logp(Level.SEVERE, "CADClientView", "keyPressed()",
                                "Exception in sending screen clear command.", e);
                    }
                }
                break;      
        }
    }
        
    /**
     * This method is called by the implemented KeyListener whenever a key is released.  The following
     * keystrokes are listened for in this method.  Each keystroke released is referenced by the keycode 
     * for the associated key.  These key codes are defined in the CADProtocol class.
     *
     *<table cellpadding="2" cellspacing="2" border="1"
     * style="text-align: left; width: 250px;">
     *  <tbody>
     *    <tr>
     *      <th>CAD Protocol Command<br></th>
     *      <th>Action Taken<br></th>
     *    </tr>
     *    <tr>
     *      <td>SHIFT_KEY<br></td>
     *      <td>Set the shiftKeyPressed flag to false.<br></td>
     *    </tr>
     *    <tr>
     *      <td>CYCLE<br></td>
     *      <td>Preserve the current page number to return the screen to the same location after 
     *          the cycle.  Transmit the current command line to the CAD Simulator with a 
     *          SAVE_COMMAND_LINE message type.  Transmit the CYCLE command as a 
     *          TERMINAL_FUNCTION to the CAD Simulator.<br></td>
     *    </tr>
     *    <tr>
     *      <td>REFRESH<br></td>
     *      <td>Preserve the current page number to return the screen to the same location after 
     *          the refresh.  Transmit the current command line to the CAD Simulator with a 
     *          SAVE_COMMAND_LINE message type.  Transmit the CYCLE command as a 
     *          TERMINAL_FUNCTION to the CAD Simulator.<br></td>
     *    </tr>
     *    <tr>
     *      <td>PGDN</td>
     *      <td>Notify the main pane object of the received page down command.<br></td>
     *    </tr>
     *    <tr>
     *      <td>PGUP</td>
     *      <td>Notify the main pane object of the received page up command.<br> </td>
     *    </tr>
     *    <tr>
     *      <td>LEFT_ARROW</td>
     *      <td>Notify the command line pane object of the received left arrow.<br> </td>
     *    </tr>
     *    <tr>
     *      <td>UP_ARROW</td>
     *      <td>Notify the command line pane object of the received up arrow.<br> </td>
     *    </tr>
     *    <tr>
     *      <td>RIGHT_ARROW</td>
     *      <td>Notify the command line pane object of the received right arrow.<br> </td>
     *    </tr>
     *    <tr>
     *      <td>DOWN_ARROW</td>
     *      <td>Notify the command line pane object of the received down arrow.<br> </td>
     *    </tr>
     *    <tr>
     *      <td>COMMAND_LINE_TX<br> </td>
     *      <td>Parse the current command line and create an XMLWriter with the 
     *          converted XML representation of the command line data.
     *          Transmit the command line as a TERMINAL_CMD_LINE message to 
     *          the CAD Simulator.  Clear the current CAD screen's command line.
     *          If there is an error in parsing, show the corresponding error
     *          message and clear the command line.<br></td>
     *    </tr>
     *    <tr>
     *      <td>NEXT_QUEUE<br></td>
     *      <td>Transmit the NEXT_QUEUE command as a TERMINAL_FUNCTION to the CAD Simulator.<br></td>
     *    </tr>
     *    <tr>
     *      <td>DELETE_QUEUE<br></td>
     *      <td>Transmit the DELETE_QUEUE command as a TERMINAL_FUNCTION to the CAD Simulator.<br></td>
     *    </tr>
     *    <tr>
     *      <td>PREV_QUEUE<br></td>
     *      <td>Transmit the PREV_QUEUE command as a TERMINAL_FUNCTION to the CAD Simulator.<br></td>
     *    </tr>
     *    <tr>
     *      <td>BACKSPACE<br></td>
     *      <td>Notify the command line pane of the received backspace command.<br></td>
     *    </tr>
     *    <tr>
     *      <td>ENTER<br></td>
     *      <td>Currently, do nothing<br></td>
     *    </tr>
     *  </tbody>
     *</table>
     *
     * @param e KeyEvent
     */        
    public void keyReleased(KeyEvent evt) {
       //System.out.println("keyReleased" + evt.getKeyCode());       
        
        switch(CAD_KEYS.fromValue(CAD_KEYS.keyboard_type, evt.getKeyCode())) {  
            
            case  SHIFT_KEY: 
                shiftKeyPressed = false;
                break; 

            case REFRESH:
            case CYCLE: 

                pageLocationSaved = true;
                pageLocationMap.put(currentScreenNum, CADMainPane.getCurrentPage());

                try {
                    Document cmdDoc = DocumentBuilderFactory.newInstance()
                            .newDocumentBuilder().newDocument();
                    Element cmdElem = cmdDoc.createElement(
                            CAD_CLIENT_CMD.SAVE_COMMAND_LINE.type);                 
                    cmdElem.appendChild(cmdDoc.createTextNode(
                            CADCommandLinePane.getCommandLine()));
                    cmdDoc.appendChild(cmdElem);                                    
                    theModel.transmitCommand(cmdDoc);
                                        
                    cmdDoc = DocumentBuilderFactory.newInstance()
                            .newDocumentBuilder().newDocument();
                    cmdElem = cmdDoc.createElement(
                            CAD_CLIENT_CMD.TERMINAL_FUNCTION.type);                 
                    cmdElem.appendChild(cmdDoc.createTextNode(
                            CAD_KEYS.keyboard_type + ":" + 
                            String.valueOf(evt.getKeyCode())));                 
                    cmdDoc.appendChild(cmdElem);            
                    theModel.transmitCommand(cmdDoc);           
                    
                } catch (Exception e) {
                    cadLogger.logp(Level.SEVERE, "CADClientView", 
                            "keyReleased()",
                            "Exception in sending cycle command.", e);
                }
                break;                  
            
            case PGDN:
               
               CADMainPane.pageDown();
               break;
            
            case PGUP: 
            
               CADMainPane.pageUp();
               break;  
               
            case LEFT_ARROW: 
            
               CADCommandLinePane.receiveArrow(ARROW.LEFT);
               break;  
               
            case UP_ARROW: 
            
               CADCommandLinePane.receiveArrow(ARROW.UP);
               break;  
    
            case RIGHT_ARROW: 
            
               CADCommandLinePane.receiveArrow(ARROW.RIGHT);
               break;  
    
            case DOWN_ARROW: 
            
               CADCommandLinePane.receiveArrow(ARROW.DOWN);
               break;                                                  
    
            case COMMAND_LINE_TX:
                   
                try {
                    Document cmdDoc = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder().newDocument();
                    Element cmdElem = cmdDoc.createElement(CAD_CLIENT_CMD.TERMINAL_CMD_LINE.type);
                    
                    cmdParser.parseCommand(cmdElem, CADCommandLinePane.getCommandLine());
                    cmdDoc.appendChild(cmdElem);
                
                    theModel.transmitCommand(cmdDoc);
            
                    CADCommandLinePane.clearCommandLine();

                    pageLocationSaved = false;
                }
                catch (Exception ex) {
                    CADFooterPane.displayInfoMessage(CAD_ERROR.UNAUTH_CMD.message);
                    CADCommandLinePane.clearCommandLine();
                }
                break;  
    
            case PREV_QUEUE:
            case DELETE_QUEUE:
            case NEXT_QUEUE:
                try {
                    Document cmdDoc = DocumentBuilderFactory.newInstance()
                            .newDocumentBuilder().newDocument();
                    Element cmdElem = cmdDoc.createElement(
                            CAD_CLIENT_CMD.TERMINAL_FUNCTION.type);
                                        
                    cmdElem.appendChild(cmdDoc.createTextNode(
                            CAD_KEYS.keyboard_type + ":" + 
                            String.valueOf(evt.getKeyCode())));
                    cmdDoc.appendChild(cmdElem);                    
                    theModel.transmitCommand(cmdDoc);
    
                } catch (Exception e) {
                    cadLogger.logp(Level.SEVERE, "CADClientView", "keyReleased()",
                            "Exception in sending queue command.", e);
                }
                break;

            case BACKSPACE:
               CADCommandLinePane.backspace();
               break;       
            case ENTER:                   
                break;
            default:  
        }
                
    }

    /**
     * This method implements the necessary KeyListener functionality, and is 
     * called whenever a key is typed.  Only letters, numbers, and standard 
     * keyboard symbols, whose ascii value is between 32(inclusive) and 
     * 127(exclusive).  Lower case character are in the range between 97 and 
     * 122, inclusive.  These characters are made uppercase by substracting 
     * 32 from their ascii value.  The ascii value for an upper case character 
     * is 32 less than its lower case representation.  The valid character 
     * is then sent to the CADCommandLinePane.
     *
     * @param e KeyEvent received.
     */
    public void keyTyped(KeyEvent e) {
        //System.out.println("keyTyped" + e.getKeyCode());
        
        char key = e.getKeyChar();
        //if valid character
        if(key >= 32 && key < 127) {
            //lower case letter
            if(key >= 97 && key <= 122)
                key -= 32; //make uppercase
            
            CADCommandLinePane.receiveKeyPress(key);                
            
        }
    }

    /**
     * Observable update method.  The CADClientView class is an observer of the 
     * CADClientModel.  If the model sends a null object, it is signifying that
     * it has shut down.  In this case, an error message should be shown to prompt
     * the user to restart the CAD Client.  If the update object is an
     * ObserverMessage object, the following actions are to be taken:
     * 
     *<table cellpadding="2" cellspacing="2" border="1"
     * style="text-align: left; width: 250px;">
     *  <tbody>
     *    <tr>
     *      <th>Message Type</th>
     *      <th>Message Data</th>
     *      <th>Action Taken</th>
     *    </tr>
     *    <tr>
     *      <td>INCIDENT_INQUIRY<br></td>
     *      <td>IncidentInquiryModel<br></td>
     *      <td>Construct a new II_IncidentInquiry view pane from the model data.
     *          Reset the observer relationship between the footer and main pane.
     *          Update the page location map and update the views with the model data.
     *      </td>
     *    </tr>
     *    <tr>
     *      <td>INCIDENT_SUMMARY<br></td>
     *      <td>IncidentSummaryModel<br></td>
     *      <td>Construct a new SA_IncidentSummary view pane from the model data.
     *          Reset the observer relationship between the footer and main pane.
     *          Update the page location map and update the views with the model data.
     *      </td>
     *    </tr>
     *    <tr>
     *      <td>INCIDENT_BOARD<br></td>
     *      <td>IncidentBoardModel<br></td>
     *      <td>Construct a new IB_IncidentBoard view pane from the model data.
     *          Reset the observer relationship between the footer and main pane.
     *          Update the page location map and update the views with the model data.
     *      </td>
     *    </tr>
     *    <tr>
     *      <td>ROUTED_MESSAGE<br></td>
     *      <td>RoutedMessageModel<br></td>
     *      <td>Construct a new TO_RoutedMessage view pane from the model data.
     *          Reset the observer relationship between the footer and main pane.
     *          Update the page location map and update the views with the model data.
     *      </td>
     *    </tr>
     *    <tr>
     *      <td>BLANK_SCREEN<br></td>
     *      <td>BlankScreenModel<br></td>
     *      <td>Construct a new empty view pane.
     *          Reset the observer relationship between the footer and main pane.
     *          Update the page location map and update the views with the model data.
     *      </td>
     *    </tr>
     *    <tr>
     *      <td>SCREEN_UPDATE<br></td>
     *      <td>TreeMap<CADScreenNum, Boolean><br></td>
     *      <td>Update the footer pane with the new screen updates map.</td>
     *    </tr>
     *    <tr>
     *      <td>TIME_UPDATE<br></td>
     *      <td>Time String<br></td>
     *      <td>Update the footer pane with the new time.</td>
     *    </tr>
     *    <tr>
     *      <td>ROUTED_MESSAGE_COUNT_UPDATE<br></td>
     *      <td># Routed Messages<br></td>
     *      <td>Update the footer pane with the new number of routed messages.</td>
     *    </tr>
     *    <tr>
     *      <td>ROUTED_MESSAGE_UNREAD_UPDATE<br></td>
     *      <td>Unread Routed Messages Boolean<br></td>
     *      <td>Update the footer pane with the unread messages boolean.</td>
     *    </tr>
     *    <tr>
     *      <td>CAD_INFO_MESSAGE<br></td>
     *      <td>Information message<br></td>
     *      <td>Update the footer pane with the new info message.</td>
     *    </tr>
     *  </tbody>
     *</table>
     */
    public void update(Observable o, Object arg) {
        
        
        if(arg == null) 
        {
            JOptionPane.showMessageDialog(this, 
                    "Connection to the CAD Simulator has been lost.  " +
                    "Restart the CAD Client.", "Connection Error", 
                    JOptionPane.ERROR_MESSAGE); 
            return;
        }
        
        ObserverMessage oMessage = (ObserverMessage)arg;
    
        switch(oMessage.type) {
            case INCIDENT_INQUIRY:
                IncidentInquiryModel iiModel = (IncidentInquiryModel)oMessage.value;
                
                CADMainPane   = new II_IncidentInquiry(iiModel, mainTextPane.getDocument());
                CADMainPane.addObserver(CADFooterPane);
                
                if(!pageLocationSaved)
                    pageLocationMap.put(currentScreenNum, CADMainPane.getCurrentPage());
                
                updateViews(iiModel);               
                
                break;
                
            case INCIDENT_SUMMARY:
                IncidentSummaryModel isModel = (IncidentSummaryModel)oMessage.value;
                
                CADMainPane   = new SA_IncidentSummary(isModel, mainTextPane.getDocument());
                CADMainPane.addObserver(CADFooterPane);
                
                if(!pageLocationSaved)
                    pageLocationMap.put(currentScreenNum, CADMainPane.getCurrentPage());
                
                updateViews(isModel);
                break;
                
            case INCIDENT_BOARD:
                IncidentBoardModel ibModel = (IncidentBoardModel)oMessage.value;
            
                CADMainPane   = new IB_IncidentBoard(ibModel, mainTextPane.getDocument());
                CADMainPane.addObserver(CADFooterPane);     
                
                if(!pageLocationSaved)
                    pageLocationMap.put(currentScreenNum, CADMainPane.getCurrentPage());
                
                updateViews(ibModel);
                break;
                
            case ROUTED_MESSAGE:
                RoutedMessageModel rmModel = (RoutedMessageModel)oMessage.value;
            
                CADMainPane = new TO_RoutedMessage(rmModel, mainTextPane.getDocument());
                CADMainPane.addObserver(CADFooterPane);
                
                if(!pageLocationSaved)
                    pageLocationMap.put(currentScreenNum, CADMainPane.getCurrentPage());
                
                updateViews(rmModel);
                break;
                
            case BLANK_SCREEN:
                BlankScreenModel bsModel = (BlankScreenModel)oMessage.value;
                
                CADMainPane = new CADMainView(mainTextPane.getDocument());
                CADMainPane.addObserver(CADFooterPane); 

                if(!pageLocationSaved)
                    pageLocationMap.put(currentScreenNum, CADMainPane.getCurrentPage());
                
                updateViews(bsModel);
                break;              
                
            case SCREEN_UPDATE:
                CADFooterPane.updateStatus(CADScreenModel.updateStringToMap(
                        (String)oMessage.value));
                break;
                
            case TIME_UPDATE:
                CADFooterPane.updateTime((String)oMessage.value);           
                break;
                
            case ROUTED_MESSAGE_COUNT_UPDATE:
                CADFooterPane.updateRoutedMessageCount((Integer)oMessage.value);
                break;
                
            case ROUTED_MESSAGE_UNREAD_UPDATE:
                CADFooterPane.updateUnreadMessages((Boolean)oMessage.value);
                break;
                
            case CAD_INFO_MESSAGE:
                CADFooterPane.displayInfoMessage((String)oMessage.value);
                break;
        }           
    }
    
   
    /**
     * Update the command line and footer pane with model
     * data.  Then refresh all of the views to repaint the screen.
     * 
     * @param model CADScreenModel shown in main Pane.
     */
    private void updateViews(CADScreenModel model) {
                
        currentScreenNum = model.getScreenNum();
        
        CADCommandLinePane.setCommandLine(model.commandLine);

        CADFooterPane.setCADScreenNum(model.getScreenNum());        
        CADFooterPane.updateTime(CADScreenModel.theCADTime);
        CADFooterPane.updateDate(CADScreenModel.theCADDate);        
        CADFooterPane.updateStatus(model.screenUpdateMap);
        CADFooterPane.updateRoutedMessageCount(model.numberRoutedMessages); 
        CADFooterPane.updateUnreadMessages(model.unreadMessages);       
        
        CADCommandLinePane.refreshView();
        CADMainPane.refreshView(pageLocationMap.get(currentScreenNum));
        CADFooterPane.refreshView();
        
    }
    
    /* SWING OBJECTS */       
    
    private JTextPane cmdLineTextPane = null;
    private JTextPane mainTextPane    = null;
    private JTextPane footerTextPane  = null;
    
    private JPanel cmdLinePanel = null;
    private JPanel mainPanel    = null;
    private JPanel footerPanel  = null;
    
    
}    
