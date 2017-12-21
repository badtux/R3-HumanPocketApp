package com.rype3.pocket_hrm.Sqldb;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "LocationManager";

    // Contacts table name
    private static final String TABLE_CONTACTS = "location";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_STATE = "state";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_META = "meta";
    private static final String KEY_TYPE = "type";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + TABLE_CONTACTS
                + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT,"
                + KEY_STATE + " TEXT,"
                + KEY_DEVICE_ID + " TEXT,"
                + KEY_META + " TEXT"
                + KEY_TYPE + " TEXT"
                + ")";
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public void addContact(LocationDetails locationDetails) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOCATION, locationDetails.getLocation_name()); // location
        values.put(KEY_LATITUDE, locationDetails.getLat()); // latitude
        values.put(KEY_LONGITUDE, locationDetails.getLon()); // longitude
        values.put(KEY_STATE, locationDetails.getCheck_Status()); // state
        values.put(KEY_DEVICE_ID, locationDetails.getDevice_Id()); // device_id
        values.put(KEY_META, locationDetails.getMeta()); // meta
        values.put(KEY_TYPE, locationDetails.getType()); // type

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection

    }

    // Getting single contact
    public LocationDetails getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                        KEY_LOCATION, KEY_LATITUDE, KEY_LONGITUDE,KEY_STATE,KEY_DEVICE_ID,KEY_TYPE}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        LocationDetails locationDetails = new LocationDetails(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7));
        // return contact
        return locationDetails;

    }

    // Getting All Contacts
    public List<LocationDetails> getAllContacts() {
        List<LocationDetails> contactList = new ArrayList<LocationDetails>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LocationDetails locationDetails = new LocationDetails();
                locationDetails.set_id(Integer.parseInt(cursor.getString(0)));
                locationDetails.setLocation_name(cursor.getString(1));
                locationDetails.setLat(cursor.getString(3));
                locationDetails.setLon(cursor.getString(4));
                locationDetails.setCheck_Status(cursor.getString(5));
                locationDetails.setDevice_Id(cursor.getString(6));
                locationDetails.setMeta(cursor.getString(7));
                locationDetails.setType(cursor.getString(8));
                // Adding contact to list
                contactList.add(locationDetails);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;

    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
    // Updating single contact
    public int updateContact(LocationDetails locationDetails) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOCATION, locationDetails.getLocation_name()); // location
        values.put(KEY_LATITUDE, locationDetails.getLat()); // latitude
        values.put(KEY_LONGITUDE, locationDetails.getLon()); // longitude
        values.put(KEY_STATE, locationDetails.getCheck_Status()); // state
        values.put(KEY_DEVICE_ID, locationDetails.getDevice_Id()); // device_id
        values.put(KEY_META, locationDetails.getMeta()); // meta
        values.put(KEY_TYPE, locationDetails.getType()); // type

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(locationDetails.get_id()) });
    }

    // Deleting single contact
    public void deleteContact(LocationDetails locationDetails) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(locationDetails.get_id()) });
        db.close();
    }
}
