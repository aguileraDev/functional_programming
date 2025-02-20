package com.practice.fuctional.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product  {

    @BsonId
    private String id;

    private String name;
    Double price;

    public Product(Double price, String name) {
        this.price = price;
        this.name = name;
    }


}
