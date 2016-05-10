package tmcsim.cadsimulator.viewer;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Observable;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import tmcsim.cadsimulator.viewer.model.SimulationStatusPanelModel;
import tmcsim.common.CADEnums;
import tmcsim.common.CADEnums.SCRIPT_STATUS;
import tmcsim.interfaces.CADViewer;

/**
 * A view from the console observing the current status information for the
 * CAD Simulator.
 * @author Jonathan Molina
 */
public class CADConsoleViewer implements CADViewer
{
    private SimulationStatusPanelModel simulationPanel;
    private SimulatorErrorHandler errorHandler;
    private StringBuffer infoMsgBuffer;
    private StringBuffer errorMsgBuffer;
    private Writer initialWriter;
    private PrintWriter out;
    
    /**
     * Logging Handler to listen for Information and Error
     * messages logged for the CAD Simulator.  Received LogRecords
     * are displayed in the info or error message Text Area.
     * 
     * @author Matthew Cechini
     */
    private class SimulatorErrorHandler extends Handler
    {
        /**
         * No implementation, only necessary to extend Handler.
         * @throws SecurityException not thrown
         */
        public void close()
        {
        }
        
        /**
         * No implementation, only necessary to extend Handler.
         */
        public void flush()
        {
        }
        
        /**
         * Updates the console view with the newly logged messages to the respective
         * info or error message text areas. Called every time a logged has been
         * recorded.
         * @param rec the most recent logged record
         */
        public void publish(LogRecord rec)
        {
            StringBuffer msgBuffer = new StringBuffer();

            msgBuffer.append(rec.getSourceClassName() + "." + 
                rec.getSourceMethodName() + " = " + 
                rec.getMessage());
            
            // info messages go under the info text area
            if (rec.getLevel() == Level.INFO)
            {
                infoMsgBuffer.append(msgBuffer.toString() + "\n");
                update(simulationPanel, null);
            }
            else
            {
                errorMsgBuffer.append(msgBuffer.toString() + "\n");
                update(simulationPanel, null);
            }
        }
    }
    
    /**
     * Instantiates this class. Logs errors from the tmcsim.cadsimulator package level.
     */
    public CADConsoleViewer()
    {
        errorHandler = new SimulatorErrorHandler();
        Logger.getLogger("tmcsim.cadsimulator").addHandler(errorHandler);
        infoMsgBuffer = new StringBuffer();
        errorMsgBuffer = new StringBuffer();
        out = new PrintWriter(System.out);
        simulationPanel = new SimulationStatusPanelModel();
    }
    
    /**
     * Redirect the printed output to the given writer. Assume setVisible(true) after
     * setting the writer.
     * @param writer the redirected writer
     */
    public void setWriter(Writer writer)
    {
        initialWriter = writer;
        out = new PrintWriter(initialWriter);
    }

    /**
     * Enables or disables this view.
     * @param state true to enable, false to disable
     */
    @Override
    public void setVisible(boolean state)
    {
        out.close();
        // print out initial state of console view
        if (state)
        {
            // start writing to the listed printwriter
            if (initialWriter != null)
            {
                out = new PrintWriter(initialWriter);
            }
            // default
            else
            {
                out = new PrintWriter(System.out);
            }

            update(simulationPanel, null);
        }
    }
    
    private String formatTime(long seconds)
    {
        String time = new String();     
        long timeSegment;   
        
        timeSegment = seconds / 3600;
        time += String.valueOf(timeSegment) + ":";      
        
        seconds = seconds % 3600;
        
        timeSegment = seconds / 60;
        // append extra 0's for formatting
        if (timeSegment < 10)
        {
            time += "0";
        }
        
        time += String.valueOf(timeSegment) + ":";      
        seconds = seconds % 60; 
        
        timeSegment = seconds;
        // append extra 0's for formatting
        if (timeSegment < 10)
        {
            time += "0";
        }
        
        time += String.valueOf(timeSegment);
        return time;
    }

    /**
     * Updates this console view. Updates only if the obs parameter is of type
     * SimulationStatusPanelModel.
     * @param obs updates if instanceof SimulationStatusPanelModel
     * @param obj not needed for update
     */
    @Override
    public void update(Observable obs, Object obj)
    {
        // type check observable
        if (obs instanceof SimulationStatusPanelModel)
        {
            simulationPanel = (SimulationStatusPanelModel) obs;
            
            out.println("--- CAD Simulator ---");
            out.println("Elapsed Simulation Time     : "
                    + formatTime(simulationPanel.getTimeSegment()));
            out.println("Status                      : "
                    + scriptStatusToString(simulationPanel.getScriptStatus()));
            out.println("Connected CAD Terminals     : "
                    + simulationPanel.getNumClients());
            
            String simManagerConnected = "No";
            // check for sim manager connection
            if (simulationPanel.isSimManagerConnected())
            {
                simManagerConnected = "Yes";
            }
            out.println("Simulation Manager Connected: "
                    + simManagerConnected);
            
            String paramicsStatus = "No";
            // check for paramics status connection
            if(simulationPanel.getParamicsStatus() == CADEnums.PARAMICS_STATUS.CONNECTED)
            {
                paramicsStatus = "Yes";
            }
            out.println("Connected to Paramics       : "
                    + paramicsStatus);

            out.println("Network Loaded              : " 
                    + simulationPanel.getNetworkLoaded());
            printInfoErrorMessages();
            out.flush();
        }
    }
    
    private String scriptStatusToString(SCRIPT_STATUS newStatus)
    {
        String result = "";
        // set status depending on script status
        switch (newStatus)
        {
            case NO_SCRIPT:
                result = "No Script";
                break;          
                
            case SCRIPT_STOPPED_NOT_STARTED:
                result = "Ready"; 
                break;          
                
            case SCRIPT_PAUSED_STARTED:
                result = "Paused";
                break;
                
            case SCRIPT_RUNNING:
                result = "Running";
                break;
            case ATMS_SYNCHRONIZATION:
                result = "Synchronizing";
                break;
            default:
                break;
        }
        return result;
    }
    
    private void printInfoErrorMessages()
    {
        out.println("-- Info Messages --");
        // check for info msgs
        if (infoMsgBuffer.length() > 0)
        {
            out.print(infoMsgBuffer.toString());
        }
        else
        {
            out.println();
        }
        out.println("-- Error Messages --");
        // check for error msgs
        if (errorMsgBuffer.length() > 0)
        {
            out.print(errorMsgBuffer.toString());
        }
        else
        {
            out.println();
        }
    }

}