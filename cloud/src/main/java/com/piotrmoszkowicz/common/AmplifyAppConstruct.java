package com.piotrmoszkowicz.common;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.SecretValue;
import software.amazon.awscdk.core.SecretsManagerSecretOptions;

import software.amazon.awscdk.services.amplify.*;
import software.amazon.awscdk.services.codebuild.*;

import java.util.List;
import java.util.Map;

public class AmplifyAppConstruct extends Construct {
    private final App amplifyApp;

    public AmplifyAppConstruct(final Construct scope, final String id) {
        super(scope, id);

        amplifyApp = new App(this, "AmplifyApp", AppProps.builder()
                .buildSpec(BuildSpec.fromObjectToYaml(Map.of(
                        "version", "1.0",
                        "frontend", Map.of(
                                "phases", Map.of(
                                        "preBuild", Map.of(
                                                "commands", List.of("cd frontend", "yarn"),
                                            "build", Map.of(
                                                    "commands", List.of("yarn build")
                                                )
                                        ),
                                    "artifacts", Map.of(
                                            "baseDirectory", "frontend/build",
                                            "files", "**/*"
                                        )
                                )
                        )
                )))
                .sourceCodeProvider(new GitHubSourceCodeProvider(GitHubSourceCodeProviderProps
                        .builder()
                        .owner("piotrmoszkowicz-wfiis")
                        .repository("WFiIS-ZTI-2021-PROJ")
                        .oauthToken(SecretValue.secretsManager("github_token", SecretsManagerSecretOptions.builder().jsonField("GITHUB_TOKEN").build()))
                        .build()
                ))
                .build()
        );

        amplifyApp.addBranch("master", BranchOptions.builder()
                .branchName("master")
                .pullRequestPreview(true)
                .stage("PRODUCTION")
                .build()
        );
    }

    public String getAmplifyAppUrl() { return amplifyApp.getDefaultDomain(); }
}
