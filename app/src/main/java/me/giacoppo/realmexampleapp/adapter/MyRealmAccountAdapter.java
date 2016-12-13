package me.giacoppo.realmexampleapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import me.giacoppo.realmexampleapp.R;
import me.giacoppo.realmexampleapp.listener.OnItemClickListener;
import me.giacoppo.realmexampleapp.listener.OnItemLongClickListener;
import me.giacoppo.realmexampleapp.model.RealmAccount;
import me.giacoppo.realmexampleapp.model.RealmTransaction;

/**
 * Created by Peppe on 07/12/2016.
 */

public class MyRealmAccountAdapter extends RecyclerView.Adapter<MyRealmAccountAdapter.ViewHolder> {

    private RealmList<RealmAccount> items = new RealmList<>();
    private OnItemClickListener<RealmAccount> listener;
    private OnItemLongClickListener<RealmAccount> listenerLongClick;
    private Realm realm;

    public MyRealmAccountAdapter(OnItemClickListener<RealmAccount> listener,OnItemLongClickListener<RealmAccount> listenerLongClick) {
        this.listener = listener;
        this.listenerLongClick = listenerLongClick;
        realm = Realm.getDefaultInstance();
    }

    public void setList(RealmResults<RealmAccount> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RealmAccount account = items.get(position);
        holder.title.setText(account.getName());

        holder.transactions.setText(holder.transactions.getResources().getQuantityString(R.plurals.transactions,account.getTransactions().size(),account.getTransactions().size()));

        double total = realm.where(RealmTransaction.class).equalTo("account.id",account.getId()).sum(RealmTransaction.FIELDS.AMOUNT).doubleValue();

        holder.amount.setText(String.valueOf(total));
        holder.bind(account,listener);
        holder.bindLongPress(account,listenerLongClick);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, transactions, amount;

        ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.account_name);
            transactions = (TextView) v.findViewById(R.id.account_transactions);
            amount = (TextView) v.findViewById(R.id.account_amount);
        }

        void bind(final RealmAccount item, final OnItemClickListener<RealmAccount> listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });
        }

        void bindLongPress(final RealmAccount item, final OnItemLongClickListener<RealmAccount> listener) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClick(item);
                    return true;
                }
            });
        }
    }
}
