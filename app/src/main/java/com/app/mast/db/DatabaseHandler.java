package com.app.mast.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.mast.models.Repository;
import com.app.mast.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pawansingh on 21/05/18.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "must";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_REPOSITORY = "repositories";

    private static final String REPOSITORY_LOGIN = "repository_login";
    private static final String REPOSITORY_NAME = "repository_name";
    private static final String REPOSITORY_HTML_URL = "repository_html_url";
    private static final String REPOSITORY_SIZE = "repository_size";
    private static final String REPOSITORY_WATCHERS = "repository_watchers";
    private static final String REPOSITORY_OPEN_ISSUES_COUNT = "repository_open_issue_count";
    private static final String REPOSITORY_DESCRIPTION = "repository_description";
    private static final String REPOSITORY_USER_AVATAR_URL = "repository_user_avatar_url";

    private static final String USER_LOGIN = "user_login";
    private static final String USER_AVATAR_URL = "user_avatar_url";
    private static final String USER_NAME = "user_name";
    private static final String USER_PUBLIC_REPOS = "user_public_repos";
    private static final String USER_PUBLIC_GISTS = "user_public_gists";
    private static final String USER_FOLLOWERS = "user_followers";
    private static final String USER_FOLLOWING = "user_following";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + USER_LOGIN + " TEXT, " + USER_AVATAR_URL + " TEXT, " + USER_NAME + " TEXT, "
                + USER_PUBLIC_REPOS + " INTEGER ," + USER_PUBLIC_GISTS + " INTEGER ," + USER_FOLLOWERS + " INTEGER ," + USER_FOLLOWING + " INTEGER "
                + ")";

        String CREATE_REPOSITORY_TABLE = "CREATE TABLE " + TABLE_REPOSITORY + "("
                + REPOSITORY_LOGIN + " TEXT, " + REPOSITORY_NAME + " TEXT, " + REPOSITORY_HTML_URL + " TEXT, "
                + REPOSITORY_DESCRIPTION + " TEXT, " + REPOSITORY_USER_AVATAR_URL + " TEXT, "
                + REPOSITORY_SIZE + " INTEGER ," + REPOSITORY_WATCHERS + " INTEGER ," + REPOSITORY_OPEN_ISSUES_COUNT + " INTEGER ," + USER_FOLLOWING + " INTEGER "
                + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_REPOSITORY_TABLE);
    }



    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }


    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_LOGIN, user.getLogin());
        values.put(USER_AVATAR_URL, user.getAvatar_url());
        values.put(USER_NAME, user.getName());
        values.put(USER_PUBLIC_REPOS, user.getPublic_repos());
        values.put(USER_PUBLIC_GISTS, user.getPublic_gists());
        values.put(USER_FOLLOWERS, user.getFollowers());
        values.put(USER_FOLLOWING, user.getFollowing());

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public void addRepository(Repository repository, String login) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(REPOSITORY_LOGIN, login);
        values.put(REPOSITORY_NAME, repository.getName());
        values.put(REPOSITORY_HTML_URL, repository.getHtml_url());
        values.put(REPOSITORY_DESCRIPTION, repository.getDescription());
        values.put(REPOSITORY_OPEN_ISSUES_COUNT, repository.getOpen_issues_count());
        values.put(REPOSITORY_SIZE, repository.getSize());
        values.put(REPOSITORY_WATCHERS, repository.getWatchers());
        values.put(REPOSITORY_USER_AVATAR_URL, repository.getOwner().getAvatar_url());

        db.insert(TABLE_REPOSITORY, null, values);
        db.close();
    }


    public User getUser(String login) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{USER_LOGIN, USER_AVATAR_URL, USER_NAME, USER_PUBLIC_REPOS, USER_PUBLIC_GISTS, USER_FOLLOWERS, USER_FOLLOWING},
                USER_LOGIN + "=?",
                new String[]{String.valueOf(login)}, null, null, null, null);


        if (cursor != null && cursor.getCount() == 0) {
            return null;
        } else
            cursor.moveToFirst();

        User user = new User();
        user.setLogin(cursor.getString(cursor.getColumnIndexOrThrow(USER_LOGIN)));
        user.setAvatar_url(cursor.getString(cursor.getColumnIndexOrThrow(USER_AVATAR_URL)));
        user.setFollowers(cursor.getInt(cursor.getColumnIndexOrThrow(USER_FOLLOWERS)));
        user.setFollowing(cursor.getInt(cursor.getColumnIndexOrThrow(USER_FOLLOWING)));
        user.setName(cursor.getString(cursor.getColumnIndexOrThrow(USER_NAME)));
        user.setPublic_repos(cursor.getInt(cursor.getColumnIndexOrThrow(USER_PUBLIC_REPOS)));
        user.setPublic_gists(cursor.getInt(cursor.getColumnIndexOrThrow(USER_PUBLIC_GISTS)));

        return user;
    }


    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<User>();
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setLogin(cursor.getString(cursor.getColumnIndexOrThrow(USER_LOGIN)));
                user.setAvatar_url(cursor.getString(cursor.getColumnIndexOrThrow(USER_AVATAR_URL)));
                user.setFollowers(cursor.getInt(cursor.getColumnIndexOrThrow(USER_FOLLOWERS)));
                user.setFollowing(cursor.getInt(cursor.getColumnIndexOrThrow(USER_FOLLOWING)));
                user.setName(cursor.getString(cursor.getColumnIndexOrThrow(USER_NAME)));
                user.setPublic_repos(cursor.getInt(cursor.getColumnIndexOrThrow(USER_PUBLIC_REPOS)));
                user.setPublic_gists(cursor.getInt(cursor.getColumnIndexOrThrow(USER_PUBLIC_GISTS)));
                userList.add(user);
            } while (cursor.moveToNext());
        }

        return userList;
    }

    public List<Repository> getAllRepositories(String login) {
        List<Repository> repositoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_REPOSITORY,
                new String[]{REPOSITORY_LOGIN, REPOSITORY_DESCRIPTION, REPOSITORY_HTML_URL, REPOSITORY_NAME, REPOSITORY_USER_AVATAR_URL, REPOSITORY_OPEN_ISSUES_COUNT, REPOSITORY_SIZE, REPOSITORY_WATCHERS},
                REPOSITORY_LOGIN + "=?",
                new String[]{String.valueOf(login)}, null, null, null, null);
        if(cursor != null && cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    Repository repository = new Repository();
                    repository.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(REPOSITORY_DESCRIPTION)));
                    repository.setHtml_url(cursor.getString(cursor.getColumnIndexOrThrow(REPOSITORY_HTML_URL)));
                    repository.setName(cursor.getString(cursor.getColumnIndexOrThrow(REPOSITORY_NAME)));
                    repository.setOpen_issues_count(cursor.getInt(cursor.getColumnIndexOrThrow(REPOSITORY_OPEN_ISSUES_COUNT)));
                    repository.setSize(cursor.getInt(cursor.getColumnIndexOrThrow(REPOSITORY_SIZE)));
                    repository.setWatchers(cursor.getInt(cursor.getColumnIndexOrThrow(REPOSITORY_WATCHERS)));
                    User user = new User();
                    user.setAvatar_url(cursor.getString(cursor.getColumnIndexOrThrow(REPOSITORY_USER_AVATAR_URL)));
                    repository.setOwner(user);
                    repositoryList.add(repository);
                } while (cursor.moveToNext());
            }
        }

        return repositoryList;
    }


    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_LOGIN, user.getLogin());
        values.put(USER_AVATAR_URL, user.getAvatar_url());
        values.put(USER_NAME, user.getName());
        values.put(USER_PUBLIC_REPOS, user.getPublic_repos());
        values.put(USER_PUBLIC_GISTS, user.getPublic_gists());
        values.put(USER_FOLLOWERS, user.getFollowers());
        values.put(USER_FOLLOWING, user.getFollowing());


        return db.update(TABLE_USERS, values, USER_LOGIN + " = ?", new String[]{String.valueOf(user.getLogin())});
    }


    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, USER_LOGIN + " = ?",
                new String[]{String.valueOf(user.getLogin())});
        db.close();
    }


    public void deleteRepository(String login) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REPOSITORY, REPOSITORY_LOGIN + " = ?",
                new String[]{String.valueOf(login)});
        db.close();
    }


    public int getUserCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

}