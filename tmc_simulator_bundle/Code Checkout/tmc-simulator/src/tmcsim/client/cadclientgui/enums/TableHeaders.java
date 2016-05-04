package tmcsim.client.cadclientgui.enums;

public class TableHeaders {
    
    public static final String[] ASSIGNED_INCIDENTS_HEADERS = { "ID", "Master Inc #",
            "Alerts    (Hub 2)", "Primary", "Unit/s", "Type", "Address",
            "Location", "Beat", "Area", "OAU", "Time", "Log Time", "Type Code",
            "P", "AGY" };

    public static final String[] UNIT_STATUS_HEADERS = { "ID", "Master Inc #",
            "Unit", "Status", "OOS", "Type", "Current Location", "Destination",
            "Misc", "Stack", "Area", "Badge Number", "Officer", "Timer",
            "Office", "P" };

    public static final String[] PENDING_INCIDENTS_HEADERS = { "ID", "Master Inc #",
            "Alerts    (Hub 2)", "P", "Type", "Address", "Location", "City",
            "Beat", "Area", "OAU", "Timer", "Log Time", "Type Code", "AGY" };

    public static final String[] INCIDENT_EDITOR_HEADERS = { "Date", "Type",
            "Address", "Location", "City", "Incident #", "CC", "Area", "Units",
            "Dispo" };
}
