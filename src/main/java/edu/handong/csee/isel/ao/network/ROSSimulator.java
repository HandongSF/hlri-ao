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

                    // Frame → BufferedImage
                    BufferedImage image = converter.convert(frame);
                    if (image == null) continue;

                    // BufferedImage → JPEG byte[]
                    byte[] frameBytes = encodeToJpeg(image);

                    notifySubscriber(frameBytes);
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
        try {
            // 숫자라면 카메라 인덱스로 사용 (예: "0")
            int camIndex = Integer.parseInt(source);
            return new FFmpegFrameGrabber(camIndex);
        } catch (NumberFormatException e) {
            // 숫자가 아니라면 파일명 or RTSP URL임
            return new FFmpegFrameGrabber(source);
        }
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

    private void notifySubscriber(byte[] imageBytes) {
        subscriber.update(
                imageBytes,   // image
                null,         // depth
                null,         // acceleration
                null,         // angular
                null,         // mag_str_x
                null,         // mag_str_y
                null,         // target
                null          // text
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
