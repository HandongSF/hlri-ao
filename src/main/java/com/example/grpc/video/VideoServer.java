package com.example.grpc.video;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class VideoServer {

    private Server server;

    private void start(int port) throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new VideoServiceImpl())
                .build()
                .start();
        System.out.println("[Server] gRPC started on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("[Server] Shutting down...");
            VideoServer.this.stop();
            System.err.println("[Server] Bye.");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 50052;
        if (args.length > 0) port = Integer.parseInt(args[0]);
        VideoServer s = new VideoServer();
        s.start(port);
        s.blockUntilShutdown();
    }

    // 실제 업로드 처리
    static class VideoServiceImpl extends VideoServiceGrpc.VideoServiceImplBase {
        @Override
        public StreamObserver<VideoChunk> upload(StreamObserver<UploadStatus> responseObserver) {
            return new StreamObserver<>() {
                private FileOutputStream fos;
                private String fileName = "unknown.bin";
                private final AtomicLong bytes = new AtomicLong(0L);

                @Override
                public void onNext(VideoChunk chunk) {
                    try {
                        if (fos == null) {
                            fileName = (chunk.getFileName() == null || chunk.getFileName().isBlank())
                                    ? "upload.bin" : chunk.getFileName();

                            File dir = new File("received");
                            if (!dir.exists() && !dir.mkdirs()) {
                                throw new IOException("Failed to create directory: " + dir.getAbsolutePath());
                            }
                            File out = new File(dir, new File(fileName).getName()); // 경로 세이프가드
                            fos = new FileOutputStream(out);
                            System.out.println("[Server] Start receiving -> " + out.getAbsolutePath());
                        }
                        var data = chunk.getData();
                        data.writeTo(fos);
                        bytes.addAndGet(data.size());
                    } catch (IOException e) {
                        onError(e);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                    closeQuietly();
                }

                @Override
                public void onCompleted() {
                    closeQuietly();
                    UploadStatus status = UploadStatus.newBuilder()
                            .setOk(true)
                            .setMessage("Saved as 'received/" + new File(fileName).getName() + "'")
                            .setBytesReceived(bytes.get())
                            .build();
                    System.out.println("[Server] Completed: " + status.getBytesReceived() + " bytes");
                    responseObserver.onNext(status);
                    responseObserver.onCompleted();
                }

                private void closeQuietly() {
                    if (fos != null) {
                        try { fos.flush(); fos.close(); } catch (IOException ignored) {}
                        fos = null;
                    }
                }
            };
        }
    }
}
