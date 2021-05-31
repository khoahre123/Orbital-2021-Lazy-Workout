import React from "react";
import { Button, Text, SafeAreaView, StyleSheet } from "react-native";
import { CommonActions } from "@react-navigation/native";

import * as Authentication from "../../api/auth";

let ProfileScreen;
export default ProfileScreen = ({ navigation }) => {
    const userName = Authentication.getCurrentUserName();

    const handleLogout = () => {
        Authentication.signOut(
            () => navigation.dispatch(CommonActions.reset({
                index: 0,
                routes: [{ name: "Login" }]
            })),
            console.error
        );
    }

    return (
        <SafeAreaView style={styles.container}>
            <Text>Hi {userName}. This is your Profile page!</Text>
            <Text>We will implement this page and come back in next Milestone!</Text>
            <Button onPress={handleLogout} title="Log out" />
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: "space-around",
        paddingHorizontal: 30
    }
})


