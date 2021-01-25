package com.saudi_sale.models;

import java.io.Serializable;
import java.util.List;

public class MessageDataModel extends StatusResponse implements Serializable {
    private List<MessageModel> data;

    public List<MessageModel> getData() {
        return data;
    }
}
