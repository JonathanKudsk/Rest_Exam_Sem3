package app.security.routes;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.utils.Utils;
import app.security.controllers.SecurityController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

/**
 * Purpose: To handle security in the API
 *  Author: Thomas Hartmann
 */
public class SecurityRoutes {
    private static ObjectMapper jsonMapper = new Utils().getObjectMapper();
    private static SecurityController securityController = SecurityController.getInstance();
    public static EndpointGroup getSecurityRoutes() {
        return ()->{
            path("/auth", ()->{
                get("/healthcheck", securityController::healthCheck, Role.ANYONE);
                get("/test", ctx->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello World!")), Role.ANYONE);
                post("/register", securityController.register(), Role.ANYONE);
                post("/login", securityController.login(), Role.ANYONE);
                get("/users", securityController.getAllUsers(), Role.ADMIN);
                post("/user/role", securityController.addRole(), Role.ADMIN);
                delete("/user/role", securityController.removeRole(), Role.ADMIN);
                delete("/user", securityController.deleteUser(), Role.ADMIN);
            });
        };
    }
}
