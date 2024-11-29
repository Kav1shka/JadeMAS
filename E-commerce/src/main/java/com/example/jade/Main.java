package com.example.jade;

import com.example.jade.Handlers.AddProductHandler;
import com.example.jade.Handlers.BuyerAgentHandler;
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
import java.util.concurrent.Executors;

import static jade.core.Profile.*;

public class Main {
    public static void main(String[] args) {

        try {
            // More robust JADE runtime configuration
            Runtime runtime = Runtime.instance();
            Profile profile = new ProfileImpl();
            profile.setParameter(MAIN_HOST, "127.0.0.1");
            profile.setParameter(MAIN_PORT, "1501"); // Standard JADE port
            profile.setParameter(GUI, "false");
            profile.setParameter(LOCAL_HOST, "127.0.0.1");
//            profile.setParameter(Profile.MTP, "jade.mtp.http.MessageTransportProtocol");
//            profile.setParameter(Profile.VERBOSE, "true");

//            // Specific network settings
//            System.setProperty("jade.core.messaging.LocalMessageManager", "enable");
//            System.setProperty("jade.imtp.leap.JICP.listen_address", "localhost");


            // Create main container with error handling
            AgentContainer mainContainer = runtime.createMainContainer(profile);
            if (mainContainer == null) {
                throw new RuntimeException("Failed to create JADE main container");
            }

            // HTTP Server with more robust configuration
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.setExecutor(Executors.newFixedThreadPool(10));
            server.start();
            System.out.println("Server successfully started on 127.0.0.1:8080");

            // Agent creation with comprehensive error handling
            try {
                AgentController sellerAgent = mainContainer.createNewAgent(
                        "SellerAgent",
                        "com.example.jade.Agents.SellerAgent",
                        new Object[]{}
                );
                sellerAgent.start();
                System.out.println("SellerAgent started successfully.");
            } catch (Exception agentError) {
                System.err.println("Agent initialization failed: " + agentError.getMessage());
                agentError.printStackTrace();
            }

            // Create BuyerAgent
            try {
                AgentController buyerAgent = mainContainer.createNewAgent(
                        "BuyerAgent",
                        "com.example.jade.Agents.BuyerAgent",
                        new Object[]{}
                );
                buyerAgent.start();
                System.out.println("BuyerAgent started successfully.");
            } catch (Exception agentError) {
                System.err.println("BuyerAgent initialization failed: " + agentError.getMessage());
                agentError.printStackTrace();
            }

            // Context mapping
            server.createContext("/addproduct",new AddProductHandler(mainContainer));
//            server.createContext("/update-product", new AddProductHandler(mainContainer));
//            server.createContext("/delete-product", new AddProductHandler(mainContainer));
            server.createContext("/byproduct",new BuyerAgentHandler(mainContainer));

        } catch (Exception e) {
            System.err.println("Critical initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
