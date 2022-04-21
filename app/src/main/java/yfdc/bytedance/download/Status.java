package yfdc.bytedance.download;
import org.jetbrains.annotations.NotNull;
public enum Status {
    PRE_ENCODE(0),
    ENCODING(1),
    ENCODED(2);
    private final int status;
    private Status(int code){
        this.status=code;
    }
    public final int getStatus() {
        return this.status;
    }
    @NotNull
    public final String toString(){
        return name();
    }
}
