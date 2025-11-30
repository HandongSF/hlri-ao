package com.example.grpc.video;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class VideoSender {

    private static final int CHUNK_SIZE = 64 * 1024; // 64KB

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java ... VideoSender <path-to-video> [host=localhost] [port=50051]");
            System.exit(1);
        }
        String path = args[0];
        String host = (args.length >= 2) ? args[1] : "localhost";
        int port = (args.length >= 3) ? Integer.parseInt(args[2]) : 50051;

        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            System.err.println("File not found: " + file.getAbsolutePath());
            System.exit(2);
        }

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        try {
            VideoServiceGrpc.VideoServiceStub stub = VideoServiceGrpc.newStub(channel);
            CountDownLatch latch = new CountDownLatch(1);

            StreamObserver<UploadStatus> responseObserver = new StreamObserver<>() {
                @Override
                public void onNext(UploadStatus status) {
                    System.out.println("[Client] Server response: ok=" + status.getOk()
                            + ", bytes=" + status.getBytesReceived()
                            + ", msg=" + status.getMessage());
                }

                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                    latch.countDown();
                }

                @Override
                public void onCompleted() {
                    System.out.println("[Client] Upload completed.");
                    latch.countDown();
                }
            };

            StreamObserver<VideoChunk> requestObserver = stub.upload(responseObserver);

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buf = new byte[CHUNK_SIZE];
                int read;
                while ((read = fis.read(buf)) != -1) {
                    VideoChunk chunk = VideoChunk.newBuilder()
                            .setFileName(file.getName())
                            .setData(ByteString.copyFrom(buf, 0, read))
                            .build();
                    requestObserver.onNext(chunk);
                }
            } catch (Exception e) {
                requestObserver.onError(e);
                throw e;
            }

            requestObserver.onCompleted();
            // 서버 응답 대기
            if (!latch.await(60, TimeUnit.SECONDS)) {
                System.err.println("[Client] Timeout waiting for server response.");
            }
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
