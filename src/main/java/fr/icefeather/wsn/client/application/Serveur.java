package fr.icefeather.wsn.client.application;

import com.sun.net.httpserver.*;

import java.io.*;
import java.net.InetSocketAddress;

public class Serveur {

    private int port;
    private NotificationListener notificationListener;

    private HttpServer server;
    private HttpContext context;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public NotificationListener getNotificationListener() {
        return notificationListener;
    }

    public void setNotificationListener(NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    public void start() throws IOException{
        server = HttpServer.create(new InetSocketAddress(port), 0);
        context = server.createContext("/", new Handler(notificationListener));
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    static class Handler implements HttpHandler {

        private NotificationListener notificationListener;

        public Handler(NotificationListener notificationListener) {
            this.notificationListener = notificationListener;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            InputStreamReader isr =  new InputStreamReader(httpExchange.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);

            // From now on, the right way of moving from bytes to utf-8 characters:

            int b;
            StringBuilder buf = new StringBuilder(512);
            while ((b = br.read()) != -1) {
                buf.append((char) b);
            }

            br.close();
            isr.close();

            if (httpExchange.getRequestMethod().equals("POST")){
                notification(buf.toString(), httpExchange.getRequestHeaders());
                httpExchange.getRequestHeaders().entrySet();
            }

            String response = "This is the response";
            httpExchange.sendResponseHeaders(200, response.length());
        }

        private void notification(String message, Headers headers) {
            notificationListener.nouvelleNotification(new Notification(message, headers));
        }


    }

}