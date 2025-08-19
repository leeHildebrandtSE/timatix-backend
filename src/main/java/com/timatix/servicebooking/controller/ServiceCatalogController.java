package com.timatix.servicebooking.controller;

import com.timatix.servicebooking.model.ServiceCatalog;
import com.timatix.servicebooking.service.ServiceCatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServiceCatalogController {

    private final ServiceCatalogService serviceCatalogService;

    @GetMapping
    public ResponseEntity<List<ServiceCatalog>> getAllServices() {
        List<ServiceCatalog> services = serviceCatalogService.getAllServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ServiceCatalog>> getActiveServices() {
        List<ServiceCatalog> services = serviceCatalogService.getActiveServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceCatalog> getServiceById(@PathVariable Long id) {
        Optional<ServiceCatalog> service = serviceCatalogService.getServiceById(id);
        return service.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ServiceCatalog>> searchServicesByName(@RequestParam String name) {
        List<ServiceCatalog> services = serviceCatalogService.searchServicesByName(name);
        return ResponseEntity.ok(services);
    }

    @PostMapping
    public ResponseEntity<?> createService(@RequestBody ServiceCatalog service) {
        try {
            ServiceCatalog savedService = serviceCatalogService.createService(service);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedService);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error creating service", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable Long id, @RequestBody ServiceCatalog serviceDetails) {
        try {
            ServiceCatalog updatedService = serviceCatalogService.updateService(id, serviceDetails);
            return ResponseEntity.ok(updatedService);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating service", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleServiceStatus(@PathVariable Long id) {
        try {
            ServiceCatalog service = serviceCatalogService.toggleServiceStatus(id);
            return ResponseEntity.ok(service);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error toggling service status", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        try {
            serviceCatalogService.deleteService(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Service deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting service", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}