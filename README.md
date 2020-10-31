# Socket-Chat
This is a simple text communication program, a chat program, between a socket server and a socket client.

The Server class first asks the user to choose a port to be open then listens for connections and if a client socket tries to connect at that moment it accepts the connection. After that an interface is built both in the client and server side,
the client(s) that are connected are added to a class called HandleClients. Theoretically the server should be able to broadcast the message to all clients, but this feature still needs some twerks. Finally theres the method run which calls out the broadcast method in a while loop.

The Client class works in kind of similar way as the server class but it is the class that has to connect to the server class so it needs to know the IP and Port which to connect.
After prompting the user for those it just connect and build the interface, and like the Server clas it has methods for transmiting and receiving messages, but, unlike the server class it doesn't have a way to broadcast the message to all connected clients.

hope you were able to understand. Have great day!
