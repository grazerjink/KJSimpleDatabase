package com.winsonmac.sample;

import com.winsonmac.kjsimpledatabase.KJDatabaseManager;
import com.winsonmac.sample.database.DatabaseConfigHelper;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        KJDatabaseManager.init(this)
                .setConfigHelper(new DatabaseConfigHelper());
    }
}