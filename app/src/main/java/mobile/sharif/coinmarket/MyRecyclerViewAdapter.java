package mobile.sharif.coinmarket;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Coin> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<Coin> data) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.single_coin, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Coin coin = mData.get(position);
        holder.name.setText(coin.getDisplay_name());
        holder.price.setText(String.valueOf(coin.getPriceShow()));
        holder.one_hour.setText(String.valueOf(coin.showOne_hour_change()));
        if (coin.getOne_hour_change() < 0){
            holder.one_hour.setTextColor(Color.RED);
        }else{
            holder.one_hour.setTextColor(Color.GREEN);
        }
        holder.one_day.setText(String.valueOf(coin.showOne_day_change()));
        if (coin.getOne_day_change() < 0){
            holder.one_day.setTextColor(Color.RED);
        }else{
            holder.one_day.setTextColor(Color.GREEN);
        }
        holder.seven_day.setText(String.valueOf(coin.showSeven_hour_change()));
        if (coin.getSeven_day_change() < 0){
            holder.seven_day.setTextColor(Color.RED);
        }else{
            holder.seven_day.setTextColor(Color.GREEN);
        }
        GlideApp.with(context).load(coin.getLogo()).into(holder.logo);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView price;
        TextView one_hour;
        TextView one_day;
        TextView seven_day;
        ImageView logo;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView);
            price = itemView.findViewById(R.id.textView2);
            one_hour = itemView.findViewById(R.id.textView3);
            one_day = itemView.findViewById(R.id.textView4);
            seven_day = itemView.findViewById(R.id.textView5);
            logo = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Coin getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
