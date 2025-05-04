package com.lucio.erp_new_app.dtos;

import java.util.List;

// ErpNextListResponse.java
public class ErpNextListResponse<T> {
    private List<T> data;

    // Standard getters and setters
    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}