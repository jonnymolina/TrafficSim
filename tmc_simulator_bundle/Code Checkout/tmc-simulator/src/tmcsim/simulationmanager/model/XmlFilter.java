

package tmcsim.simulationmanager.model;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * FileFilter used to select only .xml files.
 * 
 * @author Matthew
 * @version
 */
public class XmlFilter extends FileFilter{
    
    
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            String extension = s.substring(i+1).toLowerCase();
            if (extension.equals("xml")) {
                return true;
            }
        }
        return false;
    }

    /** 
     * Get description for display in file chooser. 
     * @return "XML Files (.xml)"
     */
    public String getDescription() {
        return "XML Files (.xml)";
    }
}
