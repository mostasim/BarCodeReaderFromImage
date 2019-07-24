package tech.mbsoft.barcodereader;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public final class BackgroundExecutor extends Thread {
    public static Handler handler;

    public BackgroundExecutor() {
    }


    @Override
    public void run() {
        boolean looperIsNotPreparedInCurrentThread = Looper.myLooper() == null;

        if (looperIsNotPreparedInCurrentThread) {
            Looper.prepare();
        }

        handler = new Handler();

        if (looperIsNotPreparedInCurrentThread) {
            Looper.loop();
        }
    }

    public static void post(Runnable runnable) {
        handler.post(runnable);
    }

    public static void send(Message message) {
        handler.sendMessage(message);
    }

    public static void postOnMainThread(Runnable runnable)
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }
}
