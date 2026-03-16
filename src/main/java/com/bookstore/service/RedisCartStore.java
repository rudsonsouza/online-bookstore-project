package com.bookstore.service;

import com.bookstore.model.Cart;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.util.logging.Level;
import java.util.logging.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis-based cart storage.
 * Carts are stored with a TTL (Time to Live) of 24 hours.
 */
@ApplicationScoped
public class RedisCartStore {

    private static final Logger LOGGER = Logger.getLogger(RedisCartStore.class.getName());
    private static final String CART_KEY_PREFIX = "cart:";
    private static final int CART_TTL_SECONDS = 86400; // 24 hours

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JedisPool jedisPool;

    @PostConstruct
    public void init() {
        String redisHost = System.getProperty("redis.host", "redis");
        int redisPort = Integer.parseInt(System.getProperty("redis.port", "6379"));

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);

        try {
            jedisPool = new JedisPool(poolConfig, redisHost, redisPort);
            LOGGER.info("Redis connection pool initialized: " + redisHost + ":" + redisPort);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize Redis connection pool", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }

    public Cart getCart(Long customerId) {
        String key = CART_KEY_PREFIX + customerId;
        try (Jedis jedis = jedisPool.getResource()) {
            String data = jedis.get(key);
            if (data == null) {
                return null;
            }
            return deserialize(data);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to get cart from Redis for customer " + customerId, e);
            return null;
        }
    }

    public void saveCart(Cart cart) {
        String key = CART_KEY_PREFIX + cart.getCustomerId();
        try (Jedis jedis = jedisPool.getResource()) {
            String data = serialize(cart);
            jedis.setex(key, CART_TTL_SECONDS, data);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to save cart to Redis for customer " + cart.getCustomerId(), e);
        }
    }

    public void deleteCart(Long customerId) {
        String key = CART_KEY_PREFIX + customerId;
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to delete cart from Redis for customer " + customerId, e);
        }
    }

    private String serialize(Cart cart) throws Exception {
        return OBJECT_MAPPER.writeValueAsString(cart);
    }

    private Cart deserialize(String data) throws Exception {
        return OBJECT_MAPPER.readValue(data, Cart.class);
    }

    /**
     * Setter for testing purposes.
     */
    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
}
