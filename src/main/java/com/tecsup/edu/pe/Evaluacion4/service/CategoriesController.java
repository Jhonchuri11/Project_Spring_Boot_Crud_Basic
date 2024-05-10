package com.tecsup.edu.pe.Evaluacion4.service;

import com.tecsup.edu.pe.Evaluacion4.model.Categories;
import com.tecsup.edu.pe.Evaluacion4.repository.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {

    @Autowired
    private CategoriesRepository categoriesRepository;

    // Listar todas las categorias
    @GetMapping("/getAll")
    public List<Categories> getAll() {
        return categoriesRepository.findAll();
    }

    // Obtener una categoria por su id
    @GetMapping("/getById/{id}")
    public Optional<Categories> getById(@PathVariable ("id") int id) {
        return categoriesRepository.findById(id);
    }

    // Para registrar una nueva categoria
    @PostMapping("/store")
    public Categories store(@RequestBody Categories categories) {
        categories.setCreate_at(new Date());
        categories.setUpdate_at(new Date());
        return categoriesRepository.save(categories);
    }

    // Para actualizar una categoria
    @PutMapping("/update/{id}")
    public Categories update(Categories categories, @PathVariable ("id") int id) {
        // Obtenemos la categoria por su id
        Categories cat = categoriesRepository.getById(id);

        // Asigamos los nuevos valores
        cat.setName(categories.getName());
        cat.setDescripcion(categories.getDescripcion());
        cat.setStatus(categories.getStatus());

        // Ejecuatamos y guardamos la nueva data
        return categoriesRepository.save(cat);
    }

    // Para eliminar una categoria
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Categories> delete(@PathVariable ("id") int id) {
        categoriesRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
