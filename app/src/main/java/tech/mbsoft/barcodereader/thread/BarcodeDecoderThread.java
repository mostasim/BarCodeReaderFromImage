package tech.mbsoft.barcodereader.thread;

import android.util.Log;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class BarcodeDecoderThread extends Thread {
    private static final String TAG = "BarcodeDecoderThread";
    private AtomicBoolean isAlive = new AtomicBoolean(true);
    private ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    public BarcodeDecoderThread() {
        super(TAG);
        start();
    }

    @Override
    public void run() {
        while (isAlive.get()) {
            Runnable task = tasks.poll();
            if (task != null) {
                task.run();
            }
        }
        Log.e(TAG, "BarcodeDecoder Thread terminated!");
    }

    public BarcodeDecoderThread execute(Runnable task) {
        tasks.add(task);
        return this;
    }

    public void quit() {
        isAlive.set(false);
    }
}
