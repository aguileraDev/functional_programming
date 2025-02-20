package com.practice.fuctional.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Comparable<Product> {


    private Long id;

    private String name;
    Double price;

    public Product(Double price, String name) {
        this.price = price;
        this.name = name;
    }

    @Override
    public int compareTo(Product other) {
        return Double.compare(this.price, other.price);
    }
}
