package org.codapayments.loadbalancer;

import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.codapayments.loadbalancer.downstream.Downstream;
import org.codapayments.loadbalancer.downstream.http.HttpDownstream;
import org.codapayments.loadbalancer.downstream.http.HttpDownstreamHostConfig;
import org.codapayments.loadbalancer.dto.http.HttpOriginalRequest;
import org.codapayments.loadbalancer.dto.http.HttpOriginalResponse;
import org.codapayments.loadbalancer.registry.BalancingStrategy;
import org.codapayments.loadbalancer.registry.RoundRobinBalancingStrategy;
import org.codapayments.loadbalancer.stats.DownstreamStats;
import org.codapayments.loadbalancer.stats.InMemoryDownstreamStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@ApplicationScoped
public class HttpLoadBalancer {

    private final Logger logger = LoggerFactory.getLogger(HttpLoadBalancer.class);

    @Inject
    HttpDownstreamHostConfig httpDownstreamHostConfig;

    private  BalancingStrategy<HttpOriginalRequest, HttpOriginalResponse> balancingStrategy;

    private DownstreamStats downstreamStats;

    private List<Downstream<HttpOriginalRequest, HttpOriginalResponse>> downstreams;

    @PostConstruct
    public void init() {
        this.downstreams = httpDownstreamHostConfig.hosts().stream().map(
                HttpDownstream::new
        ).collect(Collectors.toList());

        this.balancingStrategy = new RoundRobinBalancingStrategy<>(downstreams);
        this.downstreamStats = new InMemoryDownstreamStats();
        logger.info("Initializing load balancer");
        downstreams.forEach(downstream -> {
            if (downstream.isAlive()) {
                balancingStrategy.addServer(downstream);
            } else {
                logger.warn("Adding downstream: {} to quarantine", downstream.identification());
            }
        });
        logger.info("Initialized load balancer");
    }

    public HttpOriginalResponse route(HttpOriginalRequest request) {
        logger.debug("Processing request ");
        Downstream<HttpOriginalRequest, HttpOriginalResponse> downstream = balancingStrategy.next();
        logger.debug("Routing request to downstream: {}", downstream.identification());
        try {
            HttpOriginalResponse response = downstream.process(request);
            downstreamStats.incrementSuccess(downstream.identification());
            return response;
        } catch (Exception e) {
            logger.error("Downstream failed: {}", downstream.identification());
            logger.debug("Downstream failed: {}", downstream.identification(), e);
            downstreamStats.incrementFailure(downstream.identification());
            throw e;
        }
    }


    @Scheduled(every="10s")
    public void reconcileStats() {
        logger.info("Reconciling downstream stats");
        balancingStrategy.getDownstreams().forEach(
                downstream -> {
                    Optional<Long> successCount = downstreamStats.getSuccessCount(downstream.identification());
                    Optional<Long> failureCount = downstreamStats.getFailureCount(downstream.identification());

                    if (failureCount.orElse(0L) > successCount.orElse(0L)) {
                        logger.warn("Removing downstream: {} due to high failure rate", downstream.identification());
                        balancingStrategy.removeServer(downstream.identification());
                    }
                }
        );
        logger.info("Reconciled downstream stats");
    }

    @Scheduled(every="10s")
    public void reconcileDownstreamsInQuarantine() {
        logger.info("Reconciling downstreams in quarantine");
        List<Downstream<HttpOriginalRequest, HttpOriginalResponse>> strategyDownstreams = balancingStrategy.getDownstreams();
        List<Downstream<HttpOriginalRequest, HttpOriginalResponse>> downstreamsInQuarantine = downstreams.stream()
                .filter(downstream -> !strategyDownstreams.contains(downstream))
                .toList();

        downstreamsInQuarantine.forEach(downstream -> {
            if (downstream.isAlive()) {
                logger.info("Adding downstream: {} back to the list", downstream.identification());
                balancingStrategy.addServer(downstream);
            }
        });
        logger.info("Reconciled downstreams in quarantine");
    }
}




