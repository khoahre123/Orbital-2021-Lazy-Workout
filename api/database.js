import firebase from "./firebase";

const db = firebase.database();

const newAccount = (id, username, email, telegram) => ({ id, username, email, telegram });

export const createAccountDatabase = async ({ email, username, telegram }, onSuccess, onError) => {
  try {
    const account = db.ref(`users`).push();
    await account.set(newAccount(account.key, username, email, telegram));
    return onSuccess(account);
  } catch (error) {
    return onError(error);
  }
}