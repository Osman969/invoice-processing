package it.arrive.invoicesystem.lineitem.repository;

import it.arrive.invoicesystem.lineitem.model.LineItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface LineItemRepository extends JpaRepository<LineItem, Long> {

    Optional<LineItem> findBySku( String sku );

    @Modifying
    @Transactional
    @Query ( "UPDATE LineItem li SET li.quantity = li.quantity - :decrement " +
            "WHERE li.sku = :sku AND li.quantity >= :decrement" )
    int decrementQuantityBySku( @Param ( "sku" ) String sku, @Param ( "decrement" ) BigDecimal decrement );
}
