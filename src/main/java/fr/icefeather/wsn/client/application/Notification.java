package fr.icefeather.wsn.client.application;

import fr.icefeather.wsn.client.application.util.xml.DateAdapter;
import fr.icefeather.wsn.client.application.util.xml.HeadersMapAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Notification {

    @XmlJavaTypeAdapter(DateAdapter.class)
    public Date date;
    @XmlElement
    public String message;
    @XmlJavaTypeAdapter(HeadersMapAdapter.class)
    public Map<String, String> headers;

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public Notification(){
        this.date = new Date();
    }

    public Notification(String message, Map<String, String> headers){
        this.date = new Date();
        this.message = message;
        this.headers = headers;
    }

    public String getDate(){
        return dateFormat.format(date);
    }

    public void setDate(String date) throws ParseException {
        this.date = dateFormat.parse(date);
    }

    public String getXmlMessage() {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            //initialize StreamResult with File object to save to file
            StreamSource source = new StreamSource(new StringReader(message));
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(source, result);
            return result.getWriter().toString();
        } catch (TransformerException e) {
            return message;
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

}
