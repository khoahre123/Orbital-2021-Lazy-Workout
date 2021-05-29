import React, { useEffect } from "react";
import { ActivityIndicator } from "react-native";
import { CommonActions } from "@react-navigation/native";


import * as Authentication from "../../api/auth";

let MainScreen;
export default MainScreen = ({ navigation }) => {
    useEffect(() => {
        return Authentication.setOnAuthStateChanged(
            () => navigation.dispatch(CommonActions.reset({ index: 0, routes: [{ name: "Home" }] })),
            () => navigation.dispatch(CommonActions.reset({ index: 0, routes: [{ name: "Login" }] })),
        );
    }, []);

    return (
        <ActivityIndicator animating size="large" color="black" />
    );
}
