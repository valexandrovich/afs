package ua.com.valexa.db.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(schema = "sys", name = "test_entity")
@Data
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String field;

}
