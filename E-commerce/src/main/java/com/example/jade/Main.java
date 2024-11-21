package com.example.jade;

import com.example.jade.Handlers.AddProductHandler;
import com.sun.net.httpserver.HttpHandler;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import com.sun.net.httpserver.HttpServer;
//import jade.core.ContainerController;
import jade.mtp.http.MessageTransportProtocol;
import jade.wrapper.AgentController;


import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize JADE Runtime
            Runtime runtime = Runtime.instance();

            // Configure JADE Main Container
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_HOST, "127.0.0.1"); // Use IPv4
//            profile.setParameter(Profile.MAIN_HOST, "localhost"); // Set host
            profile.setParameter(Profile.MAIN_PORT, "1501"); // Set custom port (default 1099)
            profile.setParameter(Profile.GUI, "false"); // Disable GUI
//            profile.setParameter(Profile.LOG_LEVEL, "INFO"); // Increase log level

            // Create the Main JADE Container
            AgentContainer mainContainer = runtime.createMainContainer(profile);
            System.out.println("JADE Main Container created on port 1501.");

//            // Install the HTTP MTP (Message Transport Protocol)
//            mainContainer.installMTP("http", new HttpMTP());
//            System.out.println("HTTP MTP installed successfully.");

            // Start HTTP Server
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            System.out.println("HTTP Server started on port 8080");

            try {
                // Create and start the SellerAgent
                AgentController sellerAgentController = mainContainer.createNewAgent("SellerAgent", "com.example.jade.Agents.SellerAgent", new Object[]{});
                sellerAgentController.start();  // Start the agent

                // Optionally, log that the agent has started
                System.out.println("SellerAgent started successfully.");

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Map AddProductHandler to the /add-product endpoint
            server.createContext("/add-product", (HttpHandler) new AddProductHandler(mainContainer));

            // Set a default executor
            server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(10));

            // Start the server
            server.start();
            System.out.println("Server is up and running!");

            // JADE Platform Initialization Complete
            System.out.println("JADE Platform initialized successfully.");
        } catch (Exception e) {
            System.err.println("Error starting JADE or HTTP server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

