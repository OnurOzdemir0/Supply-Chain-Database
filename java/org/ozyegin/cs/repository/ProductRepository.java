package org.ozyegin.cs.repository;

import java.util.*;
import javax.sql.DataSource;
import org.ozyegin.cs.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository extends JdbcDaoSupport {

  final String createPS = "INSERT INTO product (name, description, brand_name) VALUES(?,?,?) RETURNING id";
  final String updatePS = "UPDATE product SET name=?, description=?, brand_name=? WHERE id=?";
  final String getSinglePS = "SELECT * FROM product WHERE id=?";
  final String deleteAllPS = "DELETE FROM product";
  final String deletePS = "DELETE FROM product WHERE id=?";

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  private final RowMapper<Product> productRowMapper = (resultSet, i) -> {
    Product product = new Product();
    product.setId(resultSet.getInt("id"));
    product.setName(resultSet.getString("name"));
    product.setDescription(resultSet.getString("description"));
    product.setBrandName(resultSet.getString("brand_name"));
    return product;
  };

  public Product find(int id) {
    List<Product> products = Objects.requireNonNull(getJdbcTemplate()).query(getSinglePS, new Object[]{id}, productRowMapper);

    return products.isEmpty() ? null : products.get(0);
  }

  public List<Product> findMultiple(List<Integer> ids) {
    if (ids == null || ids.isEmpty()) {
      return new ArrayList<>();
    } else {
      String findMultiplePS = "SELECT * FROM product WHERE id IN (:ids)";
      Map<String, List<Integer>> params = new HashMap<>() {
        {
          this.put("ids", new ArrayList<>(ids));
        }
      };

      var template = new NamedParameterJdbcTemplate(Objects.requireNonNull(getJdbcTemplate()));
      return template.query(findMultiplePS, params, productRowMapper);
    }
  }

  public List<Product> findByBrandName(String brandName) {
    String findByBrandNamePS = "SELECT * FROM product WHERE brand_name = ?";
    List<Product> products = Objects.requireNonNull(getJdbcTemplate()).query(findByBrandNamePS, new Object[]{brandName}, productRowMapper);

    return products;
  }

  public List<Integer> create(List<Product> products) {
    //createPS = "INSERT INTO product (name, description, brand_name) VALUES(?,?,?) RETURNING id";
    List<Integer> generatedIds = new ArrayList<>();

    for (Product product : products) {
      int id = getJdbcTemplate().queryForObject(createPS, Integer.class, product.getName(), product.getDescription(), product.getBrandName());
      generatedIds.add(id);
    }

    return generatedIds;
  }

  public void update(List<Product> products) {
    for (Product product : products) {
      getJdbcTemplate().update(updatePS, product.getName(), product.getDescription(), product.getBrandName(), product.getId());
    }
  }

  public void delete(List<Integer> ids) {
    Objects.requireNonNull(getJdbcTemplate()).batchUpdate(deletePS, ids, ids.size(),
            (ps, id) -> ps.setInt(1, id));
  }

  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).update(deleteAllPS);
  }
}
