import React, { useState, useRef } from "react";
import { View, TouchableOpacity, Pressable, StyleSheet, Text, Keyboard, SafeAreaView,
     StatusBar, ScrollView } from "react-native";
import { Button, TextInput } from "react-native-paper";
import { CommonActions } from "@react-navigation/native";

import Logo from "../components/Logo";

import colors from "../constants/colors";

import * as Authentication from "../../api/auth";
import * as Database from "../../api/database";

let RegisterScreen;
export default RegisterScreen = ({ navigation }) => {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [telegram, setTelegram] = useState("");
    const [password, setPassword] = useState("");
    const [isPasswordVisible, setIsPasswordVisible] = useState(false);
    const [isRegisterLoading, setIsRegisterLoading] = useState(false);
    const emailTextInput = useRef();
    const passwordTextInput = useRef();

    const handleRegister = () => {
        Keyboard.dismiss();
        setIsRegisterLoading(true);

        Authentication.createAccount(
            { name: username, email, telegram: telegram, password },
            (user) => navigation.dispatch(CommonActions.reset({
                index: 0,
                routes: [{
                    name: "LockSetting",
                    params: { name: user.displayName }
                }]
            })),
            (error) => {
                setIsRegisterLoading(false);
                return alert(error);
            }
        );

        Database.createAccountDatabase({email, username, telegram}, () => {}, console.error);
    }

    return (
        <SafeAreaView style={styles.container}>
            <ScrollView 
                contentContainerStyle={{ paddingBottom: 20, paddingHorizontal: 20 }}
                keyboardShouldPersistTaps="always">

                <TouchableOpacity onPress={() => navigation.goBack()}>
                    <Logo type="tinyLogo" />
                </TouchableOpacity>   

                <Text style={styles.title}>welcome</Text>

                <TextInput
                    mode="outlined"
                    placeholder="Name"
                    style={styles.inputText}
                    theme={{ colors: { primary: colors.secondaryDark } }}
                    value={username}
                    onChangeText={setUsername}
                    autoCapitalize="words"
                    returnKeyType="next"
                    onSubmitEditing={() => emailTextInput.current.focus()}
                    blurOnSubmit={false}
                    left={<TextInput.Icon name="run-fast" />}
                />

                <TextInput
                    ref={emailTextInput}
                    mode="outlined"
                    placeholder="Email"
                    keyboardType="email-address"
                    style={styles.inputText}
                    theme={{ colors: { primary: colors.secondaryDark } }}
                    value={email}
                    onChangeText={setEmail}
                    autoCapitalize="none"
                    returnKeyType="next"
                    onSubmitEditing={() => passwordTextInput.current.focus()}
                    blurOnSubmit={false}
                    left={<TextInput.Icon name="email" />}
                />

                <TextInput
                    mode="outlined"
                    placeholder="Telegram handle (Optional)"
                    style={styles.inputText}
                    theme={{ colors: { primary: colors.secondaryDark } }}
                    value={telegram}
                    onChangeText={setTelegram}
                    autoCapitalize="words"
                    returnKeyType="next"
                    onSubmitEditing={() => emailTextInput.current.focus()}
                    blurOnSubmit={false}
                    left={<TextInput.Icon name="message-text-outline" />}
                />

                <TextInput
                    ref={passwordTextInput}
                    mode="outlined"
                    placeholder="Password"
                    style={styles.inputText}
                    theme={{ colors: { primary: colors.secondaryDark } }}
                    value={password}
                    onChangeText={setPassword}
                    left={<TextInput.Icon name="lock" />}
                    secureTextEntry={!isPasswordVisible}
                    autoCapitalize="none"
                    right={<TextInput.Icon name={isPasswordVisible ? "eye-off" : "eye"} onPress={() => setIsPasswordVisible((state) => !state)} />}
                />

                <Button
                    mode="contained"
                    style={styles.button}
                    contentStyle={{ paddingVertical: 5 }}
                    onPress={handleRegister}
                    labelStyle={{ color: colors.secondaryDark }}
                    loading={isRegisterLoading}
                    disabled={isRegisterLoading}
                >Create account</Button>

                <View style={styles.row}>
                    <Text style={{
                        color: colors.secondaryDark,
                        fontSize: 16
                    }}>Already have an account? </Text>
                    <TouchableOpacity onPress={() => navigation.goBack()}>
                        <Text style={styles.link}>Login</Text>
                    </TouchableOpacity>
                </View>

                <Pressable style={{alignSelf:"center", marginTop:10}} onPress={() => { }}>
                    <Text style={styles.link}>Privacy & Terms</Text>
                </Pressable>

            </ScrollView>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    title: {
        fontSize: 34,
        fontWeight: "bold",
        paddingTop: 50,
        color: colors.secondaryDark,
        paddingBottom: 20
    },

    container: {
        flex: 1,
        backgroundColor: colors.secondary,
        paddingTop: StatusBar.currentHeight ? StatusBar.currentHeight : 0,
        paddingBottom: 0,
        paddingHorizontal: 20,
        justifyContent: "center"
    },

    button: {
        width: "80%",
        backgroundColor: colors.primary,
        borderRadius: 25,
        height: 50,
        alignSelf: "center",
        justifyContent: "center",
        marginTop: 50,
        marginBottom: 10,
    },

    inputText: {
        marginTop: 7,
        borderRadius: 25,
        height: 50,
        marginBottom: 7,
        justifyContent: "center"
    },

    row: {
        flexDirection: 'row',
        marginTop: 4,
        alignSelf: "center",
    },
    
    link: {
        fontWeight: 'bold',
        color: "#6495ed",
        fontSize: 16
    }
});