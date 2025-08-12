package com.cabbooking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cabbooking.model.Cab;
import com.cabbooking.repository.CabRepository;

@Service
public class CabServiceImpl implements ICabService {

    @Autowired
    private CabRepository cabRepository;

    @Override
    public Cab insertCab(Cab cab) {
        return cabRepository.save(cab);
    }

    @Override
    public Cab updateCab(Cab cab) {
        // findById returns an Optional, so we need to handle the case where the cab might not exist
        return cabRepository.findById(cab.getCabId())
                .map(existingCab -> cabRepository.save(cab))
                .orElseThrow(() -> new IllegalArgumentException("Cab with id " + cab.getCabId() + " not found"));
    }

    @Override
    public Cab deleteCab(int cabId) {
        Cab cab = cabRepository.findById(cabId)
                .orElseThrow(() -> new IllegalArgumentException("Cab with id " + cabId + " not found"));
        cabRepository.delete(cab);
        return cab;
    }

    @Override
    public List<Cab> viewCabsOfType(String carType) {
        return cabRepository.findByCarType(carType);
    }

    @Override
    public int countCabsOfType(String carType) {
        return cabRepository.findByCarType(carType).size();
    }
}
