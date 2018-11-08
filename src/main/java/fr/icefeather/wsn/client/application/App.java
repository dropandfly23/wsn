package fr.icefeather.wsn.client.application;

import org.apache.batik.util.gui.xmleditor.XMLEditorKit;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;

public class App implements NotificationListener {
    private JTextField TextUrl;
    private JButton ButtonStartStop;
    private JTextField TextTopic;
    private JList ListNotifications;
    private ListModel<String> ListModelMessagesRecus;
    private JPanel PanelApp;
    private JEditorPane EditorPaneNotification;
    private JScrollPane ScrollPaneTextPaneNotification;
    private JScrollPane ScrollPaneListNotifications;
    private JTextField TextPort;
    private JTextField TextFiltre;
    private JTable TableNotificationHeaders;
    private JSplitPane SplitPaneNotificationDetail;
    private JSplitPane SplitPaneNotificationPanel;
    private JScrollPane ScrollPaneTableNotificationHeaders;
    private JButton ButtonClear;
    private JLabel VersionLabel;

    private Abonnement abonnement;
    private Serveur serveur;
    private final List<Notification> listeNotifications;

    private ImageIcon IconPoubelle;
    private ImageIcon IconStart;
    private ImageIcon IconStop;


    public App() {

        try {
            IconPoubelle = new ImageIcon(ImageIO.read(getClass().getResource("/icons/poubelle.png")));
            IconStart = new ImageIcon(ImageIO.read(getClass().getResource("/icons/start.png")));
            IconStop = new ImageIcon(ImageIO.read(getClass().getResource("/icons/stop.png")));
        } catch (IOException iconsException) {

        }

        ButtonStartStop.setIcon(IconStart);
        ButtonClear.setIcon(IconPoubelle);

        // DATA
        listeNotifications = new ArrayList<Notification>();

        // ELEMENTS
        final AppForm formulaire = new AppForm();

        final AppTextField urlTextField = new AppTextField(TextUrl, "http:// url broker wsn", "http://localhost/cxf/wsn/NotificationBroker", true);
        formulaire.formFields.add(urlTextField);
        final AppTextField topicTextField = new AppTextField(TextTopic, "Topic", null, true);
        formulaire.formFields.add(topicTextField);
        final AppTextField filtreTextField = new AppTextField(TextFiltre, "Filtre", null, false);
        formulaire.formFields.add(filtreTextField);
        final AppTextField portTextField = new AppTextField(TextPort, "8011", "8011", true);
        formulaire.formFields.add(portTextField);

        // VIEWS

        ListModelMessagesRecus = new DefaultListModel();
        ListNotifications.setModel(ListModelMessagesRecus);

        EditorPaneNotification.setEditorKit(new XMLEditorKit());
        ScrollPaneTextPaneNotification.setPreferredSize(EditorPaneNotification.getSize());

        ScrollPaneTextPaneNotification.setBorder(null);
        ScrollPaneListNotifications.setBorder(null);
        ScrollPaneTableNotificationHeaders.setBorder(null);

        DefaultTableModel TableNotificationHeadersModel = new DefaultTableModel();
        TableNotificationHeadersModel.setColumnIdentifiers(new String[] { "header", "value" });
        TableNotificationHeaders.setModel(TableNotificationHeadersModel);


        serveur = new Serveur();
        serveur.setNotificationListener(this);

        ButtonStartStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!serveur.isActif() && (abonnement == null || !abonnement.isActif())) {
                    if (formulaire.valider()) {
                        try {
                            abonnement = new Abonnement(urlTextField.getText(), portTextField.getText(), topicTextField.getText(), filtreTextField.getText());
                        } catch (UnknownHostException abonnementException) {
                            afficherErreur("Problème réseau", abonnementException);
                            return;
                        }
                        serveur.setPort(Integer.parseInt(TextPort.getText()));
                        startAll();
                    }
                } else {
                    stopAll();
                }
            }
        });

        ListNotifications.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int selected = ListNotifications.getSelectedIndex();
                if (listeNotifications.size() > 0) {
                    updateNotificationView(listeNotifications.get(selected));
                }
            }
        });

        ButtonClear.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                listeNotifications.clear();
                ((DefaultListModel)ListNotifications.getModel()).removeAllElements();
                updateNotificationView(new Notification("", null));
            }
        });
    }

    public void startAll() {
        if (!serveur.isActif()) {
            try {
                serveur.start();
            } catch (Exception serverStartException) {
                afficherErreur("Problème lors du démarrage du bouchon", serverStartException);
                stopAll();
                return;
            }
        }
        if (abonnement == null || !abonnement.isActif()) {
            try {
                abonnement.abonnement();
            } catch (Exception abonnementException) {
                afficherErreur("Problème lors de l'abonnement", abonnementException);
                stopAll();
                return;
            }
        }
        ButtonStartStop.setIcon(IconStop);
    }

    public void stopAll(){
        if (serveur.isActif()) {
            try {
                serveur.stop();
            } catch (Exception sevreurStopException) {
                afficherErreur("Problème lors de l'arret du bouchon", sevreurStopException);
            }
        }
        if (abonnement != null) {
            if (abonnement.isActif()) {
                try {
                    abonnement.desabonnement();
                } catch (Exception desabonnementException) {
                    afficherErreur("Problème lors du désabonnement du client", desabonnementException);
                }
            }
        }
        if (!serveur.isActif() && (abonnement == null || !abonnement.isActif())) {
            ButtonStartStop.setIcon(IconStart);
        }
    }

    public void afficherErreur(String titre, Exception e) {
        JOptionPane d = new JOptionPane();
        d.showMessageDialog(PanelApp.getParent(), e.getMessage(), titre, JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, IOException {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
        JFrame frame = new JFrame("wsn-client");
        final App application = new App();
        frame.setIconImage(ImageIO.read(application.getClass().getResource("/icons/inbox.png")));
        final JPanel panel = application.PanelApp;

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                application.stopAll();
            }
        });

        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override public void nouvelleNotification(Notification notification) {
        listeNotifications.add(notification);
        ((DefaultListModel) ListNotifications.getModel()).addElement(notification.getDate());
    }

    public void updateNotificationView(Notification notification) {
        EditorPaneNotification.setText(notification.getXmlMessage());

        DefaultTableModel tableModel = (DefaultTableModel) TableNotificationHeaders.getModel();
        tableModel.setRowCount(0);
        if (notification.headers != null) {
            if (notification.headers.size() > 0) {
                Iterator headersIter = notification.getHeaders().entrySet().iterator();
                while (headersIter.hasNext()) {
                    Map.Entry entry = (Map.Entry) headersIter.next();
                    String headerKey = (String) entry.getKey();
                    String headerValue = StringUtils.join((List) entry.getValue(), ";");
                    tableModel.addRow(new Object[] { headerKey, headerValue });
                }
            }
        }
        ((DefaultTableModel) TableNotificationHeaders.getModel()).fireTableDataChanged();

    }

    class AppTextField implements AppFormField {

        public JTextField textField;
        public Boolean isPlaceholder = false;
        public String placeholder;
        public String defaultText;
        public Boolean notnull;

        public AppTextField(JTextField jTextField, String placeholder, String defaultText, Boolean notnull) {
            this.textField = jTextField;
            this.placeholder = placeholder;
            this.defaultText = defaultText;
            this.notnull = notnull;
            if (defaultText == null || defaultText.isEmpty()){
                showPlaceholder();
            } else {
                showDefaultText();
            }
            listeners();
        }

        public String getText(){
            if (!isPlaceholder){
                return textField.getText();
            }
            return null;
        }

        private void showDefaultText() {
            isPlaceholder = false;
            textField.setForeground(Color.BLACK);
            textField.setFont(new Font(TextUrl.getFont().getFamily(), Font.PLAIN, TextUrl.getFont().getSize()));
            textField.setText(defaultText);
        }

        private void showPlaceholder() {
            isPlaceholder = true;
            textField.setText(placeholder);
            textField.setForeground(Color.GRAY);
            textField.setFont(new Font(TextUrl.getFont().getFamily(), Font.ITALIC, TextUrl.getFont().getSize()));
        }

        private void listeners(){
            textField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if(isPlaceholder) {
                        showDefaultText();
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if(textField.getText().isEmpty()){
                       showPlaceholder();
                    }
                }
            });
        }

        @Override
        public Boolean valider() {
            if (notnull) {
                if (getText() == null || getText().isEmpty()){
                    textField.setForeground(Color.RED);
                    return false;
                }
            }
            return true;
        }

    }


    interface AppFormField {
        Boolean valider();
    }


    class AppForm {

        public List<AppFormField> formFields = new ArrayList<>();

        public Boolean valider(){
            boolean valide = true;
            for (AppFormField formField : formFields){
                if(!formField.valider()){
                    valide = false;
                }
            }
            return valide;
        }

    }
}