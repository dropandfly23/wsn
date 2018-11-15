package fr.icefeather.wsn.client.application.util.xml;

import fr.icefeather.wsn.client.application.Notification;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

public class DateAdapter extends XmlAdapter<String, Date> {

    @Override
    public String marshal(Date date) throws Exception {
        return Notification.dateFormat.format(date);
    }

    @Override
    public Date unmarshal(String date) throws Exception {
        return Notification.dateFormat.parse(date);
    }

}
