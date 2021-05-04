import { ApolloLink, HttpLink } from "@apollo/client";

import { AUTH_TYPE } from "aws-appsync-auth-link";
import { createSubscriptionHandshakeLink } from "aws-appsync-subscription-link";
import { UrlInfo } from "aws-appsync-subscription-link/lib/types";

const createApolloLink = (session: any): ApolloLink => {
  const config: UrlInfo = {
    url: process.env.REACT_APP_AWS_APPSYNC_URL!,
    region: process.env.REACT_APP_AWS_APPSYNC_REGION!,
    auth: {
      type: AUTH_TYPE.AMAZON_COGNITO_USER_POOLS,
      jwtToken: session.getAccessToken().getJwtToken(),
    },
  };

  const customFetch: WindowOrWorkerGlobalScope["fetch"] = (uri, options) => {
    const token = session.getAccessToken().getJwtToken();
    let opts = { ...options };

    if (token) {
      const authorizationObject = {
        Authorization: token,
      };

      if (opts) {
        opts.headers = { ...opts.headers, ...authorizationObject };
      } else {
        opts = { headers: authorizationObject };
      }
    }
    return fetch(uri, opts);
  };

  const httpLink = new HttpLink({
    uri: process.env.REACT_APP_AWS_APPSYNC_URL!,
    fetch: customFetch,
    useGETForQueries: true,
  });

  return createSubscriptionHandshakeLink(config, httpLink);
};

export default createApolloLink;
