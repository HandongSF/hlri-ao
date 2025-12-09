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

        videoThread = new Thread(() -> {
            try {
                grabber = createGrabber(videoSource);
                grabber.start();

                while (running) {
                    Frame frame = grabber.grab();
                    if (frame == null) {
                        System.out.println("No more frame");
                        break;
                    }

                    long timestampUs = grabber.getTimestamp();
                    long timestampMs = timestampUs / 1000;  // ms단위를 위해

                    // Frame → BufferedImage
                    BufferedImage image = converter.convert(frame);
                    if (image == null) continue;

                    // BufferedImage → JPEG byte[]
                    byte[] frameBytes = encodeToJpeg(image);

                    // image + video timestamp
                    notifySubscriber(frameBytes, timestampMs);
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

        subscriber.update(
                imageBytes,   // byte[] image
                null,         // byte[] depth
                0f,           // float acceleration
                0f,           // float angular
                0f,           // float mag_str_x
                0f,           // float mag_str_y
                null,         // String target
                tsString,     //String text: 영상 타임스탬프(ms)
                null,         // byte[] extra1
                null,         // byte[] extra2
                null          // byte[] extra3
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
