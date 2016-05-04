package tmcsim.client.cadscreens.view;


import java.awt.Color;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import tmcsim.common.CADEnums.CADScreenNum;
import tmcsim.common.CADEnums.TEXT_STYLES;


/**
 * CADFooterView is the base view class for the footer information displayed on
 * all CAD Screens.  The footer is used to display the following information to
 * the user:
 * <br>
 * <ul>
 * <li>Information Messages</li>
 * <li>CAD Date</li>
 * <li>CAD Time</li>
 * <li>Screen Updates</li>
 * <li>Page Scrolling</li>
 * <li>Queued Message Count/Update</li>
 * <li>Current CAD Screen Number</li>
 * </ul> 
 * <br>
 * The refreshView() method is used to add text/style pairs for the footer to the 
 * local Document object.  Update methods are provided to modify values from the  
 * previous list.  These updates only modify the portion of the footer that has
 * changed.  This reduces redraw of the entire footer everytime a single value
 * changes.
 * 
 * 
 * @author Matthew Cechini
 * @version
 */
public class CADFooterView implements Observer {
    
    /** Error Logger.  */
    private static Logger viewLogger = Logger.getLogger("tmcsim.client.cadscreens");

    /** Font size for text displayed on CAD Screen */
    private static final int FONT_SIZE = 15;
    
    /** Maximum information message length */
    private static final int MAX_INFO_MESSAGE_LENGTH   = 50;    
    
    /**
     * Enumeration containing document position values for specific text items.
     * @author Matthew Cechini
     */
    private static enum DOCUMENT_POSITIONS {
        /** Document position of the screen update status. */
        UPDATE_STATUS_DOC_POS    (260),
        /** Document position of the CAD date. */
        CAD_DATE_DOC_POS         (245),
        /** Document position of the CAD time. */
        CAD_TIME_DOC_POS         (250),
        /** Document position of the routed messages count. */
        ROUTED_MESSAGE_DOC_POS   (297),
        /** Document position of the page scrolling text. */
        PAGE_SCROLLING_DOC_POS   (272),
        /** Document position of the information message text. */
        INFO_MESSAGE_DOC_POS     (81);
        
        public int pos;
        
        private DOCUMENT_POSITIONS(int p) {
            pos = p;
        }
        
    }

    /** Document displaying CAD Screen text. */
    private Document  theDoc    = null;

    /** JTextPane displaying CAD Screen text. */
    private JTextPane stylePane = null;
    
    /** Current page scrolling text. */
    private String pageScrolling  = null; 
    
    /** Current CAD Date text. */ 
    private String theCADDate     = null;
    
    /** Current CAD Time text. */
    private String theCADTime     = null;
    
    /** Current number of routed messages. */
    private int numberRoutedMessages;

    /** Current flag of whether there are unread routed messages. */
    private boolean unreadRoutedMessages;
    
    /** Current CAD Screen number. */
    private CADScreenNum currentCADScreenNum;

    /** Current update status for all CAD screens. */
    private TreeMap<CADScreenNum, Boolean> screenUpdateMap;
    
    /**
     * Constructor.  Initialize data elements and styles.
     * 
     * @param viewDoc Target Document for text display.
     */
    public CADFooterView(Document viewDoc) {
        
        numberRoutedMessages = 0;
        currentCADScreenNum  = CADScreenNum.ONE;
        unreadRoutedMessages = false;
        screenUpdateMap      = new TreeMap<CADScreenNum, Boolean>();
        
        for(CADScreenNum screen : CADScreenNum.orderedList()) {
            screenUpdateMap.put(screen, false);
        }
        
        theDoc = viewDoc;        
        initStyles();   
            
    }

    /**
     * This method implements the Observer interface method.  The footer
     * is set up to observe the main cad screen and listen for page
     * scrolling text updates.  The update Object argument is cast
     * to a string and the page scrolling text is replaced in the 
     * footer's display Document.
     */
    public void update(Observable o, Object arg) {
        
        pageScrolling = (String)arg;
        
        try {
            if(theDoc.getLength() > 0) {
                theDoc.remove(
                        DOCUMENT_POSITIONS.PAGE_SCROLLING_DOC_POS.pos, 
                        pageScrolling.length());
                
                theDoc.insertString(
                        DOCUMENT_POSITIONS.PAGE_SCROLLING_DOC_POS.pos, 
                        pageScrolling,
                        stylePane.getStyle(TEXT_STYLES.YELLOW.style));
            }
        }
        catch( BadLocationException ble) {
            viewLogger.log(Level.SEVERE, "Exception in updating view document.", ble);
        }
    }
    
    /**
     * Sets the footer's current CADScreen number.
     * 
     * @param screenNum New CADScreen number.
     */
    public void setCADScreenNum(CADScreenNum screenNum) {
        currentCADScreenNum = screenNum;
    }

    /**
     * This method updates the CAD Screen update portion of the footer.
     * The screenUpdateMap is updated with the new update boolean 
     * values from the parameter map.  The footer text is then
     * updated with the new updates.  Screens with an update appear
     * in yellow text, those without in green.    
     *
     * @param newStatus Map of CADScreenNum objects and their boolean update 
     * values. Map entries for all CADScreenNum values are not needed.
     */
    public void updateStatus(TreeMap<CADScreenNum, Boolean> newUpdates) {       

        for(CADScreenNum screen : CADScreenNum.orderedList()) {
            if(newUpdates.get(screen) != null) { 
                screenUpdateMap.put(screen, newUpdates.get(screen));
            }
        }

        try {
            if(theDoc.getLength() > 0) {        
                for(CADScreenNum screen : CADScreenNum.orderedList()) {
                    if(newUpdates.get(screen) != null && newUpdates.get(screen)) {
                        theDoc.remove(
                                DOCUMENT_POSITIONS.UPDATE_STATUS_DOC_POS.pos + 
                                ((screen.intNum-1)*3), 1);
                        theDoc.insertString(
                                DOCUMENT_POSITIONS.UPDATE_STATUS_DOC_POS.pos + 
                                ((screen.intNum-1)*3), 
                                String.valueOf(screen.intNum),
                                stylePane.getStyle(TEXT_STYLES.YELLOW.style));
                    }
                }
            }
        }
        catch (BadLocationException ble) {
            viewLogger.log(Level.SEVERE, "Exception in updating view document.", ble);
        }
    }
    
    /**
     * This method updates the date portion of the footer.  Only
     * those digits in the date that have changed are changed.
     *
     * @param newDate The new date with format: MMYY
     */
    public void updateDate(String newDate) {
        
        try {
            if(theDoc.getLength() > 0) {
                for(int i = 0; i < 4; i++) {             
                    if(theCADDate.charAt(i) != newDate.charAt(i)) {
                        theDoc.remove(
                                DOCUMENT_POSITIONS.CAD_DATE_DOC_POS.pos+(i), 1);
                        theDoc.insertString(
                                DOCUMENT_POSITIONS.CAD_DATE_DOC_POS.pos + (i), 
                                String.valueOf(newDate.charAt(i)),
                                stylePane.getStyle(TEXT_STYLES.GREEN.style));
                    }
                }
            }
        }
        catch (BadLocationException ble) {
            viewLogger.log(Level.SEVERE, "Exception in updating view document.", ble);
        }   
        finally {
            theCADDate = newDate;
        }
        
    }
    
    /**
     * This method updates the time portion of the footer.  Only
     * those digits in the time that have changed are changed.
     *
     * @param newTime The new time with format: HHMM
     */
    public void updateTime(String newTime) {
        
        try {
            if(theDoc.getLength() > 0) {
                for(int i = 0; i < 4; i++) {
             
                    if(theCADTime.charAt(i) != newTime.charAt(i)) {
                        theDoc.remove(
                                DOCUMENT_POSITIONS.CAD_TIME_DOC_POS.pos +(i), 1);
                        theDoc.insertString(
                                DOCUMENT_POSITIONS.CAD_TIME_DOC_POS.pos + (i), 
                                String.valueOf(newTime.charAt(i)),
                                stylePane.getStyle(TEXT_STYLES.GREEN.style));
                    }
                }
            }
        }
        catch (BadLocationException ble) {
            viewLogger.log(Level.SEVERE, "Exception in updating view document.", ble);
        }       
        finally {
            theCADTime = newTime;
        }
    }   
    
    /**
     * This method updates the queued messages portion of the footer.  The
     * parameter boolean value specifies whether there are unread messages
     * or not.  If unread messages exist, the number of messages is highlighted
     * with green and the text is displayed yellow.  If there are no unread
     * messages, the text is displayed unhighlighted and with green text.
     * 
     * @param unread Boolean flag to toggle unread message highlighting.  true
     * if unread messages exist, false if not.
     */
    public void updateUnreadMessages(Boolean unread) {
        unreadRoutedMessages = unread;
        
        try {
            if(theDoc.getLength() > 0) {
            
                theDoc.remove(
                        DOCUMENT_POSITIONS.ROUTED_MESSAGE_DOC_POS.pos, 
                        String.valueOf(numberRoutedMessages).length());
                
                if(unreadRoutedMessages) { 
                    theDoc.insertString(
                            DOCUMENT_POSITIONS.ROUTED_MESSAGE_DOC_POS.pos, 
                            String.valueOf(numberRoutedMessages), 
                            stylePane.getStyle(TEXT_STYLES.GREEN_HIGHLIGHT.style));
                }
                else {
                    theDoc.insertString(
                            DOCUMENT_POSITIONS.ROUTED_MESSAGE_DOC_POS.pos, 
                            String.valueOf(numberRoutedMessages), 
                            stylePane.getStyle(TEXT_STYLES.GREEN.style));
                }
            }
        }
        catch (BadLocationException ble) {
            viewLogger.log(Level.SEVERE, "Exception in updating view document.", ble);
        }
        
    }
    
    /**
     * This method updates the queued messages portion of the footer.  The 
     * parameter value is used to update the number of received routed 
     * messages to this CAD position.  The current number of messages is 
     * removed from the document and replaced with the new number.  If the 
     * number of messages is 0, the text is written in yellow, else the text
     * is written in green.
     *
     * @param newMessageCount Number of routed messages.
     */
    public void updateRoutedMessageCount(Integer newMessageCount) {
        
        numberRoutedMessages = newMessageCount;
        
        try {
            if(theDoc.getLength() > 0) {
            
                theDoc.remove(
                        DOCUMENT_POSITIONS.ROUTED_MESSAGE_DOC_POS.pos, 
                        newMessageCount.toString().length());
                if(numberRoutedMessages > 0) { 
                    theDoc.insertString(
                            DOCUMENT_POSITIONS.ROUTED_MESSAGE_DOC_POS.pos, 
                            String.valueOf(numberRoutedMessages), 
                            stylePane.getStyle(TEXT_STYLES.YELLOW.style));
                }
                else {
                    theDoc.insertString(
                            DOCUMENT_POSITIONS.ROUTED_MESSAGE_DOC_POS.pos, 
                            String.valueOf(numberRoutedMessages), 
                            stylePane.getStyle(TEXT_STYLES.GREEN.style));
                }
            }
        }
        catch (BadLocationException ble) {
            viewLogger.log(Level.SEVERE, "Exception in updating view document.", ble);
        }
    }       
    
    
    /**
     * This method displays an informational message on the info line of the 
     * footer.  The message to be displayed will be truncated to the maximum
     * length which specified by the final static MAX_INFO_MESSAGE_LENGTH.
     * Text output is written in white.
     *
     * @param message Message to display.
     */
    public void displayInfoMessage(String message) {
        try {
            
            theDoc.remove(
                    DOCUMENT_POSITIONS.INFO_MESSAGE_DOC_POS.pos, 
                    MAX_INFO_MESSAGE_LENGTH);
            
            //TODO test truncation
            if(message.length() > MAX_INFO_MESSAGE_LENGTH)
                message = message.substring(0, MAX_INFO_MESSAGE_LENGTH);
            
            while(message.length() < MAX_INFO_MESSAGE_LENGTH)
                message += " ";
            
            theDoc.insertString(
                    DOCUMENT_POSITIONS.INFO_MESSAGE_DOC_POS.pos, 
                    message,
                    stylePane.getStyle(TEXT_STYLES.WHITE.style));
        }
        catch (BadLocationException ble) {
            viewLogger.log(Level.SEVERE, "Exception in updating view document.", ble);
        }
    }
    
    
    /**
     * This method clears the display Document object and then adds text/style
     * pairs to create the footer display document. A leading line of 80 '=' 
     * characters in aqua is added for CAD Screen formatting, followed by a 
     * blank line used to display info messages. The footer items then added in
     * the following order:
     * 
     * <ul>
     * <li>CAD Date</li>
     * <li>CAD Time</li>
     * <li>Screen Updates</li>
     * <li>Page Scrolling</li>
     * <li>Queued Message Count/Update</li>
     * <li>Current CAD Screen Number</li>
     * </ul>
     */ 
    public void refreshView() { 
        try {
                    
            theDoc.remove(0, theDoc.getLength());
         
            //Load the text pane with styled text.
            
            theDoc.insertString(theDoc.getLength(), "==========================" +
                    "======================================================\n", 
                    stylePane.getStyle(TEXT_STYLES.AQUA.style));
            theDoc.insertString(theDoc.getLength(), rPad("", 80) + "\n", 
                    stylePane.getStyle(TEXT_STYLES.BLACK.style));       
            theDoc.insertString(theDoc.getLength(), rPad("", 80) + "\n", 
                    stylePane.getStyle(TEXT_STYLES.AQUA.style));    
            theDoc.insertString(theDoc.getLength(), theCADDate +"/",
                    stylePane.getStyle(TEXT_STYLES.GREEN.style));   
            theDoc.insertString(theDoc.getLength(), rPad(theCADTime, 5),
                    stylePane.getStyle(TEXT_STYLES.GREEN.style));                                
                                                             
            theDoc.insertString(theDoc.getLength(), "REF: ",
                    stylePane.getStyle(TEXT_STYLES.GREEN.style));   

            for(CADScreenNum screen : CADScreenNum.orderedList()) {
                if(screenUpdateMap.get(screen)) {
                      theDoc.insertString(theDoc.getLength(), String.valueOf(screen.intNum), 
                              stylePane.getStyle(TEXT_STYLES.YELLOW.style));        
                }
                else {
                      theDoc.insertString(theDoc.getLength(), String.valueOf(screen.intNum), 
                              stylePane.getStyle(TEXT_STYLES.GREEN.style)); 
                }
                theDoc.insertString(theDoc.getLength(), "  ",
                        stylePane.getStyle(TEXT_STYLES.GREEN.style));   
            }
                                                                          
            theDoc.insertString(theDoc.getLength(), pageScrolling,
                                stylePane.getStyle(TEXT_STYLES.YELLOW.style));                                                               
    
            theDoc.insertString(theDoc.getLength(), rPad(lPad("CAD: 0  0  0", 18), 20), 
                    stylePane.getStyle(TEXT_STYLES.GREEN.style));                   
            
            if(unreadRoutedMessages)                 
                theDoc.insertString(theDoc.getLength(), String.valueOf(numberRoutedMessages),
                        stylePane.getStyle(TEXT_STYLES.GREEN_HIGHLIGHT.style));                 
            else 
                theDoc.insertString(theDoc.getLength(), String.valueOf(numberRoutedMessages),
                        stylePane.getStyle(TEXT_STYLES.GREEN.style));
            
            theDoc.insertString(theDoc.getLength(), lPad("CLETS: 0 0", 13), 
                                       stylePane.getStyle(TEXT_STYLES.GREEN.style));
            theDoc.insertString(theDoc.getLength(), rPad(lPad("MIS: 0", 8), 10), 
                       stylePane.getStyle(TEXT_STYLES.GREEN.style));
                             
            theDoc.insertString(theDoc.getLength(), String.valueOf(currentCADScreenNum.intNum), 
                                       stylePane.getStyle("red"));                               
                      
        } 
        catch (BadLocationException ble) {
            viewLogger.log(Level.SEVERE, "Exception in updating view document.", ble);
        }
        
    }   
        
    /**
     * Method pads the parameter string with spaces to the right
     * of the string until the string length is equal to the parameter
     * length.  If the parameter width is less than the length of the
     * parameter String, no action is taken.
     * 
     * @param str String to pad.
     * @param width Desired string length.
     * @return Padded string.
     */
    protected String rPad(String str, int width) 
    {
        StringBuffer buf = new StringBuffer(str);
        while(buf.length() < width)
            buf.append(" ");
        
        return buf.toString();      
    }
    
    /**
     * Method pads the parameter string with spaces to the left
     * of the string until the string length is equal to the parameter
     * length.  If the parameter width is less than the length of the
     * parameter String, no action is taken.
     * 
     * @param str String to pad.
     * @param width Desired string length.
     * @return Padded string.
     */    
    protected String lPad(String str, int width) 
    {
        StringBuffer buf = new StringBuffer(str);
        while(buf.length() < width)
            buf.insert(0, " ");
        
        return buf.toString();      
    }
    
    /** 
     * This method intializes the stylePane with the styles found in the 
     * TEXT_STYLES enumeration that are needed for footer display. 
     * The value of the static FONT_SIZE is used for text sizing, along
     * with the COURIER font style.
     */
    private void initStyles() {
              
        stylePane = new JTextPane();
              
        Style def = StyleContext.getDefaultStyleContext().
                                        getStyle(StyleContext.DEFAULT_STYLE);
                                        
        Style regular = stylePane.addStyle(TEXT_STYLES.REGULAR.style, def);
        StyleConstants.setFontFamily(def, TEXT_STYLES.COURIER.style);                                        
                                        
        Style s = stylePane.addStyle(TEXT_STYLES.ITALIC.style, regular);
        StyleConstants.setItalic(s, true);

        s = stylePane.addStyle(TEXT_STYLES.BOLD.style, regular);
        StyleConstants.setBold(s, true);
        
        s = stylePane.addStyle(TEXT_STYLES.BLUE.style, regular);
        StyleConstants.ColorConstants.setForeground(s, Color.blue);
        StyleConstants.setFontSize(s, FONT_SIZE);
        
        s = stylePane.addStyle(TEXT_STYLES.AQUA.style, regular);
        StyleConstants.ColorConstants.setForeground(s, new Color(0,128,128));
        StyleConstants.setFontSize(s, FONT_SIZE);  
        
        s = stylePane.addStyle(TEXT_STYLES.RED.style, regular);
        StyleConstants.ColorConstants.setForeground(s, Color.red);           
        StyleConstants.setFontSize(s, FONT_SIZE);
        
        s = stylePane.addStyle(TEXT_STYLES.GRAY.style, regular);
        StyleConstants.ColorConstants.setForeground(s, Color.gray);           
        StyleConstants.setFontSize(s, FONT_SIZE);
        
        s = stylePane.addStyle(TEXT_STYLES.CYAN.style, regular);
        StyleConstants.ColorConstants.setForeground(s, Color.cyan);           
        StyleConstants.setFontSize(s, FONT_SIZE);                

        s = stylePane.addStyle(TEXT_STYLES.YELLOW.style, regular);
        StyleConstants.ColorConstants.setForeground(s, Color.yellow);           
        StyleConstants.setFontSize(s, FONT_SIZE);  

        s = stylePane.addStyle(TEXT_STYLES.WHITE.style, regular);
        StyleConstants.ColorConstants.setForeground(s, Color.white);           
        StyleConstants.setFontSize(s, FONT_SIZE); 

        s = stylePane.addStyle(TEXT_STYLES.GREEN.style, regular);
        StyleConstants.ColorConstants.setForeground(s, Color.green);           
        StyleConstants.setFontSize(s, FONT_SIZE); 
        
        s = stylePane.addStyle(TEXT_STYLES.REVERSE_GREEN.style, regular);         
        StyleConstants.ColorConstants.setBackground(s, Color.green);           
        StyleConstants.ColorConstants.setForeground(s, Color.black);  
        StyleConstants.setFontSize(s, FONT_SIZE);    

        s = stylePane.addStyle(TEXT_STYLES.GREEN_HIGHLIGHT.style, regular);         
        StyleConstants.ColorConstants.setBackground(s, Color.green);           
        StyleConstants.ColorConstants.setForeground(s, Color.yellow);  
        StyleConstants.setFontSize(s, FONT_SIZE);        
        
    }
}