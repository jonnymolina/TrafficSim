package tmcsim.simulationmanager.model;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;


/**
 * UpperCaseField extends from JTextField to provide a text field that only
 * displays upper case characters.
 * 
 * @author Matthew Cechini
 * @version 
 */
@SuppressWarnings("serial")
public class UpperCaseField extends JTextField {

    /**
     * Constructor.
     * @param cols Number of columns for the text field.
     */ 
    public UpperCaseField(int cols) {
        super(cols);
    }

    protected Document createDefaultModel() {
          return new UpperCaseDocument();
    }

    /**
     * The UpperCaseDocument extends from PlainDocument to overload the
     * insertString method to ensure that all characters are upper case.
     * @author Matthew Cechini
     */
    class UpperCaseDocument extends PlainDocument {
        
        public void insertString(int offs, String str, AttributeSet a) 
              throws BadLocationException {

              if (str == null) {
              return;
              }
              char[] upper = str.toCharArray();
              for (int i = 0; i < upper.length; i++) {
              upper[i] = Character.toUpperCase(upper[i]);
              }
              super.insertString(offs, new String(upper), a);
          }
    }
    
}
