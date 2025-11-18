package dat.daos;

import app.config.HibernateConfig;
import app.daos.RecipeDAO;
import app.dtos.RecipeDTO;
import app.dtos.RecipeIngredientDTO;
import app.entities.Recipe;
import app.entities.Ingredient;
import app.entities.RecipeIngredient;
import app.enums.Category;
import app.enums.Type;
import app.exceptions.DatabaseException;
import app.populators.RecipePopulator;
import app.populators.UserPopulator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RecipeDAOTest {
    private EntityManagerFactory emf;
    private RecipeDAO recipeDAO;

    @BeforeAll
    void beforeAll() {
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        recipeDAO = new RecipeDAO(emf);
    }

    @AfterAll
    void afterAll() {
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    void setup() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("TRUNCATE TABLE recipes_ingredients RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE recipes RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE ingredients RESTART IDENTITY CASCADE").executeUpdate();
            em.getTransaction().commit();

            RecipePopulator.populate(em);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testReadRecipe() {
        RecipeDTO recipe = recipeDAO.read(1);
        
        assertThat(recipe, notNullValue());
        assertThat(recipe.getId(), is(1));
        assertThat(recipe.getName(), containsString("Garlic Scrambled Eggs"));
        assertThat(recipe.getCategory(), is(Category.BREAKFAST));
        assertThat(recipe.getIngredients(), notNullValue());
        assertThat(recipe.getIngredients().size(), greaterThan(0));
    }

    @Test
    void testReadNonExistentRecipe() {
        RecipeDTO recipe = recipeDAO.read(999);
        assertThat(recipe, nullValue());
    }

    @Test
    void testReadAllRecipes() {
        var recipes = recipeDAO.readAll();
        
        assertThat(recipes, notNullValue());
        assertThat(recipes.size(), greaterThanOrEqualTo(3));
    }

    @Test
    void testCreateRecipe() {
        RecipeDTO newRecipe = RecipeDTO.builder()
                .name("Test Recipe")
                .category(Category.SNACK)
                .description("Test description")
                .build();

        RecipeDTO created = recipeDAO.create(newRecipe);
        
        assertThat(created, notNullValue());
        assertThat(created.getId(), notNullValue());
        assertThat(created.getName(), is("Test Recipe"));
        assertThat(created.getCategory(), is(Category.SNACK));
    }

    @Test
    void testUpdateRecipe() {
        RecipeDTO updateData = RecipeDTO.builder()
                .name("Updated Recipe")
                .category(Category.DINNER)
                .description("Updated description")
                .build();

        RecipeDTO updated = recipeDAO.update(1, updateData);
        
        assertThat(updated, notNullValue());
        assertThat(updated.getId(), is(1));
        assertThat(updated.getName(), is("Updated Recipe"));
        assertThat(updated.getCategory(), is(Category.DINNER));
    }

    @Test
    void testUpdateNonExistentRecipe() {
        RecipeDTO updateData = RecipeDTO.builder()
                .name("Updated Recipe")
                .category(Category.DINNER)
                .description("Updated description")
                .build();

        Assertions.assertThrows(DatabaseException.class, () -> {
            recipeDAO.update(999, updateData);
        });
    }

    @Test
    void testDeleteRecipe() {
        recipeDAO.delete(1);
        
        // Verificer at recipe er slettet
        RecipeDTO deleted = recipeDAO.read(1);
        assertThat(deleted, nullValue());
    }

    @Test
    void testDeleteRecipeCascadesToIngredients() {
        // Først verificer at recipe har ingredients
        RecipeDTO recipe = recipeDAO.read(1);
        assertThat(recipe.getIngredients().size(), greaterThan(0));
        
        // Slet recipe
        recipeDAO.delete(1);
        
        // Verificer at recipe er slettet
        RecipeDTO deleted = recipeDAO.read(1);
        assertThat(deleted, nullValue());
        
        // Verificer at RecipeIngredient entities også er slettet (via orphanRemoval)
        try (EntityManager em = emf.createEntityManager()) {
            var count = em.createQuery("SELECT COUNT(ri) FROM RecipeIngredient ri WHERE ri.recipe.id = 1", Long.class)
                    .getSingleResult();
            assertThat(count, is(0L));
        }
    }

    @Test
    void testDeleteNonExistentRecipe() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            recipeDAO.delete(999);
        });
    }

    @Test
    void testAddIngredient() {
        RecipeIngredientDTO result = recipeDAO.addIngredient(1, 5, 5, "pieces", "chopped");
        
        assertThat(result, notNullValue());
        assertThat(result.getQuantity(), is(5));
        assertThat(result.getUnit(), is("pieces"));
        assertThat(result.getPreparation(), is("chopped"));
        assertThat(result.getIngredient(), notNullValue());
    }

    @Test
    void testAddIngredientToNonExistentRecipe() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            recipeDAO.addIngredient(999, 1, 5, "pieces", "chopped");
        });
    }

    @Test
    void testAddNonExistentIngredient() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            recipeDAO.addIngredient(1, 999, 5, "pieces", "chopped");
        });
    }

    @Test
    void testRemoveIngredient() {
        // Først tilføj et ingredient
        recipeDAO.addIngredient(1, 1, 5, "pieces", "chopped");
        
        // Derefter fjern det
        RecipeDTO updated = recipeDAO.removeIngredient(1, 1);
        
        assertThat(updated, notNullValue());
    }

    @Test
    void testRemoveIngredientFromNonExistentRecipe() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            recipeDAO.removeIngredient(999, 1);
        });
    }

    @Test
    void testRemoveNonExistentIngredient() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            recipeDAO.removeIngredient(1, 999);
        });
    }

    @Test
    void testSearchByCategory() {
        var recipes = recipeDAO.searchByCategory("BREAKFAST");
        
        assertThat(recipes, notNullValue());
        assertThat(recipes.size(), greaterThan(0));
        recipes.forEach(r -> assertThat(r.getCategory(), is(Category.BREAKFAST)));
    }

    @Test
    void testSearchByInvalidCategory() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            recipeDAO.searchByCategory("INVALID");
        });
    }

    @Test
    void testValidatePrimaryKey() {
        assertThat(recipeDAO.validatePrimaryKey(1), is(true));
        assertThat(recipeDAO.validatePrimaryKey(999), is(false));
        assertThat(recipeDAO.validatePrimaryKey(null), is(false));
        assertThat(recipeDAO.validatePrimaryKey(0), is(false));
        assertThat(recipeDAO.validatePrimaryKey(-1), is(false));
    }
}

