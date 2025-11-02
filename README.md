# gRPC Gateway Server

A minimal gRPC service that synchronizes product stock between the Web Shop and the ERP system in real time.

---

## Project Structure

```
src/
 └── main/java/de/fhaachen/si/gateway/
      ├── GrpcServer.java        # Entry point
      ├── StockService.java      # gRPC service logic
      └── StockConfig.java       # Configuration loader
src/main/resources/
 └── stock.properties            # ERP credentials and endpoint
Dockerfile
docker-compose.yml
```

---

## Configuration

Create a file named `stock.properties` inside `src/main/resources`:

```properties
erp.endpoint=http://your-erp-endpoint/api/products
erp.username=alice
erp.password=alice
```

---

## Run with Docker

Build and start the container:

```bash
docker compose up -d --build
```

The server will start on **port 9090**.

Check logs:

```bash
docker logs -f grpc-gateway
```

---

## Usage

The gRPC server exposes a method:

```
getStock(productId)
```

It returns the current stock quantity for the given product.  
Once started, the service is available at **localhost:9090**.
