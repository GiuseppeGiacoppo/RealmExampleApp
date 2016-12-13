package me.giacoppo.realmexampleapp.ui.comparison;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import me.giacoppo.realmexampleapp.R;
import me.giacoppo.realmexampleapp.enums.DBEnum;
import me.giacoppo.realmexampleapp.model.RealmAccount;
import me.giacoppo.realmexampleapp.sqlite.MyDatabaseHelper;
import me.giacoppo.realmexampleapp.sqlite.tables.AccountTable;

import static me.giacoppo.realmexampleapp.enums.DBEnum.REALM;
import static me.giacoppo.realmexampleapp.enums.DBEnum.REALM_MULTIPLE;
import static me.giacoppo.realmexampleapp.enums.DBEnum.SQLITE;

public class ComparisonActivity extends AppCompatActivity implements View.OnClickListener {
    private static final long TEST_ENTRIES = 1000;

    private List<RealmAccount> realmAccounts = new ArrayList<>();
    private Realm realmMain, realm;
    private RealmConfiguration realmComparison, realm2Comparison;
    private MyDatabaseHelper sqlite;
    private SQLiteDatabase db;
    private ScrollView scroll;

    private Button writeComparison, readComparison;

    private boolean realmWriteCompleted, realm2WriteCompleted, sqliteWriteCompleted;
    private boolean realmReadCompleted, sqliteReadCompleted;
    private TextView text, progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);
        setTitle(R.string.comparison);

        text = (TextView) findViewById(R.id.textView);
        progress = (TextView) findViewById(R.id.progress);

        scroll = (ScrollView) findViewById(R.id.comparison_scrollview);
        writeComparison = (Button) findViewById(R.id.write_comparison);
        readComparison = (Button) findViewById(R.id.read_comparison);

        readComparison.setOnClickListener(this);
        writeComparison.setOnClickListener(this);

        realmComparison = new RealmConfiguration.Builder()
                .name("comparison.realm")
                .build();

        realm2Comparison = new RealmConfiguration.Builder()
                .name("comparison2.realm")
                .build();

        sqlite = new MyDatabaseHelper(getApplicationContext());
        db = sqlite.getWritableDatabase();

        new CreatingListTask().execute();
    }

    private int realmReadTest() {
        RealmResults<RealmAccount> items = realm.where(RealmAccount.class).findAll();
        Log.i("ComparisonActivity", "realmReadTest: Entries: " + items.size());
        return items.size();
    }

    private int sqliteReadTest() {
        Cursor c = db.query(AccountTable.TABLE_NAME, new String[]{AccountTable._ID, AccountTable.NAME, AccountTable.TOTAL}, null, null, null, null, null);
        Log.i("ComparisonActivity", "sqliteReadTest: Entries: " + c.getCount());
        return c.getCount();
    }

    private void realmErase() {
        realmMain = Realm.getInstance(realmComparison);
        realmMain.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
        realmMain.close();
    }

    private void realmErase2() {
        realmMain = Realm.getInstance(realm2Comparison);
        realmMain.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
        realmMain.close();
    }

    private void sqliteErase() {
        db.execSQL(MyDatabaseHelper.SQL_DELETE_ACCOUNT_TABLE);
        db.execSQL(MyDatabaseHelper.SQL_CREATE_ACCOUNT_TABLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqlite.close();
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, ComparisonActivity.class);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.write_comparison:
                text.append("\n\nWriting " + TEST_ENTRIES + " entries...");

                realmWriteCompleted = false;
                realm2WriteCompleted = false;
                sqliteWriteCompleted = false;

                writeComparison.setEnabled(false);
                readComparison.setEnabled(false);
                new WritingTask().execute(REALM);
                new WritingTask().execute(REALM_MULTIPLE);
                new WritingTask().execute(SQLITE);
                break;
            case R.id.read_comparison:
                text.append("\n\nReading...");

                realmReadCompleted = false;
                sqliteReadCompleted = false;

                readComparison.setEnabled(false);
                writeComparison.setEnabled(false);
                new ReadingTask().execute(REALM);
                new ReadingTask().execute(SQLITE);
                break;
        }
    }

    private class CreatingListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            text.append("Creating list...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < TEST_ENTRIES; i++) {
                RealmAccount account = new RealmAccount();
                account.setId(UUID.randomUUID().toString());
                account.setName("Account " + i);
                realmAccounts.add(account);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            text.append("\nList ready!");
        }
    }

    private class WritingTask extends AsyncTask<DBEnum, Integer, Long> {
        private DBEnum type;
        long startTime;

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progress.setText("Writing: " + values[0] + "%");
        }

        @Override
        protected Long doInBackground(DBEnum... strings) {
            type = strings[0];
            switch (strings[0]) {
                case REALM:
                    try {
                        realm = Realm.getInstance(realmComparison);
                        startTime = System.currentTimeMillis();
                        realm.beginTransaction();
                        for (int i = 0; i < realmAccounts.size(); i++) {
                            RealmAccount account = realmAccounts.get(i);
                            account.setId(UUID.randomUUID().toString());
                            realm.copyToRealm(account);
                            publishProgress((int)(100 * i / realmAccounts.size()));
                        }
                        realm.commitTransaction();
                    } finally {
                        realm.close();
                    }
                    break;
                case REALM_MULTIPLE:
                    try {
                        realm = Realm.getInstance(realm2Comparison);
                        startTime = System.currentTimeMillis();
                        for (int i = 0; i < realmAccounts.size(); i++) {
                            realm.beginTransaction();
                            RealmAccount account = realmAccounts.get(i);
                            account.setId(UUID.randomUUID().toString());
                            realm.copyToRealm(account);
                            realm.commitTransaction();
                            publishProgress((int)(100 * i / realmAccounts.size()));
                        }
                    } finally {
                        realm.close();
                    }
                    break;
                case SQLITE:
                    startTime = System.currentTimeMillis();
                    for (int i = 0; i < realmAccounts.size(); i++) {
                        RealmAccount account = realmAccounts.get(i);
                        account.setId(UUID.randomUUID().toString());
                        ContentValues values = new ContentValues();
                        values.put(AccountTable._ID, account.getId());
                        values.put(AccountTable.NAME, account.getName());
                        values.put(AccountTable.TOTAL, 0);
                        db.insert(AccountTable.TABLE_NAME, null, values);
                        publishProgress((int)(100 * i / realmAccounts.size()));
                    }
                    break;
            }
            return System.currentTimeMillis();
        }

        @Override
        protected void onPostExecute(Long finishTime) {
            super.onPostExecute(finishTime);
            progress.setText("");
            switch (type) {
                case REALM:
                    realmWriteCompleted = true;
                    text.append("\nRealm, 1 transaction: ");
                    break;
                case REALM_MULTIPLE:
                    realm2WriteCompleted = true;
                    text.append("\nRealm, " + TEST_ENTRIES + " transactions: ");
                    break;
                case SQLITE:
                    sqliteWriteCompleted = true;
                    text.append("\nSqlite: ");
                    break;
            }
            long elapsed = finishTime - startTime;

            text.append(elapsed + "ms");
            scrollDown();

            if (realmWriteCompleted && realm2WriteCompleted && sqliteWriteCompleted) {
                writeComparison.setEnabled(true);
                readComparison.setEnabled(true);
            }
        }
    }

    private class ReadingTask extends AsyncTask<DBEnum, Void, Long> {
        private DBEnum type;
        long startTime;
        long count = 0;

        @Override
        protected Long doInBackground(DBEnum... strings) {
            type = strings[0];
            switch (strings[0]) {
                case REALM:
                    try {
                        realm = Realm.getInstance(realmComparison);
                        startTime = System.currentTimeMillis();
                        count = realmReadTest();
                    } finally {
                        realm.close();
                    }
                    break;
                case SQLITE:
                    startTime = System.currentTimeMillis();
                    count = sqliteReadTest();
                    break;
            }
            return System.currentTimeMillis();
        }

        @Override
        protected void onPostExecute(Long finishTime) {
            super.onPostExecute(finishTime);
            switch (type) {
                case REALM:
                    realmReadCompleted = true;
                    text.append("\nRealm, " + count + " entries: ");
                    break;

                case SQLITE:
                    sqliteReadCompleted = true;
                    text.append("\nSqlite, " + count + " entries: ");
                    break;
            }

            long elapsed = finishTime - startTime;
            text.append(elapsed + "ms");
            scrollDown();

            if (realmReadCompleted && sqliteReadCompleted) {
                readComparison.setEnabled(true);
                writeComparison.setEnabled(true);
            }
        }
    }

    private void scrollDown() {
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comparison_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.erase_db:
                realmErase();
                realmErase2();
                sqliteErase();
                text.append("\n\nDatabases erased");
                scrollDown();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
