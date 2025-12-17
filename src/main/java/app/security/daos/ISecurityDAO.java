package app.security.daos;

import app.security.entities.User;
import app.security.exceptions.ValidationException;
import dk.bugelhartmann.UserDTO;

import java.util.List;

public interface ISecurityDAO {
    UserDTO getVerifiedUser(String username, String password) throws ValidationException;
    User createUser(String username, String password);
    User addRole(UserDTO user, String newRole);
    User removeRole(String username, String roleToRemove);
    void deleteUser(String username);
    List<UserDTO> getAllUsers();
}
