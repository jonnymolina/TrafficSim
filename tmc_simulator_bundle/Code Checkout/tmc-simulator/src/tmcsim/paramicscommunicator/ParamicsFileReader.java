package tmcsim.paramicscommunicator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tmcsim.common.CADProtocol.PARAMICS_ACTIONS;
import tmcsim.common.CADProtocol.PARAMICS_COMM_TAGS;
import tmcsim.paramicscommunicator.FileIOUpdate.IO_TYPE;


/**
 * The ParamicsFileReader handles reading fom a target file which
 * is written to by Paramics.  Once initialized, a timer task is
 * started which periodically checks the target file for updates.
 * If the file has been modified, it is read and cleared.  The read
 * data is transmitted to the CAD Simulator.
 *
 * @author Matthew Cechini
 * @version
 */
public class ParamicsFileReader extends Observable {
    
    /** Error Logger. */
    private Logger paramLogger = Logger.getLogger("tmcsim.paramicscommunicator");   
    
    /** FileReader ID used for creation of the ParamicsCommMessage */
    private String readerID = null;
    
    /** File reference to the file where data is read. */
    private File inputFile = null;
    
    /** FileReader used to read data from the input file. */
    private FileReader fileReader = null;
    
    /** FileWriter used to clear the input file. */
    private FileWriter fileWriter = null;
    
    /** Value (seconds since 1/1/1970) of input file's last modification time. */
    private long lastModified = 0;
    
    /** Timer used to schedule file reading tasks. */
    private Timer readerTimer = null;
    
    /** 
     * Duration (in seconds) that the TimerTask will be scheduled to
     * read from the target file.
     */
    private long readerInterval;
    
    /**
     * A TimerTask to read from the target file.  If the file has been modified since
     * last read, read the file and transmit the data to the CAD Simulator.
     * @author Matthew Cechini
     */
    private class ReaderTimerTask extends TimerTask {
        public void run() {     
            
            if (lastModified < inputFile.lastModified()) {
                                
                try {
                    Document readerDoc = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder().newDocument();
            
                    Element readerElem = readerDoc.createElement(PARAMICS_COMM_TAGS.READER.tag);
                    readerElem.setAttribute(PARAMICS_COMM_TAGS.ID.tag, readerID);
                    readerElem.setAttribute(PARAMICS_COMM_TAGS.ACTION.tag, 
                            PARAMICS_ACTIONS.READ_FILE.action);
                    
                    Element messageElem = readerDoc.createElement(PARAMICS_COMM_TAGS.MESSAGE.tag);
                    messageElem.appendChild(readerDoc.createTextNode(readFromFile()));
                    readerElem.appendChild(messageElem);
                    
                    readerDoc.appendChild(readerElem);          
                        
                    setChanged();
                    notifyObservers(readerDoc);
                }                   
                catch (Exception e) {
                    paramLogger.logp(Level.SEVERE, 
                            "ParamicsFileReader.ReaderTimerTask", "run()", 
                            "Exception in reading from file: " + 
                            inputFile.getName(), e);
                }
            }
        }
    }


    /**
     * Constructor.  Set the reader id and interval from the parsed
     * ParamicsCommMessage.  The interval is found within the first three
     * characters of the object's "message" data member.  Create a file
     * object for the target file, and create a new file if it does
     * not already exist.  Instantiate the timer and ReaderTimerTask
     * to begin periodic reading of the file.
     * 
     * @param workingDir Target working directory.
     * @param mess ParamicsCommMessage object containing registration data.
     * @param theComm Reference to the ParamicsCommunicator.
     */
    public ParamicsFileReader(String workingDir, String id, Integer interval, String targetFile) {
        
        try {       
            readerID       = id;
            readerInterval = interval;
                    
            inputFile = new File(workingDir + targetFile);  
                        
            if(!inputFile.exists()) {
                inputFile.createNewFile();
            }
            
            readerTimer = new Timer();
            readerTimer.scheduleAtFixedRate(new ReaderTimerTask(), 
                0L, readerInterval * 1000); 
            
        } catch (IOException ioe) {
            paramLogger.logp(Level.SEVERE, "ParamicsFileReader", 
                    "Constructor()", "Exception in initializing file reading.", ioe);
        }
    }
    
    
    /**
     * Method opens the target file and reads all contents.  The file is then 
     * cleared.
     *
     * @returns
     * @throws IOException if there is an error in reading or writing to the file.
     */
    private String readFromFile() throws IOException {
        
        char[] input = new char[(int)inputFile.length()];
        
        fileReader = new FileReader(inputFile);
        fileReader.read(input);     
        fileReader.close();
        
        //make sure the new file has a modified time that is different
        try {
            Thread.sleep(1000);
        }
        catch (Exception e) {}
        
        //clear file after reading contents
        fileWriter = new FileWriter(inputFile);
        fileWriter.write("");
        fileWriter.close();
        
        lastModified = inputFile.lastModified();
        
        setChanged();
        notifyObservers(new FileIOUpdate(IO_TYPE.READ, readerID, (long)input.length));      
                        
        return new String(input);
    }       
}