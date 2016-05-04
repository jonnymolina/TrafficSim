package tmcsim.paramicscommunicator.gui;

import java.awt.Dimension;
import java.text.DateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import tmcsim.paramicscommunicator.FileIOUpdate;
import tmcsim.paramicscommunicator.FileRegUpdate;

/**
 * ParamicsCommunicatorGUI is the view class for the ParamicsCommunicator.
 * The user interface shows a tab for each I/O reader or writer that has
 * been registered with the ParamicsCommunicator.  The tab shows the 
 * history of I/O reads and writes, and the unique information for the
 * target file and I/O interval.
 * 
 * @author Matthew Cechini
 * @version
 */
@SuppressWarnings("serial")
public class ParamicsCommunicatorGUI extends JFrame implements Observer {
    
    /**
     * Logging handler that writes all received log records to the
     * error text area.
     * @author Matthew Cechini
     */
    protected class ParamicsLoggerHandler extends Handler {

        DateFormat timeFormat = DateFormat.getTimeInstance();
        
        public void close() throws SecurityException {
        }
        
        public void flush() {
        }
        
        public void publish(LogRecord record) {
            StringBuffer errorBuf = new StringBuffer();
            
            errorBuf.append(timeFormat.format(new Date(record.getMillis())));
            errorBuf.append("  -  " + record.getMessage() + "\n");          
            
            errorTA.setText(errorTA.getText() + errorBuf.toString()); 
        }       
    }

    /** Map of FileIOTableModel objects for each reader id. */
    protected TreeMap<String, FileIOTableModel> readerTables;

    /** Map of FileIOTableModel objects for each writer id. */
    protected TreeMap<String, FileIOTableModel> writerTables;
    
    /**
     * Constructor.  Initialize local lists, set up the logging handler and
     * initialize the GUI.
     */
    public ParamicsCommunicatorGUI() {      
        super("Paramics Communicator");
        
        readerTables = new TreeMap<String, FileIOTableModel>();
        writerTables = new TreeMap<String, FileIOTableModel>();
        
        Logger.getLogger("tmcsim.paramicscommunicator").addHandler(new ParamicsLoggerHandler());
        
        initializeGUI();
    }   
    
    /**
     * Observer update method.  If the update object is a FileIOUpdate object,
     * update the TableModel corresponding to the unique I/O id.  If the
     * update object is a FileRegUpdate object, add a new tab if the 
     * registration type is REGISTER, or update an existing tab if the
     * registration type is UNREGISTER.
     */
    public void update(Observable o, Object arg) {
        
        if(arg instanceof FileIOUpdate) {

            try {
                FileIOUpdate update = (FileIOUpdate)arg;
                
                switch(update.ioType) {
                    case READ:
                        readerTables.get(update.ioID).addIOUpdate(update);
                        break;
                    case WRITE:
                        writerTables.get(update.ioID).addIOUpdate(update);
                        break;
                }
            }
            catch (Exception e) {
                Logger.getLogger("tmcsim.paramicscommunicator.gui").logp(
                        Level.SEVERE, "ParamicsCommunicatorGUI", "update", 
                        "Exception in receiving FileIOUpdate object.", e);
            }
        }
        else if(arg instanceof FileRegUpdate) {
            try {
                FileRegUpdate update = (FileRegUpdate)arg;
                
                switch(update.ioType) {
                    case READ:
                        switch(update.regType) {
                            case REGISTER:
                                FileIOTableModel model = new FileIOTableModel();
                                readerTables.put(update.ioID, model);
    
                                addTab(update, model);
                                break;
                            case UNREGISTER:
                                //unregister
                                break;
                        }
                        break;
                    case WRITE:
                        switch(update.regType) {
                            case REGISTER:
                                FileIOTableModel model = new FileIOTableModel();
                                writerTables.put(update.ioID, model);
                                
                                addTab(update, model);
                                break;
                            case UNREGISTER:
                                //unregister
                                break;
                        }                   
                        break;
                }

            }
            catch (Exception e) {
                Logger.getLogger("tmcsim.paramicscommunicator.gui").logp(
                        Level.SEVERE, "ParamicsCommunicatorGUI", "update", 
                        "Exception in receiving FileRegUpdate object.", e);
            }
        }       
    }
    
    private void initializeGUI() {
        
        /* Added by Nathaniel Lehrer */
        this.setJMenuBar(new javax.swing.JMenuBar() {
            {
                javax.swing.JMenu fileMenu = new javax.swing.JMenu("File");
                javax.swing.JMenuItem logItem = new javax.swing.JMenuItem("Show Log");
                
                logItem.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        tmcsim.paramicslog.gui.ParamicsLogGUI.getInstance().display(); 
                    }
                });
                
                fileMenu.add(logItem);
                this.add(fileMenu);
            }
        });
        /* End Add by Nathaniel Lehrer */
        
        fileIOTabs = new JTabbedPane();
        fileIOTabs.setAlignmentX(Box.CENTER_ALIGNMENT);
        fileIOTabs.setMinimumSize(new Dimension(420, 480));
        fileIOTabs.setPreferredSize(new Dimension(420, 480));
        fileIOTabs.setMaximumSize(new Dimension(420, 480));
        fileIOTabs.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createRaisedBevelBorder(), "Registered I/O"),
                    BorderFactory.createEmptyBorder(5,5,5,5)));
    
        errorTA = new JTextArea();
        errorTA.setLineWrap(true);
        
        errorPane = new JScrollPane();
        errorPane.setViewportView(errorTA);
        errorPane.setAlignmentX(Box.CENTER_ALIGNMENT);
        errorPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createRaisedBevelBorder(), "Errors"),
                    BorderFactory.createEmptyBorder(5,5,5,5)));
        
        
        Box guiBox = new Box(BoxLayout.Y_AXIS);
        guiBox.add(fileIOTabs);
        guiBox.add(Box.createVerticalStrut(10));
        guiBox.add(errorPane);
        
        add(guiBox);
        
        setMinimumSize(new Dimension(420, 680));
        setPreferredSize(new Dimension(420, 680));
        setResizable(false);
        pack();
        setVisible(true);
    }
    
    /**
     * Method creates a new tab for the new I/O object.  The tab is labeled
     * "Reader #" or "Writer #", where '#' is the I/O object's ID.
     * @param update Initial update object.
     * @param model TableModel for reader/writer that will display I/O operations.
     */
    private void addTab(FileRegUpdate update, FileIOTableModel model) {
        
        String tabName = null;
        
        switch(update.ioType) {
            case READ:
                tabName = "Reader " + update.ioID;
                break;
            case WRITE:
                tabName = "Writer " + update.ioID;
                break;
        }
        
        fileIOTabs.add(tabName, new ParamicsIOInfoPanel(update, model));
    }
    
    private JTabbedPane fileIOTabs;

    private JScrollPane errorPane;
    
    private JTextArea errorTA;

}
