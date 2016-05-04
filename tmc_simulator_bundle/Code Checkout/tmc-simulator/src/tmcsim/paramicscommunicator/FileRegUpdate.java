package tmcsim.paramicscommunicator;

import tmcsim.paramicscommunicator.FileIOUpdate.IO_TYPE;

/**
 * FileRegUpdate is an update class used to record registration operations 
 * performed by the ParamicsCommunicator.
 *  
 * @author Matthew Cechini
 * @version
 */
public class FileRegUpdate {

    /**
     * Enumeration of registration actions.
     * @author Matthew Cechini
     */
    public static enum REG_TYPE { REGISTER, UNREGISTER };
    
    /** I/O action type performed by this Paramics FileReader or FileWriter */
    public IO_TYPE  ioType;

    /** Registration action performed. */
    public REG_TYPE regType;

    /** ID of FileReader or FileWriter. */
    public String   ioID;

    /** Target file to read from or write to. */
    public String   targetFile;

    /** Interval (in seconds) that the I/O action is performed. */
    public Integer  ioInterval;
    
    /**
     * Constructor.
     * 
     * @param io I/O action type.
     * @param reg Registration action type.
     * @param id   I/O object id.
     * @param file Target file for I/O operation.
     * @param interval Interval for I/O action.
     */
    public FileRegUpdate(IO_TYPE io, REG_TYPE reg, String id, String file, Integer interval) {
        ioType  = io;
        ioID    = id;
        regType = reg;
        targetFile = file;
        ioInterval = interval;
    }
    
}
