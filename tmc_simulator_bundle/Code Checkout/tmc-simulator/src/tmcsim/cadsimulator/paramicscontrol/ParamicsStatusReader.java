package tmcsim.cadsimulator.paramicscontrol;

import java.io.ByteArrayInputStream;
import java.util.logging.Level;

import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import tmcsim.cadsimulator.managers.ParamicsSimulationManager;
import tmcsim.common.CADEnums.PARAMICS_STATUS;

/**
 * ParamicsStatusReader extends from the Abstrct ParamicsReader to provide
 * the methods necessary to read the Paramics Status file.  The receive()
 * method is overloaded to parse the XML node for status information.
 * The ParamicsSimulationManager is notified after each successful parse
 * of a Paramics Status Node. 
 *
 * @author Matthew Cechini
 * @version
 */
public class ParamicsStatusReader extends ParamicsReader {
    
    /** Current Paramics Status. */
    protected PARAMICS_STATUS currentStatus;
    
    /** Current Network ID that has been loaded. */
    protected int currentNetwork;
    
    /** 
     * Reference to the ParamicsSimulationManager used to send notifications 
     * of Paramics status changes.
     */
    protected ParamicsSimulationManager paramicsSimMgr = null;  

    /** SAX Handler that is used to parse the recieved Paramics Status Node. */
    protected ParamicsStatusHandler psh = null; 
    
    
    /**
     * Constructor. Initialize Paramics Status to UNKNOWN and network loaded to -1.
     *
     * @param theParamSimMgr The ParamicsSimulationManager object.
     */
    public ParamicsStatusReader(ParamicsSimulationManager theParamSimMgr) {
        paramicsSimMgr = theParamSimMgr;
        
        psh = new ParamicsStatusHandler();
        
        currentStatus  = PARAMICS_STATUS.UNKNOWN;
        currentNetwork = -1;
    }
    
    /** Get the current paramics status */
    public PARAMICS_STATUS getStatus() { return currentStatus; }
    
    /**  Get the ID of the current paramics network. (Default: -1) */
    public int getNetworkID() { return currentNetwork; }
    
    /**
     * Reset the Paramics Status info.  The currentStatus will be set to UNKNOWN
     * and the currentNetwork set to -1.
     */
    public void resetStatusInfo() {
        currentStatus  = PARAMICS_STATUS.UNKNOWN;
        currentNetwork = -1;
    }
    
    /**
     * This method parses the received XML Node with the local 
     * ParamicsStatusHandler.  The parsed Paramics Status is sent to the 
     * ParamicsSimulationManager.
     */
    public void receive(Node rxMessage) {
        
        try {           
            if(rxMessage.getTextContent().length() > 0)
                SAXParserFactory.newInstance().newSAXParser().parse(
                        new ByteArrayInputStream(rxMessage.getTextContent().getBytes()), psh);  
        }
        catch (Exception e) {
            paramLogger.logp(Level.SEVERE, "ParamicsStatusReader", "receive",
                    "Exception in parsing received ParamicsCommMessage.", e);
            paramLogger.logp(Level.INFO, "ParamicsStatusReader", "receive",
                    "Invalid received bytes", rxMessage.getTextContent().getBytes());           
        }
                
        paramicsSimMgr.updateParamicsStatus(currentStatus);
    }
    
    /**
     * Internal SAX Handler used to parse the Camera Status Document read by
     * the remote Status Reader.  The schema for this document is: <br/>
     *
     * <Paramics>
     *    <Network_Status/>    ("LOADING", "WARMING", or "LOADED")
     *    <Network_ID/>         (integer network ID)
     * </Paramics>
     */ 
    protected class ParamicsStatusHandler extends DefaultHandler {  
    

        /** String XML creation.  Tag contains information for the paramics status. */
        private final String NETWORK_STATUS = "Network_Status";
        
        /** String XML creation.  Tag contains information for the loaded paramics network id. */
        private final String NETWORK_ID     = "Network_ID";
        
        private StringBuffer parsedValue = new StringBuffer();
        
        public void startDocument() {               

        }   
        
        public void characters(char[] ch, int start, int length) {
            parsedValue.append(new String(ch, start, length).trim());       
        }
        
        public void endElement(String uri, String localName, String qName)  {
            
            if(qName.equals(NETWORK_STATUS)) { 
            
                currentStatus = PARAMICS_STATUS.UNKNOWN;
                for(PARAMICS_STATUS val : PARAMICS_STATUS.values()) {
                    if(val.toString().equals(parsedValue.toString())) 
                        currentStatus = val;
                }
            }
            else if(qName.equals(NETWORK_ID)) { 
                currentNetwork = Integer.parseInt(parsedValue.toString()); 
            }
            
            parsedValue.setLength(0);

        }   
        
        public void error(SAXParseException e) {
            paramLogger.logp(Level.SEVERE, "ParamicsStatusReader:ParamicsStatusHandler", 
                    "error", "Error in parsing received ParamicsCommMessage.", e);
        }
        
        public void fatalError(SAXParseException e) {
            paramLogger.logp(Level.SEVERE, "ParamicsStatusReader:ParamicsStatusHandler", 
                    "error", "Fatal error in parsing received ParamicsCommMessage.", e);
        }
        
        public void warning(SAXParseException e) {
            paramLogger.logp(Level.WARNING, "ParamicsStatusReader:ParamicsStatusHandler", 
                    "error", "Warning in parsing received ParamicsCommMessage.", e);
        }       
    }
}