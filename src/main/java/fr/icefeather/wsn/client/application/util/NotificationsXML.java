package fr.icefeather.wsn.client.application.util;

import fr.icefeather.wsn.client.application.Notifications;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class NotificationsXML {

    public static void toXmlFile(Notifications notifications, String filePath) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Notifications.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        File file = new File(filePath);
        marshaller.marshal(notifications, file);
    }

    public static Notifications fromXmlFile(String filepath) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Notifications.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        File file = new File(filepath);
        Notifications notifications = (Notifications) unmarshaller.unmarshal(file);
        return notifications;
    }

}
