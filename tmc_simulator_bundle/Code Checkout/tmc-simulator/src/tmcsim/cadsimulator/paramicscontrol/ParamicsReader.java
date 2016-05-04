package tmcsim.cadsimulator.paramicscontrol;

import java.util.logging.Logger;

import org.w3c.dom.Node;

/**
 * ParamicsReader is an abstract class to define objects that may
 * read data from the Paramics Communicator.  Each ParamicsWriter
 * is identified by a unique id.  The reader is assigned a target file 
 * that will be read from and the duration (in seconds) between reads.
 * All implementing Readers must implement the receive() method to 
 * parse the XML data that is received from the ParamicsCommunicator.
 * 
 * @author Matthew Cechini
 * @version
 */
public abstract class ParamicsReader {
    
    /** Error Logger */
    protected static Logger paramLogger = Logger.getLogger("tmcsim.cadsimulator.paramicscontrol");
    
    /** Unique identifier for the reader. */
    public String readerID   = null;

    /** Interval (in seconds) between reads. */
    public String interval   = null;
    
    /** Target file being read by the ParamicsCommunicator. */
    public String targetFile = null;
    
    /** Abstract method to receive a XML Node object containing message data.*/
    public abstract void receive(Node rxMessage);
}
