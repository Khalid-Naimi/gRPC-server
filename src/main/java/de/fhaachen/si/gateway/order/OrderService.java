package de.fhaachen.si.gateway.order;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import si.OrderGrpc;
import si.OrderIdRequest;
import si.OrderItem;
import si.OrderRequest;
import si.OrderResponse;
import si.OrderStatusRequest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.fhaachen.si.gateway.config.Config;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class OrderService extends OrderGrpc.OrderImplBase {

	private final Config config;
	private final Gson gson = new Gson();

	public OrderService(Config config) {
		this.config = config;
	}
    /**
     * Creates a new Order in the ERP system via its REST API.
     */
    @Override
    public void createOrder(OrderRequest req, StreamObserver<OrderResponse> res) {
        try {
            String basicAuth = "Basic " + Base64.getEncoder()
                    .encodeToString((config.username() + ":" + config.password()).getBytes(StandardCharsets.UTF_8));

            // Prepare payload structure matching ERP's expectations
            JsonObject payload = new JsonObject();
            payload.addProperty("customer_ID", req.getCustomerId());
            payload.addProperty("orderDate", req.getOrderDate());
            payload.addProperty("orderAmount", req.getOrderAmount());
            payload.addProperty("currency_code", req.getCurrency());

            JsonArray itemsArray = new JsonArray();
            for (OrderItem item : req.getItemsList()) {
                JsonObject itemObj = new JsonObject();
                itemObj.addProperty("product_ID", item.getProductId());
                itemObj.addProperty("quantity", item.getQuantity());
                itemObj.addProperty("itemAmount", item.getItemAmount());
                itemObj.addProperty("currency_code", item.getCurrency());
                itemsArray.add(itemObj);
            }
            payload.add("items", itemsArray);

            // Send HTTP POST to ERP (likely to /orders or /Orders)
            HttpRequest httpReq = HttpRequest.newBuilder()
                    .uri(URI.create(config.endpoint() + "/orders"))
                    .header("Authorization", basicAuth)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(payload)))
                    .build();

            HttpResponse<String> httpResp = HttpClient.newHttpClient()
                    .send(httpReq, HttpResponse.BodyHandlers.ofString());

            if (httpResp.statusCode() == 200 || httpResp.statusCode() == 201) {
                JsonObject jsonResp = JsonParser.parseString(httpResp.body()).getAsJsonObject();
                String orderId = jsonResp.has("orderID") ? jsonResp.get("orderID").getAsString() : "unknown";
                res.onNext(OrderResponse.newBuilder()
                        .setOrderId(orderId)
                        .setStatus("CREATED")
                        .setMessage("Order created successfully")
                        .build());
            } else {
                res.onNext(OrderResponse.newBuilder()
                        .setMessage("ERP returned HTTP " + httpResp.statusCode())
                        .setStatus("FAILED")
                        .build());
            }
            res.onCompleted();

        } catch (Exception e) {
            e.printStackTrace();
            res.onError(Status.INTERNAL.withDescription("Failed to create order: " + e.getMessage()).asRuntimeException());
        }
    }

    /**
     * Fetches a single order by ID from the ERP.
     */
    @Override
    public void getOrderById(OrderIdRequest req, StreamObserver<OrderResponse> res) {
        try {
            String basicAuth = "Basic " + Base64.getEncoder()
                    .encodeToString((config.username() + ":" + config.password()).getBytes(StandardCharsets.UTF_8));

            String url = config.endpoint() + "/Orders(" + req.getOrderId() + ")";
            HttpRequest httpReq = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", basicAuth)
                    .GET()
                    .build();

            HttpResponse<String> httpResp = HttpClient.newHttpClient()
                    .send(httpReq, HttpResponse.BodyHandlers.ofString());

            if (httpResp.statusCode() == 200) {
                JsonObject jsonResp = JsonParser.parseString(httpResp.body()).getAsJsonObject();
                res.onNext(OrderResponse.newBuilder()
                        .setOrderId(jsonResp.get("orderID").getAsString())
                        .setTotalAmount(jsonResp.get("orderAmount").getAsDouble())
                        .setCurrency(jsonResp.get("currency_code").getAsString())
                        .setStatus(jsonResp.get("orderStatus_status").getAsString())
                        .setMessage("Order retrieved successfully")
                        .build());
            } else {
                res.onNext(OrderResponse.newBuilder()
                        .setMessage("ERP returned HTTP " + httpResp.statusCode())
                        .setStatus("FAILED")
                        .build());
            }
            res.onCompleted();

        } catch (Exception e) {
            res.onError(Status.INTERNAL.withDescription("Failed to fetch order: " + e.getMessage()).asRuntimeException());
        }
    }

    /**
     * Updates order status in ERP (pick, ship, complete, cancel, etc.).
     */
    @Override
    public void changeOrderStatus(OrderStatusRequest req, StreamObserver<OrderResponse> res) {
        try {
            String basicAuth = "Basic " + Base64.getEncoder()
                    .encodeToString((config.username() + ":" + config.password()).getBytes(StandardCharsets.UTF_8));

            // ERP endpoints like POST /Orders(ID)/<action>
            String action = mapStatusToAction(req.getNewStatus());
            if (action == null) {
                res.onError(Status.INVALID_ARGUMENT.withDescription("Unknown status: " + req.getNewStatus()).asRuntimeException());
                return;
            }

            String url = config.endpoint() + "/Orders(" + req.getOrderId() + ")/" + action;
            HttpRequest httpReq = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", basicAuth)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> httpResp = HttpClient.newHttpClient()
                    .send(httpReq, HttpResponse.BodyHandlers.ofString());

            if (httpResp.statusCode() == 200 || httpResp.statusCode() == 204) {
                res.onNext(OrderResponse.newBuilder()
                        .setOrderId(req.getOrderId())
                        .setStatus(req.getNewStatus())
                        .setMessage("Order status updated successfully")
                        .build());
            } else {
                res.onNext(OrderResponse.newBuilder()
                        .setMessage("ERP returned HTTP " + httpResp.statusCode())
                        .setStatus("FAILED")
                        .build());
            }
            res.onCompleted();

        } catch (Exception e) {
            res.onError(Status.INTERNAL.withDescription("Failed to update order status: " + e.getMessage()).asRuntimeException());
        }
    }

    // Helper to map our status to ERP actions
    private String mapStatusToAction(String status) {
        return switch (status.toUpperCase()) {
            case "PICKED" -> "pickOrder";
            case "SHIPPED" -> "shipOrder";
            case "COMPLETED" -> "completeOrder";
            case "CANCELLED" -> "cancelOrder";
            default -> null;
        };
    }
}
