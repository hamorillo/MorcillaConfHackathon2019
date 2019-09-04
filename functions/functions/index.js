'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotif = functions.firestore.document('/push/{pushId}')
    .onWrite(async (change, context) => {
        const pushData = change.after.exists ? change.after.data() : null;
        if(pushData === null){
            console.log('Push Data deleted: ', context.params.pushId);
            return
        }

        const destinationId = pushData.destinationId;        
        const getDeviceToken = admin.firestore()
            .doc(`/users/${destinationId}`).get();

        let token;
        const results = await Promise.all([getDeviceToken]);
        token = results[0].data().tokenPush;
        console.log('UserID: ', destinationId,  'Token: ', token);

        // Notification details.
        const payload = {
            notification: {
                title: 'You have a new follower!',
                body: `is now following you.`
                //icon: follower.photoURL
            }
        };

        const response = await admin.messaging().sendToDevice(token, payload);
        console.log('Send Push To: ', token, ' -> Response: ', response.results);
        response.results.forEach((result, index) => {
            const error = result.error;
            if (error) {
                console.error('Failure sending notification to', token, error);
            }
        });
    });