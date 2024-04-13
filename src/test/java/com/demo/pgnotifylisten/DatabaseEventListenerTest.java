package com.demo.pgnotifylisten;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class DatabaseEventListenerTest {
    @Mock
    private Connection mockConnection;
    @Mock
    private PGConnection pgConnection;
    @Mock
    private Statement mockStatement;
    @Mock
    private BlockingQueue<String> mockBlockingQueue;
    @InjectMocks
    private DatabaseListenerService databaseListenerService;

    //@Test
    public void testListenForDatabaseEvents() throws Exception{

        // Mock PGConnection to return a mock PGNotification array
        PGNotification[] mockNotifications = new PGNotification[] {
            new PGNotification() {
                @Override
                public String getName() {
                    return "demo_channel";
                }

                @Override
                public int getPID() {
                    return 1234;
                }

                @Override
                public String getParameter() {
                    return "New data inserted";
                }
            }
        };

        // Configure mocks
        when(mockConnection.unwrap(PGConnection.class)).thenReturn(pgConnection);
        when(pgConnection.getNotifications()).thenReturn(mockNotifications);
        when(mockStatement.execute("LISTEN demo_channel")).thenReturn(true);

        // Use doAnswer to simulate listening behavior
        doAnswer(invocation -> {
            // Simulate waiting for notifications
            Thread.sleep(500);
            return null;
        }).when(mockConnection).getMetaData();


        // Act
        databaseListenerService.testlisteForDatabaseEvents();
        /*// Use reflection to access the private method
        Method method = DatabaseListenerService.class.getDeclaredMethod("listenForDatabaseEvents");
        method.setAccessible(true);
        // Assuming the private method does not return a value and takes no parameters
        method.invoke(databaseListenerService);*/

        // Assert
        verify(mockStatement, times(1)).execute("LISTEN demo_channel");
        verify(mockBlockingQueue, times(1)).put("New data inserted");
    }

}
