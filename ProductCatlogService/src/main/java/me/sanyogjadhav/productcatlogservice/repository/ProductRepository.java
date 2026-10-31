package me.sanyogjadhav.productcatlogservice.repository;

import me.sanyogjadhav.productcatlogservice.model.Product;
import me.sanyogjadhav.productcatlogservice.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
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

    List<Product> findByCategoryId(Long categoryId);


}
