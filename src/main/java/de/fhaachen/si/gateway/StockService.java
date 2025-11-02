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

import org.springframework.stereotype.Service;

@Service
public class StockService extends StockGrpc.StockImplBase {

    private final StockConfig config;

    public StockService(StockConfig config) {
        this.config = config;
    }

    @Override
    public void getStock(StockRequest req, StreamObserver<StockResponse> res) {
        String itemId = req.getItemId();
        if (itemId == null || itemId.isBlank()) {
            res.onError(Status.INVALID_ARGUMENT.withDescription("itemId required").asRuntimeException());
            return;
        }

        int quantity = -1;
        try {
            String url = config.endpoint();
            String basic = "Basic " + Base64.getEncoder()
                    .encodeToString((config.username() + ":" + config.password()).getBytes(StandardCharsets.UTF_8));

            HttpRequest httpReq = HttpRequest.newBuilder(URI.create(url))
                    .header("Authorization", basic)
                    .GET()
                    .build();

            HttpResponse<String> httpResp = HttpClient.newHttpClient()
                    .send(httpReq, HttpResponse.BodyHandlers.ofString());

            JsonArray arr = JsonParser.parseString(httpResp.body()).getAsJsonArray();
            for (JsonElement el : arr) {
                var obj = el.getAsJsonObject();
                if (itemId.equals(obj.get("ID").getAsString())) {
                    quantity = obj.get("stock").getAsInt();
                    break;
                }
            }
        } catch (Exception e) {
            res.onError(Status.INTERNAL.withDescription("ERP call failed: " + e.getMessage()).asRuntimeException());
            return;
        }

        StockResponse reply = StockResponse.newBuilder()
                .setQuantity(quantity)
                .build();
        res.onNext(reply);
        res.onCompleted();
    }
}
