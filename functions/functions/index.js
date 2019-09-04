'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
exports.sendNotif = functions.firestore.document('/push/{userId}')
    .onCreate(async (change, context) => {
        const userId = context.params.userId;
        
        const getDeviceToken = admin.firestore()
            .doc(`/users/${userId}`).get();


        // The snapshot to the user's tokens.
        let tokensSnapshot;

        // The array containing all the user's tokens.
        let tokens;

        const results = await Promise.all([getDeviceToken]);
        tokensSnapshot = results[0].data().tokenPush;

        // Check if there are any device tokens.
        /*if (!tokensSnapshot.hasChildren()) {
            return console.log('There are no notification tokens to send to.');
        }
        console.log('There are', tokensSnapshot.numChildren(), 'tokens to send notifications to.');*/
        console.log('device', results);
        console.log('Token', tokensSnapshot);

        // Notification details.
        const payload = {
            notification: {
                title: 'You have a new follower!',
                body: `is now following you.`
                //icon: follower.photoURL
            }
        };

        // Send notifications to all tokens.
        const response = await admin.messaging().sendToDevice(tokensSnapshot, payload);
        // For each message check if there was an error.        
        console.log('sendPushResponse', response.results);
        response.results.forEach((result, index) => {
            const error = result.error;
            if (error) {
                console.error('Failure sending notification to', tokensSnapshot, error);
                // Cleanup the tokens who are not registered anymore.
                /*if (error.code === 'messaging/invalid-registration-token' ||
                    error.code === 'messaging/registration-token-not-registered') {
                    tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
                }*/
            }
        });
        //return Promise.all(tokensToRemove);
    });