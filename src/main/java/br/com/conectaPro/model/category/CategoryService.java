package br.com.conectaPro.model.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;

    @Transactional
    public Category save(Category category) {

        category.setEnabled(Boolean.TRUE);
        return repository.save(category);
    }

    public List<Category> getAll() {

        return repository.findAll();
    }

    public Category getById(Long id) {

        return repository.findById(id).get();
    }

    @Transactional
    public void update(Long id, Category categoryChanged) {

        Category category = repository.findById(id).get();
        category.setName(categoryChanged.getName());
        category.setDescription(categoryChanged.getDescription());
        repository.save(category);
    }

    @Transactional
    public void delete(Long id) {

        Category category = repository.findById(id).get();
        category.setEnabled(Boolean.FALSE);

        repository.save(category);
    }
}
