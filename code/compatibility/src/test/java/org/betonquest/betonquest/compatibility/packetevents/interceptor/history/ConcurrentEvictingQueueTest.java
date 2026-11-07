package org.betonquest.betonquest.compatibility.packetevents.interceptor.history;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrentEvictingQueueTest {

    @Test
    void keepsNewestElementsInInsertionOrder() {
        final Queue<Integer> queue = new ConcurrentEvictingQueue<>(3);
        for (int i = 1; i <= 5; i++) {
            queue.add(i);
        }
        assertEquals(List.of(3, 4, 5), new ArrayList<>(queue), "Queue should keep only the newest elements in order");
    }

    @Test
    void zeroSizeQueueStaysEmpty() {
        final Queue<Integer> queue = new ConcurrentEvictingQueue<>(0);
        queue.add(1);
        assertTrue(queue.isEmpty(), "Queue with size limit 0 should never hold elements");
    }

    @Test
    void rejectsNegativeSize() {
        assertThrows(IllegalArgumentException.class, () -> new ConcurrentEvictingQueue<>(-1),
                "Negative size limit should be rejected");
    }

    @Test
    void iterationIncludesElementsAddedWhileIterating() {
        final Queue<Integer> queue = new ConcurrentEvictingQueue<>(5);
        queue.add(1);
        queue.add(2);
        final List<Integer> seen = new ArrayList<>();
        for (final Integer element : queue) {
            final boolean first = seen.isEmpty();
            seen.add(element);
            if (first) {
                queue.add(3);
            }
        }
        assertEquals(List.of(1, 2, 3), seen, "Iteration should include elements added while iterating");
    }

    @Test
    @SuppressWarnings({"PMD.DoNotUseThreads", "PMD.CloseResource"})
    void staysWithinLimitUnderConcurrentAddsAndIteration() throws Exception {
        final int limit = 10;
        final Queue<Integer> queue = new ConcurrentEvictingQueue<>(limit);
        final CountDownLatch start = new CountDownLatch(1);
        final List<Future<?>> tasks = new ArrayList<>();
        final ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            for (int thread = 0; thread < 8; thread++) {
                tasks.add(executor.submit(() -> {
                    assertTrue(start.await(10, TimeUnit.SECONDS), "Workers should start together");
                    for (int iteration = 0; iteration < 2000; iteration++) {
                        queue.add(iteration);
                        queue.forEach(Integer::intValue);
                    }
                    return null;
                }));
            }
            start.countDown();
            for (final Future<?> task : tasks) {
                task.get(30, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
        }
        assertTrue(queue.size() <= limit, "Queue should not exceed its limit once all adds have finished");
    }
}
