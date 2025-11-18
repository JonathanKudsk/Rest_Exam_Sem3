package app.populators;

import app.entities.Ingredient;
import app.entities.Recipe;
import app.entities.RecipeIngredient;
import app.enums.Category;
import app.enums.Type;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class RecipePopulator {

    public static void populate(EntityManager em) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // Opret Ingredients
            Ingredient eggs = createIngredient("Egg", Type.PROTEIN, "Chicken eggs", "egg", em);
            Ingredient butter = createIngredient("Butter", Type.DAIRY, "Dairy butter", "butter", em);
            Ingredient garlic = createIngredient("Garlic", Type.VEGETABLE, "Garlic clove", "garlic", em);
            Ingredient salt = createIngredient("Salt", Type.SEASONING, "Table salt", "salt", em);
            Ingredient rice = createIngredient("Rice", Type.GRAIN, "White rice", "rice", em);
            Ingredient oliveOil = createIngredient("Olive Oil", Type.OIL, "Extra virgin olive oil", "olive-oil", em);
            Ingredient lemon = createIngredient("Lemon", Type.FRUIT, "Fresh lemon", "lemon", em);
            Ingredient pasta = createIngredient("Pasta", Type.GRAIN, "Pasta noodles", "pasta", em);
            Ingredient blackPepper = createIngredient("Black Pepper", Type.SEASONING, "Freshly ground black pepper", "black-pepper", em);
            Ingredient basil = createIngredient("Basil", Type.HERB, "Fresh basil leaves", "basil", em);

            // Opret Recipe 1: Garlic Scrambled Eggs
            Recipe garlicScrambledEggs = new Recipe();
            garlicScrambledEggs.setName("Garlic Scrambled Eggs");
            garlicScrambledEggs.setCategory(Category.BREAKFAST);
            garlicScrambledEggs.setDescription("Delicious scrambled eggs with garlic");
            em.persist(garlicScrambledEggs);

            // Link ingredients til recipe 1
            createRecipeIngredient(garlicScrambledEggs, eggs, 2, "eggs", "beaten", em);
            createRecipeIngredient(garlicScrambledEggs, butter, 10, "g", "melted in the pan", em);
            createRecipeIngredient(garlicScrambledEggs, garlic, 1, "clove", "finely chopped", em);
            createRecipeIngredient(garlicScrambledEggs, salt, 1, "pinch", "to taste", em);

            // Opret Recipe 2: Lemon Rice
            Recipe lemonRice = new Recipe();
            lemonRice.setName("Lemon Rice");
            lemonRice.setCategory(Category.LUNCH);
            lemonRice.setDescription("Aromatic rice with lemon flavor");
            em.persist(lemonRice);

            // Link ingredients til recipe 2
            createRecipeIngredient(lemonRice, rice, 150, "g", "cooked", em);
            createRecipeIngredient(lemonRice, oliveOil, 1, "tbsp", "heated in the pan", em);
            createRecipeIngredient(lemonRice, lemon, 1, "½ lemon", "juice + a little zest", em);
            createRecipeIngredient(lemonRice, salt, 1, "pinch", "to taste", em);

            // Opret Recipe 3: Butter Basil Pasta
            Recipe butterBasilPasta = new Recipe();
            butterBasilPasta.setName("Butter Basil Pasta");
            butterBasilPasta.setCategory(Category.DINNER);
            butterBasilPasta.setDescription("Simple and delicious pasta with butter and basil");
            em.persist(butterBasilPasta);

            // Link ingredients til recipe 3
            createRecipeIngredient(butterBasilPasta, pasta, 200, "g", "cooked al dente", em);
            createRecipeIngredient(butterBasilPasta, butter, 20, "g", "melted", em);
            createRecipeIngredient(butterBasilPasta, basil, 5, "g", "roughly torn", em);
            createRecipeIngredient(butterBasilPasta, blackPepper, 1, "pinch", "freshly ground", em);

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error populating recipes", e);
        }
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

