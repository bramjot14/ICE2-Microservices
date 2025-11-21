# microservices-parent (ICE 2)

- `product-service` (MongoDB + Redis cache)
- `inventory-service` (PostgreSQL)
- `order-service` (PostgreSQL, calls inventory through API Gateway)
- `api-gateway` (Spring Cloud Gateway, all external traffic goes through here)

## How to run 

1. From the root (`microservices-parent`), run:

   ```bash
   mvn clean package -DskipTests
   ```

   This builds all microservice JARs.
2. Start the full Docker setup:

   ```bash
   docker compose up --build
   ```

3. Test from Postman **through the gateway only**:

   - Base URL: `http://localhost:8080`
   - Product endpoints (proxied to product-service):
     - `GET  /api/product`
     - `POST /api/product`
   - Inventory endpoints (proxied to inventory-service):
     - `GET  /api/inventory?skuCode=ABC-123`
     - `POST /api/inventory`
   - Order endpoints (proxied to order-service):
     - `POST /api/order`

4. Redis and Mongo helpers:

   - Redis Insight: `http://localhost:5540`
   - Mongo Express: `http://localhost:8084`
   - pgAdmin: `http://localhost:5050`

## Where ICE 2 items live

- **API Gateway only**: `api-gateway` module (`application.yml` has routes).
- **Redis cache for products**: `product-service` module (`ProductService`).
- **order → inventory call**: `order-service` module (`OrderService`).
- **Testcontainers** integration tests:
  - `product-service/src/test/.../ProductServiceApplicationCacheTests.java`
  - `inventory-service/src/test/.../InventoryServiceIntegrationTest.java`
  - `order-service/src/test/.../OrderServiceIntegrationTest.java`
