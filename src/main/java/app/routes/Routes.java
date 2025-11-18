package app.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {
    private final RecipeRoutes recipeRoutes = new RecipeRoutes();
    private final IngredientRoutes ingredientRoutes = new IngredientRoutes();

    public EndpointGroup getRoutes() {
        return () -> {
            path("/recipes",recipeRoutes.getRoutes());
            path("/ingredients",ingredientRoutes.getRoutes());
        };
    }
}
