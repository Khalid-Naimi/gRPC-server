package si;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: si.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class StockGrpc {

  private StockGrpc() {}

  public static final java.lang.String SERVICE_NAME = "si.Stock";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<si.StockRequest,
      si.StockResponse> getGetStockMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetStock",
      requestType = si.StockRequest.class,
      responseType = si.StockResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<si.StockRequest,
      si.StockResponse> getGetStockMethod() {
    io.grpc.MethodDescriptor<si.StockRequest, si.StockResponse> getGetStockMethod;
    if ((getGetStockMethod = StockGrpc.getGetStockMethod) == null) {
      synchronized (StockGrpc.class) {
        if ((getGetStockMethod = StockGrpc.getGetStockMethod) == null) {
          StockGrpc.getGetStockMethod = getGetStockMethod =
              io.grpc.MethodDescriptor.<si.StockRequest, si.StockResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetStock"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  si.StockRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  si.StockResponse.getDefaultInstance()))
              .setSchemaDescriptor(new StockMethodDescriptorSupplier("GetStock"))
              .build();
        }
      }
    }
    return getGetStockMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static StockStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StockStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StockStub>() {
        @java.lang.Override
        public StockStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StockStub(channel, callOptions);
        }
      };
    return StockStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static StockBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StockBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StockBlockingStub>() {
        @java.lang.Override
        public StockBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StockBlockingStub(channel, callOptions);
        }
      };
    return StockBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static StockFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StockFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StockFutureStub>() {
        @java.lang.Override
        public StockFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StockFutureStub(channel, callOptions);
        }
      };
    return StockFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void getStock(si.StockRequest request,
        io.grpc.stub.StreamObserver<si.StockResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetStockMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service Stock.
   */
  public static abstract class StockImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return StockGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service Stock.
   */
  public static final class StockStub
      extends io.grpc.stub.AbstractAsyncStub<StockStub> {
    private StockStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StockStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StockStub(channel, callOptions);
    }

    /**
     */
    public void getStock(si.StockRequest request,
        io.grpc.stub.StreamObserver<si.StockResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetStockMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service Stock.
   */
  public static final class StockBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<StockBlockingStub> {
    private StockBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StockBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StockBlockingStub(channel, callOptions);
    }

    /**
     */
    public si.StockResponse getStock(si.StockRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetStockMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service Stock.
   */
  public static final class StockFutureStub
      extends io.grpc.stub.AbstractFutureStub<StockFutureStub> {
    private StockFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StockFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StockFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<si.StockResponse> getStock(
        si.StockRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetStockMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_STOCK = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_STOCK:
          serviceImpl.getStock((si.StockRequest) request,
              (io.grpc.stub.StreamObserver<si.StockResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGetStockMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              si.StockRequest,
              si.StockResponse>(
                service, METHODID_GET_STOCK)))
        .build();
  }

  private static abstract class StockBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    StockBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return si.Si.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Stock");
    }
  }

  private static final class StockFileDescriptorSupplier
      extends StockBaseDescriptorSupplier {
    StockFileDescriptorSupplier() {}
  }

  private static final class StockMethodDescriptorSupplier
      extends StockBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    StockMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (StockGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new StockFileDescriptorSupplier())
              .addMethod(getGetStockMethod())
              .build();
        }
      }
    }
    return result;
  }
}
