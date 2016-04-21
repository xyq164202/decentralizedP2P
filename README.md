# decentralizedP2P
This is a simple P2Psystem, It  has two component, a decentralized indexing server and a peer.
The indexing server provides registry and search interface.
The peer provides obtain interface. A peer is both a client and a server. As a client, when the user specifies a file name, the indexing server returns a list of all other peers that hold the file. The user can pick one such peer and theclient then connects to this peer and downloads the file. As a server, the peer waits for requests from other peers and sends the requested file when receiving a request.
