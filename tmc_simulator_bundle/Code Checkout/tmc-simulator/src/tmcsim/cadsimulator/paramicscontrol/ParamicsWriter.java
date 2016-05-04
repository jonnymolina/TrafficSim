package tmcsim.cadsimulator.paramicscontrol;

import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tmcsim.common.CADProtocol.PARAMICS_ACTIONS;
import tmcsim.common.CADProtocol.PARAMICS_COMM_TAGS;

/**
 * ParamicsWriter is an abstract class to define objects that may
 * write data to the Paramics Communicator.  Each ParamicsWriter
 * is identified by a unique id and has a target file that will be 
 * written to.  This file may be a file name or relative path, which
 * will become relative to the target location of the remote Paramics
 * Communicator.  All implementing Writers call the writeXML() method
 * to transmit messages to the ParamicsCommunicator.
 * 
 * @author Matthew Cechini
 * @version
 */
public abstract class ParamicsWriter extends Observable {

    /** Error Logger. */
    private static Logger paramLogger = Logger.getLogger("tmcsim.cadsimulator.paramicscontrol");
    
    /** Unique identifier for writer. */
    public String writerID   = null;
    
    /** Target file writer will write to. */
    public String targetFile = null;
        
    /**
     * This method adopts the parameter message data Document into 
     * a new document that has tags for the writer's id, the action 
     * (WRITE_FILE) being performed.  This new Document is then sent 
     * to the observing ParamicsCommunicator for transmission.
     * 
     * @param xmlDoc Output Document containing message data.
     */
    public void writeXML(Document xmlDoc) {
        
        try {
            Document writerDoc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
            
            Element writerElem = writerDoc.createElement(PARAMICS_COMM_TAGS.WRITER.tag);
            writerElem.setAttribute(PARAMICS_COMM_TAGS.ID.tag, writerID.toString());
            writerElem.setAttribute(PARAMICS_COMM_TAGS.ACTION.tag,
                    PARAMICS_ACTIONS.WRITE_FILE.action);
            
            writerElem.appendChild(writerDoc.adoptNode(xmlDoc.getDocumentElement()));
        
            writerDoc.appendChild(writerElem);      
                    
            setChanged();
            notifyObservers(writerDoc);
        } catch(Exception e) {
            paramLogger.logp(Level.SEVERE, "ParamicsWriter", "writeXML", 
                    "Exception in writing XML for writer " + writerID, e);
        }
            
    };
}
