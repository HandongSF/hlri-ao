package com.example.grpc.video;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * 클라이언트 스트리밍 업로드
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: video.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class VideoServiceGrpc {

  private VideoServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "video.VideoService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.grpc.video.VideoChunk,
      com.example.grpc.video.UploadStatus> getUploadMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Upload",
      requestType = com.example.grpc.video.VideoChunk.class,
      responseType = com.example.grpc.video.UploadStatus.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<com.example.grpc.video.VideoChunk,
      com.example.grpc.video.UploadStatus> getUploadMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.video.VideoChunk, com.example.grpc.video.UploadStatus> getUploadMethod;
    if ((getUploadMethod = VideoServiceGrpc.getUploadMethod) == null) {
      synchronized (VideoServiceGrpc.class) {
        if ((getUploadMethod = VideoServiceGrpc.getUploadMethod) == null) {
          VideoServiceGrpc.getUploadMethod = getUploadMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.video.VideoChunk, com.example.grpc.video.UploadStatus>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Upload"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.video.VideoChunk.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.video.UploadStatus.getDefaultInstance()))
              .setSchemaDescriptor(new VideoServiceMethodDescriptorSupplier("Upload"))
              .build();
        }
      }
    }
    return getUploadMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static VideoServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VideoServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VideoServiceStub>() {
        @java.lang.Override
        public VideoServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VideoServiceStub(channel, callOptions);
        }
      };
    return VideoServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static VideoServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VideoServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VideoServiceBlockingStub>() {
        @java.lang.Override
        public VideoServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VideoServiceBlockingStub(channel, callOptions);
        }
      };
    return VideoServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static VideoServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VideoServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VideoServiceFutureStub>() {
        @java.lang.Override
        public VideoServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VideoServiceFutureStub(channel, callOptions);
        }
      };
    return VideoServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * 클라이언트 스트리밍 업로드
   * </pre>
   */
  public interface AsyncService {

    /**
     */
    default io.grpc.stub.StreamObserver<com.example.grpc.video.VideoChunk> upload(
        io.grpc.stub.StreamObserver<com.example.grpc.video.UploadStatus> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getUploadMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service VideoService.
   * <pre>
   * 클라이언트 스트리밍 업로드
   * </pre>
   */
  public static abstract class VideoServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return VideoServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service VideoService.
   * <pre>
   * 클라이언트 스트리밍 업로드
   * </pre>
   */
  public static final class VideoServiceStub
      extends io.grpc.stub.AbstractAsyncStub<VideoServiceStub> {
    private VideoServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VideoServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VideoServiceStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.example.grpc.video.VideoChunk> upload(
        io.grpc.stub.StreamObserver<com.example.grpc.video.UploadStatus> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getUploadMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service VideoService.
   * <pre>
   * 클라이언트 스트리밍 업로드
   * </pre>
   */
  public static final class VideoServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<VideoServiceBlockingStub> {
    private VideoServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VideoServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VideoServiceBlockingStub(channel, callOptions);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service VideoService.
   * <pre>
   * 클라이언트 스트리밍 업로드
   * </pre>
   */
  public static final class VideoServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<VideoServiceFutureStub> {
    private VideoServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VideoServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VideoServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_UPLOAD = 0;

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
        case METHODID_UPLOAD:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.upload(
              (io.grpc.stub.StreamObserver<com.example.grpc.video.UploadStatus>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getUploadMethod(),
          io.grpc.stub.ServerCalls.asyncClientStreamingCall(
            new MethodHandlers<
              com.example.grpc.video.VideoChunk,
              com.example.grpc.video.UploadStatus>(
                service, METHODID_UPLOAD)))
        .build();
  }

  private static abstract class VideoServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    VideoServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.grpc.video.VideoProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("VideoService");
    }
  }

  private static final class VideoServiceFileDescriptorSupplier
      extends VideoServiceBaseDescriptorSupplier {
    VideoServiceFileDescriptorSupplier() {}
  }

  private static final class VideoServiceMethodDescriptorSupplier
      extends VideoServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    VideoServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (VideoServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new VideoServiceFileDescriptorSupplier())
              .addMethod(getUploadMethod())
              .build();
        }
      }
    }
    return result;
  }
}
