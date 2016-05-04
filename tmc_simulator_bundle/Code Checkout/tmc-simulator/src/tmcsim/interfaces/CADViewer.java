package tmcsim.interfaces;

/**
 * CADViewer specifies the behavior for classes that want to be Observers of the
 * CADSimulator.
 *
 * @author jdalbey
 */
public interface CADViewer extends java.util.Observer
{

    public void setVisible(boolean state);

    public void update(java.util.Observable obs, Object obj);
}