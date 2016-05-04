package tmcsim.client.cadscreens.view;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import tmcsim.client.CADCaret;
import tmcsim.common.CADEnums.ARROW;
import tmcsim.common.CADEnums.TEXT_STYLES;


/**
 * CADCommandLineView is the view class used in the CAD Client to
 * display the current command line and process key presses.
 * As characters are added and removed from the command line, the current
 * text is contained within the commandLineText StringBuffer object.  The
 * Document object is used to display the current command line text to the user.
 * A position counter for the Caret is used to keep track of where input
 * is to be received and characters are to be removed.
 * 
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class CADCommandLineView {
    
    /** Error Logger. */
    private static Logger viewLogger = Logger.getLogger("tmcsim.client.cadscreens");

    /** Font size for text displayed on CAD Screen */
    private static int FONT_SIZE = 15;
    
    /** Document displaying CAD Screen text. */
    private Document  theDoc    = null;

    /** JTextPane displaying CAD Screen text. */
    private JTextPane stylePane = null;
    
    /** Current position for text input. */
    private int currentPosition;
    
    /** Current command line text. */
    private StringBuffer commandLineText;    
    
    /** Caret used visually to track the current position of command line input. */
    private CADCaret theCADCaret = null;
    

    /**
     * Constructor.  Initialize data objects and styles.  Create the 
     * CADCaret and add it to the parameter JTextPane.
     * 
     * @param textPane Target JTextPane whose Document object
     * will be written to for command line display.
     */
    public CADCommandLineView(JTextPane textPane) {
            
        currentPosition = 0;
        commandLineText = new StringBuffer(160);
        
        theCADCaret = new CADCaret();
        
        textPane.setCaret(theCADCaret);
        textPane.setCaretColor(Color.yellow);
        
        theDoc = textPane.getDocument();    
        initStyles();   
        
        clearCommandLine();
        theCADCaret.setVisible(true);
    }

  
    /**
     * Sets the current command line with a new string.  Clear
     * the current command line and then call the receiveKeyPress()
     * method with each character in the new string.  The Caret
     * is hidden during these operations to reduce erratic scroling.
     * 
     * @param cmdLine New command line text.
     */
    public void setCommandLine(String cmdLine) 
    {
        try
        {
            clearCommandLine();    
    
            theCADCaret.setVisible(false);
            for(int i = 0; i < cmdLine.length(); i++) 
                receiveKeyPress(cmdLine.charAt(i)); 
            theCADCaret.setVisible(true);
        }
        catch (Exception e)
        {
            viewLogger.log(Level.SEVERE, "Exception occured while setting command line.", e);
        }
    } 
    
    /**
     * Replaces the character at the curent command line position with an 
     * empty space if the command line contains text. Decrement the position
     * counter and move the caret back one space.
     */
    public void backspace() {
            
        try{
            if(commandLineText.length() > 0 && currentPosition > 0) {
                commandLineText.deleteCharAt(currentPosition-1);
                
                theDoc.remove(currentPosition-1, 1);
                
                if(currentPosition-1 == commandLineText.length()) {
                    theDoc.insertString(currentPosition-1,
                            " ",
                            stylePane.getStyle(TEXT_STYLES.YELLOW.style));
                }
                else {
                    theDoc.insertString(commandLineText.length(),
                            " ",
                            stylePane.getStyle(TEXT_STYLES.YELLOW.style));
                }

                currentPosition--;
                theCADCaret.moveCaretBackward(1);
            }
            
        } catch (BadLocationException ble) {
            viewLogger.log(Level.SEVERE, "Exception in updating view document.", ble);
        }       
    }
    

    /**
     * Receive a new key press from user.  If the current position is at least 
     * one character before the end of the current commmand line text, replace 
     * the current character with the new character and move the caret forward
     * one space.  If not, the current position is at the end of the command 
     * line.  If the command line is not full, add the new character at the 
     * end of the command line and move the caret forward one space.  Else the
     * command line is full, replace the last character with the new character 
     * and do not move the caret. 
     *
     * @param inputChar Character being added to command line     
     */
    public void receiveKeyPress(char inputChar) {
        
        try {
            //doesn't matter where we are, replace
            if(currentPosition <= commandLineText.length() - 1 &&
               commandLineText.length() != 0) 
            {
                
                commandLineText.setCharAt(currentPosition, inputChar);              
                theDoc.remove(currentPosition, 1);
                theDoc.insertString(currentPosition,
                                           String.valueOf(inputChar),
                                           stylePane.getStyle(TEXT_STYLES.YELLOW.style));
                currentPosition++;
                theCADCaret.moveCaretForward(1);
                
            }
            //else we are appending, be sure that the StringBuffer isn't full
            else if(commandLineText.length() < 160) 
            {
                commandLineText.append(inputChar);
                
                theDoc.remove(currentPosition, 1);
                theDoc.insertString(currentPosition,
                                    String.valueOf(inputChar),
                                    stylePane.getStyle(TEXT_STYLES.YELLOW.style));              
                currentPosition = commandLineText.length();  
                theCADCaret.moveCaretForward(1);                                
                
            }
            else if(commandLineText.length() == 160) 
            {
                commandLineText.setCharAt(currentPosition-1, inputChar);    
                theDoc.remove(currentPosition-1, 1);
                theDoc.insertString(currentPosition-1,
                                    String.valueOf(inputChar),
                                    stylePane.getStyle(TEXT_STYLES.YELLOW.style));
            }
            
        } catch (BadLocationException ble) {
            viewLogger.log(Level.SEVERE, "Exception in updating view document.", ble);
        }       
    }
    
    /**
     * Receive an arrow press by the user. The following actions are taken 
     * for each direction:<br><br> 
     * <br>
     *<table cellpadding="2" cellspacing="2" border="1"
     * style="text-align: left; width: 250px;">
     *  <tbody>
     *    <tr>
     *      <th>Direction<br></th>
     *      <th>Action Taken<br></th>
     *    </tr>
     *    <tr>
     *      <td>LEFT<br></td>
     *      <td>If command line position is not at the beginning of the command 
     *          line, decrement the position and move the Caret back one space.</td>
     *    </tr>
     *    <tr>
     *      <td>UP<br></td>
     *      <td>If the command line position is at the end of a full(160 character) 
     *          command, move back 81 characters to the end of the previous line.  
     *          If the command line is not full, but the position is on the second 
     *          line, move back 80 characters.  Move the Caret back the same
     *          number of spaces.  (The descrepancy in moving 81 or 
     *          80 charactes has to do with the value of the position counter 
     *          when it is at the end of a full command line)</td>
     *    </tr>
     *    <tr>
     *      <td>RIGHT</td>
     *      <td>If command line position is not at the end of the command line,
     *          increment the position and move the Caret foward one space.</td>
     *    </tr>
     *    <tr>
     *      <td>DOWN</td>
     *      <td>If the command line position is on the first line of the command 
     *          line, and is directly above or to the left of the last character
     *          on the second line, move forward 80 characters.  Else if the 
     *          position is on the first line and there is a second line, move the 
     *          position to the end of the command line.  Move the Caret forward
     *          the same number of spaces.</td>
     *    </tr>
     *  </tbody>
     *</table>
     *
     * @param direction Arrow direction.  
     */
    public void receiveArrow(ARROW direction) {

        switch(direction) {
            
            case LEFT:
                if(currentPosition > 0) { 
                    currentPosition--;
                    theCADCaret.moveCaretBackward(1);   
                }
                break;
                    
            case UP:
                if(currentPosition == 160) {
                    currentPosition -= 81;
                    theCADCaret.moveCaretBackward(81);   
                }
                else if(currentPosition > 80) {
                    currentPosition -= 80;
                    theCADCaret.moveCaretBackward(80);   
                }
                break;
                
            case RIGHT:
                if(currentPosition < commandLineText.length() - 1) {
                    currentPosition++;
                    theCADCaret.moveCaretForward(1);
                }
                break;
                
            case DOWN:
                if(currentPosition < (commandLineText.length() - 80)) {
                    currentPosition += 80;
                    theCADCaret.moveCaretForward(80);
                }
                else if (currentPosition <= 80 && 
                         commandLineText.length() <= currentPosition + 80) {

                    theCADCaret.moveCaretForward(commandLineText.length() - 
                            currentPosition);
                    currentPosition = commandLineText.length();
                }
                break;
            
        }      
    }


    /**
     * Refresh the command line by saving the text, clearing the current
     * command line, and then calling the receiveKeyPress() method for all
     * characters in the saved text.  The Caret is hidden during these 
     * operations to eliminate erratic scrolling.
     */
    public void refreshView() {
            
        String savedCmdLine = commandLineText.toString();
        
        clearCommandLine();

        theCADCaret.setVisible(false);

        for(int pos = 0; pos < savedCmdLine.length(); pos++) {
            receiveKeyPress(savedCmdLine.charAt(pos));
        }
        theCADCaret.setVisible(true);
    }

    /**
     * Get the current command line string.
     * @return Command line string.
     */
    public String getCommandLine() {
        return commandLineText.toString();  
    }  
    
    
    /**
     * Clears the command line by clearing the command line StringBuffer, 
     * clearing the command line document, resetting the position count, and
     * resetting the Caret. The Caret is hidden during these operations to 
     * eliminate erratic scrolling.
     */
    public void clearCommandLine() 
    {
    
        try 
        {
            theCADCaret.setVisible(false);
            currentPosition = 0;
            commandLineText.setLength(0);
            
            while(theDoc.getLength() > 0) 
            {
                theDoc.remove(0, 1);
            }       
            
            for(int i = 0; i < 160; i++) 
            {
                theDoc.insertString(i,
                        " ",
                        stylePane.getStyle(TEXT_STYLES.YELLOW.style));
            }

            theCADCaret.resetCaretPosition();
            theCADCaret.setVisible(true);
        } 
        catch (Exception e) 
        {
            viewLogger.log(Level.SEVERE, "Exception in updating view document.", e);
        }           
        
    }   
    

    /** 
     * This method intializes the stylePane with the styles found in the 
     * TEXT_STYLES enumeration that are needed for command line display. 
     * The value of the static FONT_SIZE is used for text sizing, along
     * with the COURIER font style.
     */
    private void initStyles() {
        
        stylePane = new JTextPane();
        
        Style def = StyleContext.getDefaultStyleContext().
                                        getStyle(StyleContext.DEFAULT_STYLE);
                                        
        Style regular = stylePane.addStyle(TEXT_STYLES.REGULAR.style, def);
        StyleConstants.setFontFamily(def, TEXT_STYLES.COURIER.style);       
        
        Style s = stylePane.addStyle(TEXT_STYLES.YELLOW.style, regular);
        StyleConstants.ColorConstants.setForeground(s, Color.yellow);           
        StyleConstants.setFontSize(s, FONT_SIZE);  

        s = stylePane.addStyle(TEXT_STYLES.GREEN.style, regular);
        StyleConstants.ColorConstants.setForeground(s, Color.green);           
        StyleConstants.setFontSize(s, FONT_SIZE);           
    }
    
}