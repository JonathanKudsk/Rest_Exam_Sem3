package app.daos;

import app.dtos.RecipeIngredientDTO;
import app.entities.RecipeIngredient;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class RecipeIngredientDAO implements IDAO<RecipeIngredientDTO, Long>{
    private EntityManagerFactory emf;

    public RecipeIngredientDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public RecipeIngredientDTO read(Long aLong) {
        try (EntityManager em = emf.createEntityManager()) {
            RecipeIngredient recipeIngredient = em.find(RecipeIngredient.class, aLong);
            return recipeIngredient == null ? null : new RecipeIngredientDTO(recipeIngredient);
        }  catch (RuntimeException e) {
            throw new DatabaseException(500, "Failed to get recipe ingredient with ID: " + aLong);
        }
    }

    @Override
    public List<RecipeIngredientDTO> readAll() {
        try(var em = emf.createEntityManager()) {
            TypedQuery<RecipeIngredient> query = em.createQuery("SELECT ri FROM RecipeIngredient ri", RecipeIngredient.class);
            return query.getResultList().stream().map(RecipeIngredientDTO::new).toList();
        } catch (RuntimeException e) {
            throw new DatabaseException(500, "Failed to get all recipe ingredients");
        }
    }

    @Override
    public RecipeIngredientDTO create(RecipeIngredientDTO recipeIngredientDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            // Bemærk: RecipeIngredient bør typisk oprettes gennem Recipe.addIngredient()
            // Denne metode er tilføjet for fuldstændighed, men kræver at recipe og ingredient er sat
            RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                    .quantity(recipeIngredientDTO.getQuantity())
                    .unit(recipeIngredientDTO.getUnit())
                    .preparation(recipeIngredientDTO.getPreparation())
                    .build();

            em.getTransaction().begin();
            em.persist(recipeIngredient);
            em.getTransaction().commit();
            return new RecipeIngredientDTO(recipeIngredient);
        } catch (RuntimeException e) {
            throw new DatabaseException(500, "Failed to create recipe ingredient");
        }
    }

    @Override
    public RecipeIngredientDTO update(Long aLong, RecipeIngredientDTO recipeIngredientDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                RecipeIngredient recipeIngredient = em.find(RecipeIngredient.class, aLong);

                if (recipeIngredient == null) {
                    em.getTransaction().rollback();
                    throw new DatabaseException(404, "RecipeIngredient with ID " + aLong + " not found");
                }

                recipeIngredient.setQuantity(recipeIngredientDTO.getQuantity());
                recipeIngredient.setUnit(recipeIngredientDTO.getUnit());
                recipeIngredient.setPreparation(recipeIngredientDTO.getPreparation());

                em.getTransaction().commit();
                return new RecipeIngredientDTO(recipeIngredient);
            } catch (RuntimeException e) {
                em.getTransaction().rollback();
                throw new DatabaseException(500, "Failed to update recipe ingredient");
            }
        }
    }

    @Override
    public void delete(Long aLong) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                RecipeIngredient recipeIngredient = em.find(RecipeIngredient.class, aLong);
                if (recipeIngredient == null) {
                    em.getTransaction().rollback();
                    // Gyldigt ID format men ressource eksisterer ikke → 404
                    throw new DatabaseException(404, "RecipeIngredient with ID " + aLong + " not found");
                }
                em.remove(recipeIngredient);
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                em.getTransaction().rollback();
                if (e instanceof DatabaseException) {
                    throw e;
                }
                throw new DatabaseException(500, "Failed to delete recipe ingredient");
            }
        }
    }

    @Override
    public boolean validatePrimaryKey(Long aLong) {
        if (aLong == null || aLong <= 0) {
            return false;
        }
        try (EntityManager em = emf.createEntityManager()) {
            RecipeIngredient recipeIngredient = em.find(RecipeIngredient.class, aLong);
            return recipeIngredient != null;
        } catch (RuntimeException e) {
            return false;
        }
    }
}

