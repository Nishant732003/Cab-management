package com.cabbooking.repository;

import com.cabbooking.model.Cab;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CabRepository extends JpaRepository<Cab, Integer> {
}
