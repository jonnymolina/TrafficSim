package tmcsim.client.cadclientgui.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * The main CADMenu with buttons to open up other screens. This class uses
 * mostly images to build from, and java gui does not allow those images to be
 * editable here, so all the images' pixels are predefined in the images
 * themselves. The images can be found in the images folder. An image called
 * "CADMenuLayout" can also be found in the same folder showing how this class's
 * looks were made.
 * 
 * @author Vincent
 * 
 */
public class CADMenu extends JFrame {

    private final int ONE_SECOND = 1000;

    private DateFormat dateFormat;

    private Box mainPanel;
    private Box bottomRightIcons;
    private Box bottomRightButtonsGrayed;
    private Box bottomRightButtonsColored;
    private JLabel dateAndTime;
    private JLabel name;
    private JLabel userListName1;

    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JButton button6;
    private JButton buttonTool;
    private JButton buttonMore;
    
    // the drop down menu when the "more button" is clicked.
    private JFrame moreMenu;

    // the drop down menu when the "tool button" is clicked.
    private JFrame toolMenu;

    private JButton buttonCheckmark;
    private JButton buttonMinus;

    private JLabel position;

    /**
     * Constructor call. Creates the CADMenu.
     */
    public CADMenu() {
        initialize();
        createTopPanel();
        createBottomPanel();
        createDropDownMenus();
        
        initControllers();
        
        getContentPane().add(mainPanel);

        setTitle("Inform CAD");
        setPreferredSize(new Dimension(1195, 178));
        setResizable(true);
        setFocusable(true);
        pack();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(false);
    }

    /**
     * Initializes variables and connects the CADMenu to the CADMenuListener.
     */
    public void initialize() {
        mainPanel = new Box(BoxLayout.Y_AXIS);
        mainPanel.setAlignmentX(LEFT_ALIGNMENT);
        dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    }

    public void addListeners() {
        addComponentListener(new ComponentListener() {
            public void componentHidden(ComponentEvent e) {
            }

            public void componentMoved(ComponentEvent e) {
                closeToolMenu();
                closeMoreMenu();
                setLocation(getLocation());
            }

            public void componentResized(ComponentEvent e) {
            }

            public void componentShown(ComponentEvent e) {
            }
        });
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent arg0) {
                closeToolMenu();
                closeMoreMenu();
            }

            public void mouseEntered(MouseEvent arg0) {
            }

            public void mouseExited(MouseEvent arg0) {
            }

            public void mousePressed(MouseEvent arg0) {
            }

            public void mouseReleased(MouseEvent arg0) {
            }
        });
    }
    
    public void initToolMenuListeners(){
        toolMenu.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
            }

            public void mouseMoved(MouseEvent e) {
            }
        });

        toolMenu.addMouseListener(new MouseListener() {
            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
            }
        });
    }
    
    public void initMoreMenuListeners(){
        moreMenu.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
            }

            public void mouseMoved(MouseEvent e) {
                ImageIcon image = new ImageIcon(
                        "images/MoreMenuImages/moreMenu.png");
                JLabel menu;
                if (e.getLocationOnScreen().getY() >= moreMenu.getY()
                        && e.getLocationOnScreen().getY() <= moreMenu.getY()
                                + moreMenu.getHeight() / 8) {
                    image = new ImageIcon(
                            "images/MoreMenuImages/moreMenuHighlighted1.png");
                } else if (e.getLocationOnScreen().getY() >= moreMenu.getY()
                        + moreMenu.getHeight() * 2 / 8
                        && e.getLocationOnScreen().getY() <= moreMenu.getY()
                                + moreMenu.getHeight() * 3 / 8) {
                    image = new ImageIcon(
                            "images/MoreMenuImages/moreMenuHighlighted3.png");
                }
                menu = new JLabel(image);
                moreMenu.getContentPane().removeAll();
                moreMenu.getContentPane().add(menu);
                moreMenu.validate();
            }
        });

        moreMenu.addMouseListener(new MouseListener() {
            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
                if (e.getLocationOnScreen().getY() >= moreMenu.getY()
                        && e.getLocationOnScreen().getY() <= moreMenu.getY()
                                + moreMenu.getHeight() / 8) {
                    ScreenManager.openIncidentEditor();
                    closeMoreMenu();
                } else if (e.getLocationOnScreen().getY() >= moreMenu.getY()
                        + moreMenu.getHeight() * 2 / 8
                        && e.getLocationOnScreen().getY() <= moreMenu.getY()
                                + moreMenu.getHeight() * 3 / 8) {
                    ScreenManager.openCardfile();
                    closeMoreMenu();
                }
            }
        });
    }
    
    public void initControllers(){
        addListeners();

        buttonTool.addActionListener(newToolActionListener());
        buttonMore.addActionListener(newMoreActionListener());
        
        initToolMenuListeners();
        initMoreMenuListeners();    
    }

    public ActionListener newToolActionListener(){
        return new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (moreMenu.isVisible()) {
                    closeMoreMenu();
                }
                if (toolMenu.isVisible()) {
                    closeToolMenu();
                } else {
                    toolMenu.setLocation(new Point(getX() + getWidth()
                            - toolMenu.getWidth(), getY() + 85));
                    toolMenu.setVisible(true);
                }
            }
        };
    }
    
    public ActionListener newMoreActionListener(){
        return new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (toolMenu.isVisible()) {
                    closeToolMenu();
                }
                if (moreMenu.isVisible()) {
                    closeMoreMenu();
                } else {
                    moreMenu.setLocation(new Point(getX() + getWidth()
                            - moreMenu.getWidth(), getY() + 85));
                    moreMenu.setVisible(true);
                }
            }
        };
    }
    
    /*
     * Creates the topPanel for the CADMenu.
     */
    public void createTopPanel() {

        Box topPanel = new Box(BoxLayout.X_AXIS);
        topPanel.setAlignmentX(LEFT_ALIGNMENT);

        ImageIcon image = new ImageIcon("images/CADMenuImages/tritech.png");
        JLabel tritech = new JLabel(image);
        topPanel.add(tritech);

        image = new ImageIcon("images/CADMenuImages/empty.png");
        JLabel empty = new JLabel(image);
        topPanel.add(empty);

        // center holds the User's Name, user's position, and date/time
        Box center = new Box(BoxLayout.Y_AXIS);
        image = new ImageIcon("images/CADMenuImages/top.png");

        name = new JLabel(image);
        name.setText("User");
        name.setFont(new Font("SanSerif", Font.BOLD, 16));
        name.setForeground(Color.WHITE);
        name.setHorizontalTextPosition(JLabel.CENTER);
        center.add(name);

        Box centerBottom = new Box(BoxLayout.X_AXIS);
        centerBottom.setAlignmentX(Box.LEFT_ALIGNMENT);

        image = new ImageIcon("images/CADMenuImages/bottom.png");
        position = new JLabel(image);
        position.setText("Public Safety Dispatch Sup");
        position.setFont(new Font("Arial", Font.PLAIN, 12));
        position.setForeground(new Color(190, 210, 255));
        position.setHorizontalTextPosition(JLabel.CENTER);
        centerBottom.add(position);

        image = new ImageIcon("images/CADMenuImages/bottom.png");
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String dateTime = dateFormat.format(date);
        dateAndTime = new JLabel(image);
        dateAndTime.setText(dateTime);
        dateAndTime.setForeground(Color.WHITE);
        dateAndTime.setHorizontalTextPosition(JLabel.CENTER);
        centerBottom.add(dateAndTime);
        center.add(centerBottom);

        topPanel.add(center);

        image = new ImageIcon("images/CADMenuImages/empty2.png");
        JLabel empty2 = new JLabel(image);
        topPanel.add(empty2);

        image = new ImageIcon("images/CADMenuImages/button1.png");
        button1 = makeButton(image);
        topPanel.add(button1);

        image = new ImageIcon("images/CADMenuImages/button2.png");
        button2 = makeButton(image);
        topPanel.add(button2);

        image = new ImageIcon("images/CADMenuImages/button3.png");
        button3 = makeButton(image);
        topPanel.add(button3);

        image = new ImageIcon("images/CADMenuImages/button4.png");
        button4 = makeButton(image);
        topPanel.add(button4);

        image = new ImageIcon("images/CADMenuImages/button5.png");
        button5 = makeButton(image);
        topPanel.add(button5);

        image = new ImageIcon("images/CADMenuImages/button6.png");
        button6 = makeButton(image);
        topPanel.add(button6);

        image = new ImageIcon("images/CADMenuImages/button7.png");
        buttonTool = makeButton(image);
        topPanel.add(buttonTool);

        image = new ImageIcon("images/CADMenuImages/button8.png");
        buttonMore = makeButton(image);
        topPanel.add(buttonMore);

        mainPanel.add(topPanel);
    }

    /**
     * Creates the bottomPanel of the CADMenu.
     */
    public void createBottomPanel() {
        Color grayBackground = new Color(100, 100, 100);

        Box userList = new Box(BoxLayout.Y_AXIS);
        userList.setAlignmentX(LEFT_ALIGNMENT);

        Dimension size = new Dimension(400, 85);
        userList.setPreferredSize(size);
        userList.setMaximumSize(size);
        userList.setMinimumSize(size);
        userList.setBackground(grayBackground);
        userList.setOpaque(true);

        Box user1 = new Box(BoxLayout.X_AXIS);
        user1.setAlignmentX(LEFT_ALIGNMENT);

        ImageIcon image = new ImageIcon("images/CADMenuImages/mailClose.png");
        JLabel userListIcon1 = new JLabel(image);
        userListName1 = new JLabel("User");
        userListName1.setForeground(Color.WHITE);
        userListName1.setBackground(grayBackground);
        userListName1.setOpaque(true);
        JLabel userListNumbers = new JLabel(
                "<html><font color=white>(</font><font color=red>0</font><font color=white>,0,0)</font></html>");
        userListNumbers.setBackground(grayBackground);
        userListNumbers.setOpaque(true);

        user1.add(userListIcon1);
        user1.add(userListName1);
        user1.add(userListNumbers);
        userList.add(user1);

        Box user2 = new Box(BoxLayout.X_AXIS);
        user2.setAlignmentX(LEFT_ALIGNMENT);

        image = new ImageIcon("images/CADMenuImages/mailClose.png");
        JLabel userListIcon2 = new JLabel(image);
        JLabel userListName2 = new JLabel("  SL007  ");
        userListName2.setForeground(Color.WHITE);
        userListName2.setBackground(grayBackground);
        userListName2.setOpaque(true);
        userListNumbers = new JLabel(
                "<html><font color=white>(</font><font color=red>0</font><font color=white>,0,0)</font></html>");
        userListNumbers.setBackground(grayBackground);
        userListNumbers.setOpaque(true);

        user2.add(userListIcon2);
        user2.add(userListName2);
        user2.add(userListNumbers);
        userList.add(user2);

        JLabel currentAgency = new JLabel("  Current Agency is CHP");
        currentAgency.setForeground(Color.WHITE);
        currentAgency.setBackground(grayBackground);
        currentAgency.setOpaque(true);
        currentAgency.setHorizontalTextPosition(JLabel.LEFT);
        userList.add(currentAgency);

        Box bottomPanel = new Box(BoxLayout.X_AXIS);
        bottomPanel.setAlignmentX(LEFT_ALIGNMENT);
        bottomPanel.add(userList);
        bottomPanel.add(Box.createHorizontalStrut(5));

        Box bottomRight = new Box(BoxLayout.Y_AXIS);
        size = new Dimension(775, 85);
        bottomRight.setPreferredSize(size);
        bottomRight.setMaximumSize(size);
        bottomRight.setMinimumSize(size);
        image = new ImageIcon("images/CADMenuImages/grayFillerTop.png");
        bottomRight.add(new JLabel(image));

        bottomRightIcons = new Box(BoxLayout.X_AXIS);
        bottomRightIcons.setAlignmentX(LEFT_ALIGNMENT);
        image = new ImageIcon("images/CADMenuImages/grayFillerBottom.png");
        bottomRightIcons.add(new JLabel(image));

        createBottomRightButtons();
        bottomRightIcons.add(bottomRightButtonsGrayed);
        bottomRight.add(bottomRightIcons);
        bottomPanel.add(bottomRight);

        mainPanel.add(bottomPanel);
    }

    /**
     * Creates the two bottom right buttons.
     */
    public void createBottomRightButtons() {
        bottomRightButtonsGrayed = new Box(BoxLayout.X_AXIS);
        ImageIcon image = new ImageIcon(
                "images/CADMenuImages/checkmarkGrey.png");
        buttonCheckmark = makeButton(image);
        bottomRightButtonsGrayed.add(buttonCheckmark);
        image = new ImageIcon("images/CADMenuImages/minusGrey.png");

        buttonMinus = makeButton(image);
        bottomRightButtonsGrayed.add(buttonMinus);

        bottomRightButtonsColored = new Box(BoxLayout.X_AXIS);
        image = new ImageIcon("images/CADMenuImages/checkmarkGreen.png");
        buttonCheckmark = makeButton(image);
        bottomRightButtonsColored.add(buttonCheckmark);
        image = new ImageIcon("images/CADMenuImages/minusRed.png");

        buttonMinus = makeButton(image);
        bottomRightButtonsColored.add(buttonMinus);
    }

    /**
     * This method creates the drop down menus for both the tool and more
     * buttons
     */
    public void createDropDownMenus() {

        ImageIcon image = new ImageIcon("images/ToolMenuImages/toolMenu.png");
        JLabel menu = new JLabel(image);
        toolMenu = new JFrame();
        toolMenu.getContentPane().add(menu);
        toolMenu.setUndecorated(true);
        toolMenu.pack();
        toolMenu.setVisible(false);

        image = new ImageIcon("images/MoreMenuImages/moreMenu.png");
        menu = new JLabel(image);
        moreMenu = new JFrame();
        moreMenu.getContentPane().add(menu);
        moreMenu.setUndecorated(true);
        moreMenu.pack();
        moreMenu.setVisible(false);
    }

    /**
     * Takes in the name of the user and displays it.
     */
    public void setName(String username) {
        name.setText(username);
        userListName1.setText(username);
    }

    /**
     * Factory method. Makes a JButton with an image and listener.
     * 
     * @param image
     *            the image this button will display.
     * @param listener
     *            the action listener for this button.
     * @return the JButton.
     */
    public JButton makeButton(ImageIcon image) {
        JButton button = new JButton(image);
        Dimension size = new Dimension(image.getImage().getWidth(null), image
                .getImage().getHeight(null));
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
        button.setBorderPainted(false);
        button.setFocusable(false);

        return button;
    }

    /**
     * Hides the tool drop down menu. Also resets the image to the default(no
     * highlighted rows).
     */
    public void closeToolMenu() {
        ImageIcon image;
        JLabel menu;
        image = new ImageIcon("images/ToolMenuImages/toolMenu.png");
        menu = new JLabel(image);
        toolMenu.getContentPane().removeAll();
        toolMenu.getContentPane().add(menu);
        toolMenu.validate();
        toolMenu.setVisible(false);
    }

    /**
     * Hides the more drop down menu. Also resets the image to the default(no
     * highlighted rows).
     */
    public void closeMoreMenu() {
        ImageIcon image;
        JLabel menu;
        image = new ImageIcon("images/MoreMenuImages/moreMenu.png");
        menu = new JLabel(image);
        moreMenu.getContentPane().removeAll();
        moreMenu.getContentPane().add(menu);
        moreMenu.validate();
        moreMenu.setVisible(false);
    }

    /**
     * This method is called every second in ScreenManger to update the display
     * time every second.
     */
    public void handleUpdateTime() {
        Date date = new Date();
        String dateTime = dateFormat.format(date);
        dateAndTime.setText(dateTime);
    }

    /**
     * Makes screen visible.
     */
    public void open() {
        setVisible(true);
    }

    /**
     * Hides screen.
     */
    public void close() {
        setVisible(false);
    }

    /**
     * Currently not used.
     */
    public void setIncomingIncident() {
        bottomRightIcons.remove(bottomRightButtonsGrayed);
        bottomRightIcons.add(bottomRightButtonsColored);
        revalidate();
        repaint();
    }

    /**
     * Currently not used.
     */
    public void endIncomingIncident() {
        bottomRightIcons.remove(bottomRightButtonsColored);
        bottomRightIcons.add(bottomRightButtonsGrayed);
        revalidate();
        repaint();
    }

    /**
     * Sets position title to trainee.
     */
    public void removeDispatcherStatus() {
        position.setText("Trainee");
    }

}
