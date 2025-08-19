package com.timatix.servicebooking.repository;

import com.timatix.servicebooking.model.ServiceCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, Long> {

    List<ServiceCatalog> findByIsActiveTrue();

    List<ServiceCatalog> findByIsActiveFalse();

    List<ServiceCatalog> findByNameContainingIgnoreCase(String name);

    @Query("SELECT sc FROM ServiceCatalog sc WHERE sc.isActive = true AND sc.name ILIKE %:name%")
    List<ServiceCatalog> findActiveServicesByNameContaining(@Param("name") String name);

    @Query("SELECT sc FROM ServiceCatalog sc WHERE sc.basePrice BETWEEN :minPrice AND :maxPrice")
    List<ServiceCatalog> findByBasePriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT sc FROM ServiceCatalog sc WHERE sc.isActive = true AND sc.basePrice BETWEEN :minPrice AND :maxPrice")
    List<ServiceCatalog> findActiveServicesByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT sc FROM ServiceCatalog sc WHERE sc.estimatedDurationMinutes <= :maxDuration")
    List<ServiceCatalog> findByEstimatedDurationLessThanEqual(@Param("maxDuration") Integer maxDuration);

    @Query("SELECT sc FROM ServiceCatalog sc ORDER BY sc.name ASC")
    List<ServiceCatalog> findAllOrderByName();

    @Query("SELECT sc FROM ServiceCatalog sc WHERE sc.isActive = true ORDER BY sc.basePrice ASC")
    List<ServiceCatalog> findActiveServicesOrderByPrice();
}