import React from "react";

import CloseIcon from "@material-ui/icons/Close";

import { Box, Typography } from "@material-ui/core";

import { Rule } from "@generated/types";

interface Props {
  deleteRule(incomingText: string): Promise<void>;
  rule: Rule;
}

const RulesListItem: React.FC<Props> = ({
  deleteRule,
  rule: { id, incomingText, outgoingText },
}) => (
  <Box display="flex" justifyContent="space-between">
    <Typography>{id}</Typography>
    <Typography>{incomingText}</Typography>
    <Typography>{outgoingText}</Typography>
    <Box>
      <CloseIcon
        cursor="pointer"
        htmlColor="#FF0000"
        onClick={() => deleteRule(incomingText)}
      />
    </Box>
  </Box>
);

export default RulesListItem;
