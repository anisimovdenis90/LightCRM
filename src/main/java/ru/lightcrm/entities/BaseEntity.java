package ru.lightcrm.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Содержит общий для всех сущностей id
 * Нужно, чтобы с помощью рефлекшена заполнить SearchItemDto
 */
@Data
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

}
