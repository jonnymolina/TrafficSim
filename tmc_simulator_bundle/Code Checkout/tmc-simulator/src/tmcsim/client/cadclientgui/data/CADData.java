package tmcsim.client.cadclientgui.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Vector;

import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import tmcsim.client.cadclientgui.enums.IncidentEnums;
import tmcsim.client.cadclientgui.enums.TableHeaders;
import tmcsim.client.cadclientgui.enums.UnitStatusEnums;
import tmcsim.interfaces.CADClientInterface;

/**
 * This class holds the data for all the units and incidents. It is responsible
 * for sending out data to all the screens.
 * 
 * @author Vincent
 * 
 */
public class CADData implements Serializable {

    private final int ONE_SECOND = 1000;

    private static Vector<Unit> units;
    private static Vector<Incident> incidents;
    private static Vector<String> assignedIncidentsHeaders;
    private static Vector<String> unitStatusHeaders;
    private static Vector<String> pendingIncidentsHeaders;
    private static Vector<String> incidentEditorHeaders;
    private static Vector<Object> toUnitTableVector;
    private static Vector<Object> toPendingTableVector;
    private static Vector<Object> toAssignedTableVector;
    private static Vector<Object> toIncidentEditorVector;
    private static Vector<Object> toCommentsNotesVector;
    private static DefaultTableModel pendingIncidentsTableModel;
    private static DefaultTableModel unitStatusTableModel;
    private static DefaultTableModel assignedIncidentsTableModel;
    private static DefaultTableModel incidentEditorModel;

    public CADData() {
        units = new Vector<Unit>();
        incidents = new Vector<Incident>();

        toUnitTableVector = new Vector<Object>();
        toPendingTableVector = new Vector<Object>();
        toAssignedTableVector = new Vector<Object>();
        toIncidentEditorVector = new Vector<Object>();
        toCommentsNotesVector = new Vector<Object>();
        assignedIncidentsHeaders = new Vector<String>();
        pendingIncidentsHeaders = new Vector<String>();
        unitStatusHeaders = new Vector<String>();
        incidentEditorHeaders = new Vector<String>();
        initializeAssignedIncidentsSettings();
        initializeUnitStatusSettings();
        initializePendingIncidentsSettings();
        initializeIncidentEditorSettings();

        handleUpdateTimes();
    }
    
    public void clearData(){
        units = new Vector<Unit>();
        incidents = new Vector<Incident>();
    }
    
    public void resetSimulation(){
        for(Incident inc : incidents){
            inc.resetCADDataSimulation();
        }
        for(Unit unit : units){
            unit.resetCADDataSimulation();
        }
    }

    private void initializeAssignedIncidentsSettings() {
        Collections
                .addAll(assignedIncidentsHeaders, TableHeaders.ASSIGNED_INCIDENTS_HEADERS);
        assignedIncidentsTableModel = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;// This causes all cells to be not editable
            }
        };
        assignedIncidentsTableModel
                .setColumnIdentifiers(TableHeaders.ASSIGNED_INCIDENTS_HEADERS);
    }

    private void initializeUnitStatusSettings() {
        Collections.addAll(unitStatusHeaders, TableHeaders.UNIT_STATUS_HEADERS);
        unitStatusTableModel = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;// This causes all cells to be not editable
            }
        };
        unitStatusTableModel.setColumnIdentifiers(TableHeaders.UNIT_STATUS_HEADERS);
    }

    private void initializeIncidentEditorSettings() {
        Collections.addAll(incidentEditorHeaders, TableHeaders.INCIDENT_EDITOR_HEADERS);
        incidentEditorModel = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;// This causes all cells to be not editable
            }
        };
        incidentEditorModel.setColumnIdentifiers(TableHeaders.INCIDENT_EDITOR_HEADERS);
    }

    private void initializePendingIncidentsSettings() {
        Collections.addAll(pendingIncidentsHeaders, TableHeaders.PENDING_INCIDENTS_HEADERS);
        pendingIncidentsTableModel = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;// This causes all cells to be not editable
            }
        };
        pendingIncidentsTableModel
                .setColumnIdentifiers(TableHeaders.PENDING_INCIDENTS_HEADERS);
    }

    /**
     * Obtains the list of units from the XML.
     * 
     * @param unit
     *            the new list of units.
     */
    public void setUnitsFromXML(Vector<Unit> unit) {
        units = unit;
    }

    /**
     * Obtains the list of incidents from the XML.
     * 
     * @param incident
     *            the new list of incidents.
     */
    public void setIncidentsFromXML(Vector<Incident> incident) {
        incidents = incident;
    }

    /**
     * Returns the specified unitNum
     */
    public Unit getUnit(String unitNum) {
        for (int i = 0; i < units.size(); i++) {
            if (units.get(i).getUnitNum().equals(unitNum)) {
                return units.get(i);
            }
        }
        return null;
    }

    /**
     * Check if it contains an incident with the specified Id.
     */
    public boolean checkForValidId(int incidentId) {
        for (int i = 0; i < incidents.size(); i++) {
            if (incidents.get(i).getLogNum() == incidentId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Uses an Incident's masterInc to look up its corresponding ID.
     * 
     * @param masterInc
     *            the Incident's masterInc
     * @return the Incident's ID, -1 if invalid masterInc
     */
    public int getIncidentId(String masterInc) {
        for (int i = 0; i < incidents.size(); i++) {
            if (incidents.get(i).getMasterInc().equals(masterInc)) {
                return incidents.get(i).getLogNum();
            }
        }
        return -1;
    }

    /**
     * Returns the specified incident
     */
    public Incident getIncident(int incidentId) {
        for (int i = 0; i < incidents.size(); i++) {
            if (incidents.get(i).getLogNum() == incidentId) {
                return incidents.get(i);
            }
        }
        return null;
    }

    /**
     * Sends all the objects for UnitStatus
     * 
     * @return DefaultTableModel for UnitStatus
     */
    public DefaultTableModel tableForUnitStatus() {
        toUnitTableVector.clear();
        for (int i = 0; i < units.size(); i++) {
            toUnitTableVector.add(units.get(i).toVector());
        }
        unitStatusTableModel
                .setDataVector(toUnitTableVector, unitStatusHeaders);
        return unitStatusTableModel;
    }

    /**
     * Sends all the objects for AssignedIncidents
     * 
     * @return DefaultTableModel for AssignedIncidents
     */
    public DefaultTableModel tableForAssignedIncidents() {
        toAssignedTableVector.clear();
        for (int i = 0; i < incidents.size(); i++) {
            if (incidents.get(i).hasOccured()
                    && incidents.get(i).getIncidentStatus() == IncidentEnums.Assigned) {
                toAssignedTableVector.add(incidents.get(i)
                        .toVectorForAssignedIncidents());
            }
        }
        assignedIncidentsTableModel.setDataVector(toAssignedTableVector,
                assignedIncidentsHeaders);
        return assignedIncidentsTableModel;
    }

    /**
     * Sends all the objects for PendingIncidents
     * 
     * @return DefaultTableModel for PendingIncidents
     */
    public DefaultTableModel tableForPendingIncidents() {
        toPendingTableVector.clear();
        for (int i = 0; i < incidents.size(); i++) {
            if (incidents.get(i).hasOccured()
                    && incidents.get(i).getIncidentStatus() == IncidentEnums.Pending) {
                toPendingTableVector.add(incidents.get(i)
                        .toVectorForPendingIncidents());
            }
        }
        pendingIncidentsTableModel.setDataVector(toPendingTableVector,
                pendingIncidentsHeaders);
        return pendingIncidentsTableModel;
    }

    /**
     * Sends all the objects for IncidentEditor
     * 
     * @return DefaultTableModel for IncidentEditor
     */
    public DefaultTableModel tableForIncidentEditor() {
        toIncidentEditorVector.clear();
        for (int i = 0; i < incidents.size(); i++) {
            if (incidents.get(i).hasOccured()) {
                toIncidentEditorVector.add(incidents.get(i)
                        .toVectorForIncidentEditor());
            }
        }
        incidentEditorModel.setDataVector(toIncidentEditorVector,
                incidentEditorHeaders);
        return incidentEditorModel;
    }

    /**
     * Sets up a timer to subtract one from the unit's timer every second.
     */
    public void handleUpdateTimes() {
        Timer timer = new Timer(ONE_SECOND, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < units.size(); i++) {
                    units.get(i).TimerMinusSecond();
                }
            }
        });
        timer.start();
    }
    
    /**
     * Assigns the specified unit to the specified incident and updates information in both screens
     * @param unitNum the unit being assigned
     * @param incidentNum the incident being assigned to
     * @param primary whether or not the assigned unit is the primary unit
     */
    
    public void unitAssignedToIncident(String unitNum, int incidentNum, boolean primary){
        if(primary){
            getIncident(incidentNum).setPrimaryAssignedUnitNum(unitNum);
            getIncident(incidentNum).setIncidentStatus(IncidentEnums.Assigned);
        }else{
            getIncident(incidentNum).addAssignedUnitNum(unitNum);
        }
            
        getUnit(unitNum).setAssignedIncidentId(incidentNum);
        getUnit(unitNum).setMasterInc(getIncident(incidentNum).getMasterInc());
        getUnit(unitNum).setStatus("ENRT");
        getUnit(unitNum).setUnitStatus(UnitStatusEnums.Enroute);
        getUnit(unitNum).setType(getIncident(incidentNum).getAdditionalInfo().getType());
        getUnit(unitNum).setDestination(getIncident(incidentNum).getIncidentLocation().getAddress());
        getUnit(unitNum).setArea(getIncident(incidentNum).getIncidentLocation().getArea()); 
        getUnit(unitNum).setP(getIncident(incidentNum).getP());
    }
    
    /**
     * Updates information in both incident and unit screens for when a unit has arrived at scene
     * aka that the unit status is "10-97"
     * @param unitNum the unit which arrived at the scene
     */
    public void unitArrivedAtIncidentScene(String unitNum, int incidentNum, boolean primary){
        if(getUnit(unitNum).getAssignedIncidentId() != incidentNum){
            if(primary){
                getIncident(incidentNum).setPrimaryAssignedUnitNum(unitNum);
                getIncident(incidentNum).setIncidentStatus(IncidentEnums.Assigned);
            }else{
                getIncident(incidentNum).addAssignedUnitNum(unitNum);
            }
            getUnit(unitNum).setAssignedIncidentId(incidentNum);
            getUnit(unitNum).setMasterInc(getIncident(incidentNum).getMasterInc());
            getUnit(unitNum).setType(getIncident(incidentNum).getAdditionalInfo().getType());
            getUnit(unitNum).setDestination(getIncident(incidentNum).getIncidentLocation().getAddress());
            getUnit(unitNum).setArea(getIncident(incidentNum).getIncidentLocation().getArea()); 
            getUnit(unitNum).setP(getIncident(incidentNum).getP());
        }
        getUnit(unitNum).setStatus("10-97");
        getUnit(unitNum).setUnitStatus(UnitStatusEnums.Arrived);
        getUnit(unitNum).setCurrentLocation(getUnit(unitNum).getDestination());
    }
    
    /**
     * Updates information in both incident and unit screens for when a unit has become available again
     * aka that the unit status is "10-98"
     * @param unitNum the unit which arrived at the scene
     */
    public void unitAvailable(String unitNum){
        getIncident(getUnit(unitNum).getAssignedIncidentId()).removeAssignedUnitNum(unitNum);
        
        getUnit(unitNum).setStatus("10-98");
        getUnit(unitNum).setUnitStatus(UnitStatusEnums.Assignable);
        getUnit(unitNum).setAssignedIncidentId(-1);
        getUnit(unitNum).setMasterInc("");
        getUnit(unitNum).setType("");
        getUnit(unitNum).setDestination("");
        getUnit(unitNum).setP("");
        
    }
}
