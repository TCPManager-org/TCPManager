CREATE TABLE intake_history (
    id SERIAL PRIMARY KEY,
    date DATE NOT NULL,
    calories INT NOT NULL,
    protein INT NOT NULL,
    fat INT NOT NULL,
    carbs INT NOT NULL,
    calories_goal INT NOT NULL,
    protein_goal INT NOT NULL,
    fat_goal INT NOT NULL,
    carbs_goal INT NOT NULL
);