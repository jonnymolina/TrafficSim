package tmcsim.client.cadclientgui.enums;

public class CADScriptTags {

    public static enum SCRIPT_LEVEL_TAGS 
    {
        TMC_SCRIPT         ("TMC_SCRIPT"),
        SCRIPT_DATA        ("SCRIPT_DATA"),
        CAD_DATA           ("CAD_DATA"),
        SCRIPT_EVENT       ("SCRIPT_EVENT"),
        NEW_UNIT            ("NEW_UNIT"),
        HEADER_INFO         ("HEADER_INFO"),
        LOCATION_INFO       ("LOCATION_INFO"),
        CAD_INCIDENT_EVENT  ("CAD_INCIDENT_EVENT"), 
        PARAMICS            ("PARAMICS");
        
        public String tag;
        
        private SCRIPT_LEVEL_TAGS(String t) {
            tag = t;
        }
    }
    
    public static enum UNIT_TAGS
    {
        UNIT_NUM       ("UnitNum"),
        ID             ("ID"),
        MASTER_INC_NUM ("MASTER_INC_NUM"),
        STATUS         ("STATUS"),
        OOS            ("OOS"),
        TYPE           ("TYPE"),
        CURR_LOC       ("CURR_LOC"),
        DESTINATION    ("DESTINATION"),
        MISC_INFO      ("MISC_INFO"),
        STACK          ("STACK"),
        AREA           ("AREA"),
        OFFICER        ("OFFICER"),
        BADGE_NUM      ("BADGE_NUM"),
        TIMER          ("TIMER"),
        OFFICE         ("OFFICE"),
        P              ("P"),
        AGY            ("AGY"),
        ALIAS          ("ALIAS"),
        UNIT_STATUS    ("UNIT_STATUS");
        
        
        public String tag;
        
        private UNIT_TAGS(String t)
        {
            tag = t;
        }
    }
    
    public static enum SCRIPT_EVENT_TAGS
    {
        TIME_INDEX     ("TIME_INDEX"), 
        INCIDENT       ("INCIDENT"), 
        LOG_NUMBER     ("LogNum"),
        LOCATION_INFO  ("LOCATION_INFO"),
        ASSIGN         ("ASSIGN"),
        UNIT_NUM       ("UnitNum"),
        CAD_DATA       ("CAD_DATA");
        
        public String tag;
        
        private SCRIPT_EVENT_TAGS(String t)
        {
            tag = t;
        }
    }
    
    public static enum CAD_DATA_TAGS
    {
        MASTER_INC_NUM    ("MASTER_INC_NUM"),
        LOCATION    ("LOCATION"),
        CALLER     ("CALLER"), 
        PROBLEM       ("PROBLEM"), 
        GENERAL  ("GENERAL"),
        RESPONSE   ("RESPONSE"),
        ADDITIONAL_INFO  ("ADDITIONAL_INFO"),
        ACTIVITIES     ("ACTIVITIES"),
        CALL_BACKS    ("CALL_BACKS"),
        EDIT_LOG      ("EDIT_LOG"),
        INFO        ("INFO"),
        TIMES       ("TIMES"),
        TRANSPORT_INFO  ("TRANSPORT_INFO"),
        USER_DATA    ("USER_DATA"),
        ATTACHMENTS   ("ATTACHMENTS"),
        RP  ("RP"),
        ALI ("ALI"),
        RP_TYPE ("RP_TYPE"),
        PRI ("PRI"),
        MEDIA   ("MEDIA"),
        TYPE_CODE ("TYPE_CODE"), 
        P   ("P");
        
        
        public String tag;
        
        private CAD_DATA_TAGS(String t)
        {
            tag = t;
        }
    }
    
    public static enum INCIDENT_LOCATION_TAGS
    {
        ADDRESS       ("ADDRESS"),
        LOC           ("LOC"),
        CITY          ("CITY"),
        COUNTY        ("COUNTY"),
        STATE         ("STATE"),
        ZIP           ("ZIP"),
        BEAT          ("BEAT"),
        AREA          ("AREA"),
        SECTOR        ("SECTOR"),
        SECTOR_CODE   ("SECTOR_CODE"),
        DIVISION      ("DIVISION"),
        APT           ("APT"),
        BUILDING      ("BUILDING"),
        CROSS_STREET  ("CROSS_STREET"),
        LAW           ("LAW"),
        FIRE              ("FIRE"),
        EMS           ("EMS");
        
        public String tag;
        
        private INCIDENT_LOCATION_TAGS(String t)
        {
            tag = t;
        }
    }
    
    public static enum CARDFILE_TAGS
    {
        CARDFILE            ("CARDFILE"),
        TAB             ("TAB"),
        TITLE               ("title"),//this is lowercase on purpose to match XML
        CARDFILE_OBJ    ("CARDFILE_OBJ"),
        NAME                ("name"),
        ADDRESS         ("ADDRESS"),
        CITY                ("CITY"),
        STATE               ("STATE"),
        ZIP             ("ZIP"),
        PHONE1          ("PHONE1"),
        PHONE2          ("PHONE2"),
        FAX             ("FAX");
        
        public String tag;
        
        private CARDFILE_TAGS(String t)
        {
            tag = t;
        }
    }
    
}
