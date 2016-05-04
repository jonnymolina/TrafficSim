package tmcsim.client.cadclientgui.data;

import java.io.Serializable;
import java.util.LinkedList;

import javax.swing.table.DefaultTableModel;

/**
 * NOTE THIS CLASS IS CURRENTLY UNUSED. This class is a
 * further subdivision of Incident to hold data.
 * 
 * @author Vincent
 * 
 */
public class IncidentVehicle implements Serializable {

    private LinkedList<String> plate;
    private LinkedList<String> state;
    private LinkedList<String> type;
    private LinkedList<String> year;
    private DefaultTableModel licenseTable;

    private static enum RESPONSE_ENUMS {
        PLATE("PLATE"), STATE("STATE"), TYPE("TYPE"), YEAR("YEAR");

        public String tag;

        private RESPONSE_ENUMS(String t) {
            tag = t;
        }
    }

    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentVehicle() {
        plate = new LinkedList<String>();
        state = new LinkedList<String>();
        type = new LinkedList<String>();
        year = new LinkedList<String>();
        licenseTable = new DefaultTableModel();
        licenseTable.setColumnIdentifiers(new String[] { "Plate", "State" });
    }

    public LinkedList<String> getPlate() {
        return plate;
    }

    /*
     * public void addPlate(String plate) { this.plate.add(plate); }
     */

    public LinkedList<String> getState() {
        return state;
    }

    /*
     * public void addState(String state) { this.state.add(state); }
     */

    public LinkedList<String> getType() {
        return type;
    }

    /*
     * public void addType(String type){ this.type.add(type); }
     */

    public LinkedList<String> getYear() {
        return year;
    }

    /*
     * public void addYear(String year){ this.year.add(year); }
     */

    public DefaultTableModel getLicenseTable() {
        return licenseTable;
    }

    public void addVehicleInformation(String plate, String state, String type,
            String year) {
        this.plate.add(plate);
        this.state.add(state);
        this.type.add(type);
        this.year.add(year);
        licenseTable.addRow(new String[] { plate, state });
    }

}
