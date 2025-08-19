package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.ServiceCatalog;
import com.timatix.servicebooking.repository.ServiceCatalogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ServiceCatalogService {

    private final ServiceCatalogRepository serviceCatalogRepository;

    public List<ServiceCatalog> getAllServices() {
        return serviceCatalogRepository.findAll();
    }

    public List<ServiceCatalog> getActiveServices() {
        return serviceCatalogRepository.findByIsActiveTrue();
    }

    public Optional<ServiceCatalog> getServiceById(Long id) {
        return serviceCatalogRepository.findById(id);
    }

    public List<ServiceCatalog> searchServicesByName(String name) {
        return serviceCatalogRepository.findByNameContainingIgnoreCase(name);
    }

    public ServiceCatalog createService(ServiceCatalog service) {
        validateServiceForCreation(service);

        // Set default values
        if (service.getIsActive() == null) {
            service.setIsActive(true);
        }

        log.info("Creating new service: {}", service.getName());
        return serviceCatalogRepository.save(service);
    }

    public ServiceCatalog updateService(Long id, ServiceCatalog serviceDetails) {
        ServiceCatalog service = serviceCatalogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));

        service.setName(serviceDetails.getName());
        service.setDescription(serviceDetails.getDescription());
        service.setBasePrice(serviceDetails.getBasePrice());
        service.setEstimatedDurationMinutes(serviceDetails.getEstimatedDurationMinutes());

        if (serviceDetails.getIsActive() != null) {
            service.setIsActive(serviceDetails.getIsActive());
        }

        log.info("Updating service with id: {}", id);
        return serviceCatalogRepository.save(service);
    }

    public ServiceCatalog toggleServiceStatus(Long id) {
        ServiceCatalog service = serviceCatalogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));

        service.setIsActive(!service.getIsActive());

        log.info("Toggling service {} status to: {}", id, service.getIsActive());
        return serviceCatalogRepository.save(service);
    }

    public void deleteService(Long id) {
        ServiceCatalog service = serviceCatalogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));

        // Check if service has any associated requests before deletion
        if (!service.getServiceRequests().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete service that has associated service requests. Deactivate it instead.");
        }

        log.info("Deleting service with id: {}", id);
        serviceCatalogRepository.deleteById(id);
    }

    private void validateServiceForCreation(ServiceCatalog service) {
        if (service.getName() == null || service.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Service name is required");
        }

        if (service.getBasePrice() != null && service.getBasePrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Base price cannot be negative");
        }

        if (service.getEstimatedDurationMinutes() != null && service.getEstimatedDurationMinutes() < 0) {
            throw new IllegalArgumentException("Estimated duration cannot be negative");
        }
    }
}