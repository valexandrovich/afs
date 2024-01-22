package ua.com.valexa.common.dto.red;

import lombok.Data;

@Data
public class GovUa01Dto {
    private String number;
    private String date;
    private String type;
    private String firm_edrpou;
    private String firm_name;
    private String case_number;
    private String start_date_auc;
    private String end_date_auc;
    private String court_name;
    private String end_registration_date;
}
