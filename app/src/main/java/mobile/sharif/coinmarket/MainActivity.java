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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.android.gms.security.ProviderInstaller;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;


public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, View.OnClickListener {
    public static final int INITIALIZE_VIEW = 1;

    Button load_btn;
    private long mLastClickTime = 0;
    ProgressBar progressBar;
    static ArrayList<Coin> coins = new ArrayList<>();
    APIInterface api;

    static MyRecyclerViewAdapter adapter;
    static RecyclerView recyclerView;
    public static final int REQ_CODE = 11;

    DbHelper dbHelper;
    SQLiteDatabase db;
    boolean threadcomplete = false;

    public static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mainActivity;

        public MyHandler(MainActivity mainActivity) {
            this.mainActivity = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INITIALIZE_VIEW:
                    Log.i("HASHEM", "handleMessage: llllllllllllll");
                    adapter = new MyRecyclerViewAdapter(mainActivity.get(), coins);
                    adapter.setClickListener(mainActivity.get());
                    recyclerView.setAdapter(adapter);
            }
        }
    }

    private final MyHandler mainHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("MainLogs", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Button and progressbar Configuration
        progressBar = findViewById(R.id.pBar);
        load_btn = findViewById(R.id.load_btn);
        recyclerView = findViewById(R.id.coinlist);
        load_btn.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // ------------------- SSL (HTTPS) PROTOCOL -----------------------
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            SSLEngine engine = sslContext.createSSLEngine();
        } catch (Exception e) {
            Log.i("Error", e.toString());
        }

        // ------------------- DB -----------------------
        dbHelper = new DbHelper(this);
        // Gets the data repository in write mode
        db = dbHelper.getWritableDatabase();
        api = new APIInterface(db, dbHelper);
//        dbHelper.onUpgrade(db, 1, 1); // run this if have db problem

        // start fetching on another thread
        ThreadPool.getInstance().submit(() -> {
            coins = dbHelper.getAllCoins(db, dbHelper, progressBar);
            if (coins.isEmpty()) {
                new AlertDialog.Builder(MainActivity.this).setMessage(R.string.not_internet)
                        .setPositiveButton(R.string.reload, (dialog, id) -> load_btn.callOnClick()).show();
            }
            Message message = new Message();
            message.what = INITIALIZE_VIEW;
            mainHandler.sendMessage(message);
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent detailIntent = new Intent(this, DetailPage.class);
        detailIntent.putExtra("coin", adapter.getItem(position));
        startActivityForResult(detailIntent, REQ_CODE);
    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 5000) {
            Toast.makeText(this, R.string.too_many_req, Toast.LENGTH_SHORT).show();
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Log.i("BIG", "start big compute");
        api.retrieveCoinFromApi(progressBar);
        coins.clear();
        adapter.notifyDataSetChanged();
        coins.addAll(dbHelper.getAllCoins(db, dbHelper, progressBar));
        adapter.notifyItemRangeInserted(0, coins.size());
        progressBar.setProgress(0);
        Log.i("BIG", "end of big computation");
        threadcomplete = true;
        boolean untill_end = true;
        while (untill_end) {
            if (threadcomplete) {
                untill_end = false;
            }
        }

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
            api.resetStart();
            coins.clear();
            adapter.notifyDataSetChanged();
            dbHelper.deleteAllData(db);
            return true;
        } else if (menuItem.getItemId() == R.id.reload_btn) {
            coins.clear();
            adapter.notifyDataSetChanged();
            coins.addAll(dbHelper.getAllCoins(db, dbHelper, progressBar));
            adapter.notifyItemRangeInserted(0, coins.size());
            progressBar.setProgress(0);
            return true;
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThreadPool.getInstance().end();
    }
}
