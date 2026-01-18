package com.springdb.example.repository;

import com.springdb.example.entities.CarEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.repository.type", havingValue = "jdbc")
public class CarJdbcRepository implements IRepository<CarEntity> {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<CarEntity> carRowMapper = (rs, rowNum) -> {
        CarEntity car = new CarEntity();
        car.setId(rs.getLong("id"));
        car.setBrand(rs.getString("brand"));
        car.setModel(rs.getString("model"));
        car.setSeatingCapacity(rs.getInt("seating_capacity"));
        return car;
    };

    @Override
    public List<CarEntity> findAll() {
        String sql = "SELECT v.id, v.brand, v.model, c.seating_capacity " +
                "FROM vehicle_entity v JOIN car_entity c ON v.id = c.id";
        System.out.println("JDBC");
        return jdbcTemplate.query(sql, carRowMapper);
    }

    @Override
    public Optional<CarEntity> findById(Long id) {
        String sql = "SELECT v.id, v.brand, v.model, c.seating_capacity " +
                "FROM vehicle_entity v JOIN car_entity c ON v.id = c.id WHERE v.id = ?";
        return jdbcTemplate.query(sql, carRowMapper, id).stream().findFirst();
    }

    @Override
    @Transactional
    public CarEntity save(CarEntity car) {
        if (car.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO vehicle_entity (brand, model) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, car.getBrand());
                ps.setString(2, car.getModel());
                return ps;
            }, keyHolder);

            long newId = (long) keyHolder.getKeys().get("id");
            car.setId(newId);

            jdbcTemplate.update("INSERT INTO car_entity (id, seating_capacity) VALUES (?, ?)",
                    newId, car.getSeatingCapacity());
        } else {
            jdbcTemplate.update("UPDATE vehicle_entity SET brand = ?, model = ? WHERE id = ?",
                    car.getBrand(), car.getModel(), car.getId());
            jdbcTemplate.update("UPDATE car_entity SET seating_capacity = ? WHERE id = ?",
                    car.getSeatingCapacity(), car.getId());
        }
        return car;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM car_entity WHERE id = ?", id);
        jdbcTemplate.update("DELETE FROM vehicle_entity WHERE id = ?", id);
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM car_entity WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public List<CarEntity> findByBrand(String brand) {
        String sql = "SELECT v.id, v.brand, v.model, c.seating_capacity " +
                "FROM vehicle_entity v JOIN car_entity c ON v.id = c.id WHERE v.brand = ?";
        return jdbcTemplate.query(sql, carRowMapper, brand);
    }
}