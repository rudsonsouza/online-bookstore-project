package com.bookstore.config;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS Application configuration.
 * Sets the base path for all REST endpoints to /api.
 */
@ApplicationPath("/api")
public class JaxRsApplication extends Application {
}
