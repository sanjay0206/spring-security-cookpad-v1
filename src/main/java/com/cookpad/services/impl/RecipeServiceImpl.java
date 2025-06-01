package com.cookpad.services.impl;

import com.cookpad.dto.RecipeDto;
import com.cookpad.entities.Recipe;
import com.cookpad.exceptions.ResourceNotFoundException;
import com.cookpad.mapper.RecipeMapper;
import com.cookpad.repositories.RecipeRepository;
import com.cookpad.responses.RecipePreviewResponse;
import com.cookpad.responses.RecipeResponse;
import com.cookpad.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;
    private final ModelMapper modelMapper;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, RecipeMapper recipeMapper, ModelMapper modelMapper) {
        this.recipeRepository = recipeRepository;
        this.recipeMapper = recipeMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public RecipeResponse getAllRecipes(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Recipe> recipes = recipeRepository.findAll(pageable);

        List<RecipeDto> listOfRecipes = recipes.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        RecipeResponse recipeResponse = new RecipeResponse();
        recipeResponse.setContent(listOfRecipes);
        recipeResponse.setPageNo(recipes.getNumber() + 1);
        recipeResponse.setPageSize(recipes.getSize());
        recipeResponse.setTotalElements(recipes.getTotalElements());
        recipeResponse.setTotalPages(recipes.getTotalPages());
        recipeResponse.setLast(recipes.isLast());

        return recipeResponse;
    }

    @Override
    public RecipeResponse searchRecipes(String query, Long recipeId, String recipeName, String recipeType,
                                        Integer prepTime, Integer cookingTime, Integer serves, Pageable pageable) {
        Specification<Recipe> spec = RecipeSpecification.search(query, recipeId, recipeName, recipeType, prepTime, cookingTime, serves);
        Page<Recipe> recipePage = recipeRepository.findAll(spec, pageable);

        List<RecipeDto> recipeDtos = recipePage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        RecipeResponse response = new RecipeResponse();
        response.setContent(recipeDtos);
        response.setPageNo(recipePage.getNumber() + 1);
        response.setPageSize(recipePage.getSize());
        response.setTotalElements(recipePage.getTotalElements());
        response.setTotalPages(recipePage.getTotalPages());
        response.setLast(recipePage.isLast());

        return response;
    }

    @Override
    public List<RecipePreviewResponse> getAllRecipesPreview() {
        return recipeMapper.getRecipesWithNutrition();
    }

    @Override
    public RecipeDto getRecipeById(Long recipeId) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "recipeId", recipeId));

        return mapToDTO(recipe);
    }

    @Override
    public RecipeDto createRecipe(RecipeDto recipeDto) {
        Recipe recipe = mapToEntity(recipeDto);

        recipe.setCreatedAt(LocalDateTime.now());;
        Recipe savedRecipe = recipeRepository.save(recipe);

        return mapToDTO(savedRecipe);
    }

    @Override
    public RecipeDto updateRecipe(Long recipeId, RecipeDto recipeDto) {

        Recipe existingRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "recipeId", recipeId));

        if (recipeDto.getRecipeName() != null) {
            existingRecipe.setRecipeName(recipeDto.getRecipeName());
        }
        if (recipeDto.getRecipeType() != null) {
            existingRecipe.setRecipeType(recipeDto.getRecipeType());
        }
        if (recipeDto.getPrepTime() != null) {
            existingRecipe.setPrepTime(recipeDto.getPrepTime());
        }
        if (recipeDto.getCookingTime() != null) {
            existingRecipe.setCookingTime(recipeDto.getCookingTime());
        }
        if (recipeDto.getServes() != null) {
            existingRecipe.setServes(recipeDto.getServes());
        }
        if (recipeDto.getImageUrl() != null) {
            existingRecipe.setImageUrl(recipeDto.getImageUrl());
        }
        if (recipeDto.getIngredients() != null) {
            existingRecipe.setIngredients(recipeDto.getIngredients());
        }
        if (recipeDto.getCookingMethod() != null) {
            existingRecipe.setCookingMethod(recipeDto.getCookingMethod());
        }
        existingRecipe.setModifiedAt(LocalDateTime.now());

        return mapToDTO(recipeRepository.save(existingRecipe));
    }

    @Override
    public void deleteRecipe(Long recipeId) {

        if (!recipeRepository.existsById(recipeId)) {
            throw new ResourceNotFoundException("Recipe", "recipeId", recipeId);
        }
        recipeRepository.deleteById(recipeId);
    }

    @Override
    public RecipeDto mapToDTO(Recipe recipe) {
        return modelMapper.map(recipe, RecipeDto.class);
    }

    @Override
    public Recipe mapToEntity(RecipeDto recipeDto) {
        return modelMapper.map(recipeDto, Recipe.class);
    }
}
