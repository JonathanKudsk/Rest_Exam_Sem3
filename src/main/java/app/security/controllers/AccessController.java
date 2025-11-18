package app.security.controllers;

import app.security.enums.Role;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.RouteRole;

import java.util.Set;

/**
 * Purpose: To handle security in the API at the route level
 *  Author: Jon Bertelsen
 */

public class AccessController implements IAccessController {

    SecurityController securityController = SecurityController.getInstance();

    /**
     * This method checks if the user has the necessary roles to access the route.
     * @param ctx
     */
    public void accessHandler(Context ctx) {

        // Hvis ingen roller er specificeret på endpoint, kan alle tilgå routen
        if (ctx.routeRoles().isEmpty() || ctx.routeRoles().contains(Role.ANYONE)){
           return;
        }

        // Tjek om brugeren er autentificeret
        try {
            securityController.authenticate().handle(ctx);
        } catch (UnauthorizedResponse e) {
            throw new UnauthorizedResponse(e.getMessage());
        } catch (Exception e) {
            throw new UnauthorizedResponse("You are not authorized to access this resource. Please provide valid authentication credentials.");
        }

        // Tjek om brugeren har de nødvendige roller til at tilgå routen
        UserDTO user = ctx.attribute("user");
        Set<RouteRole> allowedRoles = ctx.routeRoles(); // roller tilladt for den nuværende route
        if (!securityController.authorize(user, allowedRoles)) {
            throw new UnauthorizedResponse("Unauthorized with roles: " + user.getRoles() + ". Needed roles are: " + allowedRoles);
        }
    }
}
