package me.giacoppo.realmexampleapp.ui.portafogli;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import me.giacoppo.realmexampleapp.R;
import me.giacoppo.realmexampleapp.adapter.MyRealmTransactionAdapter;
import me.giacoppo.realmexampleapp.listener.OnItemClickListener;
import me.giacoppo.realmexampleapp.model.RealmAccount;
import me.giacoppo.realmexampleapp.model.RealmTransaction;

public class SingleAccountActivity extends AppCompatActivity implements OnItemClickListener<RealmTransaction>, RealmChangeListener<RealmResults<RealmTransaction>>, View.OnClickListener {

    private static final String ACCOUNT_KEY = "ACCOUNT_KEY";
    private static final String ACCOUNT_NAME = "ACCOUNT_NAME";
    private String id ="";
    private String accountName ="";

    private EditText name,amount;

    private RealmResults<RealmTransaction> transactions;
    private MyRealmTransactionAdapter adapter;
    private RecyclerView recyclerView;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_account);

        id = getIntent().getStringExtra(ACCOUNT_KEY);
        accountName = getIntent().getStringExtra(ACCOUNT_NAME);

        setTitle(accountName);

        name = (EditText) findViewById(R.id.realm_transaction_name);
        amount = (EditText) findViewById(R.id.realm_transaction_amount);

        findViewById(R.id.realm_transaction_add).setOnClickListener(this);

        realm = Realm.getDefaultInstance();
        recyclerView = (RecyclerView) findViewById(R.id.realm_transaction_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        transactions = getTransactions(id);
        transactions.addChangeListener(this);

        adapter = new MyRealmTransactionAdapter(this);
        adapter.setList(transactions);
        recyclerView.setAdapter(adapter);
    }

    public static Intent getIntent(Context context, String id, String accountName) {
        Intent i = new Intent(context, SingleAccountActivity.class);
        i.putExtra(ACCOUNT_KEY, id);
        i.putExtra(ACCOUNT_NAME,accountName);
        return i;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.realm_transaction_add:
                addTransaction(name.getText().toString(),Double.valueOf(amount.getText().toString()));
            break;
        }
    }

    @Override
    public void onItemClick(RealmTransaction realmTransaction) {

    }

    /* DB METHODS */

    @Override
    public void onChange(RealmResults<RealmTransaction> element) {
        adapter.setList(element);
    }

    private RealmResults<RealmTransaction> getTransactions(String accountId) {
       return realm.where(RealmTransaction.class).equalTo("account.id",accountId).findAll();
    }

    private void addTransaction(final String title, final double amount) {
        final RealmAccount account = realm.where(RealmAccount.class).equalTo(RealmAccount.FIELDS.ID,id).findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmTransaction rt = new RealmTransaction();
                rt.setId(UUID.randomUUID().toString());
                rt.setTitle(title);
                rt.setAmount(amount);
                rt.setDate(String.valueOf(new Date().getTime()));
                rt.setAccount(account);

                final RealmTransaction managedTransaction = realm.copyToRealm(rt);
                account.getTransactions().add(managedTransaction);

            }
        });

        this.name.setText("");
        this.amount.setText("");
    }
}
