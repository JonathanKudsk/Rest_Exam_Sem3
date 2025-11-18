package app.controllers;

import app.config.HibernateConfig;
import app.daos.IngredientDAO;
import app.dtos.IngredientDTO;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Map;

public class IngredientController implements IController<IngredientDTO, Integer> {

    private final IngredientDAO ingredientDAO;

    public IngredientController(){
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.ingredientDAO = new IngredientDAO(emf);
    }

    @Override
    public void create(Context ctx) {
        IngredientDTO ingredientDTO = validateEntity(ctx);
        IngredientDTO saved = ingredientDAO.create(ingredientDTO);
        ctx.status(201).json(saved, IngredientDTO.class);
    }

    @Override
    public void read(Context ctx){
        int id = ctx.pathParamAsClass("id", Integer.class).get();

        // Tjekker om ID format er gyldigt (burde blive fanget af pathParamAsClass, men dobbelttjekker pga. det nogle gange fejlede)
        if (id <= 0) {
            ctx.status(400).json(Map.of("message", "Invalid ingredient ID format. ID must be a positive integer."));
            return;
        }

        // Gyldigt ID format så tjekker om ressourcen eksisterer
        IngredientDTO ingredientDTO = ingredientDAO.read(id);
        if (ingredientDTO == null) {
            ctx.status(404).json(Map.of("message", "Ingredient with ID " + id + " not found"));
            return;
        }

        ctx.status(200).json(ingredientDTO, Map.class);
    }

    @Override
    public void readAll(Context ctx) {
        List<IngredientDTO> ingredientDTOS;
        String query = ctx.queryParam("type");

        if (query != null && !query.isBlank()) {
            ingredientDTOS = ingredientDAO.searchByType(query);
        }
        else {
            ingredientDTOS = ingredientDAO.readAll();
        }

        ctx.status(200).json(ingredientDTOS, IngredientDTO.class);
    }

    @Override
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();

        // Tjekker om ID format er gyldigt
        if (id <= 0) {
            ctx.status(400).json(Map.of("message", "Invalid ingredient ID format. ID must be a positive integer."));
            return;
        }

        IngredientDTO ingredientDTO = validateEntity(ctx);
        // Gyldigt ID format så DAO vil kaste 404 hvis ressourcen ik eksisterer
        IngredientDTO updated = ingredientDAO.update(id, ingredientDTO);

        ctx.status(200).json(updated, IngredientDTO.class);
    }

    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();

        // Tjekker om ID format er gyldigt
        if (id <= 0) {
            ctx.status(400).json(Map.of("message", "Invalid ingredient ID format. ID must be a positive integer."));
            return;
        }

        // Gyldigt ID format så DAO vil kaste 404 hvis ressourcen ik eksisterer
        ingredientDAO.delete(id);
        ctx.status(204);
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        return ingredientDAO.validatePrimaryKey(integer);
    }

    @Override
    public IngredientDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(IngredientDTO.class)
                .check(i -> i.getName() != null && !i.getName().isBlank(), "Name is required")
                .check(i -> i.getType() != null, "Type is required")
                .check(i -> i.getDescription() != null && !i.getDescription().isBlank(), "Description is required")
                .check(i -> i.getSlug() != null && !i.getSlug().isBlank(), "Slug is required")
                .get();
    }
}

