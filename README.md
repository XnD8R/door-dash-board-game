# 🎮 DooR DasH: Scare vs Laugh Touchdown

A 2-player monster race board game built in **Java** with a **JavaFX** GUI, designed around object-oriented principles.

<img width="1535" height="790" alt="Gameplay" src="https://github.com/user-attachments/assets/54c4678c-6332-4008-b068-58f3ae2aec1f" />

## 🎯 How the Game Works
Two monsters, a **SCARER** and a **LAUGHER**, race across a 100-cell board.
To win, a monster must land **exactly on cell 99** with **at least 1,000 energy**.
Rolling past 99 wraps you back around the board, so the last few moves need luck and planning.

Along the way, each cell type changes the game:

| Cell | Effect |
|---|---|
| 🚪 Door | Gains or loses energy depending on whether it matches your role |
| 🃏 Card | Draws a card with a special effect |
| 👾 Monster | A stationed monster that can affect your energy |
| ➡️ Conveyor Belt | Moves you forward |
| 🧦 Contamination Sock | Pushes you back and costs energy |

## 🃏 Cards
- **Swapper**: swaps positions with the opponent
- **Shield**: protects you from the next negative effect
- **Energy Steal**: takes energy from the opponent
- **Start Over**: sends a monster back to the start
- **Confusion**: swaps both monsters' roles for a few turns

## 🦾 Monster Types
- 💨 **Dasher**: moves at 2x speed
- ⚡ **Dynamo**: doubles energy changes
- 🌀 **MultiTasker**: gains bonus energy
- 🎭 **Schemer**: steals energy from opponents

Each monster also has a unique **powerup** (costs 500 energy).

## ✨ Features
- 🎲 Animated dice rolls and a computer-controlled opponent
- 📜 Scrollable game log of every move and energy change
- 🖥️ Board that resizes to fill the window
- 🎊 Confetti win screen and 🔊 sound effects
- 🌌 Custom "Aurora Borealis" dark theme
- 📄 Board, cards and monsters loaded from CSV files

## 🧱 OOP Design
| Concept | Where it's used |
|---|---|
| **Abstraction** | Abstract `Monster`, `Card` and `Cell` base classes |
| **Inheritance** | `Dasher`, `Dynamo`, `MultiTasker`, `Schemer` extend `Monster`; `DoorCell`, `CardCell`, `MonsterCell`, `TransportCell` extend `Cell` |
| **Polymorphism** | Each cell overrides `onLand()`; each monster has its own powerup |
| **Interfaces** | `CanisterModifier` for objects that change energy |
| **Custom exceptions** | `InvalidMoveException`, `OutOfEnergyException`, `InvalidCSVFormat`, … |
| **Separation of concerns** | `engine` (game logic) is separate from `view` (JavaFX UI) |

## 📁 Project Structure
```
src/game/
├── engine/        # game logic
│   ├── cards/     # card types
│   ├── cells/     # cell types
│   ├── monsters/  # monster types
│   ├── dataloader/# reads the CSV files
│   ├── exceptions/
│   └── interfaces/
└── view/          # JavaFX GUI (GameView, Main, SoundManager)
cards.csv, cells.csv, monsters.csv   # game data
```

## 📸 Screenshots
<p align="center">
  <img width="49%" alt="Screenshot 2" src="https://github.com/user-attachments/assets/c862fe58-c460-499c-b1c4-6557daf1b429" />
  <img width="49%" alt="Screenshot 3" src="https://github.com/user-attachments/assets/9a449a2d-5ab3-484b-aef3-7e684a51699c" />
</p>

## 🚀 How to Run
1. Install **JDK 21** and the **JavaFX SDK 21**.
2. Clone the repo and open it in Eclipse or IntelliJ.
3. Add the JavaFX `lib` folder to the build path.
4. Run `game.view.Main` with these VM arguments:
   ```
   --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml
   ```
5. Run it from the project root folder so the game can find the `.csv` files.

## 👤 Author
**Ziad Elmeligy** · CS student at the German International University (GIU)
[GitHub](https://github.com/XnD8R) · [LinkedIn](https://linkedin.com/in/Ziad-M-Elmeligy)

## 📄 License
MIT
