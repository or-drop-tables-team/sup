package org.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.common.TokenPair;
import org.common.Utils;

import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;

public class ChatWindow extends JFrame {

    private JPanel contentPane;
    private JTextField msgEntry;
    private JTextArea chatBox;

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
                        getChatBox().setText(getChatBox().getText() + "\n" + tokens[0] + ": " + tokens[1]);
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
        
        JScrollBar scrollBar = new JScrollBar();
        sl_contentPane.putConstraint(SpringLayout.NORTH, scrollBar, 0, SpringLayout.NORTH, chatBox);
        sl_contentPane.putConstraint(SpringLayout.WEST, scrollBar, 6, SpringLayout.EAST, chatBox);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollBar, -5, SpringLayout.SOUTH, chatBox);
        contentPane.add(scrollBar);
        
        
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
}
