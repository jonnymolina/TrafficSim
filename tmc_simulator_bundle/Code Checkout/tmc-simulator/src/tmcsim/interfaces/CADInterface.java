package tmcsim.interfaces;

import java.awt.List;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

import javax.swing.JList;
import javax.swing.table.DefaultTableModel;

import tmcsim.client.cadclientgui.data.CADData;
import tmcsim.client.cadclientgui.data.CardfileDataObject;
import tmcsim.client.cadclientgui.data.CardfileList;
import tmcsim.client.cadclientgui.data.ChangeLog;
import tmcsim.client.cadclientgui.enums.CADScriptTags.UNIT_TAGS;
import tmcsim.client.cadclientgui.enums.CADScriptTags;
import tmcsim.client.cadclientgui.enums.IncidentEnums;
import tmcsim.client.cadclientgui.enums.UnitStatusEnums;
import tmcsim.client.cadclientgui.enums.CADDataEnums.*;

public interface CADInterface extends Remote{

    /**
     * Registers a remote CADClient for callback.  
     *
     * @param client Interface to the CADClient for callback RMI
     * @throws RemoteException if there is an error in the RMI communication.
     */
    public void registerForCallback(CADClientInterface client)
        throws RemoteException;

    /**
     * Unregisters a remote CADClient from callback.  
     * @param client Interface to the CADClient for callback RMI
     * @throws RemoteException if there is an error in the RMI communication.
     */
    public void unregisterForCallback(CADClientInterface client)
        throws RemoteException;
    
    /**
     * Checks the CADData for an existing incident with id
     * @param id the incident id
     * @return true if CADData contains such an incident, otherwise false.
     * @throws RemoteException
     */
    public boolean checkForValidIncidentID(int id) throws RemoteException;
    
    /**
     * Uses an Incident's masterInc to lookup its ID.
     * @param masterInc the Incident to look up
     * @return the same Incident's ID, -1 if invalid masterInc
     * @throws RemoteException
     */
    public int getIncidentId(String masterInc) throws RemoteException;
    
    /**
     * Returns a table model out of CADData based on tag.
     * @param tag a CADDataEnums tag
     * @throws RemoteException
     */
    public DefaultTableModel getCadDataTable(TABLE tag) throws RemoteException;
    
    /**
    * Returns the specified incident's table based on the tag.
    * @param tag a CADDataEnums tag
    * @param incidentId the incident's ID to look up
    * @return
    * @throws RemoteException
    */
   public DefaultTableModel getCadDataIncidentTable(INC_TABLE tag, int incidentId) throws RemoteException;
   
   /**
    * Adds a row of data to a specified incident's table based on the tag.
    * @param tag a CADDataEnums tag
    * @param incidentId the incident's ID to look up
    * @fields the row of fields to add to the specified table
    * @throws RemoteException
    */
   public void addCadDataIncidentTable(INC_TABLE tag, int incidentId, String[] fields) throws RemoteException;
    
    /**
     * Returns an object out of Incident based on tag.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public Object getCadDataIncVal(INC_VAL tag, int incidentId) throws RemoteException;
    
    /**
     * Set incidentId's field based on tag to value.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void setCadDataIncVal(INC_VAL tag, int incidentId, String value) throws RemoteException;
    
    /**
     * Returns a string out of IncidentLocation based on tag.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncLoc(INC_LOC tag, int incidentId) throws RemoteException;
    
    /**
     * Set incidentId's field based on tag to value.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void setCadDataIncLoc(INC_LOC tag, int incidentId, String value) throws RemoteException;
    
    /**
     * Returns a string out of IncidentCaller based on tag.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncCaller(INC_CALLER tag, int incidentId) throws RemoteException;
    
    /**
     * Set incidentId's field based on tag to value.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void setCadDataIncCaller(INC_CALLER tag, int incidentId, String value) throws RemoteException;
    
    /**
     * Returns a string out of IncidentProblem based on tag.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncProblem(INC_PROBLEM tag, int incidentId) throws RemoteException;
    
    /**
     * Set incidentId's field based on tag to value.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void setCadDataIncProblem(INC_PROBLEM tag, int incidentId, String value) throws RemoteException;
    
    /**
     * Returns a string out of IncidentGeneralInfo based on tag.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncGenInfo(INC_GEN_INFO tag, int incidentId) throws RemoteException;
    
    /**
     * Set incidentId's field based on tag to value.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void setCadDataIncGenInfo(INC_GEN_INFO tag, int incidentId, String value) throws RemoteException;
    
    /**
     * Returns a string out of IncidentResponse based on tag.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncResp(INC_RESP tag, int incidentId) throws RemoteException;
    
    /**
     * Set incidentId's field based on tag to value.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void setCadDataIncResp(INC_RESP tag, int incidentId, String value) throws RemoteException;
    
    /**
     * Returns a string out of IncidentAdditionalInfo based on tag.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncAddInfo(INC_ADD_INFO tag, int incidentId) throws RemoteException;
    
    /**
     * Set incidentId's field based on tag to value.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void setCadDataIncAddInfo(INC_ADD_INFO tag, int incidentId, String value) throws RemoteException;
    
    /**
     * Returns a string out of IncidentActivities based on tag.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncActivities(INC_ACTIVITIES tag, int incidentId) throws RemoteException;
    
    /**
     * Set incidentId's field based on tag to value.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void setCadDataIncActivities(INC_ACTIVITIES tag, int incidentId, String value) throws RemoteException;
    
    /**
     * Returns a string out of IncidentCallback based on tag.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncCallBack(INC_CALLBACK tag, int incidentId) throws RemoteException;
    
    /**
     * Returns a string out of IncidentEditLog based on tag.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncEditLog(INC_EDIT_LOG tag, int incidentId) throws RemoteException;
    
    /**
     * Returns a string out of IncidentInfo based on tag.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncInfo(INC_INFO tag, int incidentId) throws RemoteException;
    
    /**
     * Returns a string out of IncidentTimes based on tag.
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncTimes(INC_TIMES tag, int incidentId) throws RemoteException; 
    
    /**
     * Returns a CardfileList matching the tag.
     */
    public CardfileList getCardfileList(CARDFILE tag) throws RemoteException;
    
    /**
     * Receives the fields of the ChangeLog in Cardfile.java and uses those fields
     * to make changes to its own copy of the Cardfiledata.
     */
    public void editCardfile(ChangeLog log) throws RemoteException;
    
    /**
     * Obtain a new unique ID for a cardfileDataObject.
     */
    public int obtainNewUniqueId() throws RemoteException;
    
    /**
     * Returns the unitNum's status.
     * @param unitNum the unitNum to look up
     * @throws RemoteException
     */
    public UnitStatusEnums getCadDataUnitStatus(String unitNum) throws RemoteException;
    
    /**
     * Returns a string field from Unit based on tag.
     * @param tag a CADScriptTags tag
     * @param unitNum the unitNum to look up
     * @return A string value
     * @throws RemoteException
     */
    public String getCadDataUnitValue(String unitNum, CADScriptTags.UNIT_TAGS tag) throws RemoteException;
    
    /**
     * Sets a field from Unit based on tag.
     * @param tag a CADScriptTags tag
     * @param unitNum the unitNum to look up
     * @param value the new value to be set
     * @throws RemoteException
     */
    public void setCadDataUnitValue(String unitNum, UNIT_TAGS tag, Object value) throws RemoteException;
    
    /**
     * Assigns a unit to the specified incident.
     * @param unitNum the unitNum to look up
     * @param id the incident id that this unit is assigned to.
     * @throws RemoteException
     */
    public void setCadDataUnitAssignedId(String unitNum, int id) throws RemoteException;
    
    /**
     * Adds a unit to the incident's list of assigned units.
     * @param incidentId the incident
     * @param assignedUnitNum the unit that's been assigned to the incident
     * @throws RemoteException
     */
    public void addCadDataIncidentAssignedUnitNum(int incidentId, String assignedUnitNum) throws RemoteException;
    
    /**
     * Sets the specified incident to the specified status.
     * @param incidentId the incident
     * @param status the status of the incident
     * @throws RemoteException
     */
    public void setCadDataIncidentStatus(int incidentId, IncidentEnums status) throws RemoteException;
    
    /** 
     * Returns a LinkedList containing entries that match the search string.
     * @param search the string entry to be searched
     * @return linked list of CardfileDataObjects matching search
     */
    public LinkedList<CardfileDataObject> getSearchList(String search) throws RemoteException;
}

