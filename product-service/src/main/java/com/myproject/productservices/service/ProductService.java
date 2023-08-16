package com.myproject.productservices.service;

import com.myproject.productservices.dto.ProductRequest;
import com.myproject.productservices.dto.ProductResponse;
import com.myproject.productservices.model.Product;
import com.myproject.productservices.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest){
        Product product= Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
        log.info("product {} is saved ", product.getId());
    }

    public List<ProductResponse> getAllProduct(){
        List<Product> products= productRepository.findAll();

        return  products.stream().map(this::mapTOProductResponsse)
                .toList();
    }

    public ProductResponse mapTOProductResponsse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
