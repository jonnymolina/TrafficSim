package tmcsim.simulationmanager.dialogs;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;

import tmcsim.cadmodels.CMSDiversion;
import tmcsim.cadmodels.CMSInfo;
import tmcsim.simulationmanager.model.CMSDiversionSliderBox;


/**
 * The CMSDiversionDialog is used to allow the user to select diversion percentages
 * for a specific changeable message sign.  This dialog is initialized with a CMSInfo
 * object, which contains the unique identifying information and diversions for a CMS.
 * A slider for each possible diversion is created and represented on the dialog.  
 * The user may modify diversion percentages and then choose to apply them, or cancel 
 * and close the dialog.  Diversion percentages are updated in the RouteSliderBox,
 * which points to the local CMSInfo object.
 * 
 * @author Matthew Cechini (mcechini@calpoly.edu)
 * @version $Date: 2006/06/06 20:46:41 $ $Revision: 1.7 
 */
@SuppressWarnings("serial")
public class CMSDiversionDialog extends JDialog {
    
    /** CMSInfo object associated with this dialog. */
    public CMSInfo theCMSInfo = null;
    
    /** Vector of RouteSliderBox objects, each representing a possible diversion. */
    private Vector<CMSDiversionSliderBox> diversionSliders;

    /**
     * Constructor.  Initialize GUI components.
     * 
     * @param parent View object to attach.
     * @param newCMSInfo CMSInfo object containing diversion info.
     */ 
    public CMSDiversionDialog(Frame parent) {
        super(parent, "Add New Diversion", true);
        
        diversionSliders = new Vector<CMSDiversionSliderBox>();
        
    }
    
    public void showDialog(CMSInfo newCMSInfo) {
        theCMSInfo = newCMSInfo;
        
        diversionSliders.clear();
        getContentPane().removeAll();
        
        initComponents(theCMSInfo);     

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width/2 - getWidth()/2, screenSize.height/2 - getHeight()/2);
        
        setVisible(true);
    }
    
    /**
     * Initialize GUI Swing elements and listeners.  Create RouteSliderBox 
     * objects for each possible diversion in the CMSInfo object.
     * 
     * @param theDiversion
     */
    private void initComponents(CMSInfo theCMS) {

        Box cmsSignInfoBox = new Box(BoxLayout.X_AXIS);
        cmsSignInfoBox.setAlignmentX(Box.CENTER_ALIGNMENT);
        
        JLabel cmsIDLabel        = new JLabel("CMS ID:  ");
        JLabel cmsPostmileLabel  = new JLabel("Postmile:  ");
        JLabel cmsMainLineLabel  = new JLabel("Main Line:  ");      
        
        JTextField cmsIDTextField        = new JTextField(String.valueOf(theCMS.cmsID), 4);
        JTextField cmsPostmileTextField  = new JTextField(String.valueOf(theCMS.postmile), 10);
        JTextField cmsMainLineTextField  = new JTextField(theCMS.initialRoute, 10);     
        
        cmsIDTextField.setEditable(false);
        cmsIDTextField.setMaximumSize(new Dimension(190, 25));
        cmsIDTextField.setMinimumSize(new Dimension(120, 25));
        cmsPostmileTextField.setEditable(false);
        cmsPostmileTextField.setMaximumSize(new Dimension(60, 25));
        cmsPostmileTextField.setMinimumSize(new Dimension(40, 25));;
        cmsMainLineTextField.setEditable(false);                
        cmsMainLineTextField.setMaximumSize(new Dimension(60, 25));
        cmsMainLineTextField.setMinimumSize(new Dimension(40, 25));

        cmsSignInfoBox.add(cmsIDLabel);
        cmsSignInfoBox.add(cmsIDTextField);     
        cmsSignInfoBox.add(Box.createHorizontalStrut(10));
        cmsSignInfoBox.add(cmsPostmileLabel);
        cmsSignInfoBox.add(cmsPostmileTextField);       
        cmsSignInfoBox.add(Box.createHorizontalStrut(10));
        cmsSignInfoBox.add(cmsMainLineLabel);
        cmsSignInfoBox.add(cmsMainLineTextField);       
        
        Box diversionSlidersBox = new Box(BoxLayout.X_AXIS);
        diversionSlidersBox.setAlignmentX(Box.CENTER_ALIGNMENT);
        
        Box labelBox = new Box(BoxLayout.Y_AXIS);
        JLabel oldPathLabel = new JLabel("Old Path:");
        oldPathLabel.setAlignmentX(Box.CENTER_ALIGNMENT);
        JLabel spaceLabel = new JLabel(" ");
        oldPathLabel.setAlignmentX(Box.CENTER_ALIGNMENT);
        JLabel newPathLabel = new JLabel("New Path:");
        newPathLabel.setAlignmentX(Box.CENTER_ALIGNMENT);
        
        labelBox.add(oldPathLabel);
        labelBox.add(spaceLabel);
        labelBox.add(newPathLabel);
        labelBox.add(Box.createVerticalGlue());

        diversionSlidersBox.add(labelBox);
        
        CMSDiversionSliderBox slider;
        // For each diversion, create a new RouteSliderBox and
        // needed labels and GUI elements.
        for(CMSDiversion cmsd : theCMS.possibleDiversions) {
            
            Box singleDiversionBox = new Box(BoxLayout.Y_AXIS);
            JLabel originalLabel = new JLabel(cmsd.originalPath);
            JLabel toLabel       = new JLabel("To");
            JLabel newLabel      = new JLabel(cmsd.newPath);
            
            originalLabel.setAlignmentX(Box.CENTER_ALIGNMENT);
            toLabel.setAlignmentX(Box.CENTER_ALIGNMENT);
            newLabel.setAlignmentX(Box.CENTER_ALIGNMENT);
            
            singleDiversionBox.add(originalLabel);
            singleDiversionBox.add(toLabel);
            singleDiversionBox.add(newLabel);
    
            slider = new CMSDiversionSliderBox(cmsd);
            diversionSliders.add(slider);
            singleDiversionBox.add(slider);
            singleDiversionBox.add(Box.createHorizontalStrut(85));
            
            diversionSlidersBox.add(singleDiversionBox);
            
        }
        
        JLabel instructionsLabel = new JLabel("Choose the percentage of traffic " + 
                                              "traveling on the main line that will be diverted.");
        instructionsLabel.setAlignmentX(Box.CENTER_ALIGNMENT);
        
        Box diversionInteractionBox = new Box(BoxLayout.Y_AXIS);
        diversionInteractionBox.setAlignmentX(Box.CENTER_ALIGNMENT);
        diversionInteractionBox.add(cmsSignInfoBox);
        diversionInteractionBox.add(Box.createVerticalStrut(5));
        diversionInteractionBox.add(new JSeparator());
        diversionInteractionBox.add(instructionsLabel);
        diversionInteractionBox.add(Box.createVerticalStrut(10));
        diversionInteractionBox.add(diversionSlidersBox);       

        CompoundBorder cBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), 
                                                 "Diversion Information"),
                BorderFactory.createEmptyBorder(5,5,5,5));
        diversionInteractionBox.setBorder(cBorder);         
        
        
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(new ActionListener() {
            /* 
             * Set this dialog's visibility to false, and call the apply() 
             * method in all route slider boxes to set the new diversion 
             * percentages into the CMSDiversion objects.  
             */
            public void actionPerformed(ActionEvent evt) {
                for(CMSDiversionSliderBox rs : diversionSliders)
                    rs.apply();
                
                setVisible(false);              
            }
        });  
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            /* 
             * Set this dialog's visibility to false, and call the reset() 
             * method in all route slider boxes to reset them to the 
             * original diversion percentage.
             */         
            public void actionPerformed(ActionEvent evt) {
                theCMSInfo.reset();

                setVisible(false);
            }
        });  
        
        Box diversionButtonBox = new Box(BoxLayout.X_AXIS);
        diversionButtonBox.setAlignmentX(Box.CENTER_ALIGNMENT);     
        
        diversionButtonBox.add(Box.createHorizontalGlue());
        diversionButtonBox.add(applyButton);
        diversionButtonBox.add(Box.createHorizontalStrut(20));
        diversionButtonBox.add(cancelButton);
        diversionButtonBox.add(Box.createHorizontalStrut(15));      
        
                    
        Box diversionDialogBox = new Box(BoxLayout.Y_AXIS);
        diversionDialogBox.add(Box.createVerticalStrut(10));        
        diversionDialogBox.add(diversionInteractionBox);
        diversionDialogBox.add(diversionButtonBox);
    
        getContentPane().add(diversionDialogBox);
        pack();
        
        int width = 550 > diversionSliders.size()*100 ? 550 : diversionSliders.size()*100;
        setSize(new Dimension(width, 500));
        setResizable(false);        
        
    }   
    
}





