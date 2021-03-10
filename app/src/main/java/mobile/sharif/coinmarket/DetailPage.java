package mobile.sharif.coinmarket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AtomicFile;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import java.util.ArrayList;


public class DetailPage extends AppCompatActivity {
    Switch range ;
    ArrayList<CandleEntry> yValsCandleStick;
    CandleStickChart candleStickChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        /*Bundle extras = getIntent().getExtras();
        Coin coin = new Coin();
        if (extras != null)
            if (extras.containsKey("coin"))
                coin = (Coin) getIntent().getSerializableExtra("coin");
*/
        CandleDataStructer.setDataForTest();
        yValsCandleStick = new ArrayList<>();
        yValsCandleStick.addAll(CandleDataStructer.candleEntries);
        range = findViewById(R.id.range);
        range.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(DetailPage.this, "Changed", Toast.LENGTH_SHORT).show();
                if(!range.isChecked()){
                    ArrayList<CandleEntry> temp= new ArrayList<>();
                    for(int i = 0; i<7; i++){
                        temp.add(CandleDataStructer.candleEntries.get(i));
                    }
                    yValsCandleStick = temp;
                }else{
                    yValsCandleStick = new ArrayList<>();
                    yValsCandleStick.addAll(CandleDataStructer.candleEntries);
                }
                candleStickChart = findViewById(R.id.candle_stick_chart);
                candleStickChart.setHighlightPerDragEnabled(true);
                candleStickChart.setDrawBorders(true);
                candleStickChart.setBorderColor(getResources().getColor(R.color.colorLightGray));

                YAxis yAxis = candleStickChart.getAxisLeft();
                YAxis rightAxis = candleStickChart.getAxisRight();
                yAxis.setDrawGridLines(false);
                rightAxis.setDrawGridLines(false);
                candleStickChart.requestDisallowInterceptTouchEvent(true);

                XAxis xAxis = candleStickChart.getXAxis();

                xAxis.setDrawGridLines(false);// disable x axis grid lines
                xAxis.setDrawLabels(true);
                rightAxis.setTextColor(Color.BLACK);
                yAxis.setDrawLabels(true);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setAvoidFirstLastClipping(true);

                Legend l = candleStickChart.getLegend();
                l.setEnabled(true);

                CandleDataSet set1 = new CandleDataSet(yValsCandleStick, "DataSet 1");
                set1.setColor(Color.rgb(80, 80, 80));
                set1.setShadowColor(getResources().getColor(R.color.colorLightGrayMore));
                set1.setShadowWidth(0.8f);
                set1.setDecreasingColor(getResources().getColor(R.color.colorRed));
                set1.setDecreasingPaintStyle(Paint.Style.FILL);
                set1.setIncreasingColor(getResources().getColor(R.color.colorAccent));
                set1.setIncreasingPaintStyle(Paint.Style.FILL);
                set1.setNeutralColor(Color.LTGRAY);
                set1.setDrawValues(true);



                // create a data object with the datasets
                CandleData data = new CandleData(set1);


                // set data
                candleStickChart.setData(data);
                candleStickChart.invalidate();
            }
        });

    }

}
