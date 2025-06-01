package com.cookpad.controllers;

import com.cookpad.dto.NutritionDto;
import com.cookpad.services.NutritionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recipes/{recipeId}")
@Tag(name = "Nutrition", description = "Endpoints for managing nutrition information for recipes")
public class NutritionController {

    private final NutritionService nutritionService;

    @Autowired
    public NutritionController(NutritionService nutritionService) {
        this.nutritionService = nutritionService;
    }

    @PreAuthorize("hasAuthority('SCOPE_recipe:create')")
    @Operation(
            summary = "Add nutrition information for a recipe",
            description = "This endpoint adds nutrition information for a specific recipe.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nutrition information added successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid nutrition data provided"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found")
            }
    )
    @PostMapping("/add-nutrition")
    public NutritionDto createNutrition(@PathVariable Long recipeId, @RequestBody NutritionDto nutritionDto) {
        return nutritionService.createNutrition(recipeId, nutritionDto);
    }

    @PreAuthorize("hasAuthority('SCOPE_recipe:update')")
    @Operation(
            summary = "Update nutrition information for a recipe",
            description = "This endpoint updates nutrition information for a specific recipe.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nutrition information updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid nutrition data provided"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found"),
                    @ApiResponse(responseCode = "409", description = "Nutrition data conflict")
            }
    )
    @PutMapping("/update-nutrition")
    public ResponseEntity<NutritionDto> updateNutrition(@PathVariable Long recipeId, @RequestBody NutritionDto nutritionDto) {
        return ResponseEntity.ok(nutritionService.updateNutrition(recipeId, nutritionDto));
    }

    @PreAuthorize("hasAuthority('SCOPE_recipe:delete')")
    @Operation(
            summary = "Delete nutrition information for a recipe",
            description = "This endpoint deletes nutrition information for a specific recipe.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Nutrition information deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found")
            }
    )
    @DeleteMapping("/delete-nutrition")
    public ResponseEntity<Void> deleteNutrition(@PathVariable Long recipeId) {
        nutritionService.deleteNutrition(recipeId);
        return ResponseEntity.noContent().build();
    }
}
