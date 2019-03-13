package com.example.checkinnow.sqlite_creation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.checkinnow.Employee;

import java.util.ArrayList;
import java.util.List;

import static com.example.checkinnow.sqlite_creation.FS_SQLite_Contract.*;


public class FS_to_SQLite_DBHelper extends SQLiteOpenHelper {



    private static FS_to_SQLite_DBHelper instance;

    private SQLiteDatabase db;

    public static final String DATABASE_NAME = "FS_SQLiteDB";

    public static final int DATABASE_VERSION=1;

    //this is liss from firestore we created
    private static ArrayList<Employee> liss;


    public FS_to_SQLite_DBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    public static synchronized FS_to_SQLite_DBHelper getInstance(Context context, ArrayList<Employee> liss){

        if(instance==null){
            instance = new FS_to_SQLite_DBHelper(context);
        }
        FS_to_SQLite_DBHelper.liss=liss;
        return instance;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        this.db =db;

        Log.i("checkk flow SQ","oncreate");

        final String SQL_CREATE_FSTOSQLITE_TABLE = "CREATE TABLE "+
                FSToSQLite.TABLE_NAME +" ( "+
                FSToSQLite._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                FSToSQLite.COLUMN_NAME + " TEXT, "+
                FSToSQLite.COLUMN_PHONE + " TEXT, "+
                FSToSQLite.COLUMN_IMAGE_URL + " TEXT, "+
                FSToSQLite.COLUMN_SCORE_CARD_REF + " TEXT "+
                " ) ";

        db.execSQL(SQL_CREATE_FSTOSQLITE_TABLE);

        Log.i("checkk flow SQ","h :");
        addToTableFuntion(liss);

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ FSToSQLite.TABLE_NAME);
        onCreate(db);
    }

    private void addToTableFuntion(ArrayList<Employee> liss) {

//        int h =0;
//        Log.i("checkk flow SQ","h :"+h+" : sqlite count");
//        Log.i("checkk flow SQ","liss :"+liss.size()+" : liss size");

        for(int i=0; i<liss.size();i++) {
       //     h++;
        //    Log.i("checkk flow SQ","h :"+h+" : sqlite count");

            Employee employee = liss.get(i);

            ContentValues cv = new ContentValues();

            cv.put(FSToSQLite.COLUMN_NAME, employee.getName());
            cv.put(FSToSQLite.COLUMN_PHONE, employee.getPhone());
            cv.put(FSToSQLite.COLUMN_IMAGE_URL, employee.getImageurl());
            cv.put(FSToSQLite.COLUMN_SCORE_CARD_REF, employee.getRef_score_card());

            db.insert(FSToSQLite.TABLE_NAME, null, cv);
        }
//        Log.i("checkk flow SQ","h :"+h+" : sqlite count");
//
//        Log.i("checkk flow SQ","liss :"+liss.size()+" : liss size");

    }

    public ArrayList<Employee> getAll_FSToSQLite(){

        Log.i("checkk flow SQ","sqlite called");
        ArrayList<Employee> returnList =new ArrayList<>();

        db = getReadableDatabase();

        Cursor c =db.rawQuery("SELECT *FROM "+ FSToSQLite.TABLE_NAME, null);

        if(c.moveToFirst()){

            do{

                Employee employee = new Employee();

                employee.setName(c.getString(c.getColumnIndex(FSToSQLite.COLUMN_NAME)));
                employee.setPhone(c.getString(c.getColumnIndex(FSToSQLite.COLUMN_PHONE)));
                employee.setImageurl(c.getString(c.getColumnIndex(FSToSQLite.COLUMN_IMAGE_URL)));
                employee.setRef_score_card(c.getString(c.getColumnIndex(FSToSQLite.COLUMN_SCORE_CARD_REF)));


                returnList.add(employee);

            }while (c.moveToNext());


        }

        c.close();

        return returnList;
    }
}
