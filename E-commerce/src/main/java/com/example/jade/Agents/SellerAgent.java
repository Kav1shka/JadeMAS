//old one

//package com.example.jade.Agents;
//
//import jade.core.Agent;
//import jade.core.behaviours.*;
//import jade.lang.acl.ACLMessage;
//import jade.lang.acl.MessageTemplate;
//import jade.domain.DFService;
//import jade.domain.FIPAException;
//import jade.domain.FIPAAgentManagement.DFAgentDescription;
//import jade.domain.FIPAAgentManagement.ServiceDescription;
//import java.util.concurrent.ConcurrentHashMap;
//@SuppressWarnings("unused")
//public class SellerAgent extends Agent {
//    private ConcurrentHashMap<String, Integer> catalogue;
//
//    protected void setup() {
//        System.out.println("SellerAgent is ready.");
//        setEnabledO2ACommunication(true, 0);
//
//        catalogue = new ConcurrentHashMap<>();
//        catalogue.put("ProductA", 100);
//        catalogue.put("ProductB", 150);
//        System.out.println("SellerAgent started.");
//
//        // Register the selling service
//        DFAgentDescription dfd = new DFAgentDescription();
//        dfd.setName(getAID());
//        ServiceDescription sd = new ServiceDescription();
//        sd.setType("product-selling");
//        sd.setName("JADE-product-trading");
//        dfd.addServices(sd);
//        try {
//            DFService.register(this, dfd);
//        } catch (FIPAException fe) {
//            fe.printStackTrace();
//        }
//
//        addBehaviour(new OfferRequestsServer());
//        addBehaviour(new PurchaseOrdersServer());
//        addBehaviour(new ProductAddBehaviour());
//    }
//    protected void takeDown() {
//        // Deregister the agent from the DFService
//        try {
//            DFService.deregister(this);
//        } catch (FIPAException fe) {
//            fe.printStackTrace();
//        }
//        System.out.println(getAID().getName() + " terminated.");
//    }
//
//    /**
//     * Behavior to handle adding new products to the catalogue.
//     */
//    private class ProductAddBehaviour extends CyclicBehaviour {
//
//        @Override
//        public void action() {
//            ACLMessage msg = myAgent.receive();
//            System.out.println("msg: "+msg);
//            if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
//                try {
//                String[] content = msg.getContent().split(":");
//                String operation = content[0];
//                    System.out.println("xxx");
//                    if ("ADD_PRODUCT".equals(operation)) {
//                        String productTitle = content[1];
//                        int productPrice = Integer.parseInt(content[2]);
//
//                        if (productTitle != null && !productTitle.isEmpty() && productPrice > 0) {
//                            catalogue.put(productTitle, productPrice);
//                            sendSuccessResponse(msg, "Product added successfully: " + productTitle);
//                            System.out.println("Product added: " + productTitle + " with price " + productPrice);
//                        } else {
//                            sendErrorResponse(msg, "Invalid product details");
//                        }
//                    } else {
//                        sendErrorResponse(msg, "Unknown operation: " + operation);
//                    }
//                } catch (Exception e) {
//                    sendErrorResponse(msg, "Invalid message format or data");
//                }
//            } else {
//                System.out.println("No valid message received. Blocking...");
//                block(); // No message received, block the behaviour
//            }
//        }
//
//        private void sendErrorResponse(ACLMessage originalMessage, String errorMessage) {
//            ACLMessage reply = originalMessage.createReply();
//            reply.setPerformative(ACLMessage.FAILURE);
//            reply.setContent(errorMessage);
//            myAgent.send(reply);
//            System.err.println("Error response sent: " + errorMessage);
//        }
//
//        private void sendSuccessResponse(ACLMessage originalMessage, String successMessage) {
//            ACLMessage reply = originalMessage.createReply();
//            reply.setPerformative(ACLMessage.INFORM);
//            reply.setContent(successMessage);
//            myAgent.send(reply);
//        }
//    }
//
//    private class OfferRequestsServer extends CyclicBehaviour {
//        public void action() {
//            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
//            ACLMessage msg = myAgent.receive(mt);
//            if (msg != null) {
//                String title = msg.getContent();
//                ACLMessage reply = msg.createReply();
//                Integer price = catalogue.get(title);
//                if (price != null) {
//                    reply.setPerformative(ACLMessage.PROPOSE);
//                    reply.setContent(String.valueOf(price));
//                } else {
//                    reply.setPerformative(ACLMessage.REFUSE);
//                    reply.setContent("not-available");
//                }
//                myAgent.send(reply);
//            } else {
//                block();
//            }
//        }
//    }
//
//    private class PurchaseOrdersServer extends CyclicBehaviour {
//        public void action() {
//            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
//            ACLMessage msg = myAgent.receive(mt);
//            if (msg != null) {
//                String title = msg.getContent();
//                ACLMessage reply = msg.createReply();
//                Integer price = catalogue.remove(title);
//                if (price != null) {
//                    reply.setPerformative(ACLMessage.INFORM);
//                    System.out.println(title + " sold to agent " + msg.getSender().getName());
//                } else {
//                    reply.setPerformative(ACLMessage.FAILURE);
//                    reply.setContent("not-available");
//                }
//                myAgent.send(reply);
//            } else {
//                block();
//            }
//        }
//    }
//
//}
//
//

//previous one
package com.example.jade.Agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.concurrent.ConcurrentHashMap;
@SuppressWarnings("unused")
public class SellerAgent extends Agent {
    //    private ConcurrentHashMap<String, Integer> catalogue;
//
//    @Override
//    protected void setup() {
//        System.out.println("SellerAgent is ready.");
//        // Enable O2A communication
//        setEnabledO2ACommunication(true, 0);
//
//        catalogue = new ConcurrentHashMap<>();
//
//        // Add a behavior to process O2A objects
//        addBehaviour(new O2AProcessingBehaviour());
//    }
//
//    private class O2AProcessingBehaviour extends CyclicBehaviour {
//        @Override
//        public void action() {
//            try {
//                // Retrieve an object from the O2A queue
//                Object obj = myAgent.getO2AObject();
//                if (obj != null) {
//                    // Process the object (e.g., ACLMessage)
//                    if (obj instanceof ACLMessage) {
//                        ACLMessage msg = (ACLMessage) obj;
//                        System.out.println("Received O2A message: " + msg.getContent());
//                        processMessage(msg);
//                    } else {
//                        System.out.println("Unknown object received via O2A: " + obj);
//                    }
//                } else {
//                    block(); // No object in the queue, block the behavior
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        private void processMessage(ACLMessage msg) {
//            try {
//                String[] content = msg.getContent().split(":");
//                String operation = content[0];
//
//                if ("ADD_PRODUCT".equals(operation)) {
//                    String productTitle = content[1];
//                    int productPrice = Integer.parseInt(content[2]);
//
//                    if (productTitle != null && !productTitle.isEmpty() && productPrice > 0) {
//                        catalogue.put(productTitle, productPrice);
//                        System.out.println("Product added via O2A: " + productTitle + " with price " + productPrice);
//                    } else {
//                        System.out.println("Invalid product details received via O2A.");
//                    }
//                } else {
//                    System.out.println("Unknown operation received via O2A: " + operation);
//                }
//            } catch (Exception e) {
//                System.out.println("Error processing O2A message: " + e.getMessage());
//            }
//        }
//    }
    private ConcurrentHashMap<String, Product> catalogue;

    // Inner Product class for more robust management
    private static class Product {
        String id;
        String title;
        int price;

        Product(String id, String title, int price) {
            this.id = id;
            this.title = title;
            this.price = price;
        }
    }

    @Override
    protected void setup() {
        try {
            catalogue = new ConcurrentHashMap<>();
            System.out.println("SellerAgent Setup Starting...");
            setEnabledO2ACommunication(true, 0);
            addBehaviour(new O2AProcessingBehaviour());
            System.out.println("SellerAgent Setup Completed Successfully.");
        } catch (Exception e) {
            System.err.println("SellerAgent Setup Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
     private class O2AProcessingBehaviour extends CyclicBehaviour {
            @Override
            public void action() {
                try {

                    Object obj = myAgent.getO2AObject();
                    if (obj instanceof ACLMessage) {
                        processMessage((ACLMessage) obj);
                    } else if (obj != null) {
                        System.out.println("Unhandled object type: " + obj.getClass());
                    } else {
                        block();
                    }
                } catch (Exception e) {
                    System.err.println("O2A Processing Error: " + e.getMessage());
                    e.printStackTrace();
                    block(500); // Longer block on error
                }
            }

            private void processMessage(ACLMessage msg) {
                try {
                    String[] parts = msg.getContent().split(":");
                    String operation = parts[0];

                    switch (operation) {
                        case "ADD_PRODUCT":
                            addProduct(parts[1], parts[2], Integer.parseInt(parts[3]));
                            break;
                        case "UPDATE_PRODUCT":
                            updateProduct(parts[1], parts[2], Integer.parseInt(parts[3]));
                            break;
                        case "DELETE_PRODUCT":
                            deleteProduct(parts[1]);
                            break;
                        default:
                            System.out.println("Unknown operation: " + operation);
                    }
                } catch (Exception e) {
                    System.err.println("Message processing error: " + e.getMessage());
                }
            }

            private void addProduct(String id, String title, int price) {
                catalogue.put(id, new Product(id, title, price));
                System.out.println("Product added: " + title);
            }

            private void updateProduct(String id, String newTitle, int newPrice) {
                if (catalogue.containsKey(id)) {
                    catalogue.put(id, new Product(id, newTitle, newPrice));
                    System.out.println("Product updated: " + id);
                }
            }

            private void deleteProduct(String id) {
                if (catalogue.remove(id) != null) {
                    System.out.println("Product deleted: " + id);
                }
            }
        }

    }
