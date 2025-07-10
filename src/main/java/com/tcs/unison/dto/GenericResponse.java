package com.tcs.unison.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenericResponse {
	private String message;
	private HttpStatus responseStatus;
	private int responseCode;

}
