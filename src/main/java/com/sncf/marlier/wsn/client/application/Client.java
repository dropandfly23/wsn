// Java
package com.sncf.marlier.wsn.client.application;

import org.w3c.dom.Element;

import org.apache.cxf.wsn.client.Consumer;
import org.apache.cxf.wsn.client.NotificationBroker;
import org.apache.cxf.wsn.client.Subscription;
import org.oasis_open.docs.wsn.b_2.NotificationMessageHolderType;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 */
public final class Client {

    private Consumer consumer;
    private Subscription subscription;

    public Client(String port) throws UnknownHostException {
        String adresse = new StringBuilder().append("http://")
                .append(InetAddress.getLocalHost().getHostAddress())
                .append(":").append(port)
                .append("/wsn-client").toString();

        this.consumer = new Consumer(new Consumer.Callback() {
            public void notify(NotificationMessageHolderType message) {
                Object o = message.getMessage().getAny();
                System.out.println(message.getMessage().getAny());
                if (o instanceof Element) {
                    System.out.println(((Element)o).getTextContent());
                }
            }
        }, adresse);
    }

    public void abonnement(String url, String topic, String filter) throws Exception{
        NotificationBroker notificationBroker = new NotificationBroker(url);
        if (filter == null || filter.length() == 0) {
            subscription = notificationBroker.subscribe(consumer, topic);
        } else {
            subscription = notificationBroker.subscribe(consumer, topic, filter);
        }
    }

    public void desabonnement() throws Exception{
        subscription.unsubscribe();
        consumer.stop();
    }



}