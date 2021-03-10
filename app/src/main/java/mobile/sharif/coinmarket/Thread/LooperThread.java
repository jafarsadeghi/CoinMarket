package mobile.sharif.coinmarket.Thread;

import android.os.Looper;
import android.util.Log;

public class LooperThread extends Thread {
    public MyHandler myHandler;

    @Override
    public void run() {
        Looper.prepare();
        myHandler = new MyHandler();
        Log.i("LooperThread", "Hey!");
        Looper.loop();
        super.run();
    }
}
