package com.demo.pgnotifylisten;

import jakarta.annotation.PostConstruct;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class DatabaseListenerService {
    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    @Autowired
    private EventProcessorService eventProcessorService;

    @PostConstruct
    public void init() {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(this::listenForDatabaseEvents);
        eventProcessorService.processEvents(queue);
    }

    public void testlisteForDatabaseEvents() {
        listenForDatabaseEvents();
    }
    private void listenForDatabaseEvents() {
        try {
            Connection conn = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword);
            // Cast the connection to a PGConnection to use the PostgreSQL specific methods
            PGConnection pgConn = conn.unwrap(PGConnection.class);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("LISTEN demo_channel");
                while (!Thread.currentThread().isInterrupted()) {
                    // Wait for a notification to be available
                    PGNotification[] notifications = pgConn.getNotifications();
                    if (notifications != null) {
                        for (PGNotification notification : notifications) {
                            System.out.println("Received Notification: " + notification.getParameter());
                            queue.offer(notification.getParameter());
                        }
                    }
                    // Sleep for a short interval to prevent tight looping
                    // System.out.println("Waiting..");
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
