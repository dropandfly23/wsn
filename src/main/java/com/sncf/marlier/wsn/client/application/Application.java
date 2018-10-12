package com.sncf.marlier.wsn.client.application;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private static App app;
    private Client client;
    private List<Notification> listeNotifications;

    public static void main(String[] args) {
        new Application();
    }

    public Application(){
//        app = new App();
//
//        listeNotifications = new ArrayList<Notification>();
//
//        app.getButtonStartStop().addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//
//                if(app.getButtonStartStop().getText() == "Start") {
//                    try {
//                        client = new Client(app.getTextPort().getText());
//                        client.abonnement(app.getTextUrl().getText(), app.getTextTopic().getText(), app.getTextFiltre().getText());
//                        app.getButtonStartStop().setText("Stop");
//                        app.getButtonStartStop().setBackground(Color.RED);
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//                }
//                else {
//                    try {
//                        client.desabonnement();
//                        app.getButtonStartStop().setText("Start");
//                        app.getButtonStartStop().setBackground(Color.GREEN);
//                        client = null;
//                    } catch (Exception e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            }
//        });
//
//        app.getListNotifications().addListSelectionListener(new ListSelectionListener() {
//            public void valueChanged(ListSelectionEvent e) {
//                int selected = app.getListNotifications().getSelectedIndex();
//                app.getTextPaneNotification().setText(listeNotifications.get(selected).message);
//            }
//        });
//
//        JFrame frame = new JFrame("wsn-client");
//        frame.setContentPane(app.getPanelApp());
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);


    }

}
