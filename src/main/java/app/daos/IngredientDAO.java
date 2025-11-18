package app.daos;

import app.dtos.IngredientDTO;
import app.entities.Ingredient;
import app.enums.Type;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class IngredientDAO implements IDAO<IngredientDTO, Integer>{
    private EntityManagerFactory emf;

    public IngredientDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public IngredientDTO read(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Ingredient ingredient = em.find(Ingredient.class, integer);
            return ingredient == null ? null : new IngredientDTO(ingredient);
        }  catch (RuntimeException e) {
            throw new DatabaseException(500, "Failed to get ingredient with ID: " + integer);
        }
    }

    @Override
    public List<IngredientDTO> readAll() {
        try(var em = emf.createEntityManager()) {
            TypedQuery<Ingredient> query = em.createQuery("SELECT i FROM Ingredient i", Ingredient.class);
            return query.getResultList().stream().map(IngredientDTO::new).toList();
        } catch (RuntimeException e) {
            throw new DatabaseException(500, "Failed to get all ingredients");
        }
    }

    @Override
    public IngredientDTO create(IngredientDTO ingredientDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            Ingredient ingredient = Ingredient.builder()
                    .name(ingredientDTO.getName())
                    .type(ingredientDTO.getType())
                    .description(ingredientDTO.getDescription())
                    .slug(ingredientDTO.getSlug())
                    .build();

            em.getTransaction().begin();
            em.persist(ingredient);
            em.getTransaction().commit();
            return new IngredientDTO(ingredient);
        } catch (RuntimeException e) {
            throw new DatabaseException(500, "Failed to create ingredient");
        }
    }

    @Override
    public IngredientDTO update(Integer integer, IngredientDTO ingredientDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Ingredient ingredient = em.find(Ingredient.class, integer);

                if (ingredient == null) {
                    em.getTransaction().rollback();
                    throw new DatabaseException(404, "Ingredient with ID " + integer + " not found");
                }

                ingredient.setName(ingredientDTO.getName());
                ingredient.setType(ingredientDTO.getType());
                ingredient.setDescription(ingredientDTO.getDescription());
                ingredient.setSlug(ingredientDTO.getSlug());

                em.getTransaction().commit();
                return new IngredientDTO(ingredient);
            } catch (RuntimeException e) {
                em.getTransaction().rollback();
                if (e instanceof DatabaseException) {
                    throw e;
                }
                throw new DatabaseException(500, "Failed to update ingredient");
            }
        }
    }

    @Override
    public void delete(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Ingredient ingredient = em.find(Ingredient.class, integer);
                if (ingredient == null) {
                    em.getTransaction().rollback();
                    // Gyldigt ID format men ressource eksisterer ikke → 404
                    throw new DatabaseException(404, "Ingredient with ID " + integer + " not found");
                }
                em.remove(ingredient);
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                em.getTransaction().rollback();
                if (e instanceof DatabaseException) {
                    throw e;
                }
                throw new DatabaseException(500, "Failed to delete ingredient");
            }
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        if (integer == null || integer <= 0) {
            return false;
        }
        try (EntityManager em = emf.createEntityManager()) {
            Ingredient ingredient = em.find(Ingredient.class, integer);
            return ingredient != null;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public List<IngredientDTO> searchByType(String type) {
        try(var em = emf.createEntityManager()) {
            Type ingredientType = Type.valueOf(type.toUpperCase());
            TypedQuery<Ingredient> query = em.createQuery(
                    "SELECT i FROM Ingredient i WHERE i.type = :type", Ingredient.class);
            query.setParameter("type", ingredientType);
            return query.getResultList().stream().map(IngredientDTO::new).toList();
        } catch (IllegalArgumentException e) {
            // Ugyldigt type format/navn er en 400 Bad Request (ugyldigt input format)
            throw new DatabaseException(400, "Invalid type: " + type);
        } catch (RuntimeException e) {
            throw new DatabaseException(500, "Failed to get ingredients with type: " + type);
        }
    }
}

