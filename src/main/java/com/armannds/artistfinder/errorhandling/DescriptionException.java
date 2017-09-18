package com.armannds.artistfinder.errorhandling;

/**
 * Exception that will be used by the DescriptionService, do indicate that an error has occurred.
 * Either when fetching data or converting from Json.
 */
public class DescriptionException extends Exception {

	public DescriptionException(String message) {
		super(message);
	}
}
