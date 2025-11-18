package app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import app.entities.RecipeIngredient;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class RecipeIngredientDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("ingredient")
    private IngredientDTO ingredient;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("preparation")
    private String preparation;

    public RecipeIngredientDTO(RecipeIngredient recipeIngredient) {
        this.id = recipeIngredient.getId();
        this.quantity = recipeIngredient.getQuantity();
        this.unit = recipeIngredient.getUnit();
        this.preparation = recipeIngredient.getPreparation();
        this.ingredient = Optional.ofNullable(recipeIngredient.getIngredient())
                .map(IngredientDTO::new)
                .orElse(null);
    }
}
