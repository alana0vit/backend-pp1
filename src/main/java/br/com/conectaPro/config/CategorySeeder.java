package br.com.conectaPro.config;

import br.com.conectaPro.model.category.Category;
import br.com.conectaPro.model.category.CategoryRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategorySeeder implements CommandLineRunner {

  private final CategoryRepository categoryRepository;

  public CategorySeeder(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Override
  public void run(String... args) {

    seedCategories();
  }

  private void seedCategories() {

    List<Category> categories =
        List.of(
            Category.builder().name("Eletricista").description("Serviços elétricos").build(),
            Category.builder().name("Encanador").description("Serviços hidráulicos").build(),
            Category.builder().name("Pedreiro").description("Construção e reformas").build(),
            Category.builder()
                .name("Pintor")
                .description("Pintura residencial e comercial")
                .build(),
            Category.builder()
                .name("Carpinteiro")
                .description("Artes e móveis com madeira")
                .build(),
            Category.builder()
                .name("Professor")
                .description("Aulas on-line ou presenciais")
                .build(),
            Category.builder()
                .name("Serviços Gerais")
                .description("Limpeza e organização de residencias, comercios ou empresas")
                .build());

    for (Category category : categories) {

      boolean exists = categoryRepository.existsByName(category.getName());

      if (!exists) {

        category.setEnabled(true);

        categoryRepository.save(category);

        System.out.println("Categoria criada: " + category.getName());
      }
    }
  }
}
