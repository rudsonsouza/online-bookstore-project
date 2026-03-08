package com.bookstore.jms;

import com.bookstore.model.Book;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JMS message producer for sending warehouse notifications
 * when book stock runs out.
 */
@ApplicationScoped
public class OrderMessageProducer {

    private static final Logger LOGGER = Logger.getLogger(OrderMessageProducer.class.getName());

    @Resource(lookup = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "java:/jms/queue/WarehouseQueue")
    private Queue warehouseQueue;

    /**
     * Sends a JMS message to the warehouse queue when a book's stock is depleted.
     */
    public void sendStockDepletedMessage(Book book) {
        try {
            if (connectionFactory != null && warehouseQueue != null) {
                JMSContext context = connectionFactory.createContext();
                String message = String.format(
                        "{\"event\":\"STOCK_DEPLETED\",\"bookId\":%d,\"isbn\":\"%s\",\"bookName\":\"%s\"}",
                        book.getId(), book.getIsbn(), book.getName()
                );
                context.createProducer().send(warehouseQueue, message);
                context.close();
                LOGGER.info("Stock depleted message sent for book: " + book.getName());
            } else {
                LOGGER.warning("JMS resources not available. Logging stock depletion for book: " + book.getName());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to send stock depleted message for book: " + book.getName(), e);
        }
    }

    // Setter for testing
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setWarehouseQueue(Queue warehouseQueue) {
        this.warehouseQueue = warehouseQueue;
    }
}
