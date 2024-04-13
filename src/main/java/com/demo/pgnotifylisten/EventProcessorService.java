package com.demo.pgnotifylisten;

import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class EventProcessorService {
    public void processEvents(BlockingQueue<String> queue) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            while (true) {
                try {
                    String payload = queue.take();
                    System.out.println("Processing: " + payload);
                    // Process the payload
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }
}
