package com.saudi_sale.models;

import java.io.Serializable;

public class SingleMessageDataModel extends StatusResponse implements Serializable {
    private MessageModel data;

    public MessageModel getData() {
        return data;
    }
}
