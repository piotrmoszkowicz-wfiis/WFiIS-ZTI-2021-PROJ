import React, { useState } from "react";

import { Box, Button, TextField, Typography } from "@material-ui/core";

import { RuleInput } from "@generated/types";

interface Props {
  addRule(ruleInput: RuleInput): Promise<void>;
}

const AddRuleView: React.FC<Props> = ({ addRule }) => {
  const [ruleInput, setRuleInput] = useState<RuleInput>({
    incomingText: "",
    outgoingText: "",
  });

  return (
    <Box>
      <Typography>Add new Rule</Typography>
      <form>
        <Box display="flex" flexDirection="row" justifyContent="space-around">
          <TextField
            label="Incoming text"
            margin="normal"
            type="text"
            variant="outlined"
            onChange={(e) =>
              setRuleInput((prevState) => ({
                ...prevState,
                incomingText: e.target.value,
              }))
            }
            value={ruleInput.incomingText}
            required
          />
          <TextField
            label="Outgoing text"
            margin="normal"
            type="text"
            variant="outlined"
            onChange={(e) =>
              setRuleInput((prevState) => ({
                ...prevState,
                outgoingText: e.target.value,
              }))
            }
            value={ruleInput.outgoingText}
            required
          />
          <Button
            color="secondary"
            variant="contained"
            onClick={() => addRule(ruleInput)}>
            Add!
          </Button>
        </Box>
      </form>
    </Box>
  );
};

export default AddRuleView;
