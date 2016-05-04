package tmcsim.client.cadclientgui.screens;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import tmcsim.cadsimulator.Coordinator;
import tmcsim.client.cadclientgui.data.CADData;
import tmcsim.client.cadclientgui.data.Unit;
import tmcsim.interfaces.CoordinatorInterface;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * This class contains a reference to every single view-able component as well as a reference to the coordinator(which serves as the database).
 * All commands to access data or manipulate a screen outside of the individual screen should be done through this ScreenManager.
 * 
 * @author Stuart
 */

public class ScreenManager {
    
    private final static int ONE_SECOND = 1000;
    
    private static ActivityLogViewer activityLogViewer;
    private static AssignedIncidents assignedIncidents;
    //private static BOLOEntry boloEntry;
    private static CADMenu cadMenu;
    private static Cardfile cardfile;
    private static IncidentEditor incidentEditor;
    private static IncidentInfo incidentInfo;
    //private static IncidentSupplementPersonForm incidentSupplementPersonForm;
    private static IncidentViewer incidentViewer;
    private static Login login;
    private static PendingIncidents pendingIncidents;
    private static PowerlineUI powerlineUI1;
    private static PowerlineUI powerlineUI2;
    private static PowerlineUI powerlineUI3;
    private static PowerlineUI powerlineUI4;
    private static PowerlineUI powerlineUI5;
    private static int currPowerlineFocus = 1;
    //private static RotationServiceRequest rotationServiceRequest;
    private static Search search;
    private static UnitStatus unitStatus;
    //private static VehicleInformationEntry vehicleInformationEntry;
    
    //Reference to the Coordinator to use RMI methods
    protected static CoordinatorInterface theCoordinator;
    
    public ScreenManager(CoordinatorInterface theCoor){
        theCoordinator = theCoor;
        activityLogViewer = new ActivityLogViewer();
        
        assignedIncidents = new AssignedIncidents();
        assignedIncidents.addWindowListener(createWindowListener());

        //boloEntry = new BOLOEntry();
        //boloEntry.addWindowListener(createWindowListener());
        
        cadMenu = new CADMenu();
        cadMenu.addWindowListener(createWindowListener());
        
        cardfile = new Cardfile();
        cardfile.addWindowListener(createWindowListener());
        
        incidentEditor = new IncidentEditor();
        incidentEditor.addWindowListener(createWindowListener());
        
        incidentInfo = new IncidentInfo();
        incidentInfo.addWindowListener(createWindowListener());
        
        //incidentSupplementPersonForm = new IncidentSupplementPersonForm();
        //incidentSupplementPersonForm.addWindowListener(createWindowListener());
        
        incidentViewer = new IncidentViewer();
        incidentViewer.addWindowListener(createWindowListener());
        
        pendingIncidents = new PendingIncidents();
        pendingIncidents.addWindowListener(createWindowListener());

        powerlineUI1 = new PowerlineUI(false, 1);
        powerlineUI1.addWindowListener(createWindowListener());
        
        powerlineUI2 = new PowerlineUI(false, 2);
        powerlineUI2.addWindowListener(createWindowListener());
        
        powerlineUI3 = new PowerlineUI(false, 3);
        powerlineUI3.addWindowListener(createWindowListener());
        
        powerlineUI4 = new PowerlineUI(false, 4);
        powerlineUI4.addWindowListener(createWindowListener());
        
        powerlineUI5 = new PowerlineUI(false, 5);
        powerlineUI5.addWindowListener(createWindowListener());
        
        //rotationServiceRequest = new RotationServiceRequest();
        //rotationServiceRequest.addWindowListener(createWindowListener());
        
        search = new Search();
        search.addWindowListener(createWindowListener());
        unitStatus = new UnitStatus();
        unitStatus.addWindowListener(createWindowListener());
        
        //vehicleInformationEntry = new VehicleInformationEntry();
        //vehicleInformationEntry.addWindowListener(createWindowListener());
        
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addKeyEventDispatcher(new KeyEventDispatcher(){
            public boolean dispatchKeyEvent(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_F2 && e.getID() == KeyEvent.KEY_PRESSED
                        && e.getModifiers() == InputEvent.SHIFT_MASK){
                    openPendingIncidents();
                    return true;
                }
                if(e.getKeyCode() == KeyEvent.VK_F3 && e.getID() == KeyEvent.KEY_PRESSED
                        && e.getModifiers() == InputEvent.SHIFT_MASK){
                    openAssignedIncidents();
                    return true;
                }
                if(e.getKeyCode() == KeyEvent.VK_F4 && e.getID() == KeyEvent.KEY_PRESSED
                        && e.getModifiers() == InputEvent.SHIFT_MASK){
                    openUnitStatus();
                    return true;
                }
                if(e.getKeyCode() == KeyEvent.VK_F4 && e.getID() == KeyEvent.KEY_PRESSED){
                    cyclePowerlineUI();
                    return true;
                }
                if(e.getKeyCode() == KeyEvent.VK_F5 && e.getID() == KeyEvent.KEY_PRESSED){
                    openPowerlineUI();
                    return true;
                }
                if(e.getKeyCode() == KeyEvent.VK_F9 && e.getID() == KeyEvent.KEY_PRESSED){
                    openCardfile();
                    return true;
                }
                if(e.getKeyCode() == KeyEvent.VK_F10 && e.getID() == KeyEvent.KEY_PRESSED){
                    putRCARDinPL();
                    return true;
                }
                return false;
            }
        });
        
        handleUpdateTimes();
    }
    
    public WindowListener createWindowListener(){
        return new WindowListener(){
            public void windowActivated(WindowEvent e) {
                closeDropDownMenus();
            }
            public void windowClosed(WindowEvent e) {
                closeDropDownMenus();
            }
            public void windowClosing(WindowEvent e) {
                closeDropDownMenus();
            }
            public void windowDeactivated(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {
                closeDropDownMenus();
            }
            public void windowOpened(WindowEvent e) {}
        };
    }
    
    public static void openActivityLogViewer(){ activityLogViewer.open(); }
    public static void closeActivityLogViewer(){ activityLogViewer.close(); }
    
    public static void openAssignedIncidents(){ assignedIncidents.open(); }
    public static void closeAssignedIncidents(){ assignedIncidents.close(); }
    
    //public static void openBOLOEntry(){ boloEntry.open(); }
    //public static void closeBOLOEntry(){ boloEntry.close(); }
    
    public static void openCADMenu(){ cadMenu.open(); }
    public static void closeCADMenu(){ cadMenu.close(); }
    
    public static void openCardfile(){ cardfile.open(); }
    public static void closeCardfile(){ cardfile.close(); }
    
    public static void openIncidentEditor(){ incidentEditor.open(); }
    public static void closeIncidentEditor(){ incidentEditor.close(); }
    public static void refreshIncidentEditor(){ incidentEditor.refreshInformation(); }
    
    public static void openIncidentInfo(int incidentId){ incidentInfo.open(incidentId); }
    public static void closeIncidentInfo(){ incidentInfo.close(); }
    
    //public static void openIncidentSupplementPersonForm(){ incidentSupplementPersonForm.open(); }
    //public static void closeIncidentSupplementPersonForm(){ incidentSupplementPersonForm.close(); }
    
    public static void openIncidentViewer(int incidentId){ incidentViewer.open(incidentId); }
    public static void closeIncidentViewer(){ incidentViewer.close(); }
    
    public static void openPendingIncidents(){ pendingIncidents.open(); }
    public static void closePendingIncidents(){ pendingIncidents.close(); }
    
    public static void openPowerlineUI(){ 
        if(!powerlineUI1.isVisible()){
            powerlineUI1.open();
        }else if(!powerlineUI2.isVisible()){
            powerlineUI2.open();
        }else if(!powerlineUI3.isVisible()){
            powerlineUI3.open();
        }else if(!powerlineUI4.isVisible()){
            powerlineUI4.open();
        }else if(!powerlineUI5.isVisible()){
            powerlineUI5.open();
        }    
    }
    
    public static void cyclePowerlineUI(){
        if(currPowerlineFocus == 1){
            currPowerlineFocus = 2;
            powerlineUI1.setVisible(true);
            powerlineUI1.requestFocus();
            powerlineUI1.clearText();
        }
        else if(currPowerlineFocus == 2){
            currPowerlineFocus = 3;
            powerlineUI2.setVisible(true);
            powerlineUI2.requestFocus();
            powerlineUI2.clearText();
        }
        else if(currPowerlineFocus == 3){
            currPowerlineFocus = 4;
            powerlineUI3.setVisible(true);
            powerlineUI3.requestFocus();
            powerlineUI3.clearText();
        }
        else if(currPowerlineFocus == 4){
            currPowerlineFocus = 5;
            powerlineUI4.setVisible(true);
            powerlineUI4.requestFocus();
            powerlineUI4.clearText();
        }
        else if(currPowerlineFocus == 5){
            currPowerlineFocus = 1;
            powerlineUI5.setVisible(true);
            powerlineUI5.requestFocus();
            powerlineUI5.clearText();
        }
    }
    
    public static void putRCARDinPL(){
        if(powerlineUI2.hasFocus()){
            powerlineUI2.putRCARD();
        }
        else if(powerlineUI3.hasFocus()){
            powerlineUI3.putRCARD();
        }
        else if(powerlineUI4.hasFocus()){
            powerlineUI4.putRCARD();
        }
        else if(powerlineUI5.hasFocus()){
            powerlineUI5.putRCARD();
        }
        else{
            powerlineUI1.setVisible(true);
            powerlineUI1.putRCARD();
        }
    }
    
    //public static void openRotationServiceRequest(){ rotationServiceRequest.open(); }
    //public static void closeRotationServiceRequest(){ rotationServiceRequest.close(); }
    
    public static void openSearch(){ search.open(); }
    public static void closeSearch(){ search.close(); }
    
    public static void openUnitStatus(){ unitStatus.open(); }
    public static void closeUnitStatus(){ unitStatus.close(); }
    
    //public static void openVehicleInformationEntry(){ vehicleInformationEntry.open(); }
    //public static void closeVehicleInformationEntry(){ vehicleInformationEntry.close(); }
    
    public static void createPowerlineSearch(String search){
        new PowerlineSearch(search);
    }
    
    public static void closeDropDownMenus(){
        cadMenu.closeMoreMenu();
        cadMenu.closeToolMenu();
        assignedIncidents.closeDropDownMenu();
        unitStatus.closeDropDownMenu();
        unitStatus.closeDropDownWithAssignedIncMenu();
        pendingIncidents.closeDropDownMenu();
    }
    
    public static void refreshScreens(){
        assignedIncidents.refreshTable();
        unitStatus.refreshTable();
        pendingIncidents.refreshTable();
    }
    
    public static void setUserName(String username){
        cadMenu.setName(username);
    }
    
    /**
     * This method calls CADMenu's and UnitStatus's update time method.
     */
    public static void handleUpdateTimes(){
        Timer timer = new Timer(ONE_SECOND, new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                refreshScreens();
                cadMenu.handleUpdateTime();
                unitStatus.handleUpdateTime();
            }
        });
        timer.start();
    }
    
    /**
     * Removes drag and drop/button clicking in unitStatus panel if bool is false.
     * This method should only be called from Login.java
     */
    public static void setDispatcherAuthority(boolean bool){
        if (!bool){
            unitStatus.removeDispatcherAuthority();
            cadMenu.removeDispatcherStatus();
        }
    }
}
