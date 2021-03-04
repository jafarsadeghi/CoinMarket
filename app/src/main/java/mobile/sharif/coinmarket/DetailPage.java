package mobile.sharif.coinmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class DetailPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        Bundle extras = getIntent().getExtras();
        Coin coin = new Coin();
        if (extras != null)
            if (extras.containsKey("coin"))
                coin = (Coin) getIntent().getSerializableExtra("coin");
    }
}
