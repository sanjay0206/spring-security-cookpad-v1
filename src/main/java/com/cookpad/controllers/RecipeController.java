package com.cookpad.controllers;


import com.cookpad.dto.RecipeDto;
import com.cookpad.responses.RecipeResponse;
import com.cookpad.responses.RecipeWithNutritionResponse;
import com.cookpad.services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {
    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "2";
    private static final String DEFAULT_SORT_BY = "recipeId";
    private static final String DEFAULT_SORT_DIRECTION = "ASC";

    @Autowired
    private RecipeService recipeService;

    @GetMapping
    public RecipeResponse getAllRecipes(@RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                        @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
                                        @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
                                        @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        return recipeService.getAllRecipes(pageNo, pageSize, sortBy, sortDir);
    }

    @PreAuthorize("hasAuthority('SCOPE_recipe:read')")
    @GetMapping("/recipes-with-nutrition")
    public List<RecipeWithNutritionResponse> getRecipesWithNutrition() {
        return recipeService.getRecipesWithNutrition();
    }


    @PreAuthorize("hasAuthority('SCOPE_recipe:read')")
    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable Long recipeId) {
        return ResponseEntity.ok(recipeService.getRecipeById(recipeId));
    }

    @PreAuthorize("hasAuthority('SCOPE_recipe:create')")
    @PostMapping("/add-recipe")
    public RecipeDto createRecipe(@RequestBody RecipeDto recipeDto) {
        return recipeService.createRecipe(recipeDto);
    }

    @PreAuthorize("hasAuthority('SCOPE_recipe:update')")
    @PutMapping("/update-recipe/{recipeId}")
    public ResponseEntity<RecipeDto> updateRecipe(@PathVariable Long recipeId, @RequestBody RecipeDto RecipeDto) {
        return ResponseEntity.ok(recipeService.updateRecipe(recipeId, RecipeDto));
    }

    @PreAuthorize("hasAuthority('SCOPE_recipe:delete')")
    @DeleteMapping("/delete-recipe/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long recipeId) {
        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.noContent().build();
    }
}
