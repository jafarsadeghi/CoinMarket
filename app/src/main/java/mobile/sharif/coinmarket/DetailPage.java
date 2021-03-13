package mobile.sharif.coinmarket;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class DetailPage extends AppCompatActivity {
    public static final int CREATE_CHART = 1;

    private final APIInterface api = new APIInterface();
    private ArrayList<CandleEntry> candleEntries;
    private ArrayList<CandleEntry> yValuesCandleStick = new ArrayList<>();
    private Coin coin;

    public static class MyHandler extends Handler {
        private final WeakReference<DetailPage> detailPageWeakReference;

        public MyHandler(DetailPage detailPage) {
            this.detailPageWeakReference = new WeakReference<>(detailPage);
        }

        @Override
        public void handleMessage(Message msg) {
            DetailPage detailPage = detailPageWeakReference.get();
            switch (msg.what) {
                case CREATE_CHART:
                    detailPage.range.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        Toast.makeText(detailPage, "Changed", Toast.LENGTH_SHORT).show();
                        if (detailPage.candleEntries.size() >= 7) {
                            if (detailPage.range.isChecked()) {
                                detailPage.yValuesCandleStick.clear();
                                for (int i = 0; i < 7; i++) {
                                    detailPage.yValuesCandleStick.add(detailPage.candleEntries.get(i));
                                }
                            } else {
                                detailPage.yValuesCandleStick.clear();
                                detailPage.yValuesCandleStick.addAll(detailPage.candleEntries);
                            }

                            detailPage.candleStickChart.setHighlightPerDragEnabled(true);
                            detailPage.candleStickChart.setDrawBorders(true);
                            detailPage.candleStickChart.setBorderColor(detailPage.getResources().getColor(R.color.colorLightGray));
                            detailPage.candleStickChart.requestDisallowInterceptTouchEvent(true);

                            detailPage.setAxisOptions();

                            Legend l = detailPage.candleStickChart.getLegend();
                            l.setEnabled(true);

                            CandleDataSet set1 = detailPage.setCandleDataSet(detailPage.coin);

                            // create a data object with the dataset
                            CandleData data = new CandleData(set1);

                            // set data
                            detailPage.candleStickChart.setData(data);
                            detailPage.candleStickChart.invalidate();
                        } else {
                            Toast.makeText(detailPage, "No Candle Data Received", Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
            }
        }
    }

    private MyHandler handler;


    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch range;
    CandleStickChart candleStickChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        Bundle extras = getIntent().getExtras();
        range = findViewById(R.id.range);
        candleStickChart = findViewById(R.id.candle_stick_chart);

        handler = new MyHandler(this);

        coin = (Coin) getIntent().getSerializableExtra("coin");

        ThreadPool.getInstance().submit(() -> {
            candleEntries = api.getCandles(coin.getSymbol(), APIInterface.Range.oneMonth);
            handler.sendEmptyMessage(CREATE_CHART);
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

    private CandleDataSet setCandleDataSet(Coin coin) {

        CandleDataSet set = new CandleDataSet(yValuesCandleStick, coin.getDisplay_name());
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
        Log.i("Detailed", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Detailed", "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Detailed", "onPause");
    }
}
