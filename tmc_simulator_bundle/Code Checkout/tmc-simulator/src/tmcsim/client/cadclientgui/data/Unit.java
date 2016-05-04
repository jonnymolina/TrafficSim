package tmcsim.client.cadclientgui.data;

import java.text.SimpleDateFormat;
import java.util.Vector;

import tmcsim.client.cadclientgui.enums.UnitStatusEnums;
import tmcsim.client.cadclientgui.enums.CADScriptTags.UNIT_TAGS;
import tmcsim.client.cadclientgui.screens.ScreenManager;

/**
 * This class holds all the data for a unit Object.
 * 
 * @author Vincent
 * 
 */
public class Unit {

    /* The init variables are set only from the XML script (readXMLNode method)
     * and are only used for resetCADDataSimulation purposes
     */
    private String init_id = "";
    private String init_masterInc = "";
    private String init_status  = "";
    private String init_oos  = "";
    private String init_type  = "";
    private String init_currentLocation  = "";
    private String init_destination  = "";
    private String init_misc  = "";
    private String init_stack  = "";
    private String init_area  = "";
    private String init_badge  = "";
    private String init_officer  = "";
    private int init_timer  = 0;
    private String init_office  = "";
    private String init_p  = "";
    private String init_agy  = "";
    private String init_alias  = "";
    private UnitStatusEnums init_unitStatus = null;
    
    private String id;
    private String masterInc;
    private String unitNum;
    private String status;
    private String oos;
    private String type;
    private String currentLocation;
    private String destination;
    private String misc;
    private String stack;
    private String area;
    private String badge;
    private String officer;
    private int timer;
    private String office;
    private String p;
    private String agy;
    private String alias;
    private UnitStatusEnums unitStatus;
    private int assignedIncidentId; // -1 means not assigned

    private Vector<Object> toTableVector;

    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public Unit(String unitNum) {
        this.unitNum = unitNum;
        id = "";
        masterInc = "";
        status = "";
        oos = "";
        type = "";
        currentLocation = "";
        destination = "";
        misc = "";
        stack = "";
        area = "";
        badge = "";
        officer = "";
        timer = 0;
        office = "";
        p = "";
        agy = "";
        alias = "";
        unitStatus = UnitStatusEnums.Assignable;
        assignedIncidentId = -1; // Negative means not assigned

        toTableVector = new Vector<Object>();
    }

    public void resetCADDataSimulation(){
        id = init_id;
        masterInc = init_masterInc;
        status = init_status;
        oos = init_oos;
        type = init_type;
        currentLocation = init_currentLocation;
        destination = init_destination;
        misc = init_misc;
        stack = init_stack;
        area = init_area;
        badge = init_badge;
        officer = init_officer;
        timer = init_timer;
        office = init_office;
        p = init_p;
        agy = init_agy;
        alias = init_alias;
        if(init_unitStatus == null){
            unitStatus = UnitStatusEnums.Assignable;
        }else{
            unitStatus = init_unitStatus;
        }
        assignedIncidentId = -1; // Negative means not assigned
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMasterInc() {
        return masterInc;
    }

    public void setMasterInc(String masterInc) {
        this.masterInc = masterInc;
    }

    public String getUnitNum() {
        return unitNum;
    }

    public void setUnitNum(String unitNum) {
        this.unitNum = unitNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOos() {
        return oos;
    }

    public void setOos(String oos) {
        this.oos = oos;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getOfficer() {
        return officer;
    }

    public void setOfficer(String officer) {
        this.officer = officer;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getAgy() {
        return agy;
    }

    public void setAgy(String agy) {
        this.agy = agy;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public UnitStatusEnums getUnitStatus() {
        return unitStatus;
    }

    public void setUnitStatus(UnitStatusEnums unitStatus) {
        this.unitStatus = unitStatus;
    }

    public int getAssignedIncidentId() {
        return assignedIncidentId;
    }

    public void setAssignedIncidentId(int id) {
        this.assignedIncidentId = id;
    }

    public void resetAssignedIncidentId() {
        id = "";
        unitStatus = UnitStatusEnums.Assignable;
        this.assignedIncidentId = -1; // represents not assigned
    }

    public Vector<Object> toVector() {
        toTableVector.clear();

        if (assignedIncidentId >= 0) {
            id = ((Integer) assignedIncidentId).toString();
        }
        toTableVector.add(id);
        toTableVector.add(masterInc);
        toTableVector.add(unitNum);
        toTableVector.add(status);
        toTableVector.add(oos);
        toTableVector.add(type);
        toTableVector.add(currentLocation);
        toTableVector.add(destination);
        toTableVector.add(misc);
        toTableVector.add(stack);
        toTableVector.add(area);
        toTableVector.add(badge);
        toTableVector.add(officer);
        toTableVector.add(intToHHMMSS(timer));
        toTableVector.add(office);
        toTableVector.add(p);
        toTableVector.add(agy);
        toTableVector.add(alias);
        toTableVector.add(unitStatus);
        return toTableVector;
    }

    /*
     * Called from the tmc.simulator.cadclient.ScriptHandler.java. Handles
     * storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if (tag_name.equals(UNIT_TAGS.ID.tag)) {
            init_id = value;
            setId(value);
        }
        if (tag_name.equals(UNIT_TAGS.STATUS.tag)) {
            init_status = value;
            setStatus(value);
        } else if (tag_name.equals(UNIT_TAGS.MASTER_INC_NUM.tag)) {
            init_masterInc = value;
            setMasterInc(value);
        } else if (tag_name.equals(UNIT_TAGS.OOS.tag)) {
            init_oos = value;
            setOos(value);
        } else if (tag_name.equals(UNIT_TAGS.TYPE.tag)) {
            init_type = value;
            setType(value);
        } else if (tag_name.equals(UNIT_TAGS.CURR_LOC.tag)) {
            init_currentLocation = value;
            setCurrentLocation(value);
        } else if (tag_name.equals(UNIT_TAGS.DESTINATION.tag)) {
            init_destination = value;
            setDestination(value);
        } else if (tag_name.equals(UNIT_TAGS.MISC_INFO.tag)) {
            init_misc = value;
            setMisc(value);
        } else if (tag_name.equals(UNIT_TAGS.STACK.tag)) {
            init_stack = value;
            setStack(value);
        } else if (tag_name.equals(UNIT_TAGS.AREA.tag)) {
            init_area = value;
            setArea(value);
        } else if (tag_name.equals(UNIT_TAGS.OFFICER.tag)) {
            init_officer = value;
            setOfficer(value);
        } else if (tag_name.equals(UNIT_TAGS.BADGE_NUM.tag)) {
            init_badge = value;
            setBadge(value);
        } else if (tag_name.equals(UNIT_TAGS.TIMER.tag)) {
            init_timer = Integer.parseInt(value);
            setTimer(Integer.parseInt(value));
        } else if (tag_name.equals(UNIT_TAGS.OFFICE.tag)) {
            init_office = value;
            setOffice(value);
        } else if (tag_name.equals(UNIT_TAGS.P.tag)) {
            init_p = value;
            setP(value);
        } else if (tag_name.equals(UNIT_TAGS.AGY.tag)) {
            init_agy = value;
            setAgy(value);
        } else if (tag_name.equals(UNIT_TAGS.ALIAS.tag)) {
            init_alias = value;
            setAlias(value);
        } else if (tag_name.equals(UNIT_TAGS.UNIT_STATUS.tag)) {
            init_unitStatus = UnitStatusEnums.valueOf(value);
            setUnitStatus(UnitStatusEnums.valueOf(value));
        }

    }

    public String intToHHMMSS(int time) {
        String hhmmss = "";
        int hms;

        hms = time / 3600;
        time = time % 3600;
        hhmmss = hhmmss.concat(((Integer) hms).toString() + ":");

        hms = time / 60;
        time = time % 60;
        hhmmss = hhmmss.concat(((Integer) hms).toString() + ":"
                + ((Integer) time).toString());

        return hhmmss;
    }

    public void TimerMinusSecond() {
        if (timer > 0) {
            this.timer--;
        }
    }

    public String getTimerInString() {
        return intToHHMMSS(timer);
    }
}
