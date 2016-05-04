package tmcsim.paramicscommunicator;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Element;

import tmcsim.paramicscommunicator.FileIOUpdate.IO_TYPE;

/**
 * The ParamicsFileWriter handles writing messages to a target file which
 * is read by Paramics.  Messages are received through the writeMessage() method.
 * This object handles queueing messages and writing as the file becomes
 * available.  New data is written to the target file when it has been
 * modified (cleared) by Paramics.  If this does not happen, messages
 * are queued and a timer is used to periodically determine if the
 * file has become available for writing.
 * 
 * @author Matthew Cechini
 * @version
 */
public class ParamicsFileWriter extends Observable {
    
    /** 
     * Duration (in  ms) that the TimerTask will be scheduled to
     * retry writing to the target file. Default = 2000ms
     */
    private static long TIMER_DURATION = 2000;
    
    /** Error Logger. */
    private Logger paramLogger = Logger.getLogger("tmcsim.paramicscommunicator");
    
    /** Linked List of messages that have been received */
    private LinkedList<Element> queuedMessages = null;
    
    /**  */
    private String writerID = null;

    /** File name of the file where data is written */
    private String outputFile = null;
    
    /** FileWriter used to write data to the output file. */
    private FileWriter fileWriter = null;
    
    /** Value (seconds since 1/1/1970) of output file's last modifcation time */
    private long lastModified = 0;  
    
    /** Timer used to schedule file writing tasks. */
    private Timer writerTimer = null;
    
    /**
     * A TimerTask to retry writing messages that have been queued within this
     * ParamicsWriter.  If a message has been queued, see if the target file
     * has been modified since last write.  If so, write the first queued message
     * to the file and remove the message from the queue.  If writing is unsuccessful,
     * do not remove the message from the queue.  If there are no more messages in 
     * the queue, cancel this timer.
     */
    private class WriterTimerTask extends TimerTask {
        public void run() {

            synchronized(lock) {
            
                //if we've queued something, continue.
                if(queuedMessages.size() > 0) {         
                    
                    //if file has been modified, write to it
                    if(lastModified < new File(outputFile).lastModified())
                    {       
                        try {
                            writeToFile(queuedMessages.getFirst());
                            queuedMessages.remove(0);
                        }
                        catch (IOException ioe) {
                            paramLogger.logp(Level.SEVERE, "ParamicsFileWriter.WriterTimerTask", 
                                    "run()", "Exception in writing to the target file: " + 
                                    outputFile + ".  Queue size = " + queuedMessages.size(), ioe);
                        }
                        
                        //all queued messages gone, cancel timer
                        if(queuedMessages.size() == 0)
                            this.cancel();
                    }   
                }
            }               
        }
    }
    
    /** Synchronizing lock to protect File IO and message queuing. */
    private Object lock = null;     
    
    /**
     * Constructor.  Initialize data objects.  If the target file exists, delete 
     * it, and then create a new file.  
     *
     * @param workingDir Directory path where the output file is to be written
     * @param mess The ParamicsCommMessage containing the outputFile filename.
     */
    public ParamicsFileWriter(String id, String workingDir, String targetFile) {

        try {
            writerID = id;
    
            queuedMessages  = new LinkedList<Element>();    
            lock            = new Object();
                        
            outputFile      = workingDir + targetFile;  
            
            File tempFile = new File(outputFile);           
            if(tempFile.exists()) {
                tempFile.delete();
            }

            tempFile.createNewFile();           
                                                
            writerTimer     = new Timer();
            
        } catch (IOException ioe) {
            paramLogger.logp(Level.SEVERE, "ParamicsFileWriter", "Constructor", 
                    "Unable to create Paramics File Writer.", ioe);
        }
        
    }
    
    
    /**
     * Method is called when a message has been received from the CAD Simulator.
     * If the message queue is not empty, add the new message to the queue.
     * If the output file has not been modified (read) since last write,
     * add the message to the queue and set a timer to repeatedly check for 
     * modification to the output file.  Else, write the new message to the file.  
     * If there is an error in writing the data, queue the message start a timer
     * to retry the writing.
     *
     * @param newMessage The received message which is to be written to 
     * the output file.
     */
    public void writeMessage(Element messageElem) {
        
        synchronized(lock) {
            
            //messages already queued... get in line.           
            if(queuedMessages.size() > 0) {
                queuedMessages.add(messageElem);

                paramLogger.log(Level.INFO, "Queueing message, new queue " +
                        "size = " + queuedMessages.size());
            }               
            //No modification since last write. (first queue)               
            else if (lastModified >= new File(outputFile).lastModified()) {
                queuedMessages.add(messageElem);                
                                
                writerTimer.scheduleAtFixedRate(new WriterTimerTask(), 
                        0L, TIMER_DURATION);

                paramLogger.log(Level.INFO, "First message queued");
            }           
            //free and clear, write.
            else {
                try {
                    writeToFile(messageElem);
                }
                catch(IOException ioe) {
                    paramLogger.logp(Level.SEVERE, "ParamicsFileWriter", 
                            "writeMessage()", "Exception in writing to the " +
                            "target file: " + outputFile, ioe);
                    
                    queuedMessages.add(messageElem);
                    
                    writerTimer.scheduleAtFixedRate(new WriterTimerTask(), 
                            0L, TIMER_DURATION);
                }
            }
        }
    }
        
    /**
     * Method writes data to the output file.
     * 
     * @param output Data to be written to the file.
     */
    private void writeToFile(Element output) throws IOException {       
        
        fileWriter = new FileWriter(outputFile);
        
        OutputFormat of = new OutputFormat("XML","ISO-8859-1",true);
        of.setIndent(1);
        of.setIndenting(true);
        
        XMLSerializer serializer = new XMLSerializer(fileWriter, of);
        serializer.asDOMSerializer();
        serializer.serialize(output);

        /** Added by Nathaniel Lehrer */
        try {
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            new XMLSerializer(outputStream, of).serialize(output);
            tmcsim.paramicslog.ParamicsLog.getInstance().writeToLog(outputStream.toString());
        } catch(Exception e) {
            System.out.println(e);
        }
        /** End Add by Nathaniel Lehrer */
        
        fileWriter.flush();
        fileWriter.close();
        
        lastModified = new File(outputFile).lastModified();     
        
        setChanged();
        notifyObservers(new FileIOUpdate(IO_TYPE.WRITE, writerID, new File(outputFile).length()));
                            
    }               
}