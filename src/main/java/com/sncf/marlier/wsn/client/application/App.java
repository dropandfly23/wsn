package com.sncf.marlier.wsn.client.application;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class App implements NotificationListener{
    private JTextField TextUrl;
    private JButton ButtonStartStop;
    private JTextField TextTopic;
    private JList ListNotifications;
    private ListModel<String> ListModelMessagesRecus;
    private JSplitPane SplitPaneMessages;
    private JPanel PanelApp;
    private JTextPane TextPaneNotification;
    private JScrollPane ScrollPaneTextPaneNotification;
    private JScrollPane ScrollPaneListNotifications;
    private JTextField TextPort;
    private JTextField TextFiltre;

    private Abonnement abonnement;
    private final List<Notification> listeNotifications;

    public App() {
        ListModelMessagesRecus = new DefaultListModel();
        ListNotifications.setModel(ListModelMessagesRecus);

        listeNotifications = new ArrayList<Notification>();

        ButtonStartStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if(ButtonStartStop.getText().equals("Start")) {
                    try {
                        abonnement = new Abonnement(TextUrl.getText(), TextPort.getText(), TextTopic.getText(), TextFiltre.getText());
                        abonnement.abonnement();
                        ButtonStartStop.setText("Stop");
                        ButtonStartStop.setBackground(Color.RED);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        try {
                            abonnement.desabonnement();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
                else {
                    try {
                        abonnement.desabonnement();
                        ButtonStartStop.setText("Start");
                        ButtonStartStop.setBackground(Color.GREEN);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        ListNotifications.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int selected = ListNotifications.getSelectedIndex();
                TextPaneNotification.setText(listeNotifications.get(selected).message);
            }
        });
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        JFrame frame = new JFrame("wsn-abonnement");
        JPanel panel = new App().PanelApp;
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void nouvelleNotification(Notification notification){
        listeNotifications.add(notification);
        ((DefaultListModel) ListNotifications.getModel()).addElement(notification.getDate());
    }



}
