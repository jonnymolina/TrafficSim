package tmcsim.cadsimulator.managers;

import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

import tmcsim.cadsimulator.Coordinator;
import tmcsim.common.CADEnums.SCRIPT_STATUS;

public class SimulationControlManager  {

    /** 
     * 
     */
    private class ClockTask extends TimerTask {     
        public void run() {
            currentSimTime++;
            theCoordinator.tick();
        }
    }
    
    /** The SimulationTimer used to keep track of simulation time. */
    private Timer simTimer = null;
    
    /**  */
    private Coordinator theCoordinator;
    
        /**
     * Boolean flag to designate whether the simulation has been started or not.
     */    
    private boolean simulationStarted;
    
    /**
     * Object used to count the number of seconds that the simulation has run.
     * The value is initialized to 0, and is reset to 0 every time a new simulation
     * is loaded, or the current simulation is stopped and reset.
     */
    private long currentSimTime;    
    
    
    public SimulationControlManager(Coordinator coor) {
        
        theCoordinator    = coor;       
        simulationStarted = false;
        currentSimTime    = 0;    
    }
    
    public boolean simulationStarted() {
        return simulationStarted;
    }
    
    public long getCurrentSimTime() {
        return currentSimTime;
    }
    
    public void gotoSimulationTime(long newSimTime) {
        currentSimTime = newSimTime;
    }
    
    public void startSimulation() {
            
        simTimer = new Timer();     
        simTimer.scheduleAtFixedRate(new ClockTask(), 0, 1000);
        
        simulationStarted = true;   
        
        theCoordinator.setScriptStatus(SCRIPT_STATUS.SCRIPT_RUNNING);
    }
    
    public void pauseSimulation() {  
        
        if(simTimer != null)
            simTimer.cancel();          
            
        simulationStarted = false;              
        
        theCoordinator.setScriptStatus(SCRIPT_STATUS.SCRIPT_PAUSED_STARTED);
    }
    
    public void resetSimulation() throws RemoteException {      
        
        if(simTimer != null)
            simTimer.cancel(); 
         
        currentSimTime = 0;              
        
        theCoordinator.setScriptStatus(SCRIPT_STATUS.SCRIPT_STOPPED_NOT_STARTED);
    }
    
}
