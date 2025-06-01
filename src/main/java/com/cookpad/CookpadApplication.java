package com.cookpad;

import com.cookpad.entities.Nutrition;
import com.cookpad.entities.Recipe;
import com.cookpad.entities.User;
import com.cookpad.entities.enums.Gender;
import com.cookpad.entities.enums.RecipeType;
import com.cookpad.repositories.NutritionRepository;
import com.cookpad.repositories.RecipeRepository;
import com.cookpad.repositories.UserRepository;
import com.cookpad.security.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
/*@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,
									 DataSourceTransactionManagerAutoConfiguration.class,
									 HibernateJpaAutoConfiguration.class})*/
@ComponentScan(basePackages = {"com.cookpad"})
@EnableJpaRepositories("com.cookpad.repositories")
@MapperScan("com.cookpad.mapper")
public class CookpadApplication {

	public static void main(String[] args) {
		SpringApplication.run(CookpadApplication.class, args);
		System.out.println("Cookpad app is running...");
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		return mapper;
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return modelMapper;
	}

	@Bean
	CommandLineRunner commandLineRunner (UserRepository userRepository,
										 RecipeRepository recipeRepository,
										 NutritionRepository nutritionRepository) {
		return args -> {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

			User user1 = new User("john_doe", "john@gmail.com", Gender.MALE, encoder.encode("john123") , UserRole.USER, LocalDateTime.now());
			User user2 = new User("jane_smith", "jane@gmail.com", Gender.FEMALE, encoder.encode("jane123"), UserRole.ADMIN, LocalDateTime.now());
			userRepository.saveAll(Arrays.asList(user1, user2));
		};
	}
}
