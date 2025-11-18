package app.routes;

import app.controllers.IngredientController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class IngredientRoutes {
    private final IngredientController ingredientController = new IngredientController();

    public EndpointGroup getRoutes() {
        return () -> {
                // GET endpoints er public (ingen authentication påkrævet)
                get("/", ingredientController::readAll, Role.ANYONE);
                get("/{id}", ingredientController::read, Role.ANYONE);
                // Write endpoints kræver authentication med Role.USER
                post("/", ingredientController::create, Role.USER);
                put("/{id}", ingredientController::update, Role.USER);
                delete("/{id}", ingredientController::delete, Role.USER);
        };
    }
}

