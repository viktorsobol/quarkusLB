package org.codapayments.loadbalancer.registry;

import org.codapayments.loadbalancer.downstream.Downstream;
import org.codapayments.loadbalancer.dto.OriginalRequest;
import org.codapayments.loadbalancer.dto.OriginalResponse;

import java.util.List;

public interface BalancingStrategy<REQ extends OriginalRequest, RES extends OriginalResponse> {

    Downstream<REQ,RES> next();

    void removeServer(String identifier);

    void addServer(Downstream<REQ,RES> downstream);

    List<Downstream<REQ,RES>> getDownstreams();

}