package com.practice.fuctional.services;

import com.practice.fuctional.dto.ProductDTO;
import com.practice.fuctional.models.Product;
import com.practice.fuctional.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    private final Supplier<List<Product>> defaultProductSupplier = () -> List.of(
            new Product(1L, "Cafe", 24.0),
            new Product(2L, "UPC", 86.0),
            new Product(3L, "Pan", 12.0)
    );


    private final Consumer<Product> logProduct = product -> System.out.printf("product added %s price %.2f", product.getName(), product.getPrice());

    private final Predicate<Product> isCheap = product -> product.getPrice() < 20.0;

    private final Function<Product, ProductDTO> productToDto = product -> new ProductDTO(product.getName(), product.getPrice());

    //Cache con ConcurrentHashMap para evitar consultas repetitivas
    private final Map<Long, ProductDTO> productCache = new ConcurrentHashMap<>();

    //Cola para manejar productos recientes FIFO
    private final Queue<Product> recentProducts = new LinkedList<>();

    //Mantiene productos ordenados por precio en tiempo real
    private final NavigableSet<Product> sortedProducts = new TreeSet<>(Comparator.comparing(Product::getPrice));

    //Lista enlazada para historico de modificaciones
    private final Deque<Product> modifiedProducts = new LinkedList<>();

    //Set para evitar productos duplicados por nombre
    private final Set<String> uniqueProductName = new HashSet<>();

    /*public List<ProductDTO> getAllProducts(){
        List<Product> products = productRepository.findAll();

        if(products.isEmpty()){
            System.out.println("No hay productos en la base de datos");
            products = defaultProductSupplier.get();
        }

        return products.stream()
                .map(this.productToDto)
                .toList();
    }

    public ProductDTO addProduct(ProductDTO productDTO){
        Product product = new Product();
        product.setName(product.getName());
        product.setPrice(product.getPrice());

        productRepository.save(product);
        logProduct.accept(product);

        //agregando el producto a las estructuras de datos
        sortedProducts.add(product);
        recentProducts.add(product);
        modifiedProducts.push(product);
        productCache.putIfAbsent(product.getId(), productDTO);

        if(recentProducts.size() > 5){
            recentProducts.poll();
        }

        return productDTO;

    }

    public List<ProductDTO> getSortedProducts(){
        if(sortedProducts.isEmpty()){
            sortedProducts.addAll(productRepository.findAll());
        }

        return sortedProducts.stream()
                .map(this.productToDto)
                .toList();
    }

    public List<ProductDTO> getRecentProducts(Integer limit) {

        List<Product> recentList = new ArrayList<>(recentProducts);

        if(recentList.isEmpty()){
            recentList = productRepository.findAll()
                    .stream()
                    .sorted(Comparator.comparing(Product::getId).reversed())
                    .limit(limit)
                    .toList();
        }

        return recentList.stream()
                .map(this.productToDto)
                .toList();
    }

    public List<ProductDTO> getModifiedProducts(Integer limit) {
        return modifiedProducts.stream()
                .limit(limit)
                .map(this.productToDto)
                .toList();
    }

    //agrupando productos por precio
    public Map<Double, List<ProductDTO>> groupByPrice(){
        return productRepository.findAll()
                .stream()
                .map(this.productToDto)
                .collect(Collectors.groupingBy(ProductDTO::price));
    }

    //obtener el min y max

    public Map<String, ProductDTO> getMinMaxProductsPrice(){

        return Map.of(
                "min", Objects.requireNonNull(productRepository.findAll()
                        .stream()
                        .min(Comparator.comparing(Product::getPrice))
                                .map(this.productToDto).orElse(null)
                        ),
                "max", Objects.requireNonNull(productRepository.findAll()
                        .stream()
                        .max(Comparator.comparing(Product::getPrice))
                        .map(this.productToDto).orElse(null)
                )
        );
    }

    //Obtener los productos baratos
    public List<ProductDTO> getCheapProducts(){
        return productRepository.findAll()
                .stream()
                .filter(isCheap)
                .map(this.productToDto)
                .toList();
    } */

    // Flux / Obtiene los productos - map - defaultIfEmpty - switchIfEmpty

    public Flux<ProductDTO> getAllProductsFlux(){
        return
                         productRepository.findAll()
                        .map(productToDto)
                        .switchIfEmpty(Flux.fromIterable(defaultProductSupplier.get())
                        .map(productToDto))
                        .delayElements(Duration.ofMillis(500));

    }

    public Mono<ProductDTO> getProductById(Long id){
        return productRepository.findById(id)
                .flatMap(product -> Mono.just(productToDto.apply(product)))
                .doOnError(error -> System.out.printf("Error al buscar el producto: %s", error.getMessage()))
                .defaultIfEmpty(new ProductDTO("Producto no encontrado", 0.0));
    }

    //agregamos un nuevo producto - doOnNext
    public Mono<ProductDTO> addProduct(ProductDTO productDTO){

        return Mono.fromSupplier(() -> {
            Product product = new Product();
            product.setName(productDTO.name());
            product.setPrice(productDTO.price());
            productRepository.save(product);
            return product;
        }).doOnNext(logProduct)
                .map(productToDto);
    }

    //obtener los productos baratos / isCheap

    public Flux<ProductDTO> getCheapProducts(){
        return productRepository.findAll()
                .filter(isCheap)
                .map(productToDto);
    }

    //Obtener los ultimos 3 agregados
    public Flux<ProductDTO> getLastProducts(){
        return productRepository.findAll()
                .take(3)
                .map(productToDto);
    }

    //eliminar por id  - doOnError
    public Mono<Void> deleteProduct(Long id){

        return productRepository.deleteById(id)
                .doOnError(error -> System.out.printf("Error al eliminar el producto: %s", error.getMessage()))
                .then();

/*        return productRepository.findById(id)
                .flatMap(product -> productRepository.delete(product))
                .doOnError(error -> System.out.printf("Error al eliminar el producto: %s", error.getMessage()))
                .then();*/
    }

    //contar productos - collectList

    public Mono<Long> countProducts(){
        return productRepository.count();
    }
}