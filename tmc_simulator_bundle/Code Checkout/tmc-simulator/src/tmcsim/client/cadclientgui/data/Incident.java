package tmcsim.client.cadclientgui.data;

import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Vector;

import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

import tmcsim.cadmodels.IncidentEditLog;
import tmcsim.cadmodels.IncidentInquiryHeader;
import tmcsim.client.cadclientgui.data.IncidentEvent.EVENT_STATUS;
import tmcsim.client.cadclientgui.enums.CADScriptTags.CAD_DATA_TAGS;
import tmcsim.client.cadclientgui.enums.IncidentEnums;
import tmcsim.common.ParamicsLocation;

/**
 * This class holds for the data for an incident.
 * 
 * @author Vincent
 * 
 */
public class Incident implements Serializable {
    
    public Integer logNum;
    private String masterInc;
    
    /* The init variables are set only from the XML script (readXMLNode method)
     * and are only used for resetCADDataSimulation purposes
     */
    private String init_oau = "";
    private String init_logTime = "";
    private String init_p = "";
    private String init_userData = "";
    private String init_attachments = "";
    private String init_rp = "";
    private String init_ali = "";
    private String init_rpType = "";
    private String init_media = "";
    
    private ImageIcon alert;
    private String oau;
    private String logTime;
    private String p;
    private LinkedList<String> assignedUnitNums;
    private String userData;
    private String attachments;
    private String rp;
    private String ali;
    private String rpType;
    private String media;
    private DefaultTableModel commentsNotesTable;
    
    private long startTime;
    private long secondsIncidentStarted = 0;
    private boolean incidentOccured = false;
    public TreeMap<String, ParamicsLocation> locationMap;
    private Vector<IncidentEvent> eventList = null;
    public String description;
    
    private IncidentLocation incidentLocation;
    private IncidentCaller incidentCaller;
    private IncidentProblem problem;
    private IncidentGeneralInfo genInfo;
    private IncidentResponse response;
    private IncidentAdditionalInfo additionalInfo;
    private IncidentActivities activities;
    private IncidentCallBacks callBacks;
    private IncidentEditLog editLog;
    private IncidentInfo info;
    private IncidentTimes times;
    private IncidentTransportInfo transpInfo;
    private IncidentVehicle incidentVehicle;
    private IncidentEnums status;

    private Vector<Object> toTableVector;

    public IncidentInquiryHeader header = new IncidentInquiryHeader();


    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    /*public Incident(int logNum) {
        masterInc = "";
        alert = new ImageIcon("images/IncidentPanelsImages/blank.png");
        oau = "";
        p = "";
        description = "";
        incidentLocation = new IncidentLocation();
        incidentCaller = new IncidentCaller();
        problem = new IncidentProblem();
        genInfo = new IncidentGeneralInfo();
        assignedUnitNums = new LinkedList<String>();
        response = new IncidentResponse();
        additionalInfo = new IncidentAdditionalInfo();
        activities = new IncidentActivities();
        callBacks = new IncidentCallBacks();
        editLog = new IncidentEditLog();
        info = new IncidentInfo();
        times = new IncidentTimes();
        transpInfo = new IncidentTransportInfo();
        incidentVehicle = new IncidentVehicle();
        userData = "";
        attachments = "";
        status = IncidentEnums.Pending;
        assignedUnitNums = new LinkedList<String>();
        rp = "";
        ali = "";
        rpType = "";
        media = "";
        eventList = new Vector<IncidentEvent>();

        toTableVector = new Vector<Object>();

        commentsNotesTable = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;// This causes all cells to be not editable
            }
        };
        commentsNotesTable.setColumnIdentifiers(new String[] { "Date", "Time",
                "Initial", "Block", "Comment" });

        setLogNum(logNum);
    }*/

    public Incident(Integer log, String desc, long start) {
        logNum = log;
        description = desc;
        startTime = start;
        header      = new IncidentInquiryHeader();      
        locationMap = new TreeMap<String, ParamicsLocation>();
        eventList    = new Vector<IncidentEvent>();
        
        masterInc = "";
        alert = new ImageIcon("images/IncidentPanelsImages/blank.png");
        oau = "";
        p = "";
        incidentLocation = new IncidentLocation();
        incidentCaller = new IncidentCaller();
        problem = new IncidentProblem();
        genInfo = new IncidentGeneralInfo();
        assignedUnitNums = new LinkedList<String>();
        response = new IncidentResponse();
        additionalInfo = new IncidentAdditionalInfo();
        activities = new IncidentActivities();
        callBacks = new IncidentCallBacks();
        editLog = new IncidentEditLog();
        info = new IncidentInfo();
        times = new IncidentTimes();
        transpInfo = new IncidentTransportInfo();
        userData = "";
        attachments = "";
        status = IncidentEnums.Pending;
        assignedUnitNums = new LinkedList();
        rp = "";
        ali = "";
        rpType = "";
        media = "";
        eventList = new Vector<IncidentEvent>();

        toTableVector = new Vector<Object>();
        
        commentsNotesTable = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;// This causes all cells to be not editable
            }
        };
        commentsNotesTable.setColumnIdentifiers(new String[] { "Date", "Time",
                "Initial", "Block", "Comment" });
    }

    /*This method is to be called alongside resetSimulation for true reset.
     * resetSimulation handles the variables from prior version, while this method resets all the variables
     * specific to the CADData version. This method is called in the Coordinator following resetSimulation.
     */
    public void resetCADDataSimulation(){
        oau = init_oau;
        logTime = init_logTime;
        p = init_p;
        userData = init_userData;
        attachments = init_attachments;
        rp = init_rp;
        ali = init_ali;
        rpType = init_rpType;
        media = init_media;
        assignedUnitNums.clear();
        while(commentsNotesTable.getRowCount() > 0){
            commentsNotesTable.removeRow(0);
        }
        
        incidentLocation.resetCADDataSimulation();
        incidentCaller.resetCADDataSimulation();
        problem.resetCADDataSimulation();
        genInfo.resetCADDataSimulation();
        response.resetCADDataSimulation();
        additionalInfo.resetCADDataSimulation();
        activities.resetCADDataSimulation();
        callBacks.resetCADDataSimulation();
        //editLog.resetCADDataSimulation();
        info.resetCADDataSimulation();
        //times.resetCADDataSimulation();
        //transpInfo.resetCADDataSimulation();
    }
    
    public boolean tick(long scriptSeconds) {

        if (scriptSeconds >= startTime && !incidentOccured) {
            incidentOccured = true;
            secondsIncidentStarted = scriptSeconds;

            return true;
        } else
            return false;
    }

    public Vector<IncidentEvent> getTriggeredEvents(long simTime) {

        Vector<IncidentEvent> triggered = new Vector<IncidentEvent>();

        if (incidentOccured) {
            for (IncidentEvent evt : eventList) {
                if (evt.triggerEvent(secondsIncidentStarted, simTime)) {
                    triggered.add(evt);
                }
            }
        }

        return triggered;
    }

    public Vector<IncidentEvent> getCompletedEvents() {

        Vector<IncidentEvent> completed = new Vector<IncidentEvent>();

        for (IncidentEvent evt : eventList) {
            if (evt.eventStatus == EVENT_STATUS.COMPLETED) {
                completed.add(evt);
            }
        }

        return completed;
    }

    public Integer getLogNumber() {
        return logNum;
    }

    public void manualTrigger(long newtime) {
        secondsIncidentStarted = newtime;
        incidentOccured = true;
    }

    public long getSecondsToStart() {
        return startTime;
    }

    public void setSecondsToStart(long newStartTime) {
        startTime = newStartTime;
    }

    public Long getIncidentLength() {
        return eventList.lastElement().secondsToOccurInIncident
                - eventList.firstElement().secondsToOccurInIncident;
    }

    public boolean hasOccured() {
        return incidentOccured;
    }

    public void resetSimulation() {
        secondsIncidentStarted = 0;
        incidentOccured = false;

        for (IncidentEvent ie : eventList)
            ie.resetSimulation();

    }

    public void addEvent(IncidentEvent newEvent) {
        newEvent.eventInfo.setHeader(header);
        eventList.add(newEvent);
    }
    
    public String getMasterInc() {
        return masterInc;
    }

    public void setMasterInc(String masterInc) {
        this.masterInc = masterInc;
    }

    public int getLogNum() {
        return logNum;
    }

    public void setLogNum(int logNum) {
        this.logNum = logNum;
    }

    public String getOau() {
        return oau;
    }

    public void setOau(String oau) {
        this.oau = oau;
    }

    public long getTime() {
        return startTime;
    }

    public void setTime(long time) {
        this.startTime = time;
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }
    
    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IncidentCaller getIncidentCaller() {
        return incidentCaller;
    }

    public void setIncidentCaller(IncidentCaller incidentCaller) {
        this.incidentCaller = incidentCaller;
    }

    public IncidentLocation getIncidentLocation() {
        return incidentLocation;
    }

    public void setIncidentLocation(IncidentLocation incidentLocation) {
        this.incidentLocation = incidentLocation;
    }

    public IncidentProblem getProblem() {
        return problem;
    }

    public void setProblem(IncidentProblem problem) {
        this.problem = problem;
    }

    public IncidentGeneralInfo getGenInfo() {
        return genInfo;
    }

    public void setGenInfo(IncidentGeneralInfo genInfo) {
        this.genInfo = genInfo;
    }

    public LinkedList<String> getAssignedUnitNums() {
        return assignedUnitNums;
    }

    public void setPrimaryAssignedUnitNum(String assignedUnitNum) {
        assignedUnitNums.remove(assignedUnitNum);
        assignedUnitNums.addFirst(assignedUnitNum);
    }

    public void addAssignedUnitNum(String assignedUnitNum) {
        assignedUnitNums.addLast(assignedUnitNum);
    }
    
    public void removeAssignedUnitNum(String assignedUnitNum){
        assignedUnitNums.remove(assignedUnitNum);
        if(assignedUnitNums.size() == 0){
            status = IncidentEnums.Closed;
        }
    }

    public IncidentResponse getResponse() {
        return response;
    }

    public void setResponse(IncidentResponse response) {
        this.response = response;
    }

    public IncidentAdditionalInfo getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(IncidentAdditionalInfo additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public IncidentActivities getActivities() {
        return activities;
    }

    public void setActivities(IncidentActivities activities) {
        this.activities = activities;
    }

    public IncidentCallBacks getCallBacks() {
        return callBacks;
    }

    public void setCallBacks(IncidentCallBacks callBacks) {
        this.callBacks = callBacks;
    }

    public IncidentEditLog getEditLog() {
        return editLog;
    }

    public void setEditLog(IncidentEditLog editLog) {
        this.editLog = editLog;
    }

    public IncidentInfo getInfo() {
        return info;
    }

    public void setInfo(IncidentInfo info) {
        this.info = info;
    }

    public IncidentTimes getTimes() {
        return times;
    }

    public void setTimes(IncidentTimes times) {
        this.times = times;
    }

    public IncidentTransportInfo getTranspInfo() {
        return transpInfo;
    }

    public void setTranspInfo(IncidentTransportInfo transpInfo) {
        this.transpInfo = transpInfo;
    }

    public IncidentVehicle getIncidentVehicle() {
        return incidentVehicle;
    }

    public void setIncidentVehicle(IncidentVehicle incidentVehicle) {
        this.incidentVehicle = incidentVehicle;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public IncidentEnums getIncidentStatus() {
        return status;
    }

    public void setIncidentStatus(IncidentEnums status) {
        this.status = status;
    }

    public String getRp() {
        return rp;
    }

    public void setRp(String rp) {
        this.rp = rp;
    }

    public String getAli() {
        return ali;
    }

    public void setAli(String ali) {
        this.ali = ali;
    }

    public String getRpType() {
        return rpType;
    }

    public void setRpType(String rpType) {
        this.rpType = rpType;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public DefaultTableModel getCommentsNotesTable() {
        return commentsNotesTable;
    }

    public void addToCommentsNotesTable(String[] fields) {
        this.commentsNotesTable.addRow(fields);
    }

    /*
     * Stores the data needed to be displayed in the AssignedIncidents panel and
     * sends it out as a toTableVector.
     */
    public Vector<Object> toVectorForAssignedIncidents() {
        Vector<Object> vector = new Vector<Object>();

        toTableVector.clear();
        toTableVector.add(logNum);
        toTableVector.add(masterInc);
        toTableVector.add(alert);
        if (assignedUnitNums.size() != 0) {
            toTableVector.add(assignedUnitNums.getFirst());
        } else {
            toTableVector.add("");
        }

        String unitNums = "";
        for (int i = 0; i < assignedUnitNums.size(); i++) {
            unitNums = unitNums.concat((String) assignedUnitNums.get(i));
            if (i == assignedUnitNums.size() - 1) {
                break;
            }
            unitNums = unitNums.concat(", ");
        }
        toTableVector.add(unitNums);
        toTableVector.add(additionalInfo.getType());
        toTableVector.add(incidentLocation.getAddress());
        toTableVector.add(incidentLocation.getLoc());
        toTableVector.add(incidentLocation.getBeat());
        toTableVector.add(incidentLocation.getArea());
        toTableVector.add(oau);
        toTableVector.add(startTime);
        toTableVector.add(logTime);
        toTableVector.add(additionalInfo.getTypeCode());
        toTableVector.add(p);
        toTableVector.add(genInfo.getAgy());
        return toTableVector;
    }

    /*
     * Stores the data needed to be displayed in the PendingIncidents panel and
     * sends it out as a toTableVector.
     */
    public Vector<Object> toVectorForPendingIncidents() {
        Vector<Object> vector = new Vector<Object>();
        vector.add(logNum);
        vector.add(masterInc);
        vector.add(alert);
        vector.add(p);
        vector.add(additionalInfo.getType());
        vector.add(incidentLocation.getAddress());
        vector.add(incidentLocation.getLoc());
        vector.add(incidentLocation.getCity());
        vector.add(incidentLocation.getBeat());
        vector.add(incidentLocation.getArea());
        vector.add(oau);
        vector.add(startTime);
        vector.add(logTime);
        vector.add(additionalInfo.getTypeCode());
        vector.add(genInfo.getAgy());

        return vector;
    }

    public Vector<Object> toVectorForIncidentEditor() {
        toTableVector.clear();
        toTableVector.add(logTime);
        toTableVector.add(additionalInfo.getTypeCode());
        toTableVector.add(incidentLocation.getAddress());
        toTableVector.add(incidentLocation.getLoc());
        toTableVector.add(incidentLocation.getCity());
        toTableVector.add(masterInc);
        toTableVector.add("GG");
        toTableVector.add(incidentLocation.getArea());
        
        String unitNums = "";
        for (int i = 0; i < assignedUnitNums.size(); i++) {
            unitNums = unitNums.concat((String) assignedUnitNums.get(i));
            if (i == assignedUnitNums.size() - 1) {
                break;
            }
            unitNums = unitNums.concat(", ");
        }
        
        toTableVector.add("F-File");
        return toTableVector;
    }

    /*
     * Called from the tmc.simulator.cadclient.ScriptHandler.java. Handles
     * storing data based on script tag.
     */
    public void readXMLNode(String outer_tag_name, String inner_tag_name,
            String value) {
        if (outer_tag_name.equals(CAD_DATA_TAGS.LOCATION.tag)) {
            incidentLocation.readXMLNode(inner_tag_name, value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.CALLER.tag)) {
            incidentCaller.readXMLNode(inner_tag_name, value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.PROBLEM.tag)) {
            problem.readXMLNode(inner_tag_name, value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.GENERAL.tag)) {
            genInfo.readXMLNode(inner_tag_name, value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.RESPONSE.tag)) {
            response.readXMLNode(inner_tag_name, value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.ADDITIONAL_INFO.tag)) {
            additionalInfo.readXMLNode(inner_tag_name, value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.ACTIVITIES.tag)) {
            activities.readXMLNode(inner_tag_name, value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.CALL_BACKS.tag)) {
            callBacks.readXMLNode(inner_tag_name, value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.EDIT_LOG.tag)) {
            editLog.readXMLNode(inner_tag_name, value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.INFO.tag)) {
            info.readXMLNode(inner_tag_name, value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.TIMES.tag)) {
            times.readXMLNode(inner_tag_name, value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.TRANSPORT_INFO.tag)) {
            transpInfo.readXMLNode(inner_tag_name, value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.USER_DATA.tag)) {
            init_userData = value;
            setUserData(value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.ATTACHMENTS.tag)) {
            init_attachments = value;
            setAttachments(value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.RP.tag)) {
            init_rp = value;
            setRp(value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.ALI.tag)) {
            init_ali = value;
            setAli(value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.RP_TYPE.tag)) {
            init_rpType = value;
            setRpType(value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.MEDIA.tag)) {
            init_media = value;
            setMedia(value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.MASTER_INC_NUM.tag)) {
            //masterInc should be unchanged,no init
            setMasterInc(value);
        } else if (outer_tag_name.equals(CAD_DATA_TAGS.P.tag)) {
            init_p = value;
            setP(value);
        } 

    }
}
