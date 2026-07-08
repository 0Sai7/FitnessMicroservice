package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ActivityAiAervice {
    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity) {
        String prompt=createPromptForActivity(activity);
        String aiResponse=geminiService.getRecommendations(prompt);
        log.info("Response from AI:{}",aiResponse);
        return processAiResponse(activity,aiResponse);
    }

    private Recommendation processAiResponse(Activity activity,String aiResponse) {
        try {
            ObjectMapper mapper=new ObjectMapper();
            JsonNode rootNode= mapper.readTree(aiResponse);
            JsonNode textNode= rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .get("parts")
                    .get(0)
                    .path("text");
            String jsonContent=textNode.asText()
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .replaceAll("\\n", "")
                    .trim();
          ///  log.info("Response from Json:{}",jsonContent);

        JsonNode analysisJson=mapper.readTree(jsonContent);
            String recommendation = analysisJson.get("recommendation").asText();

            // 2. Extract the arrays into Lists
            List<String> improvements = extractList(analysisJson.get("improvements"));
            List<String> suggestions = extractList(analysisJson.get("suggestions"));
            List<String> safety = extractList(analysisJson.get("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .type(activity.getActivityType().toString())
                    .recommendation((recommendation))
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();




        }catch (Exception e){
            e.printStackTrace();

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .type(activity.getActivityType().toString())
                    .recommendation(("recommendation"))
                    .improvements(Collections.singletonList("improvements"))
                    .suggestions(Collections.singletonList("suggestions"))
                    .safety(Collections.singletonList("safety"))
                    .createdAt(LocalDateTime.now())
                    .build();

        }

    }
    private static List<String> extractList(JsonNode arrayNode) {
        List<String> list = new ArrayList<>();
        if (arrayNode != null && arrayNode.isArray()) {
            for (JsonNode node : arrayNode) {
                list.add(node.asText());
            }
        }
        return list;
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
    You are an expert AI fitness and health coach. I will provide you with a user's recent workout data. 
    
    Your task is to analyze this data and generate personalized post-workout feedback based on the intensity, duration, and type of activity.
    
    Here is the workout data:
    - Activity Type: %s
    - Duration (minutes): %d
    - Calories Burned: %d
    - Additional Metrics: %s
    
    CRITICAL INSTRUCTION: You must respond ONLY with a valid JSON object. Do not include any introductory text, markdown formatting blocks (like ```json), or conversational filler. The output must strictly adhere to the following JSON format:
    
    {
      "recommendation": "Provide a single, comprehensive paragraph summarizing your main takeaway and primary advice for the user based on this specific session.",
      "improvements": [
        "First specific area where they can improve their performance or technique.",
        "Second area for improvement."
      ],
      "suggestions": [
        "First actionable suggestion for their next workout, recovery, or nutrition.",
        "Second actionable suggestion."
      ],
      "safety": [
        "First safety or injury prevention tip relevant to this activity and duration.",
        "Second safety tip."
      ]
    }
    """,activity.getActivityType(),activity.getDuration(),activity.getCaloriesBurned(),activity.getAddtionalMetrics());
    }

}
