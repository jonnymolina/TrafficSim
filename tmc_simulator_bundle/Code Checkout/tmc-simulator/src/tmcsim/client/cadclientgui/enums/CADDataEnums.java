package tmcsim.client.cadclientgui.enums;

public class CADDataEnums {
    
    public static enum TABLE
    {
        ASSIGNED_INCIDENTS  ("ASSIGNED_INCIDENTS"),
        UNIT_STATUS             ("UNIT_STATUS"),
        PENDING_INCIDENTS       ("PENDING_INCIDENTS"),
        INCIDENT_EDITOR     ("INCIDENT_EDITOR");
        
        public String tag;
        
        private TABLE(String t) {
            tag = t;
        }
    }
    
    public static enum INC_TABLE
    {
        COMMENTS_NOTES          ("COMMENTS_NOTES");
        
        public String tag;
        
        private INC_TABLE(String t) {
            tag = t;
        }
    }
    
    public static enum INC_VAL
    {
        LOG_NUM                 ("LOG_NUM"),
        MASTER_INC              ("MASTER_INC"),
        ALERT                       ("ALERT"),
        OAU                     ("OAU"),
        P                           ("P"),
        DESCRIPTION             ("DESCRIPTION"),
        RP                          ("RP"),
        ALI                     ("ALI"),
        RP_TYPE                 ("RP_TYPE"),
        MEDIA                       ("MEDIA");
        
        public String tag;
        
        private INC_VAL(String t) {
            tag = t;
        }
    }
    
    public static enum INC_LOC
    {
        ADDRESS                 ("ADDRESS"),
        LOC                     ("LOC"),
        CITY                        ("CITY"),
        COUNTY                  ("COUNTY"),
        STATE                       ("STATE"),
        ZIP                     ("ZIP"),
        BEAT                        ("BEAT"),
        AREA                        ("AREA"),
        SECTOR                  ("SECTOR"),
        SECTOR_CODE             ("SECTOR_CODE"),
        DIVISION                    ("DIVISION"),
        APT                     ("APT"),
        BUILDING                    ("BUILDING"),
        CROSS_ST                    ("CROSS_ST"),
        LAW                     ("LAW"),
        FIRE                        ("FIRE"),
        EMS                     ("EMS");
        
        public String tag;
        
        private INC_LOC(String t) {
            tag = t;
        }
    }
    
    public static enum INC_CALLER
    {
        TYPE                        ("TYPE"),
        NAME                        ("NAME"),
        PHONE                       ("PHONE"),
        EXT                     ("EXT");
        
        public String tag;
        
        private INC_CALLER(String t) {
            tag = t;
        }
    }
    
    public static enum INC_PROBLEM
    {
       PROBLEM                  ("PROBLEM"),
        CODE                    ("CODE"), 
        PRIORITY                ("PRIORITY");
        
        public String tag;
        
        private INC_PROBLEM(String t)
        {
            tag = t;
        }
    }
    
    public static enum INC_GEN_INFO
    {
        ID                      ("ID"),
        MASTER_INC_NUM          ("MASTER_INC_NUM"),
        JURISDICTION            ("JURISDICTION"), 
        ALARM                       ("ALARM"),
        AGY                         ("AGY");
        
        public String tag;
        
        private INC_GEN_INFO(String t)
        {
            tag = t;
        }
    }
    
    public static enum INC_RESP
    {
        PLAN                    ("PLAN"),
        AREA                    ("AREA");
        
        public String tag;
        
        private INC_RESP(String t)
        {
            tag = t;
        }
    }
    
    public static enum INC_ADD_INFO
    {
        TYPE                        ("TYPE"),
        TYPE_CODE                   ("TYPE_CODE"),
        MACHINE                     ("MACHINE"), 
        CALL_STATUS                 ("CALL_STATUS"),
        CALL_TAKER_EXT              ("CALL_TAKER_EXT"),
        ALARM_LEVEL                 ("ALARM_LEVEL"),
        ROTATION_PROVIDER_AREA  ("ROTATION_PROVIDER_AREA"),
        COMMENT                     ("COMMENT");
        
        public String tag;
        
        private INC_ADD_INFO(String t)
        {
            tag = t;
        }
    }
    
    public static enum INC_ACTIVITIES
    {
        VEHICLE                 ("VEHICLE"),
        ACTIVITY                ("ACTIVITY"),
        LOCATION                ("LOCATION"), 
        COMMENT                 ("COMMENT"),
        DISP                    ("DISP");
        
        public String tag;
        
        private INC_ACTIVITIES(String t)
        {
            tag = t;
        }
    }
    
    public static enum INC_CALLBACK
    {
        INITIAL                 ("INITIAL"),
        COMMENT                 ("COMMENT");
        
        public String tag;
        
        private INC_CALLBACK(String t)
        {
            tag = t;
        }
    }
    
    public static enum INC_EDIT_LOG
    {
        EDIT                    ("EDIT"),
        REASON                      ("REASON"),
        CHANGE_BY               ("CHANGE_BY"), 
        TERMINAL                ("TERMINAL");
        
        public String tag;
        
        private INC_EDIT_LOG(String t)
        {
            tag = t;
        }
    }
    
    public static enum INC_INFO
    {
        CALL_INITIATED    ("CALL_INITIATED"),
        CALL_TAKEN          ("CALL_TAKEN"),
        TIME_IN_Q           ("TIME_IN_Q"), 
        LAST_UPDATED        ("LAST_UPDATED");
        
        public String tag;
        
        private INC_INFO(String t)
        {
            tag = t;
        }
    }
    
    public static enum INC_TIMES
    {
        UNIT                ("UNIT"),
        ALARM               ("ALARM"),
        ASSIGNED            ("ASSIGNED"), 
        ENROUTE             ("ENROUTE"),
        STAGED              ("STAGED"),
        ARRIVAL             ("ARRIVAL"),
        ACCESS                  ("ACCESS"),
        DEPART              ("DEPART"), 
        AT_DEST             ("AT_DEST"),
        STATUS_5            ("STATUS_5"),
        AVAILABLE           ("AVAILABLE"),
        RESP_NUM            ("RESP_NUM"),
        
        RING                    ("RING"),
        IN_QUEUE                ("IN_QUEUE"),
        ALL_AVAILABLE       ("ALL_AVAILABLE"),
        CALL_CLOSED         ("CALL_CLOSED");
        
        public String tag;
        
        private INC_TIMES(String t)
        {
            tag = t;
        }
    }
    
    public static enum CARDFILE
    {
        COASTAL_DIVISION_UNITS  ("Coastal Division Units"),
        POLICE_SHERIFF_CORONER  ("Police/Sheriff/Coroner"),
        COURTS                      ("Courts"),
        PUBLIC_TRANSPORTATION   ("Public Transportation"),
        GG_OTHER                        ("GG Other"),
        MY_MISC                     ("MY Misc"),
        SL_MISC                     ("SL Misc"),
        VT_MISC                     ("VT Misc"),
        CHP_OFFICES                 ("CHP Offices"),
        STATE_AGENCIES_FACILITIES   ("State Agencies/Facilities"),
        GOVERNMENT_OFFICIALS        ("Government Officials"),
        FEDERAL_AGENCIES            ("Federal Agencies"),
        RANCHES_LIVESTOCK           ("Ranches/Livestock"),
        FIRE_EMS                        ("Fire/EMS"),
        JAILS                           ("Jails"),
        HOSPITALS_MED_CENTERS   ("Hospitals/Med Centers"),
        TOW_COMPANIES               ("Tow Companies"),
        CALTRANS                        ("CalTrans"),
        COUNTY_ROADS                ("County Roads"),
        UTILITIES                   ("Utilities"),
        ANIMAL_CONTROL              ("Animal Control"),
        AIRPORTS                        ("Airports"),
        CREDIT_CARDS                ("Credit Cards"),
        GG_CRISIS_SHELTERS      ("GG Crisis Shelters"),
        RANGES                      ("Ranges"),
        HOTLINES                        ("Hotlines"),
        HWY_PATROLS_OOS         ("Hwy Patrols OOS"),
        PARKS_RECREATION            ("Parks/Recreation"),
        SHELTERS                        ("Shelters"),
        SL_COUNTY_SERVICES      ("SL County Services"),
        SL_RESOURCES                ("SL Resources"),
        TRUCK_TIRE_REPAIR           ("Truck/Tire Repair"),
        MCC_EMPLOYEES               ("MCC Employees"),
        GATE_ACCESS_CODES           ("Gate Access Codes"),
        VT_CALL_SIGNS               ("VT Call Signs"),
        SLCC_EMPLOYEES              ("SLCC Employees");
        
        public String tag;
        
        private CARDFILE(String t)
        {
            tag = t;
        }
    }
    
    public enum EditCommand{
         OBJECT_ADD,
         OBJECT_DELETE,
         TABLE_ADD,
         TABLE_DELETE,
         NAME,
         ADDRESS,
         CITY,
         STATE,
         ZIP,
         PHONE1,
         PHONE2,
         FAX,
     }
}
