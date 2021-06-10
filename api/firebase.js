import firebase from "firebase";
import "firebase/auth";
import "firebase/database";

const firebaseConfig = {
  apiKey: "AIzaSyA84YbjB-vOUp06hNtnKr397GFiX2CyxKQ",
  authDomain: "lazy-workout.firebaseapp.com",
  projectId: "lazy-workout",
  databaseURL: "https://lazy-workout-default-rtdb.asia-southeast1.firebasedatabase.app/",
  storageBucket: "lazy-workout.appspot.com",
  messagingSenderId: "40697356289",
  appId: "1:40697356289:web:be0b3b9dfba713366a2000",
  measurementId: "G-S2Y6SR8MNK"
};

const firebaseApp = !firebase.apps.length ? firebase.initializeApp(firebaseConfig) : firebase.app();
export default firebaseApp;