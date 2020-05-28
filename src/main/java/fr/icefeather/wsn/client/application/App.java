package fr.icefeather.wsn.client.application;

import fr.icefeather.wsn.client.application.util.NotificationsCSV;
import fr.icefeather.wsn.client.application.util.NotificationsXML;
import org.apache.batik.util.gui.xmleditor.XMLContext;
import org.apache.batik.util.gui.xmleditor.XMLEditorKit;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
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
    private JTextField TextUseraw;
    private JTable TableNotificationHeaders;
    private JSplitPane SplitPaneNotificationDetail;
    private JSplitPane SplitPaneNotificationPanel;
    private JScrollPane ScrollPaneTableNotificationHeaders;
    private JButton ButtonClear;
    private JLabel VersionLabel;
    private JButton ButtonSave;
    private JButton ButtonOpen;

    private Abonnement abonnement;
    private Serveur serveur;
    private Notifications notifications;

    private ImageIcon IconPoubelle;
    private ImageIcon IconStart;
    private ImageIcon IconStop;


    public App() {

        try {
            IconStart = new ImageIcon(ImageIO.read(getClass().getResource("/icons/start.png")));
            IconStop = new ImageIcon(ImageIO.read(getClass().getResource("/icons/stop.png")));
        } catch (IOException iconsException) {

        }

        ButtonStartStop.setIcon(IconStart);

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
        final AppTextField userawTextField = new AppTextField(TextUseraw, "useraw", "false", true);
        formulaire.formFields.add(userawTextField);


        // VIEWS

        notifications = new Notifications();
        ListModelMessagesRecus = new DefaultListModel();
        ListNotifications.setModel(ListModelMessagesRecus);

        // Vue notification XML

        Map syntaxFontMap = new HashMap();
        Map syntaxForegroundMap = new HashMap();
        Font defaultFont = new Font("Lucida Console", 0, 11);
        Color fontForeground = Color.black;
        syntaxFontMap.put("default", defaultFont);
        syntaxForegroundMap.put("default", fontForeground);
        Font font = defaultFont.deriveFont(1);
        fontForeground = new Color(0, 0, 124);
        syntaxFontMap.put(XMLContext.XML_DECLARATION_STYLE, font);
        syntaxForegroundMap.put(XMLContext.XML_DECLARATION_STYLE, fontForeground);
        font = defaultFont.deriveFont(1);
        fontForeground = new Color(0, 0, 124);
        syntaxFontMap.put(XMLContext.DOCTYPE_STYLE, font);
        syntaxForegroundMap.put(XMLContext.DOCTYPE_STYLE, fontForeground);
        fontForeground = new Color(128, 128, 128);
        syntaxFontMap.put(XMLContext.COMMENT_STYLE, defaultFont);
        syntaxForegroundMap.put(XMLContext.COMMENT_STYLE, fontForeground);
        fontForeground = new Color(0, 0, 255);
        syntaxFontMap.put(XMLContext.ELEMENT_STYLE, defaultFont);
        syntaxForegroundMap.put(XMLContext.ELEMENT_STYLE, fontForeground);
        fontForeground = Color.black;
        syntaxFontMap.put(XMLContext.CHARACTER_DATA_STYLE, defaultFont);
        syntaxForegroundMap.put(XMLContext.CHARACTER_DATA_STYLE, fontForeground);
        fontForeground = new Color(0, 124, 0);
        syntaxFontMap.put(XMLContext.ATTRIBUTE_NAME_STYLE, defaultFont);
        syntaxForegroundMap.put(XMLContext.ATTRIBUTE_NAME_STYLE, fontForeground);
        fontForeground = new Color(153, 0, 107);
        syntaxFontMap.put(XMLContext.ATTRIBUTE_VALUE_STYLE, defaultFont);
        syntaxForegroundMap.put(XMLContext.ATTRIBUTE_VALUE_STYLE, fontForeground);
        fontForeground = new Color(124, 98, 0);
        syntaxFontMap.put(XMLContext.CDATA_STYLE, defaultFont);
        syntaxForegroundMap.put(XMLContext.CDATA_STYLE, fontForeground);

        XMLContext xmlContext = new XMLContext(syntaxFontMap, syntaxForegroundMap);

        EditorPaneNotification.setEditorKit(new XMLEditorKit(xmlContext));

        ScrollPaneTextPaneNotification.setBorder(null);
        ScrollPaneListNotifications.setBorder(null);
        ScrollPaneTableNotificationHeaders.setBorder(null);

        DefaultTableModel TableNotificationHeadersModel = new DefaultTableModel();
        TableNotificationHeadersModel.setColumnIdentifiers(new String[] { "header", "value" });
        TableNotificationHeaders.setModel(TableNotificationHeadersModel);
        TableNotificationHeaders.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableNotificationHeaders.getColumnModel().getColumn(1).setPreferredWidth(25);

        serveur = new Serveur();
        serveur.setNotificationListener(this);


        // LISTENERS

        ButtonStartStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!serveur.isActif() && (abonnement == null || !abonnement.isActif())) {
                    if (formulaire.valider()) {
                        try {
                            abonnement = new Abonnement(urlTextField.getText(), portTextField.getText(), topicTextField.getText(),Boolean.parseBoolean(TextUseraw.getText()),
                                    filtreTextField.getText());
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
                if (notifications.notifications.size() > 0) {
                    updateNotificationView(notifications.notifications.get(selected));
                }
            }
        });

        ButtonClear.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                notifications.notifications.clear();
                ((DefaultListModel) ListNotifications.getModel()).removeAllElements();
                updateNotificationView(new Notification("",null));
            }
        });

        ButtonSave.addActionListener(new ActionListener() {

            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter xmlFileType = new FileNameExtensionFilter(".xml", "xml");
            FileNameExtensionFilter csvFileType = new FileNameExtensionFilter(".csv", "csv");

            @Override public void actionPerformed(ActionEvent e) {
                fileChooser.addChoosableFileFilter(xmlFileType);
                fileChooser.addChoosableFileFilter(csvFileType);
                fileChooser.setFileFilter(xmlFileType);
                // Demonstrate "Save" dialog:
                int rVal = fileChooser.showSaveDialog(PanelApp);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getCurrentDirectory().toString() + File.separator + fileChooser.getSelectedFile().getName();
                    saveFile(filePath);
                }
            }

            public void saveFile(String filePath) {
                try {
                    if (filePath.endsWith(xmlFileType.getExtensions()[0])) {
                        NotificationsXML.toXmlFile(notifications, filePath);
                    } else if (filePath.endsWith(csvFileType.getExtensions()[0])) {
                        NotificationsCSV.toCsvFile(notifications, filePath);
                    } else {
                        filePath += "." + ((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0];
                        saveFile(filePath);
                    }
                } catch (JAXBException | IOException sauvegardeException) {
                    afficherErreur("Erreur lors de la sauvegarde", sauvegardeException);
                }
            }

        });

        ButtonOpen.addActionListener(new ActionListener() {

            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter xmlFileType = new FileNameExtensionFilter(".xml", "xml");

            @Override public void actionPerformed(ActionEvent e) {
                fileChooser.addChoosableFileFilter(xmlFileType);
                fileChooser.setFileFilter(xmlFileType);
                // Demonstrate "Save" dialog:
                int rVal = fileChooser.showOpenDialog(PanelApp);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getCurrentDirectory().toString() + File.separator + fileChooser.getSelectedFile().getName();
                    try {
                        notifications = NotificationsXML.fromXmlFile(filePath);
                        Iterator notificationIterator = notifications.notifications.iterator();
                        while (notificationIterator.hasNext()) {
                            ((DefaultListModel) ListNotifications.getModel()).addElement(
                                    ((Notification) notificationIterator.next()).getDate()
                            );
                        }
                    } catch (JAXBException ouvertureException) {
                        afficherErreur("Erreur lors de l'ouverture", ouvertureException);
                    }
                }
            }
        });

    }


    // APP METHODS

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


    // DISPLAY METHODS

    public void afficherErreur(String titre, Exception e) {
        JOptionPane d = new JOptionPane();
        d.showMessageDialog(PanelApp.getParent(), e.getMessage(), titre, JOptionPane.ERROR_MESSAGE);
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
                    tableModel.addRow(new Object[] {entry.getKey(), entry.getValue()});
                }
            }
        }
        ((DefaultTableModel) TableNotificationHeaders.getModel()).fireTableDataChanged();

    }


    // TRANSVERSE

    @Override public void nouvelleNotification(Notification notification) {
        notifications.notifications.add(notification);
        ((DefaultListModel) ListNotifications.getModel()).addElement(notification.getDate());
    }


    // MAIN

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException, IOException {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Windows".equals(info.getName())) {
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


    // APP FORM

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