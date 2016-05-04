package tmcsim.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class KeyboardTester extends JFrame implements KeyListener {

    public KeyboardTester() {
        JTextArea ta = new JTextArea();
        ta.addKeyListener(this);
        
        add(ta);
        
        pack();
        setSize(200,00);
        setVisible(true);
    }
    
    public void keyTyped(KeyEvent evt) {
        System.out.println(evt.getKeyCode() + " typed");
    }

    public void keyPressed(KeyEvent evt) {
        System.out.println(evt.getKeyCode() + " pressed");
    }

    public void keyReleased(KeyEvent evt) {
        System.out.println(evt.getKeyCode() + " released");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new KeyboardTester();
    }

}
