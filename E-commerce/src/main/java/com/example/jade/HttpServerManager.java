package com.example.jade;

import jade.wrapper.AgentContainer;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.example.jade.Handlers.AddProductHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.io.OutputStream;
import java.io.IOException;

public class HttpServerManager {
    private final AgentContainer mainContainer;
    private HttpServer server;

    public HttpServerManager(AgentContainer mainContainer) {
        this.mainContainer = mainContainer;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/addProduct", (HttpHandler) new AddProductHandler(mainContainer));
//        server.createContext("/updateProduct", new UpdateProductHandler(mainContainer));
//        server.createContext("/deleteProduct", new DeleteProductHandler(mainContainer));
        server.setExecutor(Executors.newFixedThreadPool(10)); // Handle concurrent requests
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}
