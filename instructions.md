**BluffOrBuff – Project Overview for Debugging & Enhancement**

This document summarizes the original design, module responsibilities, and planned evolution of the BluffOrBuff poker engine. Use it as context for debugging, refactoring, and extending the codebase.

---

## 1. Project Structure (Maven Layout)

```
BluffOrBuff/
├── pom.xml                    # Build configuration & dependencies
└── src/main/java/Bluff/Or/Buff
    ├── Main.java              # Entry point: initializes game components & starts the loop
    ├── model/                 # Core data structures and domain logic
    │   ├── Card.java          # Immutable playing-card type (rank+suit)
    │   ├── Deck.java          # 52-card deck: shuffle(), deal(), reset()
    │   ├── Player.java        # Player identity, chip stack, hole cards
    │   ├── Hand.java          # Represents a 5-card hand
    │   └── HandEvaluator.java # Hand-ranking & comparison utilities
    │
    ├── logic/                 # Game-flow orchestration
    │   ├── GameController.java# High-level game loop (deal, bet rounds, showdown)
    │   └── RoundManager.java  # Manages pre-flop, flop, turn, river steps
    │
    ├── menu/                  # Console I/O and user prompts
    │   └── GameMenu.java      # Displays menus, reads/validates input
    │
    ├── ai/                    # AI decision-making logic
    │   ├── PokerAI.java       # Abstract AI engine interface
    │   ├── RuleBasedAI.java   # Baseline strategy (hand thresholds)
    │   ├── MonteCarloAI.java  # Simulates outcomes for hand strength
    │   └── OpponentModel.java # Tracks opponent behavior, adjusts play
    │
    ├── simulation/            # Monte Carlo simulation harness
    │   └── MonteCarloSimulator.java
    │
    ├── state/                 # Persistence & game-state snapshots
    │   └── GameState.java
    │
    ├── util/                  # Supporting utilities
    │   ├── ConsoleUtils.java  # Screen clearing, formatting helpers
    │   ├── AsciiCards.java    # ASCII-art card rendering
    │   ├── PokerTableUI.java  # Text-based table display
    │   ├── BettingUI.java     # Bet animations, chip displays
    │   ├── InputHandler.java  # Centralized input parsing & validation
    │   ├── InputValidator.java# Numeric/string validation
    │   └── Logger.java        # Debug and diagnostic logging
    │
    └── exception/             # Game-specific exceptions
        └── GameException.java

src/main/resources/
├── config.properties         # Tunable game settings (starting chips, blinds)
├── ai_profiles.json          # AI weighting & thresholds
└── assets/                   # Optional UI assets, sounds
```

---

## 2. Core Responsibilities & Interactions

1. **Main & GameController**: initializes `Deck`, `GameMenu`, and selects player types (human vs. AI). Delegates each round to `RoundManager` and triggers showdown via `HandEvaluator`.
2. **Model**: encapsulates card logic. `Deck` deals into each `Player`’s `Hand`; `HandEvaluator` ranks and compares hands at showdown.
3. **Menu & Util**: handle all user-facing text I/O. Input validation via `InputValidator`; formatting via `ConsoleUtils` and UI classes.
4. **AI & Simulation**: `RuleBasedAI` implements threshold-based decisions; `MonteCarloAI` uses Monte Carlo simulations (via `MonteCarloSimulator`) to estimate win probabilities; `OpponentModel` tunes strategy by profiling opponent tendencies.
5. **State Persistence**: `GameState` serializes active players, chip stacks, and board to allow save/load between sessions.

---

## 3. Testing & Quality Gates

* Unit tests for `Deck.shuffle()`, `HandEvaluator.compare()`, and `Player` chip management
* Integration tests for full game flows (`FullroundTest`)
* Logging tests to verify debug output

---

## 4. Planned Enhancements & Debug Targets

1. **Deck & HandEvaluator**

   * Ensure randomness quality and reproducibility via seeding
   * Confirm correctness of tie-breaker logic in edge cases (low-Ace straights)

2. **RoundManager & Betting Logic**

   * Fix bugs in blind rotation and pot-splitting when multiple winners
   * Add support for raises, all-in side pots

3. **AI Improvements**

   * Integrate `MonteCarloSimulator` into `MonteCarloAI` decision loop
   * Validate performance; optimize simulation speed
   * Enhance `OpponentModel` with Bayesian updating of opponent ranges

4. **Persistence & Recovery**

   * Implement JSON-based save/load for `GameState`
   * Write tests to cover state serialization/deserialization

5. **User Interface**

   * Polish ASCII-art table layout
   * Improve input error messaging
   * Add option menus for save/load, exit confirmation

6. **Logging & Debugging**

   * Centralize logger configuration (log levels via `config.properties`)
   * Clean up leftover `System.out.println` calls
   * Add performance metrics around simulation

---

Use this overview as context for Codex to navigate existing classes, locate debugging hotspots, and guide refactoring or feature additions.
