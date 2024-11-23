package com.example.jade.Agents;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.concurrent.ConcurrentHashMap;
@SuppressWarnings("unused")
public class SellerAgent extends Agent {
    private ConcurrentHashMap<String, Integer> catalogue;

    protected void setup() {
        catalogue = new ConcurrentHashMap<>();
        catalogue.put("ProductA", 100);
        catalogue.put("ProductB", 150);
        System.out.println("SellerAgent started.");
        // Register the selling service
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("product-selling");
        sd.setName("JADE-product-trading");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new OfferRequestsServer());
        addBehaviour(new PurchaseOrdersServer());
        addBehaviour(new ProductAddBehaviour());
    }
    protected void takeDown() {
        // Deregister the agent from the DFService
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println(getAID().getName() + " terminated.");
    }

    /**
     * Behavior to handle adding new products to the catalogue.
     */
    private class ProductAddBehaviour extends CyclicBehaviour {
        private ConcurrentHashMap<String, Integer> catalogue = new ConcurrentHashMap<>();

        public ProductAddBehaviour() {
            this.catalogue = catalogue;
        }

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            System.out.println("msg: "+msg);
            if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
                try {
                String[] content = msg.getContent().split(":");
                String operation = content[0];

                if ("ADD_PRODUCT".equals(operation)) {
                    String productTitle = content[1];
                    int productPrice;
                    try {
                        productPrice = Integer.parseInt(content[2]);
                    } catch (NumberFormatException e) {
                        sendErrorResponse(msg, "Invalid price format");
                        return;
                    }

                    // Validate product
                    if (productTitle == null || productTitle.isEmpty() || productPrice <= 0) {
                        sendErrorResponse(msg, "Invalid product details");
                        return;
                    }

                    // Add product to the catalogue
                    synchronized (catalogue) {
                        if (catalogue.containsKey(productTitle)) {
                            sendErrorResponse(msg, "Product already exists");
                        } else {
                            catalogue.put(productTitle, productPrice);
                            sendSuccessResponse(msg, "Product added successfully: " + productTitle);
                            System.out.println("Product added: " + productTitle + " with price " + productPrice);
                        }
                    }
                } else {
                    sendErrorResponse(msg, "Unknown operation: " + operation);
                }
                } catch (Exception e) {
                    sendErrorResponse(msg, "Invalid message format or data");
                }
            } else {
                block(); // No message received, block the behaviour
            }
        }

        private void sendErrorResponse(ACLMessage originalMessage, String errorMessage) {
            ACLMessage reply = originalMessage.createReply();
            reply.setPerformative(ACLMessage.FAILURE);
            reply.setContent(errorMessage);
            myAgent.send(reply);
            System.err.println("Error response sent: " + errorMessage);
        }

        private void sendSuccessResponse(ACLMessage originalMessage, String successMessage) {
            ACLMessage reply = originalMessage.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(successMessage);
            myAgent.send(reply);
        }
    }

    private class OfferRequestsServer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String title = msg.getContent();
                ACLMessage reply = msg.createReply();
                Integer price = catalogue.get(title);
                if (price != null) {
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent(String.valueOf(price));
                } else {
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("not-available");
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }

    private class PurchaseOrdersServer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String title = msg.getContent();
                ACLMessage reply = msg.createReply();
                Integer price = catalogue.remove(title);
                if (price != null) {
                    reply.setPerformative(ACLMessage.INFORM);
                    System.out.println(title + " sold to agent " + msg.getSender().getName());
                } else {
                    reply.setPerformative(ACLMessage.FAILURE);
                    reply.setContent("not-available");
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }

}


