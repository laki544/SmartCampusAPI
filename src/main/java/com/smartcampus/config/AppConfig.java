package com.smartcampus.config;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS Application configuration.
 * Sets the base URI for all API endpoints to /api/v1
 */
@ApplicationPath("/api/v1")
public class AppConfig extends Application {
    // All resource classes are auto-discovered via web.xml package scanning
}
