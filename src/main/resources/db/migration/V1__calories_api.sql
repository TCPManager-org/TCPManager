CREATE SCHEMA calories;
CREATE TABLE calories.ingredients
(
    id       SERIAL PRIMARY KEY,
    name     TEXT    NOT NULL,
    calories NUMERIC NOT NULL,
    fat      NUMERIC NOT NULL,
    carbs    NUMERIC NOT NULL,
    protein  NUMERIC NOT NULL,
    EAN      CHARACTER[13] NOT NULL
);
CREATE TABLE calories.meals
(
    id   SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);
CREATE TABLE calories.meal_ingredients
(
    id            SERIAL PRIMARY KEY,
    meal_id       INTEGER     NOT NULL,
    ingredient_id INTEGER     NOT NULL,
    amount        NUMERIC NOT NULL,
    FOREIGN KEY (meal_id) REFERENCES calories.meals (id),
    FOREIGN KEY (ingredient_id) REFERENCES calories.ingredients (id)
);
CREATE TABLE calories.intake_history
(
    id            SERIAL PRIMARY KEY,
    date          DATE NOT NULL,
    calories      NUMERIC  NOT NULL,
    protein       NUMERIC  NOT NULL,
    fat           NUMERIC  NOT NULL,
    carbs         NUMERIC  NOT NULL,
    calories_goal INTEGER  NOT NULL,
    protein_goal  INTEGER  NOT NULL,
    fat_goal      INTEGER  NOT NULL,
    carbs_goal    INTEGER  NOT NULL
);