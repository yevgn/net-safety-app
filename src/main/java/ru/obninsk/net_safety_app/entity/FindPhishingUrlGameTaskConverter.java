package ru.obninsk.net_safety_app.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import ru.obninsk.net_safety_app.exception.JsonSerializationException;

import java.util.Map;

public class FindPhishingUrlGameTaskConverter implements AttributeConverter<Map<String, Boolean>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Boolean> attribute) {
        try{
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException ex){
            throw new JsonSerializationException(
                    String.format("Ошибка при сериализации %s в json в сущности FindPhishingUrlGameTask", attribute.toString())
            );
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Boolean> convertToEntityAttribute(String dbData) {
        try{
            return objectMapper.readValue(dbData, Map.class);
        } catch (JsonProcessingException ex){
            throw new JsonSerializationException(
                    String.format("Ошибка при чтении данных {%s} и записи в сущность FindPhishingUrlGameTask", dbData)
            );
        }
    }
}