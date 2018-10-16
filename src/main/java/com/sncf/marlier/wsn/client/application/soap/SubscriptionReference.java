package com.sncf.marlier.wsn.client.application.soap;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;

@XmlRootElement(name="SubscriptionReference", namespace = "http://docs.oasis-open.org/wsn/b-2")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionReference {

    protected SubscriptionReference() {
    }

    // private but necessary properties for databinding
    @XmlElement(name="Address", namespace = "http://www.w3.org/2005/08/addressing")
    protected String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
