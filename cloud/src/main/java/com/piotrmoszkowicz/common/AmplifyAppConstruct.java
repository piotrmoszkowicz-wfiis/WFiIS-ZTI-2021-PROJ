package com.piotrmoszkowicz.common;

import software.amazon.awscdk.core.*;

import software.amazon.awscdk.services.amplify.*;
import software.amazon.awscdk.services.amplify.App;
import software.amazon.awscdk.services.amplify.AppProps;
import software.amazon.awscdk.services.codebuild.*;
import software.amazon.awscdk.services.iam.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AmplifyAppConstruct extends Construct {
    private final App amplifyApp;

    public AmplifyAppConstruct(final Construct scope, final String id) {
        super(scope, id);

        var wildcardStackArn = String
                .format("arn:aws:cloudformation:%s:%s:stack/*", System.getenv("CDK_DEFAULT_REGION"), System.getenv("CDK_DEFAULT_ACCOUNT"));

        var amplifyPolicy = new ManagedPolicy(this, "AmplifyPolicy", ManagedPolicyProps.builder()
                .managedPolicyName("AmplifyCICDPolicy")
                .statements(List.of(
                        new PolicyStatement(PolicyStatementProps.builder()
                                .actions(List.of(
                                        "cloudformation:CreateChangeSet",
                                        "cloudformation:DeleteChangeSet",
                                        "cloudformation:DescribeChangeSet",
                                        "cloudformation:DescribeStackEvents",
                                        "cloudformation:DescribeStackResource",
                                        "cloudformation:DescribeStackResources",
                                        "cloudformation:DescribeStacks",
                                        "cloudformation:ExecuteChangeSet",
                                        "cloudformation:GetTemplate",
                                        "cloudformation:UpdateStack"
                                ))
                                .effect(Effect.ALLOW)
                                .resources(List.of(wildcardStackArn))
                                .build()
                        )
                ))
                .build()
        );

        var amplifyRole = new Role(this, "AmplifyRole", RoleProps.builder()
                .assumedBy(new ServicePrincipal("amplify.amazonaws.com"))
                .managedPolicies(List.of(
                        ManagedPolicy.fromAwsManagedPolicyName("AdministratorAccess-Amplify"),
                        amplifyPolicy
                ))
                .build()
        );

        amplifyApp = new App(this, "AmplifyApp", AppProps.builder()
                // Yup, Java sucks and sorts keys... That's why "preBuild" is after "build" and for some reason that doesn't go well with Amplify... LinkedHashMap fixes issue...
                .buildSpec(BuildSpec.fromObject(new LinkedHashMap<>(Map.of(
                        "version", "1.0",
                        "frontend", Map.of(
                                "phases", Map.of(
                                        "preBuild", Map.of(
                                                "commands", List.of("cd cloud", "source ./src/main/resources/setEnvs.bash", "cd ../frontend", "yarn")
                                        ),
                                        "build", Map.of(
                                                "commands", List.of("yarn build")
                                        )
                                ),
                                "artifacts", Map.of(
                                        "baseDirectory", "frontend/build",
                                        "files", List.of("**/*")
                                )
                        )
                ))))
                .sourceCodeProvider(new GitHubSourceCodeProvider(GitHubSourceCodeProviderProps
                        .builder()
                        .owner("piotrmoszkowicz-wfiis")
                        .repository("WFiIS-ZTI-2021-PROJ")
                        .oauthToken(SecretValue.secretsManager("github_token", SecretsManagerSecretOptions.builder().jsonField("GITHUB_TOKEN").build()))
                        .build()
                ))
                .role(amplifyRole)
                .build()
        );

        amplifyApp.addCustomRule(CustomRule.SINGLE_PAGE_APPLICATION_REDIRECT);

        amplifyApp.addBranch("master", BranchOptions.builder()
                .branchName("master")
                .pullRequestPreview(true)
                .stage("PRODUCTION")
                .build()
        );

        // ========================================================================
        // Exports
        // ========================================================================
        new CfnOutput(this, "AmplifyDomainExport", CfnOutputProps.builder()
                .exportName("amplifyDomain")
                .value(getAmplifyAppUrl())
                .build()
        );
    }

    public String getAmplifyAppUrl() { return "master." + amplifyApp.getDefaultDomain(); }
}
