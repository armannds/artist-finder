package com.armannds.artistfinder.errorhandling;

/**
 * Exception that will be used by the CoverIconService, do indicate that an error has occurred.
 * Either when fetching data or converting from Json.
 */
public class CoverIconException extends Exception {

	public CoverIconException(String message) {
		super(message);
	}
}
