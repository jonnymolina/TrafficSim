package tmcsim.simulationmanager;

import java.awt.BorderLayout;
import java.util.Vector;
import javax.swing.JFrame;
import tmcsim.cadmodels.*;
import tmcsim.client.cadclientgui.data.IncidentEvent;

/**
 * Driver to display a sample IncidentHistoryPanel
 * @author Jonathan Molina
 */
public class IncidentHistoryPanelDriver
{

    public static void main(String[] args)
    {
        // Create an IncidentEvent
        // The incident type is a 4-digit string (Use MMYY for month and year of your birth)
        // The incident location is the name of your home town
        // The incident details is a parade on the main street (provide street name for your home town)
        // Create an IncidentHistoryPanel
        // Add the event to the panel

        // Create the frame.
        JFrame frame = new JFrame("Incident Panel Demo");
        // Specify closing behavior
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Add component to the frame.
//        frame.getContentPane().add(ihPanel, BorderLayout.CENTER);
        // Size the frame.
        frame.pack();
        // Show it.
        frame.setVisible(true);
    }
}