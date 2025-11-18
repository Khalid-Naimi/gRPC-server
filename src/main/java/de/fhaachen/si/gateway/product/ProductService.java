package de.fhaachen.si.gateway.product;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import de.fhaachen.si.gateway.config.Config;
import de.fhaachen.si.grpc.Empty;
import de.fhaachen.si.grpc.ProductGrpc;
import de.fhaachen.si.grpc.ProductIdRequest;
import de.fhaachen.si.grpc.ProductList;
import de.fhaachen.si.grpc.ProductResponse;

public class ProductService extends ProductGrpc.ProductImplBase{
	  private final Config config;
	    private final Gson gson = new Gson();

	    public ProductService(Config config) {
	        this.config = config;
	    }

	    private String basicAuth() {
	        return "Basic " + Base64.getEncoder().encodeToString(
	                (config.username() + ":" + config.password()).getBytes(StandardCharsets.UTF_8)
	        );
	    }

	    @Override
	    public void getAllProducts(Empty request, StreamObserver<ProductList> responseObserver) {
	        try {
	            String url = config.endpoint() + "/products";
	            HttpRequest httpReq = HttpRequest.newBuilder()
	                    .uri(URI.create(url))
	                    .header("Authorization", basicAuth())
	                    .GET()
	                    .build();

	            HttpResponse<String> httpResp = HttpClient.newHttpClient()
	                    .send(httpReq, HttpResponse.BodyHandlers.ofString());

	            if (httpResp.statusCode() != 200) {
	                throw new RuntimeException("ERP returned HTTP " + httpResp.statusCode());
	            }

	            JsonElement parsed = JsonParser.parseString(httpResp.body());
	            JsonArray arr;

	            if (parsed.isJsonArray()) {
	                arr = parsed.getAsJsonArray();
	            } else if (parsed.isJsonObject() && parsed.getAsJsonObject().has("value")) {
	                arr = parsed.getAsJsonObject().getAsJsonArray("value");
	            } else {
	                arr = new JsonArray();
	            }

	            List<ProductResponse> list = new ArrayList<>();
	            for (JsonElement el : arr) {
	                var obj = el.getAsJsonObject();
	                list.add(ProductResponse.newBuilder()
	                        .setId(obj.has("ID") ? obj.get("ID").getAsString() : "")
	                        .setProductCode(obj.has("productID") ? obj.get("productID").getAsString() : "")
	                        .setName(obj.has("name") ? obj.get("name").getAsString() : "")
	                        .setDescription(obj.has("description") && !obj.get("description").isJsonNull()
	                                ? obj.get("description").getAsString()
	                                : "")
	                        .setPrice(obj.has("price") ? obj.get("price").getAsDouble() : 0.0)
	                        .setCurrency(obj.has("currency_code") ? obj.get("currency_code").getAsString()
	                                : obj.has("currency") ? obj.get("currency").getAsString()
	                                : "")
	                        .setStock(obj.has("stock") ? obj.get("stock").getAsInt() : 0)
	                        .build());
	            }

	            responseObserver.onNext(ProductList.newBuilder().addAllProducts(list).build());
	            responseObserver.onCompleted();

	        } catch (Exception e) {
	            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
	        }
	    }


	    @Override
	    public void getProductById(ProductIdRequest request, StreamObserver<ProductResponse> responseObserver) {
	        try {
	            String productId = request.getId();
	            String url = config.endpoint() + "/Products('" + productId + "')";
	            HttpRequest httpReq = HttpRequest.newBuilder()
	                    .uri(URI.create(url))
	                    .header("Authorization", basicAuth())
	                    .header("Content-Type", "application/json")
	                    .GET()
	                    .build();

	            HttpResponse<String> httpResp = HttpClient.newHttpClient()
	                    .send(httpReq, HttpResponse.BodyHandlers.ofString());

	            if (httpResp.statusCode() != 200) {
	                throw new RuntimeException("ERP returned HTTP " + httpResp.statusCode() + ": " + httpResp.body());
	            }

	            JsonElement parsed = JsonParser.parseString(httpResp.body());
	            JsonObject obj;

	            if (parsed.isJsonObject()) {
	                obj = parsed.getAsJsonObject();
	            } else if (parsed.isJsonArray() && parsed.getAsJsonArray().size() > 0) {
	                // in case ERP returns array even for a single product
	                obj = parsed.getAsJsonArray().get(0).getAsJsonObject();
	            } else {
	                throw new RuntimeException("Invalid ERP response for product: " + httpResp.body());
	            }

	            ProductResponse product = ProductResponse.newBuilder()
                        .setId(obj.has("ID") ? obj.get("ID").getAsString() : "")
                        .setProductCode(obj.has("productID") ? obj.get("productID").getAsString() : "")
	                    .setName(obj.has("name") ? obj.get("name").getAsString() : "")
	                    .setDescription(obj.has("description") && !obj.get("description").isJsonNull()
	                            ? obj.get("description").getAsString()
	                            : "")
	                    .setPrice(obj.has("price") ? obj.get("price").getAsDouble() : 0.0)
	                    .setCurrency(obj.has("currency_code") ? obj.get("currency_code").getAsString()
	                            : obj.has("currency") ? obj.get("currency").getAsString()
	                            : "")
	                    .setStock(obj.has("stock") ? obj.get("stock").getAsInt() : 0)
	                    .build();

	            responseObserver.onNext(product);
	            responseObserver.onCompleted();

	        } catch (Exception e) {
	            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
	        }
	    }

}
