package app.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AddIngredientRequestDTO {

    @JsonProperty("ingredientId")
    private Integer ingredientId;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("preparation")
    private String preparation;
}

