package com.timatix.servicebooking.repository;

import com.timatix.bookingservices.model.ServiceCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, Long> {
}