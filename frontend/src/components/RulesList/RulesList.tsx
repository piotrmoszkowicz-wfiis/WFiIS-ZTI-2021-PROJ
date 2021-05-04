import React, { useEffect, useLayoutEffect, useState } from "react";

import { Rule, RuleInput } from "@generated/types";

import RulesListView from "./RulesListView";

import {
  useAddRuleMutation,
  useDeleteRuleMutation,
} from "./graphql/mutations.generated";
import { useGetRulesLazyQuery } from "./graphql/queries.generated";

const RulesList: React.FC = () => {
  const [rules, setRules] = useState<Rule[]>([]);
  const [getRules, { data, error, loading }] = useGetRulesLazyQuery({
    fetchPolicy: "no-cache",
  });

  useLayoutEffect(() => {
    (() => {
      setTimeout(async () => {
        await getRules();
      });
    })();
  }, [getRules]);

  useEffect(() => {
    console.log("data", data);
    setRules(data?.getRules ?? []);
  }, [data]);

  const [addRuleMutation] = useAddRuleMutation();
  const [deleteRuleMutation] = useDeleteRuleMutation();

  const addRule = async (ruleInput: RuleInput) => {
    const result = await addRuleMutation({
      variables: {
        ruleInput,
      },
    });

    if (result?.data?.addNewRule) {
      setRules((prevState) => [
        ...prevState.filter(
          (rule) => rule.incomingText !== ruleInput.incomingText
        ),
        result.data!.addNewRule!,
      ]);
    }
  };

  const deleteRule = async (incomingText: string) => {
    await deleteRuleMutation({
      variables: {
        incomingText,
      },
    });
    setRules((prevState) =>
      prevState.filter((rule) => rule.incomingText !== incomingText)
    );
  };

  return (
    <RulesListView
      addRule={addRule}
      deleteRule={deleteRule}
      rules={rules}
      error={error}
      loading={loading}
    />
  );
};

export default RulesList;
