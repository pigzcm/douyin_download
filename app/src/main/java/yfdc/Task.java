package yfdc;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Response;
public abstract class Task implements okhttp3.Callback{
    private volatile int status;
    public Task(){
        super();
        synchronized (this){
            status = 0;
        }
    }
    public final synchronized void myWait(){
        synchronized (this){
            try {
                this.wait();
            }catch (Throwable e){
                throw new RuntimeException(e);
            }
        }
    }
    @Override public void onFailure(@NotNull Call call, @NotNull IOException e) {
        synchronized (this){
            status = 2;
            this.notify();
        }
    }
    @Override public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        synchronized (this){
            this.notify();
            status = 1;
        }
    }
    public synchronized final int getStatus(){
        synchronized (this){
            return status;
        }
    }
}
