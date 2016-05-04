package tmcsim.client.cadscreens.view;

import java.awt.Color;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import tmcsim.common.CADEnums.TEXT_STYLES;


/**
 * The CADMainView is the base view class for all CAD Screens.  The 
 * Vector of CADDocElement objects contains all text/style pairs
 * that will be used in creating a document for display.  Within the list of 
 * CADDocElements are separate "pages."  CADDocElements that contain an endline 
 * character signify the end of a line.  Each CADMainView "page" is a collection 
 * of these lines.  The static MAIN_CAD_SCREEN_LINES defines how many lines are 
 * displayed on each page.  When the refreshView() method is called, the 
 * text/style pairs for the current page are added to the local Document object.
 * The CADMainView keeps track of the current page, total number of pages, current
 * number lines on the page displayed, and the total number of lines.  
 * 
 * @author
 * @version
 */
public class CADMainView extends Observable {
    
    /** Error Logger.  */
    private static Logger viewLogger = Logger.getLogger("tmcsim.client.cadscreens");
    
    /** Number of viewable lines on a CAD Screen page.  */
    protected final static int MAIN_CAD_SCREEN_LINES = 20;
    
    /** Font size for text displayed on CAD Screen */
    private final static int FONT_SIZE = 15;   
    
    /** Vector of Document elements used to create the display. */
    private Vector<CADDocElement> theDocElements;
    
    /** Document displaying CAD Screen text. */
    private Document  theDoc    = null;

    /** JTextPane displaying CAD Screen text. */
    private JTextPane stylePane = null;
           
    /** Current page number being viewed in this screen. (# => 1)*/
    private int currentPage;
    
    /** Total number of pages for this screen. (# => 1)*/
    private int totalPages; 
    
    /** Total number of lines on all pages for this screen. (# => 0)*/
    private int totalLines;
    
    /** The number of lines displayed on the last page for this screen. (# => 0) */
    private int lastPageLineCount;
    

    /**
     * Constructor.  Initialize data objects and styles.
     * 
     * @param viewDoc Target Document for text display.
     */
    public CADMainView(Document viewDoc) {
        
        theDocElements    = new Vector<CADDocElement>(); 
        
        currentPage       = 1;
        totalPages        = 1;      
        totalLines        = 0;
        lastPageLineCount = 0;      
        
        theDoc = viewDoc;
        initStyles();
        
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
     * Adds a new string to the list of format strings.  The parameter string 
     * is tokenized by '\n' characters.  Each token is considered a new 
     * "line" of text.  Strings without an endline character will be treated 
     * as a text chunk.  Each new line or text chunk is added to the document 
     * with the associated parameter text style.  For every new "line" 
     * being added, the lastPageLineCount is incremented. While the 
     * lastPageLineCount is < the maximum number of MAIN_CAD_SCREEN_LINES, 
     * increment it.  When this becomes false, reset the lastPageLineCount 
     * to 1 and increment the totalPages count.
     *
     * @param text Text string to add to the screen document
     * @param style Text style to associate with the text being added.
     */
    protected void addDocElement(String text, TEXT_STYLES style) {
        
        int beginPos = 0;
        int endlPos  = text.indexOf('\n', 0);
        
        while(endlPos != -1) {
            
            //if current lines less than max per page, add line, else add new page
            if(lastPageLineCount < MAIN_CAD_SCREEN_LINES) 
                lastPageLineCount++;
            else {
                lastPageLineCount = 1;
                totalPages++;
            }           
            
            totalLines++; //keep track of how many total lines have been added
            endlPos++;    //include the endline character
            theDocElements.add(new CADDocElement(text.substring(beginPos, endlPos), style));
            
            beginPos = endlPos; 
            endlPos  = text.indexOf('\n', endlPos);
        }
        
        if(beginPos != text.length()) {
            theDocElements.add(new CADDocElement(text.substring(beginPos, text.length()), style));
        }
    }
    
    /**
     * This method is used to move the screen's displayed information up one 
     * page.  If the current page is greater than one, decrement the 
     * currentPage variable and refresh the view.
     */
    public void pageUp() {
        if(currentPage > 1) {
            currentPage--;
            
            try {
                refreshView(currentPage);
            } catch (Exception e) {
                // due to conditional statement, parameter will never be
                // invalid.
            }
        }
    }   

    /**
     * This method is used to move the screen's displayed information down one 
     * page. If the current page is less than the total number of pages, 
     * increment the currentPage variable and refresh the view.
     */
    public void pageDown() {
        if (currentPage < totalPages) {
            currentPage++;

            try {
                refreshView(currentPage);
            } catch (Exception e) {
                // due to conditional statement, parameter will never be
                // invalid.
            }
        }
    }    
    
    /**
     * Get the current page number.
     * 
     * @return Current page number. (# >= 1)
     */
    public int getCurrentPage() {
        return currentPage;
    }
    
    /**
     * This method refreshes the screen to a specific page, passed in as a 
     * parameter.  The current document is cleared and the page scrolling
     * text is updated according the new page position on the screen. 
     * The document element index for the first line of the new page is found.
     * All text and style pairs in the document elements for the new page are 
     * added to the Document object.  Each pair that is added is checked 
     * for endline character, signifying a new line.  This process ends when 
     * enough pairs have been added to display the maximum number of lines on 
     * the screen, or there are no more document elements.
     * 
     * @param gotoPage Page number to display.  (# >= 1)
     */
   public void refreshView(int gotoPage) {
            
       if(gotoPage > totalPages) {
           currentPage = totalPages;
       }
       else {
           currentPage = gotoPage;
       }
            
        int currIndex = 0;
        int lineCount = 0;
        
        try {
            theDoc.remove(0, theDoc.getLength());

            notifyPageScrolling();
                        
            //If the page being viewed is not the first page, search through
            //the formatStrings vector until the first string of the current
            //page is found.
            if(currentPage != 1) {
                while(currIndex < theDocElements.size() && 
                      lineCount < ((currentPage - 1)*MAIN_CAD_SCREEN_LINES)) {
                            
                    lineCount += countEndLines(theDocElements.get(currIndex).text); 
                    
                    currIndex++;
                }       
            }               
            //else this is the first page   
            lineCount = 0;
       
            //always output the header bar
            theDoc.insertString(theDoc.getLength(), 
                                "==================================================" +
                                "==============================" + "\n", 
                                stylePane.getStyle(TEXT_STYLES.AQUA.style));
                                 
            //start outputting strings from the formatStrings vector, counting
            //how many lines have been output                   
            while(currIndex < theDocElements.size() && 
                  lineCount < MAIN_CAD_SCREEN_LINES) {
                    
               theDoc.insertString(theDoc.getLength(), 
                                   theDocElements.get(currIndex).text,
                                   stylePane.getStyle(theDocElements.get(currIndex).style.style));              
               
               lineCount += countEndLines(theDocElements.get(currIndex).text);         
               
               currIndex++;                  
            }
        } 
        catch (BadLocationException ble) {
            viewLogger.logp(Level.SEVERE, "CADMainView", "refreshView()", 
                    "Exception in updating view document.", ble);
        }
        
    }
   
   /**
    * This method counts the number of endline terminated strings contained 
    * within the parameter string.
    * 
    * @param text A text string.
    * @return The number of lines contained in the parameter text string.
    */
   private int countEndLines(String text) {
       
       int lastPos   = 0;
       int endlCount = 0;
       
       while(text.indexOf("\n", lastPos) != -1) {
           lastPos = text.indexOf("\n", lastPos) + 1;
           endlCount++;
       }
       
       return endlCount;
   }
   
   /**
    * This method determines whether the current CAD Screen can be paged up 
    * or down, then notifies observers with the associated scrolling string.  
    * <br>
    * The following are the possible outcomes:
    * 
    * - Only page up          - "PGUP"
    * - Only page down        - "PGDN"
    * - Page up and page down - "UPDN"
    * - No scrolling          - "    "
    */
   private void notifyPageScrolling() {
       
        String pageScrolling = "     ";    

        if(totalLines < MAIN_CAD_SCREEN_LINES) {
            //smile, only one page
        }
        else if((currentPage)*MAIN_CAD_SCREEN_LINES < totalLines &&
                currentPage == 1) {
            //pgdn
            pageScrolling = "PG DN";
        }
        else if((currentPage)*MAIN_CAD_SCREEN_LINES < totalLines &&
                currentPage > 1) {
            //page up and down
            pageScrolling = "UP DN";
        }
        else if(currentPage > 1) {
            pageScrolling = "PG UP";
        }
        
        setChanged();
        notifyObservers(pageScrolling);
               
   }
    
   /** 
    * This method intializes the stylePane with the styles found in the 
    * TEXT_STYLES enumeration that are needed for main screen display. 
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

        s = stylePane.addStyle(TEXT_STYLES.GREEN.style, regular);
        StyleConstants.ColorConstants.setForeground(s, Color.green);           
        StyleConstants.setFontSize(s, FONT_SIZE); 

        s = stylePane.addStyle(TEXT_STYLES.ORANGE.style, regular);
        StyleConstants.ColorConstants.setForeground(s, Color.orange);           
        StyleConstants.setFontSize(s, FONT_SIZE);

        s = stylePane.addStyle(TEXT_STYLES.REVERSE_CYAN.style, regular);
        StyleConstants.ColorConstants.setForeground(s, Color.black);   
        StyleConstants.ColorConstants.setBackground(s, Color.cyan);           
        StyleConstants.setFontSize(s, FONT_SIZE);
        
    }           
        
    
}