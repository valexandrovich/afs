package ua.com.valexa.common.dto.sys;

import lombok.Data;
import ua.com.valexa.db.model.enums.StepStatus;

import java.util.HashMap;
import java.util.Map;

@Data
public class StepResponseDto {
    private Long stepId;
    private StepStatus status;
    private Map<String, String> results = new HashMap<>();
}
