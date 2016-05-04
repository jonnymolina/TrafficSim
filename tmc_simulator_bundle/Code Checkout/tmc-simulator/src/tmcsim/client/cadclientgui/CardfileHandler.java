package tmcsim.client.cadclientgui;

import java.awt.List;
import java.io.File;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import tmcsim.client.cadclientgui.data.CardfileData;
import tmcsim.client.cadclientgui.data.CardfileDataObject;
import tmcsim.client.cadclientgui.data.CardfileList;
import tmcsim.client.cadclientgui.enums.CADDataEnums.CARDFILE;
import tmcsim.client.cadclientgui.enums.CADScriptTags.CARDFILE_TAGS;
import tmcsim.client.cadclientgui.screens.ScreenManager;

/**
 * This class is handles the XML script reading for Cardfile. It reads tags and
 * assigns data accordingly.
 * 
 * @author Vincent
 * 
 */
public class CardfileHandler extends DefaultHandler {

    private static enum LEVEL {
        CARDFILE, TITLE, NAME
    };

    private StringBuffer parsedValue = new StringBuffer();

    private String currentTitle;

    private CardfileDataObject cfdObj;

    private CardfileData cardfileData;

    private CardfileList cfdList;

    public CardfileHandler(CardfileData cfd) {
        cardfileData = cfd;
        currentTitle = "";
        cfdList = new CardfileList();
    }

    /*
     * Performs action based on the start tag encountered
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {

        try {
            if (qName.equals(CARDFILE_TAGS.TAB.tag)) {
                currentTitle = attributes.getValue(CARDFILE_TAGS.TITLE.tag);
            } else if (qName.equals(CARDFILE_TAGS.CARDFILE_OBJ.tag)) {
                cfdObj = new CardfileDataObject(currentTitle,
                        attributes.getValue(CARDFILE_TAGS.NAME.tag),
                        cardfileData.obtainNewUniqueId());
            }

        } catch (Exception e) {
            System.out
                    .println("startElement error: " + e.getLocalizedMessage());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length) {
        parsedValue.append(new String(ch, start, length).trim());
    }

    /*
     * Performs the action based on the end tag encountered
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName) {

        try {
            if (qName.equals(CARDFILE_TAGS.ADDRESS.tag)) {
                cfdObj.setAddress(parsedValue.toString());
            } else if (qName.equals(CARDFILE_TAGS.CITY.tag)) {
                cfdObj.setCity(parsedValue.toString());
            } else if (qName.equals(CARDFILE_TAGS.STATE.tag)) {
                cfdObj.setState(parsedValue.toString());
            } else if (qName.equals(CARDFILE_TAGS.ZIP.tag)) {
                cfdObj.setZip(parsedValue.toString());
            } else if (qName.equals(CARDFILE_TAGS.PHONE1.tag)) {
                cfdObj.setPhone1(parsedValue.toString());
            } else if (qName.equals(CARDFILE_TAGS.PHONE2.tag)) {
                cfdObj.setPhone2(parsedValue.toString());
            } else if (qName.equals(CARDFILE_TAGS.FAX.tag)) {
                cfdObj.setFax(parsedValue.toString());
            } else if (qName.equals(CARDFILE_TAGS.CARDFILE_OBJ.tag)) {
                cfdList.addDataObject(cfdObj);
            } else if (qName.equals(CARDFILE_TAGS.TAB.tag)) {
                addToCardfileData();
                cfdList = new CardfileList();
            }
        } catch (Exception e) {
            System.out.println("endElement error" + e.getLocalizedMessage());
        }
        parsedValue.setLength(0);
    }

    private void addToCardfileData() {
        if (currentTitle.equals(CARDFILE.COASTAL_DIVISION_UNITS.tag)) {
            cardfileData.setCoastalDivisionUnitsList(cfdList);
        } else if (currentTitle.equals(CARDFILE.POLICE_SHERIFF_CORONER.tag)) {
            cardfileData.setPoliceSheriffCoronerList(cfdList);
        } else if (currentTitle.equals(CARDFILE.COURTS.tag)) {
            cardfileData.setCourtsList(cfdList);
        } else if (currentTitle.equals(CARDFILE.PUBLIC_TRANSPORTATION.tag)) {
            cardfileData.setPublicTransportationList(cfdList);
        } else if (currentTitle.equals(CARDFILE.GG_OTHER.tag)) {
            cardfileData.setGgOtherList(cfdList);
        } else if (currentTitle.equals(CARDFILE.MY_MISC.tag)) {
            cardfileData.setMyMiscList(cfdList);
        } else if (currentTitle.equals(CARDFILE.SL_MISC.tag)) {
            cardfileData.setSlMiscList(cfdList);
        } else if (currentTitle.equals(CARDFILE.VT_MISC.tag)) {
            cardfileData.setVlMiscList(cfdList);
        } else if (currentTitle.equals(CARDFILE.CHP_OFFICES.tag)) {
            cardfileData.setChpOfficesList(cfdList);
        } else if (currentTitle.equals(CARDFILE.STATE_AGENCIES_FACILITIES.tag)) {
            cardfileData.setStateAgenciesFacilitiesList(cfdList);
        } else if (currentTitle.equals(CARDFILE.GOVERNMENT_OFFICIALS.tag)) {
            cardfileData.setGovernmentOfficialsList(cfdList);
        } else if (currentTitle.equals(CARDFILE.FEDERAL_AGENCIES.tag)) {
            cardfileData.setFederalAgenciesList(cfdList);
        } else if (currentTitle.equals(CARDFILE.FIRE_EMS.tag)) {
            cardfileData.setFireEmsList(cfdList);
        } else if (currentTitle.equals(CARDFILE.JAILS.tag)) {
            cardfileData.setJailsList(cfdList);
        } else if (currentTitle.equals(CARDFILE.HOSPITALS_MED_CENTERS.tag)) {
            cardfileData.setHospitalsMedCentersList(cfdList);
        } else if (currentTitle.equals(CARDFILE.TOW_COMPANIES.tag)) {
            cardfileData.setTowCompaniesList(cfdList);
        } else if (currentTitle.equals(CARDFILE.CALTRANS.tag)) {
            cardfileData.setCalTransList(cfdList);
        } else if (currentTitle.equals(CARDFILE.COUNTY_ROADS.tag)) {
            cardfileData.setCountyRoadsList(cfdList);
        } else if (currentTitle.equals(CARDFILE.UTILITIES.tag)) {
            cardfileData.setUtilitiesList(cfdList);
        } else if (currentTitle.equals(CARDFILE.ANIMAL_CONTROL.tag)) {
            cardfileData.setAnimalControlList(cfdList);
        } else if (currentTitle.equals(CARDFILE.AIRPORTS.tag)) {
            cardfileData.setAirportsList(cfdList);
        } else if (currentTitle.equals(CARDFILE.CREDIT_CARDS.tag)) {
            cardfileData.setCreditCardsList(cfdList);
        } else if (currentTitle.equals(CARDFILE.GG_CRISIS_SHELTERS.tag)) {
            cardfileData.setGgCrisisSheltersList(cfdList);
        } else if (currentTitle.equals(CARDFILE.RANGES.tag)) {
            cardfileData.setRangesList(cfdList);
        } else if (currentTitle.equals(CARDFILE.HOTLINES.tag)) {
            cardfileData.setHotlinesList(cfdList);
        } else if (currentTitle.equals(CARDFILE.HWY_PATROLS_OOS.tag)) {
            cardfileData.setHwyPatrolsOosList(cfdList);
        } else if (currentTitle.equals(CARDFILE.PARKS_RECREATION.tag)) {
            cardfileData.setParksRecreationList(cfdList);
        } else if (currentTitle.equals(CARDFILE.SHELTERS)) {
            cardfileData.setSheltersList(cfdList);
        } else if (currentTitle.equals(CARDFILE.SL_COUNTY_SERVICES.tag)) {
            cardfileData.setSlCountyServicesList(cfdList);
        } else if (currentTitle.equals(CARDFILE.SL_RESOURCES.tag)) {
            cardfileData.setSlResourcesList(cfdList);
        } else if (currentTitle.equals(CARDFILE.TRUCK_TIRE_REPAIR.tag)) {
            cardfileData.setTruckTireRepairList(cfdList);
        } else if (currentTitle.equals(CARDFILE.MCC_EMPLOYEES.tag)) {
            cardfileData.setMccEmployeesList(cfdList);
        } else if (currentTitle.equals(CARDFILE.GATE_ACCESS_CODES.tag)) {
            cardfileData.setGateAccessCodesList(cfdList);
        } else if (currentTitle.equals(CARDFILE.VT_CALL_SIGNS.tag)) {
            cardfileData.setVtCallSignsList(cfdList);
        } else if (currentTitle.equals(CARDFILE.SLCC_EMPLOYEES.tag)) {
            cardfileData.setSlccEmployeesList(cfdList);
        }
    }

}
