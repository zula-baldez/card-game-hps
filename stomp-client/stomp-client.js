const readline = require('readline');
const fs = require('fs');
const StompJs = require('@stomp/stompjs');

// Helper for reading user input
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
    prompt: ''
});

const ask = (query) => new Promise(resolve => rl.question(query, resolve));

(async () => {
    try {
        const jwt = (await ask('Enter your JWT token: ')).trim();
        const roomId = (await ask('Enter your roomId: ')).trim();
        const serverUrl = `http://localhost:8082/app/game?roomId=${roomId}`;

        const client = new StompJs.Client({
            brokerURL: serverUrl,
            connectHeaders: {
                Authorization: `Bearer ${jwt}`
            },
            reconnectDelay: 5000,
            onConnect: (frame) => {
                console.log('Connected to the STOMP server');

                const topics = [
                    '/topic/card-changes',
                    '/topic/accounts',
                    '/topic/common/players',
                    '/topic/start-game'
                ];

                topics.forEach((topic, idx) => {
                    client.subscribe(topic, (message) => {
                        console.log(`Received message from ${topic}: ${message.body}`);
                    });
                    console.log(`Subscribed to ${topic}`);
                });
            },
            onWebSocketError: (error) => {
                console.error('WebSocket error', error.error);
            },
            onStompError: (frame) => {
                console.error(`STOMP error: ${frame.headers['message']}`);
                console.error(frame.body);
            },
        });

        client.activate();

        const destinationTopic = (await ask("Enter the topic to send messages to (e.g., '/topic/test'): ")).trim();
        const payloadFile = (await ask('Enter the path to the JSON file containing the payload: ')).trim();

        while (true) {
            console.log("\nOptions:");
            console.log("1. Send message");
            console.log("2. Exit");
            const choice = (await ask('Enter your choice: ')).trim();

            if (choice === "1") {
                if (fs.existsSync(payloadFile)) {
                    try {
                        const payload = JSON.parse(fs.readFileSync(payloadFile, 'utf8'));
                        client.publish({
                            destination: destinationTopic,
                            body: JSON.stringify(payload),
                        });
                        console.log(`Sent message to ${destinationTopic}: ${JSON.stringify(payload)}`);
                    } catch (error) {
                        console.error("Error: Invalid JSON file. Please check the file contents.");
                    }
                } else {
                    console.error(`Error: File '${payloadFile}' not found. Please check the file path.`);
                }
            } else if (choice === "2") {
                console.log("\nExit signal received. Shutting down...");
                client.deactivate();
                rl.close();
                break;
            } else {
                console.log("Invalid choice. Please try again.");
            }
        }
    } catch (error) {
        console.error('Error:', error);
    }
})();
