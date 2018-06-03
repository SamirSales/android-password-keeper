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
import io.github.samirsamir.passwordkeeper.util.TreatmentInjectionSQL;

public class RegistrationDB extends SQLiteOpenHelper {

    private static String TABLE = "users";

    private static final int VERSION = 1;

    private final String COLUMN_ID = "id";
    private final String COLUMN_SITE = "site";
    private final String COLUMN_LOGIN = "login";
    private final String COLUMN_PASSWORD = "password";
    private final String COLUMN_USER_TYPE = "user_type";

    private SQLiteDatabase db;

    private TreatmentInjectionSQL tiSQL;

    public RegistrationDB(Context context) {
        super(context, TABLE, null, VERSION);
        tiSQL = new TreatmentInjectionSQL();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE if not exists "+ TABLE +"(" +
                COLUMN_ID+" INTEGER PRIMARY KEY, " +
                COLUMN_SITE+" TEXT, " +
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
        values.put(COLUMN_SITE, tiSQL.filter(registration.getSite()));
        values.put(COLUMN_LOGIN, tiSQL.filter(registration.getLogin()));
        values.put(COLUMN_PASSWORD, tiSQL.filter(registration.getPassword()));
        values.put(COLUMN_USER_TYPE, tiSQL.filter(registration.getRegistrationType().getType()));

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

    private Registration getRegistrationByCursor(Cursor cursor){
        Registration registration = new Registration();
        registration.setId(cursor.getLong(0));
        registration.setSite(tiSQL.reclaim(cursor.getString(1)));
        registration.setLogin(tiSQL.reclaim(cursor.getString(2)));
        registration.setPassword(tiSQL.reclaim(cursor.getString(3)));
        registration.setRegistrationType(RegistrationType.getUserType(cursor.getString(4)));

        return registration;
    }

    public Registration getAppUserAccess(){
        Cursor c = getWritableDatabase().rawQuery("SELECT * FROM "+ TABLE
                +" WHERE "+COLUMN_USER_TYPE+" = '"+ RegistrationType.APP_ACCESS.getType()+"';",null);

        Registration appAccessRegistration = null;

        if(c.moveToNext()){
            appAccessRegistration = getRegistrationByCursor(c);
        }

        c.close();
        return appAccessRegistration;
    }

    public ArrayList<Registration> getDefaultUsers(){
        ArrayList<Registration> registrations = new ArrayList<>();
        Cursor c = getWritableDatabase().rawQuery("SELECT * FROM "+ TABLE
                +" WHERE "+COLUMN_USER_TYPE +" = '"+ RegistrationType.DEFAULT.getType()+"'"
                +" ORDER BY LOWER("+ COLUMN_SITE + ") ASC;",null);

        while(c.moveToNext()){
            registrations.add(getRegistrationByCursor(c));
        }

        c.close();
        return registrations;
    }

    public ArrayList<Registration> getUsersBySiteAndLogin(String site, String login){
        ArrayList<Registration> registrations = new ArrayList<>();
        Cursor c = getWritableDatabase().rawQuery("SELECT * FROM "+ TABLE
                +" WHERE "+COLUMN_SITE +" = '"+ tiSQL.filter(site).trim()+"'"
                +" AND "+COLUMN_LOGIN+" = '"+ tiSQL.filter(login).trim()+"'"
                +" AND "+COLUMN_USER_TYPE+" = '"+RegistrationType.DEFAULT.getType()+"'"
                +";",null);

        while(c.moveToNext()){
            registrations.add(getRegistrationByCursor(c));
        }

        c.close();
        return registrations;
    }

    public ArrayList<Registration> getAll(){
        ArrayList<Registration> registrations = new ArrayList<>();
        Cursor c = getWritableDatabase().rawQuery("SELECT * FROM "+ TABLE +";",null);

        while(c.moveToNext()){
            registrations.add(getRegistrationByCursor(c));
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
        boolean b = db.delete(TABLE, COLUMN_ID+"="+rowId, null)>0;
        close();
        return 	b;
    }
}
