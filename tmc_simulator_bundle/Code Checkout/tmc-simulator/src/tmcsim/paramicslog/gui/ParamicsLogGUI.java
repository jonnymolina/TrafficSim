package tmcsim.paramicslog.gui;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.util.*;

/**
 * The UI for ParamicsLog. 
 * @author Nathaniel Lehrer
 * @version
 */
public class ParamicsLogGUI extends JFrame implements Observer {

    /** The static instance */
    private static ParamicsLogGUI instance = new ParamicsLogGUI();

    /** The text area to display the log in */
    private JTextArea textArea;
    
    /** Creates an instance of this class */
    public ParamicsLogGUI() {
        
        setupGUI();
    }
    
    /** Creates the UI */
    private void setupGUI()
    {
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
        
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            
            public void windowClosing(WindowEvent e)
            {
                setVisible(false);
            }
        });
        
        
        setTitle("Paramics Log");
        
        textArea = new JTextArea();
        textArea.setColumns(60);
        textArea.setRows(30);
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        getContentPane().add(scrollPane);
        
    }
    
    /** Shows the UI window */
    public void display()
    {
        pack();
        setVisible(true);
    }

    /**
     * Updates the text area. If the observable class given is of type ParamicsLog then
     * the log entries are displayed.
     * @param o The model for this viewer.
     * @param arg An argument that is not used.
     */
    public void update(Observable o, Object arg)
    {
        if (o instanceof tmcsim.paramicslog.ParamicsLog)
        {
            textArea.setText(((tmcsim.paramicslog.ParamicsLog) o).getLog());
        } 

        repaint();
    }
    
    /**
     * Accessor for the instance of ParamicsLogGUI.
     * @return The instance of ParamicsLogGUI.
     */
    public static ParamicsLogGUI getInstance() {
        return instance;
    }
    
}
