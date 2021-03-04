package mobile.sharif.coinmarket;

import android.view.View;
import android.widget.TextView;

import java.io.Serializable;

public class Coin  implements Serializable {
    private long row_id;
    private String name;
    private String short_name;
    private int price;
    private String one_hour_change;
    private String one_day_change;
    private String seven_day_change;

    public Coin(){}

    public Coin(String name, String short_name, int price) {
        this.name = name;
        this.short_name = short_name;
        this.price = price;
    }

    public Coin(String name, String short_name) {
        this.name = name;
        this.short_name = short_name;
    }

    public void fill_view(View view) {
        TextView name = view.findViewById(R.id.textView);
        String display_name = this.name + " | " + this.short_name;
        name.setText(display_name);
        TextView price = view.findViewById(R.id.textView2);
        price.setText(this.price);
        TextView one_hour = view.findViewById(R.id.textView3);
        one_hour.setText(this.one_hour_change);
        TextView one_day = view.findViewById(R.id.textView4);
        one_day.setText(this.one_day_change);
        TextView seven_day = view.findViewById(R.id.textView5);
        seven_day.setText(this.seven_day_change);
    }

    public long getRow_id() {
        return row_id;
    }

    public void setRow_id(long row_id) {
        this.row_id = row_id;
    }

    String getName() {
        return name;
    }

    String getShort_name() {
        return short_name;
    }

    String getPrice() {
        return price + "$";
    }

    String getOne_hour_change() {
        return one_hour_change;
    }

    String getOne_day_change() {
        return one_day_change;
    }

    String getSeven_day_change() {
        return seven_day_change;
    }

    String getDisplay_name() {
        return short_name.toUpperCase() + " | " + name;
    }

}
