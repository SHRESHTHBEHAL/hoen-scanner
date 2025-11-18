package com.skyscanner;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HoenScannerApplication extends Application<HoenScannerConfiguration> {

    private List<SearchResult> searchResults = new ArrayList<>();

    public static void main(final String[] args) throws Exception {
        new HoenScannerApplication().run(args);
    }

    @Override
    public String getName() {
        return "hoen-scanner";
    }

    @Override
    public void initialize(final Bootstrap<HoenScannerConfiguration> bootstrap) {

    }

    @Override
    public void run(final HoenScannerConfiguration configuration, final Environment environment) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Load hotels
            InputStream hotelsStream = getClass().getClassLoader().getResourceAsStream("hotels.json");
            List<SearchResult> hotels = objectMapper.readValue(hotelsStream, new TypeReference<List<SearchResult>>() {});
            hotels.forEach(hotel -> hotel.setKind("hotel"));

            // Load rental cars
            InputStream carsStream = getClass().getClassLoader().getResourceAsStream("rental_cars.json");
            List<SearchResult> cars = objectMapper.readValue(carsStream, new TypeReference<List<SearchResult>>() {});
            cars.forEach(car -> car.setKind("rental_car"));

            // Merge the lists
            searchResults.addAll(hotels);
            searchResults.addAll(cars);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load JSON data", e);
        }

        // Register the search resource
        environment.jersey().register(new SearchResource(searchResults));
    }

    public List<SearchResult> getSearchResults() {
        return searchResults;
    }

}
