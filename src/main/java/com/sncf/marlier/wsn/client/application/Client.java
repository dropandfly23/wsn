// Java
package com.sncf.marlier.wsn.client.application;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

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
    private Server server;
    private Subscription subscription;
    private NotificationListener notificationListener;
    private NotificationConsumerImpl notificationConsumer;

    public void abonnement(String url, String topic, String filter, String port) throws Exception{
        creerConsumerSansCallback(port);
        creerListener(port);
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

    public void creerConsumer(String port) throws UnknownHostException {
        String adresse = new StringBuilder().append("http://")
                .append(InetAddress.getLocalHost().getHostAddress())
                .append(":").append(port)
                .append("/wsn-client").toString();

        this.consumer = new Consumer(new Consumer.Callback() {
            public void notify(NotificationMessageHolderType message) {
                message.toString();
                Object o = message.getMessage().getAny();
                System.out.println(message.getMessage().getAny());
                if (o instanceof Element) {
                    Element e = (Element) o;
                    System.out.println(e.getTextContent());
                    System.out.println();
                    System.out.println("From: "+e.getElementsByTagName("from").item(0).getTextContent());
                    System.out.println("To: "+e.getElementsByTagName("to").item(0).getTextContent());
                    System.out.println("Subject: "+e.getElementsByTagName("subject").item(0).getTextContent());
                    System.out.println("Body: "+e.getElementsByTagName("body").item(0).getTextContent());
                    System.out.println();
                }
                notificationListener.nouvelleNotification(new Notification(message.getSubscriptionReference().toString()));
            }
        }, adresse);
    }

    public void creerConsumerSansCallback(String port) throws UnknownHostException{
        String adresse = new StringBuilder().append("http://")
                .append(InetAddress.getLocalHost().getHostAddress())
                .append(":").append(port)
                .append("/wsn-client").toString();
        this.consumer = new Consumer(null, adresse);
    }


    public void creerListener(String port) throws Exception{
        server = new Server(Integer.parseInt(port));

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.setContextPath("/");
        server.setHandler(servletContextHandler);

        ServletHolder servletHolder = servletContextHandler.addServlet(ServletContainer.class, "/*");
        servletHolder.setInitOrder(0);

        servletHolder.setInitParameter("jersey.config.server.provider.classnames", Handler.class.getCanonicalName());

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }

    }


    public void setNotificationListener(NotificationListener listener) {
        notificationListener = listener;
    }


}