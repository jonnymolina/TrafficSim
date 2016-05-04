package tmcsim.cadsimulator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.Manager;
import javax.media.Player;
import java.net.URL;
import javax.media.CannotRealizeException;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.PlugInManager;
import javax.media.format.AudioFormat;

import tmcsim.client.cadclientgui.data.IncidentEvent;

/**
 * SoundPlayer is used to play audio files associated with IncidentEvents that 
 * occur during a simulation.  When the SoundPlayer has been enabled audio 
 * clips are enqueued by calling the enqueueClip() method.  The audio files 
 * are played in order of receipt and when finished, the IncidentEvent is 
 * notified.  Audio playing may be disabled or enabled through the 
 * setAudioEnabled() method.  Disabling audio will cause a currently
 * playing audio clip to stop and be requeued at the front of the queue.  
 * Re-enabling the audio will then continue playing all queued clips.  The
 * deQueueAll() method is used to clear the list of audio clips that have 
 * been queued.
 *
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/06 20:46:41 $ $Revision: 1.3 $
 */
public class SoundPlayer extends Thread {
    
    /** Error Logger. */
    private Logger soundLogger = Logger.getLogger("tmcsim.cadsimulator");
                                
    /** The base location that wav files are referenced from */             
    private String baseAudioDir = "";
                                
    /** The private vector of audioClips used during a simulation */
    private Vector<AudioClipInfo> enqueuedClips = null;
    
    /** The audio clip that is currently being played by the AudioPlayer. */
    private AudioClipInfo currentClip = null;
    
    /** The player to play audio files. */
    private AudioPlayerJMF player;
    
    /**
     * Flag to designate when an audio file is playing. This prevents 
     * multiple audio files from being played simultaneously. 
     * Flag is initialized to false.
     */
    private boolean audioPlaying = false;   
    
    /** Flag to designate whether audio playing is enabled.  If audio is disabled,
     *  then enquque events will be ignored.  Flag is initialized to false.
     */
    private boolean audioEnabled = false;
    
    /** Timer used to pause audio player during an audio file. */
    private Timer timer = null;
    
    /** Inner class used to contain the audio file's name and duration. */
    private class AudioClipInfo {
        String fileName;
        int duration;
        IncidentEvent theEvent;
        
        AudioClipInfo(IncidentEvent ie) {
            fileName = ie.waveFile;
            duration = ie.waveLength;   
            theEvent = ie;
        }       
                
        AudioClipInfo(String name, int dur) {
            fileName = name;
            duration = dur; 
            theEvent = null;
        }   
        
        public void wavePlayed() {
            if(theEvent != null)
                theEvent.wavePlayed();
        }   
    }
    
    /**
     * Inner class used to play the audio files. Uses the Java Media Framework in place
     * of the previous sun API for audio files.
     * @author Jonathan Molina
     */
    private class AudioPlayerJMF {
        private Player audioPlayer = null;
        private boolean isPlaying;

        /**
         * Constructs an instance of this class but does not initialize the audio
         * player with a specific sound file. In other words, using the start() method
         * without preemptively calling the useFile() method will throw a
         * NullPointerException.
         */
        public AudioPlayerJMF() {
            isPlaying = false;
        }
        
        /**
         * Will use the given audio file to be played.
         * @param file the audio file
         * @throws MalformedURLException improper file to URL conversion
         * @throws IOException 
         * @throws NoPlayerException a javax.media.Player is not found within the class
         * @throws CannotRealizeException unable to determine or acquire the resources
         * necessary to play the audio file
         */
        public void useAudioFile(File file) throws MalformedURLException, IOException, 
                NoPlayerException, CannotRealizeException {
            String extension = file.getName().substring(file.getName().length() - 3);
            if (extension.equals("wav")) {
                audioPlayer = Manager.createRealizedPlayer(file.toURL());
            } else if (extension.equals("mp3")) {
                Format input1 = new AudioFormat(AudioFormat.MPEGLAYER3);
                Format input2 = new AudioFormat(AudioFormat.MPEG);
                Format output = new AudioFormat(AudioFormat.LINEAR);
                PlugInManager.addPlugIn(
                        "com.sun.media.codec.audio.mp3.JavaDecoder",
                        new Format[]{input1, input2},
                        new Format[]{output},
                        PlugInManager.CODEC);
                MediaLocator locator = new MediaLocator(file.toURI().toURL());
                audioPlayer = Manager.createPlayer(locator);
            }   
        }

        /**
         * Starts playing the audio file.
         * @throws NullPointerException the method useAudioFile() was not called before
         * this method or that method threw an error upon use
         */
        public void start() throws NullPointerException {
            isPlaying = true;
            audioPlayer.start();
        }

        /**
         * Stops playing the audio file. This is able to be called even if start() was
         * not called prior, which does nothing.
         */
        public void stop() {
            if (isPlaying) {
                isPlaying = false;
                audioPlayer.stop();
                audioPlayer.close();
            }
        }
    }
 
    /**
     * Constructor.  Establish the base file path for wav file referencing.
     *
     * @param wavFilePath Pathname for where the simulation wav files will be referenced.
     */
    public SoundPlayer(String baseFilePath) {
        
        baseAudioDir  = baseFilePath;
        audioPlaying  = true;
        audioEnabled  = true;
        timer         = new Timer();                        
        enqueuedClips = new Vector<AudioClipInfo>();
        player = new AudioPlayerJMF();
    }

    /**
     * Add a audioClipInfo object to the audioClips Vector.  The enqueued audio file will play
     * when all previously enqueued audio files have been played.  If audio is not enabled, the
     * clip will not be queued, and the incident event will be notified that the audio clip 
     * has finished playing to allow the simulation to continue.
     *
     * @param ie The Incident Event to enqueue to the current list of events.
     */
    public void enqueueClip (IncidentEvent ie) {

        if(audioEnabled) {
            synchronized(enqueuedClips)  {
                enqueuedClips.add(new AudioClipInfo(ie));
            }
        }
        else {
            ie.wavePlayed();
        }
    } 
    
    /**
     * Method called when user presses reset in simulation.  All queued events need
     * to be deQueued.
     */
    public void deQueueAll() {
        synchronized(enqueuedClips) {
            enqueuedClips.clear();
        }       
    }  
    
    /** 
     * Get the current audio enabled status.
     *
     * @return True if enabled, false if disabled.
     */
    public boolean getAudioEnabled() {
        return audioEnabled;
    }   
    
    /**
     * Set the current audio enabled status.  A false value will cause all
     * enqueue events to be ignored.  A currently playing audio file will be 
     * stopped and requeued.  All other queued events will remain in the queue.
     *
     * @param enable True if enabling, false if disabling.
     */
    public void setAudioEnabled(boolean enable) {
        
        if(!enable) {           
            timer.cancel();
            player.stop();
            
            if(currentClip != null) {
                enqueuedClips.add(0, currentClip);
            }           
            
            audioPlaying = false;
        }
        
        audioEnabled = enable;
    }
    
        
    /**
     * Method declaration for the Thread.run() method.  While this thread is not 
     * interrupted, check if there are enqueued audio files.  If so, remove the 
     * first one(FIFO) create an AudioClip object, and play the audio file.
     * A timer tis then started for the duration of the audio file.  When the
     * timer expires the audioPlaying flag is reset to false and the corresponding
     * incident event is notified of the audio file's completion.
     */
    public void run() {
        AudioClipInfo theClip = null;
        
        while(!isInterrupted()) {
            
            synchronized(enqueuedClips) {
                if(enqueuedClips.size() > 0 && !audioPlaying && audioEnabled)
                    theClip = enqueuedClips.remove(0);                  
            }
                
            //if nothing to play, wait a second
            if(theClip == null) {
                try { Thread.sleep(1000);} catch (Exception e) {}
            }
            else {
                        
                try{
                    
                    if(theClip.duration > 0) {              
                        
                        File audioFile = new File(baseAudioDir + theClip.fileName);
                        
                        if(audioFile.exists()) {
                            player.useAudioFile(audioFile);
                            player.start();
                        }
                        else {
                            throw new Exception();
                        } 
                    }
                    
                    currentClip  = theClip;                                             
                    audioPlaying = true;
                    
                    timer = new Timer();
                    timer.schedule(new TimerTask () {
                        public void run() {
                            clipPlayed();
                            audioPlaying = false;                       
                        }
                    }, theClip.duration * 1000);
                    
                    theClip = null;                     
                } 
                catch (Exception e) {
                    soundLogger.logp(Level.WARNING, "SoundPlayer", "run", 
                            "Unable to play audio file: " + baseAudioDir + theClip.fileName, e);
    
                    theClip.theEvent.wavePlayed();
                    theClip = null;
                    timer.cancel();
                }   
            }                               
        }                       
    }
    
        
    /**
     * Called when audio file finishes playing.  Notifies the corresponding 
     * Incident Event that its audio file has finished playing.
     */
    private void clipPlayed() {
        currentClip.wavePlayed();
    }  
}
