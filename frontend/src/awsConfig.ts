/* eslint-disable import/prefer-default-export */
export const authConfig = {
  Auth: {
    identityPoolId: process.env.REACT_APP_AWS_COGNITO_IDENTITY_POOL,
    region: process.env.REACT_APP_AWS_COGNITO_REGION,
    userPoolId: process.env.REACT_APP_AWS_COGNITO_USER_POOL_ID,
    userPoolWebClientId: process.env.REACT_APP_AWS_COGNITO_USER_POOL_CLIENT_ID,
    oauth: {
      domain: process.env.REACT_APP_AWS_COGNITO_USER_POOL_DOMAIN,
      redirectSignIn: `${window.location.origin}/sign-in/`,
      redirectSignOut: `${window.location.origin}/sign-out/`,
      responseType: "code",
      scope: [
        "phone",
        "email",
        "profile",
        "openid",
        "aws.cognito.signin.user.admin",
      ],
    },
  },
};
