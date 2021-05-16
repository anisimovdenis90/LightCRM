package ru.lightcrm.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * Содержит общий для всех сущностей id
 * Нужно, чтобы с помощью рефлекшена заполнить SearchItemDto
 */
@Data
@MappedSuperclass
@NoArgsConstructor
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @CreationTimestamp
    @Column(name = "create_timestamp")
    private OffsetDateTime createTimeStamp;

    @UpdateTimestamp
    @Column(name = "update_timestamp", updatable = false)
    private OffsetDateTime updateTimeStamp;

    public BaseEntity(Long id) {
        this.id = id;
    }
}
