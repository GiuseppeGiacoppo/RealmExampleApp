package me.giacoppo.realmexampleapp.ui.portafogli;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import me.giacoppo.realmexampleapp.R;
import me.giacoppo.realmexampleapp.adapter.MyRealmAccountAdapter;
import me.giacoppo.realmexampleapp.listener.OnItemClickListener;
import me.giacoppo.realmexampleapp.listener.OnItemLongClickListener;
import me.giacoppo.realmexampleapp.model.RealmAccount;
import me.giacoppo.realmexampleapp.model.RealmTransaction;
import me.giacoppo.realmexampleapp.ui.dialog.AlertDialogFragment;

public class WalletActivity extends AppCompatActivity implements OnItemClickListener<RealmAccount>, RealmChangeListener<RealmResults<RealmAccount>>, View.OnClickListener, TextWatcher, OnItemLongClickListener<RealmAccount> {
    private EditText name;
    private RecyclerView recyclerView;
    private Button add;
    private RealmResults<RealmAccount> accounts;

    private MyRealmAccountAdapter adapter;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        setTitle(R.string.wallet_app);

        realm = Realm.getDefaultInstance();
        recyclerView = (RecyclerView) findViewById(R.id.accounts_list);
        name = (EditText) findViewById(R.id.account_name);
        name.addTextChangedListener(this);

        add = (Button)findViewById(R.id.button_add);
        add.setOnClickListener(this);
        add.setEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        accounts = getAccounts();
        accounts.addChangeListener(this);
        adapter = new MyRealmAccountAdapter(this,this);
        adapter.setList(accounts);

        recyclerView.setAdapter(adapter);


        final RealmAccount result =
                realm
                .where(RealmAccount.class)
                .equalTo(RealmAccount.FIELDS.NAME, "Portafogli")
                .findFirst(); // Ottengo il primo tra i conti col nome "Portafogli"

        realm.beginTransaction();
        result.setName("Nuovo nome");
        realm.commitTransaction(); //Il conto precedentemente trovato adesso si chiama "Nuovo nome"

        realm.beginTransaction();
        result.deleteFromRealm();
        realm.commitTransaction(); //Il conto precedentemente trovato è stato cancellato
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, WalletActivity.class);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_add:
                if (!TextUtils.isEmpty(name.getText())) {
                    addAccount(name.getText().toString());
                    name.setText("");
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!TextUtils.isEmpty(charSequence)) {
            adapter.setList(findAccounts(charSequence.toString()));
            add.setEnabled(true);
        } else {
            adapter.setList(getAccounts());
            add.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    /* ADAPTER METHODS */
    @Override
    public void onItemClick(RealmAccount item) {
        startActivity(SingleAccountActivity.getIntent(this,item.getId(),item.getName()));
    }

    @Override
    public void onItemLongClick(final RealmAccount realmAccount) {
        AlertDialogFragment.newInstance(getString(R.string.delete_confirm)).setPositiveButton(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAccount(realmAccount);
            }
        }).show(getFragmentManager(), AlertDialogFragment.TAG);
    }


    /* DB METHODS */
    @Override
    public void onChange(RealmResults<RealmAccount> element) {
        adapter.setList(element);
    }

    private RealmResults<RealmAccount> getAccounts() {
        return realm.where(RealmAccount.class).findAll();
    }

    private RealmResults<RealmAccount> findAccounts(String accountName) {
        return realm.where(RealmAccount.class)
                .beginsWith(RealmAccount.FIELDS.NAME,accountName)
                .findAll();
    }

    private void deleteTransactions(String accountId) {
        final RealmResults<RealmTransaction> results = realm.where(RealmTransaction.class).equalTo("account.id",accountId).findAll(); //"account.id": we can retrieve a list of results using a key inside a realmobject
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });
    }

    private void deleteAccount(RealmAccount account) {
        final RealmAccount result = realm.where(RealmAccount.class).equalTo(RealmAccount.FIELDS.NAME,account.getName()).findFirst();

        deleteTransactions(result.getId());

        realm.beginTransaction();
        result.deleteFromRealm();
        realm.commitTransaction();
    }

    private RealmAccount addAccount(String name) {
        RealmAccount unmanagedAccount = new RealmAccount();
        unmanagedAccount.setId(UUID.randomUUID().toString());
        unmanagedAccount.setName(name);

        realm.beginTransaction();
        final RealmAccount managedAccount= realm.copyToRealm(unmanagedAccount);
        realm.commitTransaction();

        return managedAccount;
    }
}
