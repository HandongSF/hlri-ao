package edu.handong.csee.isel.ao.network;

import edu.handong.csee.isel.ao.AgentOrchestrator;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class ROSSimulator {

    private AgentOrchestrator subscriber;
    private volatile boolean running = false;
    private Thread videoThread;

    private String[] videoFiles = {"video1.mp4", "video2.mp4"};

    private FFmpegFrameGrabber grabber;
    private Java2DFrameConverter converter = new Java2DFrameConverter();

    public ROSSimulator(AgentOrchestrator ao) {
        this.subscriber = ao;
    }

    public void start() {
        running = true;

        final int TARGET_FPS = 15;
        final long BATCH_INTERVAL_MS = 1000;
        final int PLAYS_PER_VIDEO = 10;
        final int TOTAL_EPISODES = videoFiles.length * PLAYS_PER_VIDEO;

        videoThread = new Thread(() -> {
            try {
                avutil.av_log_set_level(avutil.AV_LOG_ERROR);

                for (int episode = 0; episode < TOTAL_EPISODES; episode++) {
                    if (!running) break;

                    String videoFileName = videoFiles[episode % 2];
                    URL videoUrl = ROSSimulator.class.getClassLoader().getResource(videoFileName);
                    if (videoUrl == null) {
                        System.err.println("!!! [Error] Resources 폴더에서 '" + videoFileName + "' 파일을 찾을 수 없습니다.");
                        running = false;
                        break;
                    }
                    String absolutePath = resolveResourceToFilePath(videoFileName);


                    System.out.printf("=== [ROS] (%d/%d): %s ===%n",
                            (episode + 1), TOTAL_EPISODES, videoFileName);

                    grabber = new FFmpegFrameGrabber(absolutePath);
                    grabber.setPixelFormat(avutil.AV_PIX_FMT_BGR24);
                    grabber.start();


                    int loopCount = 1;
                    int globalFrameCount = 0;
                    boolean isVideoFinished = false;

                    while (running && !isVideoFinished) {
                        long loopStart = System.currentTimeMillis();
                        int currentBatchSent = 0;

                        for (int i = 0; i < TARGET_FPS; i++) {
                            if (!running) break;

                            Frame frame = grabber.grabImage();

                            if (frame == null) {
                                isVideoFinished = true;
                                break;
                            }

                            long timestampUs = grabber.getTimestamp();
                            long timestampMs = timestampUs / 1000;

                            BufferedImage image = converter.convert(frame);
                            if (image == null) continue;

                            byte[] frameBytes = encodeToJpeg(image);

                            globalFrameCount++;
                            currentBatchSent++;

                            notifySubscriber(frameBytes, timestampMs, globalFrameCount);
                        }

                        if (isVideoFinished) {
                            System.out.println("=== [ROS] " + videoFileName + " done ===");
                            break;
                        }

                        long loopEnd = System.currentTimeMillis();
                        long procTime = loopEnd - loopStart;
                        long sleepTime = BATCH_INTERVAL_MS - procTime;

                        if (loopCount % 5 == 0) {
                            System.out.printf("[ROS] %d (%d frames sent)%n",
                                    loopCount,
                                    globalFrameCount);
                        }

                        loopCount++;

                        if (sleepTime > 0) {
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                running = false;
                            }
                        }
                    }

                    grabber.stop();
                    grabber.release();

                    if (running) Thread.sleep(500);
                }

                System.out.println("=== [ROS] All simulation (20) done ===");
                running = false;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "JavaCV-Video-Thread");

        videoThread.start();
    }

    public void stop() {
        running = false;
        if (videoThread != null) {
            try {
                videoThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private byte[] encodeToJpeg(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }

    private void notifySubscriber(byte[] imageBytes, long timestampMs, int frameCount) {
        String tsString = Long.toString(timestampMs);
        if (subscriber == null) return;

        subscriber.update(
                imageBytes, new byte[0], frameCount,
                0f, 0f, 0f, 0f, "", tsString,
                new byte[0], new byte[0], new byte[0]
        );
    }

    public void sendAction(String action) {
        System.out.print(action);
    }

    private String resolveResourceToFilePath(String resourceName) throws Exception {
        URL url = ROSSimulator.class.getClassLoader().getResource(resourceName);
        if (url == null) {
            throw new FileNotFoundException("Resource not found: " + resourceName);
        }

        if ("file".equalsIgnoreCase(url.getProtocol())) {
            return Paths.get(url.toURI()).toAbsolutePath().toString();
        }

        try (InputStream is = ROSSimulator.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) throw new FileNotFoundException("Resource stream not found: " + resourceName);

            Path temp = Files.createTempFile("rossim-", "-" + resourceName);
            temp.toFile().deleteOnExit();

            Files.copy(is, temp, StandardCopyOption.REPLACE_EXISTING);
            return temp.toAbsolutePath().toString();
        }
    }

}
