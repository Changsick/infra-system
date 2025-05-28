package com.assign.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ResponseDataDTO<T> {

    private T data;

    @Builder
    public ResponseDataDTO(T data) {
        this.data = data;
    }
}
