package org.ozyegin.cs.service;

import java.util.List;
import org.ozyegin.cs.entity.Product;
import org.ozyegin.cs.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
  @Autowired
  private ProductRepository productRepository;

  public List<Integer> create(List<Product> products) {
    for (Product product : products) {
      if (product.getName() == null) {
        throw new IllegalArgumentException("Product name cannot be null");
      }
    }
    return productRepository.create(products);
  }

  public void update(List<Product> products) {
    productRepository.update(products);
  }
}
