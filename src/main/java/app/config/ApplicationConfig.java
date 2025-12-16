package app.config;

import app.controllers.impl.ExceptionController;
import app.exceptions.DatabaseException;
import app.populators.RecipePopulator;
import app.populators.UserPopulator;
import app.routes.Routes;
import app.security.controllers.AccessController;
import app.security.enums.Role;
import app.security.exceptions.ApiException;
import app.security.routes.SecurityRoutes;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfig {

    private static final Routes routes = new Routes();
    private static final AccessController accessController = new AccessController();
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private static int count = 1;
    private static final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public static void configuration(JavalinConfig config) {
        config.showJavalinBanner = false;
        config.bundledPlugins.enableRouteOverview("/routes", Role.ANYONE);
        config.router.contextPath = "/api"; // base path for all endpoints
        config.router.apiBuilder(routes.getRoutes());
        config.router.apiBuilder(SecurityRoutes.getSecurityRoutes());
    }

    public static Javalin startServer(int port) {
        Javalin app = Javalin.create(ApplicationConfig::configuration);

        try (EntityManager em = emf.createEntityManager()) {
            UserPopulator.populate(em);
            RecipePopulator.populate(em);
        }

        app.beforeMatched(accessController::accessHandler);
        app.after(ApplicationConfig::afterRequest);

        app.before(ApplicationConfig::corsHeaders);
        app.options("/*", ApplicationConfig::corsHeadersOptions);

        exceptions(app);

        app.start(port);
        return app;
    }

    public static void afterRequest(Context ctx) {
        String requestInfo = ctx.req().getMethod() + " " + ctx.req().getRequestURI();
        logger.info(" Request {} - {} was handled with status code {}", count++, requestInfo, ctx.status());
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }

    private static Javalin exceptions(Javalin app){
        app.exception(Exception.class, ExceptionController::exceptionHandler);
        app.exception(ApiException.class, ExceptionController::apiExceptionHandler);
        app.exception(BadRequestResponse.class, ExceptionController::badRequest);
        app.exception(DatabaseException.class, ExceptionController::dataBaseExceptionHandler);
        app.exception(UnauthorizedResponse.class, ExceptionController::unauthorizedResponseHandler);
        return app;
    }

    private static void corsHeaders(Context ctx) {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ctx.header("Access-Control-Allow-Credentials", "true");
    }

    private static void corsHeadersOptions(Context ctx) {
        ctx.header("Access-Control-Allow-Origin", "*");
        ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        ctx.header("Access-Control-Allow-Credentials", "true");
        ctx.status(204);
    }
}
