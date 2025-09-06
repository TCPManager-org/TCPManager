CREATE TABLE ingredients (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    calories DECIMAL NOT NULL,
    fat DECIMAL NOT NULL,
    carbs DECIMAL NOT NULL,
    protein DECIMAL NOT NULL,
    EAN CHAR[13] NOT NULL
);
CREATE TABLE meals (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);
CREATE TABLE meal_ingredients (
    id SERIAL PRIMARY KEY,
    meal_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    amount DECIMAL NOT NULL,
    FOREIGN KEY (meal_id) REFERENCES meals(id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);