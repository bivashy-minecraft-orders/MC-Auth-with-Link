package me.mastercapexd.auth.database.model;

import com.bivashy.auth.api.account.Account;
import com.bivashy.auth.api.database.model.Reward;
import com.j256.ormlite.field.DatabaseField;
import me.mastercapexd.auth.account.AuthAccountAdapter;

import java.util.Collections;

public class DReward implements Reward {

    public static final String ACCOUNT_ID_FIELD_KEY = "account_id";
    public static final String CREATION_TIMESTAMP_FIELD_KEY = "creation_timestamp";
    @DatabaseField(generatedId = true)
    private long id;
    @DatabaseField(foreign = true, columnName = ACCOUNT_ID_FIELD_KEY, canBeNull = false)
    private AuthAccount account;
    @DatabaseField(columnName = CREATION_TIMESTAMP_FIELD_KEY)
    private long creationTimestamp;

    public DReward(AuthAccount account) {
        this.account = account;
        this.creationTimestamp = System.currentTimeMillis();
    }

    @Override
    public Account getAccount() {
        return new AuthAccountAdapter(account, Collections.emptyList());
    }

    @Override
    public long getCreationTimestamp() {
        return creationTimestamp;
    }

}
