package org.codapayments.loadbalancer.registry;

import org.codapayments.loadbalancer.downstream.Downstream;
import org.codapayments.loadbalancer.dto.OriginalRequest;
import org.codapayments.loadbalancer.dto.OriginalResponse;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinBalancingStrategy<REQ extends OriginalRequest, RES extends OriginalResponse> implements BalancingStrategy<REQ, RES> {
    private final List<Downstream<REQ, RES>> downstreams;
    private final AtomicInteger index = new AtomicInteger(0);

    public RoundRobinBalancingStrategy(List<Downstream<REQ, RES>> downstreams) {
        this.downstreams = downstreams;
    }

    @Override
    public Downstream<REQ,RES> next() {
        int currentIndex = index.getAndUpdate(i -> (i + 1) % downstreams.size());
        return downstreams.get(currentIndex);
    }

    @Override
    public synchronized void removeServer(String identifier) {
        // This is O(n). We can't ditch the list since Map does not provide a way to get by index.
        // Hence, we must choose between extra speed or extra memory.
        downstreams.removeIf(downstream -> downstream.identification().equals(identifier));
    }

    @Override
    public synchronized void addServer(Downstream<REQ, RES> downstream) {
        downstreams.stream().filter(d -> d.identification().equals(downstream.identification()))
                .findAny()
                .ifPresentOrElse(
                        d -> {},
                        () -> downstreams.add(downstream)
                );
    }

    @Override
    public List<Downstream<REQ,RES>> getDownstreams() {
        return downstreams;
    }
}
