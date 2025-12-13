package edu.handong.csee.isel.ao.network;

import edu.handong.csee.isel.ao.AgentOrchestrator;
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

        final double TARGET_FPS = 15.0;
        final long FRAME_INTERVAL_MS = (long)(1000.0 / TARGET_FPS);

        videoThread = new Thread(() -> {
            try {
                grabber = createGrabber(videoSource);
                grabber.start();

                long startTime = System.currentTimeMillis();
                long lastLogTime = startTime;
                int frameCount = 0;

                long sumWorkMs = 0;
                int framesThisSec = 0;

                while (running) {

                    long loopStart = System.currentTimeMillis();

                    Frame frame = grabber.grab();
                    if (frame == null) {
                        System.out.println("No more frame");
                        break;
                    }

                    long timestampUs = grabber.getTimestamp();
                    long timestampMs = timestampUs / 1000;

                    BufferedImage image = converter.convert(frame);
                    if (image == null) continue;

                    byte[] frameBytes = encodeToJpeg(image);
                    notifySubscriber(frameBytes, timestampMs);

                    frameCount++;
                    framesThisSec++;

                    long now = System.currentTimeMillis();
                    long workMs = now - loopStart;
                    sumWorkMs += workMs;

                    if (now - lastLogTime >= 1000) {
                        long elapsedSeconds = (now - startTime) / 1000;
                        double avgWorkMs = framesThisSec > 0
                                ? (double) sumWorkMs / framesThisSec
                                : 0.0;

                        System.out.printf(
                                "[ROS] %d초 경과 - %d 프레임 전송 (avg frame=%.2f ms)%n",
                                elapsedSeconds, frameCount, avgWorkMs
                        );

                        lastLogTime = now;
                        sumWorkMs = 0;
                        framesThisSec = 0;
                    }

                    long loopElapsed = System.currentTimeMillis() - loopStart;
                    long sleepTime = FRAME_INTERVAL_MS - loopElapsed;

                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
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

    private void notifySubscriber(byte[] imageBytes, long timestampMs) {
        // timestamp를 문자열로 변환해서 text 필드에 넣음
        String tsString = Long.toString(timestampMs);
        if (subscriber == null) {
            return;
        }

        subscriber.update(
                imageBytes,   // byte[] image
                new byte[0],         // byte[] depth
                0f,           // float acceleration
                0f,           // float angular
                0f,           // float mag_str_x
                0f,           // float mag_str_y
                "",         // String target
                tsString,     //String text: 영상 타임스탬프(ms)
                new byte[0],         // byte[] extra1
                new byte[0],         // byte[] extra2
                new byte[0]          // byte[] extra3
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

}



