package yfdc;
import org.jetbrains.annotations.NotNull;
public interface YFCallBack {
    void onSuccess(@NotNull String s);
    void onFailed(@NotNull String s);
}
