package fr.icefeather.wsn.client.application;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class Notifications {

    @XmlElement
    public List<Notification> notifications;

    public Notifications() {
        this.notifications = new ArrayList<>();
    }
}
