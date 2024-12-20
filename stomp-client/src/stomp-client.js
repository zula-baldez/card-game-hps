let stompClient;
const hostname = window.location.hostname;
const gateway = `http://${hostname}/gateway`

function connect() {
    const roomId = parseInt($("#room-id").val());
    const userId = parseInt($("#user-id").val());
    const authToken = $("#authorization").val();

    const brokerURL = `ws://${hostname}/app/game?roomId=${roomId}`;

    stompClient = new StompJs.Client({
        brokerURL: brokerURL,
        connectHeaders: {
            Authorization: 'Bearer ' + authToken
        },
    });

    stompClient.onConnect = (frame) => {
        console.log("Connected: " + frame);
        $("#leave-room").prop("disabled", false)
        $("#create-room").prop("disabled", true)

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
            const gameState = JSON.parse(message.body);
            console.log(`Received game state ${message.body}`)
            const isInGame = gameState.players.find((el) => el.id === userId)
            if (!isInGame) {
                console.log("Player is not in room anymore, disconnecting")
                disconnect()
                return
            }
            updateGameStateUI(gameState);
            enableButtonsBasedOnGameState(gameState);
        });

        getGameState()
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

function getGameState() {
    stompClient.publish({
        destination: '/app/current-state',
        body: JSON.stringify({})
    })

    console.log("Fetching game state")
}

function sendStart() {
    stompClient.publish({
        destination: `/app/start-game`,
        body: JSON.stringify({})
    });

}

function updateGameStateUI(gameState) {
    const roomId = parseInt($("#room-id").val())
    const accountId = parseInt($("#user-id").val())
    const {state, table, trumpCard, deckSize, stage, winner, players, hostId, playersCardsCount} = gameState;
    const accountIsHost = hostId === accountId
    const turningPlayerId = state.isDefending ? state.defendPlayer : state.attackPlayer
    const answeringPlayerId = state.isDefending ? state.attackPlayer : state.defendPlayer

    $("#gameState").html(`
            <div id="players-in-room"></div>
            <p id="trump-card"><strong>Trump card:</strong></p>
            <p><strong>Desk size:</strong> ${deckSize} </p>
            <p><strong>Cards on Table:</strong></p>
            <div id="deck"></div>
            <p><strong>Game Stage:</strong> ${stage}</p>
            <p><strong>Winner:</strong> ${winner} </p>
        `);

    if (trumpCard) {
        $("#trump-card").append(renderCard(trumpCard))
    }

    for (let i = 0; i < table.length; i += 2) {
        const row = $("<div class='row beat-row'/>")

        row.append(renderCard(table[i]))
        if (i + 1 < table.length) {
            row.append(renderCard(table[i + 1]))
        }

        $("#deck").append(row)
    }

    const playersInRoomElement = $("#players-in-room")

    players.forEach(function (player) {
        const playerElement = $("<div</div>")
        playerElement.addClass("player")

        if (player.id === turningPlayerId) {
            playerElement.addClass("turning-player")
        }

        if (player.id === answeringPlayerId) {
            playerElement.addClass("answering-player")
        }

        const avatarElement = $("<img alt='avatar'/>")
        avatarElement.prop("src", player.avatar ? player.avatar : "https://storage.yandexcloud.net/card-game-avatars/user-avatar-1-1732312806121")
        playerElement.append(avatarElement)

        const isHost = player.id === hostId

        const playerInfoElement = $(`<div>
                <p><strong>${player.name}</strong> ${isHost ? "(host)" : ""}</p>
                <p>${player.id in playersCardsCount ? `${playersCardsCount[player.id]} cards` : 'Spectating'}</p>
            </div>`)

        if (accountIsHost) {
            const kickButton = $("<button>KICK</button>")
            kickButton.click(() => removeAccountFromRoom(player.id, roomId, "KICK"))
            const banButton = $("<button>BAN</button>")
            banButton.click(() => removeAccountFromRoom(player.id, roomId, "BAN"))
            playerInfoElement.append(kickButton)
            playerInfoElement.append(banButton)
        }

        playerElement.append(playerInfoElement)

        playersInRoomElement.append(playerElement)
    })
}

function enableButtonsBasedOnGameState(gameState) {
    const userId = $("#user-id").val();

    const {state, stage} = gameState;

    $("#beat").prop("disabled", true);
    $("#take").prop("disabled", true);

    if (stage === "STARTED") {
        if (parseInt(userId) === state.defendPlayer && state.isDefending) {
            $("#take").prop("disabled", false);
        } else if (parseInt(userId) === state.attackPlayer && !state.isDefending) {
            $("#beat").prop("disabled", false);
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
            $("#user-id").val(data.id)
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
    const accountId = $("#user-id").val()

    post(
        `/room-service/rooms/${roomId}/players`,
        {
            "account_id": accountId
        },
        function (data) {
            console.log(`Joined room ${roomId}`)
            getRoom(roomId, function (room) {
                console.log(`Fetched room ${JSON.stringify(room)} state`)
                $("#room-id").val(room.id)
                $("#room-name").val(room.name).prop("disabled", true)
                $("#room-capacity").val(room.capacity).prop("disabled", true)
                if (!stompClient) {
                    connect()
                }
            })
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
    disconnect()
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

function updateInRoomState() {
    if (stompClient) {
        $("#rooms-list").empty()
        return
    }

    const accountId = $("#user-id").val()
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

    $("#avatar-form").submit((e) => {
        e.preventDefault()

        const accountId = $("#user-id").val()
        const formData = new FormData($("#avatar-form")[0])

        $.ajax({
            url: `${gateway}/personal-account/accounts/${accountId}/avatar`,
            headers: headers(),
            type: 'put',
            processData: false,
            contentType: false,
            async: false,
            cache: false,
            data: formData,
            success: function (response) {
                console.log(`Sent avatar reuest ${response}`)
            },
            error: error
        })
    })

    setInterval(updateInRoomState, 500)
});