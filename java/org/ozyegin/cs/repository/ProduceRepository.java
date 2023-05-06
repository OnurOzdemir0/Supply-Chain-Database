package org.ozyegin.cs.repository;


import javax.sql.DataSource;

import org.ozyegin.cs.entity.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class ProduceRepository extends JdbcDaoSupport {

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  private static final String insertProducePS = "INSERT INTO produce (company_name, product_id, capacity) VALUES (?, ?, ?) RETURNING id";
  private static final String deleteProduceByIdPS = "DELETE FROM produce WHERE id = ?";
  private static final String deleteAllProducePS = "DELETE FROM produce";

  public Integer produce(String company, int product, int capacity) {
    int id = Objects.requireNonNull(getJdbcTemplate()).queryForObject(insertProducePS, Integer.class, company, product, capacity);
    return id;
  }


  public void delete(int produceId) throws Exception{
    int rowsAffected = Objects.requireNonNull(getJdbcTemplate()).update(deleteProduceByIdPS, produceId);
    if (rowsAffected == 0) {
      throw new Exception("Onur's Error - No produce with id " + produceId); //produceDeleteNotExist() expected this call
    }
  }

  public void deleteAll() {
    getJdbcTemplate().update(deleteAllProducePS);
  }
}
