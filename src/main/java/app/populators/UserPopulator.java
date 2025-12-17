package app.populators;

import app.security.daos.SecurityDAO;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.EntityManager;

import java.util.List;

public class UserPopulator {

    private static SecurityDAO securityDAO;

    public static List<UserDTO> populate(EntityManager em) {
        securityDAO = new SecurityDAO(em.getEntityManagerFactory());

        String username1 = "A";
        String password1 = "A1";
        UserDTO u1 = new UserDTO(username1, password1);
        securityDAO.createUser(username1, password1);
        securityDAO.addRole(u1, "admin");

        String username2 = "U";
        String password2 = "U1";
        UserDTO u2 = new UserDTO(username2, password2);
        securityDAO.createUser(username2, password2);
        securityDAO.addRole(u1, "user");

        return List.of(u1, u2);
    }
}
