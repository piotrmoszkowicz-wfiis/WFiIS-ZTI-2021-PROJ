import * as Types from "../../../generated/types";

import { gql } from "@apollo/client";
import * as Apollo from "@apollo/client";
const defaultOptions = {};
export type AddRuleMutationVariables = Types.Exact<{
  ruleInput: Types.RuleInput;
}>;

export type AddRuleMutation = {
  __typename?: "Mutation";
  addNewRule: {
    __typename?: "Rule";
    id: string;
    incomingText: string;
    outgoingText: string;
  };
};

export type DeleteRuleMutationVariables = Types.Exact<{
  incomingText: Types.Scalars["String"];
}>;

export type DeleteRuleMutation = {
  __typename?: "Mutation";
  deleteRule: boolean;
};

export const AddRuleDocument = gql`
  mutation addRule($ruleInput: RuleInput!) {
    addNewRule(rule: $ruleInput) {
      id
      incomingText
      outgoingText
    }
  }
`;
export type AddRuleMutationFn = Apollo.MutationFunction<
  AddRuleMutation,
  AddRuleMutationVariables
>;

/**
 * __useAddRuleMutation__
 *
 * To run a mutation, you first call `useAddRuleMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useAddRuleMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [addRuleMutation, { data, loading, error }] = useAddRuleMutation({
 *   variables: {
 *      ruleInput: // value for 'ruleInput'
 *   },
 * });
 */
export function useAddRuleMutation(
  baseOptions?: Apollo.MutationHookOptions<
    AddRuleMutation,
    AddRuleMutationVariables
  >
) {
  const options = { ...defaultOptions, ...baseOptions };
  return Apollo.useMutation<AddRuleMutation, AddRuleMutationVariables>(
    AddRuleDocument,
    options
  );
}
export type AddRuleMutationHookResult = ReturnType<typeof useAddRuleMutation>;
export type AddRuleMutationResult = Apollo.MutationResult<AddRuleMutation>;
export type AddRuleMutationOptions = Apollo.BaseMutationOptions<
  AddRuleMutation,
  AddRuleMutationVariables
>;
export const DeleteRuleDocument = gql`
  mutation deleteRule($incomingText: String!) {
    deleteRule(incomingText: $incomingText)
  }
`;
export type DeleteRuleMutationFn = Apollo.MutationFunction<
  DeleteRuleMutation,
  DeleteRuleMutationVariables
>;

/**
 * __useDeleteRuleMutation__
 *
 * To run a mutation, you first call `useDeleteRuleMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useDeleteRuleMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [deleteRuleMutation, { data, loading, error }] = useDeleteRuleMutation({
 *   variables: {
 *      incomingText: // value for 'incomingText'
 *   },
 * });
 */
export function useDeleteRuleMutation(
  baseOptions?: Apollo.MutationHookOptions<
    DeleteRuleMutation,
    DeleteRuleMutationVariables
  >
) {
  const options = { ...defaultOptions, ...baseOptions };
  return Apollo.useMutation<DeleteRuleMutation, DeleteRuleMutationVariables>(
    DeleteRuleDocument,
    options
  );
}
export type DeleteRuleMutationHookResult = ReturnType<
  typeof useDeleteRuleMutation
>;
export type DeleteRuleMutationResult = Apollo.MutationResult<DeleteRuleMutation>;
export type DeleteRuleMutationOptions = Apollo.BaseMutationOptions<
  DeleteRuleMutation,
  DeleteRuleMutationVariables
>;
