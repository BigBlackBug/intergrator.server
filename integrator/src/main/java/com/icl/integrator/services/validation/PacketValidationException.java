package com.icl.integrator.services.validation;

import com.github.fge.jsonschema.report.ProcessingReport;

/**
 * Created by BigBlackBug on 2/19/14.
 */
public class PacketValidationException extends RuntimeException {
	private final ProcessingReport processingReport;

	public PacketValidationException(ProcessingReport processingReport) {
		super(processingReport.toString());
		this.processingReport = processingReport;
	}

	public ProcessingReport getProcessingReport() {
		return processingReport;
	}
}
