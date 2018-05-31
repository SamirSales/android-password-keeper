package io.github.samirsamir.passwordkeeper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import io.github.samirsamir.passwordkeeper.entity.Registration;
import io.github.samirsamir.passwordkeeper.entity.RegistrationType;

public class RegistrationDB extends SQLiteOpenHelper {

    private static String TABLE = "users";

    private static final int VERSION = 1;

    private final String COLUMN_ID = "id";
    private final String COLUMN_LOGIN = "login";
    private final String COLUMN_PASSWORD = "password";
    private final String COLUMN_USER_TYPE = "user_type";

    private SQLiteDatabase db;

    public RegistrationDB(Context context) {
        super(context, TABLE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE if not exists "+ TABLE +"(" +
                COLUMN_ID+" INTEGER PRIMARY KEY, " +
                COLUMN_LOGIN+" TEXT, " +
                COLUMN_PASSWORD+" TEXT, " +
                COLUMN_USER_TYPE+" TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    private ContentValues getContentValues(Registration registration){
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGIN, registration.getLogin());
        values.put(COLUMN_PASSWORD, registration.getPassword());
        values.put(COLUMN_USER_TYPE, registration.getRegistrationType().getType());

        return values;
    }

    public void add(Registration registration){
        ContentValues values = getContentValues(registration);

        open();
        int id = (int)getWritableDatabase().insert(TABLE, null, values);
        close();
        registration.setId(id);
    }

    public void update(Registration registration){
        ContentValues values = getContentValues(registration);

        open();
        db.update(TABLE, values, COLUMN_ID+"=" + registration.getId(), null);
        close();
    }

    public Registration getAppAccessUser(){
        Cursor c = getWritableDatabase().rawQuery("SELECT * FROM "+ TABLE
                +" WHERE "+COLUMN_USER_TYPE+" = '"+ RegistrationType.APP_ACCESS.getType()+"';",null);

        Registration appAccessRegistration = null;

        if(c.moveToNext()){
            Registration registration = new Registration();
            registration.setId(c.getLong(0));
            registration.setLogin(c.getString(1));
            registration.setPassword(c.getString(2));
            registration.setRegistrationType(RegistrationType.getUserType(c.getString(3)));
            appAccessRegistration = registration;
        }

        c.close();
        return appAccessRegistration;
    }

    public ArrayList<Registration> getDefaultUsers(){
        ArrayList<Registration> registrations = new ArrayList<>();
        Cursor c = getWritableDatabase().rawQuery("SELECT * FROM "+ TABLE
                +" WHERE "+COLUMN_USER_TYPE +" = '"+ RegistrationType.DEFAULT.getType()+"'"
                +" ORDER BY LOWER("+ COLUMN_LOGIN + ") ASC;",null);

        while(c.moveToNext()){
            Registration registration = new Registration();
            registration.setId(c.getLong(0));
            registration.setLogin(c.getString(1));
            registration.setPassword(c.getString(2));
            registration.setRegistrationType(RegistrationType.getUserType(c.getString(3)));
            registrations.add(registration);
        }

        c.close();
        return registrations;
    }

    public ArrayList<Registration> getAll(){
        ArrayList<Registration> registrations = new ArrayList<>();
        Cursor c = getWritableDatabase().rawQuery("SELECT * FROM "+ TABLE +";",null);
        while(c.moveToNext()){
            Registration registration = new Registration();
            registration.setId(c.getLong(0));
            registration.setLogin(c.getString(1));
            registration.setPassword(c.getString(2));
            registration.setRegistrationType(RegistrationType.getUserType(c.getString(3)));
            registrations.add(registration);
        }

        c.close();
        return registrations;
    }

    public RegistrationDB open() throws SQLException {
        db = this.getWritableDatabase();
        return this;
    }

    public void close(){
        db.close();
    }

    public void dropTable(){
        open();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
        close();
    }

    public boolean deleteRecord(long rowId){
        open();
        boolean b = db.delete(TABLE, "id="+rowId, null)>0;
        close();
        return 	b;
    }
}
