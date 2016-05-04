package tmcsim.client.cadclientgui.data;

import java.io.Serializable;

import tmcsim.client.cadclientgui.enums.CADDataEnums.EditCommand;

/**
 * Each changelog holds enough data to remember which object was changed and
 * what was changed, so that upon close, the user can choose to keep or forget
 * the changes.
 * 
 * @author Vincent
 * 
 */
public class ChangeLog implements Serializable {

    public EditCommand command;
    public String listTitle;
    public int id;
    public String newValue;
    public CardfileDataObject newCardfileObject;
    public String[] tableFields = new String[4];
    public String timeStamp;

    // creates an edit log to modify any one field of a CardfileDataObject
    public ChangeLog(EditCommand command, String listTitle, int id,
            String newValue) {
        this.command = command;
        this.listTitle = listTitle;
        this.id = id;
        this.newValue = newValue;
    }

    // creates an add log for a CardfileDataObject
    public ChangeLog(EditCommand command, String listTitle,
            CardfileDataObject newObj) {
        this.command = command;
        this.listTitle = listTitle;
        this.newCardfileObject = newObj;
    }

    // create a delete log for a CardfileDataObject
    public ChangeLog(EditCommand command, String listTitle, int id) {
        this.command = command;
        this.listTitle = listTitle;
        this.id = id;
    }

    // create an add log for a CardfileDataObject's table
    public ChangeLog(EditCommand command, String listTitle, int id,
            String[] tableFields) {
        this.command = command;
        this.listTitle = listTitle;
        this.id = id;
        this.tableFields = tableFields;
    }

    // create a delete log for a CardfileDataObject's table
    public ChangeLog(EditCommand command, String listTitle, String timeStamp,
            int id) {
        this.command = command;
        this.listTitle = listTitle;
        this.timeStamp = timeStamp;
        this.id = id;
    }
}
