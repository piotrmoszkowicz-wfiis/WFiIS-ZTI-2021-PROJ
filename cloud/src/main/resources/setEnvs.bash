stack="CloudStack"

# get_stack_output_value [stack_name] [export_name]
get_stack_output_value () {
  echo "$(aws cloudformation describe-stacks --stack-name $stack --query "Stacks[0].Outputs[?ExportName=='$envPrefix$1'].OutputValue" --output text)"
}

appsync_region=$(get_stack_output_value "graphqlRegion")
appsync_url=$(get_stack_output_value "graphqlUrl")

cognito_region=$(get_stack_output_value "cognitoRegion")
cognito_identity=$(get_stack_output_value "cognitoIdentityPool")
cognito_user_pool_id=$(get_stack_output_value "cognitoUserPoolId")
cognito_user_pool_client_id=$(get_stack_output_value "cognitoUserPoolClientId")
cognito_user_pool_domain=$(get_stack_output_value "cognitoUserPoolDomain")

export REACT_APP_AWS_APPSYNC_REGION=$appsync_region
export REACT_APP_AWS_APPSYNC_URL=$appsync_url

export REACT_APP_AWS_COGNITO_REGION=$cognito_region
export REACT_APP_AWS_COGNITO_IDENTITY_POOL=$cognito_identity
export REACT_APP_AWS_COGNITO_USER_POOL_ID=$cognito_user_pool_id
export REACT_APP_AWS_COGNITO_USER_POOL_CLIENT_ID=$cognito_user_pool_client_id
export REACT_APP_AWS_COGNITO_USER_POOL_DOMAIN=$cognito_user_pool_domain

printenv | grep REACT
