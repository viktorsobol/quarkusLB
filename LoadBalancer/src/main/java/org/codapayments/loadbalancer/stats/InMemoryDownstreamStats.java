package org.codapayments.loadbalancer.stats;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryDownstreamStats implements DownstreamStats {


    private final Map<String, AtomicLong> successCount;
    private final Map<String, AtomicLong> failureCount;

    public InMemoryDownstreamStats() {
        this.successCount = new ConcurrentHashMap<>();
        this.failureCount = new ConcurrentHashMap<>();
    }

    @Override
    public void incrementSuccess(String identifier) {
        successCount.computeIfAbsent(identifier, k -> new AtomicLong(0)).incrementAndGet();
    }

    @Override
    public void incrementFailure(String identifier) {
        failureCount.computeIfAbsent(identifier, k -> new AtomicLong(0)).incrementAndGet();
    }

    @Override
    public Optional<Long> getSuccessCount(String identifier) {
        return Optional.ofNullable(successCount.get(identifier)).map(AtomicLong::get);    }

    @Override
    public Optional<Long> getFailureCount(String identifier) {
        return Optional.ofNullable(failureCount.get(identifier)).map(AtomicLong::get);
    }

    @Override
    public void resetStats(String identifier) {
        successCount.remove(identifier);
        failureCount.remove(identifier);
    }
}
