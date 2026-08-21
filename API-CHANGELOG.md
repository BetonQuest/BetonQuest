# Changelog

All notable changes **to the API** of this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - ${maven.build.timestamp}

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

## [3.2.0] - 2026-08-17

### Added
- `TestStrategy` to enable more complex testing of conditions
- `ConditionManager::test` overload to test conditions against a `TestStrategy`

### Changed

### Deprecated

### Removed

### Fixed

### Security

## [3.1.0] - 2026-07-19

### Added
- `FunctionExpression` as an atomic evaluable element of a function
- `FunctionDefinition` and `FunctionAssignment` as a way to split functions into declaration and calculation parts
- `MathFunction` as a way to define functions combining declaration and calculation parts
- `FunctionProvider` as a retriever for functions
- `FunctionIdentifier` as `ReadableIdentifier` to identify functions in the user script
- `Functions` interface to access functions defined in the user script
- `BetonQuestApi::functions` to retrieve the `Functions` instance
- `QuestPredicate` as equivalent to `QuestBiPredicate` with only a single argument

### Changed

### Deprecated

### Removed

### Fixed

### Security

## [3.0.0] - 2026-06-13

### Added
- initial release of the new BetonQuest API
- the api and lib are now separate modules and follow lazy versioning
### Changed

### Deprecated
- the static `BetonQuest.getInstance()` method is deprecated for api retrieval
- `InstructionParts` as old api that is kept for compatibility reasons

### Removed
- most of the old BetonQuest API

### Fixed

### Security
