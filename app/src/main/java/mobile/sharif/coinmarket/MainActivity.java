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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Button Configuration
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
        coins = dbHelper.getAllCoins(db, dbHelper); // method to get coins
        if (coins.isEmpty()){
            new AlertDialog.Builder(this).setMessage(R.string.not_internet)
                    .setPositiveButton(R.string.reload, (dialog, id) -> {
                        button.callOnClick();
                    }).show();
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
        if (view.getId() == R.id.button) {
            APIInterface api = new APIInterface(db, dbHelper);
            Runnable newthread = () -> {
                Log.i("BIG","start big compute");
                api.retrieveCoinFromApi();
                threadcomplete = true;
            };

            Thread t = new Thread(newthread);
            newthread.run();

            boolean b = true;
            while (b) {
                if (threadcomplete) {
                    b = false;
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
        }
    }
}
