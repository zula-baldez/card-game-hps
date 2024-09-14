// package com.example.gamehandlerservice.service.rooms.management
//
// import com.example.gamehandlerservice.service.game.process.RoomHandler
// import com.example.gamehandlerservice.service.game.process.RoomHandlerFactory
// import com.example.roomservice.dto.CreateRoomRequest
// import io.grpc.Server
// import io.grpc.ServerBuilder
// import io.grpc.stub.StreamObserver
// import net.devh.boot.grpc.server.service.GrpcService
// import org.springframework.stereotype.Service
//
// @Service
// class RoomManagerGrpc(private val roomHandlerFactory: RoomHandlerFactory, private val roomManager: RoomManager) :
//     //private val mp: MutableMap<Long, RoomHandler> = ConcurrentHashMap()
//
//     val server: Server = ServerBuilder
//         .forPort(9090)
//         .addService(this).build().start()
//
//     override fun createRoom(
//         request: CreateRoomRequest,
//         responseObserver: StreamObserver<RoomResponse?>
//     ) {
//         println("Request received from client:\n$request");
//         val roomHandler: RoomHandler =
//             roomManager.createRoom(request.name, request.hostId, request.capacity)
//
//         val response: RoomResponse = RoomResponse.newBuilder()
//             .setSuccess(true)
//             .setId(roomHandler.id)
//             .setHostId(request.hostId)
//             .setName(request.name)
//             .setCapacity(request.capacity)
//             .setCount(0)
//             .build()
//         responseObserver.onNext(response)
//         responseObserver.onCompleted()
//     }
//
//     /**
//      */
//     override fun getAllRooms(
//         request: VoidMessage,
//         responseObserver: StreamObserver<Rooms?>
//     ) {
//         val rooms: Rooms = Rooms.newBuilder().addAllRooms(roomManager.getAllRooms().map {
//             RoomResponse.newBuilder()
//                 .setSuccess(true)
//                 .setId(it.id)
//                 .setHostId(it.hostId)
//                 .setName(it.name)
//                 .setCapacity(it.capacity)
//                 .setCount(it.count)
//                 .build()
//         }).build()
//         responseObserver.onNext(rooms)
//         responseObserver.onCompleted()
//
//     }
//
// }