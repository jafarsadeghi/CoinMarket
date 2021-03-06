package mobile.sharif.coinmarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import com.google.android.gms.security.ProviderInstaller;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, View.OnClickListener {
    Button button;
    Handler handler = new Handler();
    ProgressBar progressBar;
    int prog = 0;
    private int i = 0;
    private Handler hdlr = new Handler();
    ArrayList<Coin> coins = new ArrayList<>();

    MyRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    public static final int REQ_CODE = 11;
    public static final int REQ_RESUME = 11;

    FeedReaderDbHelper dbHelper;
    SQLiteDatabase db;
    boolean threadcomplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("MainLogs", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //testing detailed activity
//        Intent detailIntent = new Intent(this, DetailPage.class);
//        startActivity(detailIntent);

        // Button Configuration
        progressBar = findViewById(R.id.pBar);
        button = findViewById(R.id.button);
        button.setOnClickListener(this);
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
        dbHelper = new FeedReaderDbHelper(this);
        // Gets the data repository in write mode
        db = dbHelper.getWritableDatabase();

        Runnable runnable = () -> coins = dbHelper.getAllCoins(db, dbHelper, progressBar);

        runnable.run();
        if (coins.isEmpty()) {
            new AlertDialog.Builder(this).setMessage(R.string.not_internet)
                    .setPositiveButton(R.string.reload, (dialog, id) -> button.callOnClick()).show();
        }
        Log.i("COINS", coins.toString());
        // ------------------- RECYCLER VIEW -----------------------
        recyclerView = findViewById(R.id.coinlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, coins);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent detailIntent = new Intent(this, DetailPage.class);
        detailIntent.putExtra("coin", adapter.getItem(position));
        startActivityForResult(detailIntent, REQ_CODE);
        Toast.makeText(this, "You clicked " + adapter.getItem(position).getName() + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        APIInterface api = new APIInterface(db, dbHelper);
        Runnable newthread = () -> {
            Log.i("BIG", "start big compute");
            api.retrieveCoinFromApi(progressBar);
            coins.clear();
            coins.addAll(dbHelper.getAllCoins(db, dbHelper, progressBar));
            adapter.notifyItemRangeInserted(0, coins.size());
            progressBar.setProgress(0);
            Log.i("BIG", "end of big computation");
            threadcomplete = true;
        };
        newthread.run();
        boolean untill_end = true;
        while (untill_end) {
            if (threadcomplete) {
                untill_end = false;
            }
        }

    }

    @Override
    protected void onStart() {
        Log.i("MainLogs", "onStart");
        super.onStart();
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
            coins.clear();
            adapter.notifyDataSetChanged();
            dbHelper.deleteAllData(db);
            return true;
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
    }

}
