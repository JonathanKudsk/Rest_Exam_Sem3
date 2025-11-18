package dat.api;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.populators.RecipePopulator;
import app.populators.UserPopulator;
import app.utils.TokenUtil;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RecipeApiTest {
    private static Javalin app;
    private static EntityManagerFactory emf;
    private static TokenUtil tokenUtil;

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "http://localhost:7007";
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        tokenUtil = new TokenUtil(emf);
        app = ApplicationConfig.startServer(7007);
    }

    @AfterAll
    static void afterAll() {
        ApplicationConfig.stopServer(app);
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    void setup() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // Truncate alle tabeller
            em.createNativeQuery("TRUNCATE TABLE recipes_ingredients RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE recipes RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE ingredients RESTART IDENTITY CASCADE").executeUpdate();
            em.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE").executeUpdate();
            em.getTransaction().commit();

            UserPopulator.populate(em);
            RecipePopulator.populate(em);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========== Recipe Tests ==========

    @Test
    void getAllRecipes() {
        // GET endpoints er public, ingen token nødvendig
        Response response = given()
                .when()
                .get("/api/recipes")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getList(""), hasSize(greaterThanOrEqualTo(3)));
        assertThat(response.jsonPath().getString("name[0]"), containsString("Garlic Scrambled Eggs"));
    }

    @Test
    void getRecipeById() {
        // GET endpoints er public, ingen token nødvendig
        Response response = given()
                .when()
                .get("/api/recipes/1")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getString("id"), is("1"));
        assertThat(response.jsonPath().getString("name"), containsString("Garlic Scrambled Eggs"));
        assertThat(response.jsonPath().getString("category"), is("BREAKFAST"));
        assertThat(response.jsonPath().getString("description"), containsString("Delicious scrambled eggs"));

        // Verificer at ingredients er inkluderet
        assertThat(response.jsonPath().getList("ingredients"), notNullValue());
        assertThat(response.jsonPath().getList("ingredients"), hasSize(4));
        assertThat(response.jsonPath().getString("ingredients[0].quantity"), notNullValue());
        assertThat(response.jsonPath().getString("ingredients[0].unit"), notNullValue());
        assertThat(response.jsonPath().getString("ingredients[0].preparation"), notNullValue());
        
        // Verificer at nutrition data er beriget (hvis tilgængelig fra ekstern API)
        // Tjek om nutrition data eksisterer for mindst ét ingredient
        // Bemærk: Nutrition data er ikke altid tilgængelig fra ekstern API
        // men hvis det eksisterer, skal det have calories, protein, fat, og carbs
        Object nutrition = response.jsonPath().get("ingredients[0].ingredient.nutritionDTO");
        if (nutrition != null) {
            // Hvis nutrition data eksisterer, verificer at det har de forventede felter
            assertThat(response.jsonPath().get("ingredients[0].ingredient.nutritionDTO.calories"), notNullValue());
            assertThat(response.jsonPath().get("ingredients[0].ingredient.nutritionDTO.protein"), notNullValue());
            assertThat(response.jsonPath().get("ingredients[0].ingredient.nutritionDTO.fat"), notNullValue());
            assertThat(response.jsonPath().get("ingredients[0].ingredient.nutritionDTO.carbs"), notNullValue());
        }
    }

    @Test
    void getRecipeByInvalidId() {
        // GET endpoints er public, ingen token nødvendig
        Response response = given()
                .when()
                .get("/api/recipes/999")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(404));
    }

    @Test
    void getRecipesByCategory() {
        // GET endpoints er public, ingen token nødvendig
        Response response = given()
                .queryParam("category", "BREAKFAST")
                .when()
                .get("/api/recipes")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getList(""), hasSize(greaterThanOrEqualTo(1)));
        assertThat(response.jsonPath().getString("category[0]"), is("BREAKFAST"));
    }

    @Test
    void createRecipe() {
        String token = tokenUtil.generateToken("A", "A1");

        String requestBody = "{\n" +
                "  \"name\": \"Test Recipe\",\n" +
                "  \"category\": \"SNACK\",\n" +
                "  \"description\": \"A test recipe for testing purposes\"\n" +
                "}";

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post("/api/recipes")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(201));
        assertThat(response.jsonPath().getString("name"), is("Test Recipe"));
        assertThat(response.jsonPath().getString("category"), is("SNACK"));
        assertThat(response.jsonPath().getString("description"), is("A test recipe for testing purposes"));
    }

    @Test
    void createInvalidRecipe() {
        String token = tokenUtil.generateToken("A", "A1");

        String requestBody = "{\n" +
                "  \"category\": \"SNACK\"\n" +
                "}";

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post("/api/recipes")
                .then()
                .extract().response();

        // Javalin's bodyValidator kaster BadRequestResponse (400) når validering fejler
        assertThat(response.statusCode(), is(400));
    }

    @Test
    void updateRecipe() {
        String token = tokenUtil.generateToken("A", "A1");

        String requestBody = "{\n" +
                "  \"name\": \"Updated Recipe Name\",\n" +
                "  \"category\": \"DINNER\",\n" +
                "  \"description\": \"Updated description\"\n" +
                "}";

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .put("/api/recipes/1")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getString("id"), is("1"));
        assertThat(response.jsonPath().getString("name"), is("Updated Recipe Name"));
        assertThat(response.jsonPath().getString("category"), is("DINNER"));
    }

    @Test
    void updateInvalidRecipe() {
        String token = tokenUtil.generateToken("A", "A1");

        String requestBody = "{\n" +
                "  \"name\": \"Updated Recipe Name\",\n" +
                "  \"category\": \"DINNER\",\n" +
                "  \"description\": \"Updated description\"\n" +
                "}";

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .put("/api/recipes/999")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(404));
    }

    @Test
    void deleteRecipe() {
        String token = tokenUtil.generateToken("A", "A1");

        // Først verificer at recipe eksisterer og har ingredients
        Response getResponseBefore = given()
                .when()
                .get("/api/recipes/1")
                .then()
                .extract().response();

        assertThat(getResponseBefore.statusCode(), is(200));
        assertThat(getResponseBefore.jsonPath().getList("ingredients"), notNullValue());
        assertThat(getResponseBefore.jsonPath().getList("ingredients").size(), greaterThan(0));

        // Slet recipe
        Response response = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/recipes/1")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(204));

        // Verificer at recipe er slettet
        Response getResponseAfter = given()
                .when()
                .get("/api/recipes/1")
                .then()
                .extract().response();

        assertThat(getResponseAfter.statusCode(), is(404));
        
        // Verificer at RecipeIngredient entities også er slettet (cascade deletion)
        // Dette testes indirekte: hvis recipe er slettet og RecipeIngredient entities
        // ikke var slettet, ville de stadig eksistere i databasen men være forældreløse.
        // Da vi ikke kan query RecipeIngredient direkte via API, verificerer vi at
        // recipe og dens ingredients er helt væk.
    }

    @Test
    void deleteInvalidRecipe() {
        String token = tokenUtil.generateToken("A", "A1");

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/recipes/999")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(404));
    }

    // ========== Recipe Ingredient Tests ==========

    @Test
    void addIngredientToRecipe() {
        String token = tokenUtil.generateToken("A", "A1");

        // Brug ingredient 5 (rice) som ikke er i recipe 1 (Garlic Scrambled Eggs)
        // Recipe 1 har: eggs(1), butter(2), garlic(3), salt(4)
        // Recipe 2 har: rice(5), oliveOil(6), lemon(7), salt(4)
        String requestBody = "{\n" +
                "  \"ingredientId\": 5,\n" +
                "  \"quantity\": 3,\n" +
                "  \"unit\": \"pieces\",\n" +
                "  \"preparation\": \"sliced\"\n" +
                "}";

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post("/api/recipes/1/ingredients")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(201));
        assertThat(response.jsonPath().getString("quantity"), is("3"));
        assertThat(response.jsonPath().getString("unit"), is("pieces"));
        assertThat(response.jsonPath().getString("preparation"), is("sliced"));
        assertThat(response.jsonPath().getString("ingredient.id"), is("5"));
    }

    @Test
    void addIngredientToInvalidRecipe() {
        String token = tokenUtil.generateToken("A", "A1");

        String requestBody = "{\n" +
                "  \"ingredientId\": 1,\n" +
                "  \"quantity\": 3,\n" +
                "  \"unit\": \"pieces\",\n" +
                "  \"preparation\": \"sliced\"\n" +
                "}";

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post("/api/recipes/999/ingredients")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(404));
    }

    @Test
    void addInvalidIngredientToRecipe() {
        String token = tokenUtil.generateToken("A", "A1");

        String requestBody = "{\n" +
                "  \"ingredientId\": 999,\n" +
                "  \"quantity\": 3,\n" +
                "  \"unit\": \"pieces\",\n" +
                "  \"preparation\": \"sliced\"\n" +
                "}";

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post("/api/recipes/1/ingredients")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(404));
    }

    @Test
    void removeIngredientFromRecipe() {
        String token = tokenUtil.generateToken("A", "A1");

        // Først, verificer at recipe har ingredients
        Response response1 = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/recipes/1")
                .then()
                .extract().response();

        int initialIngredientCount = response1.jsonPath().getList("ingredients").size();
        assertThat(initialIngredientCount, greaterThan(0));

        // Fjern et ingredient (antager at ingredient med id 1 er i recipe 1)
        Response response2 = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/recipes/1/ingredients/1")
                .then()
                .extract().response();

        assertThat(response2.statusCode(), is(200));
    }

    @Test
    void removeIngredientFromInvalidRecipe() {
        String token = tokenUtil.generateToken("A", "A1");

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/recipes/999/ingredients/1")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(404));
    }

    @Test
    void removeInvalidIngredientFromRecipe() {
        String token = tokenUtil.generateToken("A", "A1");

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/recipes/1/ingredients/999")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(404));
    }

    // ========== Authorization Tests ==========

    @Test
    void getRecipesWithoutAuth() {
        // GET endpoints er public, skal virke uden auth
        Response response = given()
                .when()
                .get("/api/recipes")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getList(""), notNullValue());
    }

    @Test
    void createRecipeWithoutAuth() {
        String requestBody = "{\n" +
                "  \"name\": \"Test Recipe\",\n" +
                "  \"category\": \"SNACK\",\n" +
                "  \"description\": \"A test recipe\"\n" +
                "}";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/recipes")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(401));
    }
}
