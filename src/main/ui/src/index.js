import React from "react";
import ReactDOM from "react-dom";
import "./index.css";
import Planner from "./Planner";
import * as serviceWorker from "./serviceWorker";

ReactDOM.render(<Planner />, document.getElementById("root"));

serviceWorker.unregister();
