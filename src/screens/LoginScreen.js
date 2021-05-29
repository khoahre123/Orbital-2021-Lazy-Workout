import React, { useState, useRef } from "react";
import { Pressable, StyleSheet, Text, Keyboard, SafeAreaView, StatusBar} from "react-native";
import { Button, TextInput } from "react-native-paper";
import { CommonActions } from "@react-navigation/native";

import Logo from "../components/Logo";

import colors from "../constants/colors";

import * as Authentication from "../../api/auth";

let LoginScreen;
export default LoginScreen = ({ navigation }) => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isPasswordVisible, setIsPasswordVisible] = useState(false);
    const [isLoginLoading, setIsLoginLoading] = useState(false);
    const passwordTextInput = useRef();

    const handleLogin = () => {
        Keyboard.dismiss();
        setIsLoginLoading(true);

        Authentication.signIn(
            { email, password },
            (user) => navigation.dispatch(CommonActions.reset({
                index: 0,
                routes: [{
                    name: "Home",
                    params: { name: user.displayName }
                }]
            })),
            (error) => {
                //implement handling error
                setIsLoginLoading(false);
                return alert(error);
            }
        );
    }

    return (
        <SafeAreaView style={styles.container}>
            
            <Logo type="mainLogo"/>

            <TextInput
                mode="outlined"
                placeholder="Email"
                keyboardType="email-address"
                theme={{colors: {primary: colors.secondaryDark}}}
                style={styles.inputText}
                value={email}
                onChangeText={setEmail}
                autoCapitalize="none"
                returnKeyType="next"
                onSubmitEditing={() => passwordTextInput.current.focus()}
                blurOnSubmit={false}
                left={<TextInput.Icon 
                    style={{alignItems:"center"}} 
                    name="email"/>}
            />

            <TextInput
                mode="outlined"
                ref={passwordTextInput}
                placeholder="Password"
                theme={{colors: {primary: colors.secondaryDark}}}
                style={styles.inputText}
                value={password}
                onChangeText={setPassword}
                left={<TextInput.Icon name="lock" />}
                secureTextEntry={!isPasswordVisible}
                autoCapitalize="none"
                right={<TextInput.Icon name={isPasswordVisible ? "eye-off" : "eye"} onPress={() => setIsPasswordVisible((state) => !state)} />}
            />

            <Pressable onPress={() => { }}>
                <Text style={styles.forgotPasswordLink}>Forgot your password?</Text>
            </Pressable>

            <Button
                mode="contained"
                style={styles.button}
                labelStyle={{ color: colors.secondaryLight }}
                contentStyle={{ paddingVertical: 5 }}
                onPress={handleLogin}
                loading={isLoginLoading}
                disabled={isLoginLoading}
            >Log in</Button>

            <Button
                mode="contained"
                style={styles.button}
                labelStyle={{ color: colors.secondaryLight}}
                contentStyle={{ paddingVertical: 5 }}
                onPress={() => navigation.navigate("Register")}
            >Create an account</Button>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: colors.primary,
        paddingTop: StatusBar.currentHeight ? StatusBar.currentHeight : 0,
        paddingBottom: 20,
        paddingHorizontal: 20,
        justifyContent: "center"
    },

    forgotPasswordLink: {
        marginTop: 3,
        marginBottom: 30,
        fontWeight: "bold",
        fontSize: 16,
        color: colors.secondaryDark,
        textAlign: 'right'
    },

    button: {
        width: "80%",
        backgroundColor: colors.secondary,
        borderRadius: 25,
        height: 50,
        alignSelf: "center",
        justifyContent: "center",
        marginTop: 10,
        marginBottom: 10
    },

    inputText: {
        marginTop: 10,
        height: 50,
        marginBottom: 10
    }
});