let stompClient;
let selectedCard = null;
let gameState = {"stage": "WAITING"};
function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
}

function connect() {
    const roomId = $("#roomId").val();
    const userId = $("#userId").val();
    const authToken = $("#authorization").val();

    const brokerURL = `ws://localhost:8082/app/game?roomId=${roomId}`;

    stompClient = new StompJs.Client({
        brokerURL: brokerURL,
        connectHeaders: {
            Authorization: 'Bearer ' + authToken
        },
    });

    stompClient.onConnect = (frame) => {
        setConnected(true);
        console.log("Connected: " + frame);

        // Listen for cards in hand
        stompClient.subscribe(`/app/room/${roomId}/players/${userId}/events`, (message) => {
            const cards = JSON.parse(message.body)['cardsInHand'];
            $("#jsonList").empty();
            cards.forEach((card, index) => {
                const listItem = $(`<a href="#" class="list-group-item">${JSON.stringify(card)}</a>`);
                listItem.click(() => {
                    selectedCard = card;
                    $("#jsonInput").val(JSON.stringify(card, null, 2));
                });
                $("#jsonList").append(listItem);
            });
        });

        // Listen for game state updates
        stompClient.subscribe(`/app/room/${roomId}/events`, (message) => {
            gameState = JSON.parse(message.body);

            updateGameStateUI();
            enableButtonsBasedOnGameState();
        });
    };

    stompClient.onWebSocketError = (error) => {
        console.error("WebSocket Error:", error);
    };

    stompClient.onStompError = (frame) => {
        console.error("STOMP Error:", frame);
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

function sendPlayerAction(actionType) {
    const roomId = $("#roomId").val();
    const userId = $("#userId").val();

    if (!selectedCard && actionType !== "TAKE") {
        alert("Please select a card first.");
        return;
    }

    const playerAction = {
        droppedCard: selectedCard || null,
        action: actionType
    };

    stompClient.publish({
        destination: `/app/move`,
        body: JSON.stringify(playerAction)
    });

    console.log("Player Action Sent:", playerAction);
}

function sendStart() {
    stompClient.publish({
        destination: `/app/start-game`,
        body: JSON.stringify({})
    });

}

function updateGameStateUI() {
    const { attackPlayer, defendPlayer, isDefending, table, state, stage } = gameState;
    let turningPlayerId = isDefending ? defendPlayer : attackPlayer
    $("#gameState").html(`
            <p><strong>Attacking Player ID:</strong> ${attackPlayer}</p>
            <p><strong>Defending Player ID:</strong> ${defendPlayer}</p>
            <p><strong>Turing Player ID:</strong> ${turningPlayerId} </p>
            <p><strong>Cards on Table:</strong> ${JSON.stringify(table.slice(-1)[0] || {}, null, 2)}</p>
            <p><strong>Game Stage:</strong> ${stage}</p>
        `);
}

function enableButtonsBasedOnGameState() {
    const userId = $("#userId").val();

    const { attackPlayer, defendPlayer, stage } = gameState;

    $("#moveCard").prop("disabled", true);
    $("#beat").prop("disabled", true);
    $("#take").prop("disabled", true);

    if (stage === "STARTED") {
        if (parseInt(userId) === defendPlayer) {
            $("#take").prop("disabled", false);
            $("#moveCard").prop("disabled", false);
        } else if (parseInt(userId) === attackPlayer) {
            $("#beat").prop("disabled", false);
            $("#moveCard").prop("disabled", false);
        }
    }
}

$(function () {
    $("form").on("submit", (e) => e.preventDefault());
    $("#connect").click(() => connect());
    $("#disconnect").click(() => disconnect());

    $("#moveCard").click(() => sendPlayerAction("DROP_CARD"));
    $("#beat").click(() => sendPlayerAction("BEAT"));
    $("#take").click(() => sendPlayerAction("TAKE"));
    $("#start").click(() => sendStart());

});
