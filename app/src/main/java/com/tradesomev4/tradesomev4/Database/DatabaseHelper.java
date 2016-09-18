package com.tradesomev4.tradesomev4.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tradesomev4.tradesomev4.m_Model.User;

/**
 * Created by Pastillas-Boy on 9/7/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "tradesome.db";
    public static final int DB_VERSiON = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSiON);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE user(" +
                "id VARCHAR(50)," +
                "name VARCHAR(50)," +
                "email VARCHAR(30)," +
                "image TEXT," +
                "latitude VARCHAR(30)," +
                "longitude VARCHAR(30)," +
                "blocked VARCHAR(10));";

        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS user;";
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    public User getUser(String id, SQLiteDatabase db){
        User user = new User();
        String sql = "SELECT * FROM user WHERE id = '" + id + "';";

        Cursor query = db.rawQuery(sql, null);
        if(query.getCount() != 0){
            return null;
        }else{
            user.setId(query.getString(0));
            user.setName(query.getString(1));
            user.setEmail(query.getString(2));
            user.setImage(query.getString(3));

            String tmp = query.getString(4);
            user.setLatitude(Double.parseDouble(tmp));

            tmp = query.getString(5);
            user.setLongitude(Double.parseDouble(tmp));

            tmp = query.getString(6);
            user.setBlocked(Boolean.parseBoolean(tmp));
        }

        return user;
    }

    public void insertUser(User user, SQLiteDatabase db){
        String sql = "INSERT INTO user(id, name, email, image, latitude, longitude, blocked) values('" +
                user.getId() + "','" +
                user.getName() + "','" +
                user.getEmail() + "','" +
                user.getImage() + "','" +
                user.getLatitude() + "','" +
                user.getLongitude() + "','" +
                user.isBlocked() + "');";

        db.execSQL(sql);
    }

    public void updateLocation(double latitude, double longitude, SQLiteDatabase db){
        String sql = "UPDATE user SET latitude = '" + latitude + "', SET longitude = '" + longitude + "';";
        db.execSQL(sql);
    }
}
