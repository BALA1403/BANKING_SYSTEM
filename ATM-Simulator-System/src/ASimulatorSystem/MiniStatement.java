package ASimulatorSystem;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class MiniStatement extends JFrame implements ActionListener {
 
    JButton exitButton;
    JLabel statementLabel, bankLabel, cardLabel, balanceLabel;
    
    MiniStatement(String pin) {
        super("Mini Statement");
        
        // Window settings
        setSize(400, 600);
        setLocation(20, 20);
        getContentPane().setBackground(Color.WHITE);
        
        // Initialize labels
        statementLabel = new JLabel();
        bankLabel = new JLabel("Indian Bank");
        cardLabel = new JLabel();
        balanceLabel = new JLabel();
        
        // Set up labels
        bankLabel.setBounds(150, 20, 100, 20);
        cardLabel.setBounds(20, 80, 300, 20);
        statementLabel.setBounds(20, 140, 350, 300); // Adjust width for better readability
        balanceLabel.setBounds(20, 400, 300, 20);
        
        // Add labels to frame
        add(bankLabel);
        add(cardLabel);
        add(statementLabel);
        add(balanceLabel);

        // Button for exit
        exitButton = new JButton("Exit");
        exitButton.setBounds(20, 500, 100, 25);
        exitButton.addActionListener(this);
        add(exitButton);
        
        setLayout(null);

        // Fetch and display card number
        try {
            Conn c = new Conn();
            String query = "SELECT cardnumber FROM login WHERE pin = ?";
            PreparedStatement ps = c.c.prepareStatement(query);
            ps.setString(1, pin);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String cardNo = rs.getString("cardnumber");
                cardLabel.setText("Card Number:    " + cardNo.substring(0, 4) + "XXXXXXXX" + cardNo.substring(12));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Fetch and display transaction history and balance
        try {
            int balance = 0;
            StringBuilder statementText = new StringBuilder();
            Conn c1 = new Conn();
            String query = "SELECT date, type, amount FROM bank WHERE pin = ?";
            PreparedStatement ps = c1.c.prepareStatement(query);
            ps.setString(1, pin);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String date = rs.getString("date");
                String mode = rs.getString("type");
                String amount = rs.getString("amount");
                
                statementText.append("<html>").append(date)
                        .append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
                        .append(mode)
                        .append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
                        .append(amount)
                        .append("<br><br></html>");
                
                if ("Deposit".equals(mode)) {
                    balance += Integer.parseInt(amount);
                } else {
                    balance -= Integer.parseInt(amount);
                }
            }
            statementLabel.setText("<html>" + statementText.toString() + "</html>");
            balanceLabel.setText("Your total Balance is Rs " + balance);
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        this.setVisible(false);
    }
    
    public static void main(String[] args) {
        new MiniStatement("").setVisible(true);
    }
}
