package com.practice.fuctional.controllers;


import com.practice.fuctional.dto.ProductDTO;
import com.practice.fuctional.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


   /* @GetMapping
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

*/

    // Flux Mono
    @GetMapping
    public Flux<ProductDTO> getAllProductsFlux() {
        return productService.getAllProductsFlux();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductDTO>> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @PostMapping
    public Mono<ResponseEntity<ProductDTO>> addProduct(@Valid @RequestBody ProductDTO productDTO) {
        return productService.addProduct(productDTO)
                .map(product -> ResponseEntity.status(HttpStatus.CREATED).body(product));
    }

    @GetMapping("/cheap")
    public Flux<ProductDTO> getCheapProducts() {
        return productService.getCheapProducts();
    }

    @GetMapping("/last")
    public Flux<ProductDTO> getLastProducts() {
        return productService.getLastProducts();
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id)
                .map(deleted -> ResponseEntity.noContent().build());

        //.then(Mono.just(ResponseEntity.noContent().build()));
    }

    @GetMapping("/count")
    public Mono<Long> countProducts() {
        return productService.countProducts();
    }

}
