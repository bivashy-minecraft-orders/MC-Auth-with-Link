package com.bivashy.auth.api.database.model;

import com.bivashy.auth.api.account.Account;

public interface Reward {

    Account getAccount();

    long getCreationTimestamp();

}
