package br.com.conectaPro.Category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.conectaPro.model.category.CategoryRepository;
import br.com.conectaPro.model.category.CategoryService;
import br.com.conectaPro.model.category.Category;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class CategoryServiceIT {
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @BeforeEach
    void cleanUp() {
        categoryRepository.deleteAll();
    }
    @Test
    @DisplayName("Deve salvar uma categoria")
    void salvarCategoria(){
        Category categoria = new Category();
        categoria.setName("Categoria teste");
        categoria.setDescription("Descricao teste");
        Category resultado = categoryService.save(categoria);

        assertNotNull(resultado.getId());
        assertEquals("Categoria teste", resultado.getName());
    }

    @Test
    @DisplayName("Deve atualizar uma categoria")
    void atualizarCategoria(){
        Category categoria1 = new Category();
        categoria1.setName("Categoria teste");
        categoria1.setDescription("Descricao teste");
        Category resultado1 = categoryService.save(categoria1);

        Category categoria2 = new Category();
        categoria2.setName("Categoria atualizada");
        categoria2.setDescription("Descricao atualizada");

        categoryService.update(resultado1.getId(), categoria2);

        Category atualizada = categoryRepository.findById(resultado1.getId()).orElseThrow();
        assertEquals("Categoria atualizada", atualizada.getName());
        assertEquals("Descricao atualizada", atualizada.getDescription());
    }

    @Test
    @DisplayName("Deve deletar uma categoria")
    void deletarCategoria(){
        Category categoria = new Category();
        categoria.setName("Categoria teste");
        categoria.setDescription("Descricao teste");
        Category resultado = categoryService.save(categoria);

        categoryService.delete(resultado.getId());

        Category deletada = categoryRepository.findById(resultado.getId()).orElseThrow();
        assertEquals(Boolean.FALSE, deletada.getEnabled());
    }

    @Test 
    @DisplayName ("Deve listar todas as categorias")
    void listarCategorias(){
        Category categoria1 = new Category();
        categoria1.setName("Categoria teste");
        categoria1.setDescription("Descricao teste");
        categoryService.save(categoria1);

        Category categoria2 = new Category();
        categoria2.setName("Categoria teste2");
        categoria2.setDescription("Descricao teste2");
        categoryService.save(categoria2);

        List<Category> categorias = categoryService.getAll();

        assertEquals(2, categorias.size());
    }

    @Test 
    @DisplayName ("Deve buscar uma categoria por id")
    void buscarCategoriaPorId(){
        Category categoria = new Category();
        categoria.setName("Categoria teste");
        categoria.setDescription("Descricao teste");
        Category resultado = categoryService.save(categoria);

        Category buscada = categoryService.getById(resultado.getId());

        assertEquals("Categoria teste", buscada.getName());
        assertEquals("Descricao teste", buscada.getDescription());
    }
}
