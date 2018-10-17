package com.sncf.marlier.wsn.client.application;

import com.sun.net.httpserver.Headers;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class App implements NotificationListener {
    private JTextField TextUrl;
    private JButton ButtonStartStop;
    private JTextField TextTopic;
    private JList ListNotifications;
    private ListModel<String> ListModelMessagesRecus;
    private JPanel PanelApp;
    private JTextPane TextPaneNotification;
    private JScrollPane ScrollPaneTextPaneNotification;
    private JScrollPane ScrollPaneListNotifications;
    private JTextField TextPort;
    private JTextField TextFiltre;
    private JTable TableNotificationHeaders;
    private JSplitPane SplitPaneNotificationDetail;
    private JSplitPane SplitPaneNotificationPanel;

    private Abonnement abonnement;
    private Serveur serveur;
    private final List<Notification> listeNotifications;

    public App() {
        ListModelMessagesRecus = new DefaultListModel();
        ListNotifications.setModel(ListModelMessagesRecus);

        DefaultTableModel TableNotificationHeadersModel = new DefaultTableModel();
        TableNotificationHeadersModel.setColumnIdentifiers(new String[] { "header", "value" });
        TableNotificationHeaders.setModel(TableNotificationHeadersModel);

        listeNotifications = new ArrayList<Notification>();

        serveur = new Serveur();
        serveur.setNotificationListener(this);

        ButtonStartStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (ButtonStartStop.getText().equals("Start")) {
                    try {
                        abonnement = new Abonnement(TextUrl.getText(), TextPort.getText(), TextTopic.getText(), TextFiltre.getText());
                        serveur.setPort(Integer.parseInt(TextPort.getText()));
                        serveur.start();
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
                } else {
                    try {
                        serveur.stop();
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
                updateHeadersTableModel(listeNotifications.get(selected).getHeaders());
                ((DefaultTableModel) TableNotificationHeaders.getModel()).fireTableDataChanged();
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

    @Override public void nouvelleNotification(Notification notification) {
        listeNotifications.add(notification);
        ((DefaultListModel) ListNotifications.getModel()).addElement(notification.getDate());
    }

    public void updateHeadersTableModel(Headers headers) {
        DefaultTableModel tableModel = (DefaultTableModel) TableNotificationHeaders.getModel();
        tableModel.setRowCount(0);
        Iterator headersIter = headers.entrySet().iterator();
        while (headersIter.hasNext()) {
            Map.Entry entry = (Map.Entry) headersIter.next();
            tableModel.addRow(new Object[] { entry.getKey().toString(), entry.getValue().toString() });
        }
    }

}
