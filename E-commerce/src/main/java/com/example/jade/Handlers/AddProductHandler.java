package com.example.jade.Handlers;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.OutputStream;
import java.io.IOException;

public class AddProductHandler implements HttpHandler {
    private final AgentContainer mainContainer;

    public AddProductHandler(AgentContainer mainContainer) {
        System.out.println("Came here 1");
        this.mainContainer = mainContainer;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                System.out.println("Came here 2");
                // Read request body (assuming JSON input)
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                System.out.println("Received request body: " + requestBody);

                // Extract product details (title and price) from JSON
                // Example: {"title":"Product A", "price":100}
                String title = extractJsonValue(requestBody, "title");
                int price = Integer.parseInt(extractJsonValue(requestBody, "price"));


                // Verify that the SellerAgent is available
                AgentController sellerAgent = mainContainer.getAgent("SellerAgent");

                if (sellerAgent == null) {
                    System.out.println("SellerAgent not found in container!");
                    // Optionally, create and start it if not found
                } else {
                    // Now you can interact with the agent
                    System.out.println("SellerAgent found, ready for interaction.");

                    // Create an ACL message
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver(new AID("SellerAgent", AID.ISLOCALNAME));
                    msg.setContent("ADD_PRODUCT:" + title + ":" + price);

                    // Pass the message to the SellerAgent via O2A
                    sellerAgent.putO2AObject(msg, AgentController.ASYNC);
                    System.out.println("Message sent to SellerAgent: " + msg.getContent());
                    
                };

                // Send success response
                String response = "Product added: " + title + " for price " + price;
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();

            } catch (Exception e) {
                e.printStackTrace();
                String errorResponse = "Failed to notify the agent!";
                exchange.sendResponseHeaders(500, errorResponse.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(errorResponse.getBytes());
                os.close();
            }
        } else {
            // Unsupported method
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }

    /**
     * Helper method to extract a value from a JSON string by key.
     * Note: This is a basic implementation. Consider using a JSON library like Jackson or Gson.
     */
    private String extractJsonValue(String json, String key) {

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        return jsonObject.get(key).getAsString();
    }
}