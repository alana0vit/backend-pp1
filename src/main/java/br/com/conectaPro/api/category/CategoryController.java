package br.com.conectaPro.api.category;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.conectaPro.model.category.Category;
import br.com.conectaPro.model.category.CategoryService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/category")
@CrossOrigin
@Tag (
    name = "Category",
    description = "Categorias atreladas aos profissionais para classificar o tipo de serviço que executam"
)
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Operation (
        summary = "Criar entidade categoria"
    )
    @PostMapping
    public ResponseEntity<Category> save(@RequestBody @Valid CategoryRequest request) {

        Category category = categoryService.save(request.build());
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @Operation (
        summary = "Lista todas as categorias"
    )
    @GetMapping
    public List<Category> getAll() {
        return categoryService.getAll();
    }

    @Operation (
        summary = "Lista uma categoria pelo ID"
    )
    @GetMapping("/{id}")
    public Category getById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @Operation (
        summary = "Atualiza uma categoria pelo seu ID"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable("id") Long id, @RequestBody CategoryRequest request) {

        categoryService.update(id, request.build());
        return ResponseEntity.ok().build();

    }

    @Operation (
        summary = "Deleta uma categoria pelo seu ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
