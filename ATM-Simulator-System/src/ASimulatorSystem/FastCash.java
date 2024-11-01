package ASimulatorSystem;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FastCash extends JFrame implements ActionListener {

    JLabel l1;
    JButton b1, b2, b3, b4, b5, b6, b7;
    String pin;

    FastCash(String pin) {
        this.pin = pin;
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("ASimulatorSystem/icons/atm.jpg"));
        Image i2 = i1.getImage().getScaledInstance(1000, 1180, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel l3 = new JLabel(i3);
        l3.setBounds(0, 0, 960, 1080);
        add(l3);

        l1 = new JLabel("SELECT WITHDRAWAL AMOUNT");
        l1.setForeground(Color.WHITE);
        l1.setFont(new Font("System", Font.BOLD, 16));

        b1 = new JButton("Rs 100");
        b2 = new JButton("Rs 500");
        b3 = new JButton("Rs 1000");
        b4 = new JButton("Rs 2000");
        b5 = new JButton("Rs 5000");
        b6 = new JButton("Rs 10000");
        b7 = new JButton("BACK");

        setLayout(null);

        l1.setBounds(235, 400, 700, 35);
        l3.add(l1);

        b1.setBounds(170, 499, 150, 35);
        l3.add(b1);

        b2.setBounds(390, 499, 150, 35);
        l3.add(b2);

        b3.setBounds(170, 543, 150, 35);
        l3.add(b3);

        b4.setBounds(390, 543, 150, 35);
        l3.add(b4);

        b5.setBounds(170, 588, 150, 35);
        l3.add(b5);

        b6.setBounds(390, 588, 150, 35);
        l3.add(b6);

        b7.setBounds(390, 633, 150, 35);
        l3.add(b7);

        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        b4.addActionListener(this);
        b5.addActionListener(this);
        b6.addActionListener(this);
        b7.addActionListener(this);

        setSize(960, 1080);
        setLocation(500, 0);
        setUndecorated(true);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            String amount = ((JButton) ae.getSource()).getText().substring(3); // Extract amount from button text
            int withdrawalAmount = Integer.parseInt(amount);
            Conn c = new Conn();

            // Calculate current balance
            String sql = "SELECT type, amount FROM bank WHERE pin = ?";
            PreparedStatement pstmt = c.c.prepareStatement(sql);
            pstmt.setString(1, pin);
            ResultSet rs = pstmt.executeQuery();

            int balance = 0;
            while (rs.next()) {
                String transactionType = rs.getString("type");
                int transactionAmount = rs.getInt("amount");

                if (transactionType.equals("Deposit")) {
                    balance += transactionAmount; // Add deposits
                } else if (transactionType.equals("Withdrawl")) {
                    balance -= transactionAmount; // Subtract withdrawals
                }
            }

            if (ae.getSource() == b7) {
                this.setVisible(false);
                new Transactions(pin).setVisible(true);
                return;
            }

            if (balance < withdrawalAmount) {
                JOptionPane.showMessageDialog(null, "Insufficient Balance");
                return;
            }

            // Insert withdrawal record into the bank table
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(date); // Format date correctly

            String insertSQL = "INSERT INTO bank (pin, date, type, amount) VALUES (?, ?, ?, ?)";
            PreparedStatement insertPstmt = c.c.prepareStatement(insertSQL);
            insertPstmt.setString(1, pin);
            insertPstmt.setString(2, formattedDate);
            insertPstmt.setString(3, "Withdrawl");
            insertPstmt.setInt(4, withdrawalAmount);
            insertPstmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Rs. " + withdrawalAmount + " Debited Successfully");

            setVisible(false);
            new Transactions(pin).setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error occurred while processing: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid amount selected.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new FastCash("").setVisible(true);
    }
}
