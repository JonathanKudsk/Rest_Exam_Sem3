package app.exceptions;

public class DatabaseException extends RuntimeException {
    private int code;

    public DatabaseException(int code, String msg){
        super(msg);
        this.code = code;
    }
    public int getCode(){
        return code;
    }
}