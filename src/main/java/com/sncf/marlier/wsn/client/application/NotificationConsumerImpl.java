package com.sncf.marlier.wsn.client.application;

import org.apache.cxf.annotations.EndpointProperties;
import org.apache.cxf.annotations.EndpointProperty;
import org.apache.cxf.feature.Features;
import org.apache.cxf.wsn.client.Consumer;
import org.apache.cxf.wsn.client.Referencable;
import org.apache.cxf.wsn.util.WSNHelper;
import org.oasis_open.docs.wsn.b_2.NotificationMessageHolderType;
import org.oasis_open.docs.wsn.b_2.Notify;
import org.oasis_open.docs.wsn.bw_2.NotificationConsumer;
import org.w3c.dom.Element;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.ws.Endpoint;
import javax.xml.ws.spi.http.HttpExchange;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import java.io.IOException;
import java.util.Iterator;

@javax.jws.WebService(
        serviceName = "NotificationConsumerService",
        portName = "NotificationConsumerPort",
        targetNamespace = "http://docs.oasis-open.org/wsn/bw-2",
        endpointInterface = "org.oasis_open.docs.wsn.bw_2.NotificationConsumer")

@EndpointProperties({@EndpointProperty(key = "set-jaxb-validation-event-handler", value = "false")})
@Features(features="org.apache.cxf.feature.LoggingFeature")
public class NotificationConsumerImpl implements Referencable {

    private final NotificationConsumerImpl.Callback callback;
    private final Endpoint endpoint;
    private final JAXBContext context;

    public NotificationConsumerImpl(NotificationConsumerImpl.Callback callback, String address, Class... extraClasses) {
        this.callback = callback;
        WSNHelper helper = WSNHelper.getInstance();
        if (helper.supportsExtraClasses()) {
            this.endpoint = helper.publish(address, this, extraClasses);
            this.context = null;
        } else {
            this.endpoint = helper.publish(address, this, new Class[0]);
            if (extraClasses != null && extraClasses.length > 0) {
                try {
                    this.context = JAXBContext.newInstance(extraClasses);
                } catch (JAXBException var6) {
                    throw new RuntimeException(var6);
                }
            } else {
                this.context = null;
            }
        }

    }

    public void stop() {
        this.endpoint.stop();
    }

    public W3CEndpointReference getEpr() {
        return (W3CEndpointReference)this.endpoint.getEndpointReference(W3CEndpointReference.class);
    }

    public void notify(HttpExchange httpExchange){}

    public interface Callback {
        void notify(HttpExchange var1) throws IOException;
    }

}
