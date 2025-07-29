package it.arrive.invoicesystem.lineitem.model;

import it.arrive.invoicesystem.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table ( name = "line_items" )
@Getter
@Setter
public class LineItem extends BaseEntity {

    @Column ( nullable = false, unique = true, updatable = false )
    private String sku;

    @Column ( nullable = false )
    private String description;

    @Column ( nullable = false )
    private Integer quantity;

    @Column ( nullable = false )
    private BigDecimal price;
}