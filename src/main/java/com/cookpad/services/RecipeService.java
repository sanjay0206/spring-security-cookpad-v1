package com.cookpad.services;

import com.cookpad.dto.RecipeDto;
import com.cookpad.entities.Recipe;
import com.cookpad.responses.RecipePreviewResponse;
import com.cookpad.responses.RecipeResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeService {
    RecipeResponse getAllRecipes(int pageNo, int pageSize, String sortBy, String sortDir);

    RecipeResponse searchRecipes(String query, Long recipeId, String recipeName, String recipeType,
                                 Integer prepTime, Integer cookingTime, Integer serves, Pageable pageable);

    List<RecipePreviewResponse> getAllRecipesPreview();

    RecipeDto getRecipeById(Long recipeId);

    RecipeDto createRecipe(RecipeDto recipeDto);

    RecipeDto updateRecipe(Long recipeId, RecipeDto recipeDto);

    void deleteRecipe(Long recipeId);

    RecipeDto mapToDTO (Recipe recipe);

    Recipe mapToEntity(RecipeDto recipeDto);
}

