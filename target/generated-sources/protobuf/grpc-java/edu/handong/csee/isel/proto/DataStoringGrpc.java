package edu.handong.csee.isel.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: ao_network.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class DataStoringGrpc {

  private DataStoringGrpc() {}

  public static final java.lang.String SERVICE_NAME = "ao.network.DataStoring";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<edu.handong.csee.isel.proto.Data,
      com.google.rpc.Status> getSendDataMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SendData",
      requestType = edu.handong.csee.isel.proto.Data.class,
      responseType = com.google.rpc.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<edu.handong.csee.isel.proto.Data,
      com.google.rpc.Status> getSendDataMethod() {
    io.grpc.MethodDescriptor<edu.handong.csee.isel.proto.Data, com.google.rpc.Status> getSendDataMethod;
    if ((getSendDataMethod = DataStoringGrpc.getSendDataMethod) == null) {
      synchronized (DataStoringGrpc.class) {
        if ((getSendDataMethod = DataStoringGrpc.getSendDataMethod) == null) {
          DataStoringGrpc.getSendDataMethod = getSendDataMethod =
              io.grpc.MethodDescriptor.<edu.handong.csee.isel.proto.Data, com.google.rpc.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SendData"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.handong.csee.isel.proto.Data.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.rpc.Status.getDefaultInstance()))
              .setSchemaDescriptor(new DataStoringMethodDescriptorSupplier("SendData"))
              .build();
        }
      }
    }
    return getSendDataMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DataStoringStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DataStoringStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DataStoringStub>() {
        @java.lang.Override
        public DataStoringStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DataStoringStub(channel, callOptions);
        }
      };
    return DataStoringStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DataStoringBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DataStoringBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DataStoringBlockingStub>() {
        @java.lang.Override
        public DataStoringBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DataStoringBlockingStub(channel, callOptions);
        }
      };
    return DataStoringBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DataStoringFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DataStoringFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DataStoringFutureStub>() {
        @java.lang.Override
        public DataStoringFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DataStoringFutureStub(channel, callOptions);
        }
      };
    return DataStoringFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default io.grpc.stub.StreamObserver<edu.handong.csee.isel.proto.Data> sendData(
        io.grpc.stub.StreamObserver<com.google.rpc.Status> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getSendDataMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service DataStoring.
   */
  public static abstract class DataStoringImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return DataStoringGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service DataStoring.
   */
  public static final class DataStoringStub
      extends io.grpc.stub.AbstractAsyncStub<DataStoringStub> {
    private DataStoringStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataStoringStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DataStoringStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<edu.handong.csee.isel.proto.Data> sendData(
        io.grpc.stub.StreamObserver<com.google.rpc.Status> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getSendDataMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service DataStoring.
   */
  public static final class DataStoringBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<DataStoringBlockingStub> {
    private DataStoringBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataStoringBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DataStoringBlockingStub(channel, callOptions);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service DataStoring.
   */
  public static final class DataStoringFutureStub
      extends io.grpc.stub.AbstractFutureStub<DataStoringFutureStub> {
    private DataStoringFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataStoringFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DataStoringFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_SEND_DATA = 0;

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
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_DATA:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.sendData(
              (io.grpc.stub.StreamObserver<com.google.rpc.Status>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getSendDataMethod(),
          io.grpc.stub.ServerCalls.asyncClientStreamingCall(
            new MethodHandlers<
              edu.handong.csee.isel.proto.Data,
              com.google.rpc.Status>(
                service, METHODID_SEND_DATA)))
        .build();
  }

  private static abstract class DataStoringBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DataStoringBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return edu.handong.csee.isel.proto.AONetworkProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DataStoring");
    }
  }

  private static final class DataStoringFileDescriptorSupplier
      extends DataStoringBaseDescriptorSupplier {
    DataStoringFileDescriptorSupplier() {}
  }

  private static final class DataStoringMethodDescriptorSupplier
      extends DataStoringBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    DataStoringMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (DataStoringGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DataStoringFileDescriptorSupplier())
              .addMethod(getSendDataMethod())
              .build();
        }
      }
    }
    return result;
  }
}
