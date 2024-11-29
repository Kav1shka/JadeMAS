//package com.example.jade.Handlers;
//
//import jade.core.Agent;
//import jade.core.AID;
//import jade.lang.acl.ACLMessage;
//import jade.wrapper.AgentContainer;
//import jade.wrapper.AgentController;
//import com.sun.net.httpserver.HttpHandler;
//import com.sun.net.httpserver.HttpExchange;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import java.io.OutputStream;
//import java.io.IOException;
//
//@SuppressWarnings("unused")
//public class BuyerAgentHandler implements HttpHandler{
//
//    private final AgentContainer mainContainer;
//
//    // Constructor to bind the handler to the agent
//    public BuyerAgentHandler( AgentContainer container) {
//
//        this.mainContainer  = container;
//    }
//
//    // Process incoming requests
//    public void processRequest(ACLMessage message) {
//        String conversationId = message.getConversationId();
//
//        // Example: Handle different types of requests based on the conversation ID
//        switch (conversationId) {
//            case "buy-product":
//                handleBuyProductRequest(message);
//                break;
//
//            default:
//                System.out.println("Unknown request type: " + conversationId);
//                sendFailureResponse(message, "Unknown request type");
//                break;
//        }
//    }
//
//    // Handle the "buy-product" request
//    private void handleBuyProductRequest(ACLMessage message) {
//        String targetProduct = message.getContent();
//
//        System.out.println("Processing buy request for product: " + targetProduct);
//
//        // Simulate or trigger the behavior to buy the product
//        initiateProductPurchase(targetProduct, message.getSender());
//    }
//
//    // Initiate product purchase logic
//    private void initiateProductPurchase(String targetProduct, AID requester) {
//
//            System.out.println("Searching for sellers for: " + targetProduct);
//
//            // Send a response to acknowledge receipt of the request
//            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
//            reply.addReceiver(requester);
//            reply.setContent("BuyerAgent is initiating the purchase for: " + targetProduct);
//            reply.setConversationId("buy-product-response");
//
//
//            // Here you can integrate your BuyerAgent's behaviors to handle actual negotiation, etc.
//            System.out.println("Delegate to BuyerAgent's behaviors for negotiation.");
//        }
//
//
//    // Utility method to send a failure response
//    private void sendFailureResponse(ACLMessage originalMessage, String error) {
//
//            ACLMessage failureMessage = originalMessage.createReply();
//            failureMessage.setPerformative(ACLMessage.FAILURE);
//            failureMessage.setContent(error);
//
//        }
//
//
//    @Override
//    public void handle(HttpExchange exchange) throws IOException {
//
//    }
//}
package com.example.jade.Handlers;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

import java.io.OutputStream;
import java.io.IOException;

public class BuyerAgentHandler implements HttpHandler {

    private final AgentContainer mainContainer;

    // Constructor to bind the handler to the agent
    public BuyerAgentHandler(AgentContainer container) {
        this.mainContainer = container;
    }

    // Handle incoming HTTP requests
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("POST".equals(method)) {
            // Handle buy request via HTTP
            try {
                handleBuyProductRequest(exchange);
            } catch (ControllerException e) {
                throw new RuntimeException(e);
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }

    // Handle the "buy-product" HTTP request
    private void handleBuyProductRequest(HttpExchange exchange) throws IOException, ControllerException {
        // Read the HTTP request body (assumed to be JSON)
        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        System.out.println("Received HTTP request body: " + requestBody);

        // Extract product details from the request body (assumes JSON)
        String targetProduct = extractJsonValue(requestBody, "product");

        // Log the product being bought
        System.out.println("BuyerAgent received a purchase request for: " + targetProduct);

        // Simulate the BuyerAgent initiating product purchase
        initiateProductPurchase(targetProduct);

        // Send HTTP response back to the client
        String response = "BuyerAgent is processing the purchase of: " + targetProduct;
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    // Initiate product purchase logic
    private void initiateProductPurchase(String targetProduct) throws ControllerException {
        // Logic to handle product purchase - example:
        System.out.println("Searching for sellers for: " + targetProduct);

        // Verify that the SellerAgent is available
        AgentController sellerAgent = mainContainer.getAgent("SellerAgent");
        if (sellerAgent != null) {
            // Send ACL message to SellerAgent for product purchase
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(new AID("SellerAgent", AID.ISLOCALNAME));
            msg.setContent("BUY_PRODUCT:" + targetProduct);

            // Send the ACL message asynchronously
            try {
                sellerAgent.putO2AObject(msg, AgentController.ASYNC);
                System.out.println("Message sent to SellerAgent: " + msg.getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("SellerAgent not found!");
        }
    }

    // Utility method to extract a value from a JSON string by key
    private String extractJsonValue(String json, String key) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        return jsonObject.get(key).getAsString();
    }
}
