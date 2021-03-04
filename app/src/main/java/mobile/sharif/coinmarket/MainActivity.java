package mobile.sharif.coinmarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.google.android.gms.security.ProviderInstaller;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.xml.parsers.FactoryConfigurationError;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    String db_name = "coin_db";
    Button button;
    Handler handler = new Handler();
    ProgressBar progressBar;
    int prog= 0;

    MyRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    public static final int REQ_CODE = 11;
    public static final int RESUME_REQ = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        Bundle extras = getIntent().getExtras();

        ArrayList<Coin> coins = new ArrayList<>();
        if (extras != null)
            if (extras.containsKey("coins")) {
                Intent intent = getIntent();
                Bundle args = intent.getBundleExtra("BUNDLE");
                ArrayList<Coin> object = (ArrayList<Coin>) args.getSerializable("ARRAYLIST");
                coins.addAll(object);
            }

        // set up the RecyclerView
        recyclerView = findViewById(R.id.coinlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, coins);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        // handle ssl
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            SSLEngine engine = sslContext.createSSLEngine();
        } catch (Exception e){
            Log.i("Error", e.toString());
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIInterface api = new APIInterface();
                Intent resume_intent = new Intent(MainActivity.this, MainActivity.class);
                Bundle args = new Bundle();
                ArrayList<Coin> c = api.getCoins();
                args.putSerializable("ARRAYLIST",c);
                resume_intent.putExtra("BUNDLE",args);
                startActivityForResult(resume_intent, RESUME_REQ);
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        Intent detailIntent = new Intent(this, DetailPage.class);
        detailIntent.putExtra("coin", adapter.getItem(position));
        startActivityForResult(detailIntent, REQ_CODE);
        Toast.makeText(this, "You clicked " + adapter.getItem(position).getName() + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

}
