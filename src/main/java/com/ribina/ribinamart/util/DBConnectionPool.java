package com.ribina.ribinamart.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton connection pool managing the HikariCP lifecycle.
 * Ensures zero DriverManager.getConnection() calls exist in DAOs or Controllers.
 */
public final class DBConnectionPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBConnectionPool.class);
    private static volatile HikariDataSource dataSource;

    private DBConnectionPool() {
    }

    public static synchronized void init(String jdbcUrl, String user, String password) {
        if (dataSource != null && !dataSource.isClosed()) {
            LOGGER.warn("HikariDataSource is already initialized");
            return;
        }

        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(15);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(20000);
        config.setPoolName("RibinaMartHikariPool");

        dataSource = new HikariDataSource(config);
        LOGGER.info("HikariCP Connection Pool initialized with URL: {}", jdbcUrl);
    }

    public static synchronized void init(DataSource customDataSource) {
        if (customDataSource instanceof HikariDataSource) {
            dataSource = (HikariDataSource) customDataSource;
        } else {
            throw new IllegalArgumentException("Expected HikariDataSource");
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Connection pool is not initialized or closed");
        }
        return dataSource.getConnection();
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static synchronized void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            LOGGER.info("Shutting down HikariCP Connection Pool...");
            dataSource.close();
            dataSource = null;
            LOGGER.info("HikariCP Connection Pool closed successfully.");
        }
    }

    public static boolean isHealthy() {
        if (dataSource == null || dataSource.isClosed()) {
            return false;
        }
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            return stmt.execute("SELECT 1");
        } catch (SQLException e) {
            LOGGER.error("Health check query failed", e);
            return false;
        }
    }
}
