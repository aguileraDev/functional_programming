package com.practice.fuctional.services;

import com.practice.fuctional.dto.ProductDTO;
import com.practice.fuctional.models.Product;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
public class ProductService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public ProductService( ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }
    private final Supplier<List<Product>> defaultProductSupplier = () -> List.of(
            new Product(UUID.randomUUID().toString(), "Cafe", 24.0),
            new Product(UUID.randomUUID().toString(), "Pizza", 86.0),
            new Product(UUID.randomUUID().toString(), "Pan", 12.0)
    );


    private final Consumer<Product> logProduct = product -> System.out.printf("product added %s price %.2f", product.getName(), product.getPrice());

    private final Predicate<Product> isCheap = product -> product.getPrice() < 20.0;

    private final Function<Product, ProductDTO> productToDto = product -> new ProductDTO(product.getId(), product.getName(), product.getPrice());


    // Flux / Obtiene los productos - map - defaultIfEmpty - switchIfEmpty

    public Flux<ProductDTO> getAllProductsFlux(){
        return reactiveMongoTemplate.findAll(Product.class)
                .map(productToDto);

    }

    public Mono<ProductDTO> getProductById(String id){
        return reactiveMongoTemplate.findById(id, Product.class)
                .map(productToDto)
                .doOnError(error -> System.out.printf("Error al buscar el producto: %s", error.getMessage()))
                .defaultIfEmpty(new ProductDTO(UUID.randomUUID().toString(),"Producto no encontrado", 0.0));
    }

    //agregamos un nuevo producto - doOnNext
    public Mono<ProductDTO> addProduct(ProductDTO productDTO){

        Product product = new Product();
        product.setName(productDTO.name());
        product.setPrice(productDTO.price());

        return reactiveMongoTemplate.save(product)
                .doOnNext(logProduct)
                .map(productToDto);

    }

    //obtener los productos baratos / isCheap

    public Flux<ProductDTO> getCheapProducts(){

        Query query = new Query();
        query.addCriteria(Criteria.where("price").lt(20.0));

        return reactiveMongoTemplate.find(query, Product.class)
                .map(productToDto);
    }

    //Obtener los ultimos 3 agregados
    public Flux<ProductDTO> getLastProducts(){
        Query query = new Query()
                         .with(Sort.by(Sort.Direction.DESC,"_id"))
                         .limit(3);

      return reactiveMongoTemplate.find(query, Product.class)
              .map(productToDto);
    }

    //eliminar por id  - doOnError
    public Mono<Void> deleteProduct(String id){
        Product product = reactiveMongoTemplate.findById(id, Product.class).block();

        return reactiveMongoTemplate.remove(product)
                .doOnError(error -> System.out.printf("Error al eliminar el producto: %s", error.getMessage()))
                .then();
    }

    //contar productos - collectList

    public Mono<Long> countProducts(){
        Query query = new Query(Criteria.where("_id").exists(true));
        return reactiveMongoTemplate.count(query, Product.class);
    }
}