package common.error;

public class ObjectNotFound implements ErrorHARP {
    private int status;
    private String message;
    public static final String MSG =  "Not found ";

    public ObjectNotFound(int status, String message) {
        this.status = status;
        this.message = "Not found " + message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
