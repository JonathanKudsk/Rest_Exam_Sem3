package app.controllers.impl;

import app.routes.Routes;
import app.security.exceptions.ApiException;
import app.security.exceptions.*;
import app.exceptions.DatabaseException;
import app.exceptions.Message;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(Routes.class);

    public static void apiExceptionHandler(ApiException e, Context ctx) {
        LOGGER.error("{} {} {}", ctx.attribute("requestInfo"), ctx.res().getStatus(), e.getMessage());
        ctx.status(e.getCode());
        ctx.json(new Message(e.getCode(), e.getMessage()));
    }

    public static void badRequest(BadRequestResponse e, Context ctx) {
        LOGGER.error("{} {} {}", ctx.attribute("requestInfo"), ctx.res().getStatus(), e.getMessage());
        ctx.status(400);
        ctx.json(new Message(400, e.getMessage()));
    }

    public static void entityNotFoundHandler(EntityNotFoundException e, Context ctx) {
        LOGGER.error("{} {} {}", ctx.attribute("requestInfo"), ctx.res().getStatus(), e.getMessage());
        ctx.status(404);
        ctx.json(new Message(404, e.getMessage()));
    }

    public static void unauthorizedResponseHandler(UnauthorizedResponse e, Context ctx) {
        LOGGER.error("{} {} {}", ctx.attribute("requestInfo"), ctx.res().getStatus(), e.getMessage());
        ctx.status(401);
        ctx.json(new Message(401, e.getMessage()));
    }

    public static void dataBaseExceptionHandler(DatabaseException e, Context ctx) {
        LOGGER.error("{} {} {}", ctx.attribute("requestInfo"), ctx.res().getStatus(), e.getMessage());
        // Brug status code fra DatabaseException (400, 404, eller 500)
        ctx.status(e.getCode());
        ctx.json(new Message(e.getCode(), e.getMessage()));
    }

    public static void exceptionHandler(Exception e, Context ctx) {
        LOGGER.error("{} {} {}", ctx.attribute("requestInfo"), ctx.res().getStatus(), e.getMessage());
        ctx.status(500);
        ctx.json(new Message(500, e.getMessage()));
    }
}
