package tmcsim.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import tmcsim.common.SimulationException;


/**
 * CADClientSocket contains the Socket used to communication between the CAD 
 * Client and the CAD Simulator. This class uses the host and port parameter 
 * values to connect to the CAD Simulator's ServerSocket. I/O streams are 
 * created from this connection to transfer all data between applications.
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/06 20:46:41 $ $Revision: 1.3 $
 */
class CADClientSocket { 
    
    /** Socket used to communicate to the CAD Simulator. */
    private Socket clientSocket;
          
    /**
     * Constructor accepting the hostname and port for the CAD Simulator's socket.
     *
     * @param newhost Target host name for CAD Simulator.
     * @param newport Target port for CAD Simulator.
     * @throws SimulationException if there is an exception in connecting to the CAD Simulator.
     */    
    CADClientSocket(String newhost, int newport) throws SimulationException {
       
        try {
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(newhost, newport), 5000); 
        } 
        catch (IOException ioe) {
            throw new SimulationException(SimulationException.CAD_SIM_CONNECT, ioe);
        }
        
    }   
   
    /**
     * Returns a reference to the input stream associated with this socket.
     *
     * @return InputStream Input Stream for socket.
     * @throws SimulationException if there is an exception in opening the input stream.
     */     
    public InputStream getInputStream() throws SimulationException {
        try {
            return clientSocket.getInputStream();
        } 
        catch (IOException ioe) {
            throw new SimulationException(SimulationException.CAD_SIM_COMM, ioe);
        }
    }
    
    /**
     * Returns a reference to the output stream associated with this socket.
     *
     * @return OutputStream Output Stream for socket.
     * @throws SimulationException if there is an exception in opening the output stream.
     */     
    public OutputStream getOutputStream() throws SimulationException {
        try {
            return clientSocket.getOutputStream();
        } 
        catch (IOException ioe) {
            throw new SimulationException(SimulationException.CAD_SIM_COMM, ioe);
        }
    }           
      
    /**
     * Closes the Socket.
     * 
     * @throws SimulationException if there is an exception in closing the socket.
     */
    public void closeSocket() throws SimulationException {
        try {
            clientSocket.close(); 
        } 
        catch (IOException ioe) {
            throw new SimulationException(SimulationException.CAD_SIM_COMM, ioe);
        }
    }
}