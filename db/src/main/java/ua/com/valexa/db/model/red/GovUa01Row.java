package ua.com.valexa.db.model.red;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "govua01", schema = "red")
public class GovUa01Row {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private UUID hash;

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

    private LocalDateTime createdAt;
    private Long revisionId;

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
