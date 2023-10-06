package com.trungdoan.assessment.testservice.model;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class UserTest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column
    private String userName;
}
