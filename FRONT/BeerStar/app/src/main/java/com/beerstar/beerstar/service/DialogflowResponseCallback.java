package com.beerstar.beerstar.service;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;

/**
 * Callback interface for handling responses from the Dialogflow API client.
 */
public interface DialogflowResponseCallback {

    /**
     * Called when the API call is successful.
     *
     * @param response The Dialogflow DetectIntentResponse object.
     */
    void onSuccess(DetectIntentResponse response);

    /**
     * Called when the API call fails (network error, server error, etc.).
     *
     * @param errorMessage A description of the error.
     */
    void onError(String errorMessage);
}