import * as Types from "../../../generated/types";

import { gql } from "@apollo/client";
import * as Apollo from "@apollo/client";
const defaultOptions = {};
export type GetRulesQueryVariables = Types.Exact<{ [key: string]: never }>;

export type GetRulesQuery = {
  __typename?: "Query";
  getRules: Array<{
    __typename?: "Rule";
    id: string;
    incomingText: string;
    outgoingText: string;
  }>;
};

export const GetRulesDocument = gql`
  query getRules {
    getRules {
      id
      incomingText
      outgoingText
    }
  }
`;

/**
 * __useGetRulesQuery__
 *
 * To run a query within a React component, call `useGetRulesQuery` and pass it any options that fit your needs.
 * When your component renders, `useGetRulesQuery` returns an object from Apollo Client that contains loading, error, and data properties
 * you can use to render your UI.
 *
 * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
 *
 * @example
 * const { data, loading, error } = useGetRulesQuery({
 *   variables: {
 *   },
 * });
 */
export function useGetRulesQuery(
  baseOptions?: Apollo.QueryHookOptions<GetRulesQuery, GetRulesQueryVariables>
) {
  const options = { ...defaultOptions, ...baseOptions };
  return Apollo.useQuery<GetRulesQuery, GetRulesQueryVariables>(
    GetRulesDocument,
    options
  );
}
export function useGetRulesLazyQuery(
  baseOptions?: Apollo.LazyQueryHookOptions<
    GetRulesQuery,
    GetRulesQueryVariables
  >
) {
  const options = { ...defaultOptions, ...baseOptions };
  return Apollo.useLazyQuery<GetRulesQuery, GetRulesQueryVariables>(
    GetRulesDocument,
    options
  );
}
export type GetRulesQueryHookResult = ReturnType<typeof useGetRulesQuery>;
export type GetRulesLazyQueryHookResult = ReturnType<
  typeof useGetRulesLazyQuery
>;
export type GetRulesQueryResult = Apollo.QueryResult<
  GetRulesQuery,
  GetRulesQueryVariables
>;
