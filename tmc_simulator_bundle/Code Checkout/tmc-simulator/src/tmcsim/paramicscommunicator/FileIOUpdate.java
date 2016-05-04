package tmcsim.paramicscommunicator;

/**
 * FileIOUpdate is an update class used to record I/O operations 
 * performed by FileReaders and FileWriters registered with the
 * ParamicsCommunicator.
 *  
 * @author Matthew Cechini
 * @version
 */
public class FileIOUpdate {

    /**
     * Enumeration of I/O action types.
     * @author Matthew Cechini
     */
    public static enum IO_TYPE {READ, WRITE};
    
    /** Type of I/O performed. */
    public IO_TYPE ioType;

    /** ID of FileReader or FileWriter. */
    public String  ioID;

    /** Number of bytes written to or read from the target file. */
    public Long    ioBytes;
    
    /**
     * Constructor.
     * 
     * @param type I/O type.
     * @param id   I/O object id.
     * @param bytes Number of bytes for I/O action.
     */
    public FileIOUpdate(IO_TYPE type, String id, Long bytes) {
        ioType       = type;
        ioID         = id;
        ioBytes      = bytes;
    }
}
