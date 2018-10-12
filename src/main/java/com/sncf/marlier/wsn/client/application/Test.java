package com.sncf.marlier.wsn.client.application;

public class Test {

    public static void main(String[] args) {
        new Test();
    }

    public class TestServer {
        private String port;

        public TestServer(String port) {
            this.port = port;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }
    }

}