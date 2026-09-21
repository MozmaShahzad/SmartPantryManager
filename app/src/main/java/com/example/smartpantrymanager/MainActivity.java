package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

//home screen of the app - shows full pantry list
public class MainActivity extends AppCompatActivity{

    private DatabaseHelper dbHelper;
    private PantryAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        //set the recyclerview to display items in a simple
        //vertical list
        recyclerView = findViewById(R.id.pantryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //create the adapter, passing an empty list for now and
        //telling it what to do when a row or delete button is tapped
        adapter = new PantryAdapter(new ArrayList<>(), new PantryAdapter.OnItemActionListener() {
            @Override
            public void onItemClick(PantryItem item) {
                Intent intent = new Intent(MainActivity.this,AddEditIngredientActivity.class);
                intent.putExtra("id",item.id);
                startActivity(intent);
            }
            @Override
            public void onDeleteClick(PantryItem item) {
                dbHelper.deletePantryItem(item.id);
                Toast.makeText(MainActivity.this,item.name + "removed", Toast.LENGTH_SHORT).show();
                loadPantryItems();
            }
        });

        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_pantry);//highlight Pantry
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.nav_pantry) {
                return true;
            } else if (id == R.id.nav_recipes) {
                startActivity(new Intent(MainActivity.this, SuggestedRecipesActivity.class));
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }
    //onResume runs everytime this screen becomes visible
    //to make sure the list is always up-to date
    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    //reads all items from the database and updates the RecyclerView
    private void loadPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        Cursor cursor = dbHelper.getAllPantryItems();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow("quantity"));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow("unit"));
                String expiryDate = cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"));
                items.add(new PantryItem(id, name, quantity, unit, expiryDate));
            } while (cursor.moveToNext());
        }
        cursor.close(); // always close cursors when done, to free memory

        adapter.updateData(items); // push the fresh data into the RecyclerView

    }
}