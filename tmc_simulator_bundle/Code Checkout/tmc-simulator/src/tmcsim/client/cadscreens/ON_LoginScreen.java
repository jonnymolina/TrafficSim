package tmcsim.client.cadscreens;

import javax.swing.text.Document;

import tmcsim.client.cadscreens.view.CADMainView;



/**
 * The View component to the ON_LoginScreen CAD Screen. There is no model data associated
 * with this view.  All keyboard input is placed in corresponding input areas on the screen
 * for user name, personal password, and function password.  Some of the CADScreenStyleSheet
 * methods are overloaded in this class to provide for this functionality.  The inherited
 * CADScreenStyleSheet is used to contain the updated view items and create the necessary
 * objects for display.  <OUT OF DATE>
 *
 * @author Matthew Cechini 
 * @version $Revision: 1.4 $ $Date: 2009/04/17 16:27:45 $
 */
public class ON_LoginScreen extends CADMainView {

    /**
     * Count of how many characters have been written to the user identity area.
     */
    private int  userIdentityLength     = 0;

    /**
     * Count of how many characters have been written to the personal password area.
     */
    private int  personalPasswordLength = 0;

    /**
     * Count of how many characters have been written to the function password area.
     */
    private int  functionPasswordLength = 0;
    

    /**
     * Static final int value to designate the cursor's position on the user identity line.
     */
    static final int USER_IDENTITY     = 1;

    /**
     * Static final int value to designate the cursor's position on the function password line.
     */
    static final int FUNCTION_PASSWORD = 2;

    /**
     * Static final int value to designate the cursor's position on the personal password line.
     */
    static final int PERSONAL_PASSWORD = 3;

    /**
     * Keeps track of which line the cursor is currently on.  Initialize to the user identity line.
     */
    private int currentEntryLine = USER_IDENTITY;
    

    /**
     * Array of character positions to begin outputting the text strings.
     */
    private int documentStartingPosition[] = {0, 125, 140, 165};

    /**
     * Array of vector positions to offeset the new characters that have been input.  
     */
    private int formatVectorStartingPosition[] = {0, 2, 8, 14};
    
    public ON_LoginScreen(Document viewDoc) {
        super(viewDoc);                   
                    
    }   
    
    /**
     * Constructor.  Initializes the view with the login screen info.
     *
     * @param newCADScreenNumber The ScreenNumber (1-4) for the instance of this view class.
     * @param newCADScreenType The ScreenType for the instance of this view class.
     
    public ON_LoginScreen(int newCADScreenNumber, int newCADScreenType) {
        String[] initString = {"   SIGN ON\n",
                               "              USER IDENTITY:     ", "_", "_", 
                               "_", "_", "_" , "_", "\n",
                               "              PERSONAL PASSWORD: ", "_", "_", 
                               "_", "_", "_" , "_", "\n",
                               "              FUNCTION PASSWORD: ", "_", "_", 
                               "_", "_", "_" , "_", "\n"};

        String[] initStyles = {"cyan", "cyan", "yellow", "yellow", "yellow", 
                               "yellow", "yellow", "yellow", "yellow", 
                               "cyan", "red", "red", "red", "red", "red", "red", 
                               "red", "cyan", "red", "red", "red", "red", "red", 
                               "red", "red"};
        
        currentCADScreenNumber = newCADScreenNumber;
        CADScreenType = newCADScreenType;
        
        addCADScreenStyleItems(initString, initStyles); 
    }   
    
    
    /**
     * Overloading the CADScreenStyleSheet receiveArrow() method.  Up and down arrows move between input lines.
     *
     * @param direction The public static final int value of the arrow's direction. Defined in CADScreenStyleSheet.
     * @return commandline caret position
     
    public int receiveArrow(int direction) {
        switch(direction) {
            
            case LEFT:
                break;
                    
            case UP:
                break;
                
            case RIGHT:
                break;
                
            case DOWN:
                break;
            
        }   
        return 0;
    }   
    
   /**
    * Overloading the CADSCerenStyleSheet recieveKeyPress() method.  The input character is added to the current input line.
    * If the input line is full, the new character replaces the last character in that line
    *
    * @param newChar The character pressed by the user.
    * @return boolean true always returned.
    
    public boolean receiveKeyPress(char newChar) {
        
        switch (currentEntryLine) {
        
           case USER_IDENTITY:
              if(userIdentityLength < 6) {
                 textAreaUpdateObject.formatVectorPosition = (formatVectorStartingPosition[USER_IDENTITY] + userIdentityLength);
                 textAreaUpdateObject.documentPosition = (documentStartingPosition[USER_IDENTITY] + userIdentityLength);
                 userIdentityLength++;
              }
           
              break;
              
           case PERSONAL_PASSWORD:
              if(personalPasswordLength < 8) {
                 textAreaUpdateObject.formatVectorPosition = (formatVectorStartingPosition[PERSONAL_PASSWORD] + personalPasswordLength);
                 textAreaUpdateObject.documentPosition = (documentStartingPosition[PERSONAL_PASSWORD] + personalPasswordLength);
                 personalPasswordLength++;
              }
           
              break;
              
           case FUNCTION_PASSWORD:
              if(functionPasswordLength < 8) {
                 textAreaUpdateObject.formatVectorPosition = (formatVectorStartingPosition[FUNCTION_PASSWORD] + functionPasswordLength);
                 textAreaUpdateObject.documentPosition = (documentStartingPosition[FUNCTION_PASSWORD] + functionPasswordLength);
                 functionPasswordLength++;
              }
            
              break;    
            
        }
        
        formatStrings.setElementAt(String.valueOf(newChar), textAreaUpdateObject.formatVectorPosition);
        formatStyleTypes.setElementAt("yellow", textAreaUpdateObject.formatVectorPosition);     
        
        textAreaUpdateObject.formatString = String.valueOf(newChar);
        textAreaUpdateObject.formatStyleType = "yellow";
        
        
        return true;
    }
    
    /**
     * Overloading the CADScreenStyleSheet backspace() method.  This method determines if the backspace command 
     * can be executed.  If it can, the last character is removed and true is returned.  If not, the input line 
     * is unchanged and false is returned.  
     *
     * @return boolean true if backspace was successful, false if not.
     
    public boolean backspace() {
        boolean retVal = false;
        switch (currentEntryLine) {
        
           case USER_IDENTITY:
              if(userIdentityLength > 0) {
                 userIdentityLength--;
                 textAreaUpdateObject.formatVectorPosition = (formatVectorStartingPosition[USER_IDENTITY] + userIdentityLength);
                 textAreaUpdateObject.documentPosition = (documentStartingPosition[USER_IDENTITY] + userIdentityLength);                 
                 retVal = true;
              }        
              break;
              
           case PERSONAL_PASSWORD:
              if(personalPasswordLength > 0) {
                 personalPasswordLength--;
                 textAreaUpdateObject.formatVectorPosition = (formatVectorStartingPosition[PERSONAL_PASSWORD] + personalPasswordLength);
                 textAreaUpdateObject.documentPosition = (documentStartingPosition[PERSONAL_PASSWORD] + personalPasswordLength);                 
                 retVal = true;
              }        
              break;
              
           case FUNCTION_PASSWORD:
              if(functionPasswordLength > 0) {
                 functionPasswordLength--;
                 textAreaUpdateObject.formatVectorPosition = (formatVectorStartingPosition[FUNCTION_PASSWORD] + functionPasswordLength);
                 textAreaUpdateObject.documentPosition = (documentStartingPosition[FUNCTION_PASSWORD] + functionPasswordLength);                 
                 retVal = true;
              }         
              break;            
        }
        
        if(retVal = true) {
           formatStrings.setElementAt("_", textAreaUpdateObject.formatVectorPosition);
           formatStyleTypes.setElementAt("yellow", textAreaUpdateObject.formatVectorPosition);      
        
           textAreaUpdateObject.formatString = "_";
           textAreaUpdateObject.formatStyleType = "yellow";
        }       
        
        return retVal;
    }   
    
    /**
     * Overloads the CADScreenStyleSheet cycle() method.  If the ON_Login Screen is being displayed, cycling should be
     * disabled until the user logs in.
     *
     * @return boolean always returns false
     
    public boolean cycle () {
       return true;  //should be false  
    }

    /**
     * Overloads CADScreenStyleSheet isCommandLineActive() method.  Command line input is not used in the LoginScreen,
     * so return the command line is not active, return false.
     *
     * @return boolean always returns true
       
    public boolean isCommandLineActive() {
       return false;    
    }    
   */
}
