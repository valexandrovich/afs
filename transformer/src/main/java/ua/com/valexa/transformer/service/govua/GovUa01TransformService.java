package ua.com.valexa.transformer.service.govua;

import org.springframework.stereotype.Service;
import ua.com.valexa.common.dto.sys.StepResponseDto;
import ua.com.valexa.transformer.service.Transformable;

import java.util.Map;

@Service("govua01")
public class GovUa01TransformService implements Transformable {
    @Override
    public StepResponseDto handleTransform(Long stepId, Map<String, String> parameters) {
        System.out.println("TRANSFORMING");
        System.out.println(stepId);
        System.out.println(parameters);
        return null;
    }
}
