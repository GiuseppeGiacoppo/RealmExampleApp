package me.giacoppo.realmexampleapp.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Calendar;

import io.realm.RealmList;
import io.realm.RealmResults;
import me.giacoppo.realmexampleapp.listener.OnItemClickListener;
import me.giacoppo.realmexampleapp.R;
import me.giacoppo.realmexampleapp.model.RealmTransaction;
import me.giacoppo.realmexampleapp.util.Utils;


public class MyRealmTransactionAdapter extends RecyclerView.Adapter<MyRealmTransactionAdapter.ViewHolder> {

    private RealmList<RealmTransaction> items = new RealmList<>();
    private OnItemClickListener<RealmTransaction> listener;
    DecimalFormat df = new DecimalFormat("0.00");


    public MyRealmTransactionAdapter(OnItemClickListener<RealmTransaction> listener) {
        this.listener = listener;
    }

    public void setList(RealmList<RealmTransaction> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void setList(RealmResults<RealmTransaction> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return  new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RealmTransaction transaction = items.get(position);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(transaction.getDate()));

        holder.title.setText(transaction.getTitle());

        if (transaction.getAmount()>= 0) {
            holder.amount.setText(String.format("+ %s €",df.format(Math.abs(transaction.getAmount()))));
            holder.amount.setTextColor(ContextCompat.getColor(holder.amount.getContext(), R.color.positive));
        } else {
            holder.amount.setText(String.format("- %s €",df.format(Math.abs(transaction.getAmount()))));
            holder.amount.setTextColor(ContextCompat.getColor(holder.amount.getContext(), R.color.negative));
        }

        holder.dayofWeek.setText(Utils.days[cal.get(Calendar.DAY_OF_WEEK)]);
        holder.day.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
        holder.month.setText(String.format("/%s",(cal.get(Calendar.MONTH)+1)));

        holder.bind(items.get(position),listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,amount,dayofWeek,day,month;
        LinearLayout container;

        ViewHolder(View itemView) {
            super(itemView);

            container = (LinearLayout) itemView.findViewById(R.id.item_transaction_container);
            title = (TextView) itemView.findViewById(R.id.transaction_title);
            amount = (TextView) itemView.findViewById(R.id.transaction_amount);
            dayofWeek = (TextView) itemView.findViewById(R.id.transaction_dow);
            day = (TextView) itemView.findViewById(R.id.transaction_day);
            month = (TextView) itemView.findViewById(R.id.transaction_month);

        }

        void bind(final RealmTransaction item, final OnItemClickListener<RealmTransaction> listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });
        }
    }

}
