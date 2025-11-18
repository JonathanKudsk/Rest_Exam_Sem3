package app.dtos.ServiceDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class NutritionDTO {

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("calories")
    private Integer calories;

    @JsonProperty("protein")
    private Double protein;

    @JsonProperty("fat")
    private Double fat;

    @JsonProperty("carbs")
    private Double carbs;

}
