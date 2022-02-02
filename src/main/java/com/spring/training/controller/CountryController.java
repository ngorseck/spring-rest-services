package com.spring.training.controller;

import com.spring.training.model.Country;
import com.spring.training.service.CountryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("countries")
@AllArgsConstructor
public class CountryController {

    private final CountryService countryService;

    @GetMapping(produces = "application/json")
    public List<Country> getCountries() {
        return countryService.getCountries();
    }

    @GetMapping(path="{name}", produces = "application/json")
    public Country getCountry(@PathVariable("name") String name) {
        return countryService.getCountry(name);
    }

}