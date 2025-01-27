
# Load Balancer

## Architecture Overview

The LB sits between the clients and backend servers, distributing incoming traffic based on custom routing logic.

* Load Balancer: Manages incoming traffic and routes requests to the appropriate backend server based on routing logic.

* Backend Servers: Homogeneous servers that process requests and handle picture uploads/downloads.

* Monitoring System: Tracks server health, workload, and metrics for efficient traffic routing.

## Load Balancer Components

#### Routing Logic
* Session Persistence (Sticky Sessions): For authenticated requests, the load balancer will maintain session persistence using JWT. This ensures that all user-specific requests (e.g., shopping cart actions) are routed to the same backend server.

* Workload-Based Routing: For picture upload/download endpoints and all unauthenticated requests, the load balancer will monitor the workload on backend servers and route requests to the server with the least load.

#### Fallback Routing
If a server is unresponsive or overloaded, the request will be routed to the next available server.

#### Health Checks
Active health checks will verify:
* Server responsiveness (e.g., HTTP 200 status).
* Workload thresholds (e.g., CPU, memory, and network utilization).
  Unhealthy servers will be removed from the pool until they recover.

#### Traffic Distribution
* HTTPS Termination: The load balancer will terminate HTTPS traffic to offload SSL processing from backend servers.
* Dynamic Scaling: Support for adding/removing servers dynamically by updating the backend pool.
