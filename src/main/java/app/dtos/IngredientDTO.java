package app.dtos;

import app.dtos.ServiceDTOs.NutritionDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import app.entities.Ingredient;
import app.enums.Type;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class IngredientDTO {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private Type type;

    @JsonProperty("description")
    private String description;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("nutrition")
    private NutritionDTO nutritionDTO;

    public IngredientDTO(Ingredient ingredient) {
        this.id = ingredient.getId();
        this.name = ingredient.getName();
        this.type = ingredient.getType();
        this.description = ingredient.getDescription();
        this.slug = ingredient.getSlug();
    }
}
