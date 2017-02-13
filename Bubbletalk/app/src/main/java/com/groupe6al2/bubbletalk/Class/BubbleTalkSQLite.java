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

    
    private static final int version = 21;
    private static final String Name_DataBase = "BubbleTalk";
    private static final String TABLE_USERS = "UserBubble";
    private static final String COL_IDU = "id";
    private static final String COL_ID_USER = "idUser";
    private static final String COL_PSEUDO = "pseudo";
    private static final String COL_EMAIL = "email";
    private static final String COL_NAME = "name";
    private static final String COL_AVATAR = "avatar";
    private static final String COL_USE_PSEUDO = "usePseudo";
    private static final String TABLE_BUBBLE= "BubbleBubble";
    private static final String COL_IDB = "id";
    private static final String COL_ID_BUBBLE = "idBubble";
    private static final String COL_NAME_BUBBLE = "name";
    private static final String COL_DESCRIPTION_BUBBLE = "description";
    private static final String COL_PROPRIO = "proprio";
    private static final String COL_AVATAR_MD5_BUBBLE = "avatarMd5";
    private static final String COL_LAT_BUBBLE = "lat";
    private static final String COL_LONG_BUBBLE = "long";
    private static final String COL_ACTIVE_BUBBLE = "isActive";


    public BubbleTalkSQLite(Context context) {
        super(context, Name_DataBase, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table UserBubble " +
                        "("+COL_IDU+" interger primary key,"+COL_ID_USER+" text, "+COL_NAME+" text,pseudo text,"+COL_USE_PSEUDO+" boolean, "+COL_EMAIL+" text, "+COL_AVATAR+" text)"
        );
        db.execSQL(
                "create table BubbleBubble " +
                        "("+COL_IDB+" interger primary key,"+COL_ID_BUBBLE+" text, "+COL_NAME_BUBBLE+" text,"+COL_DESCRIPTION_BUBBLE+" text,"+COL_PROPRIO+" text,"+COL_AVATAR_MD5_BUBBLE+" text, "+COL_LAT_BUBBLE+" text,"+COL_LONG_BUBBLE+" text, "+COL_ACTIVE_BUBBLE+" text)"

        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUBBLE);
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

    public void addBubble(Bubble bubble){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(COL_ID_BUBBLE, bubble.getId());
        value.put(COL_NAME_BUBBLE, bubble.getName());
        value.put(COL_DESCRIPTION_BUBBLE, bubble.getDescription());
        value.put(COL_PROPRIO, bubble.getProprio());
        value.put(COL_AVATAR_MD5_BUBBLE, bubble.getAvatarMd5());
        value.put(COL_LAT_BUBBLE, bubble.getLatitude());
        value.put(COL_LONG_BUBBLE, bubble.getLongitude());
        value.put(COL_ACTIVE_BUBBLE, bubble.getActive());
        db.insert(TABLE_BUBBLE,null,value);
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

    public ArrayList<Bubble> getMyBubbles(String id){
        ArrayList<Bubble> bubbleArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_BUBBLE +" WHERE proprio = '"+id+"' ORDER BY name",null);
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                bubbleArrayList.add(new Bubble(cursor.getString(cursor.getColumnIndex(COL_ID_BUBBLE)), cursor.getString(cursor.getColumnIndex(COL_NAME_BUBBLE)), cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION_BUBBLE)), cursor.getString(cursor.getColumnIndex(COL_PROPRIO)), cursor.getString(cursor.getColumnIndex(COL_AVATAR_MD5_BUBBLE)), cursor.getString(cursor.getColumnIndex(COL_LAT_BUBBLE)), cursor.getString(cursor.getColumnIndex(COL_LONG_BUBBLE)), cursor.getString(cursor.getColumnIndex(COL_ACTIVE_BUBBLE))));
                cursor.moveToNext();
            }
        }
        return bubbleArrayList;
    }

    public Bubble getOneBubble(String id){
        ArrayList<Bubble> bubbleArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_BUBBLE +" WHERE idBubble = '"+id+"'",null);
        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                bubbleArrayList.add(new Bubble(cursor.getString(cursor.getColumnIndex(COL_ID_BUBBLE)), cursor.getString(cursor.getColumnIndex(COL_NAME_BUBBLE)), cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION_BUBBLE)), cursor.getString(cursor.getColumnIndex(COL_PROPRIO)), cursor.getString(cursor.getColumnIndex(COL_AVATAR_MD5_BUBBLE)), cursor.getString(cursor.getColumnIndex(COL_LAT_BUBBLE)), cursor.getString(cursor.getColumnIndex(COL_LONG_BUBBLE)), cursor.getString(cursor.getColumnIndex(COL_ACTIVE_BUBBLE))));
                cursor.moveToNext();
            }
        }
        return bubbleArrayList.get(0);
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

    public void updateBubble(String id,String[] data){
        SQLiteDatabase db = this.getWritableDatabase();

        if(!data[0].isEmpty()){
            db.execSQL("UPDATE "+TABLE_BUBBLE+" SET "+COL_NAME_BUBBLE+ "='"+data[0]+ "' WHERE "+COL_ID_BUBBLE+"='"+id+"'");
        }
        if(!data[1].isEmpty()){
            db.execSQL("UPDATE "+TABLE_BUBBLE+" SET "+COL_DESCRIPTION_BUBBLE+ "='"+data[1]+ "' WHERE "+COL_ID_BUBBLE+"='"+id+"'");
        }
        if(!data[2].isEmpty()){
            db.execSQL("UPDATE "+TABLE_BUBBLE+" SET "+COL_AVATAR_MD5_BUBBLE+ "='"+data[2]+ "' WHERE "+COL_ID_BUBBLE+"='"+id+"'");
        }

    }

    public void deleteMyBubble(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_BUBBLE + " WHERE "+COL_ID_BUBBLE+" = '"+id+"' ");
    }

   public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USERS);
        db.execSQL("DELETE FROM " + TABLE_BUBBLE);
        db.execSQL("DELETE FROM " + TABLE_BUBBLE);
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
