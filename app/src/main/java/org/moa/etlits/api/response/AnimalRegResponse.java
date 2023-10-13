package org.moa.etlits.api.response;

public class AnimalRegResponse {
    private String message;
    private String type;
    private String context;

    public AnimalRegResponse(String message, String type, String context) {
        this.message = message;
        this.type = type;
        this.context = context;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
