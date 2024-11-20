let stompClient;
let gameState = {"stage": "WAITING"};

const hostname = window.location.hostname;
const gateway = `http://${hostname}:8085`

function connect() {
    const roomId = $("#room-id").val();
    const userId = $("#userId").val();
    const authToken = $("#authorization").val();

    const brokerURL = `ws://${hostname}:8082/app/game?roomId=${roomId}`;

    stompClient = new StompJs.Client({
        brokerURL: brokerURL,
        connectHeaders: {
            Authorization: 'Bearer ' + authToken
        },
    });

    stompClient.onConnect = (frame) => {
        console.log("Connected: " + frame);

        // Listen for cards in hand
        stompClient.subscribe(`/topic/room/${roomId}/players/${userId}/events`, (message) => {
            const cards = JSON.parse(message.body)['cardsInHand'];
            $("#jsonList").empty();
            cards.forEach((card) => {
                const cardElement = renderCard(card)
                cardElement.click(() => {
                    sendPlayerAction("DROP_CARD", card)
                });
                $("#jsonList").append(cardElement);
            });
        });

        // Listen for game state updates
        stompClient.subscribe(`/topic/room/${roomId}/events`, (message) => {
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

function renderCard(card) {
    let suitIndex

    if (card.suit === "DIAMONDS") {
        suitIndex = 1
    } else if (card.suit === "HEARTS") {
        suitIndex = 2
    } else if (card.suit === "CLUBS") {
        suitIndex = 3
    } else if (card.suit === "SPADES") {
        suitIndex = 4
    }

    const cardIndex = card.strength * 10 + suitIndex

    return $(`<img class="card" src="./cards/${cardIndex}.png" alt='${JSON.stringify(card)}'/>`)
}

function disconnect() {
    if (stompClient) {
        stompClient.deactivate();
    }
    stompClient = undefined
    console.log("Disconnected");
}

function sendPlayerAction(actionType, selectedCard) {
    if (!selectedCard && actionType === "DROP_CARD") {
        console.log("No card selected")
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
    const { state, table, trumpCard, deckSize, stage, winner } = gameState;
    let turningPlayerId = state.isDefending ? state.defendPlayer : state.attackPlayer
    $("#gameState").html(`
            <p><strong>Attacking Player ID:</strong> ${state.attackPlayer}</p>
            <p><strong>Defending Player ID:</strong> ${state.defendPlayer}</p>
            <p><strong>Turing Player ID:</strong> ${turningPlayerId} </p>
            <p id="trump-card"><strong>Trump card:</strong></p>
            <p><strong>Desk size:</strong> ${deckSize} </p>
            <p><strong>Cards on Table:</strong></p>
            <div id="deck"></div>
            <p><strong>Game Stage:</strong> ${stage}</p>
            <p><strong>Winner:</strong> ${winner} </p>
        `);

    $("#trump-card").append(renderCard(trumpCard))

    for (let i = 0; i < table.length; i += 2) {
        const row = $("<div class='row beat-row'/>")

        row.append(renderCard(table[i]))
        if (i + 1 < table.length) {
            row.append(renderCard(table[i+1]))
        }

        $("#deck").append(row)
    }
}

function enableButtonsBasedOnGameState() {
    const userId = $("#userId").val();

    const { state, stage } = gameState;

    $("#moveCard").prop("disabled", true);
    $("#beat").prop("disabled", true);
    $("#take").prop("disabled", true);

    if (stage === "STARTED") {
        if (parseInt(userId) === state.defendPlayer && state.isDefending) {
            $("#take").prop("disabled", false);
            $("#moveCard").prop("disabled", false);
        } else if (parseInt(userId) === state.attackPlayer && !state.isDefending) {
            $("#beat").prop("disabled", false);
            $("#moveCard").prop("disabled", false);
        }
    }
}

/*
 * Utils
 */

function error(jqxhr, exception, error) {
    console.log(`${jqxhr.status} (${error}, ${exception})`)
    if (jqxhr.responseText) {
        console.log(JSON.parse(jqxhr.responseText))
    }
}

function headers() {
    const headers = {}
    const token = $("#authorization").val()

    if (token) {
        headers["Authorization"] = `Bearer ${token}`
    }

    return headers
}

function post(path, data, callback) {
    $.ajax({
        url: `${gateway}${path}`,
        headers: headers(),
        type: 'post',
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: callback,
        error: error
    })
}

function del(path, data, callback) {
    $.ajax({
        url: `${gateway}${path}`,
        headers: headers(),
        type: 'delete',
        dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: callback,
        error: error
    })
}

function get(path, data, callback) {
    $.ajax({
        url: `${gateway}${path}`,
        headers: headers(),
        type: 'get',
        dataType: 'json',
        data: data,
        success: callback,
        error: error
    })
}

/*
 * User authentication
 */

function login() {
    const username = $("#username").val()
    const password = $("#password").val()

    post(
        "/auth-service/auth/login",
        {
            username: username,
            password: password
        },
        function (data) {
            $("#authorization").val(data.token)
            $("#userId").val(data.id)
        }
    )
}

function register() {
    const username = $("#username").val()
    const password = $("#password").val()

    post(
        "/auth-service/auth/register",
        {
            username: username,
            password: password
        },
        function (data) {
            alert(`Success! User with id ${data.id} registered`)
        }
    )
}

function getAccount(accountId, callback) {
    get(
        `/personal-account/accounts/${accountId}`,
        {},
        callback
    )
}

/*
 * Rooms
 */

function getRoom(roomId, callback) {
    get(
        `/room-service/rooms/${roomId}`,
        {},
        callback
    )
}

function joinRoom(roomId) {
    const accountId = $("#userId").val()

    post(
        `/room-service/rooms/${roomId}/players`,
        {
            "account_id": accountId
        },
        function (data) {
            console.log(`Joined room ${roomId}`)
        }
    )
}

function getRooms(callback) {
    get(
        "/room-service/rooms",
        {},
        callback
    )
}

function createRoom() {
    const roomName = $("#room-name").val()
    const roomCapacity = $("#room-capacity").val()

    post(
        "/room-service/rooms",
        {
            name: roomName,
            capacity: roomCapacity
        },
        function (data) {
            console.log(`Created room: ${JSON.stringify(data)}`)
            joinRoom(data.id)
        }
    )
}

function leaveRoom() {
    const roomId = $("#room-id").val()
    const accountId = $("#userId").val()

    del(
        `/room-service/rooms/${roomId}/players/${accountId}`,
        {
            reason: "LEAVE"
        },
        function (data) {
            console.log(`Left room ${roomId}`)
        }
    )
}

function removeAccountFromRoom(accountId, roomId, reason) {
    del(
        `/room-service/rooms/${roomId}/players/${accountId}`,
        {
            reason: reason
        },
        function (data) {
            console.log(`${accountId} left room ${roomId} (${reason})`)
        }
    )
}

function roomsPolling() {
    const accountId = $("#userId").val()

    getAccount(accountId, function (account) {
        const currentRoom = account.room_id

        if (currentRoom) {
            $("#rooms-list").html("")
            $("#leave-room").prop("disabled", false)
            $("#create-room").prop("disabled", true)

            getRoom(currentRoom, function (room) {
                $("#room-id").val(room.id)
                $("#room-name").val(room.name)
                $("#room-name").prop("disabled", true)
                $("#room-capacity").val(room.capacity)
                $("#room-capacity").prop("disabled", true)

                let html = ""

                room.players.sort((a, b) => a.id - b.id)

                room.players.forEach(function (player, index) {
                    html += `
                        <div class="row">
                            <h4>${index+1}. ${player.name} ${player.id === room.host_id ? "(host)" : ""}</h4>
                            ${accountId == room.host_id ? `
                                <button onclick="removeAccountFromRoom(${player.id}, ${currentRoom}, 'KICK')">KICK</button>
                                <button onclick="removeAccountFromRoom(${player.id}, ${currentRoom}, 'BAN')">BAN</button>
                            ` : ""}
                        </div>
                    `
                })

                $("#current-room").html(html)

                if (!stompClient) {
                    connect()
                }
            })
        } else {
            $("#current-room").html("")
            $("#leave-room").prop("disabled", true)
            $("#create-room").prop("disabled", false)
            $("#room-id").val("")
            $("#room-name").prop("disabled", false)
            $("#room-capacity").prop("disabled", false)

            getRooms(function (rooms) {
                let html = ""

                for (const room of rooms) {
                    const banned = room.banned_players.map(player => player.id).includes(parseInt(accountId))

                    html += `
                        <div class="row">
                            <h4>${room.name} (${room.id})</h4>
                            <p>Players: ${room.players.length}/${room.capacity}</p>
                            ${banned ? "<button disabled='disabled'>You are banned!</button>" :
                            `<button onClick="joinRoom(${room.id})">Join</button>`}
                        </div>
                    `
                }

                $("#rooms-list").html(html)
            })

            if (stompClient) {
                disconnect()
            }
        }
    })

    setTimeout(roomsPolling, 500)
}

/*
 * Binding
 */

$(function () {
    $("#beat").click(() => sendPlayerAction("BEAT"));
    $("#take").click(() => sendPlayerAction("TAKE"));
    $("#start").click(() => sendStart());

    $("#login").click(() => login())
    $("#register").click(() => register())

    $("#create-room").click(() => createRoom())
    $("#leave-room").click(() => leaveRoom())

    roomsPolling()
});