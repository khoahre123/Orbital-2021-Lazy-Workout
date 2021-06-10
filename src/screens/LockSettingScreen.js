import React, { useEffect, useRef, useState } from "react";
import { Button, Text, SafeAreaView, StyleSheet } from "react-native";
import { CommonActions } from "@react-navigation/native";

import * as Authentication from "../../api/auth";

let LockSettingScreen;
export default LockSettingScreen = ({ navigation }) => {
  const userName = Authentication.getCurrentUserName();

  const goToProfile = () => navigation.navigate("Profile");


  return (
    <SafeAreaView style={styles.container}>
      <Text>Welcome {userName}. This is Lock Setting screen where you can
        customize Lock App behaviour</Text>
      <Button onPress={goToProfile} title="Go to Profile" />
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


