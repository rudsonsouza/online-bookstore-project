package com.bookstore.config;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS Application configuration.
 * Sets the base path for all REST endpoints to /api.
 * OpenAPI spec is served at /openapi by WildFly's SmallRye OpenAPI subsystem.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Online Bookstore API",
                version = "1.0.0",
                description = "REST API for managing books, customers, shopping carts and orders.",
                contact = @Contact(name = "Bookstore Team")
        ),
        servers = @Server(url = "/online-bookstore/api", description = "Default server")
)
@ApplicationPath("/api")
public class JaxRsApplication extends Application {
}
