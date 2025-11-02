package de.fhaachen.si.gateway;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import si.StockGrpc;
import si.StockRequest;
import si.StockResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class StockService extends StockGrpc.StockImplBase {

    private final StockConfig config;

    public StockService(StockConfig config) {
        this.config = config;
    }

    @Override
    public void getStock(StockRequest req, StreamObserver<StockResponse> res) {
        String itemId = req.getItemId();
        System.out.println("[gRPC] Received request for itemId: " + itemId);
        if (itemId == null || itemId.isBlank()) {
            res.onError(Status.INVALID_ARGUMENT.withDescription("itemId required").asRuntimeException());
            return;
        }

        int quantity = -1;
        try {
            String url = config.endpoint();
            System.out.println("[gRPC] Sending HTTP request to ERP endpoint: " + url);
            String basic = "Basic " + Base64.getEncoder()
                    .encodeToString((config.username() + ":" + config.password()).getBytes(StandardCharsets.UTF_8));

            HttpRequest httpReq = HttpRequest.newBuilder(URI.create(url))
                    .header("Authorization", basic)
                    .GET()
                    .build();

            HttpResponse<String> httpResp = HttpClient.newHttpClient()
                    .send(httpReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("[gRPC] Received HTTP response: " + httpResp.statusCode());

            // Fail fast on non-200 HTTP
            if (httpResp.statusCode() != 200) {
                throw new RuntimeException("HTTP " + httpResp.statusCode());
            }

            // Parse OData payload: { "@odata.context": ..., "value": [ ... ] }
            var parsed = JsonParser.parseString(httpResp.body());
            JsonArray arr;
            if (parsed.isJsonArray()) {
                // Response is a plain JSON array
                arr = parsed.getAsJsonArray();
            } else if (parsed.isJsonObject() && parsed.getAsJsonObject().has("value")
                    && parsed.getAsJsonObject().get("value").isJsonArray()) {
                // OData-style object with a `value` array
                arr = parsed.getAsJsonObject().getAsJsonArray("value");
            } else {
                arr = new JsonArray();
            }

            for (JsonElement el : arr) {
                var obj = el.getAsJsonObject();
                String prodId = obj.has("productID") && !obj.get("productID").isJsonNull()
                        ? obj.get("productID").getAsString()
                        : "";
                System.out.println("[gRPC] Checking productID: " + prodId);
                if (itemId.equals(prodId)) {
                    quantity = obj.has("stock") && !obj.get("stock").isJsonNull() ? obj.get("stock").getAsInt() : -1;
                    System.out.println("[gRPC] Match found, stock quantity: " + quantity);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("[gRPC] ERP call failed: " + e.getMessage());
            res.onError(Status.INTERNAL.withDescription("ERP call failed: " + e.getMessage()).asRuntimeException());
            return;
        }

        System.out.println("[gRPC] Sending StockResponse with quantity: " + quantity);
        StockResponse reply = StockResponse.newBuilder()
                .setQuantity(quantity)
                .build();
        res.onNext(reply);
        res.onCompleted();
    }
}
