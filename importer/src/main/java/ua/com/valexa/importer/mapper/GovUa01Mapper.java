package ua.com.valexa.importer.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;
import ua.com.valexa.common.dto.red.GovUa01Dto;
import ua.com.valexa.db.model.red.GovUa01Row;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
@Component
public interface GovUa01Mapper {
    @Mapping(source="number", target="number")
    @Mapping(expression = "java(parseDateFromString(dto.getDate()))", target = "date"  )
    @Mapping(source="type", target="type")
    @Mapping(source="firm_edrpou", target="firmEdrpou")
    @Mapping(source="firm_name", target="firmName")
    @Mapping(source="case_number", target="caseNumber")
    @Mapping(source="start_date_auc", target="startDateAuc")
    @Mapping(source="end_date_auc", target="endDateAuc")
    @Mapping(source="court_name", target="courtName")
    @Mapping(source="end_registration_date", target="endRegistrationDate")
    GovUa01Row mapToEntity(GovUa01Dto dto);


    @AfterMapping
    default void afterNapping(GovUa01Dto dto, @MappingTarget GovUa01Row entity){
        entity.generateHash();
        entity.setCreatedAt(LocalDateTime.now());
//        entity.setRevisionId(UUID.randomUUID());
    }
    //
//
    default LocalDate parseDateFromString(String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d.M.yyyy");
        try {
            return   LocalDate.ofInstant(simpleDateFormat.parse(dateString).toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            return null;
        }
    }
}
