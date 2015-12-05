package org.client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
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
import javax.swing.JPasswordField;

public class LoginWindow extends JDialog {

    private JPanel contentPane;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

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
        
        final JTextArea txtrUsernameUnavailable = new JTextArea();
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnLogin, 6, SpringLayout.SOUTH, txtrUsernameUnavailable);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, txtrUsernameUnavailable, -35, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtrUsernameUnavailable, 48, SpringLayout.WEST, contentPane);
        txtrUsernameUnavailable.setBackground(UIManager.getColor("Button.background"));
        txtrUsernameUnavailable.setEditable(false);
        txtrUsernameUnavailable.setVisible(false);
        contentPane.add(txtrUsernameUnavailable);
        
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
        sl_contentPane.putConstraint(SpringLayout.WEST, lblPassword, 0, SpringLayout.WEST, lblUsername);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblPassword, -11, SpringLayout.NORTH, txtrUsernameUnavailable);
        contentPane.add(lblPassword);
        
        JButton btnRegister = new JButton("Register");
        btnRegister.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		System.out.println("Registering with " + getTxtUsername().getText());
        		if(clientApp.register(getTxtUsername().getText(), getTxtPassword().getText())){
        			txtrUsernameUnavailable.setVisible(true);
        			txtrUsernameUnavailable.setText("Registration successfully");
        		}
        		else {
        			txtrUsernameUnavailable.setVisible(true);
        			txtrUsernameUnavailable.setText("Registration Failed");
        		}
        	}
        });
        sl_contentPane.putConstraint(SpringLayout.WEST, btnRegister, 139, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnRegister, 0, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnRegister, -22, SpringLayout.EAST, contentPane);
        contentPane.add(btnRegister);
        
        txtPassword = new JPasswordField();
        txtPassword.setColumns(10);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtPassword, 11, SpringLayout.SOUTH, txtUsername);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtPassword, 0, SpringLayout.WEST, txtUsername);
        contentPane.add(txtPassword);
                
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // The login button was clicked. Send the username to login.
                System.out.println("Logging in with " + getTxtUsername().getText());
                if(clientApp.login(getTxtUsername().getText(), getTxtPassword().getText())) {
                    // Then login was successful, close this window and open chat window.
                    dispose();
                }
                else {
                    txtrUsernameUnavailable.setVisible(true);
                    txtrUsernameUnavailable.setText("Login Failed");
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
    protected JTextField getTxtPassword() {
    	return txtPassword;
    }
}
