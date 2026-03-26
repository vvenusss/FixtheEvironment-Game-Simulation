# Fix the Environment Game Simulation
This is an object-oriented game simulation built using a custom abstract engine using these following managers:
1. gameMaster
2. Screen Manager -> controls game sate
3. Entity Manager -> manages all game objects
4. Collision Manager -> handles all interactions
5. Movement Manager -> updates object movement
6. I/O Manager -> processes inputs
7. Sound Manager -> plays the music

The game models a pollution control system, where the player must make strategic decisions to protect the environment.

Objects fall from the sky toward Earth, and the player controls a paddle to interact with them.

## Gameplay
Objects fall continuously from the top of the screen:
- Good objects should reach Earth
- Bad objects must be stopped

Players will control a paddle near the bottom of the screen. When an object hits the paddle, it will bounce upward and travel back to the top of the boundary.

## Features
* Collision-based bounce mechanics
* Good vs Bad object classification
* Real-time paddle control
* Continuous falling object system
* Boundary-based scoring logic
* Clear separation of system responsibilities

## Main Source Code Location
All core game logic can be found in:
lwjgl3/src/main/java/io/github/some_example_name/lwjgl3

## How to Run the Game
1. Navigate to the project root directory
2. Run the provided script: run_game.bat

## Notes
* The project is built using LWJGL backend
* the lwjgl3 folder contains the platform launcher
