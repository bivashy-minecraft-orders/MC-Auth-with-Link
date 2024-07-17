package me.mastercapexd.auth.database;

import com.bivashy.auth.api.account.Account;
import com.bivashy.auth.api.database.RewardDatabase;
import com.bivashy.auth.api.database.model.Reward;
import me.mastercapexd.auth.database.dao.RewardDao;
import me.mastercapexd.auth.database.model.DReward;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RewardDatabaseProxy implements RewardDatabase {

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final RewardDao rewardDao;

    public RewardDatabaseProxy(RewardDao rewardDao) {
        this.rewardDao = rewardDao;
    }

    @Override
    public CompletableFuture<Boolean> exists(Account account) {
        return CompletableFuture.supplyAsync(() -> rewardDao.exists(account), executorService);
    }

    @Override
    public CompletableFuture<Integer> create(Reward reward) {
        return CompletableFuture.supplyAsync(() -> rewardDao.createReward((DReward) reward), executorService);
    }

}
