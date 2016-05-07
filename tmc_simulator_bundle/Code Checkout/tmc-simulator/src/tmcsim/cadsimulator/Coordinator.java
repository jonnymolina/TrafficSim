package tmcsim.cadsimulator;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Observer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.SAXParserFactory;

import tmcsim.cadmodels.CADRoutedMessage;
import tmcsim.cadmodels.CMSInfo;
import tmcsim.cadmodels.IncidentBoardModel_obj;
import tmcsim.cadmodels.IncidentInquiryModel_obj;
import tmcsim.cadmodels.IncidentInquiryUnitsAssigned;
import tmcsim.cadmodels.IncidentSummaryModel_obj;
import tmcsim.cadsimulator.db.CMSDiversionDB;
import tmcsim.cadsimulator.managers.ATMSManager;
import tmcsim.cadsimulator.managers.IncidentManager;
import tmcsim.cadsimulator.managers.MediaManager;
import tmcsim.cadsimulator.managers.ParamicsSimulationManager;
import tmcsim.cadsimulator.managers.SimulationControlManager;
import tmcsim.cadsimulator.viewer.model.SimulationStatusPanelModel;
import tmcsim.client.cadclientgui.CardfileReader;
import tmcsim.client.cadclientgui.ScriptHandler;
import tmcsim.client.cadclientgui.data.CADData;
import tmcsim.client.cadclientgui.data.CardfileData;
import tmcsim.client.cadclientgui.data.CardfileDataObject;
import tmcsim.client.cadclientgui.data.CardfileList;
import tmcsim.client.cadclientgui.data.ChangeLog;
import tmcsim.client.cadclientgui.data.Incident;
import tmcsim.client.cadclientgui.data.IncidentEvent;
import tmcsim.client.cadclientgui.enums.CADDataEnums.*;
import tmcsim.client.cadclientgui.enums.CADScriptTags.UNIT_TAGS;
import tmcsim.client.cadclientgui.enums.IncidentEnums;
import tmcsim.client.cadclientgui.enums.UnitStatusEnums;
import tmcsim.common.ObserverMessage;
import tmcsim.common.ScriptException;
import tmcsim.common.SimulationException;
import tmcsim.common.XMLIncident;
import tmcsim.common.CADEnums.PARAMICS_STATUS;
import tmcsim.common.CADEnums.SCRIPT_STATUS;
import tmcsim.interfaces.CADClientInterface;
import tmcsim.interfaces.CoordinatorInterface;
import tmcsim.interfaces.SimulationManagerInterface;

/**
 * Coordinator is used to control and manage interactions between all CAD Simulator
 * Managers. The Coordinator is also registered as an RMI Object to allow remote control
 * and access to simulation data. A SimulationManagerInterface Object is used to provide
 * communication to a remotely connected SimulationManager. Observers may register with
 * the Coordinator to listen for simulation data updates.
 *
 * @see ATMSManager
 * @see IncidentManager
 * @see MediaManager
 * @see ParamicsSimulationManager
 * @see SimulationControlManager
 * @author Jonathan Molina
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class Coordinator extends UnicastRemoteObject
        implements CoordinatorInterface
{
    /**
     * Error logger.
     */
    private Logger coorLogger = Logger.getLogger("tmcsim.cadsimulator");
    /**
     * List of all Observers that have been registered with the Coordinator. Due to being
     * a remote object, Coordinator cannot extend the Observable class. Therefore, the
     * list is managed manually.
     */
    private Vector<Observer> observerList = null;
    /**
     * Interface object for RMI communication with the Simulation Manager. Only one
     * Simulation Manager may be connected at a time. When there is no connected Manager,
     * this object is set to null.
     */
    private static SimulationManagerInterface managerInt = null;
    private static LinkedList<CADClientInterface> clientList;
    private static CADData cadData;
    private static CardfileData cardfileData;
    private SimulationStatusPanelModel simStatusPanelModel;

    /**
     * Constructor. Call UnicastRemoteObject constructor and call initializeSimulation.
     *
     * @throws RemoteException
     */
    public Coordinator(SimulationStatusPanelModel statusPanelModel)
            throws RemoteException
    {
        super();
        simStatusPanelModel = statusPanelModel;
        clientList = new LinkedList<CADClientInterface>();
        cadData = new CADData();
        cardfileData = new CardfileData();
        try
        {
            CardfileReader cfr =
                    new CardfileReader("scripts/Cardfile.xml", cardfileData);
        }
        catch (Exception e)
        {
            System.out.println("Could not load cardfile script");
        }

        observerList = new Vector<Observer>();
    }

    public void registerForCallback(CADClientInterface client) throws RemoteException
    {
        clientList.add(client);
        simStatusPanelModel.connectClient();
    }

    public void unregisterForCallback(CADClientInterface client) throws RemoteException
    {
        clientList.remove(client);
        simStatusPanelModel.disconnectClient();
    }

    public void registerForCallback(SimulationManagerInterface simManInt) 
            throws RemoteException
    {
        managerInt = simManInt;
        simStatusPanelModel.setSimManagerStatus(true);
    }

    public void unregisterForCallback(SimulationManagerInterface simManInt) 
            throws RemoteException
    {
        managerInt = null;
        simStatusPanelModel.setSimManagerStatus(false);
    }

    public void startSimulation() throws RemoteException, ScriptException
    {
        if (!CADSimulator.theIncidentMgr.areIncidentsLoaded())
        {
            throw new ScriptException(ScriptException.NO_SCRIPT_LOADED);
        }
        else if (CADSimulator.theParamicsSimMgr.isConnected())
        {
            Runnable startRun = new Runnable()
            {
                public void run()
                {
                    try
                    {
                        setScriptStatus(SCRIPT_STATUS.ATMS_SYNCHRONIZATION);

                        long currentATMSTime = CADSimulator.theATMSMgr.getCurrentTime();
                        long sleepTime = ((60 * 1000) - (currentATMSTime % (60 * 1000))) % (30 * 1000);
                        coorLogger.logp(Level.INFO, "Coordinator", "StartSimulation",
                                "Sleeping for " + sleepTime / 1000 + " seconds.");
                        Thread.sleep(sleepTime);

                        //currentATMSTime += sleepTime;                 
                        //ParamicsCommunicator.getInstance().serverTime.setTimeInMillis(currentATMSTime);                                       
                    }
                    catch (Exception e)
                    {
                        setScriptStatus(SCRIPT_STATUS.SCRIPT_RUNNING);

                        coorLogger.logp(Level.SEVERE, "Coordinator", "StartSimulation:run",
                                "Unable to connect to ATMS server.", e);
                    }
                    finally
                    {
                        CADSimulator.theSimulationCntrlMgr.startSimulation();
                        CADSimulator.theParamicsSimMgr.startSimulation();
                        CADSimulator.theSoundPlayer.setAudioEnabled(true);
                    }
                }
            };

            Thread startThread = new Thread(startRun);
            startThread.start();
        }
        else
        {
            CADSimulator.theSimulationCntrlMgr.startSimulation();
            CADSimulator.theSoundPlayer.setAudioEnabled(true);
        }
    }

    public void pauseSimulation() throws RemoteException
    {
        CADSimulator.theSimulationCntrlMgr.pauseSimulation();
        CADSimulator.theSoundPlayer.setAudioEnabled(false);
    }

    public void resetSimulation() throws RemoteException
    {

        CADSimulator.theIncidentMgr.resetIncidents();
        cadData.resetSimulation();

        CADSimulator.theSoundPlayer.setAudioEnabled(false);
        CADSimulator.theSoundPlayer.deQueueAll();

        simStatusPanelModel.setTime(0);

        setScriptStatus(SCRIPT_STATUS.SCRIPT_STOPPED_NOT_STARTED);

        CMSDiversionDB.getInstance().resetDiversions();

        CADSimulator.theSimulationCntrlMgr.resetSimulation();
        CADSimulator.theParamicsSimMgr.resetSimulation();

        notifyObservers(new ObserverMessage(
                ObserverMessage.messageType.RESET_SIMULATION, null));

    }

    public void gotoSimulationTime(final long newSimTime) throws RemoteException
    {

        boolean audioWasEnabled = CADSimulator.theSoundPlayer.getAudioEnabled();

        CADSimulator.theSoundPlayer.setAudioEnabled(false);

        long tempTime = 0;
        while (tempTime < newSimTime)
        {
            tempTime++;

            CADSimulator.theIncidentMgr.tick(tempTime);
        }
        CADSimulator.theSoundPlayer.setAudioEnabled(audioWasEnabled);


        CADSimulator.theSimulationCntrlMgr.gotoSimulationTime(newSimTime);

        Runnable gotoRun = new Runnable()
        {
            public void run()
            {
                simStatusPanelModel.setTime(newSimTime);

                if (managerInt != null)
                {
                    try
                    {
                        managerInt.tick(newSimTime);
                    }
                    catch (RemoteException re)
                    {
                        //Simulation Manager has disappeared
                        managerInt = null;
                        simStatusPanelModel.setSimManagerStatus(false);

                        coorLogger.logp(
                            Level.SEVERE, "Coordinator", "gotoSimulationTime:run",
                            "Connection to Simulation Manager has been dropped.", re);
                    }
                }
            }
        };

        Thread gotoThread = new Thread(gotoRun);
        gotoThread.start();

    }

    /**
     * Sets the current script status. A thread is started to notify the
     * CADSimulatorViewer and SimulationManager with the new status.
     */
    public void setScriptStatus(final SCRIPT_STATUS status)
    {

        Runnable updateRun = new Runnable()
        {
            public void run()
            {

                simStatusPanelModel.setScriptStatus(status);

                if (managerInt != null)
                {
                    try
                    {
                        managerInt.setScriptStatus(status);
                    }
                    catch (RemoteException re)
                    {
                        //Simulation Manager has disappeared
                        managerInt = null;
                        simStatusPanelModel.setSimManagerStatus(false);

                        coorLogger.logp(Level.SEVERE, "Coordinator", "setScriptStatus:run",
                                "Connection to Simulation Manager has been dropped.", re);
                    }
                }
            }
        };

        Thread updateThread = new Thread(updateRun);
        updateThread.start();
    }

    /**
     * Sets the current paramics status. A thread is started to notify the
     * CADSimulatorViewer and SimulationManager with the new status.
     */
    public void setParamicsStatus(final PARAMICS_STATUS status)
    {

        Runnable updateRun = new Runnable()
        {
            public void run()
            {
                simStatusPanelModel.setParamicsStatus(status);
                
                // bug fix show correct netowrk ID
                if (status == PARAMICS_STATUS.LOADED)
                {
                    try
                    {
                        simStatusPanelModel
                                .setParamicsNetworkLoaded(
                                "" + getParamicsNetworkLoaded());
                    }
                    catch (RemoteException re)
                    {
                        coorLogger.logp(Level.SEVERE, "Coordinator", 
                            "setParamicsStatus:run", "Unable to communicate with the " +
                            "CAD Simulator.", re);
                    }
                }

                if (managerInt != null)
                {
                    try
                    {
                        managerInt.setParamicsStatus(status);
                    }
                    catch (RemoteException re)
                    {
                        //Simulation Manager has disappeared
                        managerInt = null;
                        simStatusPanelModel.setSimManagerStatus(false);

                        coorLogger.logp(
                            Level.SEVERE, "Coordinator", "setParamicsStatus:run",
                            "Connection to Simulation Manager has been dropped.", re);
                    }
                }
            }
        };

        Thread updateThread = new Thread(updateRun);
        updateThread.start();

    }

    public void connectToParamics() throws RemoteException
    {
        CADSimulator.theParamicsSimMgr.connectToParamics();
    }

    public void disconnectFromParamics() throws RemoteException
    {
        CADSimulator.theParamicsSimMgr.disconnectFromParamics();
    }

    public void loadParamicsNetwork(int networkID) throws RemoteException, SimulationException
    {
        CADSimulator.theParamicsSimMgr.loadParamicsNetwork(networkID);
    }

    public PARAMICS_STATUS getParamicsStatus() throws RemoteException
    {
        return CADSimulator.theParamicsSimMgr.getParamicsStatus();
    }

    public int getParamicsNetworkLoaded() throws RemoteException
    {
        return CADSimulator.theParamicsSimMgr.getParamicsNetworkLoaded();
    }

    public long getCurrentSimulationTime() throws RemoteException
    {
        return CADSimulator.theSimulationCntrlMgr.getCurrentSimTime();
    }

    public void triggerIncident(Integer incidentNumber) throws RemoteException, ScriptException
    {

        if (!CADSimulator.theSimulationCntrlMgr.simulationStarted())
        {
            throw new ScriptException(ScriptException.SIM_NOT_STARTED);
        }
        else if (!CADSimulator.theIncidentMgr.areIncidentsLoaded())
        {
            throw new ScriptException(ScriptException.NO_SCRIPT_LOADED);
        }

        CADSimulator.theIncidentMgr.triggerIncident(incidentNumber,
                CADSimulator.theSimulationCntrlMgr.getCurrentSimTime());
    }

    public void deleteIncident(Integer incidentNumber) throws RemoteException, ScriptException
    {
        CADSimulator.theIncidentMgr.deleteIncident(incidentNumber);

        if (CADSimulator.theIncidentMgr.getIncidentList().size() == 0)
        {
            setScriptStatus(SCRIPT_STATUS.NO_SCRIPT);
        }

        if (managerInt != null)
        {
            try
            {
                managerInt.incidentRemoved(incidentNumber);
            }
            catch (RemoteException re)
            {
                //Simulation Manager has disappeared
                managerInt = null;
                simStatusPanelModel.setSimManagerStatus(false);

                coorLogger.logp(Level.SEVERE, "Coordinator", "deleteIncident",
                        "Connection to Simulation Manager has been dropped.", re);
            }
        }
    }

    public void rescheduleIncident(Integer incidentNumber, long newTime)
            throws RemoteException, ScriptException
    {

        if (newTime < CADSimulator.theSimulationCntrlMgr.getCurrentSimTime())
        {
            throw new ScriptException(ScriptException.TIME_PASSED);
        }

        CADSimulator.theIncidentMgr.rescheduleIncident(incidentNumber, newTime);
    }

    public void addIncident(Incident newIncident) throws RemoteException
    {

        CADSimulator.theIncidentMgr.addIncident(newIncident);

        if (managerInt != null)
        {
            try
            {
                managerInt.incidentAdded(newIncident);
            }
            catch (RemoteException re)
            {
                //Simulation Manager has disappeared
                managerInt = null;
                simStatusPanelModel.setSimManagerStatus(false);

                coorLogger.logp(Level.SEVERE, "Coordinator", "addIncident",
                        "Connection to Simulation Manager has been dropped.", re);
            }
        }
    }

    public void loadScriptFile(File scriptFile) throws RemoteException, ScriptException
    {


        try
        {
            CADSimulator.theIncidentMgr.clearIncidents();
            cadData.clearData();

            ScriptHandler sh = new ScriptHandler();

            SAXParserFactory.newInstance().newSAXParser().parse(scriptFile, sh);

            cadData.setIncidentsFromXML(sh.getIncidents());
            cadData.setUnitsFromXML(sh.getUnits());
            refreshClients();
            CADSimulator.theIncidentMgr.addIncidents(sh.getIncidents());

            resetSimulation();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public Vector<Incident> getIncidentList() throws RemoteException
    {
        return CADSimulator.theIncidentMgr.getIncidentList();
    }

    public TreeMap<Integer, Vector<IncidentEvent>> getTriggeredEvents() throws RemoteException
    {
        return CADSimulator.theIncidentMgr.getTriggeredEvents();
    }

    public SCRIPT_STATUS getScriptStatus() throws RemoteException
    {
        if (CADSimulator.theIncidentMgr.areIncidentsLoaded())
        {
            if (CADSimulator.theSimulationCntrlMgr.simulationStarted())
            {
                return SCRIPT_STATUS.SCRIPT_RUNNING;
            }
            else
            {
                for (Incident inc : CADSimulator.theIncidentMgr.getIncidentList())
                {
                    if (inc.hasOccured() == true)
                    {
                        return SCRIPT_STATUS.SCRIPT_PAUSED_STARTED;
                    }
                }
                return SCRIPT_STATUS.SCRIPT_STOPPED_NOT_STARTED;
            }
        }
        return SCRIPT_STATUS.NO_SCRIPT;
    }

    /**
     * Route a message to a CAD terminal. If the new message is an incident update, the
     * current message text will be the requested incident's log number. Set the
     * message's text to the XML representation of the requested IncidentInquiry object.
     * Notify observers with the new message.
     *
     * @param newMessage Routed message received from CAD Client.
     */
    public void routeMessage(CADRoutedMessage newMessage)
    {

        //if this is an incidentUpdate
        /*
         * TODO  Is this a priority??
         if(newMessage.incidentUpdate) {
         IncidentInquiryModel tempII = new IncidentInquiryModel(
         newMessage.fromPosition,
         CADScreenNum.ONE, 
         Integer.parseInt(newMessage.message));
            
         for(IncidentInquiryModel_obj iimo : getIncidentInquiryModelObjects(
         Integer.parseInt(newMessage.message))) {
         tempII.addModelObject(iimo);
         }
            
         XMLWriter tempWriter = new XMLWriter();
         tempII.toXML(tempWriter);
         newMessage.message = tempWriter.getString();
         }
         */

        notifyObservers(new ObserverMessage(ObserverMessage.messageType.ROUTED_MESSAGE,
                newMessage));

    }

    public TreeSet<String> getCMSIDs() throws RemoteException
    {
        return new TreeSet<String>(CMSDiversionDB.getInstance().getAllDiversions().keySet());
    }

    public CMSInfo getCMSDiversionInfo(String theCMSID) throws RemoteException
    {
        return CMSDiversionDB.getInstance().getDiversion(theCMSID);
    }

    public void applyDiversions(CMSInfo theDiversion) throws RemoteException
    {
        CMSDiversionDB.getInstance().updateDiversions(theDiversion);
        CADSimulator.theParamicsSimMgr.updateDiversion(theDiversion);
    }

    /**
     * Method updates the simulation with the new Incident information. The parameter
     * IncidentInquiryModel_obj Object is used to create a new IncidentEvent Object which
     * is finalized and sent to the IncidentManager for simulation updating.
     *
     * @param update IncidentInquiryModel_obj containing CAD line update
     * @see IncidentManager
     */
    public void commandLineUpdate(IncidentInquiryModel_obj modelInfo)
    {

        long currentSimTime = CADSimulator.theSimulationCntrlMgr.getCurrentSimTime();

        IncidentEvent triggeredEvent = new IncidentEvent(currentSimTime);
        triggeredEvent.eventInfo = modelInfo;

        triggeredEvent.finalizeEvent(currentSimTime, CADSimulator.getCADTime());

        CADSimulator.theIncidentMgr.updateIncident(modelInfo.getLogNumber(), triggeredEvent);

        updateIncidentInGUI(modelInfo.getLogNumber(), triggeredEvent);

    }

    /**
     * If the Simulation has started, spawn a thread to update the CADSimulatorViewer,
     * SimulationManager, and IncidentManager with the new simulation time. If the
     * current time is a 30 second interval, notify the ParamicsControlManager to send an
     * IncidentUpdate.
     *
     * @see SimulationControlManager
     * @see ParamicsControlManager
     * @see IncidentManager
     */
    public void tick()
    {
        if (CADSimulator.theSimulationCntrlMgr.simulationStarted())
        {

            final long currentSimTime = 
                    CADSimulator.theSimulationCntrlMgr.getCurrentSimTime();

            Runnable timeRun = new Runnable()
            {
                public void run()
                {
                    simStatusPanelModel.setTime(currentSimTime);

                    //send an update every 30 seconds
                    if (currentSimTime % 30 == 0)
                    {
                        CADSimulator.theParamicsSimMgr
                                .sendIncidentUpdate(currentSimTime);
                    }

                    if (managerInt != null)
                    {
                        try
                        {
                            managerInt.tick(currentSimTime);
                        }
                        catch (RemoteException re)
                        {
                            //Simulation Manager has disappeared
                            managerInt = null;
                            simStatusPanelModel.setSimManagerStatus(false);

                            coorLogger.logp(
                                Level.SEVERE, "Coordinator", "tick:run",
                                "Connection to Simulation Manager has been dropped.",
                                re);
                        }
                    }

                    CADSimulator.theIncidentMgr.tick(currentSimTime);
                }
            };

            Thread timeThread = new Thread(timeRun);
            timeThread.start();
        }
    }

    /**
     * Method notifies observers with an IncidentSummaryModel_obj to signify that a new
     * Incident has started. Then spawn a thread to notify the SimulationManager with the
     * Incident's log number has started.
     *
     * @param completedEvent Completed IncidentEvent.
     */
    public void incidentStarted(final IncidentEvent completedEvent)
    {
        notifyObservers(new ObserverMessage(ObserverMessage.messageType.INCIDENT_SUMMARY,
                new IncidentSummaryModel_obj(completedEvent.eventInfo.getHeader())));

        incidentStartedInGUI(completedEvent.eventInfo.getLogNumber());

        Runnable startRun = new Runnable()
        {
            public void run()
            {
                if (managerInt != null)
                {
                    try
                    {
                        managerInt.incidentStarted(
                                completedEvent.eventInfo.getLogNumber());
                    }
                    catch (RemoteException re)
                    {
                        //Simulation Manager has disappeared
                        managerInt = null;
                        simStatusPanelModel.setSimManagerStatus(false);

                        coorLogger.logp(
                            Level.SEVERE, "Coordinator", "updateIncidents",
                            "Connection to Simulation Manager has been dropped.", re);
                    }
                }
            }
        };

        Thread startThread = new Thread(startRun);
        startThread.start();
    }

    /**
     * Method notifies observers with the IncidentEvent Object to signify that a new
     * IncidentEvent has completed. All XMLIncident Objects in the completed
     * IncidentEvent is sent to the ParamicsControlManager for transmission to Paramics.
     * All CCTVInfo Objects are sent to the MediaManager for media control. A thread is
     * then spawned to notify the SimulationManager with the completed IncidentEvent.
     *
     * @param completedEvent Completed IncidentEvent.
     * @see ParamicsControlManager
     * @see MediaManager
     */
    public void incidentUpdated(final IncidentEvent completedEvent)
    {
        notifyObservers(new ObserverMessage(ObserverMessage.messageType.INCIDENT_INQUIRY,
                completedEvent.eventInfo));

        for (XMLIncident xmlInc : completedEvent.XMLIncidents)
        {
            CADSimulator.theParamicsSimMgr.updateIncident(xmlInc);
        }


        /*for(CCTVInfo info : completedEvent.cctvInfos) {
         CADSimulator.theMediaMgr.triggerIncident(info, 
         completedEvent.eventInfo.getLogNumber());
         }*/

        Runnable displayRun = new Runnable()
        {
            public void run()
            {
                if (managerInt != null)
                {
                    try
                    {
                        managerInt.eventOccured(
                                completedEvent.eventInfo.getLogNumber(),
                                completedEvent);
                    }
                    catch (RemoteException re)
                    {
                        //Simulation Manager has disappeared
                        managerInt = null;
                        simStatusPanelModel.setSimManagerStatus(false);

                        coorLogger.logp(
                            Level.SEVERE, "Coordinator", "updateIncidents:run",
                            "Connection to Simulation Manager has been dropped.", re);
                    }
                }
            }
        };

        Thread displayThread = new Thread(displayRun);
        displayThread.start();
    }

    /**
     * @see IncidentManager
     */
    public boolean incidentExists(Integer logNumber)
    {
        return CADSimulator.theIncidentMgr.incidentExists(logNumber);
    }

    /**
     * @see IncidentManager
     */
    public Vector<IncidentBoardModel_obj> getIncidentBoardModelObjects()
    {
        return CADSimulator.theIncidentMgr.getIncidentBoardModelObjects();
    }

    /**
     * @see IncidentManager
     */
    public Vector<IncidentInquiryModel_obj> getIncidentInquiryModelObjects(Integer logNumber)
    {
        return CADSimulator.theIncidentMgr.getIncidentInquiryModelObjects(logNumber);
    }

    /**
     * @see IncidentManager
     */
    public Vector<IncidentSummaryModel_obj> getIncidentSummaryModelObjects()
    {
        return CADSimulator.theIncidentMgr.getIncidentSummaryModelObjects();
    }

    /**
     * Adds an observer to the list of observers.
     *
     * @param o New observer object.
     */
    public void addObserver(Observer o)
    {
        observerList.add(o);
    }

    /**
     * Removes an observer from the list of observers. If the observer is not found, this
     * method returns false, else true is returned for a successful removal.
     *
     * @param o Observer to be removed.
     * @returns True if remove was successful, false if not.
     */
    public boolean removeObserver(Observer o)
    {
        return observerList.remove(o);
    }

    /**
     * Notify all registered observers with the parameter ObserverMessage object.
     *
     * @param newMsg The ObserverMessage to be sent.
     */
    private void notifyObservers(ObserverMessage newMsg)
    {
        for (Observer o : observerList)
        {
            o.update(null, newMsg);
        }
    }

    public void refreshClients() throws RemoteException
    {
        for (int i = 0; i < clientList.size(); i++)
        {
            clientList.get(i).refresh();
        }
    }

    /**
     * Checks the CADData for an existing incident with id
     *
     * @param id the incident id
     * @return true if CADData contains such an incident, otherwise false.
     * @throws RemoteException
     */
    public boolean checkForValidIncidentID(int id) throws RemoteException
    {
        return cadData.checkForValidId(id);
    }

    /**
     * Uses an Incident's masterInc to lookup its ID.
     *
     * @param masterInc the Incident to look up
     * @return the same Incident's ID, -1 if invalid masterInc
     * @throws RemoteException
     */
    public int getIncidentId(String masterInc) throws RemoteException
    {
        return cadData.getIncidentId(masterInc);
    }

    /**
     * Returns a table model out of CADData based on tag.
     *
     * @param tag a CADDataEnums tag
     * @throws RemoteException
     */
    public DefaultTableModel getCadDataTable(TABLE tag) throws RemoteException
    {
        if (tag.equals(TABLE.ASSIGNED_INCIDENTS))
        {
            return cadData.tableForAssignedIncidents();
        }
        else if (tag.equals(TABLE.UNIT_STATUS))
        {
            return cadData.tableForUnitStatus();
        }
        else if (tag.equals(TABLE.PENDING_INCIDENTS))
        {
            return cadData.tableForPendingIncidents();
        }
        else if (tag.equals(TABLE.INCIDENT_EDITOR))
        {
            return cadData.tableForIncidentEditor();
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.getCadDataTable");
        }
    }

    /**
     * Returns the specified incident's table based on the tag.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @return
     * @throws RemoteException
     */
    public DefaultTableModel getCadDataIncidentTable(INC_TABLE tag, int incidentId) throws RemoteException
    {
        if (tag.equals(INC_TABLE.COMMENTS_NOTES))
        {
            return cadData.getIncident(incidentId).getCommentsNotesTable();
        }
        return null;
    }

    /**
     * Adds a row of data to a specified incident's table based on the tag.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @fields the row of fields to add to the specified table
     * @throws RemoteException
     */
    public void addCadDataIncidentTable(INC_TABLE tag, int incidentId, String[] fields) throws RemoteException
    {
        if (tag.equals(INC_TABLE.COMMENTS_NOTES))
        {
            cadData.getIncident(incidentId).addToCommentsNotesTable(fields);
        }
    }

    /**
     * Adds a row to the specified data table based on the incoming fields.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void addCadDataTableRow(TABLE tag, String field1, String field2, String field3, String field4) throws RemoteException
    {
    }

    /**
     * Returns an object out of Incident based on tag.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public Object getCadDataIncVal(INC_VAL tag, int incidentId) throws RemoteException
    {
        if (tag.equals(INC_VAL.LOG_NUM))
        {
            return cadData.getIncident(incidentId).getLogNum();
        }
        else if (tag.equals(INC_VAL.MASTER_INC))
        {
            return cadData.getIncident(incidentId).getMasterInc();
        }
        else if (tag.equals(INC_VAL.OAU))
        {
            return cadData.getIncident(incidentId).getOau();
        }
        else if (tag.equals(INC_VAL.P))
        {
            return cadData.getIncident(incidentId).getP();
        }
        else if (tag.equals(INC_VAL.DESCRIPTION))
        {
            return cadData.getIncident(incidentId).getDescription();
        }
        else if (tag.equals(INC_VAL.RP))
        {
            return cadData.getIncident(incidentId).getRp();
        }
        else if (tag.equals(INC_VAL.RP_TYPE))
        {
            return cadData.getIncident(incidentId).getRpType();
        }
        else if (tag.equals(INC_VAL.ALI))
        {
            return cadData.getIncident(incidentId).getAli();
        }
        else if (tag.equals(INC_VAL.MEDIA))
        {
            return cadData.getIncident(incidentId).getMedia();
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.getCadDataIncVal");
        }
    }

    /**
     * Set incidentId's field based on tag to value.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void setCadDataIncVal(INC_VAL tag, int incidentId, String value) throws RemoteException
    {
        if (tag.equals(INC_VAL.OAU))
        {
            cadData.getIncident(incidentId).setOau(value);
        }
        else if (tag.equals(INC_VAL.P))
        {
            cadData.getIncident(incidentId).setP(value);
        }
        else if (tag.equals(INC_VAL.DESCRIPTION))
        {
            cadData.getIncident(incidentId).setDescription(value);
        }
        else if (tag.equals(INC_VAL.RP))
        {
            cadData.getIncident(incidentId).setRp(value);
        }
        else if (tag.equals(INC_VAL.RP_TYPE))
        {
            cadData.getIncident(incidentId).setRpType(value);
        }
        else if (tag.equals(INC_VAL.ALI))
        {
            cadData.getIncident(incidentId).setAli(value);
        }
        else if (tag.equals(INC_VAL.MEDIA))
        {
            cadData.getIncident(incidentId).setMedia(value);
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.setCadDataIncVal");
        }
    }

    /**
     * Returns a string out of IncidentLocation based on tag.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncLoc(INC_LOC tag, int incidentId) throws RemoteException
    {
        if (tag.equals(INC_LOC.ADDRESS))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getAddress();
        }
        else if (tag.equals(INC_LOC.LOC))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getLoc();
        }
        else if (tag.equals(INC_LOC.CITY))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getCity();
        }
        else if (tag.equals(INC_LOC.COUNTY))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getCounty();
        }
        else if (tag.equals(INC_LOC.STATE))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getState();
        }
        else if (tag.equals(INC_LOC.ZIP))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getZip();
        }
        else if (tag.equals(INC_LOC.BEAT))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getBeat();
        }
        else if (tag.equals(INC_LOC.AREA))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getArea();
        }
        else if (tag.equals(INC_LOC.SECTOR))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getSector();
        }
        else if (tag.equals(INC_LOC.SECTOR_CODE))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getSectorCode();
        }
        else if (tag.equals(INC_LOC.DIVISION))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getDivision();
        }
        else if (tag.equals(INC_LOC.APT))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getApt();
        }
        else if (tag.equals(INC_LOC.BUILDING))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getBuilding();
        }
        else if (tag.equals(INC_LOC.CROSS_ST))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getCrossSt();
        }
        else if (tag.equals(INC_LOC.LAW))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getLaw();
        }
        else if (tag.equals(INC_LOC.FIRE))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getFire();
        }
        else if (tag.equals(INC_LOC.EMS))
        {
            return cadData.getIncident(incidentId).getIncidentLocation().getEms();
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.getCadDataIncLoc");
        }
    }

    /**
     * Set incidentId's field based on tag to value.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void setCadDataIncLoc(INC_LOC tag, int incidentId, String value) throws RemoteException
    {
        if (tag.equals(INC_LOC.ADDRESS))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setAddress(value);
        }
        else if (tag.equals(INC_LOC.LOC))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setLoc(value);
        }
        else if (tag.equals(INC_LOC.CITY))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setCity(value);
        }
        else if (tag.equals(INC_LOC.COUNTY))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setCounty(value);
        }
        else if (tag.equals(INC_LOC.STATE))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setState(value);
        }
        else if (tag.equals(INC_LOC.ZIP))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setZip(value);
        }
        else if (tag.equals(INC_LOC.BEAT))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setBeat(value);
        }
        else if (tag.equals(INC_LOC.AREA))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setArea(value);
        }
        else if (tag.equals(INC_LOC.SECTOR))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setSector(value);
        }
        else if (tag.equals(INC_LOC.SECTOR_CODE))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setSectorCode(value);
        }
        else if (tag.equals(INC_LOC.DIVISION))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setDivision(value);
        }
        else if (tag.equals(INC_LOC.APT))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setApt(value);
        }
        else if (tag.equals(INC_LOC.BUILDING))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setBuilding(value);
        }
        else if (tag.equals(INC_LOC.CROSS_ST))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setCrossSt(value);
        }
        else if (tag.equals(INC_LOC.LAW))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setLaw(value);
        }
        else if (tag.equals(INC_LOC.FIRE))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setFire(value);
        }
        else if (tag.equals(INC_LOC.EMS))
        {
            cadData.getIncident(incidentId).getIncidentLocation().setEms(value);
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.setCadDataIncLoc");
        }
    }

    /**
     * Returns a string out of IncidentCaller based on tag.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncCaller(INC_CALLER tag, int incidentId) throws RemoteException
    {
        if (tag.equals(INC_CALLER.TYPE))
        {
            return cadData.getIncident(incidentId).getIncidentCaller().getCallerType();
        }
        else if (tag.equals(INC_CALLER.NAME))
        {
            return cadData.getIncident(incidentId).getIncidentCaller().getCallerName();
        }
        else if (tag.equals(INC_CALLER.PHONE))
        {
            return cadData.getIncident(incidentId).getIncidentCaller().getPhone();
        }
        else if (tag.equals(INC_CALLER.EXT))
        {
            return cadData.getIncident(incidentId).getIncidentCaller().getExt();
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.getCadDataIncCaller");
        }
    }

    /**
     * Set incidentId's field based on tag to value.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void setCadDataIncCaller(INC_CALLER tag, int incidentId, String value) throws RemoteException
    {
        if (tag.equals(INC_CALLER.TYPE))
        {
            cadData.getIncident(incidentId).getIncidentCaller().setCallerType(value);
        }
        else if (tag.equals(INC_CALLER.NAME))
        {
            cadData.getIncident(incidentId).getIncidentCaller().setCallerName(value);
        }
        else if (tag.equals(INC_CALLER.PHONE))
        {
            cadData.getIncident(incidentId).getIncidentCaller().setPhone(value);
        }
        else if (tag.equals(INC_CALLER.EXT))
        {
            cadData.getIncident(incidentId).getIncidentCaller().setExt(value);
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.setCadDataIncCaller");
        }
    }

    /**
     * Returns a string out of IncidentProblem based on tag.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncProblem(INC_PROBLEM tag, int incidentId) throws RemoteException
    {
        if (tag.equals(INC_PROBLEM.PROBLEM))
        {
            return cadData.getIncident(incidentId).getProblem().getProblem();
        }
        else if (tag.equals(INC_PROBLEM.CODE))
        {
            return cadData.getIncident(incidentId).getProblem().getProblemCode();
        }
        else if (tag.equals(INC_PROBLEM.PRIORITY))
        {
            return cadData.getIncident(incidentId).getProblem().getPriority();
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.getCadDataIncProblem");
        }
    }

    /**
     * Set incidentId's field based on tag to value.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void setCadDataIncProblem(INC_PROBLEM tag, int incidentId, String value) throws RemoteException
    {
        if (tag.equals(INC_PROBLEM.PROBLEM))
        {
            cadData.getIncident(incidentId).getProblem().setProblem(value);
        }
        else if (tag.equals(INC_PROBLEM.CODE))
        {
            cadData.getIncident(incidentId).getProblem().setProblemCode(value);
        }
        else if (tag.equals(INC_PROBLEM.PRIORITY))
        {
            cadData.getIncident(incidentId).getProblem().setPriority(value);
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.setCadDataIncProblem");
        }
    }

    /**
     * Returns a string out of IncidentGeneralInfo based on tag.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncGenInfo(INC_GEN_INFO tag, int incidentId) throws RemoteException
    {
        if (tag.equals(INC_GEN_INFO.JURISDICTION))
        {
            return cadData.getIncident(incidentId).getGenInfo().getJurisdiction();
        }
        else if (tag.equals(INC_GEN_INFO.ALARM))
        {
            return cadData.getIncident(incidentId).getGenInfo().getAlarm();
        }
        else if (tag.equals(INC_GEN_INFO.AGY))
        {
            return cadData.getIncident(incidentId).getGenInfo().getAgy();
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.getCadDataIncGenInfo");
        }
    }

    /**
     * Set incidentId's field based on tag to value.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void setCadDataIncGenInfo(INC_GEN_INFO tag, int incidentId, String value) throws RemoteException
    {
        if (tag.equals(INC_GEN_INFO.JURISDICTION))
        {
            cadData.getIncident(incidentId).getGenInfo().setJurisdiction(value);
        }
        else if (tag.equals(INC_GEN_INFO.ALARM))
        {
            cadData.getIncident(incidentId).getGenInfo().setAlarm(value);
        }
        else if (tag.equals(INC_GEN_INFO.AGY))
        {
            cadData.getIncident(incidentId).getGenInfo().setAgy(value);
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.setCadDataIncGenInfo");
        }
    }

    /**
     * Returns a string out of IncidentResponse based on tag.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncResp(INC_RESP tag, int incidentId) throws RemoteException
    {
        if (tag.equals(INC_RESP.PLAN))
        {
            return cadData.getIncident(incidentId).getResponse().getPlan();
        }
        else if (tag.equals(INC_RESP.AREA))
        {
            return cadData.getIncident(incidentId).getResponse().getArea();
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.getCadDataIncResp");
        }
    }

    /**
     * Set incidentId's field based on tag to value.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void setCadDataIncResp(INC_RESP tag, int incidentId, String value) throws RemoteException
    {
        if (tag.equals(INC_RESP.PLAN))
        {
            cadData.getIncident(incidentId).getResponse().setPlan(value);
        }
        else if (tag.equals(INC_RESP.AREA))
        {
            cadData.getIncident(incidentId).getResponse().setArea(value);
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.setCadDataIncResp");
        }
    }

    /**
     * Returns a string out of IncidentAdditionalInfo based on tag.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncAddInfo(INC_ADD_INFO tag, int incidentId) throws RemoteException
    {
        if (tag.equals(INC_ADD_INFO.TYPE))
        {
            return cadData.getIncident(incidentId).getAdditionalInfo().getType();
        }
        else if (tag.equals(INC_ADD_INFO.TYPE_CODE))
        {
            return cadData.getIncident(incidentId).getAdditionalInfo().getTypeCode();
        }
        else if (tag.equals(INC_ADD_INFO.MACHINE))
        {
            return cadData.getIncident(incidentId).getAdditionalInfo().getMachine();
        }
        else if (tag.equals(INC_ADD_INFO.CALL_STATUS))
        {
            return cadData.getIncident(incidentId).getAdditionalInfo().getCallStatus();
        }
        else if (tag.equals(INC_ADD_INFO.CALL_TAKER_EXT))
        {
            return cadData.getIncident(incidentId).getAdditionalInfo().getCallTakerExt();
        }
        else if (tag.equals(INC_ADD_INFO.ALARM_LEVEL))
        {
            return cadData.getIncident(incidentId).getAdditionalInfo().getAlarmLevel();
        }
        else if (tag.equals(INC_ADD_INFO.ROTATION_PROVIDER_AREA))
        {
            return cadData.getIncident(incidentId).getAdditionalInfo().getRotationProviderArea();
        }
        else if (tag.equals(INC_ADD_INFO.COMMENT))
        {
            return cadData.getIncident(incidentId).getAdditionalInfo().getComment();
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.getCadDataIncAddInfo");
        }
    }

    /**
     * Set incidentId's field based on tag to value.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void setCadDataIncAddInfo(INC_ADD_INFO tag, int incidentId, String value) throws RemoteException
    {
        if (tag.equals(INC_ADD_INFO.TYPE))
        {
            cadData.getIncident(incidentId).getAdditionalInfo().setType(value);
        }
        else if (tag.equals(INC_ADD_INFO.TYPE_CODE))
        {
            cadData.getIncident(incidentId).getAdditionalInfo().setTypeCode(value);
        }
        else if (tag.equals(INC_ADD_INFO.MACHINE))
        {
            cadData.getIncident(incidentId).getAdditionalInfo().setMachine(value);
        }
        else if (tag.equals(INC_ADD_INFO.CALL_STATUS))
        {
            cadData.getIncident(incidentId).getAdditionalInfo().setCallStatus(value);
        }
        else if (tag.equals(INC_ADD_INFO.CALL_TAKER_EXT))
        {
            cadData.getIncident(incidentId).getAdditionalInfo().setCallTakerExt(value);
        }
        else if (tag.equals(INC_ADD_INFO.ALARM_LEVEL))
        {
            cadData.getIncident(incidentId).getAdditionalInfo().setAlarmLevel(value);
        }
        else if (tag.equals(INC_ADD_INFO.ROTATION_PROVIDER_AREA))
        {
            cadData.getIncident(incidentId).getAdditionalInfo().setRotationProviderArea(value);
        }
        else if (tag.equals(INC_ADD_INFO.COMMENT))
        {
            cadData.getIncident(incidentId).getAdditionalInfo().setComment(value);
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.setCadDataIncAddInfo");
        }
    }

    /**
     * Returns a string out of IncidentActivities based on tag.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncActivities(INC_ACTIVITIES tag, int incidentId) throws RemoteException
    {
        if (tag.equals(INC_ACTIVITIES.VEHICLE))
        {
            return cadData.getIncident(incidentId).getActivities().getVehicle();
        }
        else if (tag.equals(INC_ACTIVITIES.ACTIVITY))
        {
            return cadData.getIncident(incidentId).getActivities().getActivity();
        }
        else if (tag.equals(INC_ACTIVITIES.LOCATION))
        {
            return cadData.getIncident(incidentId).getActivities().getLocation();
        }
        else if (tag.equals(INC_ACTIVITIES.COMMENT))
        {
            return cadData.getIncident(incidentId).getActivities().getComment();
        }
        else if (tag.equals(INC_ACTIVITIES.DISP))
        {
            return cadData.getIncident(incidentId).getActivities().getDisp();
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.getCadDataIncActivities");
        }
    }

    /**
     * Set incidentId's field based on tag to value.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public void setCadDataIncActivities(INC_ACTIVITIES tag, int incidentId, String value) throws RemoteException
    {
        if (tag.equals(INC_ACTIVITIES.VEHICLE))
        {
            cadData.getIncident(incidentId).getActivities().setVehicle(value);
        }
        else if (tag.equals(INC_ACTIVITIES.ACTIVITY))
        {
            cadData.getIncident(incidentId).getActivities().setActivity(value);
        }
        else if (tag.equals(INC_ACTIVITIES.LOCATION))
        {
            cadData.getIncident(incidentId).getActivities().setLocation(value);
        }
        else if (tag.equals(INC_ACTIVITIES.COMMENT))
        {
            cadData.getIncident(incidentId).getActivities().setComment(value);
        }
        else if (tag.equals(INC_ACTIVITIES.DISP))
        {
            cadData.getIncident(incidentId).getActivities().setDisp(value);
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.setCadDataIncActivities");
        }
    }

    /**
     * Returns a string out of IncidentCallback based on tag.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncCallBack(INC_CALLBACK tag, int incidentId) throws RemoteException
    {
        if (tag.equals(INC_CALLBACK.INITIAL))
        {
            return cadData.getIncident(incidentId).getCallBacks().getInitial();
        }
        else if (tag.equals(INC_CALLBACK.COMMENT))
        {
            return cadData.getIncident(incidentId).getCallBacks().getComment();
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.getCadDataIncCallBack");
        }
    }

    /**
     * Returns a string out of IncidentEditLog based on tag.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncEditLog(INC_EDIT_LOG tag, int incidentId) throws RemoteException
    {
        if (tag.equals(INC_EDIT_LOG.EDIT))
        {
            return cadData.getIncident(incidentId).getEditLog().getEdit();
        }
        else if (tag.equals(INC_EDIT_LOG.REASON))
        {
            return cadData.getIncident(incidentId).getEditLog().getReason();
        }
        else if (tag.equals(INC_EDIT_LOG.CHANGE_BY))
        {
            return cadData.getIncident(incidentId).getEditLog().getChangeBy();
        }
        else if (tag.equals(INC_EDIT_LOG.TERMINAL))
        {
            return cadData.getIncident(incidentId).getEditLog().getTerminal();
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.getCadDataIncEditLog");
        }
    }

    /**
     * Returns a string out of IncidentInfo based on tag.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncInfo(INC_INFO tag, int incidentId) throws RemoteException
    {
        if (tag.equals(INC_INFO.CALL_INITIATED))
        {
            return cadData.getIncident(incidentId).getInfo().getCallInit();
        }
        else if (tag.equals(INC_INFO.CALL_TAKEN))
        {
            return cadData.getIncident(incidentId).getInfo().getCallTaken();
        }
        else if (tag.equals(INC_INFO.TIME_IN_Q))
        {
            return cadData.getIncident(incidentId).getInfo().getTimeInQ();
        }
        else if (tag.equals(INC_INFO.LAST_UPDATED))
        {
            return cadData.getIncident(incidentId).getInfo().getLastUpdated();
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.getCadDataIncInfo");
        }
    }

    /**
     * Returns a string out of IncidentTimes based on tag.
     *
     * @param tag a CADDataEnums tag
     * @param incidentId the incident's ID to look up
     * @throws RemoteException
     */
    public String getCadDataIncTimes(INC_TIMES tag, int incidentId) throws RemoteException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns a CardfileList matching the tag.
     */
    public CardfileList getCardfileList(CARDFILE tag) throws RemoteException
    {
        if (tag.equals(CARDFILE.COASTAL_DIVISION_UNITS))
        {
            return cardfileData.getCoastalDivisionUnitList();
        }
        else if (tag.equals(CARDFILE.POLICE_SHERIFF_CORONER))
        {
            return cardfileData.getPoliceSheriffCoronerList();
        }
        else if (tag.equals(CARDFILE.COURTS))
        {
            return cardfileData.getCourtsList();
        }
        else if (tag.equals(CARDFILE.PUBLIC_TRANSPORTATION))
        {
            return cardfileData.getPublicTransportationList();
        }
        else if (tag.equals(CARDFILE.GG_OTHER))
        {
            return cardfileData.getGgOtherList();
        }
        else if (tag.equals(CARDFILE.MY_MISC))
        {
            return cardfileData.getMyMiscList();
        }
        else if (tag.equals(CARDFILE.SL_MISC))
        {
            return cardfileData.getSlMiscList();
        }
        else if (tag.equals(CARDFILE.VT_MISC))
        {
            return cardfileData.getVlMiscList();
        }
        else if (tag.equals(CARDFILE.CHP_OFFICES))
        {
            return cardfileData.getChpOfficesList();
        }
        else if (tag.equals(CARDFILE.STATE_AGENCIES_FACILITIES))
        {
            return cardfileData.getStateAgenciesFacilitiesList();
        }
        else if (tag.equals(CARDFILE.GOVERNMENT_OFFICIALS))
        {
            return cardfileData.getGovernmentOfficialsList();
        }
        else if (tag.equals(CARDFILE.FEDERAL_AGENCIES))
        {
            return cardfileData.getFederalAgenciesList();
        }
        else if (tag.equals(CARDFILE.RANCHES_LIVESTOCK))
        {
            return cardfileData.getRanchesLivestockList();
        }
        else if (tag.equals(CARDFILE.FIRE_EMS))
        {
            return cardfileData.getFireEmsList();
        }
        else if (tag.equals(CARDFILE.JAILS))
        {
            return cardfileData.getJailsList();
        }
        else if (tag.equals(CARDFILE.HOSPITALS_MED_CENTERS))
        {
            return cardfileData.getHospitalsMedCentersList();
        }
        else if (tag.equals(CARDFILE.TOW_COMPANIES))
        {
            return cardfileData.getTowCompaniesList();
        }
        else if (tag.equals(CARDFILE.CALTRANS))
        {
            return cardfileData.getCalTransList();
        }
        else if (tag.equals(CARDFILE.COUNTY_ROADS))
        {
            return cardfileData.getCountyRoadsList();
        }
        else if (tag.equals(CARDFILE.UTILITIES))
        {
            return cardfileData.getUtilitiesList();
        }
        else if (tag.equals(CARDFILE.ANIMAL_CONTROL))
        {
            return cardfileData.getAnimalControlList();
        }
        else if (tag.equals(CARDFILE.AIRPORTS))
        {
            return cardfileData.getAirportsList();
        }
        else if (tag.equals(CARDFILE.CREDIT_CARDS))
        {
            return cardfileData.getCreditCardsList();
        }
        else if (tag.equals(CARDFILE.GG_CRISIS_SHELTERS))
        {
            return cardfileData.getGgCrisisSheltersList();
        }
        else if (tag.equals(CARDFILE.RANGES))
        {
            return cardfileData.getRangesList();
        }
        else if (tag.equals(CARDFILE.HOTLINES))
        {
            return cardfileData.getHotlinesList();
        }
        else if (tag.equals(CARDFILE.HWY_PATROLS_OOS))
        {
            return cardfileData.getHwyPatrolsOosList();
        }
        else if (tag.equals(CARDFILE.PARKS_RECREATION))
        {
            return cardfileData.getParksRecreationList();
        }
        else if (tag.equals(CARDFILE.SHELTERS))
        {
            return cardfileData.getSheltersList();
        }
        else if (tag.equals(CARDFILE.SL_COUNTY_SERVICES))
        {
            return cardfileData.getSlCountyServicesList();
        }
        else if (tag.equals(CARDFILE.SL_RESOURCES))
        {
            return cardfileData.getSlResourcesList();
        }
        else if (tag.equals(CARDFILE.TRUCK_TIRE_REPAIR))
        {
            return cardfileData.getTruckTireRepairList();
        }
        else if (tag.equals(CARDFILE.MCC_EMPLOYEES))
        {
            return cardfileData.getMccEmployeesList();
        }
        else if (tag.equals(CARDFILE.GATE_ACCESS_CODES))
        {
            return cardfileData.getGateAccessCodesList();
        }
        else if (tag.equals(CARDFILE.VT_CALL_SIGNS))
        {
            return cardfileData.getVtCallSignsList();
        }
        else if (tag.equals(CARDFILE.SLCC_EMPLOYEES))
        {
            return cardfileData.getSlccEmployeesList();
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.getCardfileList");
        }
    }

    /**
     * Receives the fields of the ChangeLog in Cardfile.java and uses those fields to
     * make changes to its own copy of the Cardfiledata.
     */
    public void editCardfile(ChangeLog log) throws RemoteException
    {

        CardfileList selectedList;

        if (log.listTitle.equals(CARDFILE.COASTAL_DIVISION_UNITS.tag))
        {
            selectedList = cardfileData.getCoastalDivisionUnitList();
        }
        else if (log.listTitle.equals(CARDFILE.POLICE_SHERIFF_CORONER.tag))
        {
            selectedList = cardfileData.getPoliceSheriffCoronerList();
        }
        else if (log.listTitle.equals(CARDFILE.COURTS.tag))
        {
            selectedList = cardfileData.getCourtsList();
        }
        else if (log.listTitle.equals(CARDFILE.PUBLIC_TRANSPORTATION.tag))
        {
            selectedList = cardfileData.getPublicTransportationList();
        }
        else if (log.listTitle.equals(CARDFILE.GG_OTHER.tag))
        {
            selectedList = cardfileData.getGgOtherList();
        }
        else if (log.listTitle.equals(CARDFILE.MY_MISC.tag))
        {
            selectedList = cardfileData.getMyMiscList();
        }
        else if (log.listTitle.equals(CARDFILE.SL_MISC.tag))
        {
            selectedList = cardfileData.getSlMiscList();
        }
        else if (log.listTitle.equals(CARDFILE.VT_MISC.tag))
        {
            selectedList = cardfileData.getVlMiscList();
        }
        else if (log.listTitle.equals(CARDFILE.CHP_OFFICES.tag))
        {
            selectedList = cardfileData.getChpOfficesList();
        }
        else if (log.listTitle.equals(CARDFILE.STATE_AGENCIES_FACILITIES.tag))
        {
            selectedList = cardfileData.getStateAgenciesFacilitiesList();
        }
        else if (log.listTitle.equals(CARDFILE.GOVERNMENT_OFFICIALS.tag))
        {
            selectedList = cardfileData.getGovernmentOfficialsList();
        }
        else if (log.listTitle.equals(CARDFILE.FEDERAL_AGENCIES.tag))
        {
            selectedList = cardfileData.getFederalAgenciesList();
        }
        else if (log.listTitle.equals(CARDFILE.RANCHES_LIVESTOCK.tag))
        {
            selectedList = cardfileData.getRanchesLivestockList();
        }
        else if (log.listTitle.equals(CARDFILE.FIRE_EMS.tag))
        {
            selectedList = cardfileData.getFireEmsList();
        }
        else if (log.listTitle.equals(CARDFILE.JAILS.tag))
        {
            selectedList = cardfileData.getJailsList();
        }
        else if (log.listTitle.equals(CARDFILE.HOSPITALS_MED_CENTERS.tag))
        {
            selectedList = cardfileData.getHospitalsMedCentersList();
        }
        else if (log.listTitle.equals(CARDFILE.TOW_COMPANIES.tag))
        {
            selectedList = cardfileData.getTowCompaniesList();
        }
        else if (log.listTitle.equals(CARDFILE.CALTRANS.tag))
        {
            selectedList = cardfileData.getCalTransList();
        }
        else if (log.listTitle.equals(CARDFILE.COUNTY_ROADS.tag))
        {
            selectedList = cardfileData.getCountyRoadsList();
        }
        else if (log.listTitle.equals(CARDFILE.UTILITIES.tag))
        {
            selectedList = cardfileData.getUtilitiesList();
        }
        else if (log.listTitle.equals(CARDFILE.ANIMAL_CONTROL.tag))
        {
            selectedList = cardfileData.getAnimalControlList();
        }
        else if (log.listTitle.equals(CARDFILE.AIRPORTS.tag))
        {
            selectedList = cardfileData.getAirportsList();
        }
        else if (log.listTitle.equals(CARDFILE.CREDIT_CARDS.tag))
        {
            selectedList = cardfileData.getCreditCardsList();
        }
        else if (log.listTitle.equals(CARDFILE.GG_CRISIS_SHELTERS.tag))
        {
            selectedList = cardfileData.getGgCrisisSheltersList();
        }
        else if (log.listTitle.equals(CARDFILE.RANGES.tag))
        {
            selectedList = cardfileData.getRangesList();
        }
        else if (log.listTitle.equals(CARDFILE.HOTLINES.tag))
        {
            selectedList = cardfileData.getHotlinesList();
        }
        else if (log.listTitle.equals(CARDFILE.HWY_PATROLS_OOS.tag))
        {
            selectedList = cardfileData.getHwyPatrolsOosList();
        }
        else if (log.listTitle.equals(CARDFILE.PARKS_RECREATION.tag))
        {
            selectedList = cardfileData.getParksRecreationList();
        }
        else if (log.listTitle.equals(CARDFILE.SHELTERS.tag))
        {
            selectedList = cardfileData.getSheltersList();
        }
        else if (log.listTitle.equals(CARDFILE.SL_COUNTY_SERVICES.tag))
        {
            selectedList = cardfileData.getSlCountyServicesList();
        }
        else if (log.listTitle.equals(CARDFILE.SL_RESOURCES.tag))
        {
            selectedList = cardfileData.getSlResourcesList();
        }
        else if (log.listTitle.equals(CARDFILE.TRUCK_TIRE_REPAIR.tag))
        {
            selectedList = cardfileData.getTruckTireRepairList();
        }
        else if (log.listTitle.equals(CARDFILE.MCC_EMPLOYEES.tag))
        {
            selectedList = cardfileData.getMccEmployeesList();
        }
        else if (log.listTitle.equals(CARDFILE.GATE_ACCESS_CODES.tag))
        {
            selectedList = cardfileData.getGateAccessCodesList();
        }
        else if (log.listTitle.equals(CARDFILE.VT_CALL_SIGNS.tag))
        {
            selectedList = cardfileData.getVtCallSignsList();
        }
        else if (log.listTitle.equals(CARDFILE.SLCC_EMPLOYEES.tag))
        {
            selectedList = cardfileData.getSlccEmployeesList();
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.editCardfileDataObject");
        }

        CardfileDataObject cfdo = cardfileData.getCardfileDataObject(selectedList, log.id);
        if (cfdo != null && log.command.equals(EditCommand.NAME))
        {
            cfdo.setName(log.newValue);
            selectedList.resort(cardfileData.getCardfileDataIndex(selectedList, log.id));
        }
        else if (cfdo != null && log.command.equals(EditCommand.ADDRESS))
        {
            cfdo.setAddress(log.newValue);
        }
        else if (cfdo != null && log.command.equals(EditCommand.CITY))
        {
            cfdo.setCity(log.newValue);
        }
        else if (cfdo != null && log.command.equals(EditCommand.STATE))
        {
            cfdo.setState(log.newValue);
        }
        else if (cfdo != null && log.command.equals(EditCommand.ZIP))
        {
            cfdo.setZip(log.newValue);
        }
        else if (cfdo != null && log.command.equals(EditCommand.PHONE1))
        {
            cfdo.setPhone1(log.newValue);
        }
        else if (cfdo != null && log.command.equals(EditCommand.PHONE2))
        {
            cfdo.setPhone2(log.newValue);
        }
        else if (cfdo != null && log.command.equals(EditCommand.FAX))
        {
            cfdo.setFax(log.newValue);
        }
        else if (log.command.equals(EditCommand.OBJECT_DELETE))
        {
            selectedList.removeDataObject(cardfileData.getCardfileDataIndex(selectedList, log.id));
        }
        else if (log.command.equals(EditCommand.OBJECT_ADD))
        {
            selectedList.addDataObject(log.newCardfileObject);
        }
        else if (log.command.equals(EditCommand.TABLE_ADD))
        {
            if (cfdo != null)
            {
                cfdo.addComment(log.tableFields);
            }
        }
        else if (log.command.equals(EditCommand.TABLE_DELETE))
        {
            if (cfdo != null)
            {
                cfdo.removeComment(log.timeStamp);
            }
        }
        else
        {
        }


    }

    /**
     * Obtain a new unique ID for a cardfileDataObject.
     */
    public int obtainNewUniqueId() throws RemoteException
    {
        return cardfileData.obtainNewUniqueId();
    }

    /**
     * Returns a value from Unit based on tag.
     *
     * @param unitNum the unitNum to look up
     * @throws RemoteException
     */
    public UnitStatusEnums getCadDataUnitStatus(String unitNum) throws RemoteException
    {
        return cadData.getUnit(unitNum).getUnitStatus();
    }

    /**
     * Returns a string field from Unit based on tag.
     *
     * @param tag a CADScriptTags tag
     * @param unitNum the unitNum to look up
     * @return a string value
     * @throws RemoteException
     */
    public String getCadDataUnitValue(String unitNum, UNIT_TAGS tag) throws RemoteException
    {
        if (tag.equals(UNIT_TAGS.MASTER_INC_NUM))
        {
            return cadData.getUnit(unitNum).getMasterInc();
        }
        else if (tag.equals(UNIT_TAGS.STATUS))
        {
            return cadData.getUnit(unitNum).getStatus();
        }
        else if (tag.equals(UNIT_TAGS.OOS))
        {
            return cadData.getUnit(unitNum).getOos();
        }
        else if (tag.equals(UNIT_TAGS.TYPE))
        {
            return cadData.getUnit(unitNum).getType();
        }
        else if (tag.equals(UNIT_TAGS.CURR_LOC))
        {
            return cadData.getUnit(unitNum).getCurrentLocation();
        }
        else if (tag.equals(UNIT_TAGS.DESTINATION))
        {
            return cadData.getUnit(unitNum).getDestination();
        }
        else if (tag.equals(UNIT_TAGS.MISC_INFO))
        {
            return cadData.getUnit(unitNum).getMisc();
        }
        else if (tag.equals(UNIT_TAGS.STACK))
        {
            return cadData.getUnit(unitNum).getStack();
        }
        else if (tag.equals(UNIT_TAGS.AREA))
        {
            return cadData.getUnit(unitNum).getArea();
        }
        else if (tag.equals(UNIT_TAGS.OFFICER))
        {
            return cadData.getUnit(unitNum).getOfficer();
        }
        else if (tag.equals(UNIT_TAGS.BADGE_NUM))
        {
            return cadData.getUnit(unitNum).getBadge();
        }
        else if (tag.equals(UNIT_TAGS.TIMER))
        {
            return cadData.getUnit(unitNum).getTimerInString();
        }
        else if (tag.equals(UNIT_TAGS.OFFICE))
        {
            return cadData.getUnit(unitNum).getOffice();
        }
        else if (tag.equals(UNIT_TAGS.P))
        {
            return cadData.getUnit(unitNum).getP();
        }
        else if (tag.equals(UNIT_TAGS.AGY))
        {
            return cadData.getUnit(unitNum).getAgy();
        }
        else if (tag.equals(UNIT_TAGS.ALIAS))
        {
            return cadData.getUnit(unitNum).getAlias();
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.getCadDataUnitValue");
        }
    }

    /**
     * Sets a field from Unit based on tag.
     *
     * @param tag a CADScriptTags tag
     * @param unitNum the unitNum to look up
     * @param value the new value to be set
     * @throws RemoteException
     */
    public void setCadDataUnitValue(String unitNum, UNIT_TAGS tag, Object value) throws RemoteException
    {
        if (tag.equals(UNIT_TAGS.MASTER_INC_NUM))
        {
            cadData.getUnit(unitNum).setMasterInc((String) value);
        }
        else if (tag.equals(UNIT_TAGS.STATUS))
        {
            cadData.getUnit(unitNum).setStatus((String) value);
        }
        else if (tag.equals(UNIT_TAGS.OOS))
        {
            cadData.getUnit(unitNum).setOos((String) value);
        }
        else if (tag.equals(UNIT_TAGS.TYPE))
        {
            cadData.getUnit(unitNum).setType((String) value);
        }
        else if (tag.equals(UNIT_TAGS.CURR_LOC))
        {
            cadData.getUnit(unitNum).setCurrentLocation((String) value);
        }
        else if (tag.equals(UNIT_TAGS.DESTINATION))
        {
            cadData.getUnit(unitNum).setDestination((String) value);
        }
        else if (tag.equals(UNIT_TAGS.MISC_INFO))
        {
            cadData.getUnit(unitNum).setMisc((String) value);
        }
        else if (tag.equals(UNIT_TAGS.STACK))
        {
            cadData.getUnit(unitNum).setStack((String) value);
        }
        else if (tag.equals(UNIT_TAGS.AREA))
        {
            cadData.getUnit(unitNum).setArea((String) value);
        }
        else if (tag.equals(UNIT_TAGS.OFFICER))
        {
            cadData.getUnit(unitNum).setOfficer((String) value);
        }
        else if (tag.equals(UNIT_TAGS.BADGE_NUM))
        {
            cadData.getUnit(unitNum).setBadge((String) value);
        }
        else if (tag.equals(UNIT_TAGS.UNIT_STATUS))
        {
            cadData.getUnit(unitNum).setUnitStatus((UnitStatusEnums) value);
        }
        else if (tag.equals(UNIT_TAGS.OFFICE))
        {
            cadData.getUnit(unitNum).setOffice((String) value);
        }
        else if (tag.equals(UNIT_TAGS.P))
        {
            cadData.getUnit(unitNum).setP((String) value);
        }
        else if (tag.equals(UNIT_TAGS.AGY))
        {
            cadData.getUnit(unitNum).setAgy((String) value);
        }
        else if (tag.equals(UNIT_TAGS.ALIAS))
        {
            cadData.getUnit(unitNum).setAlias((String) value);
        }
        else
        {
            throw new RemoteException("Wrong Enum sent into Coordinator.setCadDataUnitValue");
        }
    }

    /**
     * Assigns a unit to the specified incident.
     *
     * @param unitNum the unitNum to look up
     * @param id the incident id that this unit is assigned to.
     * @throws RemoteException
     */
    public void setCadDataUnitAssignedId(String unitNum, int id) throws RemoteException
    {
        cadData.getUnit(unitNum).setAssignedIncidentId(id);
    }

    /**
     * Adds a unit to the incident's list of assigned units.
     *
     * @param incidentId the incident
     * @param assignedUnitNum the unit that's been assigned to the incident
     * @throws RemoteException
     */
    public void addCadDataIncidentAssignedUnitNum(int incidentId, String assignedUnitNum) throws RemoteException
    {
        cadData.getIncident(incidentId).addAssignedUnitNum(assignedUnitNum);
    }

    /**
     * Sets the specified incident to the specified status.
     *
     * @param incidentId the incident
     * @param status the status of the incident
     * @throws RemoteException
     */
    public void setCadDataIncidentStatus(int incidentId, IncidentEnums status) throws RemoteException
    {
        cadData.getIncident(incidentId).setIncidentStatus(status);
    }

    /**
     * Returns a LinkedList containing entries that match the search string.
     *
     * @param search the string entry to be searched
     * @return linked list of CardfileDataObjects matching search
     */
    public LinkedList<CardfileDataObject> getSearchList(String search) throws RemoteException
    {
        return cardfileData.getSearchList(search);
    }

    /**
     * Updates the server database so clients can view additional informations as events
     * complete
     *
     * @param incidentNumber the incident in which the event occurred
     * @return completedEvent IncidentEvent with information to be added to server
     * database
     */
    public void updateIncidentInGUI(Integer incidentNumber, IncidentEvent completedEvent)
    {
        updateDetailsInGUI(incidentNumber, completedEvent);
        updateUnitsInGUI(incidentNumber, completedEvent);
    }

    /**
     * Updates the server database so clients can view details as events complete
     *
     * @param incidentNumber the incident in which the event occurred
     * @return completedEvent IncidentEvent with details to be added to server database
     */
    public void updateDetailsInGUI(Integer incidentNumber, IncidentEvent completedEvent)
    {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        for (int i = 0; i < completedEvent.eventInfo.getDetails().size(); i++)
        {
            String[] fields =
            {
                dateFormat.format(new Date()),
                timeFormat.format(new Date()),
                "", "", completedEvent.eventInfo.getDetails().elementAt(i).details
            };

            try
            {
                addCadDataIncidentTable(INC_TABLE.COMMENTS_NOTES, incidentNumber, fields);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the server database so clients can view unit updates as events complete
     *
     * @param incidentNumber the incident in which the event occurred
     * @return completedEvent IncidentEvent with unit updates to be added to server
     * database
     */
    public void updateUnitsInGUI(Integer incidentNumber, IncidentEvent completedEvent)
    {
        for (int i = 0; i < completedEvent.eventInfo.getUnits().size(); i++)
        {
            IncidentInquiryUnitsAssigned unit = completedEvent.eventInfo.getUnits().get(i);
            if (unit.statusType.equals("ENRT"))
            {
                cadData.unitAssignedToIncident(unit.beat, incidentNumber, unit.isPrimary);
            }
            else if (unit.statusType.equals("1097"))
            {
                cadData.unitArrivedAtIncidentScene(unit.beat, incidentNumber, unit.isPrimary);
            }
            else if (unit.statusType.equals("1098"))
            {
                cadData.unitAvailable(unit.beat);
            }
        }
    }

    /**
     * Sets the specified incidentNumber to viewable in the GUI.
     *
     * @param incidentNumber the number of the Incident started
     */
    public void incidentStartedInGUI(Integer incidentNumber)
    {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        cadData.getIncident(incidentNumber).setLogTime(dateFormat.format(date));
    }
}