package com.bookstore.jms;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JMS Message Driven Bean that listens for warehouse notifications.
 * Processes stock depletion events to trigger restocking.
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/WarehouseQueue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})
public class WarehouseMessageListener implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(WarehouseMessageListener.class.getName());

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String content = textMessage.getText();
                LOGGER.info("========== WAREHOUSE NOTIFICATION ==========");
                LOGGER.info("Received stock depletion notification: " + content);
                LOGGER.info("Action: Initiating restocking process...");
                LOGGER.info("============================================");

                // In a real application, this would:
                // 1. Parse the message
                // 2. Create a restocking request
                // 3. Notify warehouse management system
                // 4. Send alerts to inventory managers
            }
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Error processing warehouse message", e);
        }
    }
}
