package org.mvc.studentInit.model;


import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.mvc.studentInit.services.BidStatusConverter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Entity
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @NotBlank(message = "Заполните поле текста")
    @Length(max = 2048, message = "Слишком много текста, отразите лишнее в приложенном файле")
    private String text;

    @Min(0)
    @Max(1000000)
    private Integer priseFrom;
    @Min(0)
    @Max(1000000)
    private Integer priseTo;
    private String address;
    private String fileName;

    @Convert(converter = BidStatusConverter.class)
    private BidStatus status;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private User author;

    public Bid(String text, User author) {
        this.text = text;
        this.author = author;
    }


    public Bid() {
    }


}
