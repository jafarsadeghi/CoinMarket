package mobile.sharif.coinmarket.Thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MyHandler extends Handler {
    public static final int TASK_A = 1;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case TASK_A:
                Log.i("Message", "Hey!");
        }
    }
}
