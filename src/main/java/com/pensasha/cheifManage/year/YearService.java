package com.pensasha.cheifManage.year;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YearService {
    
    @Autowired YearRepository yearRepository;

    //Create a year
    public Year saveYear(Year year){
        return yearRepository.save(year);
    }

    //Get year
    public Year getYear(Short year){
        return yearRepository.findById(year).get();
    }

    //Does Year exist
    public Boolean doesYearExist(Short y){
        return yearRepository.existsById(y);
    }

    //Getting all years
    public List<Year> getAllYears(){
        return yearRepository.findAll();
    }

}