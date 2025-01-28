
# Load Balancer

## Architecture Overview

The LB sits between the clients and backend servers, distributing incoming traffic based on custom routing logic.

* Load Balancer: Manages incoming traffic and routes requests to the appropriate backend server based on routing logic.

* Backend Servers: Homogeneous servers that process requests and handle picture uploads/downloads.

* Monitoring System: Tracks server health, workload, and metrics for efficient traffic routing.

## Load Balancer Components

#### Routing Logic
* [ ] Session Persistence (Sticky Sessions): For authenticated requests, the load balancer will maintain session 
persistence using JWT. This ensures that all user-specific requests (e.g., shopping cart actions) are routed to the 
same backend server.

* [X] Workload-Based Routing: For picture upload/download endpoints and all unauthenticated requests, the load balancer
will monitor the workload on backend servers and route requests to the server with the least load.

#### Fallback Routing
* [ ] If a server is unresponsive or overloaded, the request will be routed to the next available server.

#### Health Checks
Active health checks will verify:
* [X] Server responsiveness (e.g., HTTP 200 status).
* [X] Workload thresholds (e.g., CPU, memory, and network utilization).
  Unhealthy servers will be removed from the pool until they recover.

#### Traffic Distribution
HTTPS Termination: 
* [ ] The load balancer will terminate HTTPS traffic to offload SSL processing from backend servers.

Dynamic Scaling:
* [X] **Adding Servers**: When a new server is launched, it registers to the LB using the `addServer` method.
* [ ] **Removing Servers**: Unhealthy or unnecessary servers can be removed with the `removeServer` method.
* [X] **Automatic Health Checks**: The system automatically checks the health of all servers and removes any that fail
  specific amount of health checks, ensuring that traffic is only sent to healthy servers.


### Request Flow:

1. **Client Request**: The client sends an HTTP request to the Load Balancer.
2. **Routing Logic**: Based on the request type, if the user is authenticated, and server workload, the load balancer 
determines which is the best server to process the request.
3. **Forwarding the Request**: The LB forwards the request asynchronously to the selected backend server.
4. **Response Handling**: Once the backend server responds, the load balancer forwards the response back to the 
initial user.
5. **Error Handling**: If an error occurs while redirecting the request, fallback mechanism ensures that the 
request is routed to another server or a proper error response is sent back, if there are no healthy servers, which 
can process the request,