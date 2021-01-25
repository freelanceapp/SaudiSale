package com.saudi_sale.models;

import java.io.Serializable;

public class ChatUserModel implements Serializable {
    private int id;
    private String name;
    private String image;
    private int room_id;

    public ChatUserModel(int id, String name, String image, int room_id) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.room_id = room_id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public int getRoom_id() {
        return room_id;
    }
}
