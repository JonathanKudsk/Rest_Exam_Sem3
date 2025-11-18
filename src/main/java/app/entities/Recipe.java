package app.entities;

import app.enums.Category;
import app.enums.Type;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<RecipeIngredient> ingredients = new HashSet<>();


    // Bi-directional relationship - håndterer begge sider af relationen
    public void addIngredient(Ingredient ingredient, int quantity, String unit, String preparation) {
        if (ingredient == null) return;

        RecipeIngredient ri = new RecipeIngredient();
        ri.setRecipe(this);
        ri.setIngredient(ingredient);
        ri.setQuantity(quantity);
        ri.setUnit(unit);
        ri.setPreparation(preparation);

        // Tilføj til begge sider af relationen for at opretholde konsistens
        this.ingredients.add(ri);
        ingredient.getRecipes().add(ri);
    }

    public void removeIngredient(Ingredient ingredient) {
        if (ingredient == null) return;

        RecipeIngredient toRemove = this.ingredients.stream()
                .filter(ri -> ri.getIngredient().equals(ingredient))
                .findFirst()
                .orElse(null);

        if (toRemove != null) {
            this.ingredients.remove(toRemove);
            ingredient.getRecipes().remove(toRemove);

            // Valgfrit – hjælper ORM'en med at rydde op i hukommelsen
            toRemove.setRecipe(null);
            toRemove.setIngredient(null);
        }
    }
}
