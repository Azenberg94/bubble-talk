package com.groupe6al2.bubbletalk.Class;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import java.util.ArrayList;

/**
 * Created by goasguenl on 20/01/2017.
 */

public class BubbleTalkSQLite extends SQLiteOpenHelper {

    private static final int version = 7;
    private static final String Name_DataBase = "BubbleTalk";
    private static final String TABLE_USERS = "UserBubble";
    private static final String COL_ID = "id";
    private static final String COL_ID_USER = "idUser";
    private static final String COL_PSEUDO = "pseudo";
    private static final String COL_EMAIL = "email";
    private static final String COL_NAME = "name";
    private static final String COL_AVATAR = "avatar";
    private static final String COL_USE_PSEUDO = "usePseudo";



    public BubbleTalkSQLite(Context context) {
        super(context, Name_DataBase, null, version);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table UserBubble " +
                        "("+COL_ID+" interger primary key,"+COL_ID_USER+" text, "+COL_NAME+" text,pseudo text,"+COL_USE_PSEUDO+" boolean, "+COL_EMAIL+" text, "+COL_AVATAR+" text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(COL_ID_USER, user.getId());
        value.put(COL_EMAIL, user.getEmail());
        value.put(COL_PSEUDO,"");
        value.put(COL_USE_PSEUDO,false);
        value.put(COL_NAME, user.getName());
        value.put(COL_AVATAR, "");
        db.insert(TABLE_USERS, null, value);

    }

    public User getUser(String id){
        User myUser = new User();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_USERS +" WHERE idUser = '"+id+"'",null);
        ArrayList<User> userList = new ArrayList<User>();
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                myUser.setId(cursor.getString(cursor.getColumnIndex(COL_ID_USER)));
                myUser.setName(cursor.getString(cursor.getColumnIndex(COL_NAME)));
                myUser.setPseudo(cursor.getString(cursor.getColumnIndex(COL_PSEUDO)));
                boolean value = cursor.getInt(cursor.getColumnIndex(COL_USE_PSEUDO)) > 0;
                myUser.setUsePseudo(value);
                myUser.setEmail(cursor.getString(cursor.getColumnIndex(COL_EMAIL)));
                myUser.setAvatar(cursor.getString(cursor.getColumnIndex(COL_AVATAR)));

                userList.add(myUser);
                cursor.moveToNext();
            }
        }
        return userList.get(0);
    }

    public void updateUser(String id,String[] data){
        SQLiteDatabase db = this.getWritableDatabase();

        if(!data[0].isEmpty()){
            db.execSQL("UPDATE "+TABLE_USERS+" SET "+COL_PSEUDO+ "='"+data[0]+ "' WHERE "+COL_ID_USER+"='"+id+"'");
        }
        if(!data[1].isEmpty()){
            db.execSQL("UPDATE "+TABLE_USERS+" SET "+COL_EMAIL+ "='"+data[1]+ "' WHERE "+COL_ID_USER+"='"+id+"'");
        }
        if(!data[2].isEmpty()){
            db.execSQL("UPDATE "+TABLE_USERS+" SET "+COL_NAME+ "='"+data[2]+ "' WHERE "+COL_ID_USER+"='"+id+"'");
        }
        if(!data[3].isEmpty()){
            if (data[3].equals("true")){
                System.out.println("test true");
                db.execSQL("UPDATE "+TABLE_USERS+" SET "+COL_USE_PSEUDO+ "= 1 WHERE "+COL_ID_USER+"='"+id+"'");
            }else{
                System.out.println("test false");
                db.execSQL("UPDATE "+TABLE_USERS+" SET "+COL_USE_PSEUDO+ "= 0 WHERE "+COL_ID_USER+"='"+id+"'");
            }
        }
        if(!data[4].isEmpty()){
            System.out.println(data[4].length());
            db.execSQL("UPDATE "+TABLE_USERS+" SET "+COL_AVATAR+ "='"+ data[4] + "' WHERE "+COL_ID_USER+"='"+id+"'");
        }

    }

   public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USERS);
    }

    public int getCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

}