package com.cabbooking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cabbooking.model.Cab;
import com.cabbooking.service.ICabService;

@RestController
@RequestMapping("/api/cabs")
@PreAuthorize("hasRole('Admin')")
public class CabController {

    @Autowired
    private ICabService cabService;

    @PostMapping("/add")
    public ResponseEntity<Cab> addCab(@RequestBody Cab cab) {
        Cab newCab = cabService.insertCab(cab);
        return ResponseEntity.ok(newCab);
    }

    @PutMapping("/update")
    public ResponseEntity<Cab> updateCab(@RequestBody Cab cab) {
        Cab updatedCab = cabService.updateCab(cab);
        return ResponseEntity.ok(updatedCab);
    }

    @DeleteMapping("/delete/{cabId}")
    public ResponseEntity<Cab> deleteCab(@PathVariable int cabId) {
        Cab deletedCab = cabService.deleteCab(cabId);
        return ResponseEntity.ok(deletedCab);
    }

    @GetMapping("/view/{carType}")
    public ResponseEntity<List<Cab>> viewCabsOfType(@PathVariable String carType) {
        List<Cab> cabs = cabService.viewCabsOfType(carType);
        return ResponseEntity.ok(cabs);
    }

    @GetMapping("/count/{carType}")
    public ResponseEntity<Integer> countCabsOfType(@PathVariable String carType) {
        int count = cabService.countCabsOfType(carType);
        return ResponseEntity.ok(count);
    }
}
