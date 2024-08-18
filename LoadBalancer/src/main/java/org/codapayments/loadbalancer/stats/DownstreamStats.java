package org.codapayments.loadbalancer.stats;

import java.util.Optional;

public interface DownstreamStats {
    void incrementSuccess(String identifier);

    void incrementFailure(String identifier);

    Optional<Long> getSuccessCount(String identifier);

    Optional<Long> getFailureCount(String identifier);

    void resetStats(String identifier);
}
