package mobile.sharif.coinmarket;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import java.util.ArrayList;


public class DetailPage extends AppCompatActivity {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch range;
    ArrayList<CandleEntry> yValsCandleStick;
    CandleStickChart candleStickChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        Bundle extras = getIntent().getExtras();
        Coin coin = new Coin();
        if (extras != null)
            if (extras.containsKey("coin"))
                coin = (Coin) getIntent().getSerializableExtra("coin");

        APIInterface api = new APIInterface();
        api.getCandles(coin.getSymbol(), APIInterface.Range.oneMonth);
        yValsCandleStick = new ArrayList<>();
        yValsCandleStick.addAll(api.candleEntries);
        range = findViewById(R.id.range);
        Coin finalCoin = coin;
        range.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(DetailPage.this, "Changed", Toast.LENGTH_SHORT).show();
            if(api.candleEntries.size()>7){
                if (!range.isChecked()) {
                    ArrayList<CandleEntry> temp = new ArrayList<>();
                    for (int i = api.candleEntries.size()-7; i < api.candleEntries.size(); i++) {
                        temp.add(api.candleEntries.get(i));
                    }
                    yValsCandleStick = temp;
                } else {
                    yValsCandleStick = new ArrayList<>();
                    yValsCandleStick.addAll(api.candleEntries);
                }
            }else {
                Toast.makeText(DetailPage.this , "No Candle Data Recieved" , Toast.LENGTH_SHORT).show();
            }
            candleStickChart = findViewById(R.id.candle_stick_chart);
            candleStickChart.setHighlightPerDragEnabled(true);
            candleStickChart.setDrawBorders(true);
            candleStickChart.setBorderColor(getResources().getColor(R.color.colorLightGray));
            candleStickChart.requestDisallowInterceptTouchEvent(true);


            setAxisOptions();

            Legend l = candleStickChart.getLegend();
            l.setEnabled(true);

            CandleDataSet set1 = SetCandleDataSet(finalCoin);


            // create a data object with the datasets
            CandleData data = new CandleData(set1);


            // set data
            candleStickChart.setData(data);
            candleStickChart.invalidate();

        });


    }

    private void setAxisOptions() {
        YAxis yAxis = candleStickChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setDrawLabels(true);

        YAxis rightAxis = candleStickChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTextColor(Color.BLACK);

        XAxis xAxis = candleStickChart.getXAxis();
        xAxis.setDrawGridLines(false);// disable x axis grid lines
        xAxis.setDrawLabels(true);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(true);
    }

    private CandleDataSet SetCandleDataSet(Coin coin) {

        CandleDataSet set = new CandleDataSet(yValsCandleStick, coin.getDisplay_name());
        set.setColor(Color.rgb(80, 80, 80));
        set.setShadowColor(getResources().getColor(R.color.colorLightGrayMore));
        set.setShadowWidth(0.8f);
        set.setDecreasingColor(getResources().getColor(R.color.colorRed));
        set.setDecreasingPaintStyle(Paint.Style.FILL);
        set.setIncreasingColor(getResources().getColor(R.color.colorAccent));
        set.setIncreasingPaintStyle(Paint.Style.FILL);
        set.setNeutralColor(Color.LTGRAY);
        set.setDrawValues(true);
        return set;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Detailed" , "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Detailed" , "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Detailed" , "onPause");
    }
}
