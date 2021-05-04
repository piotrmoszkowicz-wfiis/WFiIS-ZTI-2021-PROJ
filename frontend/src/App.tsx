import React from "react";

import CssBaseline from "@material-ui/core/CssBaseline";

import { ApolloProvider } from "@apollo/client";
import { withAuthenticator, AmplifySignOut } from "@aws-amplify/ui-react";
import { Box, Container } from "@material-ui/core";
import { ThemeProvider } from "@material-ui/styles";

import RulesList from "@components/RulesList";

import defaultTheme from "@themes/defaultTheme";

import useApolloClient from "./apollo/useApolloClient";

const App: React.FC = () => {
  const client = useApolloClient();

  return (
    <ThemeProvider theme={defaultTheme}>
      <CssBaseline />
      <ApolloProvider client={client}>
        <Container>
          <AmplifySignOut />
          <Box mt={2}>
            <RulesList />
          </Box>
        </Container>
      </ApolloProvider>
    </ThemeProvider>
  );
};

export default withAuthenticator(App);
