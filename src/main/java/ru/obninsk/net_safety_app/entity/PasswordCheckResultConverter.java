package ru.obninsk.net_safety_app.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import ru.obninsk.net_safety_app.exception.JsonSerializationException;

import java.util.List;


public class PasswordCheckResultConverter implements AttributeConverter<List<String>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try{
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException ex){
            throw new JsonSerializationException(
                    String.format("Ошибка при сериализации %s в json в сущности PasswordCheckResult", attribute.toString())
            );
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> convertToEntityAttribute(String dbData) {
        try{
            return objectMapper.readValue(dbData, List.class);
        } catch (JsonProcessingException ex){
            throw new JsonSerializationException(
                    String.format("Ошибка при чтении данных {%s} и записи в сущность PasswordCheckResult", dbData)
            );
        }
    }
}
