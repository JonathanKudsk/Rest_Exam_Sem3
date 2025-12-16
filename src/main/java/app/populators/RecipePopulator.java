package app.populators;

import app.entities.Ingredient;
import app.entities.Recipe;
import app.entities.RecipeIngredient;
import app.enums.Category;
import app.enums.Type;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.HashMap;
import java.util.Map;

public class RecipePopulator {

    public static void populate(EntityManager em) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // Opret alle ingredienser
            Map<String, Ingredient> ingredients = createAllIngredients(em);

            // Opret 10 recipes af hver kategori
            createBreakfastRecipes(ingredients, em);
            createLunchRecipes(ingredients, em);
            createDinnerRecipes(ingredients, em);
            createDessertRecipes(ingredients, em);
            createSnackRecipes(ingredients, em);

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error populating recipes", e);
        }
    }

    private static Map<String, Ingredient> createAllIngredients(EntityManager em) {
        Map<String, Ingredient> ingredients = new HashMap<>();
        
        // Proteins
        ingredients.put("eggs", createIngredient("Egg", Type.PROTEIN, "Chicken eggs", "egg", em));
        ingredients.put("chicken", createIngredient("Chicken", Type.PROTEIN, "Chicken breast", "chicken", em));
        ingredients.put("bacon", createIngredient("Bacon", Type.PROTEIN, "Smoked bacon", "bacon", em));
        ingredients.put("salmon", createIngredient("Salmon", Type.PROTEIN, "Fresh salmon fillet", "salmon", em));
        ingredients.put("beef", createIngredient("Beef", Type.PROTEIN, "Ground beef", "beef", em));
        ingredients.put("tofu", createIngredient("Tofu", Type.PROTEIN, "Firm tofu", "tofu", em));
        
        // Dairy
        ingredients.put("butter", createIngredient("Butter", Type.DAIRY, "Dairy butter", "butter", em));
        ingredients.put("milk", createIngredient("Milk", Type.DAIRY, "Whole milk", "milk", em));
        ingredients.put("cheese", createIngredient("Cheese", Type.DAIRY, "Grated cheese", "cheese", em));
        ingredients.put("yogurt", createIngredient("Yogurt", Type.DAIRY, "Greek yogurt", "yogurt", em));
        ingredients.put("cream", createIngredient("Cream", Type.DAIRY, "Heavy cream", "cream", em));
        
        // Vegetables
        ingredients.put("garlic", createIngredient("Garlic", Type.VEGETABLE, "Garlic clove", "garlic", em));
        ingredients.put("onion", createIngredient("Onion", Type.VEGETABLE, "Yellow onion", "onion", em));
        ingredients.put("tomato", createIngredient("Tomato", Type.VEGETABLE, "Fresh tomato", "tomato", em));
        ingredients.put("spinach", createIngredient("Spinach", Type.VEGETABLE, "Fresh spinach leaves", "spinach", em));
        ingredients.put("mushroom", createIngredient("Mushroom", Type.VEGETABLE, "Button mushrooms", "mushroom", em));
        ingredients.put("bellPepper", createIngredient("Bell Pepper", Type.VEGETABLE, "Red bell pepper", "bell-pepper", em));
        ingredients.put("carrot", createIngredient("Carrot", Type.VEGETABLE, "Fresh carrot", "carrot", em));
        ingredients.put("broccoli", createIngredient("Broccoli", Type.VEGETABLE, "Fresh broccoli", "broccoli", em));
        ingredients.put("lettuce", createIngredient("Lettuce", Type.VEGETABLE, "Fresh lettuce leaves", "lettuce", em));
        
        // Grains
        ingredients.put("rice", createIngredient("Rice", Type.GRAIN, "White rice", "rice", em));
        ingredients.put("pasta", createIngredient("Pasta", Type.GRAIN, "Pasta noodles", "pasta", em));
        ingredients.put("bread", createIngredient("Bread", Type.GRAIN, "Sliced bread", "bread", em));
        ingredients.put("oats", createIngredient("Oats", Type.GRAIN, "Rolled oats", "oats", em));
        ingredients.put("flour", createIngredient("Flour", Type.BAKING, "All-purpose flour", "flour", em));
        
        // Fruits
        ingredients.put("lemon", createIngredient("Lemon", Type.FRUIT, "Fresh lemon", "lemon", em));
        ingredients.put("banana", createIngredient("Banana", Type.FRUIT, "Ripe banana", "banana", em));
        ingredients.put("apple", createIngredient("Apple", Type.FRUIT, "Fresh apple", "apple", em));
        ingredients.put("strawberry", createIngredient("Strawberry", Type.FRUIT, "Fresh strawberries", "strawberry", em));
        ingredients.put("blueberry", createIngredient("Blueberry", Type.FRUIT, "Fresh blueberries", "blueberry", em));
        
        // Seasonings & Herbs
        ingredients.put("salt", createIngredient("Salt", Type.SEASONING, "Table salt", "salt", em));
        ingredients.put("blackPepper", createIngredient("Black Pepper", Type.SEASONING, "Freshly ground black pepper", "black-pepper", em));
        ingredients.put("basil", createIngredient("Basil", Type.HERB, "Fresh basil leaves", "basil", em));
        ingredients.put("oregano", createIngredient("Oregano", Type.HERB, "Dried oregano", "oregano", em));
        ingredients.put("parsley", createIngredient("Parsley", Type.HERB, "Fresh parsley", "parsley", em));
        ingredients.put("cumin", createIngredient("Cumin", Type.SEASONING, "Ground cumin", "cumin", em));
        ingredients.put("paprika", createIngredient("Paprika", Type.SEASONING, "Sweet paprika", "paprika", em));
        ingredients.put("vanilla", createIngredient("Vanilla", Type.SEASONING, "Vanilla extract", "vanilla", em));
        
        // Oils & Fats
        ingredients.put("oliveOil", createIngredient("Olive Oil", Type.OIL, "Extra virgin olive oil", "olive-oil", em));
        ingredients.put("vegetableOil", createIngredient("Vegetable Oil", Type.OIL, "Vegetable oil", "vegetable-oil", em));
        
        // Sweeteners
        ingredients.put("sugar", createIngredient("Sugar", Type.SWEETENER, "White sugar", "sugar", em));
        ingredients.put("honey", createIngredient("Honey", Type.SWEETENER, "Natural honey", "honey", em));
        ingredients.put("chocolate", createIngredient("Chocolate", Type.SWEETENER, "Dark chocolate", "chocolate", em));
        
        // Condiments
        ingredients.put("soySauce", createIngredient("Soy Sauce", Type.CONDIMENT, "Soy sauce", "soy-sauce", em));
        ingredients.put("vinegar", createIngredient("Vinegar", Type.CONDIMENT, "White vinegar", "vinegar", em));
        
        return ingredients;
    }

    private static void createBreakfastRecipes(Map<String, Ingredient> ingredients, EntityManager em) {
        // Recipe 1: Garlic Scrambled Eggs
        Recipe r1 = createRecipe("Garlic Scrambled Eggs", Category.BREAKFAST, "Delicious scrambled eggs with garlic", em);
        createRecipeIngredient(r1, ingredients.get("eggs"), 2, "eggs", "beaten", em);
        createRecipeIngredient(r1, ingredients.get("butter"), 10, "g", "melted in the pan", em);
        createRecipeIngredient(r1, ingredients.get("garlic"), 1, "clove", "finely chopped", em);
        createRecipeIngredient(r1, ingredients.get("salt"), 1, "pinch", "to taste", em);

        // Recipe 2: Classic Pancakes
        Recipe r2 = createRecipe("Classic Pancakes", Category.BREAKFAST, "Fluffy homemade pancakes", em);
        createRecipeIngredient(r2, ingredients.get("flour"), 200, "g", "sifted", em);
        createRecipeIngredient(r2, ingredients.get("milk"), 250, "ml", "room temperature", em);
        createRecipeIngredient(r2, ingredients.get("eggs"), 2, "eggs", "beaten", em);
        createRecipeIngredient(r2, ingredients.get("butter"), 30, "g", "melted", em);
        createRecipeIngredient(r2, ingredients.get("sugar"), 2, "tbsp", "", em);

        // Recipe 3: Bacon and Eggs
        Recipe r3 = createRecipe("Bacon and Eggs", Category.BREAKFAST, "Traditional breakfast favorite", em);
        createRecipeIngredient(r3, ingredients.get("bacon"), 4, "strips", "crispy", em);
        createRecipeIngredient(r3, ingredients.get("eggs"), 2, "eggs", "fried", em);
        createRecipeIngredient(r3, ingredients.get("bread"), 2, "slices", "toasted", em);
        createRecipeIngredient(r3, ingredients.get("butter"), 10, "g", "for toast", em);

        // Recipe 4: Oatmeal with Berries
        Recipe r4 = createRecipe("Oatmeal with Berries", Category.BREAKFAST, "Healthy and filling breakfast", em);
        createRecipeIngredient(r4, ingredients.get("oats"), 100, "g", "rolled", em);
        createRecipeIngredient(r4, ingredients.get("milk"), 200, "ml", "or water", em);
        createRecipeIngredient(r4, ingredients.get("blueberry"), 50, "g", "fresh", em);
        createRecipeIngredient(r4, ingredients.get("strawberry"), 50, "g", "sliced", em);
        createRecipeIngredient(r4, ingredients.get("honey"), 1, "tbsp", "to taste", em);

        // Recipe 5: French Toast
        Recipe r5 = createRecipe("French Toast", Category.BREAKFAST, "Sweet and crispy breakfast treat", em);
        createRecipeIngredient(r5, ingredients.get("bread"), 4, "slices", "thick cut", em);
        createRecipeIngredient(r5, ingredients.get("eggs"), 2, "eggs", "beaten", em);
        createRecipeIngredient(r5, ingredients.get("milk"), 100, "ml", "", em);
        createRecipeIngredient(r5, ingredients.get("butter"), 20, "g", "for frying", em);
        createRecipeIngredient(r5, ingredients.get("sugar"), 1, "tbsp", "for dusting", em);

        // Recipe 6: Scrambled Eggs with Spinach
        Recipe r6 = createRecipe("Scrambled Eggs with Spinach", Category.BREAKFAST, "Nutritious and delicious", em);
        createRecipeIngredient(r6, ingredients.get("eggs"), 3, "eggs", "beaten", em);
        createRecipeIngredient(r6, ingredients.get("spinach"), 50, "g", "fresh", em);
        createRecipeIngredient(r6, ingredients.get("cheese"), 30, "g", "grated", em);
        createRecipeIngredient(r6, ingredients.get("butter"), 15, "g", "for cooking", em);
        createRecipeIngredient(r6, ingredients.get("salt"), 1, "pinch", "to taste", em);

        // Recipe 7: Yogurt Parfait
        Recipe r7 = createRecipe("Yogurt Parfait", Category.BREAKFAST, "Layered breakfast delight", em);
        createRecipeIngredient(r7, ingredients.get("yogurt"), 200, "g", "Greek", em);
        createRecipeIngredient(r7, ingredients.get("blueberry"), 50, "g", "fresh", em);
        createRecipeIngredient(r7, ingredients.get("strawberry"), 50, "g", "sliced", em);
        createRecipeIngredient(r7, ingredients.get("oats"), 30, "g", "raw", em);
        createRecipeIngredient(r7, ingredients.get("honey"), 1, "tbsp", "drizzled", em);

        // Recipe 8: Breakfast Burrito
        Recipe r8 = createRecipe("Breakfast Burrito", Category.BREAKFAST, "Filling morning wrap", em);
        createRecipeIngredient(r8, ingredients.get("eggs"), 2, "eggs", "scrambled", em);
        createRecipeIngredient(r8, ingredients.get("bacon"), 2, "strips", "crispy", em);
        createRecipeIngredient(r8, ingredients.get("cheese"), 40, "g", "shredded", em);
        createRecipeIngredient(r8, ingredients.get("onion"), 1, "piece", "diced", em);
        createRecipeIngredient(r8, ingredients.get("bellPepper"), 1, "piece", "diced", em);

        // Recipe 9: Banana Pancakes
        Recipe r9 = createRecipe("Banana Pancakes", Category.BREAKFAST, "Sweet and fluffy pancakes", em);
        createRecipeIngredient(r9, ingredients.get("banana"), 2, "pieces", "mashed", em);
        createRecipeIngredient(r9, ingredients.get("eggs"), 2, "eggs", "beaten", em);
        createRecipeIngredient(r9, ingredients.get("flour"), 100, "g", "sifted", em);
        createRecipeIngredient(r9, ingredients.get("milk"), 100, "ml", "", em);
        createRecipeIngredient(r9, ingredients.get("butter"), 20, "g", "melted", em);

        // Recipe 10: Avocado Toast
        Recipe r10 = createRecipe("Avocado Toast", Category.BREAKFAST, "Modern breakfast classic", em);
        createRecipeIngredient(r10, ingredients.get("bread"), 2, "slices", "toasted", em);
        createRecipeIngredient(r10, ingredients.get("eggs"), 2, "eggs", "poached", em);
        createRecipeIngredient(r10, ingredients.get("lemon"), 1, "piece", "juiced", em);
        createRecipeIngredient(r10, ingredients.get("salt"), 1, "pinch", "to taste", em);
        createRecipeIngredient(r10, ingredients.get("blackPepper"), 1, "pinch", "freshly ground", em);
    }

    private static void createLunchRecipes(Map<String, Ingredient> ingredients, EntityManager em) {
        // Recipe 1: Lemon Rice
        Recipe r1 = createRecipe("Lemon Rice", Category.LUNCH, "Aromatic rice with lemon flavor", em);
        createRecipeIngredient(r1, ingredients.get("rice"), 150, "g", "cooked", em);
        createRecipeIngredient(r1, ingredients.get("oliveOil"), 1, "tbsp", "heated in the pan", em);
        createRecipeIngredient(r1, ingredients.get("lemon"), 1, "½ lemon", "juice + a little zest", em);
        createRecipeIngredient(r1, ingredients.get("salt"), 1, "pinch", "to taste", em);

        // Recipe 2: Chicken Salad
        Recipe r2 = createRecipe("Chicken Salad", Category.LUNCH, "Fresh and healthy lunch option", em);
        createRecipeIngredient(r2, ingredients.get("chicken"), 150, "g", "cooked and diced", em);
        createRecipeIngredient(r2, ingredients.get("tomato"), 1, "piece", "diced", em);
        createRecipeIngredient(r2, ingredients.get("onion"), 1, "piece", "diced", em);
        createRecipeIngredient(r2, ingredients.get("oliveOil"), 2, "tbsp", "for dressing", em);
        createRecipeIngredient(r2, ingredients.get("lemon"), 1, "piece", "juiced", em);

        // Recipe 3: Pasta Salad
        Recipe r3 = createRecipe("Pasta Salad", Category.LUNCH, "Cold pasta with vegetables", em);
        createRecipeIngredient(r3, ingredients.get("pasta"), 200, "g", "cooked and cooled", em);
        createRecipeIngredient(r3, ingredients.get("tomato"), 2, "pieces", "diced", em);
        createRecipeIngredient(r3, ingredients.get("bellPepper"), 1, "piece", "diced", em);
        createRecipeIngredient(r3, ingredients.get("oliveOil"), 3, "tbsp", "for dressing", em);
        createRecipeIngredient(r3, ingredients.get("vinegar"), 1, "tbsp", "", em);

        // Recipe 4: Grilled Chicken Wrap
        Recipe r4 = createRecipe("Grilled Chicken Wrap", Category.LUNCH, "Protein-packed lunch wrap", em);
        createRecipeIngredient(r4, ingredients.get("chicken"), 150, "g", "grilled and sliced", em);
        createRecipeIngredient(r4, ingredients.get("bread"), 1, "wrap", "large tortilla", em);
        createRecipeIngredient(r4, ingredients.get("lettuce"), 50, "g", "shredded", em);
        createRecipeIngredient(r4, ingredients.get("tomato"), 1, "piece", "sliced", em);
        createRecipeIngredient(r4, ingredients.get("cheese"), 30, "g", "shredded", em);

        // Recipe 5: Vegetable Stir Fry
        Recipe r5 = createRecipe("Vegetable Stir Fry", Category.LUNCH, "Quick and healthy stir fry", em);
        createRecipeIngredient(r5, ingredients.get("broccoli"), 100, "g", "florets", em);
        createRecipeIngredient(r5, ingredients.get("bellPepper"), 1, "piece", "sliced", em);
        createRecipeIngredient(r5, ingredients.get("carrot"), 1, "piece", "julienned", em);
        createRecipeIngredient(r5, ingredients.get("soySauce"), 2, "tbsp", "", em);
        createRecipeIngredient(r5, ingredients.get("vegetableOil"), 1, "tbsp", "for frying", em);

        // Recipe 6: Tomato Pasta
        Recipe r6 = createRecipe("Tomato Pasta", Category.LUNCH, "Simple and classic pasta dish", em);
        createRecipeIngredient(r6, ingredients.get("pasta"), 200, "g", "cooked", em);
        createRecipeIngredient(r6, ingredients.get("tomato"), 3, "pieces", "diced", em);
        createRecipeIngredient(r6, ingredients.get("garlic"), 2, "cloves", "minced", em);
        createRecipeIngredient(r6, ingredients.get("oliveOil"), 2, "tbsp", "", em);
        createRecipeIngredient(r6, ingredients.get("basil"), 10, "g", "fresh", em);

        // Recipe 7: Rice Bowl with Vegetables
        Recipe r7 = createRecipe("Rice Bowl with Vegetables", Category.LUNCH, "Nutritious rice bowl", em);
        createRecipeIngredient(r7, ingredients.get("rice"), 150, "g", "cooked", em);
        createRecipeIngredient(r7, ingredients.get("broccoli"), 80, "g", "steamed", em);
        createRecipeIngredient(r7, ingredients.get("carrot"), 1, "piece", "sliced", em);
        createRecipeIngredient(r7, ingredients.get("soySauce"), 1, "tbsp", "", em);
        createRecipeIngredient(r7, ingredients.get("tofu"), 100, "g", "pan-fried", em);

        // Recipe 8: Mediterranean Salad
        Recipe r8 = createRecipe("Mediterranean Salad", Category.LUNCH, "Fresh Mediterranean flavors", em);
        createRecipeIngredient(r8, ingredients.get("tomato"), 2, "pieces", "diced", em);
        createRecipeIngredient(r8, ingredients.get("onion"), 1, "piece", "diced", em);
        createRecipeIngredient(r8, ingredients.get("oliveOil"), 3, "tbsp", "", em);
        createRecipeIngredient(r8, ingredients.get("lemon"), 1, "piece", "juiced", em);
        createRecipeIngredient(r8, ingredients.get("basil"), 10, "g", "fresh", em);

        // Recipe 9: Chicken and Rice
        Recipe r9 = createRecipe("Chicken and Rice", Category.LUNCH, "Simple and satisfying", em);
        createRecipeIngredient(r9, ingredients.get("chicken"), 150, "g", "cooked", em);
        createRecipeIngredient(r9, ingredients.get("rice"), 150, "g", "cooked", em);
        createRecipeIngredient(r9, ingredients.get("onion"), 1, "piece", "diced", em);
        createRecipeIngredient(r9, ingredients.get("garlic"), 1, "clove", "minced", em);
        createRecipeIngredient(r9, ingredients.get("oliveOil"), 1, "tbsp", "", em);

        // Recipe 10: Mushroom Risotto
        Recipe r10 = createRecipe("Mushroom Risotto", Category.LUNCH, "Creamy and flavorful risotto", em);
        createRecipeIngredient(r10, ingredients.get("rice"), 150, "g", "arborio", em);
        createRecipeIngredient(r10, ingredients.get("mushroom"), 150, "g", "sliced", em);
        createRecipeIngredient(r10, ingredients.get("onion"), 1, "piece", "diced", em);
        createRecipeIngredient(r10, ingredients.get("butter"), 30, "g", "", em);
        createRecipeIngredient(r10, ingredients.get("cheese"), 40, "g", "grated", em);
    }

    private static void createDinnerRecipes(Map<String, Ingredient> ingredients, EntityManager em) {
        // Recipe 1: Butter Basil Pasta
        Recipe r1 = createRecipe("Butter Basil Pasta", Category.DINNER, "Simple and delicious pasta with butter and basil", em);
        createRecipeIngredient(r1, ingredients.get("pasta"), 200, "g", "cooked al dente", em);
        createRecipeIngredient(r1, ingredients.get("butter"), 20, "g", "melted", em);
        createRecipeIngredient(r1, ingredients.get("basil"), 5, "g", "roughly torn", em);
        createRecipeIngredient(r1, ingredients.get("blackPepper"), 1, "pinch", "freshly ground", em);

        // Recipe 2: Grilled Salmon
        Recipe r2 = createRecipe("Grilled Salmon", Category.DINNER, "Healthy and flavorful fish dish", em);
        createRecipeIngredient(r2, ingredients.get("salmon"), 200, "g", "fillet", em);
        createRecipeIngredient(r2, ingredients.get("lemon"), 1, "piece", "juiced", em);
        createRecipeIngredient(r2, ingredients.get("oliveOil"), 2, "tbsp", "for grilling", em);
        createRecipeIngredient(r2, ingredients.get("salt"), 1, "pinch", "to taste", em);
        createRecipeIngredient(r2, ingredients.get("blackPepper"), 1, "pinch", "freshly ground", em);

        // Recipe 3: Beef Stir Fry
        Recipe r3 = createRecipe("Beef Stir Fry", Category.DINNER, "Quick and savory stir fry", em);
        createRecipeIngredient(r3, ingredients.get("beef"), 200, "g", "thinly sliced", em);
        createRecipeIngredient(r3, ingredients.get("bellPepper"), 1, "piece", "sliced", em);
        createRecipeIngredient(r3, ingredients.get("broccoli"), 100, "g", "florets", em);
        createRecipeIngredient(r3, ingredients.get("soySauce"), 3, "tbsp", "", em);
        createRecipeIngredient(r3, ingredients.get("vegetableOil"), 2, "tbsp", "for frying", em);

        // Recipe 4: Chicken Pasta
        Recipe r4 = createRecipe("Chicken Pasta", Category.DINNER, "Creamy pasta with chicken", em);
        createRecipeIngredient(r4, ingredients.get("pasta"), 200, "g", "cooked", em);
        createRecipeIngredient(r4, ingredients.get("chicken"), 150, "g", "cooked and diced", em);
        createRecipeIngredient(r4, ingredients.get("cream"), 100, "ml", "", em);
        createRecipeIngredient(r4, ingredients.get("garlic"), 2, "cloves", "minced", em);
        createRecipeIngredient(r4, ingredients.get("cheese"), 50, "g", "grated", em);

        // Recipe 5: Vegetable Curry
        Recipe r5 = createRecipe("Vegetable Curry", Category.DINNER, "Spicy and aromatic curry", em);
        createRecipeIngredient(r5, ingredients.get("tomato"), 2, "pieces", "diced", em);
        createRecipeIngredient(r5, ingredients.get("onion"), 1, "piece", "diced", em);
        createRecipeIngredient(r5, ingredients.get("bellPepper"), 1, "piece", "diced", em);
        createRecipeIngredient(r5, ingredients.get("cumin"), 1, "tsp", "ground", em);
        createRecipeIngredient(r5, ingredients.get("paprika"), 1, "tsp", "", em);

        // Recipe 6: Spaghetti with Meatballs
        Recipe r6 = createRecipe("Spaghetti with Meatballs", Category.DINNER, "Classic Italian-American dish", em);
        createRecipeIngredient(r6, ingredients.get("pasta"), 200, "g", "spaghetti", em);
        createRecipeIngredient(r6, ingredients.get("beef"), 150, "g", "for meatballs", em);
        createRecipeIngredient(r6, ingredients.get("tomato"), 3, "pieces", "for sauce", em);
        createRecipeIngredient(r6, ingredients.get("onion"), 1, "piece", "diced", em);
        createRecipeIngredient(r6, ingredients.get("garlic"), 2, "cloves", "minced", em);

        // Recipe 7: Baked Chicken
        Recipe r7 = createRecipe("Baked Chicken", Category.DINNER, "Tender and juicy baked chicken", em);
        createRecipeIngredient(r7, ingredients.get("chicken"), 200, "g", "breast", em);
        createRecipeIngredient(r7, ingredients.get("oliveOil"), 2, "tbsp", "for coating", em);
        createRecipeIngredient(r7, ingredients.get("oregano"), 1, "tsp", "dried", em);
        createRecipeIngredient(r7, ingredients.get("salt"), 1, "pinch", "to taste", em);
        createRecipeIngredient(r7, ingredients.get("blackPepper"), 1, "pinch", "freshly ground", em);

        // Recipe 8: Mushroom Pasta
        Recipe r8 = createRecipe("Mushroom Pasta", Category.DINNER, "Rich and earthy pasta dish", em);
        createRecipeIngredient(r8, ingredients.get("pasta"), 200, "g", "cooked", em);
        createRecipeIngredient(r8, ingredients.get("mushroom"), 200, "g", "sliced", em);
        createRecipeIngredient(r8, ingredients.get("garlic"), 2, "cloves", "minced", em);
        createRecipeIngredient(r8, ingredients.get("butter"), 30, "g", "", em);
        createRecipeIngredient(r8, ingredients.get("parsley"), 10, "g", "chopped", em);

        // Recipe 9: Fish and Rice
        Recipe r9 = createRecipe("Fish and Rice", Category.DINNER, "Simple and healthy dinner", em);
        createRecipeIngredient(r9, ingredients.get("salmon"), 200, "g", "fillet", em);
        createRecipeIngredient(r9, ingredients.get("rice"), 150, "g", "cooked", em);
        createRecipeIngredient(r9, ingredients.get("lemon"), 1, "piece", "juiced", em);
        createRecipeIngredient(r9, ingredients.get("oliveOil"), 1, "tbsp", "", em);
        createRecipeIngredient(r9, ingredients.get("broccoli"), 100, "g", "steamed", em);

        // Recipe 10: Stuffed Bell Peppers
        Recipe r10 = createRecipe("Stuffed Bell Peppers", Category.DINNER, "Colorful and filling dish", em);
        createRecipeIngredient(r10, ingredients.get("bellPepper"), 2, "pieces", "hollowed", em);
        createRecipeIngredient(r10, ingredients.get("rice"), 100, "g", "cooked", em);
        createRecipeIngredient(r10, ingredients.get("beef"), 150, "g", "cooked", em);
        createRecipeIngredient(r10, ingredients.get("tomato"), 1, "piece", "diced", em);
        createRecipeIngredient(r10, ingredients.get("cheese"), 50, "g", "grated", em);
    }

    private static void createDessertRecipes(Map<String, Ingredient> ingredients, EntityManager em) {
        // Recipe 1: Chocolate Cake
        Recipe r1 = createRecipe("Chocolate Cake", Category.DESSERT, "Rich and moist chocolate cake", em);
        createRecipeIngredient(r1, ingredients.get("flour"), 200, "g", "sifted", em);
        createRecipeIngredient(r1, ingredients.get("sugar"), 150, "g", "", em);
        createRecipeIngredient(r1, ingredients.get("chocolate"), 100, "g", "melted", em);
        createRecipeIngredient(r1, ingredients.get("eggs"), 3, "eggs", "beaten", em);
        createRecipeIngredient(r1, ingredients.get("butter"), 100, "g", "melted", em);

        // Recipe 2: Strawberry Cheesecake
        Recipe r2 = createRecipe("Strawberry Cheesecake", Category.DESSERT, "Creamy and fruity dessert", em);
        createRecipeIngredient(r2, ingredients.get("cheese"), 250, "g", "cream cheese", em);
        createRecipeIngredient(r2, ingredients.get("strawberry"), 200, "g", "fresh", em);
        createRecipeIngredient(r2, ingredients.get("sugar"), 100, "g", "", em);
        createRecipeIngredient(r2, ingredients.get("eggs"), 2, "eggs", "", em);
        createRecipeIngredient(r2, ingredients.get("cream"), 100, "ml", "heavy", em);

        // Recipe 3: Apple Pie
        Recipe r3 = createRecipe("Apple Pie", Category.DESSERT, "Classic American dessert", em);
        createRecipeIngredient(r3, ingredients.get("apple"), 4, "pieces", "sliced", em);
        createRecipeIngredient(r3, ingredients.get("flour"), 300, "g", "for crust", em);
        createRecipeIngredient(r3, ingredients.get("sugar"), 100, "g", "", em);
        createRecipeIngredient(r3, ingredients.get("butter"), 150, "g", "for crust", em);
        createRecipeIngredient(r3, ingredients.get("lemon"), 1, "piece", "juiced", em);

        // Recipe 4: Chocolate Chip Cookies
        Recipe r4 = createRecipe("Chocolate Chip Cookies", Category.DESSERT, "Classic homemade cookies", em);
        createRecipeIngredient(r4, ingredients.get("flour"), 250, "g", "", em);
        createRecipeIngredient(r4, ingredients.get("sugar"), 150, "g", "", em);
        createRecipeIngredient(r4, ingredients.get("chocolate"), 150, "g", "chips", em);
        createRecipeIngredient(r4, ingredients.get("butter"), 120, "g", "softened", em);
        createRecipeIngredient(r4, ingredients.get("eggs"), 1, "egg", "beaten", em);

        // Recipe 5: Banana Bread
        Recipe r5 = createRecipe("Banana Bread", Category.DESSERT, "Moist and sweet bread", em);
        createRecipeIngredient(r5, ingredients.get("banana"), 3, "pieces", "very ripe, mashed", em);
        createRecipeIngredient(r5, ingredients.get("flour"), 200, "g", "", em);
        createRecipeIngredient(r5, ingredients.get("sugar"), 100, "g", "", em);
        createRecipeIngredient(r5, ingredients.get("butter"), 80, "g", "melted", em);
        createRecipeIngredient(r5, ingredients.get("eggs"), 2, "eggs", "beaten", em);

        // Recipe 6: Blueberry Muffins
        Recipe r6 = createRecipe("Blueberry Muffins", Category.DESSERT, "Soft and fruity muffins", em);
        createRecipeIngredient(r6, ingredients.get("flour"), 200, "g", "", em);
        createRecipeIngredient(r6, ingredients.get("sugar"), 100, "g", "", em);
        createRecipeIngredient(r6, ingredients.get("blueberry"), 150, "g", "fresh", em);
        createRecipeIngredient(r6, ingredients.get("milk"), 120, "ml", "", em);
        createRecipeIngredient(r6, ingredients.get("butter"), 60, "g", "melted", em);

        // Recipe 7: Lemon Tart
        Recipe r7 = createRecipe("Lemon Tart", Category.DESSERT, "Tangy and refreshing tart", em);
        createRecipeIngredient(r7, ingredients.get("flour"), 200, "g", "for crust", em);
        createRecipeIngredient(r7, ingredients.get("lemon"), 3, "pieces", "juiced and zested", em);
        createRecipeIngredient(r7, ingredients.get("sugar"), 150, "g", "", em);
        createRecipeIngredient(r7, ingredients.get("eggs"), 3, "eggs", "beaten", em);
        createRecipeIngredient(r7, ingredients.get("butter"), 100, "g", "for crust", em);

        // Recipe 8: Chocolate Mousse
        Recipe r8 = createRecipe("Chocolate Mousse", Category.DESSERT, "Light and airy chocolate dessert", em);
        createRecipeIngredient(r8, ingredients.get("chocolate"), 200, "g", "dark, melted", em);
        createRecipeIngredient(r8, ingredients.get("eggs"), 4, "eggs", "separated", em);
        createRecipeIngredient(r8, ingredients.get("sugar"), 50, "g", "", em);
        createRecipeIngredient(r8, ingredients.get("cream"), 200, "ml", "whipped", em);

        // Recipe 9: Fruit Salad
        Recipe r9 = createRecipe("Fruit Salad", Category.DESSERT, "Fresh and healthy dessert", em);
        createRecipeIngredient(r9, ingredients.get("strawberry"), 100, "g", "sliced", em);
        createRecipeIngredient(r9, ingredients.get("blueberry"), 100, "g", "fresh", em);
        createRecipeIngredient(r9, ingredients.get("banana"), 2, "pieces", "sliced", em);
        createRecipeIngredient(r9, ingredients.get("apple"), 1, "piece", "diced", em);
        createRecipeIngredient(r9, ingredients.get("honey"), 2, "tbsp", "for dressing", em);

        // Recipe 10: Vanilla Ice Cream
        Recipe r10 = createRecipe("Vanilla Ice Cream", Category.DESSERT, "Creamy homemade ice cream", em);
        createRecipeIngredient(r10, ingredients.get("cream"), 300, "ml", "heavy", em);
        createRecipeIngredient(r10, ingredients.get("milk"), 200, "ml", "", em);
        createRecipeIngredient(r10, ingredients.get("sugar"), 100, "g", "", em);
        createRecipeIngredient(r10, ingredients.get("eggs"), 4, "eggs", "yolks only", em);
        createRecipeIngredient(r10, ingredients.get("vanilla"), 1, "tsp", "extract", em);
    }

    private static void createSnackRecipes(Map<String, Ingredient> ingredients, EntityManager em) {
        // Recipe 1: Trail Mix
        Recipe r1 = createRecipe("Trail Mix", Category.SNACK, "Healthy and energizing snack", em);
        createRecipeIngredient(r1, ingredients.get("oats"), 50, "g", "raw", em);
        createRecipeIngredient(r1, ingredients.get("blueberry"), 30, "g", "dried", em);
        createRecipeIngredient(r1, ingredients.get("chocolate"), 30, "g", "chips", em);
        createRecipeIngredient(r1, ingredients.get("honey"), 1, "tbsp", "for binding", em);

        // Recipe 2: Cheese and Crackers
        Recipe r2 = createRecipe("Cheese and Crackers", Category.SNACK, "Simple and satisfying snack", em);
        createRecipeIngredient(r2, ingredients.get("cheese"), 100, "g", "sliced", em);
        createRecipeIngredient(r2, ingredients.get("bread"), 4, "crackers", "crispy", em);
        createRecipeIngredient(r2, ingredients.get("tomato"), 1, "piece", "sliced", em);

        // Recipe 3: Apple Slices with Peanut Butter
        Recipe r3 = createRecipe("Apple Slices with Peanut Butter", Category.SNACK, "Crunchy and protein-rich", em);
        createRecipeIngredient(r3, ingredients.get("apple"), 1, "piece", "sliced", em);
        createRecipeIngredient(r3, ingredients.get("honey"), 1, "tbsp", "optional", em);

        // Recipe 4: Yogurt with Berries
        Recipe r4 = createRecipe("Yogurt with Berries", Category.SNACK, "Light and refreshing snack", em);
        createRecipeIngredient(r4, ingredients.get("yogurt"), 150, "g", "Greek", em);
        createRecipeIngredient(r4, ingredients.get("strawberry"), 50, "g", "fresh", em);
        createRecipeIngredient(r4, ingredients.get("blueberry"), 50, "g", "fresh", em);
        createRecipeIngredient(r4, ingredients.get("honey"), 1, "tbsp", "drizzled", em);

        // Recipe 5: Veggie Sticks with Dip
        Recipe r5 = createRecipe("Veggie Sticks with Dip", Category.SNACK, "Healthy and crunchy snack", em);
        createRecipeIngredient(r5, ingredients.get("carrot"), 2, "pieces", "cut into sticks", em);
        createRecipeIngredient(r5, ingredients.get("bellPepper"), 1, "piece", "cut into strips", em);
        createRecipeIngredient(r5, ingredients.get("yogurt"), 100, "g", "for dip", em);
        createRecipeIngredient(r5, ingredients.get("lemon"), 1, "piece", "juiced", em);

        // Recipe 6: Banana Smoothie
        Recipe r6 = createRecipe("Banana Smoothie", Category.SNACK, "Creamy and nutritious drink", em);
        createRecipeIngredient(r6, ingredients.get("banana"), 2, "pieces", "frozen", em);
        createRecipeIngredient(r6, ingredients.get("milk"), 200, "ml", "", em);
        createRecipeIngredient(r6, ingredients.get("honey"), 1, "tbsp", "optional", em);
        createRecipeIngredient(r6, ingredients.get("yogurt"), 100, "g", "optional", em);

        // Recipe 7: Popcorn
        Recipe r7 = createRecipe("Popcorn", Category.SNACK, "Classic movie snack", em);
        createRecipeIngredient(r7, ingredients.get("butter"), 30, "g", "melted", em);
        createRecipeIngredient(r7, ingredients.get("salt"), 1, "pinch", "to taste", em);

        // Recipe 8: Energy Balls
        Recipe r8 = createRecipe("Energy Balls", Category.SNACK, "Protein-packed snack balls", em);
        createRecipeIngredient(r8, ingredients.get("oats"), 100, "g", "raw", em);
        createRecipeIngredient(r8, ingredients.get("honey"), 3, "tbsp", "", em);
        createRecipeIngredient(r8, ingredients.get("chocolate"), 50, "g", "chips", em);
        createRecipeIngredient(r8, ingredients.get("butter"), 30, "g", "melted", em);

        // Recipe 9: Toast with Avocado
        Recipe r9 = createRecipe("Toast with Avocado", Category.SNACK, "Simple and healthy toast", em);
        createRecipeIngredient(r9, ingredients.get("bread"), 2, "slices", "toasted", em);
        createRecipeIngredient(r9, ingredients.get("lemon"), 1, "piece", "juiced", em);
        createRecipeIngredient(r9, ingredients.get("salt"), 1, "pinch", "to taste", em);
        createRecipeIngredient(r9, ingredients.get("blackPepper"), 1, "pinch", "freshly ground", em);

        // Recipe 10: Mixed Nuts
        Recipe r10 = createRecipe("Mixed Nuts", Category.SNACK, "Crunchy and protein-rich mix", em);
        createRecipeIngredient(r10, ingredients.get("honey"), 2, "tbsp", "for coating", em);
        createRecipeIngredient(r10, ingredients.get("salt"), 1, "pinch", "to taste", em);
    }

    private static Recipe createRecipe(String name, Category category, String description, EntityManager em) {
        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setCategory(category);
        recipe.setDescription(description);
        em.persist(recipe);
        return recipe;
    }

    private static Ingredient createIngredient(String name, Type type, String description, String slug, EntityManager em) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);
        ingredient.setType(type);
        ingredient.setDescription(description);
        ingredient.setSlug(slug);
        em.persist(ingredient);
        return ingredient;
    }

    private static void createRecipeIngredient(Recipe recipe, Ingredient ingredient, int quantity, String unit, String preparation, EntityManager em) {
        RecipeIngredient ri = new RecipeIngredient();
        ri.setRecipe(recipe);
        ri.setIngredient(ingredient);
        ri.setQuantity(quantity);
        ri.setUnit(unit);
        ri.setPreparation(preparation);
        em.persist(ri);
        
        // Opdater bi-directional relationship
        recipe.getIngredients().add(ri);
        ingredient.getRecipes().add(ri);
    }
}

