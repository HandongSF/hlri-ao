package edu.handong.csee.isel.ao.utils;

public class TempData {
    private byte[] image;
    private byte[] depth;
    private int frameNum;
    private float accel;
    private float angular; 
    private float magStrX;
    private float magStrY;
    private String target;
    private String text;
    private byte[] header; 
    private byte[] format;
    private byte[] data;

    public TempData(
            byte[] image, byte[] depth, int frameNum, 
            float accel, float angular, float magStrX, float magStrY,
            String target, String text, 
            byte[] header, byte[] format, byte[] data) {
        this.image = image;
        this.depth = depth;
        this.frameNum = frameNum;
        this.accel = accel;
        this.angular = angular;
        this.magStrX = magStrX;
        this.magStrY = magStrY;
        this.target = target;
        this.text = text;
        this.header = header;
        this.format = format;
        this.data = data;
    }

    public byte[] getImage(){
        return image;
    }

    public byte[] getDepth(){
        return depth;
    }
      
    public int getFrameNum(){
        return frameNum;
    }

    public float getAccel(){
        return accel;
    }

    public float getAngular(){
        return angular;
    }

    public float getMagStrX(){
        return magStrX;
    }

    public float getMagStrY(){
        return magStrY;
    }

    public String getTarget(){
        return target;
    }

    public String getText(){
        return text;
    }

    public byte[] getHeader() {
        return header;
    }

    public byte[] getFormat() {
        return format;
    }

    public byte[] getData() {
        return data;
    }
}
