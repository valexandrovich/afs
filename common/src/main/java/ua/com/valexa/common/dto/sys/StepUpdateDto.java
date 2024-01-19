package ua.com.valexa.common.dto.sys;

import lombok.Data;
import ua.com.valexa.db.model.enums.StepStatus;

import java.util.HashMap;
import java.util.Map;

@Data
public class StepUpdateDto {
    private Long stepId;
    private StepStatus status;
    private String comment;
    private double progress;
}
