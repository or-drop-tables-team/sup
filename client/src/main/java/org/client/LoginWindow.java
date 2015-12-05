package org.client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.common.Utils;

import javax.swing.SpringLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.JLabel;

public class LoginWindow extends JDialog {

    private JPanel contentPane;
    private JTextField txtPassword;
    private JTextField txtUsername;
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
        
        txtPassword = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.EAST, txtPassword, -10, SpringLayout.EAST, contentPane);
        txtPassword.setToolTipText("Enter Username");
        contentPane.add(txtPassword);
        txtPassword.setColumns(10);
        
        this.txtStatusField = new JTextArea();
        sl_contentPane.putConstraint(SpringLayout.SOUTH, txtPassword, -5, SpringLayout.NORTH, this.txtStatusField);
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
        
        JLabel lblUsername = new JLabel("Username");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblUsername, 6, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblUsername, 10, SpringLayout.WEST, contentPane);
        contentPane.add(lblUsername);
        
        JLabel lblPassword = new JLabel("Password");
        sl_contentPane.putConstraint(SpringLayout.NORTH, lblPassword, 6, SpringLayout.NORTH, txtPassword);
        sl_contentPane.putConstraint(SpringLayout.WEST, lblPassword, 0, SpringLayout.WEST, lblUsername);
        contentPane.add(lblPassword);
        
        JButton btnRegister = new JButton("Register");
        sl_contentPane.putConstraint(SpringLayout.WEST, btnRegister, 139, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnRegister, 0, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnRegister, -22, SpringLayout.EAST, contentPane);
        contentPane.add(btnRegister);
                
        btnRegister.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	    if(checkEntriesValid()) {
                    System.out.println("Registering with " + getTxtUsername().getText());
                    if(clientApp.register(getTxtUsername().getText(), getTxtPassword().getText())){
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
                    if(clientApp.login(getTxtUsername().getText(), getTxtPassword().getText())) {
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
    
    protected JTextField getTxtPassword() {
    	return txtPassword;
    }
    
    protected boolean checkEntriesValid() {
        // Check if entries valid
        if(!Utils.isValidUsername(getTxtUsername().getText())) {
            this.txtStatusField.setVisible(true);
            this.txtStatusField.setText("Invalid Username");
            return false;
        }
        if(!Utils.isValidPassword(getTxtPassword().getText())) {
            this.txtStatusField.setVisible(true);
            this.txtStatusField.setText("Invalid Password");
            return false;
        }
        return true;
    }
}
