package app.security.controllers;

import dk.bugelhartmann.UserDTO;
import io.javalin.http.Handler;
import io.javalin.security.RouteRole;

import java.util.Set;

/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
public interface ISecurityController {
    Handler login(); // for at få en token
    Handler register(); // for at oprette en bruger
    Handler authenticate(); // for at verificere roller i token
    boolean authorize(UserDTO userDTO, Set<RouteRole> allowedRoles); // for at verificere bruger roller
    String createToken(UserDTO user) throws Exception;
    UserDTO verifyToken(String token) throws Exception;
}
