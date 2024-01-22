package ua.com.valexa.db.model.red;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "govua01", schema = "red")
@Data
public class GovUa01 {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    @Column(unique = true)
    private UUID hash;
    private LocalDateTime createdAt;
    private Long revisionId;
    private Long number;
    private LocalDate date;
    private String type;
    private String firmEdrpou;
    private String firmName;
    private String caseNumber;
    private LocalDate startDateAuc;
    private LocalDate endDateAuc;
    private String courtName;
    private LocalDate endRegistrationDate;



    public void generateHash() {
        this.hash = UUID.nameUUIDFromBytes((number.toString()
                + date
                + type
                + firmEdrpou
                + firmName
                + caseNumber
                + startDateAuc
                + endDateAuc
                + courtName
                + endRegistrationDate
        ).getBytes());
    }
}
