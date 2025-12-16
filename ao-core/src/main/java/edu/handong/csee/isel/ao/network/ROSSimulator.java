package edu.handong.csee.isel.ao.network;

import edu.handong.csee.isel.ao.AgentOrchestrator;
import org.bytedeco.ffmpeg.global.avutil; // 로그 설정을 위해 필요
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ROSSimulator {

    private AgentOrchestrator subscriber;
    private volatile boolean running = false;
    private Thread videoThread;

    private String videoSource = "video.mp4";

    private FFmpegFrameGrabber grabber;
    private Java2DFrameConverter converter = new Java2DFrameConverter();

    public ROSSimulator(AgentOrchestrator ao) {
        this.subscriber = ao;
    }

    public void start() {
        running = true;

        final int TARGET_FPS = 15;
        final long BATCH_INTERVAL_MS = 1000;

        videoThread = new Thread(() -> {
            try {
                avutil.av_log_set_level(avutil.AV_LOG_ERROR);

                grabber = createGrabber(videoSource);
                grabber.setPixelFormat(avutil.AV_PIX_FMT_BGR24);
                grabber.start();

                double sourceFps = grabber.getFrameRate();
                if (sourceFps <= 0) sourceFps = 30.0;

                final double frameStep = sourceFps / (double) TARGET_FPS;

                System.out.printf("=== [ROS] START (영상 FPS: %.2f | 전송 FPS: %d) ===%n", sourceFps, TARGET_FPS);

                final int[] loopCount = {1};
                final int[] globalFrameCount = {0};
                final long[] internalSourceFrameIndex = {0};

                double nextTargetVideoFrame = 0.0;

                while (running) {
                    long loopStart = System.currentTimeMillis();
                    int currentBatchSent = 0;

                    boolean wasReset = false;

                    for (int i = 0; i < TARGET_FPS; i++) {
                        if (!running) break;

                        // 1. 프레임 스킵
                        long framesToSkip = (long)nextTargetVideoFrame - internalSourceFrameIndex[0];
                        for (int k = 0; k < framesToSkip; k++) {
                            Frame skipped = grabber.grabImage();
                            internalSourceFrameIndex[0]++;
                            if (skipped == null) {
                                handleVideoReset(globalFrameCount, loopCount, internalSourceFrameIndex);
                                nextTargetVideoFrame = 0.0;
                                wasReset = true;
                                break;
                            }
                        }
                        if (wasReset) break;

                        // 2. 실제 전송할 프레임 읽기
                        Frame frame = grabber.grabImage();
                        internalSourceFrameIndex[0]++;

                        if (frame == null) {
                            handleVideoReset(globalFrameCount, loopCount, internalSourceFrameIndex);
                            nextTargetVideoFrame = 0.0;
                            wasReset = true;
                            break;
                        }

                        // 다음 목표 위치 갱신
                        nextTargetVideoFrame += frameStep;

                        // 3. 전송
                        long timestampUs = grabber.getTimestamp();
                        long timestampMs = timestampUs / 1000;

                        BufferedImage image = converter.convert(frame);
                        if (image == null) continue;

                        byte[] frameBytes = encodeToJpeg(image);

                        globalFrameCount[0]++;
                        currentBatchSent++;

                        notifySubscriber(frameBytes, timestampMs, globalFrameCount[0]);
                    }

                    // 리셋 발생 시 자투리 로그 스킵
                    if (wasReset) {
                        continue;
                    }

                    // 4. 시간 계산 및 대기
                    long loopEnd = System.currentTimeMillis();
                    long procTime = loopEnd - loopStart;
                    long sleepTime = BATCH_INTERVAL_MS - procTime;

                    System.out.printf("[ROS] %d초 경과 - %d장 전송 완료 (현재 영상위치: %d 프레임)%n",
                            loopCount[0],
                            currentBatchSent,
                            globalFrameCount[0]);

                    loopCount[0]++;

                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }

                grabber.stop();
                grabber.release();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "JavaCV-Video-Thread");

        videoThread.start();
    }

    private void handleVideoReset(int[] globalFrameCount, int[] loopCount, long[] internalSourceFrameIndex) throws Exception {
        System.out.println("=== [ROS] 영상 종료 -> 0번부터 리셋 ===");
        grabber.setTimestamp(0);
        globalFrameCount[0] = 0;
        loopCount[0] = 1;
        internalSourceFrameIndex[0] = 0;
    }

    private FFmpegFrameGrabber createGrabber(String source) {
        return new FFmpegFrameGrabber(source);
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

    public void setVideoSource(String path) {
        this.videoSource = path;
    }

    public String getVideoSource() {
        return videoSource;
    }

    public void sendAction(String action) {
        System.out.print(action);
    }

    public static void main(String[] args) throws Exception {
        ROSSimulator simulator = new ROSSimulator(null);
        simulator.setVideoSource("video.mp4");
        simulator.start();
        System.out.println("ROS Simulator started. Press ENTER to stop.");
        System.in.read();
        System.out.println("Stopping ROS Simulator...");
        simulator.stop();
        System.out.println("Stopped.");
    }
}