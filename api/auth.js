import firebase from "./firebase";

export const auth = firebase.auth();


export const signIn = async ({ email, password }, onSuccess, onError) => {
  try {
    const { user } = await auth.signInWithEmailAndPassword(email, password);
    return onSuccess(user);
  } catch (error) {
    return onError(error);
  }
}

export const signInError = (error) => {

  switch (error.code) {
    case 'auth/invalid-email':
      return 'The email address is invalid!';

    case 'unverified-email':
      return error.message;

    case 'auth/user-not-found':
      return 'The account does not exist!';

    case 'auth/wrong-password':
      return 'The password is wrong!';

    default:
      console.log(error.code)
      return 'Email or password is wrong!'
  }
}

export const isFirstTimeUser = (user) => {
  return (user.metadata.creationTime === user.metadata.lastSignInTime)
}

export const createAccount = async ({ name, email, telegram, password }, onSuccess, onError) => {
  try {
    if (name === "") {
      throw ({ "code": "empty-username", "message": "Username must not be empty!" });
    }
    const { user } = await auth.createUserWithEmailAndPassword(email, password);

    if (user) {
      await user.updateProfile({ displayName: name });
      await user.sendEmailVerification();
      return onSuccess(user);
    }
  } catch (error) {
    return onError(error);
  }
}

export const registerError = (error) => {

  switch (error.code) {
    case 'auth/invalid-email':
      return 'The email address is invalid!';

    case 'auth/email-already-in-use':
      return 'The account with this email has already exist!';

    case 'auth/weak-password':
      return 'The password must have at least 6 characters!';

    case "empty-username":
      return error.message;

    default:
      return 'Invalid register. Please try again!'
  }
}

export const signOut = async (onSuccess, onError) => {
  try {
    await auth.signOut();
    return onSuccess();
  } catch (error) {
    return onError(error);
  }
}

export const getCurrentUserId = () => auth.currentUser ? auth.currentUser.uid : null;

export const getCurrentUserName = () => auth.currentUser ? auth.currentUser.displayName : null;


export const setOnAuthStateChanged = (onUserAuthenticated, onUserNotFound) => auth.onAuthStateChanged((user) => {
  if (user) {
    return onUserAuthenticated(user);
  } else {
    return onUserNotFound(user);
  }
});
