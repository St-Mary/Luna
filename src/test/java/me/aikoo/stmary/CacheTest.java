package me.aikoo.stmary;

import me.aikoo.stmary.core.cache.Cache;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CacheTest {

    @Test
    void addSomeDataToCache_WhenGetData_ThenIsEqualWithCacheElement() {
        Cache<String, String> cache = new Cache<>(3);
        cache.put("1", "test1");
        cache.put("2", "test2");
        cache.put("3", "test3");
        assertEquals("test1", cache.get("1").get());
        assertEquals("test2", cache.get("2").get());
        assertEquals("test3", cache.get("3").get());
    }

    @Test
    void addDataToCacheToTheNumberOfSize_WhenAddOneMoreData_ThenLeastRecentlyDataWillEvict() {
        Cache<String, String> cache = new Cache<>(3);
        cache.put("1", "test1");
        cache.put("2", "test2");
        cache.put("3", "test3");
        cache.put("4", "test4");
        assertFalse(cache.get("1").isPresent());
    }

    @Test
    void runMultiThreadTask_WhenPutDataInConcurrentToCache_ThenNoDataLost() throws Exception {
        final int size = 50;
        final ExecutorService executorService = Executors.newFixedThreadPool(5);
        Cache<Integer, String> cache = new Cache<>(size);
        CountDownLatch countDownLatch = new CountDownLatch(size);
        try {
            IntStream.range(0, size).<Runnable>mapToObj(key -> () -> {
                cache.put(key, "value" + key);
                countDownLatch.countDown();
            }).forEach(executorService::submit);
            countDownLatch.await();
        } finally {
            executorService.shutdown();
        }
        assertEquals(size, cache.size());
        IntStream.range(0, size).forEach(i -> assertEquals("value" + i, cache.get(i).get()));
    }
}
