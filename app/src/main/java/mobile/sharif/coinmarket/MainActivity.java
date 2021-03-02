package mobile.sharif.coinmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    String db_name = "coin_db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Coin btc = new Coin("bitcoin", "btc");
        Coin eth = new Coin("Etreum", "eth");
        Coin ltc = new Coin("Litecoin ", "ltc");
        Coin ada = new Coin("Cardano", "ada");
        Coin dot = new Coin("Polkadot", "dot");
        Coin xlm = new Coin("Stellar", "xlm");
        Coin iota = new Coin("IOTA", "IOTA");
        Coin xrp = new Coin("ripple", "xrp");
        Coin neo = new Coin("neo", "NEO");
        Coin eos = new Coin("Eos", "eos");

        btc.fill_view(findViewById(R.id.coin1));
        eth.fill_view(findViewById(R.id.coin2));
        ltc.fill_view(findViewById(R.id.coin3));
        ada.fill_view(findViewById(R.id.coin4));
        dot.fill_view(findViewById(R.id.coin5));
        xlm.fill_view(findViewById(R.id.coin6));
        iota.fill_view(findViewById(R.id.coin7));
        xrp.fill_view(findViewById(R.id.coin8));
        neo.fill_view(findViewById(R.id.coin9));
        eos.fill_view(findViewById(R.id.coin10));
    }

}
