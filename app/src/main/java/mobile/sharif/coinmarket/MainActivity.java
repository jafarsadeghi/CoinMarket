package mobile.sharif.coinmarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Button Configuration
        button = findViewById(R.id.button);
        button.setOnClickListener(this);
        // ------------------- PASSING DATA -----------------------
        Bundle extras = getIntent().getExtras();

        if (extras != null ) {
            if (extras.containsKey("coins")) {
                Intent intent = getIntent();
                Bundle args = intent.getBundleExtra("coins");
                ArrayList<Coin> object = (ArrayList<Coin>) args.getSerializable("ARRAYLIST");
                coins.addAll(object);
            }
        }
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
        Coin btc = new Coin("Bitcoin", "BTC", 1500.53);
        dbHelper = new FeedReaderDbHelper(this);
        // Gets the data repository in write mode
        db = dbHelper.getWritableDatabase();

        // method to insert coin in db
//        dbHelper.putCoin(db, btc);
        // method to get coins
        coins = dbHelper.getAllCoins(db, dbHelper);

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
            APIInterface api = new APIInterface();
            Intent resume_intent = new Intent(MainActivity.this, MainActivity.class);
            Bundle args = new Bundle();
            ArrayList<Coin> c = api.getCoins(db, dbHelper);
            args.putSerializable("ARRAYLIST", c);
            resume_intent.putExtra("coins", args);
            startActivityForResult(resume_intent, REQ_RESUME);
        }
    }
}
