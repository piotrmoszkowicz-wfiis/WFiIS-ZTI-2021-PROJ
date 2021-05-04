package com.piotrmoszkowicz;

import com.piotrmoszkowicz.common.AppSyncConstruct;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;

import com.piotrmoszkowicz.common.AmplifyAppConstruct;
import com.piotrmoszkowicz.common.CognitoAuthConstruct;
import com.piotrmoszkowicz.common.DynamoDBConstruct;

import com.piotrmoszkowicz.messenger.MessengerConstruct;

public class CloudStack extends Stack {
    public CloudStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public CloudStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        var amplifyAppConstruct = new AmplifyAppConstruct(this, "AmplifyAppConstruct");
        var cognitoAuthConstruct = new CognitoAuthConstruct(this, "CognitoAuthConstruct", amplifyAppConstruct.getAmplifyAppUrl());
        var dynamoDBConstruct = new DynamoDBConstruct(this, "DynamoDBConstruct");

        var appsyncConstruct = new AppSyncConstruct(this, "AppsyncConstruct", cognitoAuthConstruct.userPool, dynamoDBConstruct.dynamoTable);

        var messengerConstruct = new MessengerConstruct(this, "MessengerConstruct", dynamoDBConstruct.dynamoTable);
    }
}
