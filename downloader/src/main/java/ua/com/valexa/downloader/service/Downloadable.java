package ua.com.valexa.downloader.service;
import ua.com.valexa.common.dto.sys.StepResponseDto;

import java.util.Map;

public interface Downloadable {
    StepResponseDto handleDownload(Long stepId, Map<String, String> parameters);
}

