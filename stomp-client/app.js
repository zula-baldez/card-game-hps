let stompClient;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
}

function connect() {
    const roomId = $("#roomId").val();
    const authToken = $("#authorization").val();

    alert(authToken);
    const brokerURL = `ws://localhost:8082/app/game?roomId=${roomId}`;

    stompClient = new StompJs.Client({
        brokerURL: brokerURL,
        connectHeaders: {
            Authorization: 'Bearer ' + authToken
        },
        beforeSend: (frame) => {
            // Add Authorization header to every message
            frame.headers['Authorization'] = 'Bearer ' + authToken;
        }
    });

    stompClient.onConnect = (frame) => {
        setConnected(true);
        console.log("Connected: " + frame);

        // Subscribe to the required topics
        stompClient.subscribe('/topic/card-changes', (message) => {
            console.log('Card Changes:', JSON.parse(message.body));
        });

        stompClient.subscribe('/topic/accounts', (message) => {
            console.log('Accounts:', JSON.parse(message.body));
        });

        stompClient.subscribe('/topic/common/players', (message) => {
            console.log('Players:', JSON.parse(message.body));
        });

        stompClient.subscribe('/topic/start-game', (message) => {
            console.log('Start Game:', JSON.parse(message.body));
        });
    };

    stompClient.onWebSocketError = (error) => {
        console.error("Error with websocket", error);
    };

    stompClient.onStompError = (frame) => {
        console.error("Broker reported error: " + frame.headers["message"]);
        console.error("Additional details: " + frame.body);
    };

    stompClient.activate();
}

function disconnect() {
    if (stompClient) {
        stompClient.deactivate();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendJson() {
    const jsonInput = $("#jsonInput").val(); // Get the raw JSON from the text area
    try {
        const url = $("#url").val();
        const parsedJson = JSON.parse(jsonInput); // Parse the JSON to ensure it's valid
        stompClient.publish({
            destination: `${url}`, // Replace with your server's topic
            body: JSON.stringify(parsedJson) // Send the parsed JSON as a string
        });
        console.log('Sent to /app/move-card:', parsedJson);
    } catch (error) {
        console.error("Invalid JSON input:", error);
    }
}

$(function () {
    $("form").on("submit", (e) => e.preventDefault());
    $("#connect").click(() => connect());
    $("#disconnect").click(() => disconnect());
    $("#sendJson").click(() => sendJson());
});
