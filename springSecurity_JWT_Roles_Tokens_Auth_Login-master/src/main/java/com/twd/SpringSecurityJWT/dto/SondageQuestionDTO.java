package com.twd.SpringSecurityJWT.dto;

import com.twd.SpringSecurityJWT.entity.Sondage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SondageQuestionDTO {
    private Sondage sondage;
    private List<String> questions;

}
