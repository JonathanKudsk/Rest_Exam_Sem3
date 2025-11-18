package app.daos;

import app.dtos.RecipeDTO;
import app.dtos.RecipeIngredientDTO;
import app.entities.Recipe;
import app.entities.Ingredient;
import app.entities.RecipeIngredient;
import app.enums.Category;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class RecipeDAO implements IDAO<RecipeDTO, Integer>{
    private EntityManagerFactory emf;

    public RecipeDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public RecipeDTO read(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            // Bruger JOIN FETCH til at load ingredients og deres tilknyttede ingredient entities
            // Dette sikrer at alle data er tilgængelig når vi konverterer til DTO
            TypedQuery<Recipe> query = em.createQuery(
                "SELECT DISTINCT r FROM Recipe r " +
                "LEFT JOIN FETCH r.ingredients ri " +
                "LEFT JOIN FETCH ri.ingredient " +
                "WHERE r.id = :id", 
                Recipe.class
            );
            query.setParameter("id", integer);
            Recipe recipe = query.getResultStream().findFirst().orElse(null);
            return recipe == null ? null : new RecipeDTO(recipe);
        }  catch (RuntimeException e) {
            throw new DatabaseException(500, "Failed to get recipe with ID: " + integer);
        }
    }

    @Override
    public List<RecipeDTO> readAll() {
        try(var em = emf.createEntityManager()) {
            TypedQuery<Recipe> query = em.createQuery("SELECT r FROM Recipe r", Recipe.class);
            return query.getResultList().stream().map(RecipeDTO::new).toList();
        } catch (RuntimeException e) {
            throw new DatabaseException(500, "Failed to get all recipes");
        }
    }

    @Override
    public RecipeDTO create(RecipeDTO recipeDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            Recipe recipe = Recipe.builder()
                    .name(recipeDTO.getName())
                    .category(recipeDTO.getCategory())
                    .description(recipeDTO.getDescription())
                    .build();

            em.getTransaction().begin();
            em.persist(recipe);
            em.getTransaction().commit();
            return new RecipeDTO(recipe);
        } catch (RuntimeException e) {
            throw new DatabaseException(500, "Failed to create recipe");
        }
    }

    @Override
    public RecipeDTO update(Integer integer, RecipeDTO recipeDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Recipe recipe = em.find(Recipe.class, integer);

                if (recipe == null) {
                    em.getTransaction().rollback();
                    throw new DatabaseException(404, "Recipe with ID " + integer + " not found");
                }

                recipe.setName(recipeDTO.getName());
                recipe.setCategory(recipeDTO.getCategory());
                recipe.setDescription(recipeDTO.getDescription());

                em.getTransaction().commit();
                return new RecipeDTO(recipe);
            } catch (RuntimeException e) {
                em.getTransaction().rollback();
                if (e instanceof DatabaseException) {
                    throw e;
                }
                throw new DatabaseException(500, "Failed to update recipe");
            }
        }
    }

    @Override
    public void delete(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Recipe recipe = em.find(Recipe.class, integer);
                if (recipe == null) {
                    em.getTransaction().rollback();
                    throw new DatabaseException(404, "Recipe with ID " + integer + " not found");
                }
                
                // På grund af orphanRemoval = true på Recipe.ingredients,
                // vil alle tilknyttede RecipeIngredient entities automatisk blive slettet når Recipe fjernes
                em.remove(recipe);
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                em.getTransaction().rollback();
                if (e instanceof DatabaseException) {
                    throw e;
                }
                throw new DatabaseException(500, "Failed to delete recipe with ID: " + integer);
            }
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        if (integer == null || integer <= 0) {
            return false;
        }
        try (EntityManager em = emf.createEntityManager()) {
            Recipe recipe = em.find(Recipe.class, integer);
            return recipe != null;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public RecipeIngredientDTO addIngredient(Integer recipeId, Integer ingredientId, int quantity, String unit, String preparation) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Recipe recipe = em.find(Recipe.class, recipeId);
                Ingredient ingredient = em.find(Ingredient.class, ingredientId);

                if (recipe == null) {
                    em.getTransaction().rollback();
                    throw new DatabaseException(404, "Recipe with ID " + recipeId + " not found");
                }
                if (ingredient == null) {
                    em.getTransaction().rollback();
                    throw new DatabaseException(404, "Ingredient with ID " + ingredientId + " not found");
                }

                recipe.addIngredient(ingredient, quantity, unit, preparation);
                // Persist RecipeIngredient der blev oprettet af addIngredient
                // Vi skal finde den fra recipe's ingredients set, da addIngredient opretter den men ikke persisterer
                RecipeIngredient recipeIngredient = recipe.getIngredients().stream()
                    .filter(ri -> ri.getIngredient().equals(ingredient))
                    .findFirst()
                    .orElseThrow(() -> new DatabaseException(500, "Failed to create RecipeIngredient"));

                em.persist(recipeIngredient);
                em.getTransaction().commit();
                return new RecipeIngredientDTO(recipeIngredient);
            } catch (RuntimeException e) {
                em.getTransaction().rollback();
                if (e instanceof DatabaseException) {
                    throw e;
                }
                throw new DatabaseException(500, "Failed to add ingredient with id: " + ingredientId + " to recipe with id: " + recipeId);
            }
        }
    }

    public RecipeDTO removeIngredient(Integer recipeId, Integer ingredientId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Recipe recipe = em.find(Recipe.class, recipeId);
                Ingredient ingredient = em.find(Ingredient.class, ingredientId);

                if (recipe == null) {
                    em.getTransaction().rollback();
                    throw new DatabaseException(404, "Recipe with ID " + recipeId + " not found");
                }
                if (ingredient == null) {
                    em.getTransaction().rollback();
                    throw new DatabaseException(404, "Ingredient with ID " + ingredientId + " not found");
                }

                recipe.removeIngredient(ingredient);

                em.getTransaction().commit();
                return new RecipeDTO(recipe);
            } catch (RuntimeException e) {
                em.getTransaction().rollback();
                if (e instanceof DatabaseException) {
                    throw e;
                }
                throw new DatabaseException(500, "Failed to remove ingredient with id: " + ingredientId + " from recipe with id: " + recipeId);
            }
        }
    }

    public List<RecipeDTO> searchByCategory(String category) {
        try(var em = emf.createEntityManager()) {
            Category cat = Category.valueOf(category.toUpperCase());
            TypedQuery<Recipe> query = em.createQuery(
                    "SELECT r FROM Recipe r WHERE r.category = :category", Recipe.class);
            query.setParameter("category", cat);
            return query.getResultList().stream().map(RecipeDTO::new).toList();
        } catch (IllegalArgumentException e) {
            throw new DatabaseException(400, "Invalid category: " + category);
        } catch (RuntimeException e) {
            throw new DatabaseException(500, "Failed to get recipes with category: " + category);
        }
    }
}

