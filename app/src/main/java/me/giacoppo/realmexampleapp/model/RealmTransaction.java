package me.giacoppo.realmexampleapp.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Peppe on 07/12/2016.
 */

public class RealmTransaction extends RealmObject {
    public class FIELDS {
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String AMOUNT = "amount";
        public static final String ACCOUNT = "account";
        public static final String DATE = "date";
    }

    @PrimaryKey
    private String id;

    @Required
    private String title;
    private double amount;
    private RealmAccount account;
    private String date;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public RealmAccount getAccount() {
        return account;
    }

    public String getDate() {
        return date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setAccount(RealmAccount account) {
        this.account = account;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
