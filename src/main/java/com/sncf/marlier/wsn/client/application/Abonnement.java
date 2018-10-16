package com.sncf.marlier.wsn.client.application;

import com.sncf.marlier.wsn.client.application.soap.SubscribeResponse;
import org.oasis_open.docs.wsn.b_2.*;

import javax.xml.bind.*;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.apache.cxf.wsn.client.NotificationBroker.QNAME_MESSAGE_CONTENT;
import static org.apache.cxf.wsn.client.NotificationBroker.QNAME_TOPIC_EXPRESSION;

public class Abonnement {

    private String adresseClient;
    private String adresseServer;
    private String topic;
    private String filtre;
    private String adresseManager;

    private SOAPConnection soapConnection;

    public Abonnement(String adresse, String port, String topic, String filtre) throws UnknownHostException {
        this.adresseClient = "http://"+InetAddress.getLocalHost().getHostAddress()+":"+port+"/wsn-client";
        this.adresseServer = adresse;
        this.topic = topic;
        this.filtre = filtre;
    }


    public SubscribeResponse abonnement() throws JAXBException, SOAPException, IOException {

        Subscribe subscribeRequest = new Subscribe();
        SubscribeResponse subscribeResponse = new SubscribeResponse();

        W3CEndpointReference endpointReference = new W3CEndpointReferenceBuilder().address(adresseClient).build();
        subscribeRequest.setConsumerReference(endpointReference);
        subscribeRequest.setFilter(new FilterType());
        if (topic != null) {
            TopicExpressionType topicExp = new TopicExpressionType();
            topicExp.getContent().add(topic);
            subscribeRequest.getFilter().getAny().add(new JAXBElement(QNAME_TOPIC_EXPRESSION, TopicExpressionType.class, topicExp));
        }

        if (filtre != null) {
            QueryExpressionType xpathExp = new QueryExpressionType();
            xpathExp.setDialect("http://www.w3.org/TR/1999/REC-xpath-19991116");
            xpathExp.getContent().add(filtre);
            subscribeRequest.getFilter().getAny().add(new JAXBElement(QNAME_MESSAGE_CONTENT, QueryExpressionType.class, xpathExp));
        }

        subscribeResponse = (SubscribeResponse) makeRequest(subscribeRequest, subscribeResponse, adresseServer);

        adresseManager = subscribeResponse.getSubscriptionReference().getAddress();

        return subscribeResponse;
    }


    public UnsubscribeResponse desabonnement() throws JAXBException, SOAPException, IOException {

        Unsubscribe unsubscribeRequest = new Unsubscribe();
        UnsubscribeResponse unsubscribeResponse = new UnsubscribeResponse();

        unsubscribeResponse = (UnsubscribeResponse) makeRequest(unsubscribeRequest, unsubscribeResponse, adresseManager);

        return unsubscribeResponse;
    }


    public Object makeRequest(Object requete, Object reponse, String adresse) throws JAXBException, SOAPException, IOException{

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        JAXBContext requeteContext = JAXBContext.newInstance(requete.getClass());
        final MessageFactory msgFactory = MessageFactory.newInstance();
        final SOAPMessage message = msgFactory.createMessage();
        SOAPElement requeteElement = message.getSOAPBody();

        Marshaller subscribeContextMarshaller = requeteContext.createMarshaller();
        subscribeContextMarshaller.marshal(requete, new DOMResult(requeteElement) );
        SOAPMessage requeteSoap = createSOAPRequest(requeteElement);

        requeteSoap.writeTo(out);
        System.out.println(new String(out.toByteArray()));
        out.flush();

        createSOAPConnection();

        SOAPMessage reponseSoap = soapConnection.call(requeteSoap, adresse);

        reponseSoap.writeTo(out);
        System.out.println(new String(out.toByteArray()));
        out.flush();

        JAXBContext reponseContext = JAXBContext.newInstance(reponse.getClass());
        Unmarshaller subscribeResponseContextUnmarshaller = reponseContext.createUnmarshaller();
        reponse = subscribeResponseContextUnmarshaller.unmarshal(reponseSoap.getSOAPBody().getFirstChild());

        return reponse;

    }


    private SOAPMessage createSOAPRequest(SOAPElement body) throws SOAPException {

        final MessageFactory messageFactory = MessageFactory.newInstance();
        final SOAPMessage soapMessage = messageFactory.createMessage();
        final SOAPPart soapPart = soapMessage.getSOAPPart();

        // SOAP Envelope
        final SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("ns2", "http://docs.oasis-open.org/wsn/b-2");

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        soapBody.addChildElement((SOAPElement) body.getFirstChild());

        soapMessage.saveChanges();

        /* Print the request message */
        return soapMessage;
    }


    private void createSOAPConnection() throws UnsupportedOperationException,
            SOAPException {

        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory;
        soapConnectionFactory = SOAPConnectionFactory.newInstance();
        soapConnection = soapConnectionFactory.createConnection();

    }


    public String getAdresseClient() {
        return adresseClient;
    }

    public void setAdresseClient(String adresseClient) {
        this.adresseClient = adresseClient;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getFiltre() {
        return filtre;
    }

    public void setFiltre(String filtre) {
        this.filtre = filtre;
    }

    public String getAdresseManager() {
        return adresseManager;
    }

    public void setAdresseManager(String adresseManager) {
        this.adresseManager = adresseManager;
    }
}
