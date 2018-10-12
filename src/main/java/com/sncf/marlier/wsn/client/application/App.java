package com.sncf.marlier.wsn.client.application;

import org.oasis_open.docs.wsn.brw_2.NotificationBroker;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class App {
    private JTextField TextUrl;
    private JButton ButtonStartStop;
    private JTextField TextTopic;
    private JList ListNotifications;
    private ListModel ListModelMessagesRecus;
    private JSplitPane SplitPaneMessages;
    private JPanel PanelApp;
    private JTextPane TextPaneNotification;
    private JScrollPane ScrollPaneTextPaneNotification;
    private JScrollPane ScrollPaneListNotifications;
    private JTextField TextPort;
    private JTextField TextFiltre;

    private Client client;
    private final List<Notification> listeNotifications;

    public App() {
        ListModelMessagesRecus = new DefaultListModel();
        ListNotifications.setModel(ListModelMessagesRecus);

        listeNotifications = new ArrayList<Notification>();

        ButtonStartStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if(ButtonStartStop.getText() == "Start") {
                    try {
                        client = new Client( TextPort.getText());
                        client.abonnement(TextUrl.getText(), TextTopic.getText(), TextFiltre.getText());
                        ButtonStartStop.setText("Stop");
                        ButtonStartStop.setBackground(Color.RED);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                else {
                    try {
                        client.desabonnement();
                        ButtonStartStop.setText("Start");
                        ButtonStartStop.setBackground(Color.GREEN);
                        client = null;
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
        JFrame frame = new JFrame("wsn-client");
        frame.setContentPane(new App().PanelApp);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void addNotification(Notification notification){
        listeNotifications.add(notification);
        ((DefaultListModel) ListNotifications.getModel()).addElement(notification.getDate());
    }



}
