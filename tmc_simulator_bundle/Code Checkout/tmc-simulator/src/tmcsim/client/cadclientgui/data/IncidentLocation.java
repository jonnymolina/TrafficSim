package tmcsim.client.cadclientgui.data;

import java.io.Serializable;

import tmcsim.client.cadclientgui.enums.CADScriptTags.INCIDENT_LOCATION_TAGS;

/**
 * This class is a further subdivision of Incident to hold data.
 * 
 * @author Vincent
 * 
 */
public class IncidentLocation implements Serializable {
    
    /* The init variables are set only from the XML script (readXMLNode method)
     * and are only used for resetCADDataSimulation purposes
     */
    private String init_address = "";
    private String init_loc = "";
    private String init_city = "";
    private String init_county = "";
    private String init_state = "";
    private String init_zip = "";
    private String init_beat = "";
    private String init_area = "";
    private String init_sector = "";
    private String init_sectorCode = "";
    private String init_division = "";
    private String init_apt = "";
    private String init_building = "";
    private String init_crossSt = "";
    private String init_law = "";
    private String init_fire = "";
    private String init_ems = "";
    
    private String address;
    private String loc;
    private String city;
    private String county;
    private String state;
    private String zip;
    private String beat;
    private String area;
    private String sector;
    private String sectorCode;
    private String division;
    private String apt;
    private String building;
    private String crossSt;
    private String law;
    private String fire;
    private String ems;

    /*
     * Constructor. Initializes all objects to avoid null pointers.
     */
    public IncidentLocation() {
        address = "";
        loc = "";
        city = "";
        county = "";
        state = "";
        zip = "";
        beat = "";
        area = "";
        sector = "";
        sectorCode = "";
        division = "";
        apt = "";
        building = "";
        crossSt = "";
        law = "";
        fire = "";
        ems = "";
    }

    public void resetCADDataSimulation(){
        address = init_address;
        loc = init_loc;
        city = init_city;
        county = init_county;
        state = init_state;
        zip = init_zip;
        beat = init_beat;
        area = init_area;
        sector = init_sector;
        sectorCode = init_sectorCode;
        division = init_division;
        apt = init_apt;
        building = init_building;
        crossSt = init_crossSt;
        law = init_law;
        fire = init_fire;
        ems = init_ems;
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getBeat() {
        return beat;
    }

    public void setBeat(String beat) {
        this.beat = beat;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getSectorCode() {
        return sectorCode;
    }

    public void setSectorCode(String sectorCode) {
        this.sectorCode = sectorCode;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getApt() {
        return apt;
    }

    public void setApt(String apt) {
        this.apt = apt;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getCrossSt() {
        return crossSt;
    }

    public void setCrossSt(String crossSt) {
        this.crossSt = crossSt;
    }

    public String getLaw() {
        return law;
    }

    public void setLaw(String law) {
        this.law = law;
    }

    public String getFire() {
        return fire;
    }

    public void setFire(String fire) {
        this.fire = fire;
    }

    public String getEms() {
        return ems;
    }

    public void setEms(String ems) {
        this.ems = ems;
    }

    /*
     * Called from the tmc.simulator.cadclient.data.Incident.java. Handles
     * storing data based on script tag.
     */
    public void readXMLNode(String tag_name, String value) {
        if (tag_name.equals(INCIDENT_LOCATION_TAGS.ADDRESS.tag)) {
            init_address = value;
            setAddress(value);
        } else if (tag_name.equals(INCIDENT_LOCATION_TAGS.LOC.tag)) {
            init_loc = value;
            setLoc(value);
        } else if (tag_name.equals(INCIDENT_LOCATION_TAGS.CITY.tag)) {
            init_city = value;
            setCity(value);
        } else if (tag_name.equals(INCIDENT_LOCATION_TAGS.COUNTY.tag)) {
            init_county = value;
            setCounty(value);
        } else if (tag_name.equals(INCIDENT_LOCATION_TAGS.STATE.tag)) {
            init_state = value;
            setState(value);
        } else if (tag_name.equals(INCIDENT_LOCATION_TAGS.ZIP.tag)) {
            init_zip = value;
            setZip(value);
        } else if (tag_name.equals(INCIDENT_LOCATION_TAGS.BEAT.tag)) {
            init_beat = value;
            setBeat(value);
        } else if (tag_name.equals(INCIDENT_LOCATION_TAGS.AREA.tag)) {
            init_area = value;
            setArea(value);
        } else if (tag_name.equals(INCIDENT_LOCATION_TAGS.SECTOR.tag)) {
            init_sector = value;
            setSector(value);
        } else if (tag_name.equals(INCIDENT_LOCATION_TAGS.SECTOR_CODE.tag)) {
            init_sectorCode = value;
            setSectorCode(value);
        } else if (tag_name.equals(INCIDENT_LOCATION_TAGS.DIVISION.tag)) {
            init_division = value;
            setDivision(value);
        } else if (tag_name.equals(INCIDENT_LOCATION_TAGS.APT.tag)) {
            init_apt = value;
            setApt(value);
        } else if (tag_name.equals(INCIDENT_LOCATION_TAGS.BUILDING.tag)) {
            init_building = value;
            setBuilding(value);
        } else if (tag_name.equals(INCIDENT_LOCATION_TAGS.CROSS_STREET.tag)) {
            init_crossSt = value;
            setCrossSt(value);
        } else if (tag_name.equals(INCIDENT_LOCATION_TAGS.LAW.tag)) {
            init_law = value;
            setLaw(value);
        } else if (tag_name.equals(INCIDENT_LOCATION_TAGS.FIRE.tag)) {
            init_fire = value;
            setFire(value);
        } else if (tag_name.equals(INCIDENT_LOCATION_TAGS.EMS.tag)) {
            init_ems = value;
            setEms(value);
        }
    }

}
