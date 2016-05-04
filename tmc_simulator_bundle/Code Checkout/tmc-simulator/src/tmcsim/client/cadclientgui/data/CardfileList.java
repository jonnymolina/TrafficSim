package tmcsim.client.cadclientgui.data;

import java.awt.List;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * This class extends a list and holds a list of CardfileDataObject alongside a
 * list of CardfileDataObject's Name Field. The index of the CFDO and Name field
 * should match at all times. Each instance of this class is meant to display
 * any one list in the Cardfile's tabbed panel.
 * 
 * @author Vincent
 * 
 */

public class CardfileList extends List implements Serializable {

    LinkedList<CardfileDataObject> list;

    public CardfileList() {
        super();
        list = new LinkedList<CardfileDataObject>();
    }

    /**
     * Inserts the object so it remains in sorted order lexicographically.
     * 
     * @param cfdo
     *            the object to be inserted
     * @return the inserted position
     */
    public int addDataObject(CardfileDataObject cfdo) {
        int insertPosition = 0;
        if (list.size() == 0) {
            super.add(cfdo.getName());
            list.add(cfdo);
            return 0;
        }
        if (cfdo.getName().compareTo(list.get(insertPosition).getName()) < 0) {
            super.add(cfdo.getName(), insertPosition);
            list.add(insertPosition, cfdo);
            return insertPosition;
        }
        while (insertPosition < list.size()
                && cfdo.getName().compareTo(list.get(insertPosition).getName()) > 0) {
            insertPosition++;
        }
        if (insertPosition < list.size()) {
            super.add(cfdo.getName(), insertPosition);
            list.add(insertPosition, cfdo);
        } else {
            super.add(cfdo.getName());
            list.add(cfdo);
        }
        return insertPosition;
    }

    /**
     * Removes the CFDO and name field from both lists at the specified index.
     * 
     * @param index
     */
    public void removeDataObject(int index) {
        if (index >= 0 && index < list.size()) {
            super.remove(index);
            list.remove(index);
        }
    }

    /**
     * returns the CFDO as the specified index, null if a bad index was sent.
     * 
     * @param index
     * @return the CFDO at the given index, or null if a bad index.
     */
    public CardfileDataObject getCFDO(int index) {
        if (!(index < 0)) {
            return list.get(index);
        }
        return null;
    }

    /**
     * Removes the object at the specified index to be reinserted and sorted
     * into the list.
     */
    public int resort(int index) {
        CardfileDataObject cfdoToInsert = list.remove(index);
        super.remove(index);
        return addDataObject(cfdoToInsert);
    }
    
    /**
     * Returns a LinkedList of CFDO that contain the search string.
     */
    public LinkedList<CardfileDataObject> search(String search){
        LinkedList<CardfileDataObject> returnList = new LinkedList<CardfileDataObject>();
        for(int i = 0; i < list.size(); i++){
            if(search.toLowerCase().equals(list.get(i).getCategory().toLowerCase()) ||
               search.toLowerCase().equals(list.get(i).getName().toLowerCase()) ||
               search.toLowerCase().equals(list.get(i).getAddress().toLowerCase()) ||
               search.toLowerCase().equals(list.get(i).getCity().toLowerCase()) ||
               search.toLowerCase().equals(list.get(i).getState().toLowerCase()) ||
               search.toLowerCase().equals(list.get(i).getZip().toLowerCase()) ||
               search.toLowerCase().equals(list.get(i).getPhone1().toLowerCase()) ||
               search.toLowerCase().equals(list.get(i).getPhone2().toLowerCase()) ||
               search.toLowerCase().equals(list.get(i).getFax().toLowerCase())){
                returnList.add(list.get(i));
               }
        }
        return returnList;
    }
}
