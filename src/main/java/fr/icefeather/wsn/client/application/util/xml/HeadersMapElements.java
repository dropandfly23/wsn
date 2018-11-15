package fr.icefeather.wsn.client.application.util.xml;

import javax.xml.bind.annotation.XmlElement;

public class HeadersMapElements {
    @XmlElement public String key;
    @XmlElement public String value;

    private HeadersMapElements() {} //Required by JAXB

    public HeadersMapElements(String key, String value) {
        this.key   = key;
        this.value = value;
    }
}
