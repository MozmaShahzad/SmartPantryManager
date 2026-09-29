# Smart Pantry Manager 

A Java Android application that helps reduce food waste by tracking
pantry ingredients and suggesting recipes based strictly on what the user already
has at home - there is no shopping trip required.

## Description
Smart Pantry Manager lets users manage their pantry (add,edit,delete
ingredients) and see, quickly, which recipes they can make right now.
The app uses a strict-matching rule: a recipe is only suggested if every
single required ingredient is available in sufficient quantity. Recipes
missing exactly one ingredient are shown separately under an "Almost There"
section.

## Features

- Full pantry management (Create, Read, Update, Delete)
- 20 pre-seeded recipes with detailed step-by-step instructions
- Strict ingredient-matching algorithm, robust to plural/case differences
- "Almost There" bonus section for recipes missing one ingredient
- Settings screen with a persistent expiring-soon alerts toggle
- Bottom navigation across Pantry, Recipes and Settings screens

## Database

This app uses **SQLite**, implemented locally on-device via
`SQLiteOpenHelper`, with three tables: `pantry_items`, `recipes`, and
`recipe_ingredients`.

SQLite was chosen because the app's data (a personal pantry list and a
fixed set of recipes) is entirely local to the user's device, requires no
syncing between devices and does not need an internet connection to
function. This kept the setup simple and reliable without needing to
configure and maintain a separate backend or cloud service.

## Setup/ Run Instructions 

1. Clone this repository:
   git clone https://github.com/MozmaShahzad/SmartPantryManager.git
2. Open the project folder in Android Studio.
3. Allow Gradle sync complete.
4. Create or select an emulator - Android 8.0 / API 26 or higher is recommended.
5. Click RUN to build and launch the app.

No additional setup, API keys, or backend configuration is required - the
database is created and seeded automatically the first time the app runs.

## Author
Moazzama Shahzad
Mobile App Development 700 - Richfield Institute of Technology.
