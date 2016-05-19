package tmcsim.cadsimulator.stillimagecontrol;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * ATMSCommunicator handles communication between the CAD Simulator and the 
 * ATMS Server.  The funcationality provided includes querying the current time 
 * on the ATMS server and replacing images on the ATMS to model 
 * traffic flow changes.  The ATMSCommunicator uses the plink.exe external 
 * application to establish an SSH communication with the ATMS server, which is
 * used to execute commands remotely.  
 * 
 * @author Matthew Cechini
 * @version
 */
public class ATMSCommunicator {
    
    /** Connection user name. */
    protected String username;
    
    /** Connection password. */
    protected String password;
    
    /** ATMS Server Host name. */
    protected String viewerHost;
    
    /** Absolute directory path for images. */
    protected String image_dir;
    
    /** Base plink command string. */
    protected String plinkBaseCMD;
    
    /** Constructor. */
    public ATMSCommunicator(String host, String user, String pwd, String dir) {
        viewerHost = host;
        username   = user;
        password   = pwd;
        image_dir  = dir;
        if (System.getProperty("os.name") != null
                && System.getProperty("os.name").startsWith("Windows"))
        {
            plinkBaseCMD = "plink -l " + username + " -pw " + password 
                    + " " + viewerHost + " ";
        }
        else
        {
            plinkBaseCMD = "date " + username + " -pw " + password 
                    + " " + viewerHost + " ";
        }
    }
    
    /**
     * Get the current ATMS server time as the number of seconds since Jan 1, 1970.
     * 
     * @return Current time in seconds.
     * @throws RemoteException If there is an exception in RMI communication.
     */ 
    public long getCurrentTime() throws Exception {
        
        Calendar currentCal = Calendar.getInstance();

        Process timeProc = Runtime.getRuntime().exec(plinkBaseCMD + " \"date\"");
        timeProc.waitFor();
        
        if(timeProc.exitValue() == 0) {
            String tempToken = null;
            byte[] dateBytes = new byte[timeProc.getInputStream().available()];
            timeProc.getInputStream().read(dateBytes);
            
            StringTokenizer spaceTok = new StringTokenizer(new String(dateBytes), " ");
            while(spaceTok.hasMoreTokens()) {
                tempToken = spaceTok.nextToken();
                
                if(tempToken.indexOf(":") != -1) {
                    StringTokenizer colonTok = new StringTokenizer(new String(tempToken), ":");
                    
                    currentCal.set(Calendar.HOUR, Integer.parseInt(colonTok.nextToken()));
                    currentCal.set(Calendar.MINUTE, Integer.parseInt(colonTok.nextToken()));
                    currentCal.set(Calendar.SECOND, Integer.parseInt(colonTok.nextToken()));
                    
                    System.out.println("Time retreieved from ATRMS server: " + 
                            DateFormat.getDateTimeInstance().format(currentCal.getTime()));                                 
                    
                }
            }
        }       
        
        return currentCal.getTimeInMillis();        

    }

    /**
     * Show a new image for an ATMS camera.  The ATMS camera files are named
     * <ATMS_Camera_ID>.xpm.  If a camera file exists, delete it.  Then copy
     * the parameter file name to <ATMS_Camera_ID>.xpm.  If the camera ID or 
     * new file does not exist, throw an exception.     
     * 
     * @param ATMS_cameraID ATMS indexed camera ID.
     * @param fileName Filename to show.
     * @throws RemoteException If there is an exception in RMI communication.
     */ 
    public void showImage(Integer ATMS_cameraID, String fileName) throws RemoteException {
        
        System.out.println("Showing CameraID " + ATMS_cameraID + ", with filename: " + fileName);
        /*
        Process imageProc = Runtime.getRuntime().exec(plinkBaseCMD + "\"ls " + image_dir + "/cctvImage" + ATMS_cameraID + ".xpm\"");
        imageProc.waitFor();
        
        if(imageProc.exitValue() != 0) {
            throw new Exception(image_dir + "/cctvImage" + ATMS_cameraID + ".xpm does not exist");
        }
            
        imageProc = Runtime.getRuntime().exec(plinkBaseCMD + "\"ls " + image_dir + "/" + fileName + ".xpm\"");
        imageProc.waitFor();

        if(imageProc.exitValue() != 0) {
            throw new Exception(image_dir + "/" + fileName + ".xpm does not exist");
        }
    
        imageProc = Runtime.getRuntime().exec(plinkBaseCMD + "\"rm " + image_dir + "/cctvImage" + ATMS_cameraID + ".xpm\"");
        imageProc.waitFor();
        
        imageProc = Runtime.getRuntime().exec(plinkBaseCMD + "\"cp " + image_dir + "/" + fileName + ".xpm" + 
                                  "   " + image_dir + "/" + "cctvImage" + ATMS_cameraID + ".xpm\"");        
        imageProc.waitFor();
        */
        
    }   
}
