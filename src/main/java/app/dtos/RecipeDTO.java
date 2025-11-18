package app.dtos;

import app.enums.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import app.entities.Recipe;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class RecipeDTO {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("category")
    private Category category;

    @JsonProperty("description")
    private String description;

    @JsonProperty("ingredients")
    private Set<RecipeIngredientDTO> ingredients = new HashSet<>();

    public RecipeDTO(Recipe recipe){
        this.id = recipe.getId();
        this.name = recipe.getName();
        this.category = recipe.getCategory();
        this.description = recipe.getDescription();

        if (recipe.getIngredients() != null) {
            this.ingredients = recipe.getIngredients().stream()
                    .map(RecipeIngredientDTO::new)
                    .collect(Collectors.toSet());
        }
    }
}
