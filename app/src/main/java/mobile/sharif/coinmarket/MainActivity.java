package mobile.sharif.coinmarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    String db_name = "coin_db";
    Button button;

    MyRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    public static final int REQ_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Reloading", Toast.LENGTH_SHORT).show();
            }
        });
        Coin btc = new Coin("bitcoin", "btc", 16000);
        Coin eth = new Coin("Etreum", "eth");
        Coin ltc = new Coin("Litecoin ", "ltc");
        Coin ada = new Coin("Cardano", "ada");
        Coin dot = new Coin("Polkadot", "dot");
        Coin xlm = new Coin("Stellar", "xlm");
        Coin iota = new Coin("IOTA", "IOTA");

        ArrayList<Coin> coins = new ArrayList<>();
        coins.add(btc);
        coins.add(eth);
        coins.add(ltc);
        coins.add(ada);
        coins.add(dot);
        coins.add(xlm);
        coins.add(iota);

        // set up the RecyclerView
        recyclerView = findViewById(R.id.coinlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, coins);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        APIInterface api = new APIInterface();
        api.getCoins();
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent detailIntent = new Intent(this, DetailPage.class);
        detailIntent.putExtra("coin", adapter.getItem(position));
        startActivityForResult(detailIntent, REQ_CODE);
        Toast.makeText(this, "You clicked " + adapter.getItem(position).getName() + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

}
