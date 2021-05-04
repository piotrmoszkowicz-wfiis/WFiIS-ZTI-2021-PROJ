import { useEffect, useState } from "react";
import {
  ApolloClient,
  InMemoryCache,
  NormalizedCacheObject,
} from "@apollo/client";
import { Auth, Hub } from "aws-amplify";
// It's dependency of aws-amplify. We don't want install it separately as it could break with different versioning. It's just for type here anyways.
// eslint-disable-next-line import/no-extraneous-dependencies
import { HubCallback } from "@aws-amplify/core";

import HubEvent from "@enums/HubEvent";

import createApolloLink from "./createApolloLink";

const createClient = (): ApolloClient<NormalizedCacheObject> => {
  return new ApolloClient({
    cache: new InMemoryCache(),
  });
};

const useApolloClient = (): ApolloClient<NormalizedCacheObject> => {
  const [client] = useState<ApolloClient<NormalizedCacheObject>>(() =>
    createClient()
  );

  useEffect(() => {
    (async () => {
      try {
        const session = await Auth.currentSession();

        if (session) {
          client.setLink(createApolloLink(session));
        }
      } catch (e) {
        // Do nothing
      }
    })();

    const handleAuthEvents: HubCallback = ({
      payload: { event, data },
    }): void => {
      switch (event as HubEvent) {
        case HubEvent.SignIn:
          client.setLink(createApolloLink(data.signInUserSession));
          break;
        case HubEvent.SignOut:
          client.cache.reset();
          break;
        default:
          break;
      }
    };

    Hub.listen("auth", handleAuthEvents);
    return () => Hub.remove("auth", handleAuthEvents);
  });

  return client;
};

export default useApolloClient;
