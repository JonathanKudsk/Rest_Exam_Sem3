package app.dtos.ServiceDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class NutritionListDTO {

    @JsonProperty("data")
    private List<NutritionDTO> nutritionDTOS;
}
