import React from "react";
import ReactDOM from "react-dom";

import Amplify, { Auth } from "aws-amplify";

import App from "./App";
import reportWebVitals from "./reportWebVitals";

import { authConfig } from "./awsConfig";

Auth.configure(authConfig);

Amplify.Logger.LOG_LEVEL = "DEBUG";

ReactDOM.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
  document.getElementById("root")
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
