package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    User save(User user) throws SQLException;
    Optional<User> findById(Long id) throws SQLException;
    Optional<User> findByEmail(String email) throws SQLException;
    List<User> findAll() throws SQLException;
    boolean existsByEmail(String email) throws SQLException;
    boolean update(User user) throws SQLException;
}
