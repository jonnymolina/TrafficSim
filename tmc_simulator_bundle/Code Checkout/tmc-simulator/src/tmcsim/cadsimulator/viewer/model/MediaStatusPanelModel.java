package tmcsim.cadsimulator.viewer.model;

import java.util.Observable;
import tmcsim.cadsimulator.videocontrol.DVDStatusUpdate;
import tmcsim.cadsimulator.videocontrol.DVDTitleUpdate;

/**
 * MediaStatusPanelModel represents the status of the media panel for the CADSimulator.
 * @author Jonathan Molina
 */
public class MediaStatusPanelModel extends Observable
{
    /**
     * Updates the current DVDInfoPanel with the status update.
     * If a panel does not current exist, create one and add
     * a new tab.
     * 
     * @param update DVD status update.
     */
    public void updateDVDStatus(DVDStatusUpdate update)
    {
        setChanged();
        notifyObservers();
    }
    
    /**
     * Updates the current DVDInfoPanel with the title update.
     * If a panel does not current exist, create one and add
     * a new tab.
     * 
     * @param update DVD title update.
     */
    public void updateDVDTitle(DVDTitleUpdate update)
    {
        setChanged();
        notifyObservers();
    }
}
