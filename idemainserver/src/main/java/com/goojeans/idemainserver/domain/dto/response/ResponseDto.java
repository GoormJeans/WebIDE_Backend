package com.goojeans.idemainserver.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ResponseDto<T> {
    private int statusCode;
    private List<T> data;
}
