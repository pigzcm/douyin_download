package yfdc;
import org.jetbrains.annotations.NotNull;
import kotlin.jvm.internal.Intrinsics;
public final class FileUtil {
    private FileUtil(){
        throw new RuntimeException();
    }
    @NotNull
    public static java.io.File makeFile(@NotNull final String path){
        Intrinsics.checkParameterIsNotNull(path,"path");
        java.io.File p = new java.io.File(path);
        boolean b = true;
        try {
            if(!p.exists()){
                b = p.createNewFile();
            }else {
                b = p.delete() && p.createNewFile();
                b = b && p.canWrite();
            }
        } catch (Throwable e){
            e.printStackTrace(System.out);
        }
        if (!b){
            throw new IllegalStateException("error.");
        }
        return p;
    }
    @NotNull
    public static java.io.FileOutputStream stream(@NotNull final java.io.File file){
        Intrinsics.checkParameterIsNotNull(file,"file");
        java.io.FileOutputStream a = null;
        try {
            a = new java.io.FileOutputStream(file, false);
        } catch (java.io.FileNotFoundException ex){
            // ignore
        } catch (Throwable other){
            throw new IllegalStateException(other);
        }
        Intrinsics.checkNotNull(a, "not found.");
        return java.util.Objects.requireNonNull(a);
    }
}
