package com.example.attestationspring.services;

import com.example.attestationspring.models.Category;
import com.example.attestationspring.models.Product;
import com.example.attestationspring.repositories.ProductRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    public final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //Данный позволяет получить список всех товаров
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }

    //Данный позволяет получить товар по id
    public Product getProductId(int id){
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.orElse(null);
    }

    //Данный позволяет сохранить товар
    @Transactional
    public void saveProduct(Product product, Category category){
        product.setCategory(category);
        productRepository.save(product);
    }

    //Данный позволяет обновить данные о  товаре
    @Transactional
    public void updateProduct(int id,Product product){
        product.setId(id);
        productRepository.save(product);
    }

    //Данный позволяет удалить товар по id
    @Transactional
    public void deleteProduct(int id){
        productRepository.deleteById(id);
    }

}
