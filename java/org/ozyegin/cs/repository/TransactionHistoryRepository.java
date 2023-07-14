package org.ozyegin.cs.repository;

import java.sql.Date;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import org.ozyegin.cs.entity.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionHistoryRepository extends JdbcDaoSupport {
  private final RowMapper<Pair> pairMapper = (resultSet, i) -> new Pair(
      resultSet.getString(1),
      resultSet.getInt(2)
  );

  private final RowMapper<String> stringMapper = (resultSet, i) -> resultSet.getString(1);

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  public List<Pair> query1() {
    //The system shall track the list of product IDs ordered the most for each company.
  //  String sql = "SELECT company_name, product_id, SUM(amount) as total FROM transaction_history GROUP BY company_name, product_id ORDER BY total DESC";
    String sql = "SELECT company_name, product_id FROM " +
            "(SELECT company_name, product_id, RANK() OVER (PARTITION BY company_name ORDER BY SUM(amount) DESC) as rank " +
            "FROM transaction_history GROUP BY company_name, product_id) subquery " +
            "WHERE rank = 1";
    return Objects.requireNonNull(getJdbcTemplate()).query(sql, pairMapper);
  }

  public List<String> query2(Date start, Date end) {
    //The system shall track the inactive companies (i.e., their name) for a given time period. E.g., the companies which does not have any order between given two dates.
    String sql = "SELECT name FROM company WHERE name NOT IN (SELECT company_name FROM transaction_history WHERE created_date BETWEEN ? AND ?)";
    return Objects.requireNonNull(getJdbcTemplate()).query(sql, new Object[] {start, end}, stringMapper);
  }

  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM transaction");
  }
}
