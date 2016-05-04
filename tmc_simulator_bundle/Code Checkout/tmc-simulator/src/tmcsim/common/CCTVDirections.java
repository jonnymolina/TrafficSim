package tmcsim.common;


/**
 * Enumberation containing possible CCTV camera directions.
 *
 * @author
 * @version
 */
public enum CCTVDirections {
    
    NORTH, SOUTH, EAST, WEST; 

    /**
     * Returns the CCTVDirections enumeration value which has a direction
     * value that matches the parameter value.
     * @param dir 
     * @return CCTVDirections for the parameter value.
     * @throws ScriptException if the parameter value is invalid.
     */
    public static CCTVDirections fromChar(char dir) throws ScriptException {
        switch(dir){
            case 'S':
            case 's':
                return SOUTH;
            case 'N':
            case 'n': 
                return NORTH;
            case 'E':
            case 'e':
                return EAST;
            case 'W':
            case 'w':
                return WEST;
            default:
                throw new ScriptException(ScriptException.INVALID_ENUM, dir);
        }
    }
}
