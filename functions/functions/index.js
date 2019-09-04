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

        if(pushData.destinationId === null){
            console.log('Push Data without destionationID');
            return
        }

        if(pushData.originId === null){
            console.log('Push Data without originID');
            return
        }

        const originId = pushData.originId;
        const destinationId = pushData.destinationId;        
        const getDestinationUser = admin.firestore()
            .doc(`/users/${destinationId}`).get();

        const getOriginUser = admin.firestore()
            .doc(`/users/${originId}`).get();

        let token;
        const results = await Promise.all([getDestinationUser, getOriginUser]);
        const destinationUser = results[0].data()
        const originUser = results[1].data()
        token = destinationUser.tokenPush;
        console.log('UserID: ', destinationId,  'Token: ', token);

        // Notification details.
        const payload = {
            data: {
                currentPomodoroDuration: destinationUser.currentPomodoroDuration.toString(),
                currentPomodoroStartDate: destinationUser.currentPomodoroStartDate.toString(),
                issuerUser: originUser.email
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