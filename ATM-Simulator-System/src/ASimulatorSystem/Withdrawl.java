package ASimulatorSystem;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Date;
import java.sql.*;
import java.text.SimpleDateFormat;

public class Withdrawl extends JFrame implements ActionListener {
    
    JTextField t1;
    JButton b1, b2;
    JLabel l1, l2;
    String pin;

    Withdrawl(String pin) {
        this.pin = pin;
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("ASimulatorSystem/icons/atm.jpg"));
        Image i2 = i1.getImage().getScaledInstance(1000, 1180, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel l3 = new JLabel(i3);
        l3.setBounds(0, 0, 960, 1080);
        add(l3);

        l1 = new JLabel("MAXIMUM WITHDRAWAL IS RS.10,000");
        l1.setForeground(Color.WHITE);
        l1.setFont(new Font("System", Font.BOLD, 16));

        l2 = new JLabel("PLEASE ENTER YOUR AMOUNT");
        l2.setForeground(Color.WHITE);
        l2.setFont(new Font("System", Font.BOLD, 16));

        t1 = new JTextField();
        t1.setFont(new Font("Raleway", Font.BOLD, 25));

        b1 = new JButton("WITHDRAW");
        b2 = new JButton("BACK");

        setLayout(null);

        l1.setBounds(190, 350, 400, 20);
        l3.add(l1);

        l2.setBounds(190, 400, 400, 20);
        l3.add(l2);

        t1.setBounds(190, 450, 330, 30);
        l3.add(t1);

        b1.setBounds(390, 588, 150, 35);
        l3.add(b1);

        b2.setBounds(390, 633, 150, 35);
        l3.add(b2);

        b1.addActionListener(this);
        b2.addActionListener(this);

        setSize(960, 1080);
        setLocation(500, 0);
        setUndecorated(true);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        try {        
            String amountStr = t1.getText();
            if (amountStr.equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter the Amount you want to Withdraw");
                return;
            }

            int amount = Integer.parseInt(amountStr); // Parse amount once
            if (amount <= 0) {
                JOptionPane.showMessageDialog(null, "Please enter a valid amount");
                return;
            }

            Conn c1 = new Conn();
            // Calculate current balance
            String query = "SELECT type, amount FROM bank WHERE pin = ?";
            PreparedStatement pstmt = c1.c.prepareStatement(query);
            pstmt.setString(1, pin);
            ResultSet rs = pstmt.executeQuery();

            int balance = 0;
            while (rs.next()) {
                String transactionType = rs.getString("type");
                int transactionAmount = rs.getInt("amount");
                if (transactionType.equals("Deposit")) {
                    balance += transactionAmount;
                } else {
                    balance -= transactionAmount;
                }
            }

            if (balance < amount) {
                JOptionPane.showMessageDialog(null, "Insufficient Balance");
                return;
            }

            // Prepare date
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(date); // Format date correctly

            // Insert withdrawal record
            String insertSQL = "INSERT INTO bank (pin, date, type, amount) VALUES (?, ?, ?, ?)";
            PreparedStatement insertPstmt = c1.c.prepareStatement(insertSQL);
            insertPstmt.setString(1, pin);
            insertPstmt.setString(2, formattedDate);
            insertPstmt.setString(3, "Withdrawl");
            insertPstmt.setInt(4, amount);
            insertPstmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Rs. " + amount + " Debited Successfully");
            setVisible(false);
            new Transactions(pin).setVisible(true);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid numeric amount");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error occurred while processing: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error: " + e);
        }
    }

    public static void main(String[] args) {
        new Withdrawl("").setVisible(true);
    }
}
