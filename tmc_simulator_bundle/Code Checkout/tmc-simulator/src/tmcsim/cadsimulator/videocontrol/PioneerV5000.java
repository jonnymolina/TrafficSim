package tmcsim.cadsimulator.videocontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * DVDController extends from the Abstraact DVDController class to provide funtionality
 * for controlling the Pioneer V5000 DVD Controller.
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/15 19:08:15 $ $Revision: 1.5 $
 */
public class PioneerV5000 extends DVDController
{
    /**
     * Enumeration of command strings used for DVD control.
     *
     * @author Matthew Cechini
     */
    protected static enum COMMAND_CHARS
    {
        OPEN("OP"),
        CLOSE("CO"),
        START("SA"),
        SEARCH("SE"),
        PLAY("PL"),
        SEARCH_PLAY("SL"),
        PAUSE("PA"),
        STILL("ST"),
        REJECT("RJ"),
        SCAN_FWD("NF"),
        SCAN_RVS("NR"),
        SCAN_STOP("NS"),
        TITLE("TI"),
        CHAPTER("CH"),
        GROUP("GP"),
        CMD_STACK_PL("BS"),
        UPLOAD_DATA("BD"), //Data from computer to player.
        DOWNLOAD_DATA("BU"), //Data from player to computer.
        SETUP("MS");
        private String cmd;

        private COMMAND_CHARS(String c)
        {
            cmd = c;
        }
    }
    /**
     * Maximum number of command retries =5.
     */
    protected final int kMaxRetries = 5;
    /**
     * Maximum command length = 32.
     */
    protected final int kMaxCommandLen = 32;
    /**
     * Maximum response length = 32.
     */
    protected final int kMaxResponseLen = 32;
    /**
     * Carriage return character.
     */
    protected final char kCR = 0x0d;
    /**
     * Buffer for creating command string.
     */
    protected StringBuffer commandString;
    /**
     * Buffer to receive repsonse string.
     */
    protected StringBuffer responseString;
    /**
     * DVD Player host.
     */
    protected String dvdHost = null;
    /**
     * DVD Player port.
     */
    protected int dvdPort = 0;
    /**
     * Socket used for communication with DVD player.
     */
    protected Socket serialInterfaceSocket = null;
    /**
     * IOStream for writing out commands.
     */
    //protected ObjectOutputStream out       = null;
    protected OutputStream out = null;
    /**
     * IOStream for reading dvd player responses.
     */
    protected BufferedReader in = null;

    /**
     * Constructor.
     */
    public PioneerV5000()
    {
        commandString = new StringBuffer(kMaxCommandLen);
        responseString = new StringBuffer(kMaxResponseLen);
    }

    /**
     * Sets the host and port connection information for this dvd player.
     *
     * @param host DVD player host string.
     * @param port DVD player port value.
     */
    public void setConnectionInfo(String host, int port)
    {
        dvdHost = host;
        dvdPort = port;
    }

    /**
     * Get the current connection information for this dvd player. The returned string is
     * as follows: HOST:PORT
     *
     * @return DVD connection host:port
     */
    public String getConnectionInfo()
    {
        return dvdHost + ":" + dvdPort;
    }

    /**
     * Checks if connected.
     * @return true if connected, false if not
     */
    public boolean isConnected()
    {
        return isConnected;
    }

    /**
     * Establish a connection with the dvd player through socket communication. If the
     * port is successfully opened, input and ouput streams are opened on the socket. The
     * isConnected boolean flag is set to true if this is successful. Observers are
     * notified with a DVDStatusUpdate to show the new connection.
     *
     * @throws IOException if communication with the dvd player fails.
     */
    public void connect() throws IOException
    {

        serialInterfaceSocket = new Socket();
        serialInterfaceSocket.connect(new InetSocketAddress(dvdHost, dvdPort), 5000);
        serialInterfaceSocket.setSoTimeout(5000);

        //out = new ObjectOutputStream(serialInterfaceSocket.getOutputStream());
        out = serialInterfaceSocket.getOutputStream();
        in = new BufferedReader(
                new InputStreamReader(serialInterfaceSocket.getInputStream()));

        isConnected = true;

        setChanged();
        notifyObservers(new DVDStatusUpdate(getConnectionInfo(),
                isConnected()));
    }

    /**
     * Disconnect from the dvd player. Close all IOStreams and socket, then set the
     * isConnected flag to false. Observers are notified with a DVDStatusUpdate to show
     * the loss of the connection.
     */
    public void disconnect()
    {

        try
        {
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            serialInterfaceSocket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        isConnected = false;

        setChanged();
        notifyObservers(new DVDStatusUpdate(getConnectionInfo(),
                isConnected()));

    }

    /**
     * If the command is sent while the player is in the Park mode, the dvd
     * ejects and the player enters the Open mode. After the tray is ejected, 
     * player returns a completed status message. 
     *
     * If the player is in any mode other than Open or Park, the disc stops, the 
     * player enters Open mode and the door opens.
     *
     * If the player is already in Open mode, an error message is returned.
     * @throws IOException if communication with the dvd player fails.
     */
    public void open() throws IOException
    {
        commandString.append(COMMAND_CHARS.OPEN.cmd);
        transmit();
    }

    /**
     * If the command is sent while the player door is open, the door 
     * closes then the player enters the Park mode. After the door closes, the 
     * player returns the completed status message.
     *
     * If the player is in any mode other than Open or if the player door is already 
     * closed, an error message is returned.
     * @throws IOException if communication with the dvd player fails.
     */
    public void close() throws IOException
    {
        commandString.append(COMMAND_CHARS.CLOSE.cmd);
        transmit();
    }

    /**
     * If the command is sent while the player is in Open, Park or Reject 
     * mode, the player immediately enters Setup and the disc begins spinning up.
     * The player is ready for playback when the device reaches the beginning of
     * the program (DVD, CD or VCD disc pauses or stills at the first Track). The 
     * player returns the completed status when the disc pauses or stills. 
     *
     * If the player receives the command while playing a menu, the player returns 
     * an error message. However, if the disc program does not allow new 
     * commands once playback begins, the player ignores the command.
     * 
     * @throws IOException if communication with the dvd player fails.
     */
    public void start() throws IOException
    {
        commandString.append(COMMAND_CHARS.START.cmd);
        transmit();
    }

    /**
     * Play.
     * @throws IOException if communication with the dvd player fails.
     */
    public void play() throws IOException
    {
        commandString.append(COMMAND_CHARS.PLAY.cmd);
        transmit();
    }

    /**
     * Play the chapter
     * @param chapter the chapter
     * @throws IOException if communication with the dvd player fails.
     */
    public void playChapter(int chapter) throws IOException
    {
        commandString.append(COMMAND_CHARS.CHAPTER.cmd + chapter);
        commandString.append(COMMAND_CHARS.SEARCH_PLAY.cmd);
        transmit();
    }

    /**
     * Play the title
     * @param title the title
     * @throws IOException if communication with the dvd player fails.
     */
    public void playTitle(int title) throws IOException
    {
        commandString.append(COMMAND_CHARS.TITLE.cmd + title);
        commandString.append(COMMAND_CHARS.SEARCH_PLAY.cmd);
        transmit();

    }

    /**
     * Repeat the title
     * @param title the title
     * @throws IOException if communication with the dvd player fails.
     */
    public void repeatTitle(int title) throws IOException
    {
        commandString.append(title + COMMAND_CHARS.GROUP.cmd);
        commandString.append("1" + COMMAND_CHARS.CMD_STACK_PL.cmd);

        transmit();
    }

    /**
     * If the command is sent while the player is in Random Access 
     * mode, the pause occurs at the current disc location. The player returns the
     * completed status message immediately.
     *
     * In Pause mode, Still and Video Squelch are ACTIVE. However, if the disc
     * program does not allow a pause, the player ignores the command and
     * returns an error message (E04).
     * @throws IOException if communication with the dvd player fails.
     */
    public void pause() throws IOException
    {
        commandString.append(COMMAND_CHARS.PAUSE.cmd);
        transmit();
    }

    /**
     * If the command is sent while the player is in Random Access 
     * mode, playback stops at the current disc position and the player enters Still 
     * mode. The player returns the completed status message immediately. 
     *
     * However, if the disc program does not allow a pause, the player ignores the 
     * command and returns an error message (E04).
     * @throws IOException if communication with the dvd player fails.
     */
    public void still() throws IOException
    {
        commandString.append(COMMAND_CHARS.STILL.cmd);
        transmit();
    }

    /**
     * Step forward.
     * @throws IOException if communication with the dvd player fails.
     */
    public void stepForward() throws IOException
    {
    }

    /**
     * Step reverse.
     * @throws IOException if communication with the dvd player fails.
     */
    public void stepReverse() throws IOException
    {
    }

    /**
     * If the command is sent while the player is in Random Access 
     * mode, the screen proceeds forward (NF) or in reverse (NR) quickly. When 
     * scanning is finished, the player resumes the Random Access mode and 
     * returns the completed status message.
     *
     * If the SCAN command is sent while the player is in Fast Forward or Reverse 
     * Playback, the player enters Scan mode.
     *
     * Once the NS command is sent, the player resets to the normal 
     * Playback mode and returns the completed status message.
     * @throws IOException if communication with the dvd player fails.
     */
    public void scanForward() throws IOException
    {
        commandString.append(COMMAND_CHARS.SCAN_FWD.cmd);
        transmit();
    }

    /**
     * Scan reverse.
     * @throws IOException if communication with the dvd player fails.
     */
    public void scanReverse() throws IOException
    {
        commandString.append(COMMAND_CHARS.SCAN_RVS.cmd);
        transmit();
    }

    /**
     * Scan stop.
     * @throws IOException if communication with the dvd player fails.
     */
    public void scanStop() throws IOException
    {
        commandString.append(COMMAND_CHARS.SCAN_STOP.cmd);
        transmit();
    }

    /**
     * Upload.
     * @param cmdStackthe command stack
     * @throws IOException if communication with the dvd player fails.
     */
    public void upload(String cmdStack) throws IOException
    {
        //park mode
        //out.write((COMMAND_CHARS.REJECT.cmd + kCR).toString().getBytes());

        //try { Thread.sleep(500); } catch (Exception e) {}

        out.write((COMMAND_CHARS.UPLOAD_DATA.cmd + kCR).toString().getBytes());
        out.flush();

        try
        {
            Thread.sleep(500);
        }
        catch (Exception e)
        {
        }

        out.write((cmdStack + kCR).toString().getBytes());
        out.flush();


    }

    /**
     * Download.
     * @return the response
     * @throws Exception if communication with the dvd player fails
     */
    public String download() throws Exception
    {
        //park mode
        commandString.append(COMMAND_CHARS.REJECT.cmd + kCR);
        transmit();

        commandString.append(COMMAND_CHARS.DOWNLOAD_DATA.cmd + kCR);

        //send carriage return to refresh the connection
        //out.writeChar(kCR);
        out.write(kCR);
        out.flush();

        //clear response buffer
        while (in.ready())
        {
            responseString.append(in.readLine());
        }
        responseString.setLength(0);

        //send the command
        //out.writeBytes(commandString.toString());
        out.write(commandString.toString().getBytes());
        out.flush();

        //read in response
        while (in.ready())
        {
            responseString.append(in.readLine());
        }

        // check for errors
        if (responseString.indexOf("E") == 0)
        {
            throw new Exception("Error in downloading from player.");
        }
        else
        {
            return responseString.toString();
        }

    }

    /**
     * This method spawns a thread to transmit the current command found in the
     * commandString buffer to the DVD player. If the output stream is null, observers
     * are notified with a DVDStatusUpdate containing the exception. The following steps
     * are taken to transmit the command: <br>
     *
     * <nl>
     * <li>A Carriage Return (kCR) character is sent to refresh the connection.</li>
     * <li>All response characters on the buffer are cleared.</li>
     * <li>The command is transmitted with a kCR to signify the end of the command.</li>
     * <li>If the DVD response contains an 'E', then the command was not successful,
     * repeat steps 1 - 3. Otherwise, the command was successful, the thread ends.</li>
     * <li>If the retry command transmit fails, notify observers with a DVDStatusUpdate
     * containing the exception.</li>
     * <nl>
     */
    protected synchronized void transmit()
    {

        Runnable transmitRun = new Runnable()
        {
            /**
             * Run the transmit run.
             */
            public void run()
            {

                int retries = kMaxRetries;

                try
                {
                    // check for errors
                    if (out == null)
                    {
                        throw new Exception("Cannot transmit to DVD "
                                + dvdHost + ":" + dvdPort + ". Reconnect to DVD.");
                    }
                    // retry
                    while (retries > 0)
                    {
                        System.out.println("sending [" + commandString.toString()
                                + "] to " + getConnectionInfo());

                        //send carriage return to refresh the connection
                        out.write(kCR);
                        out.flush();

                        //Wait for DVD to respond
                        Thread.sleep(500);

                        //clear response buffer
                        while (in.ready())
                        {
                            responseString.append(in.readLine());
                        }
                        responseString.setLength(0);

                        //send the command
                        out.write(commandString.toString().getBytes());
                        out.write(kCR);
                        out.flush();

                        //Wait for DVD to respond
                        Thread.sleep(500);

                        //read in response
                        while (in.ready())
                        {
                            responseString.append(in.readLine());
                        }

                        //if an error occured, retry
                        if ((responseString.indexOf("E") > -1))
                        {
                            System.out.println("response: " + responseString.toString());
                            retries--;
                        }
                        else
                        {
                            retries = 0;
                            System.out.println("response: " + responseString.toString());
                        }
                    }
                }
                catch (Exception e)
                {
                    setChanged();
                    notifyObservers(new DVDStatusUpdate(getConnectionInfo(),
                            isConnected(), e));
                }
                finally
                {
                    commandString.setLength(0);
                    responseString.setLength(0);
                }
            }
        };

        Thread transmitThread = new Thread(transmitRun);
        transmitThread.start();

    }
}