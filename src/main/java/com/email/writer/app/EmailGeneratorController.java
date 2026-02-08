package com.email.writer.app;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "https://mail.google.com"})
public class EmailGeneratorController {

    private final EmailGeneratorService emailGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateEmail(@Valid @RequestBody EmailRequest emailRequest) {
        try {
            String response = emailGeneratorService.generateEmailReply(emailRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error generating email: " + e.getMessage());
        }
    }
}
