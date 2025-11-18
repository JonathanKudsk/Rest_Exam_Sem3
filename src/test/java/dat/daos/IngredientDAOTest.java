package dat.daos;

import app.config.HibernateConfig;
import app.daos.IngredientDAO;
import app.dtos.IngredientDTO;
import app.entities.Ingredient;
import app.enums.Type;
import app.exceptions.DatabaseException;
import app.populators.RecipePopulator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IngredientDAOTest {
    private EntityManagerFactory emf;
    private IngredientDAO ingredientDAO;

    @BeforeAll
    void beforeAll() {
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        ingredientDAO = new IngredientDAO(emf);
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
    void testReadIngredient() {
        IngredientDTO ingredient = ingredientDAO.read(1);
        
        assertThat(ingredient, notNullValue());
        assertThat(ingredient.getId(), is(1));
        assertThat(ingredient.getName(), notNullValue());
        assertThat(ingredient.getType(), notNullValue());
        assertThat(ingredient.getDescription(), notNullValue());
        assertThat(ingredient.getSlug(), notNullValue());
    }

    @Test
    void testReadNonExistentIngredient() {
        IngredientDTO ingredient = ingredientDAO.read(999);
        assertThat(ingredient, nullValue());
    }

    @Test
    void testReadAllIngredients() {
        var ingredients = ingredientDAO.readAll();
        
        assertThat(ingredients, notNullValue());
        assertThat(ingredients.size(), greaterThanOrEqualTo(10));
    }

    @Test
    void testCreateIngredient() {
        IngredientDTO newIngredient = IngredientDTO.builder()
                .name("Test Ingredient")
                .type(Type.VEGETABLE)
                .description("Test description")
                .slug("test-ingredient")
                .build();

        IngredientDTO created = ingredientDAO.create(newIngredient);
        
        assertThat(created, notNullValue());
        assertThat(created.getId(), notNullValue());
        assertThat(created.getName(), is("Test Ingredient"));
        assertThat(created.getType(), is(Type.VEGETABLE));
        assertThat(created.getSlug(), is("test-ingredient"));
    }

    @Test
    void testUpdateIngredient() {
        IngredientDTO updateData = IngredientDTO.builder()
                .name("Updated Ingredient")
                .type(Type.PROTEIN)
                .description("Updated description")
                .slug("updated-ingredient")
                .build();

        IngredientDTO updated = ingredientDAO.update(1, updateData);
        
        assertThat(updated, notNullValue());
        assertThat(updated.getId(), is(1));
        assertThat(updated.getName(), is("Updated Ingredient"));
        assertThat(updated.getType(), is(Type.PROTEIN));
        assertThat(updated.getSlug(), is("updated-ingredient"));
    }

    @Test
    void testUpdateNonExistentIngredient() {
        IngredientDTO updateData = IngredientDTO.builder()
                .name("Updated Ingredient")
                .type(Type.PROTEIN)
                .description("Updated description")
                .slug("updated-ingredient")
                .build();

        Assertions.assertThrows(DatabaseException.class, () -> {
            ingredientDAO.update(999, updateData);
        });
    }

    @Test
    void testDeleteIngredient() {
        ingredientDAO.delete(1);
        
        // Verificer at ingredient er slettet
        IngredientDTO deleted = ingredientDAO.read(1);
        assertThat(deleted, nullValue());
    }

    @Test
    void testDeleteIngredientCascadesToRecipeIngredients() {
        // Først verificer at ingredient eksisterer og bruges i recipes
        IngredientDTO ingredient = ingredientDAO.read(1);
        assertThat(ingredient, notNullValue());
        
        // Verificer at ingredient bruges i RecipeIngredient (via RecipePopulator)
        try (EntityManager em = emf.createEntityManager()) {
            var countBefore = em.createQuery("SELECT COUNT(ri) FROM RecipeIngredient ri WHERE ri.ingredient.id = 1", Long.class)
                    .getSingleResult();
            assertThat(countBefore, greaterThan(0L));
        }
        
        // Slet ingredient
        ingredientDAO.delete(1);
        
        // Verificer at ingredient er slettet
        IngredientDTO deleted = ingredientDAO.read(1);
        assertThat(deleted, nullValue());
        
        // Verificer at RecipeIngredient entities også er slettet (via orphanRemoval)
        try (EntityManager em = emf.createEntityManager()) {
            var countAfter = em.createQuery("SELECT COUNT(ri) FROM RecipeIngredient ri WHERE ri.ingredient.id = 1", Long.class)
                    .getSingleResult();
            assertThat(countAfter, is(0L));
        }
    }

    @Test
    void testDeleteNonExistentIngredient() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            ingredientDAO.delete(999);
        });
    }

    @Test
    void testSearchByType() {
        var ingredients = ingredientDAO.searchByType("VEGETABLE");
        
        assertThat(ingredients, notNullValue());
        assertThat(ingredients.size(), greaterThanOrEqualTo(0));
        ingredients.forEach(i -> assertThat(i.getType(), is(Type.VEGETABLE)));
    }

    @Test
    void testSearchByInvalidType() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            ingredientDAO.searchByType("INVALID");
        });
    }

    @Test
    void testValidatePrimaryKey() {
        assertThat(ingredientDAO.validatePrimaryKey(1), is(true));
        assertThat(ingredientDAO.validatePrimaryKey(999), is(false));
        assertThat(ingredientDAO.validatePrimaryKey(null), is(false));
        assertThat(ingredientDAO.validatePrimaryKey(0), is(false));
        assertThat(ingredientDAO.validatePrimaryKey(-1), is(false));
    }
}

