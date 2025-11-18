package app.services;

import app.dtos.ServiceDTOs.NutritionDTO;
import app.dtos.ServiceDTOs.NutritionListDTO;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class NutritionService {
    private final String endpoint = "https://apiprovider.cphbusinessapps.dk/api/v1/ingredients/nutrition?slugs=";
    private final FetchTools fetchTools;

    public NutritionService(FetchTools fetchTools) {
        this.fetchTools = fetchTools;
    }

    public List<NutritionDTO> getNutrition(List<String> slugs) {
        if (slugs == null || slugs.isEmpty()) {
            return new ArrayList<>();
        }

        // URL encode hver slug og join med komma
        String joinedSlugs = slugs.stream()
                .map(slug -> URLEncoder.encode(slug, StandardCharsets.UTF_8))
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        // API returnerer data wrapped i {"data": [...]}
        NutritionListDTO nutritionListDTO = fetchTools.getFromApi(endpoint + joinedSlugs, NutritionListDTO.class);
        if (nutritionListDTO == null || nutritionListDTO.getNutritionDTOS() == null) {
            return new ArrayList<>();
        }
        return nutritionListDTO.getNutritionDTOS();
    }
}
