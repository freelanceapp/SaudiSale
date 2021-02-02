package com.saudi_sale.models;

import java.io.Serializable;

public class OtherProfileDataModel extends StatusResponse implements Serializable {
    private UserModel.Data data;

    public UserModel.Data getData() {
        return data;
    }
}
