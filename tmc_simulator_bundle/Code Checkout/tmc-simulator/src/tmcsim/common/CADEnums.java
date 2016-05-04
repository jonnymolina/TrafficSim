package tmcsim.common;

import java.io.Serializable;
import java.util.Vector;


/**
 * CADEnums contains enumerations used to encapsulate specific lists of data.
 * 
 * @author Jonathan Molina
 * @author Matthew Cechini
 * @version
 */
public class CADEnums {
    
    /**
     * Enumeration representing the possible page numbers in the
     * CAD screen.  The next() method will cycle through the pages in the following
     * order: ONE - TWO - THREE - FOUR - (repeat).  Each enumeration has an integer
     * value equal to that name of the object.
     * @author Matthew Cechini
     */
    public static enum CADScreenNum 
    { 
        ONE(1), 
        TWO(2), 
        THREE(3), 
        FOUR(4);
   
        /** Numerical value of the CADScreen number. */
        public int intNum;
        
        /**
         * Constructor.
         * @param i Screen number value.
         */
        CADScreenNum(int i) {
            intNum = i;
        }
        
        /**
         * Returns the CADScreenNum enumeration value which follows the current value.
         * @return Next CADScreen number.
         */
        public CADScreenNum next() {
            switch(this) {
                case ONE:
                    return TWO;
                case TWO:
                    return THREE;
                case THREE:
                    return FOUR;
                case FOUR:
                default:
                    return ONE;
                
            }
        }               
     
        /**
         * Returns the CADScreenNum enumeration value which has a integer
         * value that matches the parameter value.
         * @param val Page number.
         * @throws ScriptException if the parameter value is invalid.
         * @return CADScreen for page number.
         */
        public static CADScreenNum fromValue(int val) {
            
            for(CADScreenNum screen : values()) {
                if(screen.intNum == val)
                    return screen;
            }
            
            return ONE;
        }       
        
        /**
         * Return an ordered list of CADScreenNum objects.  The list is ordered from
         * ONE through FOUR.
         * 
         * @return Ordered list of CADScreenNum objects.
         */
        public static Vector<CADScreenNum> orderedList() {
            Vector<CADScreenNum> orderedList = new Vector<CADScreenNum>();
            
            orderedList.add(ONE);
            orderedList.add(TWO);
            orderedList.add(THREE);
            orderedList.add(FOUR);
            
            return orderedList;
        }
    }

    /**
     * Enumeration representing the possible CAD Screens that are viewable
     * in the CAD.  
     * @author Matthew Cechini
     */
    public static enum CADScreenType { 
        BLANKSCREEN, 
        II_INCIDENT_INQUIRY, 
        ON_LOGIN_SCREEN,
        SA_INCIDENT_SUMMARY, 
        IB_INCIDENT_BOARD, 
        TO_ROUTED_MESSAGE 
    };

    /**
     * Enumeration containing the key code values for the keyboard keys that 
     * are mapped to CAD functions. 
     * @author Matthew Cechini
     */
    public static enum CAD_KEYS implements Serializable {
        COMMAND_LINE_TX    (112),  
        PGUP               (36),
        LEFT_ARROW         (37),
        UP_ARROW           (38),
        RIGHT_ARROW        (39),
        DOWN_ARROW         (40),
        PGDN               (35),
        REFRESH            (34),
        CYCLE              (33),
        BACKSPACE          (8),
        PREV_QUEUE         (121), 
        DELETE_QUEUE       (120),
        NEXT_QUEUE         (119),
        SCREEN_CLEAR       (123),    
        COMMAND_LINE_CLEAR (122),    
        SHIFT_KEY          (16),
        ENTER              (10),
        UNKNOWN            (-1);
        
        public int value;
        
        //{CAD, STD}
        public static String keyboard_type = "STD";   
        
        private CAD_KEYS(int v) {
            value         = v;          
        }
        
        /**
         * Returns the CAD_KEYS enumeration value which has a key
         * value that matches the parameter value.
         * @param v Key value.
         * @throws ScriptException if the parameter value is invalid.
         * @return CAD_KEYS for the parameter value.
         */ 
        public static CAD_KEYS fromValue(String kbd_type, int v) {
            
            String prevType = null;
            
            try {
                //Preserve previous keyboard type, if not same as parameter.
                if(!kbd_type.equals(keyboard_type)) {
                    prevType = keyboard_type;
                    
                    if(kbd_type.equals("CAD")) {
                        setupCADKeyboard();
                    }
                    else {
                        setupStandardKeyboard();
                    }
                }
                
                //Find matching key.
                for(CAD_KEYS key : values()) {
                    if(key.value == v)
                        return key;
                }               
                
                return UNKNOWN;
            } 
            finally {
                //Restore previous keyboard type, if saved.
                if(prevType != null) {
                    if(prevType.equals("CAD")) {
                        setupCADKeyboard();
                    }
                    else {
                        setupStandardKeyboard();
                    }
                }
            }
        }
        
        public static void setupCADKeyboard() {
            SCREEN_CLEAR.value       = 61451;
            COMMAND_LINE_CLEAR.value = 61450;
            COMMAND_LINE_TX.value    = 61447;

            keyboard_type = "CAD";
        }
        
        public static void setupStandardKeyboard() {
            SCREEN_CLEAR.value       = 123;
            COMMAND_LINE_CLEAR.value = 122;
            COMMAND_LINE_TX.value    = 112;
            
            keyboard_type = "STD";
        }
        
    };
                                       
    /**
     * Enumeration representing the possible directional arrows that may be pressed.  
     * @author Matthew Cechini
     */               
    public static enum ARROW { 
        LEFT, 
        RIGHT, 
        UP, 
        DOWN };

    /**
     * Enumeration representing possible errors that may occur as a result of CAD
     * command line parsing.  Each object has an associated text message for display. 
     * @author Matthew Cechini
     */
    public static enum CAD_ERROR { 
        UNAUTH_CMD  ("0002: Unauthorized Command"), 
        NO_LOG_NUM  ("0744: Must provide log # when no log is on display"),
        KYBD_LOCK   ("0796: KYBD Successfuly Locked"),
        INVALID_LOG ("0753: Invalid Log Number");
        
        /** Error message. */
        public String message;
        
        /**
         * Constructor.
         * @param m Error message.
         */
        CAD_ERROR(String m) {
            message = m;    
        }
            
    }

    /**
     * Enumeration representing possible text styles that will be used in CAD screen
     * document creation.
     * @author Matthew Cechini
     */
    public static enum TEXT_STYLES { 
        BLUE          ("blue"),
        BLACK         ("black"),
        AQUA          ("aqua"),
        RED           ("red"),
        GRAY          ("gray"),
        CYAN          ("cyan"),
        YELLOW        ("yellow"),
        WHITE         ("white"),
        GREEN         ("green"),
        ORANGE        ("orange"),
        REVERSE_GREEN ("rev_green"),
        REVERSE_CYAN  ("rev_cyan"),
        GREEN_HIGHLIGHT ("green_highlight"),
        
        REGULAR ("regular"),
        ITALIC  ("italic"),
        BOLD    ("bold"),
        
        COURIER ("Courier");    
            
        /** Style string. */
        public String style;
        
        /**
         * Constructor.
         * @param s Style string.
         */
        TEXT_STYLES(String s) {
            style = s;  
        }
    }

    /**
     * Enumeration representing all possible states that the script may be in.
     * @author Matthew Cechini
     */
    public static enum SCRIPT_STATUS { 
        NO_SCRIPT, 
        SCRIPT_STOPPED_NOT_STARTED, 
        SCRIPT_PAUSED_STARTED, 
        SCRIPT_RUNNING,
        ATMS_SYNCHRONIZATION};

    /**
     * Enumeration representing all possible states of the paramics connection.
     * @author Matthew Cechini
     */
    public static enum PARAMICS_STATUS { 
        UNKNOWN, 
        CONNECTING, 
        CONNECTED, 
        DISCONNECTED, 
        SENDING_NETWORK_ID, 
        LOADING, 
        WARMING, 
        LOADED, 
        DROPPED, 
        UNREACHABLE };

}