package br.com.conectaPro.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.conectaPro.model.category.Category;
import br.com.conectaPro.model.category.CategoryRepository;
import br.com.conectaPro.model.category.CategoryService;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("Salvando uma categoria")
    void salvandoCategoria(){
        Category categoria = new Category();
        categoria.setName("eletricista");

        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category resultado = categoryService.save(categoria);

        assertNotNull(resultado);
        assertEquals(categoria, resultado);
        assertEquals("eletricista", categoria.getName());
    }

    @Test
    @DisplayName("Listando todas as categorias")
    void ListaTodasCategoria(){
        Category categoria = new Category();
        categoria.setId(1L);

        Category categoria2 = new Category();
        categoria.setId(2L);

        when(categoryRepository.findAll()).thenReturn(List.of(categoria, categoria2));

        List<Category> resultado = categoryService.getAll();

         assertNotNull(resultado);
         assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Deletando uma categoria")
    void deletandoCategoria(){
        Long id = 1L;
        Category categoria = new Category();
        categoria.setId(id);
        categoria.setEnabled(true);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(categoria));

        categoryService.delete(id);

        assertFalse(categoria.getEnabled());
    }

    @Test
    @DisplayName("Fazendo atualizacao em uma categoria")
    void updateCategory(){
        Long id = 1L;
        Category categoriaAntiga = new Category();
        categoriaAntiga.setId(id);
        categoriaAntiga.setName("Nome antigo");
        categoriaAntiga.setDescription("descricao antiga");

        Category categoriaNova = new Category();
        categoriaNova.setName("Nome novo");
        categoriaNova.setDescription("Nova descricao");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(categoriaAntiga));

        categoryService.update(id, categoriaNova);

        assertEquals("Nome novo", categoriaAntiga.getName());
        assertEquals("Nova descricao", categoriaAntiga.getDescription());
    }

    @Test
    @DisplayName("Buscando categoria por Id")
    void deveBuscarCategoriaPorId() {
        Long id = 1L;
        Category categoria = new Category();
        categoria.setId(id);
        categoria.setName("Eletricista");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(categoria));

        Category resultado = categoryService.getById(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Eletricista", resultado.getName());
    }
    
}
