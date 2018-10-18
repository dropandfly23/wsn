package fr.icefeather.wsn.client.application;

import com.sun.net.httpserver.Headers;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.*;

public class Notification {
    public Date date;
    public String message;
    public Headers headers;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public Notification(){
        this.date = new Date();
    }

    public Notification(String message, Headers headers){
        this.date = new Date();
        this.message = message;
        this.headers = headers;
    }

    public String getDate(){
        return dateFormat.format(date);
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

    public Headers getHeaders() {
        return headers;
    }

    public List<Map.Entry> getHeadersList() {
        List<Map.Entry> headersList = new ArrayList<>();
        Iterator headersIter = headers.entrySet().iterator();
        while (headersIter.hasNext()) {
            Map.Entry entry = (Map.Entry)headersIter.next();
            headersList.add(entry);
        }
        return headersList;
    }
}
