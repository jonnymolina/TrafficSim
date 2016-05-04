package tmcsim.cadsimulator.paramicscontrol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tmcsim.cadsimulator.managers.ParamicsSimulationManager;
import tmcsim.common.CADEnums.PARAMICS_STATUS;
import tmcsim.common.CADProtocol.PARAMICS_ACTIONS;
import tmcsim.common.CADProtocol.PARAMICS_COMM_TAGS;

/**
 * The ParamicsCommunicator is a singleton object used to handle communication
 * between the CADSimulator and the remote ParamicsCommunicator.  A socket 
 * connection is established to the ParamicsCommunicator, and all data is read 
 * and written through the socket.  ParamicsWriters and ParamicsReaders are 
 * registered with this object to read and write specific data to and from files
 * on the remote ParamicsCommunicator machine.  The remote ParamicsCommunicator
 * is notified of this registeration (and unregistration) process so there is a
 * direct correlation between registered objects in both Communicators.  After a
 * ParamicsReader is registered, any messages received from the remotely
 * created reader are sent to the local ParamicsReader object.  After a writer is 
 * registered, this object is set as an Observer.  When message are to be sent,
 * they are received through the update() method and transmitted to the remote
 * ParamicsCommunicator.    
 * 
 * @author 
 * @version
 */
public class ParamicsCommunicator extends Thread implements Observer {
    
    /** Error Logger. */
    private Logger paramLogger = Logger.getLogger("tmcsim.cadsimulator.paramicscontrol");
    
    /** Remote Paramics Communicator host name. */
    private static String paramicsHost   = null;
    
    /** Remote Paramics Communicator port. */
    private static Integer paramicsPort  = null;

    /** Instance of the Coordinator to update with Paramics status */
    private ParamicsSimulationManager theController = null;
    
    /** private used for communication with the remote Paramics Communicator. */
    private Socket paramicsSocket = null;
    
    /** Input stream for reading from socket. */
    private ObjectInputStream in     = null;
    
    /** Output stream for writing to the socket. */
    private ObjectOutputStream out = null;
    
    /** Map of all registered paramics readers, indexed by a unique  id. */
    private TreeMap<String, ParamicsReader> paramicsReaders = null;

    /** Map of all registered paramics writers, indexed by a unique  id. */
    private TreeMap<String, ParamicsWriter> paramicsWriters = null; 
    
    /** 
     * Boolean flag to designate whether a connection to the remote
     * Paramics Communicator is established.
     */ 
    private boolean connected = false;
    
    /** Counter for assigning unique ids. */
    private int nextIdentifier = 0;     
    

    /**
     * Constructor.  Initialize member data.
     */
    public ParamicsCommunicator(ParamicsSimulationManager cntrl, 
            String newHost, Integer newPort) {

        theController  = cntrl;
        paramicsHost   = newHost;
        paramicsPort   = newPort;   
        
        paramicsReaders = new TreeMap<String, ParamicsReader>();
        paramicsWriters = new TreeMap<String, ParamicsWriter>();
            
    }
    
    /**
     * Returns the next unique identifier that may be used for a ParamicsWriter or
     * ParamicsReader.
     * @return int Next unique identifier value
     */
    public String nextID() {
        return String.valueOf(nextIdentifier++);
    }
    
    
    /**
     * Method connects to host and port where the remote paramics communicator 
     * has connected.  Input and Output streams are created on the new Socket
     * connection.  A RESET message is sent transmitted on the socket and this
     * thread is started.  
     */
    public void connect() throws IOException {
        
        try {
            paramicsSocket = new Socket();
            paramicsSocket.setSoTimeout(5000);
            paramicsSocket.connect(new InetSocketAddress(paramicsHost, paramicsPort));

            //** out must be performed before in to unlock for connected socket **//
            out = new ObjectOutputStream(paramicsSocket.getOutputStream());
            in  = new ObjectInputStream(paramicsSocket.getInputStream());
        }
        catch (IOException e) {
            paramicsSocket.close();
            
            paramLogger.logp(Level.SEVERE, "ParamicsCommunicator", "connect()", 
                    "Exception in creating input and output streams on socket.", e);
            
            throw new IOException("IOException in connecting to Paramics Socket");
        }

        connected = true;
        
        reset();
        
        if(getState() == State.NEW)
            start();
        
    }
    
    /**
     * This thread is interrupted and if a conenction has been made, 
     * it is closed after transmitting a RESET message.
     */
    public void disconnect() {
        interrupt();
        
        if(!connected)
            return;     
        
        reset();
        
        cleanup();      
                        
    }

    
    /**
     * Returns status of connection to remote Paramics Communicator.
     * @return True if a connection is established, false if not.
     */
    public boolean isConnected() { 
        return connected; 
    }
    
    /**
     * While a connection is established to the remote paramics communicator,
     * read messages from the input stream.  Get the ParamicsReader from the
     * local list whose unique ID matches that of the message sender, and call
     * the receive() method on that reader.
     */
    public void run() {
        
        while(connected) {
            try {               
                Document rxMessage = (Document)in.readObject();
                Element docElement = rxMessage.getDocumentElement();

                String readerID         = docElement.getAttribute(PARAMICS_COMM_TAGS.ID.tag);               
                ParamicsReader rxReader = paramicsReaders.get(readerID);
                
                if(rxReader != null) {
                    rxReader.receive(docElement.getChildNodes().item(0));
                }
            }   
            catch(SocketTimeoutException ste) {
                //just try again
            }       
            catch (Exception e) {
                paramLogger.logp(Level.SEVERE, "ParamicsCommunicator", "run()", 
                        "Exception in reading from the socket.", e);
                cleanup();
                
                theController.updateParamicsStatus(PARAMICS_STATUS.DROPPED);
            }
        }       
    }
    
    /**
     * Observer/Observable update method.  The Paramics Communicator observers
     * registered ParamicsWriters.  When messages are to be sent, they are sent
     * through this method.  All messages are ParamicsCommMessage objects.  Send 
     * these messages to the write() method for transmission on the socket.
     */
    public void update(Observable o, Object arg) {      
        write((Document)arg);               
    }
    
    
    /**
     * If a connection has been established, transmit a message to
     * register the reader, and add it to the local list of
     * ParamicsReaders.
     * 
     * @param reader The new registering ParamicsReader.
     */
    public void registerReader(ParamicsReader reader) {
        if(connected && !paramicsReaders.containsKey(reader.readerID)) {
            
            try {
                Document readerDoc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
        
                Element readerElem = readerDoc.createElement(PARAMICS_COMM_TAGS.READER.tag);
                readerElem.setAttribute(PARAMICS_COMM_TAGS.ID.tag, reader.readerID);
                readerElem.setAttribute(PARAMICS_COMM_TAGS.ACTION.tag, 
                        PARAMICS_ACTIONS.REGISTER.action);
                
                Element intElem = readerDoc.createElement(PARAMICS_COMM_TAGS.INTERVAL.tag);
                intElem.appendChild(readerDoc.createTextNode(reader.interval));
                readerElem.appendChild(intElem);    
                
                Element fileElem = readerDoc.createElement(PARAMICS_COMM_TAGS.TARGET_FILE.tag);
                fileElem.appendChild(readerDoc.createTextNode(reader.targetFile));
                readerElem.appendChild(fileElem);
        
                readerDoc.appendChild(readerElem);          
                    
                write(readerDoc);

                paramicsReaders.put(reader.readerID, reader);
            }
            catch (Exception e) {
                paramLogger.logp(Level.SEVERE, "ParamicsCommunicator", 
                        "registerReader", "Exception in registering reader.", e);
            }       
        }
    }
    
    /**
     * If a connection has been established, transmit a message to
     * unregister the reader, and remove it from the local list of
     * ParamicsReaders.
     * 
     * @param reader The unregistering ParamicsReader.
     */
    public void unregisterReader(ParamicsReader reader) {
        if(connected && paramicsReaders.containsKey(reader.readerID)) {
            
            try {
                Document readerDoc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
        
                Element readerElem = readerDoc.createElement(PARAMICS_COMM_TAGS.READER.tag);
                readerElem.setAttribute(PARAMICS_COMM_TAGS.ID.tag, reader.readerID);
                readerElem.setAttribute(PARAMICS_COMM_TAGS.ACTION.tag, 
                        PARAMICS_ACTIONS.UNREGISTER.action);
        
                readerDoc.appendChild(readerElem);          
                    
                write(readerDoc);

                paramicsReaders.remove(reader.readerID);
            }
            catch (Exception e) {
                paramLogger.logp(Level.SEVERE, "ParamicsCommunicator", 
                        "unregisterReader", "Exception in unregistering reader.", e);
            }           
        }
    }
    
    /**
     * If a connection has been established, transmit a message to
     * register the writer, and add it to the local list of
     * ParamicsWriters.  This object is set as an observer to the
     * registering writer.
     * 
     * @param writer The new registering ParamicsWriter.
     */
    public void registerWriter(ParamicsWriter writer) {
        
        if(connected && !paramicsWriters.containsKey(writer.writerID)) {
            try {
                Document writerDoc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
        
                Element writerElem = writerDoc.createElement(PARAMICS_COMM_TAGS.WRITER.tag);
                writerElem.setAttribute(PARAMICS_COMM_TAGS.ID.tag, writer.writerID);
                writerElem.setAttribute(PARAMICS_COMM_TAGS.ACTION.tag,
                        PARAMICS_ACTIONS.REGISTER.action);
                
                Element fileElem = writerDoc.createElement(PARAMICS_COMM_TAGS.TARGET_FILE.tag);
                fileElem.appendChild(writerDoc.createTextNode(writer.targetFile));
                writerElem.appendChild(fileElem);
        
                writerDoc.appendChild(writerElem);          
                    
                write(writerDoc);

                
                paramicsWriters.put(writer.writerID, writer);
                
                writer.addObserver(this);
            }
            catch (Exception e) {
                paramLogger.logp(Level.SEVERE, "ParamicsCommunicator", 
                        "registerWriter", "Exception in registering writer.", e);
            }           
        }   
    }
    
    /**
     * If a connection has been established, transmit a message to
     * unregister the writer, and remove it from the local list of
     * ParamicsWriters.  This object is removed as an Observer
     * from the unregistering writer.
     * 
     * @param writer The unregistering ParamicsWriter.
     */
    public void unregisterWriter(ParamicsWriter writer) {
        if(connected && paramicsWriters.containsKey(writer.writerID)) {
            
            try {
                Document writerDoc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
        
                Element writerElem = writerDoc.createElement(PARAMICS_COMM_TAGS.WRITER.tag);
                writerElem.setAttribute(PARAMICS_COMM_TAGS.ID.tag, writer.writerID);
                writerElem.setAttribute(PARAMICS_COMM_TAGS.ACTION.tag, 
                        PARAMICS_ACTIONS.UNREGISTER.action);
        
                writerDoc.appendChild(writerElem);          
                    
                write(writerDoc);
                    
                paramicsWriters.remove(writer.writerID);
                
                writer.deleteObserver(this);
            }
            catch (Exception e) {
                paramLogger.logp(Level.SEVERE, "ParamicsCommunicator", 
                        "unregisterWriter", "Exception in unregistering writer.", e);
            }
        }   
    }   
    
    protected void reset() {
        
        try {
            Document resetDoc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        
            Element resetElem = resetDoc.createElement(PARAMICS_COMM_TAGS.RESET.tag);
        
            resetDoc.appendChild(resetElem);        
                
            write(resetDoc);
        }
        catch (Exception e) {
            paramLogger.logp(Level.SEVERE, "ParamicsCommunicator", 
                    "reset", "Exception in reseting.", e);
        }

    }
    
    /**
     * Close the input and output streams and the socket.  Clear the lists
     * of readers and writers, and set the connection flag to false.
     */
    protected void cleanup() {
        
        try {out.close();} catch (Exception e) {}
        try {in.close();} catch (Exception e) {}
        try {paramicsSocket.close();} catch (Exception e) {}
        
        paramicsReaders.clear();
        paramicsWriters.clear();
        connected = false;  
    }   
    
    /**
     * Write the parameter ParamicsCommMessage to the output stream.  Flush 
     * the stream to be sure all data is transmitted.
     * 
     * @param message The message to transmit.
     */
    protected synchronized void write(Document output) {
        
        try {
            out.writeObject(output);
            out.flush();        
        }
        catch (Exception e) {
            paramLogger.logp(Level.SEVERE, "ParamicsCommunicator", "write", 
                    "Exception in writing to the socket.", e);
            cleanup();          
            
            theController.updateParamicsStatus(PARAMICS_STATUS.DROPPED);
        }
        
    }

        
}