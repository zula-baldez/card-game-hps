import stomp
import json
import threading
import time
import os


class StompClient(stomp.ConnectionListener):
    def __init__(self, connection, stop_event, jwt, room_id):
        self.connection = connection
        self.stop_event = stop_event
        self.jwt = jwt
        self.room_id = room_id
        self.connection.set_listener('', self)

    def on_error(self, frame):
        print(f"Received error: {frame.body}")

    def on_message(self, frame):
        print(f"Received message: {frame.body}")

    def connect(self):
        headers = {
            'Authorization': f'Bearer {self.jwt.strip()}'
        }
        self.connection.connect(headers=headers, wait=True)
        print("Connected to the STOMP server with JWT and roomId")

    def disconnect(self):
        self.connection.disconnect()
        print("Disconnected from the STOMP server")

    def send_json(self, destination, payload):
        json_message = json.dumps(payload)
        self.connection.send(destination=destination, body=json_message)
        print(f"Sent message to {destination}: {json_message}")


def main():
    jwt = input("Enter your JWT token: ").strip()
    room_id = input("Enter your roomId: ").strip()

        host = f'127.0.0.1:8082/app/game?roomId={room_id}'
    port = 8082

    stop_event = threading.Event()

    conn = stomp.Connection([(host, port)])
    client = StompClient(conn, stop_event, jwt, room_id)

    try:
        client.connect()

        topics = [
            '/topic/card-changes',
            '/topic/accounts',
            '/topic/common/players',
            '/topic/start-game'
        ]
        for idx, topic in enumerate(topics, start=1):
            conn.subscribe(destination=topic, id=idx, ack='auto')
            print(f"Subscribed to {topic}")

        destination_topic = input("Enter the topic to send messages to (e.g., '/topic/test'): ").strip()
        payload_file = input("Enter the path to the JSON file containing the payload: ").strip()

        while not stop_event.is_set():
            print("\nOptions:")
            print("1. Send message")
            print("2. Exit")
            choice = input("Enter your choice: ").strip()

            if choice == "1":
                if os.path.exists(payload_file):
                    with open(payload_file, 'r') as file:
                        try:
                            payload = json.load(file)
                            client.send_json(destination_topic, payload)
                        except json.JSONDecodeError:
                            print("Error: Invalid JSON file. Please check the file contents.")
                else:
                    print(f"Error: File '{payload_file}' not found. Please check the file path.")
            elif choice == "2":
                print("\nExit signal received. Shutting down...")
                stop_event.set()
            else:
                print("Invalid choice. Please try again.")

            time.sleep(0.1)

    except KeyboardInterrupt:
        print("\nExit signal received. Shutting down...")
        stop_event.set()
    finally:
        client.disconnect()


if __name__ == "__main__":
    main()
