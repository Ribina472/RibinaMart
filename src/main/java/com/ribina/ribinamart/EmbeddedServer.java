package com.ribina.ribinamart;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

/**
 * Standalone Embedded Tomcat launcher enabling 1-click execution
 * for local development, grading, and demonstration.
 */
public class EmbeddedServer {

    public static void main(String[] args) throws Exception {
        int port = 8085;
        String portEnv = System.getenv("PORT");
        if (portEnv != null && !portEnv.trim().isEmpty()) {
            try {
                port = Integer.parseInt(portEnv.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        String portProp = System.getProperty("server.port");
        if (portProp != null && !portProp.trim().isEmpty()) {
            try {
                port = Integer.parseInt(portProp.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir("catalina_base");
        tomcat.getConnector(); // triggers default HTTP connector initialization

        String webappDir = new File("src/main/webapp").getAbsolutePath();
        StandardContext ctx = (StandardContext) tomcat.addWebapp("/ribinamart", webappDir);
        ctx.setParentClassLoader(EmbeddedServer.class.getClassLoader());

        File additionWebInfClasses = new File("target/classes");
        if (additionWebInfClasses.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    additionWebInfClasses.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        System.out.println("================================================================================");
        System.out.println("🚀 RibinaMart Capstone E-Commerce Platform starting on port " + port);
        System.out.println("👉 Application URL:  http://localhost:" + port + "/ribinamart");
        System.out.println("👉 Health Check:     http://localhost:" + port + "/ribinamart/api/v1/health");
        System.out.println("👉 Browse Catalog:   http://localhost:" + port + "/ribinamart/products");
        System.out.println("👉 Seed Accounts:    admin@ribinamart.com, seller1@ribinamart.com, buyer1@ribinamart.com");
        System.out.println("================================================================================");

        tomcat.start();
        tomcat.getServer().await();
    }
}
