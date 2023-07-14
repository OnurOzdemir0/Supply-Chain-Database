package org.ozyegin.cs.repository;

import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import org.ozyegin.cs.entity.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyRepository extends JdbcDaoSupport {

  final String createPS = "INSERT INTO company (name, country, city, zip_number, street_info, phone_number, e_mails) VALUES(?,?,?,?,?,?,?) RETURNING id";
  final String findByNamePS = "SELECT * FROM company WHERE name=?";
  final String findByCountryPS = "SELECT * FROM company WHERE country=?";
  final String deletePS = "DELETE FROM company WHERE name=?";
  final String deleteAllPS = "DELETE FROM company";



  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  private final RowMapper<Company> companyRowMapper = (resultSet, i) -> {
    Company company = new Company();
    company.setId(resultSet.getInt("id"));
    company.setName(resultSet.getString("name"));
    company.setCountry(resultSet.getString("country"));
    company.setCity(resultSet.getString("city"));
    company.setZip(resultSet.getInt("zip_number"));
    company.setStreetInfo(resultSet.getString("street_info"));
    company.setPhoneNumber(resultSet.getString("phone_number"));
    java.sql.Array sqlArray = resultSet.getArray("e_mails");
    company.setE_mails(sqlArray == null ? null : List.of((String[]) sqlArray.getArray()));

    return company;
  };


  public String create(Company company) {
    int id = Objects.requireNonNull(getJdbcTemplate()).queryForObject(createPS, Integer.class, company.getName(), company.getCountry(), company.getCity(), company.getZip(), company.getStreetInfo(), company.getPhoneNumber(), company.getE_mails().toArray(new String[0]));
    company.setId(id);
    return company.getName();
}

  public Company find(String name) {
    List<Company> companies = Objects.requireNonNull(getJdbcTemplate()).query(findByNamePS, new Object[]{name}, companyRowMapper);
    if (companies.isEmpty()) {
      throw new EmptyResultDataAccessException(1);
    } else {
      return companies.get(0);
    }
  }



  public List<Company> findByCountry(String country) {
    List<Company> companies = Objects.requireNonNull(getJdbcTemplate()).query(findByCountryPS, new Object[]{country}, companyRowMapper);
    return companies;
  }


  public String delete(String name) {
    int rowsAffected = Objects.requireNonNull(getJdbcTemplate()).update(deletePS, name);
    return rowsAffected > 0 ? name : null;
  }


  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).update(deleteAllPS);
  }

}


