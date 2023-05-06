package org.ozyegin.cs.repository;

import java.util.Date;
import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepository extends JdbcDaoSupport {
  private static final String insertOrderPS = "INSERT INTO transaction (company_name, product_id, amount, created_date) VALUES (?, ?, ?, ?) RETURNING id";
  private static final String selectProduceCapacityPS = "SELECT capacity FROM produce WHERE company_name = ? AND product_id = ?";
    private static final String deleteTransactionByIdPS = "DELETE FROM transaction WHERE id = ?";
    private static final String deleteAllTransactionPS = "DELETE FROM transaction";

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  public Integer order(String company, int product, int amount, Date createdDate) {
    Integer produceCapacity = Objects.requireNonNull(getJdbcTemplate())
            .queryForObject(selectProduceCapacityPS, Integer.class, company, product);
    Integer id = Objects.requireNonNull(getJdbcTemplate())
            .queryForObject(insertOrderPS, Integer.class, company, product, amount, createdDate);

    if (produceCapacity != null && produceCapacity >= amount) {
        return id;
    }
    else {
        return null;
    }
  }

  public void delete(int transactionId) throws Exception {
  int rowsAffected = Objects.requireNonNull(getJdbcTemplate()).update(deleteTransactionByIdPS, transactionId);
    if (rowsAffected == 0) {
        throw new Exception("Onur's Error - No transaction with id " + transactionId); //most tests expected this call
    }
  }

  public void deleteAll() {
    getJdbcTemplate().update(deleteAllTransactionPS);
  }
}

