package me.mastercapexd.auth.database.dao;

import com.bivashy.auth.api.account.Account;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import me.mastercapexd.auth.database.model.DReward;

import java.sql.SQLException;

public class RewardDao extends BaseDaoImpl<DReward, Long> {

    private static final SupplierExceptionCatcher DEFAULT_EXCEPTION_CATCHER = new SupplierExceptionCatcher();

    public RewardDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, DReward.class);
        TableUtils.createTableIfNotExists(connectionSource, DReward.class);
    }

    public boolean exists(Account account) {
        return DEFAULT_EXCEPTION_CATCHER.execute(() -> queryBuilder()
                .where()
                .eq(DReward.ACCOUNT_ID_FIELD_KEY, account.getDatabaseId())
                .queryForFirst() != null);
    }

    public int createReward(DReward reward) {
        return DEFAULT_EXCEPTION_CATCHER.execute(() -> create(reward));
    }

}
