import React from 'react';
import { LogBox } from "react-native";
import { Provider as PaperProvider } from "react-native-paper";
import { NavigationContainer } from "@react-navigation/native";
import { createStackNavigator } from "@react-navigation/stack";

import ProfileScreen from "./src/screens/ProfileScreen";
import LoginScreen from "./src/screens/LoginScreen";
import RegisterScreen from "./src/screens/RegisterScreen";
import MainScreen from "./src/screens/MainScreen";
import LockSettingScreen from "./src/screens/LockSettingScreen";

import firebase from "./api/firebase";

const Stack = createStackNavigator();

const screens = [
  { name: "Main", component: MainScreen },
  { name: "Login", component: LoginScreen },
  { name: "Register", component: RegisterScreen },
  { name: "Profile", component: ProfileScreen },
  {name: "LockSetting", component: LockSettingScreen}
];

// unfixable "bug" due to Firebase JS SDK's use of long setTimeout
// for subscribing functions, e.g., firebase.database().Reference.on()
// read more at https://github.com/facebook/react-native/issues/12981
LogBox.ignoreLogs(["Setting a timer for a long period of"]);

export default function App() {
  return (
    <PaperProvider>
      <NavigationContainer>
        <Stack.Navigator initialRouteName={screens[0].name} headerMode="none">
          {screens.map(({ name, component }) => <Stack.Screen key={name} name={name} component={component} />)}
        </Stack.Navigator>
      </NavigationContainer>
    </PaperProvider>
  );
}