package com.piotrmoszkowicz.common;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.CfnOutputProps;
import software.amazon.awscdk.core.Construct;

import software.amazon.awscdk.services.cognito.*;

import java.util.List;

public class CognitoAuthConstruct extends Construct {
    public final UserPool userPool;

    public CognitoAuthConstruct(final Construct scope, final String id, final String amplifyUrl) {
        super(scope, id);

        userPool = new UserPool(this, "UserPool", UserPoolProps.builder()
                .accountRecovery(AccountRecovery.EMAIL_ONLY)
                .autoVerify(AutoVerifiedAttrs.builder()
                        .email(true)
                        .build()
                )
                .passwordPolicy(PasswordPolicy.builder()
                        .minLength(10)
                        .requireLowercase(true)
                        .requireUppercase(true)
                        .requireDigits(true)
                        .requireSymbols(false)
                        .build()
                )
                .selfSignUpEnabled(true)
                .signInAliases(SignInAliases.builder()
                        .email(true)
                        .phone(true)
                        .build()
                )
                .standardAttributes(StandardAttributes.builder()
                        .email(StandardAttribute.builder()
                                .mutable(true)
                                .required(true)
                                .build()
                        )
                        .phoneNumber(StandardAttribute.builder()
                                .mutable(true)
                                .required(false)
                                .build()
                        )
                        .build()
                )
                .userVerification(UserVerificationConfig.builder()
                        .emailSubject("Messenger | Verify account")
                        .build()
                )
                .build()
        );

        var userPoolDomain = new UserPoolDomain(this, "UserPoolDomain", UserPoolDomainProps.builder()
                .cognitoDomain(CognitoDomainOptions.builder()
                        .domainPrefix("messenger-bot-zti-seminar")
                        .build()
                )
                .userPool(userPool)
                .build()
        );

        var userPoolClient = new UserPoolClient(this, "UserPoolClient", UserPoolClientProps.builder()
                .disableOAuth(false)
                .oAuth(OAuthSettings.builder()
                        .callbackUrls(List.of("http://localhost:3000/sign-in/", String.format("https://%s/sign-in/", amplifyUrl)))
                        .flows(OAuthFlows.builder()
                                .authorizationCodeGrant(true)
                                .build()
                        )
                        .logoutUrls(List.of("http://localhost:3000/sign-out/", String.format("https://%s/sign-out/", amplifyUrl)))
                        .scopes(List.of(OAuthScope.EMAIL, OAuthScope.PHONE, OAuthScope.PROFILE, OAuthScope.OPENID, OAuthScope.COGNITO_ADMIN))
                        .build()
                )
                .preventUserExistenceErrors(true)
                .supportedIdentityProviders(List.of(UserPoolClientIdentityProvider.COGNITO))
                .userPool(userPool)
                .build()
        );

        var cognitoIdentityPool = new CfnIdentityPool(this, "CognitoIdentityPool", CfnIdentityPoolProps.builder()
                .allowClassicFlow(false)
                .allowUnauthenticatedIdentities(true)
                .cognitoIdentityProviders(List.of(
                            CfnIdentityPool.CognitoIdentityProviderProperty.builder()
                                    .clientId(userPoolClient.getUserPoolClientId())
                                    .providerName(userPool.getUserPoolProviderName())
                                    .serverSideTokenCheck(false)
                                    .build()
                        )
                )
                .build()
        );

        // ========================================================================
        // Exports
        // ========================================================================
        new CfnOutput(this, "CognitoIdentityPoolExport", CfnOutputProps.builder()
                .exportName("cognitoIdentityPool")
                .value(cognitoIdentityPool.getRef())
                .build()
        );

        new CfnOutput(this, "CognitoRegionExport", CfnOutputProps.builder()
                .exportName("cognitoRegion")
                .value("eu-central-1")
                .build()
        );

        new CfnOutput(this, "CognitoUserPoolIdExport", CfnOutputProps.builder()
                .exportName("cognitoUserPoolId")
                .value(userPool.getUserPoolId())
                .build()
        );

        new CfnOutput(this, "CognitoUserPoolClientIdExport", CfnOutputProps.builder()
                .exportName("cognitoUserPoolClientId")
                .value(userPoolClient.getUserPoolClientId())
                .build()
        );

        new CfnOutput(this, "CognitoUserPoolDomainExport", CfnOutputProps.builder()
                .exportName("cognitoUserPoolDomain")
                .value(userPoolDomain.baseUrl().replace("https://", ""))
                .build()
        );
    }
}
