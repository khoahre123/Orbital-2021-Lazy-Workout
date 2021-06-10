import React, { useState, useRef } from "react";
import { Pressable, StyleSheet, Text, Keyboard, SafeAreaView, StatusBar, Alert } from "react-native";
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
  const [error, setError] = useState("");
  const [firstTimeUser, setFirstTimeUser] = useState(null);
  const passwordTextInput = useRef();

  const handleLogin = () => {
    Keyboard.dismiss();
    setIsLoginLoading(true);
    setEmail("");
    setPassword("");

    Authentication.signIn(
      { email, password },
      (user) => {

        if (firstTimeUser !== null) {
          setFirstTimeUser(Authentication.isFirstTimeUser(user));
        }

        if (!user.emailVerified) {
          Authentication.signOut(() => { }, () => { });
          setFirstTimeUser(true);
          throw ({ "code": "unverified-email", "message": "Please verify your email!" });
        }

        if (firstTimeUser) {
          navigation.dispatch(CommonActions.reset({
            index: 0,
            routes: [{
              name: "LockSetting",
              params: { name: user.displayName }
            }]
          }))
        } else {
          navigation.dispatch(CommonActions.reset({
            index: 0,
            routes: [{
              name: "Profile",
              params: { name: user.displayName }
            }]
          }))
        }
      }
      ,
      (error) => {
        setIsLoginLoading(false);
        let errorMsg = Authentication.signInError(error);
        setError(errorMsg);
        console.log(error.message) //
      }
    );
  }

  return (
    <SafeAreaView style={styles.container}>

      <Logo type="mainLogo" />

      <TextInput
        mode="outlined"
        placeholder="Email"
        keyboardType="email-address"
        theme={{ colors: { primary: colors.secondaryDark } }}
        selectionColor={colors.secondaryLight}
        style={styles.inputText}
        value={email}
        onChangeText={setEmail}
        autoCapitalize="none"
        returnKeyType="next"
        onSubmitEditing={() => passwordTextInput.current.focus()}
        blurOnSubmit={false}
        left={<TextInput.Icon
          style={{ alignItems: "center" }}
          name="email" />}
      />

      <TextInput
        mode="outlined"
        ref={passwordTextInput}
        placeholder="Password"
        theme={{ colors: { primary: colors.secondaryDark } }}
        selectionColor={colors.secondaryLight}
        style={styles.inputText}
        value={password}
        onChangeText={setPassword}
        left={<TextInput.Icon name="lock" />}
        secureTextEntry={!isPasswordVisible}
        autoCapitalize="none"
        right={<TextInput.Icon name={isPasswordVisible ? "eye-off" : "eye"} onPress={() => setIsPasswordVisible((state) => !state)} />}
      />

      <Text style={styles.error}>{error}</Text>

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
        labelStyle={{ color: colors.secondaryLight }}
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

  error: {
    color: colors.error,
    fontSize: 15
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