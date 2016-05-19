package unit_tests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import junit.framework.TestCase;
import tmcsim.paramicslog.ParamicsLog;

/**
 * Unit test class for ParamicsLog.
 * @author Jonathan Molina
 */
public class ParamicsLogTest extends TestCase
{
    private boolean beforeClass = true;
    
    @Override
    public void setUp() throws FileNotFoundException
    {
        if (beforeClass)
        {
            PrintWriter pWriter = new PrintWriter("paramics_log_file.properties");
            pWriter.print(
                "LogFile = paramics_log.txt\n" +
                "CADSimulatorHost = 127.0.0.1\n" +
                "CADSimulatorRMIPort = 4445\n");
            pWriter.close();
            beforeClass = false;
            System.setProperty("PARAMICS_LOG_PROPERTIES", "paramics_log_file.properties");
            ParamicsLog.getInstance();
        }
    }

    /**
     * Test of writeToLog method, of class ParamicsLog.
     */
    public void testWriteToLog()
    {
        ParamicsLog.getInstance().writeToLog("Logging message 1");
        assertEquals("\n<!-- Time written to file: 00:00:00 -->\nLogging message 1\n", ParamicsLog.getInstance().getLog());
        ParamicsLog.getInstance().writeToLog("Logging message 2");
        assertEquals(
                "\n<!-- Time written to file: 00:00:00 -->\nLogging message 1\n" +
                "\n<!-- Time written to file: 00:00:00 -->\nLogging message 2\n", 
                ParamicsLog.getInstance().getLog());
        
    }
}
