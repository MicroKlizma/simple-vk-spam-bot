package com.vkbot.vk;

import java.io.InputStream;

public class Member {

    private int fromId;
    private byte[] image;

    public byte[] getImageBytes() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Member(int fromId) {
        this.fromId = fromId;
    }
}
