package com.cabbooking.service;

import com.cabbooking.model.Cab;
import java.util.List;

public interface ICabService {
    Cab insertCab(Cab cab);
    Cab updateCab(Cab cab);
    Cab deleteCab(int cabId);
    List<Cab> viewCabsOfType(String carType);
    int countCabsOfType(String carType);
}