package com.timatix.servicebooking.service;

import com.timatix.servicebooking.model.VehicleMake;
import com.timatix.servicebooking.repository.VehicleMakeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleMakeService {

    private final VehicleMakeRepository vehicleMakeRepository;

    public List<VehicleMake> getAllMakes() {
        return vehicleMakeRepository.findAll();
    }
}
