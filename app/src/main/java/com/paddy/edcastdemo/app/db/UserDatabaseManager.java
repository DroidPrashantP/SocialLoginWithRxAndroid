package com.paddy.edcastdemo.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.paddy.edcastdemo.app.model.User;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import timber.log.Timber;


/**
 * Created by prashant on 6/7/17.
 */

public class UserDatabaseManager extends SQLiteOpenHelper {
    private static final String TAG = "UserDatabaseManager";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "User";
    private static final String TABLE_USER = "UserInfo";

    // StoreDbRows Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_EMAIL = "email";

    private SQLiteDatabase mSqLiteDatabase;
    private Context mContext;

    public UserDatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_StoreDbRowS_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_EMAIL + " TEXT,"
                + KEY_PHOTO + " TEXT" + ")";
        db.execSQL(CREATE_StoreDbRowS_TABLE);
        mSqLiteDatabase = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        // Create tables again
        onCreate(db);
    }

    /**
     * add user info into database
     *
     * @param user
     * @return
     */
    public Observable<String> addUserIntoTable(final User user) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String msg;
                ContentValues values = new ContentValues();
                values.put(KEY_ID, user.id);
                values.put(KEY_NAME, user.name);
                values.put(KEY_EMAIL, user.email);
                values.put(KEY_PHOTO, user.picture);
                if (!isUserPresent(user.id)) {
                    mSqLiteDatabase.insert(TABLE_USER, null, values);
                    msg = "Data inserted Successfully";
                } else {
                    msg = "User already Present";
                }

                emitter.onNext(msg);
                if (mSqLiteDatabase != null)
                    mSqLiteDatabase.close(); // Closing database connection
            }
        });
    }

    /**
     * get User data from database by using Id
     *
     * @param id
     * @return
     */
    public Observable<User> getUserData(final String id) {
        Timber.d(TAG, "getUserData: " + id);
        final SQLiteDatabase db = this.getReadableDatabase();
        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                Cursor cursor = db.query(TABLE_USER, new String[]{KEY_ID, KEY_NAME,
                                KEY_EMAIL, KEY_PHOTO}, KEY_ID + "=?",
                        new String[]{String.valueOf(id)}, null, null, null, null);
                try {
                    User user = new User();
                    if (cursor.moveToFirst()) {
                        user.id = cursor.getString(0);
                        user.name = cursor.getString(1);
                        user.email = cursor.getString(2);
                        user.picture = cursor.getString(3);
                    }
                    emitter.onNext(user);
                    cursor.close();

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                if (db != null) {
                    db.close();
                }

            }
        });

    }

    /**
     * check user is present or not into the database
     *
     * @param id
     * @return
     */
    private boolean isUserPresent(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{KEY_NAME,
                        KEY_EMAIL, KEY_PHOTO}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        try {
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * delete table data when user logout from app
     */
    public void deleteAll() {
        if (mSqLiteDatabase != null) {
            mSqLiteDatabase.execSQL("delete from " + TABLE_USER);
            mSqLiteDatabase.close();
        }
    }

    /**
     * update user info into the database
     *
     * @param user
     * @return
     */
    public Observable<User> updateUserInfo(final User user) {
        final SQLiteDatabase db = getWritableDatabase();
        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                ContentValues values = new ContentValues();
                values.put(KEY_ID, user.id);
                values.put(KEY_NAME, user.name);
                values.put(KEY_EMAIL, user.email);
                boolean isUpdated = db.update(TABLE_USER, values, KEY_ID + "=" + user.id, null) > 0;
                if (isUpdated)
                    emitter.onNext(user);

                if (db != null) {
                    db.close();
                }
            }
        });
    }
}
