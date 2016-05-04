package tmcsim.client.cadclientgui.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


/**
 * This class contains the view and controller for the Login screen. The view was not built using a GUI builder plug-in
 * (but may want to consider doing so in the future), and the controller uses listeners to control how the view and data act. 
 * 
 * @author Vincent
 */

public class Login extends JFrame {

    private Box login;
    private JTextField userNameField;
    private JTextField passwordField;

    public Login() {
        initView();
    }
    
    private ActionListener newEnterActionListener(){
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                ScreenManager.setUserName(userNameField.getText());
                ScreenManager.openCADMenu();
                ScreenManager.openAssignedIncidents();
                ScreenManager.openUnitStatus();
                ScreenManager.openPendingIncidents();
                ScreenManager.openPowerlineUI();
                if (!passwordField.getText().equals("Dispatcher")) {
                    ScreenManager.setDispatcherAuthority(false);
                }
            }
        };
    }
    
    private ActionListener newExitActionListener(){
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
    }
    
    private void initView(){
        login = new Box(BoxLayout.Y_AXIS);
        login.setAlignmentX(LEFT_ALIGNMENT);
        JPanel whiteBackground = new JPanel();
        whiteBackground.setAlignmentX(LEFT_ALIGNMENT);
        whiteBackground.setBackground(Color.WHITE);
        whiteBackground.setMaximumSize(new Dimension(750, 150));
        whiteBackground.setMinimumSize(new Dimension(750, 150));
        whiteBackground.setPreferredSize(new Dimension(750, 150));
        login.add(whiteBackground);

        JPanel blueBackground = new JPanel();
        blueBackground.setAlignmentX(LEFT_ALIGNMENT);
        blueBackground.setBackground(new Color(50, 150, 200));
        blueBackground.setMaximumSize(new Dimension(750, 350));
        blueBackground.setMinimumSize(new Dimension(750, 350));
        blueBackground.setPreferredSize(new Dimension(750, 350));

        JLabel title = new JLabel();
        title.setText("Inform CAD 5.3 Patch 5");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.PLAIN, 22));
        title.setForeground(Color.WHITE);
        title.setMaximumSize(new Dimension(750, 100));
        title.setMinimumSize(new Dimension(750, 100));
        title.setPreferredSize(new Dimension(750, 100));
        blueBackground.add(title);

        Box leftBox = new Box(BoxLayout.Y_AXIS);
        leftBox.setMaximumSize(new Dimension(350, 50));
        leftBox.setMinimumSize(new Dimension(350, 50));
        leftBox.setPreferredSize(new Dimension(350, 50));
        leftBox.setAlignmentX(LEFT_ALIGNMENT);

        JLabel userNameLabel = new JLabel("User name ");
        userNameLabel.setForeground(Color.WHITE);
        userNameField = new JTextField("Enter your name");
        Box userNameBox = new Box(BoxLayout.X_AXIS);
        userNameBox.setAlignmentX(LEFT_ALIGNMENT);
        userNameBox.add(Box.createHorizontalStrut(10));
        userNameBox.add(userNameLabel);
        userNameBox.add(userNameField);
        leftBox.add(userNameBox);

        JLabel passwordLabel = new JLabel("Password  ");
        passwordLabel.setForeground(Color.WHITE);
        passwordField = new JTextField("Does not matter");
        Box passwordBox = new Box(BoxLayout.X_AXIS);
        passwordBox.setAlignmentX(LEFT_ALIGNMENT);
        passwordBox.add(Box.createHorizontalStrut(10));
        passwordBox.add(passwordLabel);
        passwordBox.add(passwordField);
        leftBox.add(Box.createVerticalStrut(10));
        leftBox.add(passwordBox);

        JButton enter = new JButton("Login");
        enter.addActionListener(newEnterActionListener());
        JButton newPassword = new JButton("New Password");
        newPassword.setEnabled(false);
        JButton exit = new JButton("Exit");
        exit.addActionListener(newExitActionListener());
        JButton selectAll = new JButton("Select All");
        selectAll.setEnabled(false);
        JButton unselectAll = new JButton("Unselect all");
        unselectAll.setEnabled(false);
        Box buttonBox = new Box(BoxLayout.X_AXIS);
        buttonBox.setAlignmentX(LEFT_ALIGNMENT);
        buttonBox.add(Box.createHorizontalStrut(75));
        buttonBox.add(enter);
        buttonBox.add(Box.createHorizontalStrut(20));
        buttonBox.add(newPassword);
        buttonBox.add(Box.createHorizontalStrut(20));
        buttonBox.add(exit);
        buttonBox.add(Box.createHorizontalStrut(125));
        buttonBox.add(selectAll);
        buttonBox.add(Box.createHorizontalStrut(10));
        buttonBox.add(unselectAll);

        Box rightBox = new Box(BoxLayout.Y_AXIS);
        rightBox.setAlignmentX(LEFT_ALIGNMENT);
        rightBox.setMaximumSize(new Dimension(250, 90));
        rightBox.setMinimumSize(new Dimension(250, 90));
        rightBox.setPreferredSize(new Dimension(250, 90));
        JLabel position = new JLabel("Positions:");
        position.setPreferredSize(new Dimension(250, 20));
        position.setMaximumSize(new Dimension(250, 20));
        position.setMinimumSize(new Dimension(250, 20));
        position.setBackground(Color.BLACK);
        position.setHorizontalAlignment(SwingConstants.LEFT);
        position.setForeground(Color.WHITE);
        rightBox.add(position);
        JPanel list = new JPanel();
        rightBox.add(list);

        Box centerBox = new Box(BoxLayout.X_AXIS);
        centerBox.setAlignmentX(LEFT_ALIGNMENT);
        centerBox.add(leftBox);
        centerBox.add(Box.createHorizontalStrut(75));
        centerBox.add(rightBox);
        blueBackground.add(centerBox);
        blueBackground.add(Box.createVerticalStrut(125));
        blueBackground.add(buttonBox);
        login.add(blueBackground);

        getContentPane().add(login);
        setUndecorated(true);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
}
