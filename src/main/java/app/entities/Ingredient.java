package app.entities;

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
@Table(name = "Ingredients")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false)
    private String description;

    @Column(nullable = true)
    private String slug;

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<RecipeIngredient> recipes = new HashSet<>();
}
