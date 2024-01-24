package ua.com.valexa.transformer.service;

import ua.com.valexa.common.dto.sys.StepResponseDto;

import java.util.Map;

public interface Transformable {
    StepResponseDto handleTransform(Long stepId, Map<String, String> parameters);
}
