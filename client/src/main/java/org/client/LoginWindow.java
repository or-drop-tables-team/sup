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

public class LoginWindow extends JDialog {

    private JPanel contentPane;
    private JTextField txtUsername;

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
        sl_contentPane.putConstraint(SpringLayout.WEST, btnLogin, 79, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnLogin, -10, SpringLayout.SOUTH, contentPane);
        contentPane.add(btnLogin);
        
        txtUsername = new JTextField();
        txtUsername.setToolTipText("Enter Username");
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtUsername, 23, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtUsername, 48, SpringLayout.WEST, contentPane);
        contentPane.add(txtUsername);
        txtUsername.setColumns(10);
        
        final JTextArea txtrUsernameUnavailable = new JTextArea();
        txtrUsernameUnavailable.setBackground(UIManager.getColor("Button.background"));
        txtrUsernameUnavailable.setEditable(false);
        sl_contentPane.putConstraint(SpringLayout.NORTH, txtrUsernameUnavailable, 6, SpringLayout.SOUTH, txtUsername);
        sl_contentPane.putConstraint(SpringLayout.WEST, txtrUsernameUnavailable, 0, SpringLayout.WEST, txtUsername);
        txtrUsernameUnavailable.setText("Username Unavailable");
        txtrUsernameUnavailable.setVisible(false);
        contentPane.add(txtrUsernameUnavailable);
        
        this.getRootPane().setDefaultButton(btnLogin);
                
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // The login button was clicked. Send the username to login.
                System.out.println("Logging in with " + getTxtUsername().getText());
                if(clientApp.login(getTxtUsername().getText())) {
                    // Then login was successful, close this window and open chat window.
                    dispose();
                }
                else {
                    txtrUsernameUnavailable.setVisible(true);
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
}
