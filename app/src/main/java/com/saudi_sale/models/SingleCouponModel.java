package com.saudi_sale.models;

import java.io.Serializable;

public class SingleCouponModel extends StatusResponse implements Serializable {
    private CouponModel data;

    public CouponModel getData() {
        return data;
    }
}
