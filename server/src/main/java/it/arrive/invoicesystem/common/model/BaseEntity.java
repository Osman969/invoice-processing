package it.arrive.invoicesystem.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue ( strategy = GenerationType.UUID )
    @Column ( updatable = false, nullable = false )
    private UUID id;

    @CreationTimestamp
    @Column ( nullable = false, updatable = false )
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column ( nullable = false )
    private LocalDateTime lastUpdatedAt;

    @Version
    private Long version;
}