package org.example.cosmocats.repository;

import org.example.cosmocats.repository.entity.ProductEntity;
import org.example.cosmocats.repository.projection.PopularProductProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByCategoryId(Long categoryId);

    @Query("select p.productName as productName, " +
           "sum(ol.quantity) as timesOrdered " +
           "from OrderLineEntity ol " +
           "join ol.product p " +
           "group by p.id, p.productName " +
           "order by sum(ol.quantity) desc")
    List<PopularProductProjection> findMostPopularProducts(Pageable pageable);
}