package org.client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import java.awt.Color;

public class ChatWindow extends JFrame {

    private JPanel contentPane;
    private JTextField msgEntry;
    private JTextArea chatBox;
    private JLabel errorMessage;
    private JScrollPane scrollPane;

    /**
     * Create the frame.
     * @param clientApp 
     */
    public ChatWindow(final ClientApp clientApp) {
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        SpringLayout sl_contentPane = new SpringLayout();
        contentPane.setLayout(sl_contentPane);
        
        JButton btnSend = new JButton("Send");
        
        // Set the default button as "Send" to realize the function of pressing "Enter" button
        this.getRootPane().setDefaultButton(btnSend);

        
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // For now, assume the first word is the desination user.
                String entry = getMsgEntry().getText();
                String[] tokens = entry.split(":", 2);
                if(tokens.length < 2) {
                    // Show an error message
                    // TODO. for now just println.
                    System.out.println("Invalid message entry: " + entry);
                }
                else {
                    if(clientApp.sendChatMessage(tokens[0], tokens[1])) {
                        // Append to our history, too.
                        getChatBox().setText(getChatBox().getText() + "\n" + tokens[0] + " <- " + tokens[1]);
                    }
                }
                // Either way, clear the entry.
                getMsgEntry().setText("");
            }
        });
        
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnSend, -6, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnSend, 0, SpringLayout.EAST, contentPane);
        contentPane.add(btnSend);
        
        msgEntry = new JTextField();
        sl_contentPane.putConstraint(SpringLayout.NORTH, msgEntry, 217, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, msgEntry, -5, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, btnSend, 4, SpringLayout.NORTH, msgEntry);
        sl_contentPane.putConstraint(SpringLayout.WEST, btnSend, 12, SpringLayout.EAST, msgEntry);
        sl_contentPane.putConstraint(SpringLayout.WEST, msgEntry, 10, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, msgEntry, -139, SpringLayout.EAST, contentPane);
        contentPane.add(msgEntry);
        msgEntry.setColumns(10);
        
        chatBox = new JTextArea();
        chatBox.setEditable(false);
        sl_contentPane.putConstraint(SpringLayout.NORTH, chatBox, 25, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, chatBox, 25, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, chatBox, -6, SpringLayout.NORTH, msgEntry);
        sl_contentPane.putConstraint(SpringLayout.EAST, chatBox, -24, SpringLayout.EAST, contentPane);
        contentPane.add(chatBox);
        chatBox.setColumns(10);
        
        scrollPane = new JScrollPane(chatBox);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(430, 200));
        scrollPane.setAutoscrolls(true);
        contentPane.add(scrollPane);
        
        errorMessage = new JLabel("");
        errorMessage.setForeground(Color.RED);
        sl_contentPane.putConstraint(SpringLayout.NORTH, errorMessage, 5, SpringLayout.SOUTH, scrollPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, errorMessage, 20, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, errorMessage, -2, SpringLayout.NORTH, msgEntry);
        sl_contentPane.putConstraint(SpringLayout.EAST, errorMessage, -20, SpringLayout.EAST, contentPane);
        contentPane.add(errorMessage);
        
                
    }
    
    /**
     * Get reference to the chat window box.
     * 
     * @return
     */
    public JTextArea getChatBox() {
        return chatBox;
    }
    
    /**
     * Get reference to the message entry area.
     * 
     * @return
     */
    public JTextField getMsgEntry() {
        return msgEntry;
    }
    
    /**
     * Get reference to the error message label.
     * 
     * @return
     */
    public JLabel getErrorMessage() {
    	return errorMessage;
    }
}
