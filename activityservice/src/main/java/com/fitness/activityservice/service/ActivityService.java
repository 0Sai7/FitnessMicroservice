package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;

    private final KafkaTemplate<String, Activity> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicName;

    public ActivityResponse trackActivity(ActivityRequest activityRequest){

        Boolean isValidUser = userValidationService.validateUserProfile(activityRequest.getUserId());

        if (!isValidUser){
            throw new RuntimeException("Invalid user profile");
        }
        Activity activity = Activity.builder().userId(activityRequest.getUserId())
                .activityType(activityRequest.getActivityType())
                .caloriesBurned(activityRequest.getCaloriesBurned())
                .addtionalMetrics(activityRequest.getAddtionalMetrics())
                .duration(activityRequest.getDuration())
                .startTime(activityRequest.getStartTime()).build();

        Activity activitySaved = activityRepository.save(activity);

        try {
            kafkaTemplate.send(topicName,activitySaved.getUserId(), activitySaved);

        }catch (Exception e){
            e.printStackTrace();
        }

        return mapToResponse(activitySaved);




    }

    private ActivityResponse mapToResponse(Activity activitySaved) {

        ActivityResponse activityResponse = ActivityResponse.builder().id(activitySaved.getId())
                                             .userId(activitySaved.getUserId())
                .activityType(activitySaved.getActivityType())
                .addtionalMetrics(activitySaved.getAddtionalMetrics())
                .duration(activitySaved.getDuration())
                .startTime(activitySaved.getStartTime())
                .createdAt(activitySaved.getCreatedAt())
                .caloriesBurned(activitySaved.getCaloriesBurned())
                .updatedAt(activitySaved.getUpdatedAt())
                .build();
        return activityResponse;
    }


}
