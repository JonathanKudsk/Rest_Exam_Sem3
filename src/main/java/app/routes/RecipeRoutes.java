package app.routes;

import app.controllers.RecipeController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class RecipeRoutes {
    private final RecipeController recipeController = new RecipeController();

    public EndpointGroup getRoutes() {
        return () -> {
                get("/", recipeController::readAll);
                get("/{id}", recipeController::read);

                post("/", recipeController::create, Role.USER);
                put("/{id}", recipeController::update, Role.USER);
                delete("/{id}", recipeController::delete, Role.USER);
                post("/{recipeId}/ingredients", recipeController::addIngredient, Role.USER);
                delete("/{recipeId}/ingredients/{ingredientId}", recipeController::removeIngredient, Role.USER);
        };
    }
}
