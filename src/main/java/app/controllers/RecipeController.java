package app.controllers;

import app.config.HibernateConfig;
import app.daos.IngredientDAO;
import app.daos.RecipeDAO;
import app.dtos.AddIngredientRequestDTO;
import app.dtos.ServiceDTOs.NutritionDTO;
import app.dtos.RecipeDTO;
import app.dtos.RecipeIngredientDTO;
import app.services.FetchTools;
import app.services.NutritionService;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeController implements IController<RecipeDTO, Integer> {

    private final RecipeDAO recipeDAO;
    private final IngredientDAO ingredientDAO;
    private final NutritionService nutritionService;

    public RecipeController(){
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.recipeDAO = new RecipeDAO(emf);
        this.ingredientDAO = new IngredientDAO(emf);
        this.nutritionService = new NutritionService(new FetchTools());
    }

    @Override
    public void create(Context ctx) {
        RecipeDTO recipeDTO = validateEntity(ctx);
        RecipeDTO saved = recipeDAO.create(recipeDTO);
        ctx.status(201).json(saved, RecipeDTO.class);
    }

    @Override
    public void read(Context ctx){
        int id = ctx.pathParamAsClass("id", Integer.class).get();

        // Tjekker om ID format er gyldigt (burde blive fanget af pathParamAsClass, men dobbelttjek pga. det fejlede nogle af gangene)
        if (id <= 0) {
            ctx.status(400).json(Map.of("message", "Invalid recipe ID format. ID must be a positive integer."));
            return;
        }

        // Gyldigt ID format så tjekker om ressourcen eksisterer
        RecipeDTO recipeDTO = recipeDAO.read(id);
        if (recipeDTO == null) {
            ctx.status(404).json(Map.of("message", "Recipe with ID " + id + " not found"));
            return;
        }

        getNutrition(recipeDTO);
        ctx.status(200).json(recipeDTO, Map.class);
    }

    @Override
    public void readAll(Context ctx) {
        List<RecipeDTO> recipeDTOS;
        String query = ctx.queryParam("category");

        if (query != null && !query.isBlank()) {
            recipeDTOS = recipeDAO.searchByCategory(query);
        }
        else {
            recipeDTOS = recipeDAO.readAll();
        }

        ctx.status(200).json(recipeDTOS, RecipeDTO.class);
    }

    @Override
    public void update(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();

        // Tjekker om ID format er gyldigt
        if (id <= 0) {
            ctx.status(400).json(Map.of("message", "Invalid recipe ID format. ID must be a positive integer."));
            return;
        }

        RecipeDTO recipeDTO = validateEntity(ctx);
        // Gyldigt ID format så DAO vil kaste 404 hvis ressourcen ik eksisterer
        RecipeDTO updated = recipeDAO.update(id, recipeDTO);

        ctx.status(200).json(updated, RecipeDTO.class);
    }

    @Override
    public void delete(Context ctx) {
        int id = ctx.pathParamAsClass("id", Integer.class).get();

        // Tjekker om ID format er gyldigt
        if (id <= 0) {
            ctx.status(400).json(Map.of("message", "Invalid recipe ID format. ID must be a positive integer."));
            return;
        }

        // Gyldigt ID format så DAO vil kaste 404 hvis ressourcen ik eksisterer
        recipeDAO.delete(id);
        ctx.status(204);
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        return recipeDAO.validatePrimaryKey(integer);
    }

    public boolean validateIngredientId(Integer integer) {
        return ingredientDAO.validatePrimaryKey(integer);
    }

    @Override
    public RecipeDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(RecipeDTO.class)
                .check(c -> c.getName() != null && !c.getName().isBlank(), "Name is required")
                .check(c -> c.getCategory() != null, "Category is required")
                .check(c -> c.getDescription() != null && !c.getDescription().isBlank(), "Description is required")
                .get();
    }

    public void addIngredient(Context ctx){
        int recipeId = ctx.pathParamAsClass("recipeId", Integer.class).get();

        // Tjekker om recipe ID format er gyldigt
        if (recipeId <= 0) {
            ctx.status(400).json(Map.of("message", "Invalid recipe ID format. ID must be a positive integer."));
            return;
        }

        // Validere og parser request body
        AddIngredientRequestDTO request = ctx.bodyValidator(AddIngredientRequestDTO.class)
                .check(r -> r.getIngredientId() != null, "ingredientId is required")
                .check(r -> r.getIngredientId() > 0, "ingredientId must be a positive integer")
                .check(r -> r.getQuantity() > 0, "quantity must be greater than 0")
                .check(r -> r.getUnit() != null && !r.getUnit().isBlank(), "unit is required")
                .get();

        // Gyldige ID formater så DAO vil kaste 404 hvis ressourcerne ikke eksisterer
        RecipeIngredientDTO created = recipeDAO.addIngredient(
                recipeId,
                request.getIngredientId(),
                request.getQuantity(),
                request.getUnit(),
                request.getPreparation() != null ? request.getPreparation() : ""
        );
        ctx.status(201).json(created, RecipeIngredientDTO.class);
    }

    public void removeIngredient(Context ctx){
        int recipeId = ctx.pathParamAsClass("recipeId", Integer.class).get();

        // Tjekker om recipe ID format er gyldigt
        if (recipeId <= 0) {
            ctx.status(400).json(Map.of("message", "Invalid recipe ID format. ID must be a positive integer."));
            return;
        }

        int ingredientId = ctx.pathParamAsClass("ingredientId", Integer.class).get();

        // Tjekker om ingredient ID format er gyldigt
        if (ingredientId <= 0) {
            ctx.status(400).json(Map.of("message", "Invalid ingredient ID format. ID must be a positive integer."));
            return;
        }

        // Gyldige ID formater så DAO vil kaste 404 hvis ressourcerne ikke eksisterer
        RecipeDTO updated = recipeDAO.removeIngredient(recipeId, ingredientId);
        ctx.status(200).json(updated, RecipeDTO.class);
    }


    private void getNutrition(RecipeDTO recipeDTO) {
        // Henter alle slugs fra ingredients og konverter til lowercase
        List<String> slugs = recipeDTO.getIngredients()
                .stream()
                .map(s -> s.getIngredient().getSlug().toLowerCase())
                .toList();

        if (!slugs.isEmpty()) {
            // Henter nutrition data fra ekstern API
            List<NutritionDTO> nutritionDTOs = nutritionService.getNutrition(slugs);


            // API returnerer slugs i lowercase, så vi matcher også med lowercase
            Map<String, NutritionDTO> nutritionMap = nutritionDTOs.stream()
                    .collect(Collectors.toMap(
                            dto -> dto.getSlug().toLowerCase(),
                            dto -> dto
                    ));

            // Går gennem hvert ingredient og match med lowercase slug
            // Hvis nutrition data findes for ingredient, tilføjes det til ingredient DTO
            recipeDTO.getIngredients().forEach(ingredient -> {
                String slug = ingredient.getIngredient().getSlug().toLowerCase();

                NutritionDTO dto = nutritionMap.get(slug);
                if (dto != null) {
                    ingredient.getIngredient().setNutritionDTO(dto);
                }
            });
        }
    }

}
