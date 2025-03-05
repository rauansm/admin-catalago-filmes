package com.codelabs.admin.catalago.infrastructure.web.handler.errorcustom;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Collection;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorApiResponse {

	private int status;
	private String code;
	private String message;
	private Instant timestamp;
	private Collection<ErrorApiFieldResponse> fields;
}
