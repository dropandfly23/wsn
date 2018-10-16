package com.sncf.marlier.wsn.client.application;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Server {

    private HttpServer server;
    private HttpContext context;

    public Server() throws IOException{
        server = HttpServer.create(new InetSocketAddress(8500), 0);
        context = server.createContext("/", new MyHandler());
    }

    public void start(){
        server.start();
    }

    public void getEndpoint(){

    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}