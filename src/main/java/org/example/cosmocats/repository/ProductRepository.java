package org.example.cosmocats.repository;

import org.example.cosmocats.domain.Product;
import org.example.cosmocats.web.projection.PopularProductProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    @Query("select p.productName as productName, " +
           "sum(ol.quantity) as timesOrdered " +
           "from OrderLine ol " +
           "join ol.product p " +
           "group by p.id, p.productName " +
           "order by sum(ol.quantity) desc")
    List<PopularProductProjection> findMostPopularProducts(Pageable pageable);
}
