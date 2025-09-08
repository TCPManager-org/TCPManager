package org.tcpmanager.calories.dto;

public record IngredientResponse(Long id, String name, int calories, int fat, int carbs, int protein, String ean) {

}
