package org.codapayments.loadbalancer.downstream;

import org.codapayments.loadbalancer.dto.OriginalRequest;
import org.codapayments.loadbalancer.dto.OriginalResponse;

public interface Downstream<REQ extends OriginalRequest, RES extends OriginalResponse> {
    RES process(REQ request) ;

    boolean isAlive();

    String identification();
}