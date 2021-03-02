package mobile.sharif.coinmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String db_name = "coin_db";
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        Coin btc = new Coin("bitcoin", "btc");
        Coin eth = new Coin("Etreum", "eth");
        Coin ltc = new Coin("Litecoin ", "ltc");
        Coin ada = new Coin("Cardano", "ada");
        Coin dot = new Coin("Polkadot", "dot");
        Coin xlm = new Coin("Stellar", "xlm");
        Coin iota = new Coin("IOTA", "IOTA");

        btc.fill_view(findViewById(R.id.coin1));
        eth.fill_view(findViewById(R.id.coin2));
        ltc.fill_view(findViewById(R.id.coin3));
        ada.fill_view(findViewById(R.id.coin4));
        dot.fill_view(findViewById(R.id.coin5));
        xlm.fill_view(findViewById(R.id.coin6));
        iota.fill_view(findViewById(R.id.coin7));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Reloading", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
