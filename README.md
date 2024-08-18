## Setup

1. Ensure that you have java > 17, python and docker-compose
2. Run `bash setup_env.sh`
3. Run `python3 send_requests.py` to send requests to a server


### Additionally
1. Remove any of the loadbalancing container and observe that after sometime the system will detect and stop using faulty server. 


## Code structure

`LoadBalancer` folder contains the java project for load balancer. 

`application.properties` contain the original list of servers. 


`HttpLoadBalancer` is the class responsible for load balancing. `HttpLoadBalancer` has the following concerns:
- Choosing particular LB strategy (without knowing the implementation details)
- Deciding how often to reconcile the list of healthy servers
- Maintaining the original config of servers


`HttpLoadBalancer` has `BalancingStrategy`.
`BalancingStrategy` is an interface which can implement different balancing strategy. 

`Downstream` is an interface for implementation for different downstream types. Right now only HttpDownstream is implemented but it easily can be extended to just tcp downstream or grpc. 

