package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.util.DBConnectionPool;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public class TestDBHelper {

    private static boolean initialized = false;

    public static synchronized void setupTestDatabase() throws Exception {
        if (!initialized) {
            DBConnectionPool.init("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
            runScript("test-schema.sql");
            initialized = true;
        }
    }

    public static void clearTables() throws Exception {
        try (Connection conn = DBConnectionPool.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM wishlist_items");
            stmt.execute("DELETE FROM reviews");
            stmt.execute("DELETE FROM order_items");
            stmt.execute("DELETE FROM orders");
            stmt.execute("DELETE FROM cart_items");
            stmt.execute("DELETE FROM products");
            stmt.execute("DELETE FROM users");
        }
    }

    private static void runScript(String resourceName) throws Exception {
        try (InputStream is = TestDBHelper.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new IllegalStateException("Cannot find resource: " + resourceName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                 Connection conn = DBConnectionPool.getConnection();
                 Statement stmt = conn.createStatement()) {

                StringBuilder sql = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("--") || line.isEmpty()) continue;
                    sql.append(line).append(" ");
                    if (line.endsWith(";")) {
                        String execSql = sql.toString().replace(";", "").trim();
                        if (!execSql.isEmpty()) {
                            stmt.execute(execSql);
                        }
                        sql.setLength(0);
                    }
                }
            }
        }
    }
}
