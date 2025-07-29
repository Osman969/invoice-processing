package it.arrive.invoicesystem.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GenericApiResponse {

    private int status;
    private String message;
    private LocalDateTime timestamp;
}
