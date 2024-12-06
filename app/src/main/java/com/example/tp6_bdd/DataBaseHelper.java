package com.example.tp6_bdd;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;


/*
cette classe gere la connectivité avec la base de données de type SQLITE
avec SQLiteOpenHelper on peut

getWritableDatabase : open DB
isOpen
close
insert
update
delete
rawQuery

*/
public class DataBaseHelper extends SQLiteOpenHelper {

    // Nom de la base de données
    private static final String DATABASE_NAME = "products.db";

    // Version de la base de données
    private static final int DATABASE_VERSION = 1;

    // Nom de la table et colonnes
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";


    // Commande SQL pour créer la table
    public static final String CREATE_PLANET_TABLE = "CREATE TABLE " +
            DataBaseHelper.TABLE_PRODUCTS + " (" +
            DataBaseHelper.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DataBaseHelper.COLUMN_NAME + " TEXT, " +
            DataBaseHelper.COLUMN_QUANTITY + " INTEGER)";

    // Constructeur
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    // Création de la base de données
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PLANET_TABLE);
    }

    // Mise à jour de la base de données
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }
}


