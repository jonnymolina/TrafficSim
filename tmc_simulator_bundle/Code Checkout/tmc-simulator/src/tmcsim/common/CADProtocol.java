package tmcsim.common;

import java.io.Serializable;


/**
 * CADProtocol contains enumerations used to create the communications protocol
 * between the CAD Client, CAD Simulator, and ParamicsCommunicator.
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/06 20:46:41 $ $Revision: 1.4 $
 */
public class CADProtocol {

    /**
     * Enumeration of commands that are performed by the CAD client and
     * transmitted to the CAD Simulator.
     * @author Matthew Cechini
     */
    public static enum CAD_CLIENT_CMD implements Serializable {
        TERMINAL_REGISTER ("TERMINAL_REGISTER"),
        TERMINAL_FUNCTION ("TERMINAL_FUNCTION"),
        TERMINAL_CMD_LINE ("TERMINAL_CMD_LINE"),
        SAVE_COMMAND_LINE ("SAVE_COMMAND_LINE"),
        UNKNOWN           ("");
        
        public String type;
        
        private CAD_CLIENT_CMD(String t) {
            type = t;           
        }
         
        /**
         * Returns the CAD_CLIENT_CMD enumeration value which has a command
         * value that matches the parameter value.
         * @param t Command type
         * @throws ScriptException if the parameter value is invalid.
         * @return CAD_CLIENT_CMD for the parameter value.
         */     
        public static CAD_CLIENT_CMD fromString(String t) {
            for(CAD_CLIENT_CMD cmdType : values()) {
                if(cmdType.type.equals(t))
                    return cmdType;
            }
            
            return UNKNOWN;
        }
    };
    
    /**
     * Enumeration of commands that are performed by the CAD simulator 
     * and transmitted to the CAD Client.
     * @author Matthew Cechini
     */    
    public static enum CAD_SIMULATOR_CMD implements Serializable {
        UPDATE_SCREEN     ("UPDATE_SCREEN"),
        UPDATE_STATUS     ("UPDATE_STATUS"),
        UPDATE_TIME       ("UPDATE_TIME"),
        UPDATE_MSG_COUNT  ("UPDATE_MSG_COUNT"),
        UPDATE_MSG_UNREAD ("UPDATE_MSG_UNREAD"),
        CAD_INFO          ("CAD_INFO"),
        APP_CLOSE         ("APP_CLOSE"),
        UNKNOWN           ("");
        
        public String type;
        
        private CAD_SIMULATOR_CMD(String t) {
            type = t;           
        }
                
        /**
         * Returns the CAD_SIMULATOR_CMD enumeration value which has a command
         * value that matches the parameter value.
         * @param t Command type
         * @throws ScriptException if the parameter value is invalid.
         * @return CAD_SIMULATOR_CMD for the parameter value.
         */         
        public static CAD_SIMULATOR_CMD fromString(String t) {
            for(CAD_SIMULATOR_CMD cmdType : values()) {
                if(cmdType.type.equals(t))
                    return cmdType;
            }
            
            return UNKNOWN;
        }
    };    
        
    /**
     * Enumeration of commands that are parsed from the CAD command line.
     * @author Matthew Cechini
     */        
    public static enum CAD_COMMANDS implements Serializable {
        
        BLANK_SCREEN     ("",   "BLANK_SCREEN"),
        INCIDENT_BOARD   ("IB", "INCIDENT_BOARD"),
        INCIDENT_UPDATE  ("UI", "INCIDENT_UPDATE"),
        INCIDENT_INQUIRY ("II", "INCIDENT_INQUIRY"),
        INCIDENT_SUMMARY ("SA", "INCIDENT_SUMMARY"),
        ROUTED_MESSAGE   ("TO", "ROUTED_MESSAGE"),
        ENTER_INCIDENT   ("EI", "ENTER_INCIDENT"),
        TERMINAL_OFF     ("OF", "TERMINAL_OFF"),
        APP_CLOSE        ("KILL", "APP_CLOSE"),
        UNKNOWN          ("", "");
        
        /** Mnemonic used on command line. */
        public String mnemonic = "";
        /** Full text name of command used for XML document creation. */
        public String fullName = "";
        
        private CAD_COMMANDS(String new_mnemonic, String new_name) {
            mnemonic = new_mnemonic;
            fullName = new_name;
        }
        
        /**
         * Returns the CAD_COMMANDS enumeration value which has a full name
         * value that matches the parameter value.
         * @param fName Full name
         * @throws ScriptException if the parameter value is invalid.
         * @return CAD_COMMANDS for the parameter value.
         */         
        public static CAD_COMMANDS fromFullName(String fName) {
            for(CAD_COMMANDS cmd : values()) {
                if(cmd.fullName.equals(fName))
                    return cmd;
            }
            
            return UNKNOWN;
        }
    }
    
    /**
     * Enumeration of field codes that are parsed from the CAD command line.
     * @author Matthew Cechini
     */     
    public static enum CAD_FIELD_CODES implements Serializable {
        
        WITNESS_ADDRESS  ("A/", "WITNESS_ADDRESS"),
        BEAT             ("B/", "BEAT"),
        CALLBOX          ("C/", "CALLBOX"),
        DETAILS          ("D/", "DETAILS"),
        HANDLING_UNIT    ("H/", "HANDLING_UNIT"),
        INCIDENT_NUMBER  ("I/", "INCIDENT_NUMBER"),
        LOCATION         ("L/", "LOCATION"),
        PRIORITY         ("P/", "PRIORITY"),
        WITNESS_PHONE    ("N/", "WITNESS_PHONE"),
        TYPE             ("T/", "TYPE"),
        TOW              ("V/", "TOW"),
        WITNESS          ("W/", "WITNESS"),
        MESSAGE          ("M/", "MESSAGE"),
        ROUTE            ("R/", "ROUTE"),
        UNKNOWN          ("", "");
        

        /** Mnemonic used on command line. */
        public String mnemonic = "";
        /** Full text name of command used for XML document creation. */
        public String fullName = "";
        
        private CAD_FIELD_CODES(String new_mnemonic, String new_name) {
            mnemonic = new_mnemonic;
            fullName = new_name;
        }
        
        /**
         * Returns the CAD_FIELD_CODES enumeration value which has a full name
         * value that matches the parameter value.
         * @param fName Full name
         * @throws ScriptException if the parameter value is invalid.
         * @return CAD_FIELD_CODES for the parameter value.
         */  
        public static CAD_FIELD_CODES fromFullName(String fName) {
            for(CAD_FIELD_CODES cmd : values()) {
                if(cmd.fullName.equals(fName))
                    return cmd;
            }
            
            return UNKNOWN;
        }
    }   
            
    /**
     * Enumeration of XML tag and attribute names that are used for XML 
     * document creation.
     * @author Matthew Cechini
     */     
    public static enum DATA_TAGS implements Serializable {
        
        POSITION_NUM      ("POSITION_NUM"),
        USER_ID           ("USER_ID"),
        LOG_NUM           ("LOG_NUM"),
        //ORIGINAL_CAD_LINE = "ORIGINAL_CAD_LINE"),
        SENSITIVE         ("SENSITIVE"),
        FULL_LOCATION     ("FULL_LOCATION"),
        TRUNC_LOCATION    ("TRUNC_LOCATION"),               
        ORIGIN            ("ORIGIN"),
        DESTINATION       ("DESTINATION"),
        MESSAGE           ("MESSAGE"),      
        WITNESS_NAME      ("WITNESS_NAME"),
        WITNESS_PHONE     ("WITNESS_PHONE"),
        WITNESS_ADDR      ("WITNESS_ADDR"),
        UNKNOWN           ("");
        
        public String tag;
        
        private DATA_TAGS(String t) {
            tag = t;
        }
        
        /**
         * Returns the DATA_TAGS enumeration value which has a name
         * value that matches the parameter value.
         * @param t Tag name
         * @throws ScriptException if the parameter value is invalid.
         * @return DATA_TAGS for the parameter value.
         */         
        public static DATA_TAGS fromString(String t) {
            for(DATA_TAGS data : values()) {
                if(data.tag.equals(t))
                    return data;
            }
            
            return UNKNOWN;
        }       
    }
    
    /**
     * Enumeration of XML tag names that are used for document creation.
     * @author Matthew Cechini
     */     
    public static enum PARAMICS_COMM_TAGS implements Serializable {
        
        ID          ("ID"),
        ACTION      ("ACTION"),
        TARGET_FILE ("TARGET_FILE"),
        INTERVAL    ("INTERVAL"),
        MESSAGE     ("MESSAGE"),
        WRITER      ("WRITER"),
        READER      ("READER"),
        RESET       ("RESET"),
        UNKNOWN     ("");
        
        public String tag;
        
        private PARAMICS_COMM_TAGS(String t) {
            tag = t;
        }
        
        /**
         * Returns the PARAMICS_COMM_TAGS enumeration value which has a name
         * value that matches the parameter value.
         * @param t Tag name.
         * @throws ScriptException if the parameter value is invalid.
         * @return PARAMICS_COMM_TAGS for the parameter value.
         */         
        public static PARAMICS_COMM_TAGS fromString(String t) {
            for(PARAMICS_COMM_TAGS comm : values()) {
                if(comm.tag.equals(t))
                    return comm;
            }
            
            return UNKNOWN;
        }
    }
    
    /**
     * Enumeration of XML tag name that are used for XML document creation
     * to perform specific actions in the ParamicsCommunicator.
     * @author Matthew Cechini
     */     
    public static enum PARAMICS_ACTIONS implements Serializable {
        
        REGISTER   ("REGISTER"),
        UNREGISTER ("UNREGISTER"),
        READ_FILE  ("READ_FILE"),
        WRITE_FILE ("WRITE_FILE"),
        UNKNOWN    ("");
        
        public String action;
        
        private PARAMICS_ACTIONS(String a){
            action = a;
        }
        
        /**
         * Returns the PARAMICS_ACTIONS enumeration value which has an action
         * value that matches the parameter value.
         * @param a Paramics action.
         * @throws ScriptException if the parameter value is invalid.
         * @return PARAMICS_ACTIONS for the parameter value.
         */ 
        public static PARAMICS_ACTIONS fromString(String a) {
            for(PARAMICS_ACTIONS act : values()) {
                if(act.action.equals(a))
                    return act;
            }
            
            return UNKNOWN;
        }       
    }
    
}

