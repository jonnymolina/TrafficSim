package tmcsim.client.cadclientgui.data;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * This class holds all the necessarily data for the Cardfile. It contains a
 * CardfileList for each tabbed panel in Cardfile.
 * 
 * @author Vincent
 * 
 */
public class CardfileData implements Serializable {

    private int newestId = 0;

    private static CardfileList coastalDivisionUnitsList;
    private static CardfileList policeSheriffCoronerList;
    private static CardfileList courtsList;
    private static CardfileList publicTransportationList;
    private static CardfileList ggOtherList;
    private static CardfileList myMiscList;
    private static CardfileList slMiscList;
    private static CardfileList vtMiscList;
    private static CardfileList chpOfficesList;
    private static CardfileList stateAgenciesFacilitiesList;
    private static CardfileList governmentOfficialsList;
    private static CardfileList federalAgenciesList;
    private static CardfileList ranchesLivestockList;
    private static CardfileList fireEmsList;
    private static CardfileList jailsList;
    private static CardfileList hospitalsMedCentersList;
    private static CardfileList towCompaniesList;
    private static CardfileList calTransList;
    private static CardfileList countyRoadsList;
    private static CardfileList utilitiesList;
    private static CardfileList animalControlList;
    private static CardfileList airportsList;
    private static CardfileList creditCardsList;
    private static CardfileList ggCrisisSheltersList;
    private static CardfileList rangesList;
    private static CardfileList hotlinesList;
    private static CardfileList hwyPatrolsOosList;
    private static CardfileList parksRecreationList;
    private static CardfileList sheltersList;
    private static CardfileList slCountyServicesList;
    private static CardfileList slResourcesList;
    private static CardfileList truckTireRepairList;
    private static CardfileList mccEmployeesList;
    private static CardfileList gateAccessCodesList;
    private static CardfileList vtCallSignsList;
    private static CardfileList slccEmployeesList;

    public CardfileData() {
        coastalDivisionUnitsList = new CardfileList();
        policeSheriffCoronerList = new CardfileList();
        courtsList = new CardfileList();
        publicTransportationList = new CardfileList();
        ggOtherList = new CardfileList();
        myMiscList = new CardfileList();
        slMiscList = new CardfileList();
        vtMiscList = new CardfileList();
        chpOfficesList = new CardfileList();
        stateAgenciesFacilitiesList = new CardfileList();
        governmentOfficialsList = new CardfileList();
        federalAgenciesList = new CardfileList();
        ranchesLivestockList = new CardfileList();
        fireEmsList = new CardfileList();
        jailsList = new CardfileList();
        hospitalsMedCentersList = new CardfileList();
        towCompaniesList = new CardfileList();
        calTransList = new CardfileList();
        countyRoadsList = new CardfileList();
        utilitiesList = new CardfileList();
        animalControlList = new CardfileList();
        airportsList = new CardfileList();
        creditCardsList = new CardfileList();
        ggCrisisSheltersList = new CardfileList();
        rangesList = new CardfileList();
        hotlinesList = new CardfileList();
        hwyPatrolsOosList = new CardfileList();
        parksRecreationList = new CardfileList();
        sheltersList = new CardfileList();
        slCountyServicesList = new CardfileList();
        slResourcesList = new CardfileList();
        truckTireRepairList = new CardfileList();
        mccEmployeesList = new CardfileList();
        gateAccessCodesList = new CardfileList();
        vtCallSignsList = new CardfileList();
        slccEmployeesList = new CardfileList();
    }

    public int obtainNewUniqueId() {
        int temp = newestId;
        newestId++;
        return temp;
    }

    public CardfileList getCoastalDivisionUnitList() {
        return coastalDivisionUnitsList;
    }

    public void setCoastalDivisionUnitsList(CardfileList objList) {
        coastalDivisionUnitsList = objList;
    }

    public CardfileList getPoliceSheriffCoronerList() {
        return policeSheriffCoronerList;
    }

    public void setPoliceSheriffCoronerList(CardfileList objList) {
        policeSheriffCoronerList = objList;
    }

    public CardfileList getCourtsList() {
        return courtsList;
    }

    public void setCourtsList(CardfileList objList) {
        courtsList = objList;
    }

    public CardfileList getPublicTransportationList() {
        return publicTransportationList;
    }

    public void setPublicTransportationList(CardfileList objList) {
        publicTransportationList = objList;
    }

    public CardfileList getGgOtherList() {
        return ggOtherList;
    }

    public void setGgOtherList(CardfileList objList) {
        ggOtherList = objList;
    }

    public CardfileList getMyMiscList() {
        return myMiscList;
    }

    public void setMyMiscList(CardfileList objList) {
        myMiscList = objList;
    }

    public CardfileList getSlMiscList() {
        return slMiscList;
    }

    public void setSlMiscList(CardfileList objList) {
        slMiscList = objList;
    }

    public CardfileList getVlMiscList() {
        return vtMiscList;
    }

    public void setVlMiscList(CardfileList objList) {
        vtMiscList = objList;
    }

    public CardfileList getChpOfficesList() {
        return chpOfficesList;
    }

    public void setChpOfficesList(CardfileList objList) {
        chpOfficesList = objList;
    }

    public CardfileList getStateAgenciesFacilitiesList() {
        return stateAgenciesFacilitiesList;
    }

    public void setStateAgenciesFacilitiesList(CardfileList objList) {
        stateAgenciesFacilitiesList = objList;
    }

    public CardfileList getGovernmentOfficialsList() {
        return governmentOfficialsList;
    }

    public void setGovernmentOfficialsList(CardfileList objList) {
        governmentOfficialsList = objList;
    }

    public CardfileList getFederalAgenciesList() {
        return federalAgenciesList;
    }

    public void setFederalAgenciesList(CardfileList objList) {
        federalAgenciesList = objList;
    }

    public CardfileList getRanchesLivestockList() {
        return ranchesLivestockList;
    }

    public void setRanchesLivestockList(CardfileList objList) {
        ranchesLivestockList = objList;
    }

    public CardfileList getFireEmsList() {
        return fireEmsList;
    }

    public void setFireEmsList(CardfileList objList) {
        fireEmsList = objList;
    }

    public CardfileList getJailsList() {
        return jailsList;
    }

    public void setJailsList(CardfileList objList) {
        jailsList = objList;
    }

    public CardfileList getHospitalsMedCentersList() {
        return hospitalsMedCentersList;
    }

    public void setHospitalsMedCentersList(CardfileList objList) {
        hospitalsMedCentersList = objList;
    }

    public CardfileList getTowCompaniesList() {
        return towCompaniesList;
    }

    public void setTowCompaniesList(CardfileList objList) {
        towCompaniesList = objList;
    }

    public CardfileList getCalTransList() {
        return calTransList;
    }

    public void setCalTransList(CardfileList objList) {
        calTransList = objList;
    }

    public CardfileList getCountyRoadsList() {
        return countyRoadsList;
    }

    public void setCountyRoadsList(CardfileList objList) {
        countyRoadsList = objList;
    }

    public CardfileList getUtilitiesList() {
        return utilitiesList;
    }

    public void setUtilitiesList(CardfileList objList) {
        utilitiesList = objList;
    }

    public CardfileList getAnimalControlList() {
        return animalControlList;
    }

    public void setAnimalControlList(CardfileList objList) {
        animalControlList = objList;
    }

    public CardfileList getAirportsList() {
        return airportsList;
    }

    public void setAirportsList(CardfileList objList) {
        airportsList = objList;
    }

    public CardfileList getCreditCardsList() {
        return creditCardsList;
    }

    public void setCreditCardsList(CardfileList objList) {
        creditCardsList = objList;
    }

    public CardfileList getGgCrisisSheltersList() {
        return ggCrisisSheltersList;
    }

    public void setGgCrisisSheltersList(CardfileList objList) {
        ggCrisisSheltersList = objList;
    }

    public CardfileList getRangesList() {
        return rangesList;
    }

    public void setRangesList(CardfileList objList) {
        rangesList = objList;
    }

    public CardfileList getHotlinesList() {
        return hotlinesList;
    }

    public void setHotlinesList(CardfileList objList) {
        hotlinesList = objList;
    }

    public CardfileList getHwyPatrolsOosList() {
        return hwyPatrolsOosList;
    }

    public void setHwyPatrolsOosList(CardfileList objList) {
        hwyPatrolsOosList = objList;
    }

    public CardfileList getParksRecreationList() {
        return parksRecreationList;
    }

    public void setParksRecreationList(CardfileList objList) {
        parksRecreationList = objList;
    }

    public CardfileList getSheltersList() {
        return sheltersList;
    }

    public void setSheltersList(CardfileList objList) {
        sheltersList = objList;
    }

    public CardfileList getSlCountyServicesList() {
        return slCountyServicesList;
    }

    public void setSlCountyServicesList(CardfileList objList) {
        slCountyServicesList = objList;
    }

    public CardfileList getSlResourcesList() {
        return slResourcesList;
    }

    public void setSlResourcesList(CardfileList objList) {
        slResourcesList = objList;
    }

    public CardfileList getTruckTireRepairList() {
        return truckTireRepairList;
    }

    public void setTruckTireRepairList(CardfileList objList) {
        truckTireRepairList = objList;
    }

    public CardfileList getMccEmployeesList() {
        return mccEmployeesList;
    }

    public void setMccEmployeesList(CardfileList objList) {
        mccEmployeesList = objList;
    }

    public CardfileList getGateAccessCodesList() {
        return gateAccessCodesList;
    }

    public void setGateAccessCodesList(CardfileList objList) {
        gateAccessCodesList = objList;
    }

    public CardfileList getVtCallSignsList() {
        return vtCallSignsList;
    }

    public void setVtCallSignsList(CardfileList objList) {
        vtCallSignsList = objList;
    }

    public CardfileList getSlccEmployeesList() {
        return slccEmployeesList;
    }

    public void setSlccEmployeesList(CardfileList objList) {
        slccEmployeesList = objList;
    }

    /**
     * Loops through the list until it finds a matching object with the
     * specified id.
     * 
     * @returns the CardfileDataObject with the specified id.
     */
    public CardfileDataObject getCardfileDataObject(CardfileList list, int id) {
        for (int i = 0; i < list.getItemCount(); i++) {
            if (list.getCFDO(i).getId() == id) {
                return list.getCFDO(i);
            }
        }
        return null;
    }

    /**
     * Loops through the list until it finds a matching object with the
     * specified id.
     * 
     * @returns the index of CardfileDataObject with the specified id.
     */
    public int getCardfileDataIndex(CardfileList list, int id) {
        for (int i = 0; i < list.getItemCount(); i++) {
            if (list.getCFDO(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public LinkedList<CardfileDataObject> getSearchList(String search){
        LinkedList<CardfileDataObject> returnList = new LinkedList<CardfileDataObject>();
        LinkedList<CardfileDataObject> tempList = coastalDivisionUnitsList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = policeSheriffCoronerList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = courtsList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = publicTransportationList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = ggOtherList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = myMiscList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = slMiscList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = vtMiscList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = chpOfficesList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = stateAgenciesFacilitiesList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = governmentOfficialsList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = federalAgenciesList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = ranchesLivestockList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = fireEmsList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = jailsList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = hospitalsMedCentersList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = towCompaniesList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = calTransList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = countyRoadsList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = utilitiesList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = animalControlList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = airportsList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = creditCardsList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = ggCrisisSheltersList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = rangesList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = hotlinesList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = hwyPatrolsOosList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = parksRecreationList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = sheltersList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = slCountyServicesList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = slResourcesList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = truckTireRepairList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = mccEmployeesList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = gateAccessCodesList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = vtCallSignsList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        tempList = slccEmployeesList.search(search);
        for(int i = 0; i < tempList.size(); i++){
            returnList.add(tempList.get(i));
        }
        return returnList;
    }
    
}
