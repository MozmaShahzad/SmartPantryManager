package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

// Screen used for BOTH adding a new pantry item AND editing an existing one.
// If an "id" was passed in via Intent, we're editing. Otherwise, we're adding.
public class AddEditIngredientActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    // -1 means "no id passed in" = we're adding a new item, not editing
    private int itemId = -1;

    // References to the input fields on screen
    private TextInputEditText nameInput, quantityInput, unitInput, expiryInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dbHelper = new DatabaseHelper(this);

        // Connect Java variables to the XML input fields
        nameInput = findViewById(R.id.nameInput);
        quantityInput = findViewById(R.id.quantityInput);
        unitInput = findViewById(R.id.unitInput);
        expiryInput = findViewById(R.id.expiryInput);

        TextView screenTitle = findViewById(R.id.screenTitle);

        // Check if an id was passed in (meaning MainActivity wants us to EDIT an item)
        if (getIntent().hasExtra("id")) {
            itemId = getIntent().getIntExtra("id", -1);
            screenTitle.setText("Edit Ingredient");
            loadExistingItem(); // pre-fill the form with this item's current data
        } else {
            screenTitle.setText("Add Ingredient");
        }

        // Save button - validates input, then adds or updates depending on mode
        findViewById(R.id.saveButton).setOnClickListener(v -> saveItem());
    }

    // Loads the existing item's data into the form fields (used only in edit mode)
    private void loadExistingItem() {
        Cursor cursor = dbHelper.getPantryItemById(itemId);
        if (cursor.moveToFirst()) {
            nameInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            quantityInput.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("quantity"))));
            unitInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("unit")));
            expiryInput.setText(cursor.getString(cursor.getColumnIndexOrThrow("expiry_date")));
        }
        cursor.close();
    }

    // Validates the form, then either adds a new pantry item or updates the existing one
    private void saveItem() {
        String name = nameInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();
        String unit = unitInput.getText().toString().trim();
        String expiry = expiryInput.getText().toString().trim();

        //INPUT VALIDATION
        if (name.isEmpty()) {
            nameInput.setError("Ingredient name is required");
            return; // stop here, don't save
        }
        if (quantityText.isEmpty()) {
            quantityInput.setError("Quantity is required");
            return;
        }
        if (unit.isEmpty()) {
            unitInput.setError("Unit is required");
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(quantityText);
            if (quantity <= 0) {
                quantityInput.setError("Quantity must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            quantityInput.setError("Enter a valid number");
            return;
        }

        //SAVE TO DATABASE
        if (itemId == -1) {
            // No id means this is a NEW item
            dbHelper.addPantryItem(name, quantity, unit, expiry);
            Toast.makeText(this, name + " added to pantry", Toast.LENGTH_SHORT).show();
        } else {
            // We have an id, so UPDATE the existing item instead
            dbHelper.updatePantryItem(itemId, name, quantity, unit, expiry);
            Toast.makeText(this, name + " updated", Toast.LENGTH_SHORT).show();
        }

        finish(); // close this screen, return to MainActivity (which will refresh via onResume)
    }
}