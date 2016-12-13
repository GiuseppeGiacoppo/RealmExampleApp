package me.giacoppo.realmexampleapp.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Peppe on 07/12/2016.
 */

public class RealmAccount extends RealmObject {
    public static class FIELDS { //utility class to get field names
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String TRANSACTIONS = "transactions";
    }

    @PrimaryKey
    private String id;

    @Index
    private String name;

    private RealmList<RealmTransaction> transactions;

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public RealmList<RealmTransaction> getTransactions() {
        return transactions;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setTransactions(RealmList<RealmTransaction> transactions) {
        this.transactions = transactions;
    }
}
