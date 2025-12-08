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

    // JavaCV 관련
    private FFmpegFrameGrabber grabber;
    private Java2DFrameConverter converter = new Java2DFrameConverter();

    public ROSSimulator(AgentOrchestrator ao) {
        this.subscriber = ao;
    }

    public void start() {
        running = true;

        videoThread = new Thread(() -> {
            try {
                // 0 → 기본 웹캠 (필요하면 RTSP 또는 파일 경로 등으로 변경)
                grabber = new FFmpegFrameGrabber(0);
                grabber.start();

                while (running) {
                    Frame frame = grabber.grab();
                    if (frame == null) {
                        System.out.println("[ROSSimulator] 더 이상 프레임 없음");
                        break;
                    }

                    // Frame → BufferedImage
                    BufferedImage image = converter.convert(frame);
                    if (image == null) continue;

                    // BufferedImage → JPEG byte[]
                    byte[] frameBytes = encodeToJpeg(image);

                    // 요청한 방식: notifySubscriber() 사용
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

    /**
     *  요청사항 ― image만 전송하고 나머지는 모두 null로 고정하는 메서드
     */
    private void notifySubscriber(byte[] imageBytes) {
        subscriber.update(
                imageBytes
        );
    }

    public void sendAction(String action) {
        System.out.print(action);
    }
}
