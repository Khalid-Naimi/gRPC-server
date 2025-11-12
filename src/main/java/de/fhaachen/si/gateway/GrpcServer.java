package de.fhaachen.si.gateway;

import de.fhaachen.si.gateway.config.Config;
import de.fhaachen.si.gateway.order.OrderService;
import de.fhaachen.si.gateway.product.ProductService;
import de.fhaachen.si.gateway.stock.StockService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServer {
    public static void main(String[] args) throws Exception {

        Config config = new Config();

        Server server = ServerBuilder.forPort(9090)
                .addService(new StockService(config))
                .addService(new OrderService(config))
                .addService(new ProductService(config))
                .build()
                .start();

        System.out.println("gRPC Server started on port 9090");
        server.awaitTermination();
    }
}
