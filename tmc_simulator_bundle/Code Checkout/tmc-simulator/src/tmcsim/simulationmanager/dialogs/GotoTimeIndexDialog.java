package tmcsim.simulationmanager.dialogs;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;

import tmcsim.simulationmanager.SimulationManagerView;
import tmcsim.simulationmanager.model.IncidentTimeSpinnerModel;


/**
 * GotoTimeIndexDialog is a dialog used to prompt the user for the
 * simulation time that the simulation will be reset to.  
 * When the dialog is closed, the gotoApplied flag is set to true 
 * if the user has chosen a time to seek to.  Otherwise, the flag is false.
 *
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class GotoTimeIndexDialog extends JDialog {
    
    /** 
     *  Boolean flag to designate whether the user has chosen to goto
     *  a new position in the simulation.
     */
    public boolean gotoApplied = false;

    /**
     * Constructor.
     *
     * @param simulationManagerView View object to attach this dialog to.
     * @param initialValue Long value (in seconds) of time to initialize spinner with. 
     */
    public GotoTimeIndexDialog(SimulationManagerView simulationManagerView, long initialValue) {
        super(simulationManagerView, "Goto CAD Time Index", true);
        
        initComponents();   
        
        timeSpinner.setValue(initialValue);     

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width/2 - getWidth()/2, screenSize.height/2 - getHeight()/2);
        
        setSize(new Dimension(250, 150));
        setResizable(false);
        setModal(true);
        setVisible(true);
            
    }   
    
    /**
     * Initialize GUI Swing elements and listeners.
     */
    private void initComponents() {
        
        addWindowListener(new WindowListener() {
            public void windowClosed(WindowEvent e)  {}
            public void windowOpened(WindowEvent e)  {}            
            public void windowIconified(WindowEvent e)  {}         
            public void windowDeiconified(WindowEvent e)  {}    
            public void windowActivated(WindowEvent e)  {}                             
            public void windowDeactivated(WindowEvent e)  {}         
            public void windowClosing(WindowEvent e)  {
                gotoApplied = false;                
            }    
        });     
        
        gotoLabel = new JLabel("Choose the new simulation time:");
        gotoLabel.setAlignmentX(Box.CENTER_ALIGNMENT);  

        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gotoApplied = true;
                setVisible(false);  
            }
            
        });
        
        cancelButton = new JButton("Cancel");        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gotoApplied = false;
                setVisible(false);  
            }           
        });
        
        incidentTimeModel = new IncidentTimeSpinnerModel();
        timeSpinner = new JSpinner(incidentTimeModel);
        timeSpinner.setMaximumSize(new Dimension(70,20));
        timeSpinner.setAlignmentX(Box.CENTER_ALIGNMENT);
        
        buttonBox = new Box(BoxLayout.X_AXIS);
        buttonBox.add(Box.createHorizontalStrut(20));
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(okButton);
        buttonBox.add(Box.createHorizontalStrut(20));
        buttonBox.add(cancelButton);
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(Box.createHorizontalStrut(20));   
        buttonBox.setAlignmentX(Box.CENTER_ALIGNMENT);  
              
        gotoIndexBox = new Box(BoxLayout.Y_AXIS);
        gotoIndexBox.add(Box.createVerticalStrut(10));
        gotoIndexBox.add(Box.createVerticalGlue());
        gotoIndexBox.add(gotoLabel);
        gotoIndexBox.add(Box.createVerticalStrut(10));
        gotoIndexBox.add(timeSpinner);
        gotoIndexBox.add(Box.createVerticalStrut(10));
        gotoIndexBox.add(buttonBox);    
        gotoIndexBox.add(Box.createVerticalGlue());   
        gotoIndexBox.add(Box.createVerticalStrut(10)); 
        
        getContentPane().add(gotoIndexBox);
        pack();     
    }       
    
    /**
     * Gets a string representation of the time set by the time
     * spinner in the dialog.  Format:  H:MM:SS
     * @return String representation of spinner time.
     */
    public String getGotoTime() {
        return (String)timeSpinner.getValue();
    }   
    
    
    private JLabel gotoLabel;
    private JSpinner timeSpinner;
    
    private IncidentTimeSpinnerModel incidentTimeModel;
    
    private JButton cancelButton;;
    private JButton okButton;

    private Box gotoIndexBox;   
    private Box buttonBox;  
    
}
