# Recipe Management API

A RESTful API for managing recipes, ingredients, and their relationships.

## Setup

1. Fork project
2. Create a PostgreSQL database
3. Create `config.properties`
```
SECRET_KEY={yourSecretKey}
ISSUER="{yourName}"
TOKEN_EXPIRE_TIME=1800000
DB_NAME={yourDbName}
DB_USERNAME={yourDbUserName}
DB_PASSWORD={yourDbPassword}
```
4. Open Maven tab (right-side)
5. Navigate to Lifecycle -> verify
6. Right-click and 'Run Maven Build'
7. Run `Main.java` to start the server on port 7070

## System Overview

The system manages:
- **Recipes** - Cooking recipes with categories (BREAKFAST, LUNCH, DINNER, DESSERT, SNACK)
- **Ingredients** - Food ingredients with types (VEGETABLE, OIL, DAIRY, PROTEIN, SEASONING, GRAIN, SWEETENER, FRUIT, HERB, CONDIMENT, BAKING)
- **RecipeIngredient** - Join table linking recipes to ingredients with quantity, unit, and preparation notes

The system initializes with sample data via `RecipePopulator` on startup:
- 3 recipes (Garlic Scrambled Eggs, Lemon Rice, Butter Basil Pasta)
- 10 ingredients
- All recipes have linked ingredients

## API Documentation

### Recipes

| Method | URL | Request Body (JSON) | Response (JSON) | Error |
| --- | --- | --- | --- | --- |
| GET | `/api/recipes` | | [recipe, recipe, …] (1) | |
| GET | `/api/recipes?category=BREAKFAST` | | [recipe, recipe, …] (1) | |
| GET | `/api/recipes/{id}` | | recipe (1) | (e1) |
| POST | `/api/recipes` | recipe (2) without id | recipe (1) | (e2) |
| PUT | `/api/recipes/{id}` | recipe (2) without id | recipe (1) | (e1), (e2) |
| DELETE | `/api/recipes/{id}` | | 204 No Content | (e1) |
| POST | `/api/recipes/{recipeId}/ingredients` | ingredientRequest (3) | recipeIngredient (4) | (e1), (e3) |
| DELETE | `/api/recipes/{recipeId}/ingredients/{ingredientId}` | | recipe (1) | (e1), (e3) |

<details>
<summary>Recipe (1)</summary>

```json
{
  "id": Integer,
  "name": String,
  "category": "BREAKFAST" | "LUNCH" | "DINNER" | "DESSERT" | "SNACK",
  "description": String,
  "ingredients": [
    {
      "id": Long,
      "ingredient": {
        "id": Integer,
        "name": String,
        "type": String,
        "description": String,
        "slug": String
      },
      "quantity": Integer,
      "unit": String,
      "preparation": String
    }
  ]
}
```

</details>

<details>
<summary>Recipe (2) - For POST/PUT</summary>

> Do not provide ID for POST

```json
{
  "name": String,
  "category": "BREAKFAST" | "LUNCH" | "DINNER" | "DESSERT" | "SNACK",
  "description": String
}
```

</details>

<details>
<summary>Add Ingredient Request (3)</summary>

```json
{
  "ingredientId": Integer,
  "quantity": Integer,
  "unit": String,
  "preparation": String
}
```

</details>

<details>
<summary>RecipeIngredient (4)</summary>

```json
{
  "id": Long,
  "ingredient": {
    "id": Integer,
    "name": String,
    "type": String,
    "description": String,
    "slug": String
  },
  "quantity": Integer,
  "unit": String,
  "preparation": String
}
```

</details>

<details>
<summary>Error (e1) - Recipe Not Found</summary>

```json
{
  "status": 404,
  "msg": "Recipe with ID {id} not found"
}
```

</details>

<details>
<summary>Error (e2) - Validation Error</summary>

```json
{
  "status": 400,
  "msg": "Field 'xxx' is required"
}
```

</details>

<details>
<summary>Error (e3) - Ingredient Not Found (in Recipe context)</summary>

```json
{
  "status": 404,
  "msg": "Ingredient with ID {id} not found"
}
```

</details>

---

### Ingredients

| Method | URL | Request Body (JSON) | Response (JSON) | Error |
| --- | --- | --- | --- | --- |
| GET | `/api/ingredients` | | [ingredient, ingredient, …] (5) | |
| GET | `/api/ingredients?type=VEGETABLE` | | [ingredient, ingredient, …] (5) | (e4) |
| GET | `/api/ingredients/{id}` | | ingredient (5) | (e5) |
| POST | `/api/ingredients` | ingredient (6) without id | ingredient (5) | (e2) |
| PUT | `/api/ingredients/{id}` | ingredient (6) without id | ingredient (5) | (e5), (e2) |
| DELETE | `/api/ingredients/{id}` | | 204 No Content | (e5) |

<details>
<summary>Ingredient (5)</summary>

```json
{
  "id": Integer,
  "name": String,
  "type": "VEGETABLE" | "OIL" | "DAIRY" | "PROTEIN" | "SEASONING" | "GRAIN" | "SWEETENER" | "FRUIT" | "HERB" | "CONDIMENT" | "BAKING",
  "description": String,
  "slug": String,
  "nutrition": {
    "slug": String,
    "calories": Integer,
    "protein": Double,
    "fat": Double,
    "carbs": Double
  }
}
```

> Note: `nutrition` field is only included when fetching ingredients as part of a recipe (via GET /api/recipes/{id})

</details>

<details>
<summary>Ingredient (6) - For POST/PUT</summary>

> Do not provide ID for POST

```json
{
  "name": String,
  "type": "VEGETABLE" | "OIL" | "DAIRY" | "PROTEIN" | "SEASONING" | "GRAIN" | "SWEETENER" | "FRUIT" | "HERB" | "CONDIMENT" | "BAKING",
  "description": String,
  "slug": String
}
```

</details>

<details>
<summary>Error (e4) - Invalid Type</summary>

```json
{
  "status": 400,
  "msg": "Invalid type: {type}"
}
```

</details>

<details>
<summary>Error (e5) - Ingredient Not Found</summary>

```json
{
  "status": 404,
  "msg": "Ingredient with ID {id} not found"
}
```

</details>

---

### Auth

| Method | URL | Request Body (JSON) | Response (JSON) | Error |
| --- | --- | --- | --- | --- |
| GET | `/api/auth/healthcheck` | | message (1) | |
| GET | `/api/auth/test` | | message (1) | |
| POST | `/api/auth/register` | user (2) | token (3) | (e1) |
| POST | `/api/auth/login` | user (2) | token (3) | (e2), (e3) |
| POST | `/api/auth/user/role` | role (4) | message (1) | (e4) |

<details>
<summary>Message (1)</summary>

```json
{
  "msg": String
}
```

</details>

<details>
<summary>User (2)</summary>

```json
{
  "username": String,
  "password": String
}
```

</details>

<details>
<summary>Token (3)</summary>

```json
{
  "token": String,
  "username": String
}
```

</details>

<details>
<summary>Role (4)</summary>

```json
{
  "role": String
}
```

</details>

<details>
<summary>Error (e1) - User Already Exists</summary>

```json
{
  "warning": "User with username: {username} already exist",
  "status": "400 Bad Request"
}
```

</details>

<details>
<summary>Error (e2) - User Not Found</summary>

```json
{
  "msg": "No user found with username: {username}"
}
```

</details>

<details>
<summary>Error (e3) - Wrong Password</summary>

```json
{
  "msg": "Wrong password"
}
```

</details>

<details>
<summary>Error (e4) - User Not Found for Role</summary>

```json
{
  "msg": "No user found with username: {username}"
}
```

</details>

---

## Authentication

**GET endpoints are public** (no authentication required):
- `GET /api/recipes`
- `GET /api/recipes/{id}`
- `GET /api/ingredients`
- `GET /api/ingredients/{id}`

**All write endpoints require authentication** (POST, PUT, DELETE). Include the JWT token in the Authorization header:

```
Authorization: Bearer {token}
```

Get a token by logging in via `/api/auth/login`.

## Testing

Run tests with:
```bash
mvn test
```

The test suite includes:
- API integration tests (`RecipeApiTest.java`, `IngredientApiTest.java`)
- DAO unit tests (`RecipeDAOTest.java`, `IngredientDAOTest.java`)

Tests use Testcontainers for database isolation.

## Project Structure

```
src/main/java/app/
├── config/          # Application and Hibernate configuration
├── controllers/     # REST controllers
├── daos/           # Data Access Objects
├── dtos/           # Data Transfer Objects
├── entities/       # JPA entities (Recipe, Ingredient, RecipeIngredient)
├── enums/          # Category and Type enums
├── exceptions/     # Custom exceptions
├── populators/     # Data initialization (RecipePopulator, UserPopulator)
├── routes/         # Route definitions
└── security/       # Security configuration and controllers
```

## Database Schema

- **Recipes** - Recipe entities
- **Ingredients** - Ingredient entities  
- **Recipes_Ingredients** - Join table with quantity, unit, and preparation
- **Users** - User accounts for authentication
- **Roles** - User roles

## Notes

- Recipe deletion cascades to RecipeIngredient entities (orphanRemoval = true)
- Ingredient deletion cascades to RecipeIngredient entities (orphanRemoval = true)
- GET endpoints (read operations) are public - no authentication required
- Write endpoints (POST, PUT, DELETE) require USER role authentication
- Recipe categories: BREAKFAST, LUNCH, DINNER, DESSERT, SNACK
- Ingredient types: VEGETABLE, OIL, DAIRY, PROTEIN, SEASONING, GRAIN, SWEETENER, FRUIT, HERB, CONDIMENT, BAKING
- Nutrition data is automatically enriched from external API when fetching recipes by ID
- Error handling follows REST principles:
  - **400 Bad Request**: Invalid input format (null, <= 0, invalid enum values, non-parseable)
  - **404 Not Found**: Valid input format but resource doesn't exist
