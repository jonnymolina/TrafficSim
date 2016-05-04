package tmcsim.simulationmanager.model;

import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import tmcsim.cadmodels.CMSDiversion;

/**
 * The CMSDiversionSliderBox is a GUI component that is used to create
 * the diversion slider that allows the user to select a diversion percentage.
 * This object represents a single possible diversion for a CMS.
 * 
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class CMSDiversionSliderBox extends Box implements ChangeListener {

    /** CMSDiversion object with data for this diversion slider. */
    private CMSDiversion diversionObject;

    /** Slider to allow user to adjust the diversion percentage. */
    private JSlider diversionSlider;

    /** Formatted Text Field to display current percentage. */
    private JFormattedTextField textField;

    /** Maximum allowed diversion percentage. */
    private int maxDiversion;

    /** Label table for diversion slider. */
    private Hashtable<Integer, JLabel> diversionLabelTable = null;

    
    /**
     * Constructor.  Initialize GUI components.  Create the diversion
     * slider with a maximum value equal to the maximum diversion
     * percentage in the parameter CMSDiversion object.
     * 
     * @param newDivObj CMS diversion data to be displayed.
     */
    public CMSDiversionSliderBox(CMSDiversion newDivObj) {
        super(BoxLayout.Y_AXIS);
        setAlignmentX(Box.CENTER_ALIGNMENT);

        diversionObject = newDivObj;
        maxDiversion    = diversionObject.maxDiversionPercent.intValue();

        diversionLabelTable = new Hashtable<Integer, JLabel>();
        diversionLabelTable.put(new Integer(0), new JLabel("0%"));
        diversionLabelTable.put(new Integer(maxDiversion), 
                new JLabel(String.valueOf(maxDiversion) + "%"));

        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        NumberFormatter formatter = new NumberFormatter(numberFormat);
        formatter.setMinimum(new Integer(0));
        formatter.setMaximum(new Integer(maxDiversion));

        textField = new JFormattedTextField(formatter);
        textField.setValue(new Integer(0));
        textField.setColumns(5);
        textField.setAlignmentX(Box.CENTER_ALIGNMENT);
        textField.setMaximumSize(new Dimension(50, 25));
        textField.setMinimumSize(new Dimension(40, 25));

        diversionSlider = new JSlider(JSlider.VERTICAL, 0, maxDiversion, 0);
        diversionSlider.addChangeListener(this);
        diversionSlider.setAlignmentX(Box.CENTER_ALIGNMENT);
        diversionSlider.setPaintTicks(true);
        diversionSlider.setPaintLabels(true);
        diversionSlider.setMinorTickSpacing(10);
        diversionSlider.setLabelTable(diversionLabelTable);
        diversionSlider.setValue(diversionObject.getCurrDiv());

        add(textField);
        add(diversionSlider);
    }

    /**
     * Set the CMSDiversion object's diversion percentage to the 
     * new value on the slider.
     */
    public void apply() {
        diversionObject.setCurrDiv(diversionSlider.getValue());
    }

    /**
     * ChangeListener method.  When the slider changes, update the
     * formatted text field with the new percentage.
     */
    public void stateChanged(ChangeEvent e) {

        float newDiv = diversionSlider.getValue();
        
        if (!diversionSlider.getValueIsAdjusting()) {
            textField.setValue(new Float(newDiv / 100f));
        } else {
            textField.setText(String.valueOf((int) newDiv) + "%");
        }
    }
}
