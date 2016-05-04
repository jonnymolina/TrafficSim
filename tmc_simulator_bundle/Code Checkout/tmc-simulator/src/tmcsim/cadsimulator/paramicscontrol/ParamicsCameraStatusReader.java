package tmcsim.cadsimulator.paramicscontrol;

import java.io.ByteArrayInputStream;
import java.util.logging.Level;

import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import tmcsim.cadsimulator.managers.ParamicsSimulationManager;


/**
 * ParamicsCameraStatusReader extends from the Abstract ParamicsReader to 
 * provide the methods necessary to read the Paramics Camrea Status file.
 * The receive() method is overloaded to parse the XML node for camrea status 
 * information.  The ParamicsSimulationManager is notified after each 
 * successful parse of a camera status update. 
 * 
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/06 20:46:40 $ $Revision: 1.4 $
 */
public class ParamicsCameraStatusReader extends ParamicsReader {
    
    /** 
     * Reference to the ParamicsSimulationManager used to send notifications 
     * of camera status changes.
     */
    private ParamicsSimulationManager paramicsSimMgr;
    
    /** A SAX Handler that is used to parse received Camera Status Node. */
    protected CameraStatusHandler csh = null;
    
    /**
     * Constructor.
     * 
     * @param theParamSimMgr The ParamicsSimulationManager object.
     */
    public ParamicsCameraStatusReader(ParamicsSimulationManager theParamSimMgr) {
        paramicsSimMgr = theParamSimMgr;        
        csh            = new CameraStatusHandler();
    }
    
    /**
     * This method parses the received XML node with the local CameraStatusHandler.
     * All updated camera information is sent to the ParamicsSimulationManager.
     */
    public void receive(Node rxMessage) {
        
        try {           
            if(rxMessage.getTextContent().length() > 0) {
                SAXParserFactory.newInstance().newSAXParser().parse(
                        new ByteArrayInputStream(rxMessage.getTextContent().getBytes()), csh);
            }
        }
        catch (Exception e) {
            paramLogger.logp(Level.SEVERE, "ParamicsCameraStatusReader", "receive",
                    "Exception in parsing received ParamicsCommMessage.", e);
            paramLogger.logp(Level.INFO, "ParamicsCameraStatusReader", "receive",
                    "Invalid received bytes", rxMessage.getTextContent().getBytes());
        }
    }
    

    /**
     * Internal SAX Handler used to parse the Camera Status Document read by
     * the remote Status Reader.  The schema for this document is: <br/>
     *
     * <Camera_Status>  <br>
     *     <Camera>  <br>
     *        <Identifier/> <br>
     *        <Route/> <br>
     *        <Direction/> <br>
     *        <Postmile/> <br>
     *        <Ave_Speed_NE/> <br>
     *        <Ave_Speed_SW/> <br>
     *     </Camera>  <br>
     * </Camera_Status> <br>
     */ 
    protected class CameraStatusHandler extends DefaultHandler {    

        private final String CAMERA       = "Camera";
        private final String CAMERA_ID    = "Identifier";
        private final String ROUTE        = "Route";
        private final String DIRECTION    = "Direction";        
        private final String POSTMILE     = "Postmile";
        private final String AVG_SPEED_NE = "Ave_Speed_NE";
        private final String AVG_SPEED_SW = "Ave_Speed_SW";

        private StringBuffer parsedValue  = new StringBuffer();
        
        Integer cameraID  = new Integer(-1);
        String route      = "";
        String direction  = "";
        float postmile    = 0.0f;
        float avgSpeed_NE = 0.0f;
        float avgSpeed_SW = 0.0f;
        
        
        public void startDocument() {   
        }   
        
        public void characters(char[] ch, int start, int length) {
            parsedValue.append(new String(ch, start, length).trim());   
        }
        
        public void endElement(String uri, String localName, String qName)  {
            
            if(qName.equals(CAMERA_ID)) { cameraID = Integer.parseInt(parsedValue.toString()); }
            else if(qName.equals(ROUTE)) { route = parsedValue.toString(); }
            else if(qName.equals(DIRECTION)) { direction = parsedValue.toString(); }
            else if(qName.equals(POSTMILE)) { postmile = Float.parseFloat(parsedValue.toString()); }
            else if(qName.equals(AVG_SPEED_NE)) { avgSpeed_NE = Float.parseFloat(parsedValue.toString()); }
            else if(qName.equals(AVG_SPEED_SW)) { avgSpeed_SW = Float.parseFloat(parsedValue.toString()); }
            else if(qName.equals(CAMERA)) { 
                paramicsSimMgr.updateCameraInfo(cameraID, avgSpeed_NE, avgSpeed_SW);
            }
            
            parsedValue.setLength(0);
        }   
        
        public void error(SAXParseException e) {
            paramLogger.logp(Level.SEVERE, "ParamicsCameraStatusReader:CameraStatusHandler", 
                    "error", "Error in parsing received ParamicsCommMessage.", e);
        }
        
        public void fatalError(SAXParseException e) {
            paramLogger.logp(Level.SEVERE, "ParamicsCameraStatusReader:CameraStatusHandler", 
                    "error", "Fatal error in parsing received ParamicsCommMessage.", e);
        }
        
        public void warning(SAXParseException e) {
            paramLogger.logp(Level.WARNING, "ParamicsCameraStatusReader:CameraStatusHandler", 
                    "error", "Warning in parsing received ParamicsCommMessage.", e);
        }       
    }   
    
}