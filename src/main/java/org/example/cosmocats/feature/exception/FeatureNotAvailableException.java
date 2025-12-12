package org.example.cosmocats.feature.exception;

public class FeatureNotAvailableException extends RuntimeException {

    private static final String ERROR_MESSAGE = "Feature '%s' is not available";

    public FeatureNotAvailableException(String featureKey) {
        super(String.format(ERROR_MESSAGE, featureKey));
    }
}