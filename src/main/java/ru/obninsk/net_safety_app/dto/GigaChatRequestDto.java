package ru.obninsk.net_safety_app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class GigaChatRequestDto {
    private String model;
    private List<Message> messages;

    @Builder
    @Data
    public static class Message{
        private String role;
        private String content;
    }
}
