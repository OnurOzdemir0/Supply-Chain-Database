package org.ozyegin.cs.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.ozyegin.cs.entity.Company;
import org.ozyegin.cs.entity.Product;
import org.ozyegin.cs.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@CrossOrigin
public class ProductController {
  @Autowired
  private ProductService productService;

  @Autowired
  private PlatformTransactionManager transactionManager;

  @RequestMapping(produces = "application/json", method = RequestMethod.POST)
  public ResponseEntity create(@RequestBody List<Product> products) {
    TransactionDefinition txDef = new DefaultTransactionDefinition();
    TransactionStatus txStatus = transactionManager.getTransaction(txDef);
    try {
      List<Integer> ids = productService.create(products);
      transactionManager.commit(txStatus);
      return new ResponseEntity<>(ids, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      // If a product name is null, roll back the transaction and return an empty list of ids
      transactionManager.rollback(txStatus);
      return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    } catch (Exception e) {
      transactionManager.rollback(txStatus);
      return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
    }
  }

  @RequestMapping(produces = "application/json", method = RequestMethod.PUT)
  public ResponseEntity update(@RequestBody List<Product> products) {
    TransactionDefinition txDef = new DefaultTransactionDefinition();
    TransactionStatus txStatus = transactionManager.getTransaction(txDef);
    try {
      productService.update(products);
      transactionManager.commit(txStatus);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      transactionManager.rollback(txStatus);
      return new ResponseEntity<>(null, null, HttpStatus.NOT_ACCEPTABLE);
    }
  }
}