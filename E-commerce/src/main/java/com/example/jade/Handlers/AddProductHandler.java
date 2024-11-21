package com.example.jade.Handlers;

import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
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

                System.out.println(title);
                System.out.println(price);

                // Verify that the SellerAgent is available
                AgentController sellerAgent = mainContainer.getAgent("SellerAgent");
                if (sellerAgent == null) {
                    System.out.println("SellerAgent not found in container!");
                    // Optionally, create and start it if not found
                } else {
                    System.out.println("SellerAgent found, ready for interaction.");
                    // Now you can interact with the agent

                    // Notify the seller agent
                    sellerAgent.putO2AObject(new Object[] { title, price }, AgentController.ASYNC);

                }





                System.out.println(sellerAgent);

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

//        String searchKey = "\"" + key + "\":";
//        int startIndex = json.indexOf(searchKey) + searchKey.length();
//
//        int endIndex = json.indexOf(",", startIndex);
//
//        if (endIndex == -1) endIndex = json.indexOf("}", startIndex);
//        return json.substring(startIndex, endIndex).replaceAll("[\"\\s]", "");
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        return jsonObject.get(key).getAsString();
    }
}