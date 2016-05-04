package tmcsim.simulationmanager.model;

/** 
 * Container class to hold information displayed in the table.
 * @author Matthew Cechini
 */
public class AppliedDiversionTableItem implements
        Comparable<AppliedDiversionTableItem> {

    public String cmsID;

    public String originalPath;

    public String newPath;

    public Integer currentDiv;

    public Long timeApplied;
    
    
    public AppliedDiversionTableItem(String id, String oPath, String nPath,
            Integer div, Long time) {
        cmsID = id;
        originalPath = oPath;
        newPath = nPath;
        currentDiv = div;
        timeApplied = time;
    }

    public int compareTo(AppliedDiversionTableItem arg) {

        if (cmsID.equals(arg.cmsID)) {
            if (originalPath.equals(arg.originalPath)) {
                return newPath.compareTo(arg.newPath);
            }
            return originalPath.compareTo(arg.originalPath);
        } else
            return cmsID.compareTo(arg.cmsID);
    }
}
