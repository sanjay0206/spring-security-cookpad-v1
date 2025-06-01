package com.cookpad.controllers;

import com.cookpad.dto.NutritionDto;
import com.cookpad.dto.RecipeDto;
import com.cookpad.dto.RecipeDtoV2;
import com.cookpad.responses.RecipePreviewResponse;
import com.cookpad.responses.RecipeResponse;
import com.cookpad.services.NutritionService;
import com.cookpad.services.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
@Tag(name = "Recipe", description = "Endpoints for managing recipes in the system")
public class RecipeController {

    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "2";
    private static final String DEFAULT_SORT_BY = "recipeId";
    private static final String DEFAULT_SORT_DIRECTION = "ASC";

    private final RecipeService recipeService;
    private final NutritionService nutritionService;

    public RecipeController(RecipeService recipeService, NutritionService nutritionService) {
        this.recipeService = recipeService;
        this.nutritionService = nutritionService;
    }

    @PreAuthorize("hasAuthority('SCOPE_recipe:read')")
    @GetMapping("/api/v1/recipes/search")
    @Operation(
            summary = "Search recipes",
            description = "This endpoint allows filtering and searching recipes based on various fields.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully searched recipes"),
                    @ApiResponse(responseCode = "400", description = "Invalid search parameters")
            }
    )
    public RecipeResponse searchRecipes(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long recipeId,
            @RequestParam(required = false) String recipeName,
            @RequestParam(required = false) String recipeType,
            @RequestParam(required = false) Integer prepTime,
            @RequestParam(required = false) Integer cookingTime,
            @RequestParam(required = false) Integer serves,
            @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        int pageIndex = Math.max(0, page - 1);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageIndex, size, sort);
        return recipeService.searchRecipes(query, recipeId, recipeName, recipeType, prepTime, cookingTime, serves, pageable);
    }


    @Operation(
            summary = "Get all recipes",
            description = "This endpoint retrieves all the recipes with pagination, sorting, and filtering options.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved all recipes"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/api/v1/recipes")
    public RecipeResponse getAllRecipes(
            @RequestParam(value = "pageNo", defaultValue = DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        return recipeService.getAllRecipes(pageNo, pageSize, sortBy, sortDir);
    }

    @PreAuthorize("hasAuthority('SCOPE_recipe:read')")
    @Operation(
            summary = "Get recipe previews",
            description = "This endpoint retrieves preview information for all recipes.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved recipe previews"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/api/v1/recipes/recipes-preview")
    public List<RecipePreviewResponse> getAllRecipesPreview() {
        return recipeService.getAllRecipesPreview();
    }

    @PreAuthorize("hasAuthority('SCOPE_recipe:read')")
    @Operation(
            summary = "Get recipe by ID (Content Negotiation)",
            description = "This endpoint retrieves the full details of a specific recipe by its ID using content negotiation.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the recipe"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found")
            }
    )
    @GetMapping(value = "/api/recipes/{recipeId}", produces = "application/vnd.cookpad.v1+json")
    public ResponseEntity<RecipeDto> getRecipeByIdContentNegotiation(@PathVariable Long recipeId) {
        return ResponseEntity.ok(recipeService.getRecipeById(recipeId));
    }

    @PreAuthorize("hasAuthority('SCOPE_recipe:read')")
    @Operation(
            summary = "Get recipe by ID (Header-based Versioning)",
            description = "This endpoint retrieves the full details of a specific recipe by its ID using header-based versioning.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the recipe"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found")
            }
    )
    @GetMapping(value = "/api/recipes/{recipeId}", headers = "X-API-VERSION=1")
    public ResponseEntity<RecipeDto> getRecipeByIdHeader(@PathVariable Long recipeId) {
        return ResponseEntity.ok(recipeService.getRecipeById(recipeId));
    }

    @PreAuthorize("hasAuthority('SCOPE_recipe:read')")
    @Operation(
            summary = "Get recipe by ID (Query Parameter Versioning)",
            description = "This endpoint retrieves the full details of a specific recipe by its ID using query parameter versioning.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the recipe"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found")
            }
    )
    @GetMapping(value = "/api/recipes/{recipeId}", params = "version=2")
    public ResponseEntity<RecipeDtoV2> getRecipeByIdParam(@PathVariable Long recipeId) {
        RecipeDto recipeDto = recipeService.getRecipeById(recipeId);

        RecipeDtoV2 recipeDtoV2 = new RecipeDtoV2();
        recipeDtoV2.setRecipeId(recipeDto.getRecipeId());
        recipeDtoV2.setRecipeName(recipeDto.getRecipeName());
        recipeDtoV2.setRecipeType(recipeDto.getRecipeType());
        recipeDtoV2.setPrepTime(recipeDto.getPrepTime());
        recipeDtoV2.setCookingTime(recipeDto.getCookingTime());
        recipeDtoV2.setServes(recipeDtoV2.getServes());
        recipeDtoV2.setIngredients(recipeDtoV2.getIngredients());
        recipeDtoV2.setCookingMethod(recipeDto.getCookingMethod());
        recipeDtoV2.setImageUrl(recipeDto.getImageUrl());

        // Add Nutrition also the response
        NutritionDto nutritionDto = nutritionService.getNutritionById(recipeId);
        recipeDtoV2.setNutrition(nutritionDto);

        return ResponseEntity.ok(recipeDtoV2);
    }

    @PreAuthorize("hasAuthority('SCOPE_recipe:create')")
    @Operation(
            summary = "Create a new recipe",
            description = "This endpoint allows the creation of a new recipe.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created the recipe"),
                    @ApiResponse(responseCode = "400", description = "Invalid recipe data provided")
            }
    )
    @PostMapping("/api/v1/recipes/add-recipe")
    public RecipeDto createRecipe(@RequestBody @Valid RecipeDto recipeDto) {
        return recipeService.createRecipe(recipeDto);
    }

    @PreAuthorize("hasAuthority('SCOPE_recipe:update')")
    @Operation(
            summary = "Update an existing recipe",
            description = "This endpoint allows updating an existing recipe by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated the recipe"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid recipe data provided")
            }
    )
    @PutMapping("/api/v1/recipes/update-recipe/{recipeId}")
    public ResponseEntity<RecipeDto> updateRecipe(@PathVariable Long recipeId, @RequestBody RecipeDto recipeDto) {
        return ResponseEntity.ok(recipeService.updateRecipe(recipeId, recipeDto));
    }

    @PreAuthorize("hasAuthority('SCOPE_recipe:delete')")
    @Operation(
            summary = "Delete a recipe",
            description = "This endpoint allows deleting a recipe by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the recipe"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found")
            }
    )
    @DeleteMapping("/api/v1/recipes/delete-recipe/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long recipeId) {
        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.noContent().build();
    }
}
