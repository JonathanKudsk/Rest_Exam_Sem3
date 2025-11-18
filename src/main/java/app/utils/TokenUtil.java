package app.utils;

import app.security.controllers.SecurityController;
import app.security.daos.SecurityDAO;
import app.security.exceptions.ValidationException;
import dk.bugelhartmann.UserDTO;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenUtil {

    private SecurityDAO securityDAO;
    private SecurityController securityController;
    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    public TokenUtil(EntityManagerFactory emf) {
        this.securityDAO = new SecurityDAO(emf);
        securityController = SecurityController.getInstance();
    }

    public String generateToken(String username, String password) {
        try {
            UserDTO userDTO = securityDAO.getVerifiedUser(username, password);
            return securityController.createToken(userDTO);
        } catch (ValidationException e) {
            logger.error("Failed to generate token");
            throw new RuntimeException(e);
        }
    }
}
