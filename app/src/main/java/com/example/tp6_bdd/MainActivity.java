package com.example.tp6_bdd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends Activity {

    private MyDatabaseManager myDatabaseManager;


    private Context context;
    DataBaseHelper databaseHelper = new DataBaseHelper(this); //instance de class DataBaseHelper pour gere la connectivité


    private EditText inputName, inputQuantity;
    private ListView listViewProducts;

    private LinearLayout listLayout, emptyLayout;

    private Button btnAddProduct;
    private Button btnDeleteProduct;
    private Button btn_upDate_product;
    private Button btnLookupProduct;


    int currentID = -1;//selected row id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //on reference les view dans la vue XML:
        inputName = findViewById(R.id.input_name);
        inputQuantity = findViewById(R.id.input_quantity);

        listViewProducts = findViewById(R.id.list_view_products);
        listViewProducts.setVisibility(View.VISIBLE);

        listLayout = findViewById(R.id.list);
        listLayout.setVisibility(View.VISIBLE);
        emptyLayout = findViewById(R.id.emptyLayout);
        emptyLayout.setVisibility(View.INVISIBLE);


        btnAddProduct = findViewById(R.id.btn_new_product);
        btnDeleteProduct = findViewById(R.id.btn_remove_product);
        btn_upDate_product = findViewById(R.id.btn_upDate_product);
        btnLookupProduct = findViewById(R.id.btn_lookup_product);


        //on recupère les données( les infos de toutes les produits auprès de la base:
        myDatabaseManager = new MyDatabaseManager(this);
        //ouvrir la base de données en mode ecriture/lecture:
        myDatabaseManager.open();
        //charger la liste des planetes:
        rechargerListePlanetes();


        //on programme l'even click sur un item de la liste:
        listViewProducts.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Cast the clicked item to a Cursor
                        Cursor Curent_cursor = (Cursor) parent.getItemAtPosition(position);

                        if (Curent_cursor != null) {
                            // Access data using column indices or names
                            currentID = Curent_cursor.getInt(Curent_cursor.getColumnIndexOrThrow("_id")); // `_id` is now the alias for `id`
                            inputName.setText(Curent_cursor.getString(Curent_cursor.getColumnIndexOrThrow(databaseHelper.COLUMN_NAME)));
                            inputQuantity.setText(Curent_cursor.getInt(Curent_cursor.getColumnIndexOrThrow(databaseHelper.COLUMN_QUANTITY)) + "");

                        }
                    }
                }
        );



        btnAddProduct.setOnClickListener(v -> {

            String currentNom = inputName.getText().toString();
            if (currentNom == null || currentNom.trim().equals("")) {
                Toast.makeText(this, "Please input a name for the product!", Toast.LENGTH_SHORT).show();
                return;
            }

            String curentQuanS = inputQuantity.getText().toString().trim();
            if (curentQuanS.isEmpty()) {
                Toast.makeText(this, "Please input a number for Quantité!", Toast.LENGTH_SHORT).show();
                return;
            }

            int currentQuantite;
            currentQuantite = Integer.parseInt(curentQuanS);
            if (currentQuantite == 0) {
                    Toast.makeText(this, "Quantité cannot be zero!", Toast.LENGTH_SHORT).show();
                    return;
                }


            Product p = new Product(currentNom, currentQuantite);
            if (myDatabaseManager.insert(p) > 0) {
                Toast.makeText(this, "Produit ajouté avec succès!", Toast.LENGTH_SHORT).show();
                rechargerListePlanetes(); // Update the ListView
            } else {
                Toast.makeText(this, "Échec : Produit non ajouté!", Toast.LENGTH_SHORT).show();
            }

            // Clear input fields
            inputName.setText("");
            inputQuantity.setText("");
            currentID = -1; // No row selected
        });



        btnDeleteProduct.setOnClickListener(v -> {
            // Check if a product is selected

            if (currentID == -1) {
                Toast.makeText(this, "Please select a product to delete!", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                // Delete the product from the database
                int rowsDeleted = (int) myDatabaseManager.delete(currentID);
                Log.d("DeleteProduct", "Rows deleted: " + rowsDeleted);

                if (rowsDeleted > 0) {
                    Toast.makeText(this, "Product deleted successfully!", Toast.LENGTH_SHORT).show();
                    rechargerListePlanetes(); // Refresh the ListView
                } else {
                    Toast.makeText(this, "Failed to delete the product!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("DeleteProduct", "Error deleting product", e);
                Toast.makeText(this, "An error occurred while deleting!", Toast.LENGTH_SHORT).show();
            }

            // Reset the views and fields
            inputName.setText("");
            inputQuantity.setText("");
            currentID = -1; // Reset current selection
            listViewProducts.setSelected(false);
        });









        btn_upDate_product.setOnClickListener(v -> {

           if(currentID == -1) {
               Toast.makeText(this,
                "please select at least one Product!!",
                Toast.LENGTH_SHORT).show();       return;
           }
            String currentNom = inputName.getText().toString();
            int currentQuntite = Integer.parseInt(inputQuantity.getText().
                    toString());

            if(myDatabaseManager.update(currentID,currentNom,currentQuntite)>0)
            {  Toast.makeText(this, "Product modifiée",
                    Toast.LENGTH_SHORT).show();
                rechargerListePlanetes(); }
            else
                Toast.makeText(this, "Product non modifiée!", Toast.LENGTH_SHORT).show();
            //reset views:
            inputName.setText("");
            inputQuantity.setText("");
            currentID = -1;
        });





        btnLookupProduct.setOnClickListener(v -> {
            // Get the name entered in the input field
            String currentNom = inputName.getText().toString().trim();

            // Validate the input
            if (currentNom.isEmpty()) {
                Toast.makeText(this, "Please input a name for the product!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call the database method to get the product
            Cursor cursor = myDatabaseManager.getProduct(currentNom);

            // Check if the product exists
            if (cursor != null && cursor.moveToFirst()) {
                // Extract product details
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                String productName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                int productQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

                // Show the product details in a Toast
                Toast.makeText(
                        this,
                        "Product Found:\nID: " + productId +
                                "\nName: " + productName +
                                "\nQuantity: " + productQuantity,
                        Toast.LENGTH_LONG
                ).show();

                // Find and select the product in the ListView
                for (int i = 0; i < listViewProducts.getCount(); i++) {
                    Cursor listCursor = (Cursor) listViewProducts.getItemAtPosition(i);
                    int listItemId = listCursor.getInt(listCursor.getColumnIndexOrThrow("_id"));

                    if (listItemId == productId) {
                        listViewProducts.setSelection(i); // Scroll to and highlight the item
                        break;
                    }
                }
            } else {
                // Product not found
                Toast.makeText(this, "Product not found!", Toast.LENGTH_SHORT).show();
            }

            // Close the cursor to release resources
            if (cursor != null) {
                cursor.close();
            }
        });








    }

    public void rechargerListePlanetes() {
        // Retrieve the data from the database:
        Cursor cProduit = myDatabaseManager.getProducts();

        // Check if there are no products to display:
        if (cProduit.getCount() == 0) {
            // Hide the layout for the list:
            listLayout.setVisibility(LinearLayout.INVISIBLE);
            // Show the emptyLayout instead:
            emptyLayout.setVisibility(LinearLayout.VISIBLE);
            return;
        } else {
            // Show the layout for the list:
            listLayout.setVisibility(LinearLayout.VISIBLE);
            // Hide the emptyLayout:
            emptyLayout.setVisibility(LinearLayout.INVISIBLE);
        }

        // Create the CursorAdapter to load the data into the list:
        SimpleCursorAdapter MyCursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.model_item_in_list,
                cProduit,
                new String[]{"_id",  // '_id' now references the aliased column
                        databaseHelper.COLUMN_NAME,
                        databaseHelper.COLUMN_QUANTITY},
                new int[]{R.id.id_lab, R.id.nom_lab, R.id.quantite_lab},
                0
        );

        // Load the data from the adapter into the ListView:
        listViewProducts.setAdapter(MyCursorAdapter);
    }
}
