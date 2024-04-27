package com.cookpad.dto;


import com.cookpad.entities.enums.RecipeType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDto {
    private String recipeName;
    private RecipeType recipeType;
    private Integer prepTime;
    private Integer cookingTime;
    private Integer serves;
    private String ingredients;
    private String cookingMethod;
    private String imageUrl;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private NutritionDto nutrition;
}
