export type Maybe<T> = T | null;
export type Exact<T extends { [key: string]: unknown }> = {
  [K in keyof T]: T[K];
};
export type MakeOptional<T, K extends keyof T> = Omit<T, K> &
  { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> &
  { [SubKey in K]: Maybe<T[SubKey]> };
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: string;
  String: string;
  Boolean: boolean;
  Int: number;
  Float: number;
};

export type Mutation = {
  __typename?: "Mutation";
  addNewRule: Rule;
  deleteRule: Scalars["Boolean"];
};

export type MutationAddNewRuleArgs = {
  rule: RuleInput;
};

export type MutationDeleteRuleArgs = {
  incomingText: Scalars["String"];
};

export type Query = {
  __typename?: "Query";
  getRules: Array<Rule>;
};

export type Rule = {
  __typename?: "Rule";
  id: Scalars["ID"];
  incomingText: Scalars["String"];
  outgoingText: Scalars["String"];
};

export type RuleInput = {
  incomingText: Scalars["String"];
  outgoingText: Scalars["String"];
};
