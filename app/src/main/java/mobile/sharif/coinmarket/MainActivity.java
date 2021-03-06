package mobile.sharif.coinmarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.google.android.gms.security.ProviderInstaller;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;


public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, View.OnClickListener {
    public static final int INITIALIZE_RECYCLERVIEW = 2;
    public static final int CLEAR_LIST = 3;
    public static final int RELOAD = 4;

    Button load_btn;
    private long mLastClickTime = 0;
    public static ArrayList<Coin> coins = new ArrayList<>();
    APIInterface api;

    MyRecyclerViewAdapter adapter;
    static RecyclerView recyclerView;
    ProgressBar progressBar;

    DbHelper dbHelper;
    SQLiteDatabase db;

    public static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mainActivityWeakReference;

        public MyHandler(MainActivity mainActivity) {
            this.mainActivityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = mainActivityWeakReference.get();
            switch (msg.what) {
                case INITIALIZE_RECYCLERVIEW:
                    mainActivity.adapter = new MyRecyclerViewAdapter(mainActivity, coins);
                    mainActivity.adapter.setClickListener(mainActivity);
                    recyclerView.setAdapter(mainActivity.adapter);
                    break;
                case CLEAR_LIST:
                    Log.i("start", mainActivity.api.getStart() + "");
                    mainActivity.adapter.notifyDataSetChanged();
                    mainActivity.api.resetStart();
                    break;
                case RELOAD:
                    Log.i("start1", mainActivity.api.getStart() + "");
                    mainActivity.progressBar.setProgress(0);
                    mainActivity.adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("MainLogs", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new MyHandler(this);

        // Button and progressbar Configuration
        progressBar = findViewById(R.id.pBar);
        load_btn = findViewById(R.id.load_btn);
        recyclerView = findViewById(R.id.coinlist);
        load_btn.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ------------------- SSL (HTTPS) PROTOCOL -----------------------
        ThreadPool.getInstance().submit(() -> {
            try {
                ProviderInstaller.installIfNeeded(getApplicationContext());
                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);
                SSLEngine engine = sslContext.createSSLEngine();
            } catch (Exception e) {
                Log.i("Error", e.toString());
            }
        });

        // ------------------- DB -----------------------
        ThreadPool.getInstance().submit(() -> {
            dbHelper = new DbHelper(this);
            // Gets the data repository in write mode
            db = dbHelper.getWritableDatabase();
            api = new APIInterface(db, dbHelper);
//        dbHelper.onUpgrade(db, 1, 1); // run this if have db problem

            coins = dbHelper.getAllCoins(db, progressBar);
            api.setStart(coins.size() + 1);
            if (coins.isEmpty()) {
                new AlertDialog.Builder(MainActivity.this).setMessage(R.string.not_internet)
                        .setPositiveButton(R.string.reload, (dialog, id) -> load_btn.callOnClick()).show();
            }
            handler.sendEmptyMessage(INITIALIZE_RECYCLERVIEW);
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent detailIntent = new Intent(this, DetailPage.class);
        detailIntent.putExtra("coin", adapter.getItem(position));
        startActivity(detailIntent);
    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 5000) {
            Toast.makeText(this, R.string.too_many_req, Toast.LENGTH_SHORT).show();
            return;
        }

        ThreadPool.getInstance().submit(() -> {
            mLastClickTime = SystemClock.elapsedRealtime();
            coins.clear();
            api.retrieveCoinFromApi(progressBar, handler);
        });
    }

    @Override
    protected void onStop() {
        Log.i("MainLogs", "onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i("MainLogs", "onRestart");
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.dump_data) {
            ThreadPool.getInstance().submit(() -> {
                dbHelper.deleteAllData(db);
                api.resetStart();
                coins.clear();
                handler.sendEmptyMessage(CLEAR_LIST);
            });
            return true;
        } else if (menuItem.getItemId() == R.id.reload_btn) {
            ThreadPool.getInstance().submit(() -> {
                coins.clear();
                coins.addAll(dbHelper.getAllCoins(db, progressBar));
                handler.sendEmptyMessage(RELOAD);
            });
            return true;
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
    }
}
