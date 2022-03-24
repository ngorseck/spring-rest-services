package com.spring.training.service;

import com.spring.training.entity.CountryEntity;
import com.spring.training.exception.EntityNotFoundException;
import com.spring.training.exception.RequestException;
import com.spring.training.domain.Country;
import com.spring.training.repository.CountryRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@CacheConfig(cacheNames = "countries")
@AllArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public List<Country> getCountries() {
        return StreamSupport.stream(countryRepository.findAll().spliterator(), false)
                .map(CountryEntity::toCountry)
                .collect(Collectors.toList());
    }

    @Cacheable(key = "#name")
    public Country getCountry(String name) {
        return countryRepository.findByNameIgnoreCase(name).orElseThrow(() ->
                new EntityNotFoundException(String.format("country not found with name = %s", name)))
                .toCountry();
    }

    public Country createCountry(Country country) {
       countryRepository.findByNameIgnoreCase(country.getName())
                .ifPresent(entity -> {
                    throw new RequestException(String.format("the country with name %s is already created", entity.getName()),
                            HttpStatus.CONFLICT);
                });
       return countryRepository.save(CountryEntity.fromCountry(country)).toCountry();
    }

    @CachePut(key = "#name")
    public Country updateCountry(String name, Country country) {
        return countryRepository.findByNameIgnoreCase(name)
                .map(entity -> {
                    country.setName(name);
                    return countryRepository.save(CountryEntity.fromCountry(country)).toCountry();
                }).orElseThrow(() -> new EntityNotFoundException(String.format("country not found with name = %s", name)));
    }

    @CacheEvict(key = "#name")
    public void deleteCountry(String name) {
        if(countryRepository.existsById(name)) {
            try {
                countryRepository.deleteById(name);
            } catch (Exception e) {
                throw new RequestException(String.format("the country with name %s cannot be deleted", name),
                        HttpStatus.CONFLICT);
            }
        }
    }

}