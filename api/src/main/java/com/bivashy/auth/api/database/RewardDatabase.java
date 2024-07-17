package com.bivashy.auth.api.database;

import com.bivashy.auth.api.account.Account;
import com.bivashy.auth.api.database.model.Reward;

import java.util.concurrent.CompletableFuture;

public interface RewardDatabase {

    CompletableFuture<Boolean> exists(Account account);

    CompletableFuture<Integer> create(Reward reward);

}
