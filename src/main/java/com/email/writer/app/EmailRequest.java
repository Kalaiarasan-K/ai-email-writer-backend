package com.email.writer.app;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequest {

    @NotBlank(message = "Email content cannot be empty")
    private String emailContent;

    @NotBlank(message = "Tone cannot be empty")
    private String tone;
}