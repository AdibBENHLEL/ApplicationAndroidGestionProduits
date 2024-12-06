package com.example.tp6_bdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
cette classe gère la base de données:
connexion, requetes de Màj et de consultation, et la fermeture
L'instance de la base qui sera manipulée au travers de cette classe
 */



public class MyDatabaseManager {

    private SQLiteDatabase database;
    private DataBaseHelper databaseHelper;





    //constructeur
    public MyDatabaseManager(Context context) {
        databaseHelper = new DataBaseHelper(context); //instance de class DataBaseHelper pour gere la connectivité
    }





    // Ouvrir une connexion avec la base de données en mode lecture/écriture
    public void open() {
        database = databaseHelper.getWritableDatabase();//getWritableDatabase de la class SQLiteOpenHelper que databaseHelper l'hérite
    }




    // Fermer la connexion avec la base de données
    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }





    /**
     Insérer un nouveau produit dans la base de donnée
     @param product Le produit à insérer
     @return ID du produit inséré
     */
    public long insert(Product product) {
        ContentValues values = new ContentValues();// utilisée pour stocker une série de paires clé-valeur qui représentent des données que vous souhaitez insérer
        values.put(DataBaseHelper.COLUMN_NAME, product.getName());//Clé = "name", Valeur = "cake"
        values.put(DataBaseHelper.COLUMN_QUANTITY, product.getQuantity());//Clé ="Quantity",Valeur="80"
        return database.insert(DataBaseHelper.TABLE_PRODUCTS, null, values);// database instance de SQLiteDatabase
    }




    /**
     Mettre à jour un produit existant dans la base de donnée
     @param product Le produit à mettre à jour
     @return Nombre de lignes affectées
     */
    public long update(int id,String name,int qun) {
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.COLUMN_NAME, name);//values.put("name", "Laptop");
        values.put(DataBaseHelper.COLUMN_QUANTITY,qun);//values.put("quantity", 50);
        /*
        long update(String table, ContentValues values, String whereClause, String[] whereArgs)
        exemple
        database.update(
            "products",                // Table name
                values,                    // New values to update
                "id = ?",                  // Where clause
                new String[]{String.valueOf(5)} // Where args (id = 5)
         );

        */
        return database.update(DataBaseHelper.TABLE_PRODUCTS, values,
                DataBaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }




    /**
     Supprimer un produit de la base de données
     @param id L'ID du produit à supprimer
     @return Nombre de lignes affectées
     */
    public long delete(int id) {
        return database.delete(DataBaseHelper.TABLE_PRODUCTS,
                DataBaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }




    /**
     Récupérer un produit par son nom
     @param name Le nom du produit
     @return Un curseur contenant le produit correspondant
     */
    public Cursor getProduct(String name) {
        return database.rawQuery(
                "SELECT id as _id, name, quantity FROM " + DataBaseHelper.TABLE_PRODUCTS +
                        " WHERE " + DataBaseHelper.COLUMN_NAME + " = ?",
                new String[]{name}
        );
    }




    /**
     Récupérer tous les produits de la base de donnée
     @return Un curseur contenant tous les produits
     */
    public Cursor getProducts() {
        // Alias the 'id' column as '_id' in the SQL query
        return database.rawQuery("SELECT id as _id, name, quantity FROM " + databaseHelper.TABLE_PRODUCTS, null);
    }



}
