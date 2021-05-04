import React from "react";

import { ApolloError } from "@apollo/client";
import { Box, Typography } from "@material-ui/core";

import AddRuleView from "@components/RulesList/AddRuleView";

import { Rule, RuleInput } from "@generated/types";

import RulesListItem from "./RulesListItem";

interface Props {
  addRule(ruleInput: RuleInput): Promise<void>;
  deleteRule(incomingText: string): Promise<void>;
  error?: ApolloError;
  loading: boolean;
  rules: Rule[];
}

const RulesListView: React.FC<Props> = ({
  addRule,
  deleteRule,
  error,
  loading,
  rules,
}) => {
  if (error) return <Typography>{error}</Typography>;
  if (loading && rules.length === 0) return <Typography>Loading...</Typography>;

  return (
    <>
      <Box>
        <AddRuleView addRule={addRule} />
      </Box>
      <Box display="flex" justifyContent="space-between">
        <Typography>ID</Typography>
        <Typography>Incoming text</Typography>
        <Typography>Outgoing text</Typography>
        <Typography>Actions</Typography>
      </Box>
      {rules.map((rule) => (
        <Box key={rule.id}>
          <RulesListItem deleteRule={deleteRule} rule={rule} />
        </Box>
      ))}
    </>
  );
};

export default RulesListView;
