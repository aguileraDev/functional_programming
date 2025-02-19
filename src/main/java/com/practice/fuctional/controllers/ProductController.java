package com.practice.fuctional.controllers;


import com.practice.fuctional.dto.ProductDTO;
import com.practice.fuctional.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping
    public ResponseEntity<ProductDTO> addProduct(ProductDTO productDTO) {
        ProductDTO savedProduct = productService.addProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping("/sorted")
    public List<ProductDTO> getSortedProducts() {
        return productService.getSortedProducts();
    }

    @GetMapping("/recent")
    public List<ProductDTO> getRecentProducts(@RequestParam(defaultValue = "5") int limit) {
        return productService.getRecentProducts(limit);
    }

    @GetMapping("/modified")
    public List<ProductDTO> getModifiedProducts(@RequestParam(defaultValue = "5") int limit) {
        return productService.getModifiedProducts(limit);
    }



    @GetMapping("/groupByPrice")
    public Map<Double, List<ProductDTO>> groupByPrice() {
        return productService.groupByPrice();
    }

    @GetMapping("/minMax")
    public Map<String, ProductDTO> getMinMaxPriceProducts() {
        return productService.getMinMaxProductsPrice();
    }


}
