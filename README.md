# gRPC Gateway Server

This project provides a lightweight gRPC server used to synchronize product stock between the Web Shop and the ERP system in real time.

## Features

- Fetches live stock data directly from the ERP API  
- Uses gRPC for real-time communication  
- Reads configuration from `stock.properties`  
- Containerized with Docker for easy deployment  

## Project Structure

src/
├── main/java/de/fhaachen/si/gateway/
│    ├── StockService.java      # gRPC service logic
│    ├── StockConfig.java       # Configuration loader
│    └── GrpcServer.java        # Entry point
├── resources/
│    └── stock.properties       # ERP credentials and endpoint
Dockerfile
docker-compose.yml

## Configuration

Create a file named `stock.properties` inside `src/main/resources` with the following content:

```properties
erp.endpoint=http://your-erp-endpoint/api/products
erp.username=alice
erp.password=alice

Run with Docker

Build and start the container with one command:

docker compose up -d --build

The server will start on port 9090.

To check logs:

docker logs -f grpc-gateway

Usage

The gRPC server exposes a method getStock(productId) which returns the current stock quantity of a given product from the ERP system.

Once started, the service is available at localhost:9090.

