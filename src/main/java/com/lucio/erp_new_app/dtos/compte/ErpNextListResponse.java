package com.lucio.erp_new_app.dtos.compte;

import java.util.List;

public class ErpNextListResponse<T> {
    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}