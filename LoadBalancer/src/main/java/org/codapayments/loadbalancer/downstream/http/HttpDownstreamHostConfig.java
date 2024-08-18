package org.codapayments.loadbalancer.downstream.http;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;

import java.util.List;

@StaticInitSafe
@ConfigMapping(prefix = "downstream")
public interface HttpDownstreamHostConfig {

    List<HttpDownstreamHost> hosts();

}
