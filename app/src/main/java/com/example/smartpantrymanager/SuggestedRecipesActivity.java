package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Shows recipes the user can make RIGHT NOW (strict matching), plus a
// separate "Almost There" section for recipes missing exactly 1 ingredient.
public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecipeMatcher recipeMatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        dbHelper = new DatabaseHelper(this);
        recipeMatcher = new RecipeMatcher(dbHelper);

        RecyclerView recyclerView = findViewById(R.id.recipesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // NEW: second RecyclerView for the "Almost There" bonus section
        RecyclerView almostThereRecyclerView = findViewById(R.id.almostThereRecyclerView);
        almostThereRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextView emptyStateText = findViewById(R.id.emptyStateText);
        // NEW: header text view shown above the Almost There list
        TextView almostThereHeader = findViewById(R.id.almostThereHeader);

        // Run the strict-matching algorithm
        List<Recipe> matches = recipeMatcher.getSuggestedRecipes();

        if (matches.isEmpty()) {
            // No matches - show the empty state message instead of an empty list
            emptyStateText.setVisibility(android.view.View.VISIBLE);
            recyclerView.setVisibility(android.view.View.GONE);
        } else {
            emptyStateText.setVisibility(android.view.View.GONE);
            recyclerView.setVisibility(android.view.View.VISIBLE);

            RecipeAdapter adapter = new RecipeAdapter(matches, recipe -> {
                // Tapping a recipe opens its detail screen
                Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
                intent.putExtra("recipeId", recipe.id);
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
        }

        // NEW: Run the "Almost There" logic - recipes missing exactly 1 ingredient.
        // These are kept in their own separate list, never mixed into the main matches.
        List<Recipe> almostThere = recipeMatcher.getAlmostThereRecipes();

        if (almostThere.isEmpty()) {
            // No almost-there recipes - hide the whole section
            almostThereHeader.setVisibility(android.view.View.GONE);
            almostThereRecyclerView.setVisibility(android.view.View.GONE);
        } else {
            almostThereHeader.setVisibility(android.view.View.VISIBLE);
            almostThereRecyclerView.setVisibility(android.view.View.VISIBLE);

            RecipeAdapter almostThereAdapter = new RecipeAdapter(almostThere, recipe -> {
                // Tapping an almost-there recipe also opens its detail screen,
                // so the user can see exactly what they're missing
                Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
                intent.putExtra("recipeId", recipe.id);
                startActivity(intent);
            });
            almostThereRecyclerView.setAdapter(almostThereAdapter);
        }

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_recipes);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                startActivity(new Intent(SuggestedRecipesActivity.this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_recipes) {
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(SuggestedRecipesActivity.this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }
}
