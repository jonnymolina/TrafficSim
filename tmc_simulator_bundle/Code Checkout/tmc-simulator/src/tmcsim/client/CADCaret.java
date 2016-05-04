package tmcsim.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

/**
 * CADCaret extends from DefaultCaret to provide a blinking Caret.  The Caret's
 * blink rate is 500ms and appears as a solid underline that is the width of one 
 * character.  All superclass methods that position the Caret are overloaded
 * to control the position of the Caret manually.
 * 
 * @author Matthew
 * @version
 */
@SuppressWarnings("serial")
public class CADCaret extends DefaultCaret {

    /** Caret's current position.  (>= 0) */
    private int currentCaretPos = 0;

    /** 
     * Constructor.
     */
    public CADCaret() {
        setBlinkRate(500); // half a second
        setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    }
    
    /**
     * Overloaded method.  The Caret's position is set with the internal
     * position count. 
     */
    public void setMagicCaretPosition(Point arg0) {
        super.setDot(currentCaretPos);
    }
    

    /**
     * The internal Caret position is incremented by the parameter offset
     * value.  After position adjustment, the Caret is repositioned.
     * 
     * @param offset Number of characters to increment the Caret's position by. 
     */
    public void moveCaretForward(int offset) {
        currentCaretPos += offset;
        super.setDot(currentCaretPos);
    }
    
    /**
     * The internal Caret position is decremented by the parameter offset
     * value.  If this resulting position is negative, the position is 
     * set to 0.  After position adjustment, the Caret is repositioned.
     * 
     * @param offset Number of characters to decrement the Caret's position by. 
     */
    public void moveCaretBackward(int offset) {
        currentCaretPos -= offset;      
        
        if(currentCaretPos < 0)
            currentCaretPos = 0;
        
        super.setDot(currentCaretPos);
    }
    
    /**
     * The internal Caret position is reset to 0 and the Caret is repositioned.
     */
    public void resetCaretPosition() {
        currentCaretPos = 0;
        super.setDot(currentCaretPos);      
    }

    @Override
    public void setVisible(boolean visible) 
    {
        if(visible)
        {
            getComponent().setCaretColor(Color.black);
        }
        else
        {
            getComponent().setCaretColor(Color.yellow);
        }
    }
    
    public void mouseClicked(MouseEvent arg0)     { }
    public void mouseDragged(MouseEvent arg0)     { }
    public void mouseEntered(MouseEvent arg0)     { }
    public void mouseExited(MouseEvent arg0)      { }
    public void mouseMoved(MouseEvent arg0)       { }
    public void mousePressed(MouseEvent arg0)     { }   
    public void mouseReleased(MouseEvent arg0)    { }
    protected void moveCaret(MouseEvent arg0)     { }
    public void moveDot(int arg0)                 { }
    public void setDot(int arg0)                  { }    
    protected void positionCaret(MouseEvent arg0) { }   
    public void focusLost(FocusEvent arg0)        { }
    
    protected synchronized void damage(Rectangle r) 
    {
        if (r == null)
            return;
        // give values to x,y,width,height (inherited from
        // java.awt.Rectangle)
        x = r.x;
        y = r.y + (r.height * 4 / 5 - 3);
        width  = 10;
        height = 5;
        repaint(); // calls getComponent().repaint(x, y, width, height)
    }

    public void paint(Graphics g) {
        JTextComponent comp = getComponent();
        if (comp == null)
            return;

        int dot = getDot();
        Rectangle r = null;
        try {
            r = comp.modelToView(dot);
        } catch (BadLocationException e) {
            return;
        }
        if (r == null)
            return;

        // will be distance from r.y to top
        int dist = r.height * 4 / 5 - 3; 

        if ((x != r.x) || (y != r.y + dist)) {
            // paint() has been called directly, without a previous call to
            // damage(), so do some cleanup. (This happens, for example,
            // when
            // the
            // text component is resized.)
            repaint(); // erase previous location of caret
            x = r.x; // set new values for x,y,width,height
            y = r.y + dist;
            width  = 10;
            height = 5;
        }

        if (isVisible()) {
            g.setColor(comp.getCaretColor());
            // 5 vertical pixels
            //g.drawLine(r.x, r.y + dist, r.x, r.y + dist + 4);
            
            // 5 horizontal pixels
            g.drawLine(r.x, r.y + dist + 4, r.x + 8, r.y + dist + 4); 
        }
    }
    
}
