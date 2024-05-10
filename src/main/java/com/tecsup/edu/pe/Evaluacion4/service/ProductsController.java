package com.tecsup.edu.pe.Evaluacion4.service;

import com.tecsup.edu.pe.Evaluacion4.model.Categories;
import com.tecsup.edu.pe.Evaluacion4.model.Productos;
import com.tecsup.edu.pe.Evaluacion4.repository.CategoriesRepository;
import com.tecsup.edu.pe.Evaluacion4.repository.ProductsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    CategoriesRepository categoriesRepository;

    // Listar todos los productos
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllProductos() {
        List<Productos> productos = productsRepository.findAll();

        // Modificar cada producto para que la respuesta contenga solo el ID de la categoría
        List<Map<String, Object>> response = new ArrayList<>();
        productos.forEach(producto -> {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("id", producto.getId());
            productMap.put("name", producto.getName());
            productMap.put("descripcion", producto.getDescripcion());
            productMap.put("measurement_id",producto.getMeausurement_id());
            productMap.put("currency_id", producto.getCurrency_id());
            productMap.put("brand_id", producto.getBrand_id());
            productMap.put("detail", producto.getDetail());
            productMap.put("status", producto.getStatus());

            Categories categoria = producto.getCategoria();
            if (categoria != null) {
                productMap.put("categoria", categoria.getId());
            } else {
                // Manejar el caso cuando la categoría es nula
                productMap.put("categoria", null);
            }

            // Otros campos que desees incluir en la respuesta

            response.add(productMap);
        });

        return ResponseEntity.ok(response);
    }

    // Obtener producto por id
    @GetMapping("/getById/{id}")
    public Optional<Productos> getById(@PathVariable ("id") int id) {
        return  productsRepository.findById(id);
    }

    @PostMapping("/store")
    public ResponseEntity<Productos> store(@RequestBody Productos producto) {
        if (producto.getCategoria() != null && producto.getCategoria().getId() != 0) {
            Categories categoria = categoriesRepository.findById(producto.getCategoria().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con ID: " + producto.getCategoria().getId()));
            producto.setCategoria(categoria);
        } else {
            throw new IllegalArgumentException("ID de categoría no válido");
        }
        producto.setCreate_at(new Date());
        producto.setUpdate_at(new Date());

        // Guardamos el producto
        return ResponseEntity.ok(productsRepository.save(producto));
    }

    // Para actualizar un producto
    @PutMapping("/update/{id}")
    public ResponseEntity<Productos> update(@RequestBody Productos productos, @PathVariable("id") int id) {
        // Verificar si el producto existe
        Optional<Productos> optionalProducto = productsRepository.findById(id);
        if (optionalProducto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Obtener el producto de la base de datos
        Productos prod = optionalProducto.get();

        // Actualizar los campos solo si no son nulos en la solicitud
        if (productos.getName() != null) {
            prod.setName(productos.getName());
        }
        if (productos.getDescripcion() != null) {
            prod.setDescripcion(productos.getDescripcion());
        }


        // Manejar la relación con la categoría
        if (productos.getCategoria() != null) {
            prod.setCategoria(productos.getCategoria());
        }

        // Guardar el producto actualizado
        Productos updatedProducto = productsRepository.save(prod);

        return ResponseEntity.ok(updatedProducto);
    }

    // Para eliminar un producto
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Productos> delete(@PathVariable ("id") int id) {
        productsRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
