const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

// Any additional Firebase Cloud Functions can be added here

// Example of a simple function to add a user to Firestore
exports.createUserRecord = functions.auth.user().onCreate((user) => {
  const userRef = admin.firestore().collection('users').doc(user.uid);
  return userRef.set({
    email: user.email,
    userType: 'Paciente', // Set default user type as 'Paciente'
    createdAt: admin.firestore.FieldValue.serverTimestamp()
  });
});

// Example of a simple function to delete a user record from Firestore when a user is deleted
exports.deleteUserRecord = functions.auth.user().onDelete((user) => {
  const userRef = admin.firestore().collection('users').doc(user.uid);
  return userRef.delete();
});
