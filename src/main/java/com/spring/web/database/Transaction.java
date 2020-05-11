package com.spring.web.database;

import com.spring.web.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;

@Repository
public class Transaction implements TransactionInterface {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean checkEmail(String email) {
        String sql = "SELECT COUNT(email) FROM accounts WHERE email = ?;";
        int bit = this.jdbcTemplate.queryForObject(sql, Integer.class, email);
        return bit == 1;
    }

    @Override
    public void registerAccount(Account account) {
        // mysql
        // String sql =
        // "INSERT INTO accounts (name, surname, birthday, age, favoriteColor, email, password) "+
        // "VALUES (?, ?, ?, ?, ?, ?, SHA2(?, '256'));";

        // postgresql
        String sql =
        "INSERT INTO accounts (name, surname, birthday, age, favoriteColor, email, password) "+
        "VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, DIGEST(?, 'sha256'));";
        this.jdbcTemplate.update(
            sql,
            account.getName(),
            account.getSurname(),
            account.getBirthday(),
            account.getAge(),
            account.getFavoriteColor(),
            account.getEmail(),
            account.getPassword());
    }

    @Override
    public void updateAccount(int id, Account account) {
        // mysql
        // String sql =
        // "UPDATE accounts SET name = ?, surname = ?, email = ?, birthday = STR_TO_DATE(?, '%d/%m/%Y'), age = ?, favoriteColor = ? "+
        // "WHERE id = ?;";

        // postgresql
        String sql =
        "UPDATE accounts SET name = ?, surname = ?, email = ?, birthday = TO_DATE(?, 'DD/MM/YYYY'), age = ?, favoriteColor = ? "+
        "WHERE id = ?;";

        this.jdbcTemplate.update(sql,
            account.getName(),
            account.getSurname(),
            account.getEmail(),
            account.getBirthday(),
            account.getAge(),
            account.getFavoriteColor(), id);
    }

    @Override
    public void updateAccountPassword(int id, String password) {
        // mysql
        // String sql = "UPDATE accounts SET password = SHA2(?, '256') WHERE id = ?;";

        // postgresql
        String sql = "UPDATE accounts SET password = DIGEST(?, 'sha256') WHERE id = ?;";
        this.jdbcTemplate.update(sql, password, id);
    }

    @Override
    public Account getAccount(String email, String password) {
        // mysql
        // String sql = "SELECT id, name, surname, birthday, age, favoriteColor, email "+
        // "FROM accounts WHERE email = ? AND SHA2(?, '256') = password;";

        // postgresql
        String sql = "SELECT id, name, surname, birthday, age, favoriteColor, email "+
        "FROM accounts WHERE email = ? AND DIGEST(?, 'sha256') = password;";

        try {
            Map<String, Object> resultSet = this.jdbcTemplate.queryForMap(sql, email, password);
            Account account = new Account();

            account.setId((Integer) resultSet.get("id"));
            account.setName((String) resultSet.get("name"));
            account.setSurname((String) resultSet.get("surname"));
            account.setBirthday(new SimpleDateFormat("dd/MM/yyyy").format((Date) resultSet.get("birthday")));
            account.setAge((Integer) resultSet.get("age"));
            account.setFavoriteColor((String) resultSet.get("favoriteColor"));
            account.setEmail((String) resultSet.get("email"));

            return account;
        }
        catch(EmptyResultDataAccessException erdae) {
            return null;
        }
    }
}
