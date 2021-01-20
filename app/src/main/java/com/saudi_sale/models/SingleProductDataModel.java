package com.saudi_sale.models;

import java.io.Serializable;

public class SingleProductDataModel extends StatusResponse implements Serializable {
    private ProductModel data;

    public ProductModel getData() {
        return data;
    }
}
