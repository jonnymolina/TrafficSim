package tmcsim.common;


/**
 * CADScriptTags contains enumerations grouping tag and attribute names
 * used in parsing and creating a simulation script. 
 * 
 * @author Matthew Cechini
 * @version
 */
public class CADScriptTags {

    /**
     * Enumeration representing the tag names corresponding to major XML 
     * document elements in a CAD Script. 
     * @author Matthew Cechini
     */
    public static enum SCRIPT_LEVEL_TAGS {
        TMC_SCRIPT          ("TMC_SCRIPT"),
        SCRIPT_EVENT        ("SCRIPT_EVENT"),
        CAD_DATA            ("CAD_DATA"),
        HEADER_INFO         ("HEADER_INFO"),
        LOCATION_INFO       ("LOCATION_INFO"),
        CAD_INCIDENT_EVENT  ("CAD_INCIDENT_EVENT"), 
        PARAMICS            ("PARAMICS");
        
        public String tag;
        
        private SCRIPT_LEVEL_TAGS(String t) {
            tag = t;
        }
    }

    /**
     * Enumeration representing the tag names corresponding to XML 
     * document tag and attribute names within the SCRIPT_EVENT
     * element in a CAD Script.
     * @author Matthew Cechini
     */
    public static enum SCRIPT_EVENT_TAGS {
        TIME_INDEX     ("TIME_INDEX"),
        INCIDENT       ("INCIDENT"),
        LOG_NUMBER     ("LogNum");
        
        public String tag;
        
        private SCRIPT_EVENT_TAGS(String t) {
            tag = t;
        }
    }

    /**
     * Enumeration representing the tag names corresponding to XML 
     * document tag and attribute names within the LOCATION_INFO
     * element in a CAD Script.
     * @author Matthew Cechini
     */
    public static enum LOCATION_INFO_TAGS {
        ID             ("ID");
        
        public String tag;
        
        private LOCATION_INFO_TAGS(String t) {
            tag = t;
        }
    }

    /**
     * Enumeration representing the tag names corresponding to XML 
     * document tag and attribute names within the CAD_DATA
     * element in a CAD Script.
     * @author Matthew Cechini
     */
    public static enum CAD_INCIDENT_DATA_TAGS {
        AUDIO             ("AUDIO"),    
        CCTV_INFO         ("CCTV_INFO"),
        DETAIL            ("DETAIL"),
        UNIT              ("UNIT"),
        WITNESS           ("WITNESS"),
        TOW               ("TOW"),
        SERVICE           ("SERVICE");
        
        public String tag;
        
        private CAD_INCIDENT_DATA_TAGS(String t) {
            tag = t;
        }
    }

    /**
     * Enumeration representing the tag names corresponding to XML 
     * document tag and attribute names within the HEADER_INFO
     * element in a CAD Script.
     * @author Matthew Cechini
     */
    public static enum INCIDENT_HEADER_TAGS {
        LOG_NUMBER     ("LogNum"),
        LOG_STATUS     ("LogStatus"),
        DESCRIPTION    ("Desc"),
        PRIORITY       ("Priority"),
        TYPE           ("Type"),
        FULL_LOCATION  ("FullLoc"),
        TRUNC_LOCATION ("TruncLoc"),
        BEAT           ("Beat"),
        CALLBOX        ("Callbox");
        
        public String tag;
        
        private INCIDENT_HEADER_TAGS(String t) {
            tag = t;
        }
    }

    /**
     * Enumeration representing the tag names corresponding to XML 
     * document tag and attribute names within the UNIT
     * element in a CAD Script.
     * @author Matthew Cechini
     */
    public static enum UNIT_TAGS {
        UNIT_NUMBER      ("UnitNum"),
        UNIT_STATUS      ("Status"),
        UNIT_PRIMARY     ("Primary"),
        UNIT_ACTIVE      ("Active");
        
        public String tag;
        
        private UNIT_TAGS (String t) {
            tag = t;
        }       
    }

    /**
     * Enumeration representing the tag names corresponding to XML 
     * document tag and attribute names within the TOW
     * element in a CAD Script.
     * @author Matthew Cechini
     */
    public static enum TOW_TAGS {
        TOW_COMPANY_NAME ("Company"),
        CONF_PHONE_NUM   ("ConfNum"),
        PUBLIC_PHONE_NUM ("PubNum"),
        BEAT             ("Beat");
        
        public String tag;
        
        private TOW_TAGS(String t) {
            tag = t;
        }       
    }

    /**
     * Enumeration representing the tag names corresponding to XML 
     * document tag and attribute names within the WITNESS
     * element in a CAD Script.
     * @author Matthew Cechini
     */
    public static enum WITNESS_TAGS {
        WITNESS_NAME     ("Name"),
        WITNESS_ADDRESS  ("Address"),
        WITNESS_PHONE    ("PhoneNum");
        
        public String tag;
        
        private WITNESS_TAGS(String t) {
            tag = t;
        }       
    }

    /**
     * Enumeration representing the tag names corresponding to XML 
     * document tag and attribute names within the SERVICE
     * element in a CAD Script.
     * @author Matthew Cechini
     */
    public static enum SERVICE_TAGS {   
        SERVICE_NAME     ("Name"),
        CONF_PHONE_NUM   ("ConfNum"),
        PUBLIC_PHONE_NUM ("PubNum");
        
        public String tag;
        
        private SERVICE_TAGS(String t) {
            tag = t;
        }
    }

    /**
     * Enumeration representing the tag names corresponding to XML 
     * document tag and attribute names within the AUDIO
     * element in a CAD Script.
     * @author Matthew Cechini
     */
    public static enum AUDIO_TAGS {
        FILE_PATH    ("Path"),
        FILE_LENGTH  ("Length");
        
        public String tag;
        
        private AUDIO_TAGS(String t) {
            tag = t;
        }
    }

    /**
     * Enumeration representing the tag names corresponding to XML 
     * document tag and attribute names within the PARAMICS
     * element in a CAD Script.
     * @author Matthew Cechini
     */
    public static enum PARAMICS_TAGS {
        LOCATION_ID      ("LocationID");    
        
        public String tag;
        
        private PARAMICS_TAGS(String t) {
            tag = t;
        }
    }

    /**
     * Enumeration representing the tag names corresponding to XML 
     * document tag and attribute names within the CCTV
     * element in a CAD Script.
     * @author Matthew Cechini
     */
    public static enum CCTV_TAGS {
        CCTV_ID          ("ID"),
        CCTV_DIR         ("Dir"),
        CCTV_TOGGLE      ("Toggle");    
        
        public String tag;
        
        private CCTV_TAGS(String t) {
            tag = t;
        }
    }

}