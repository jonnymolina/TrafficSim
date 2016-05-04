package tmcsim.cadsimulator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import tmcsim.common.SimulationException;


/**
 * CADSimulatorSocketHandler is used to accept socket connections from multiple
 * CAD Clients.  At initialization, this class binds a ServerSocket to a specific
 * port, passed in as a parameter.  This class is threaded, and when it is 'run',
 * it begins accepting connections.  When a connection is made, a Socket is created
 * for data communication with that client and a CADSimulatorClient thread
 * is spun with connection to that socket.  This thread will continue to accept
 * clients indefinitely.
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/06 20:46:41 $ $Revision: 1.3 $
 */
public class CADSimulatorSocketHandler extends Thread {
    
    /** Error logger. */
    private Logger socketLogger = Logger.getLogger("tmcsim.cadsimulator");

    /** ServerSocket used to accept connections from clients. */
    private ServerSocket serverSocket;    
    
    /**
     * Constructor.  A ServerSocket is bound to the parameter port number.
     *
     * @param port Port number for ServerSocket binding.
     * @throws SimulationException if an error occurs binding the ServerSocket.
     */
    public CADSimulatorSocketHandler(Integer port) throws SimulationException {
    
        try {   
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(5000);  //delay for accept timeout(milliseconds)
        } catch (IOException ioe) { 
            throw new SimulationException(SimulationException.BINDING, ioe);
        }
    }   

    /**
     * While this thread is not interrupted, connection requests are continuously
     * accepted from CAD Clients.  When a connection is accepted, a Socket is created,
     * passed into the CADSimulatorClient's constructor, and then that new 
     * Client thread is spun off, and the ServerSocket returns to accepting 
     * more clients.  The CADSimulatorViewer is notified of a successful client
     * conection after the Socket creating completes.  If there is an error in 
     * establishing connection to a client, the ServerSocket will continue to 
     * accept connections.
     */
    public void run() { 
        
        Socket clientSocket;
        
        while (!isInterrupted()) {
            try 
            {
                clientSocket = serverSocket.accept();

                CADSimulatorClient theTMClient = new CADSimulatorClient(clientSocket);
                theTMClient.start();
                //CADSimulator.theViewer.connectClient();
            }     
            catch(SocketTimeoutException ste) {}
            catch(Exception e) {
                socketLogger.logp(Level.SEVERE, "CADSimulatorSocketHandler", "run", 
                        "Exception in creating a connection to a remote CAD Client.", e);
            }
        }
        
        try {
            serverSocket.close();
        }
        catch (IOException ioe) {
            socketLogger.logp(Level.SEVERE, "CADSimulatorSocketHandler", "run", 
                    "Exception in closing socket.", ioe);
        }
    }   
}