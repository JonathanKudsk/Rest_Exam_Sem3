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
public class IngredientApiTest {
    private static Javalin app;
    private static EntityManagerFactory emf;
    private static TokenUtil tokenUtil;

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "http://localhost:7008";
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        tokenUtil = new TokenUtil(emf);
        app = ApplicationConfig.startServer(7008);
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

    // ========== Ingredient Tests ==========

    @Test
    void getAllIngredients() {
        // GET endpoints er public, ingen token nødvendig
        Response response = given()
                .when()
                .get("/api/ingredients")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getList(""), hasSize(greaterThanOrEqualTo(10)));
        assertThat(response.jsonPath().getString("name[0]"), notNullValue());
    }

    @Test
    void getIngredientById() {
        // GET endpoints er public, ingen token nødvendig
        Response response = given()
                .when()
                .get("/api/ingredients/1")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getString("id"), is("1"));
        assertThat(response.jsonPath().getString("name"), notNullValue());
        assertThat(response.jsonPath().getString("type"), notNullValue());
        assertThat(response.jsonPath().getString("description"), notNullValue());
        assertThat(response.jsonPath().getString("slug"), notNullValue());
    }

    @Test
    void getIngredientByInvalidId() {
        // GET endpoints er public, ingen token nødvendig
        Response response = given()
                .when()
                .get("/api/ingredients/999")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(404));
    }

    @Test
    void getIngredientsByType() {
        // GET endpoints er public, ingen token nødvendig
        Response response = given()
                .queryParam("type", "VEGETABLE")
                .when()
                .get("/api/ingredients")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getList(""), notNullValue());
        // Alle returnerede ingredients skal have type VEGETABLE
        if (response.jsonPath().getList("").size() > 0) {
            assertThat(response.jsonPath().getString("type[0]"), is("VEGETABLE"));
        }
    }

    @Test
    void getIngredientsByInvalidType() {
        // GET endpoints er public, ingen token nødvendig
        Response response = given()
                .queryParam("type", "INVALID_TYPE")
                .when()
                .get("/api/ingredients")
                .then()
                .extract().response();

        // Ugyldig type skal returnere 400 Bad Request
        assertThat(response.statusCode(), is(400));
    }

    @Test
    void createIngredient() {
        String token = tokenUtil.generateToken("A", "A1");

        String requestBody = "{\n" +
                "  \"name\": \"Test Ingredient\",\n" +
                "  \"type\": \"VEGETABLE\",\n" +
                "  \"description\": \"A test ingredient for testing purposes\",\n" +
                "  \"slug\": \"test-ingredient\"\n" +
                "}";

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post("/api/ingredients")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(201));
        assertThat(response.jsonPath().getString("name"), is("Test Ingredient"));
        assertThat(response.jsonPath().getString("type"), is("VEGETABLE"));
        assertThat(response.jsonPath().getString("description"), is("A test ingredient for testing purposes"));
        assertThat(response.jsonPath().getString("slug"), is("test-ingredient"));
    }

    @Test
    void createInvalidIngredient() {
        String token = tokenUtil.generateToken("A", "A1");

        String requestBody = "{\n" +
                "  \"type\": \"VEGETABLE\"\n" +
                "}";

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .post("/api/ingredients")
                .then()
                .extract().response();

        // Javalin's bodyValidator kaster BadRequestResponse (400) når validering fejler
        assertThat(response.statusCode(), is(400));
    }

    @Test
    void updateIngredient() {
        String token = tokenUtil.generateToken("A", "A1");

        String requestBody = "{\n" +
                "  \"name\": \"Updated Ingredient Name\",\n" +
                "  \"type\": \"PROTEIN\",\n" +
                "  \"description\": \"Updated description\",\n" +
                "  \"slug\": \"updated-ingredient\"\n" +
                "}";

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .put("/api/ingredients/1")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getString("id"), is("1"));
        assertThat(response.jsonPath().getString("name"), is("Updated Ingredient Name"));
        assertThat(response.jsonPath().getString("type"), is("PROTEIN"));
        assertThat(response.jsonPath().getString("slug"), is("updated-ingredient"));
    }

    @Test
    void updateInvalidIngredient() {
        String token = tokenUtil.generateToken("A", "A1");

        String requestBody = "{\n" +
                "  \"name\": \"Updated Ingredient Name\",\n" +
                "  \"type\": \"PROTEIN\",\n" +
                "  \"description\": \"Updated description\",\n" +
                "  \"slug\": \"updated-ingredient\"\n" +
                "}";

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when()
                .put("/api/ingredients/999")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(404));
    }

    @Test
    void deleteIngredient() {
        String token = tokenUtil.generateToken("A", "A1");

        // Først opret en ny ingredient at slette (for at undgå foreign key problemer)
        String createRequestBody = "{\n" +
                "  \"name\": \"Test Ingredient To Delete\",\n" +
                "  \"type\": \"VEGETABLE\",\n" +
                "  \"description\": \"Test ingredient for deletion\",\n" +
                "  \"slug\": \"test-delete\"\n" +
                "}";

        Response createResponse = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(createRequestBody)
                .when()
                .post("/api/ingredients")
                .then()
                .extract().response();

        assertThat(createResponse.statusCode(), is(201));
        String ingredientId = createResponse.jsonPath().getString("id");

        // Verificer at ingredient eksisterer
        Response getResponseBefore = given()
                .when()
                .get("/api/ingredients/" + ingredientId)
                .then()
                .extract().response();

        assertThat(getResponseBefore.statusCode(), is(200));

        // Slet ingredient
        Response response = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/ingredients/" + ingredientId)
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(204));

        // Verify ingredient is deleted
        Response getResponseAfter = given()
                .when()
                .get("/api/ingredients/" + ingredientId)
                .then()
                .extract().response();

        assertThat(getResponseAfter.statusCode(), is(404));
    }

    @Test
    void deleteInvalidIngredient() {
        String token = tokenUtil.generateToken("A", "A1");

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/ingredients/999")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(404));
    }

    // ========== Authorization Tests ==========

    @Test
    void getIngredientsWithoutAuth() {
        // GET endpoints er public, skal virke uden auth
        Response response = given()
                .when()
                .get("/api/ingredients")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getList(""), notNullValue());
    }

    @Test
    void createIngredientWithoutAuth() {
        String requestBody = "{\n" +
                "  \"name\": \"Test Ingredient\",\n" +
                "  \"type\": \"VEGETABLE\",\n" +
                "  \"description\": \"A test ingredient\",\n" +
                "  \"slug\": \"test-ingredient\"\n" +
                "}";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/ingredients")
                .then()
                .extract().response();

        assertThat(response.statusCode(), is(401));
    }
}

