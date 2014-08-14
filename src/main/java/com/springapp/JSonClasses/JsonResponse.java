package com.springapp.JSonClasses;

/**
 * Created by Kirill on 8/14/2014.
 */
public class JsonResponse {
    private boolean success;
    private String message;

    public JsonResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

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
}
