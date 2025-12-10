package edu.handong.csee.isel.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: ao_network.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ActionReceivingGrpc {

  private ActionReceivingGrpc() {}

  public static final java.lang.String SERVICE_NAME = "ao.network.ActionReceiving";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<edu.handong.csee.isel.proto.AgentInfo,
      com.google.rpc.Status> getConnectMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Connect",
      requestType = edu.handong.csee.isel.proto.AgentInfo.class,
      responseType = com.google.rpc.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<edu.handong.csee.isel.proto.AgentInfo,
      com.google.rpc.Status> getConnectMethod() {
    io.grpc.MethodDescriptor<edu.handong.csee.isel.proto.AgentInfo, com.google.rpc.Status> getConnectMethod;
    if ((getConnectMethod = ActionReceivingGrpc.getConnectMethod) == null) {
      synchronized (ActionReceivingGrpc.class) {
        if ((getConnectMethod = ActionReceivingGrpc.getConnectMethod) == null) {
          ActionReceivingGrpc.getConnectMethod = getConnectMethod =
              io.grpc.MethodDescriptor.<edu.handong.csee.isel.proto.AgentInfo, com.google.rpc.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Connect"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.handong.csee.isel.proto.AgentInfo.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.rpc.Status.getDefaultInstance()))
              .setSchemaDescriptor(new ActionReceivingMethodDescriptorSupplier("Connect"))
              .build();
        }
      }
    }
    return getConnectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<edu.handong.csee.isel.proto.RawAction,
      com.google.rpc.Status> getSendRawActionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SendRawAction",
      requestType = edu.handong.csee.isel.proto.RawAction.class,
      responseType = com.google.rpc.Status.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<edu.handong.csee.isel.proto.RawAction,
      com.google.rpc.Status> getSendRawActionMethod() {
    io.grpc.MethodDescriptor<edu.handong.csee.isel.proto.RawAction, com.google.rpc.Status> getSendRawActionMethod;
    if ((getSendRawActionMethod = ActionReceivingGrpc.getSendRawActionMethod) == null) {
      synchronized (ActionReceivingGrpc.class) {
        if ((getSendRawActionMethod = ActionReceivingGrpc.getSendRawActionMethod) == null) {
          ActionReceivingGrpc.getSendRawActionMethod = getSendRawActionMethod =
              io.grpc.MethodDescriptor.<edu.handong.csee.isel.proto.RawAction, com.google.rpc.Status>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SendRawAction"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  edu.handong.csee.isel.proto.RawAction.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.rpc.Status.getDefaultInstance()))
              .setSchemaDescriptor(new ActionReceivingMethodDescriptorSupplier("SendRawAction"))
              .build();
        }
      }
    }
    return getSendRawActionMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ActionReceivingStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ActionReceivingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ActionReceivingStub>() {
        @java.lang.Override
        public ActionReceivingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ActionReceivingStub(channel, callOptions);
        }
      };
    return ActionReceivingStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ActionReceivingBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ActionReceivingBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ActionReceivingBlockingStub>() {
        @java.lang.Override
        public ActionReceivingBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ActionReceivingBlockingStub(channel, callOptions);
        }
      };
    return ActionReceivingBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ActionReceivingFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ActionReceivingFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ActionReceivingFutureStub>() {
        @java.lang.Override
        public ActionReceivingFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ActionReceivingFutureStub(channel, callOptions);
        }
      };
    return ActionReceivingFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void connect(edu.handong.csee.isel.proto.AgentInfo request,
        io.grpc.stub.StreamObserver<com.google.rpc.Status> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getConnectMethod(), responseObserver);
    }

    /**
     */
    default void sendRawAction(edu.handong.csee.isel.proto.RawAction request,
        io.grpc.stub.StreamObserver<com.google.rpc.Status> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSendRawActionMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service ActionReceiving.
   */
  public static abstract class ActionReceivingImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return ActionReceivingGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service ActionReceiving.
   */
  public static final class ActionReceivingStub
      extends io.grpc.stub.AbstractAsyncStub<ActionReceivingStub> {
    private ActionReceivingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ActionReceivingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ActionReceivingStub(channel, callOptions);
    }

    /**
     */
    public void connect(edu.handong.csee.isel.proto.AgentInfo request,
        io.grpc.stub.StreamObserver<com.google.rpc.Status> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getConnectMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void sendRawAction(edu.handong.csee.isel.proto.RawAction request,
        io.grpc.stub.StreamObserver<com.google.rpc.Status> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSendRawActionMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service ActionReceiving.
   */
  public static final class ActionReceivingBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<ActionReceivingBlockingStub> {
    private ActionReceivingBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ActionReceivingBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ActionReceivingBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.google.rpc.Status connect(edu.handong.csee.isel.proto.AgentInfo request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getConnectMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.rpc.Status sendRawAction(edu.handong.csee.isel.proto.RawAction request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSendRawActionMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service ActionReceiving.
   */
  public static final class ActionReceivingFutureStub
      extends io.grpc.stub.AbstractFutureStub<ActionReceivingFutureStub> {
    private ActionReceivingFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ActionReceivingFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ActionReceivingFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.rpc.Status> connect(
        edu.handong.csee.isel.proto.AgentInfo request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getConnectMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.rpc.Status> sendRawAction(
        edu.handong.csee.isel.proto.RawAction request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSendRawActionMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CONNECT = 0;
  private static final int METHODID_SEND_RAW_ACTION = 1;

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
        case METHODID_CONNECT:
          serviceImpl.connect((edu.handong.csee.isel.proto.AgentInfo) request,
              (io.grpc.stub.StreamObserver<com.google.rpc.Status>) responseObserver);
          break;
        case METHODID_SEND_RAW_ACTION:
          serviceImpl.sendRawAction((edu.handong.csee.isel.proto.RawAction) request,
              (io.grpc.stub.StreamObserver<com.google.rpc.Status>) responseObserver);
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
          getConnectMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              edu.handong.csee.isel.proto.AgentInfo,
              com.google.rpc.Status>(
                service, METHODID_CONNECT)))
        .addMethod(
          getSendRawActionMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              edu.handong.csee.isel.proto.RawAction,
              com.google.rpc.Status>(
                service, METHODID_SEND_RAW_ACTION)))
        .build();
  }

  private static abstract class ActionReceivingBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ActionReceivingBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return edu.handong.csee.isel.proto.AONetworkProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ActionReceiving");
    }
  }

  private static final class ActionReceivingFileDescriptorSupplier
      extends ActionReceivingBaseDescriptorSupplier {
    ActionReceivingFileDescriptorSupplier() {}
  }

  private static final class ActionReceivingMethodDescriptorSupplier
      extends ActionReceivingBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    ActionReceivingMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (ActionReceivingGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ActionReceivingFileDescriptorSupplier())
              .addMethod(getConnectMethod())
              .addMethod(getSendRawActionMethod())
              .build();
        }
      }
    }
    return result;
  }
}
