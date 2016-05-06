package system_tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.CannotRealizeException;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
//import javax.media.Player;
import javax.media.PlugInManager;
import javax.media.format.AudioFormat;
import javax.media.protocol.DataSource;
import junit.framework.TestCase;

/**
 *
 * @author Jonathan Molina
 */
public class SoundPlayingTest extends TestCase {

    public SoundPlayingTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testJMF() throws IOException, NoPlayerException, CannotRealizeException {
//        SimpleAudioPlayer player = new SimpleAudioPlayer(
//                new File("audio/181/music.mp3"));
//        player = new SimpleAudioPlayer();
//        
//        player.play();
//        System.out.println("here");
//        while(true);

        Format input1 = new AudioFormat(AudioFormat.MPEGLAYER3);
        Format input2 = new AudioFormat(AudioFormat.MPEG);
        Format output = new AudioFormat(AudioFormat.LINEAR);
        final URL resource = getClass().getResource("audio/181/music.mp3");
        PlugInManager.addPlugIn(
                "com.sun.media.codec.audio.mp3.JavaDecoder",
                new Format[]{input1, input2},
                new Format[]{output},
                PlugInManager.CODEC);
        try {
            File f = new File("audio/181/music.mp3");
//            DataSource ds = Manager.createDataSource(f.toURI().toURL());
            MediaLocator locator = new MediaLocator(f.toURI().toURL());
            javax.media.Player player = Manager.createPlayer(locator);
            player.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }

//        FileInputStream stream = new FileInputStream("audio/181/music.mp3");
//        try {
//            Player player = new Player(stream);
//            player.play();
//        } catch (JavaLayerException ex) {
//            ex.printStackTrace();
//        }
        System.out.println("here");
//
        while (true);
    }

    public static void main(String[] args) throws InterruptedException {
        Format input1 = new AudioFormat(AudioFormat.MPEGLAYER3);
        Format input2 = new AudioFormat(AudioFormat.MPEG);
        Format output = new AudioFormat(AudioFormat.LINEAR);
        javax.media.Player player = null;
        PlugInManager.addPlugIn(
                "com.sun.media.codec.audio.mp3.JavaDecoder",
                new Format[]{input1, input2},
                new Format[]{output},
                PlugInManager.CODEC);
        try {
            File f = new File("audio/181/18101.mp3");
            MediaLocator locator = new MediaLocator(f.toURI().toURL());
            player = Manager.createPlayer(locator);
            player.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        
        Thread.sleep(5000);
        player.stop();
    }

    private class SimpleAudioPlayer {

        private javax.media.Player audioPlayer = null;

        public SimpleAudioPlayer() {
        }

        public SimpleAudioPlayer(URL url) throws IOException, NoPlayerException,
                CannotRealizeException {
            audioPlayer = Manager.createRealizedPlayer(url);
        }

        public SimpleAudioPlayer(File file) throws IOException, NoPlayerException,
                CannotRealizeException {
            this(file.toURL());
        }

        public void play() {
            audioPlayer.start();
        }

        public void stop() {
            audioPlayer.stop();
            audioPlayer.close();
        }
    }
}
