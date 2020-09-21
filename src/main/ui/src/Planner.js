import React, { useState } from "react";
import axios from "axios";
import { TextField } from "@material-ui/core";
import Select from "react-select";
import countryList from "react-select-country-list";

export default function Planner() {
    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);
    const [country, setCountry] = useState(null);
    const [holiday, setHolidays] = useState("");
    const [displayResult, setDisplayResult] = useState(false);

    const options = countryList().getData();

    const select = {
        option: (provided) => ({
            ...provided,
            padding: 20,
        }),
        control: () => ({
            width: 263,
        }),
    };

    const changeHandler = (country) => {
        setCountry(country);
    };

    const sendData = () => {
        if (startDate && endDate && country) {
            axios
                .post(
                    "http://localhost:8080/api/dates?startDate=" +
                    startDate +
                    "&endDate=" +
                    endDate +
                    "&country=" +
                    country.value,
                    {
                        headers: {
                            "Access-Control-Allow-Origin": "*",
                            "Access-Control-Allow-Methods": "*",
                            "Access-Control-Allow-Headers": "*",
                            "Content-Type": "application/text",
                        },
                    }
                )
                .then((response) => {
                    setHolidays(response.data);
                })
                .catch(function (error) {
                    console.log("error", error);
                });
        } else {
            console.log("Some required params are missing!");
        }
    };

    const onSubmit = (event) => {
        event.preventDefault();
        sendData();
        setDisplayResult(true);
    };

    return (
        <div>
            <form onSubmit={onSubmit}>
                <Select
                    styles={select}
                    options={options}
                    value={country}
                    placeholder="Select Country"
                    onChange={changeHandler}
                />
                <TextField
                    type="date"
                    defaultValue={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                    inputProps={{ min: "2020-04-01", max: "2021-03-31" }}
                />
                <TextField
                    type="date"
                    defaultValue={endDate}
                    onChange={(e) => setEndDate(e.target.value)}
                    inputProps={{ min: startDate, max: "2021-03-31" }}
                />
                <button style={{ padding: 5, margin: 5 }}>Submit</button>
            </form>
            {displayResult && (
                <span>The person should take {holiday} holiday days</span>
            )}
        </div>
    );
}
