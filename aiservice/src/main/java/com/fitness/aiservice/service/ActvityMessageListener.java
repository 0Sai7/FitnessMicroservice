package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActvityMessageListener {

    private final ActivityAiAervice activityAiAervice;
    private final RecommendationRepository recommendationRepository;

    @KafkaListener(topics = "${kafka.topic.name}",groupId = "activity-group")
    public void processActivity(Activity activity){
        log.info("Recieved activity for processing:{}",activity.getUserId());

        Recommendation recommendation=activityAiAervice.generateRecommendation(activity);
        recommendationRepository.save(recommendation);


    }
}
