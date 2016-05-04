package tmcsim.cadsimulator.db;

import tmcsim.common.CCTVDirections;

/**
 * CCTVInfo is a container class to hold a CCTV camera's id and direction.
 */
public class CCTVInfo implements Comparable<CCTVInfo> {
    
    /** CCTV Camera ID */
    public int id;
    
    /** CCTV Camera Direction */
    public CCTVDirections dir;
    
    public CCTVInfo(int newID, CCTVDirections newDir) {
        id  = newID;
        dir = newDir;
    }

    /**
     * Determines equality by comparing id and direction.
     */     
    public boolean equals(Object o) {
        return id == ((CCTVInfo)o).id &&
               dir == ((CCTVInfo)o).dir;
    }       

    /** 
     * If id is equal, compare according to direction, else compare id's.
     */     
    public int compareTo(CCTVInfo o ) {
        if(id == o.id) {
            return dir.compareTo(o.dir);
        }
        else {
            return new Integer(id).compareTo(o.id);
        }
    }           
}   
