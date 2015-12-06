package org.client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.common.Utils;

import javax.swing.SpringLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

public class LoginWindow extends JDialog {

    private JPanel contentPane;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    protected JTextArea txtStatusField;

    /**
     * Create the frame.
     * @param clientApp 
     */
    public LoginWindow(final ClientApp clientApp) {
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(100, 100, 250, 150);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        SpringLayout sl_contentPane = new SpringLayout();
        contentPane.setLayout(sl_contentPane);
        
        JButton btnLogin = new JButton("Login");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnLogin, 26, SpringLayout.WEST, contentPane);
        contentPane.add(btnLogin);
       

        JButton btnRegister = new JButton("Register");
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnRegister, 0, SpringLayout.NORTH, btnLogin);
        sl_contentPane.putConstraint(SpringLayout.WEST, btnRegister, 138, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnRegister, -23, SpringLayout.EAST, contentPane);
        contentPane.add(btnRegister);

        this.txtStatusField = new JTextArea();
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnLogin, 6, SpringLayout.SOUTH, this.txtStatusField);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, this.txtStatusField, -35, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, this.txtStatusField, 48, SpringLayout.WEST, contentPane);
        this.txtStatusField.setBackground(UIManager.getColor("Button.background"));
        this.txtStatusField.setEditable(false);
        this.txtStatusField.setVisible(false);
        contentPane.add(this.txtStatusField);
       
        this.getRootPane().setDefaultButton(btnLogin);
        
        txtUsername = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.EAST, txtUsername, -10, SpringLayout.EAST, contentPane);
        contentPane.add(txtUsername);
        txtUsername.setColumns(10);
        
        txtPassword = new JPasswordField();
        txtPassword.setColumns(10);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtPassword, 11, SpringLayout.SOUTH, txtUsername);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtPassword, 0, SpringLayout.WEST, txtUsername);
        contentPane.add(txtPassword);
 
        JLabel lblUsername = new JLabel("Username");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblUsername, 6, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblUsername, 10, SpringLayout.WEST, contentPane);
        contentPane.add(lblUsername);
        
        JLabel lblPassword = new JLabel("Password");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblPassword, 0, SpringLayout.WEST, lblUsername);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblPassword, -11, SpringLayout.NORTH, this.txtStatusField);
        contentPane.add(lblPassword);
       
        btnRegister.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	    if(checkEntriesValid()) {
                    System.out.println("Registering with " + getTxtUsername().getText());
                    if(clientApp.register(getTxtUsername().getText(), new String(getTxtPassword().getPassword()))){
                        txtStatusField.setVisible(true);
                        txtStatusField.setText("Registration Successfull");
                    }
                    else {
                        txtStatusField.setVisible(true);
                        txtStatusField.setText("Registration Failed");
                    }
        	    }
        	}
        });

        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // The login button was clicked. Send the username to login.
                if(checkEntriesValid()) {
                    System.out.println("Logging in with " + getTxtUsername().getText());
                    if(clientApp.login(getTxtUsername().getText(), new String(getTxtPassword().getPassword()))) {
                        // Then login was successful, close this window and open chat window.
                        dispose();
                    }
                    else {
                        txtStatusField.setVisible(true);
                        txtStatusField.setText("Login Failed");
                    }
                }
            }
        });
    }

    /**
     * Get a reference to the text field.
     * @return
     */
    protected JTextField getTxtUsername() {
        return txtUsername;
    }
    
    /**
     * Get a reference to the password field
     * @return
     */
    protected JPasswordField getTxtPassword() {
    	return txtPassword;
    }
    
    /**
     * Do validation of the username and password fields to ensure they don't contain any
     * invalid characters. If they do, set the status field appropriately. Note that this
     * doesn't mean the login/registration was successful, just that the fields are valid
     * and can be passed to the server.
     * 
     * @return true if valid, false if not
     */
    protected boolean checkEntriesValid() {
        // Check if entries valid
        if(!Utils.isValidUsername(getTxtUsername().getText())) {
            this.txtStatusField.setVisible(true);
            this.txtStatusField.setText("Invalid Username");
            return false;
        }
        if(!Utils.isValidPassword(new String(getTxtPassword().getPassword()))) {
            this.txtStatusField.setVisible(true);
            this.txtStatusField.setText("Invalid Password");
            return false;
        }
        return true;
    }
}
