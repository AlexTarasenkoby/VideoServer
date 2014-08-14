package com.springapp.JSonClasses;

import java.util.List;

/**
 * Created by Kirill on 8/14/2014.
 */
public class JsonResponseCollection {
    private boolean success;
    private String message;
    private int total;
    private List<Item> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Item> getData() {
        return data;
    }

    public void setData(List<Item> data) {
        this.data = data;
    }
}
