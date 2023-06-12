package com.unipi.findoctor.dto;

import com.unipi.findoctor.models.Rating;
import com.unipi.findoctor.models.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class DoctorDetailsDto {
    private String afm;
    private User user;
    private LocalDate dateOfBirth;
    private String specialization;
    private String businessPhone;
    private String city;
    private String address;
    private String imageURL;

    private List<Rating> ratings;

    public double getAverageRating() {

        double average = ratings.stream()
                .mapToInt(rating -> rating.getRatingValue())
                .average()
                .orElse(0.0);

        return average;
    }

    public int ratingValuePercentage(int ratingValue) {

        double countWithRating = (int) ratings.stream()
                .filter(rating -> rating.getRatingValue() == ratingValue)
                .count();

        double totalCount = ratings.size();

        double percentage = countWithRating / totalCount * 100;

        return (int) percentage;
    }

    public String getFullName() {
        return "Dr. " + user.getName() + " " + user.getSurname();
    }
}