package com.app.mast.app;

import android.app.Application;

import com.app.mast.db.DatabaseHandler;

/**
 * Created by pawansingh on 21/05/18.
 */

public class AppController extends Application {
    private static AppController appController;
    public DatabaseHandler databaseHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        appController = this;
        databaseHandler = new DatabaseHandler(this);
    }

    public static AppController getInstance() {
        return appController;
    }
}
