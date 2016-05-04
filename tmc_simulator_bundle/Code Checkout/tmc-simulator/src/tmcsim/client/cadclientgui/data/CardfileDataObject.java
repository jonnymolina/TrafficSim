package tmcsim.client.cadclientgui.data;

import java.io.Serializable;
import java.util.LinkedList;

import javax.swing.table.DefaultTableModel;

public class CardfileDataObject implements Serializable {

    private int idTag;

    private String category;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String phone1;
    private String phone2;
    private String fax;
    private LinkedList<String[]> commentsTableFields;

    public CardfileDataObject(String category, String name, int idTag) {
        this.idTag = idTag;
        this.category = category;
        this.name = name;
        address = "";
        city = "";
        state = "";
        zip = "";
        phone1 = "";
        phone2 = "";
        fax = "";
        commentsTableFields = new LinkedList<String[]>();
    }

    public int getId() {
        return idTag;
    }

    public String getCategory() {
        return category;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getZip() {
        return zip;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getFax() {
        return fax;
    }

    public LinkedList<String[]> getCommentsTableFields() {
        return commentsTableFields;
    }

    public void addComment(String[] fields) {
        commentsTableFields.add(fields);
    }

    public void removeComment(String timeStamp) {
        for (int i = 0; i < commentsTableFields.size(); i++) {
            String[] temp = commentsTableFields.get(i);
            if (temp[1].equals(timeStamp)) {
                commentsTableFields.remove(i);
                return;
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj.getClass() != getClass()) {
            return false;
        }
        if (idTag != ((CardfileDataObject) obj).idTag) {
            return false;
        }
        return true;
    }

}
