package com.cookpad.dto;


import com.cookpad.entities.enums.RecipeType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDto {

    @NotEmpty(message = "Recipe name should not be null or empty")
    private String recipeName;

    private RecipeType recipeType;

    private Integer prepTime;

    private Integer cookingTime;

    private Integer serves;

    @NotEmpty(message = "Ingredients should not be null or empty")
    private String ingredients;

    @NotEmpty(message = "Cooking method should not be null or empty")
    private String cookingMethod;

    private String imageUrl;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private NutritionDto nutrition;
}


