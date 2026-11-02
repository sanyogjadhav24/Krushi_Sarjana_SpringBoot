package me.sanyogjadhav.productcatlogservice.repository;

import me.sanyogjadhav.productcatlogservice.model.Product;
import me.sanyogjadhav.productcatlogservice.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    //find by user (perticular user)
    List<Product> findByCreatedBy(Long createdBy);

    //find by role
    List<Product> findByCreatedByRole(UserRole createdByRole);


    //find by paticular user with particular role

    List<Product> findByCreatedByAndCreatedByRole(
            Long createdBy,
            UserRole createdByRole
    );


    //find product belongs toperticuilar category

    List<Product> findByCategory_Id(Long categoryId);

    List<Product> findByCategory_IdAndCreatedByRole(
            Long categoryId,
            UserRole createdByRole
    );

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("""
    UPDATE Product p
    SET p.quantity = p.quantity - :quantity
    WHERE p.id = :productId
    AND p.quantity >= :quantity
""")
    int reserveStock(
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity
    );


    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("""
    UPDATE Product p
    SET p.quantity = p.quantity + :quantity
    WHERE p.id = :productId
""")
    int releaseStock(
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity
    );


}
