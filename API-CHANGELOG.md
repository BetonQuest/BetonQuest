# Changelog

All notable changes **to the API and library** of this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - ${maven.build.timestamp}
### API
#### Added
- `ChainableInstruction::hasOptional(String)` method to check if an optional instruction part is present
- `ChainableInstruction::hasFlag(String)` method to check if a flag is present
- `ChainableInstruction::hasNamed(String)` method to check if a named instruction part is present
- `ParticleIdentifier` to identify particle definitions in `particles` sections of quest packages
- `ParticleAnimation` to define particle animations and allow them to be spawned at a specific location
- `ParticleAnimationProvider` to produce particle animations for a specific profile
- `Particles::getParticleAnimation(ParticleIdentifier)` to retrieve the particle animation provider for a particle identifier
- `BetonQuestApi::particles` to retrieve the `ParticleAnimationProvider` instance
- `QuestPackageCache` interface representing a package-specific YAML cache
- `QuestPackage::getCache` method to access the cache of a quest package
#### Changed
#### Deprecated
#### Removed
### Library
#### Added
- `QuestPackageCacheImpl` as implementation for `QuestPackageCache`
#### Changed
#### Deprecated
#### Removed

## [3.3.0] - 2026-11-08
### API
#### Added
- `Conversations::cancel(OnlineProfile, boolean)` method to cancel conversations with an option to skip the interceptor delay
- in `DecoratableChainRetriever` and `SectionRetriever`
    - `cache` method to cache result of chain if value does not contain placeholders
- in `ChainableInstruction` and `SectionChainInstruction`
    - to all `get` methods a `cache` argument to allow setting if values should be cached if they do not contain placeholders
#### Changed
#### Deprecated
- `Conversations::cancel(OnlineProfile)` in favor of `Conversations::cancel(OnlineProfile, boolean)`
- `QuestItem::generate` which requires a profile, relevant data should already be passed by its creation
- in `ChainableInstruction` and `SectionChainInstruction`
    - all `get` methods without a `cache` argument
#### Removed
### Library
#### Added
- `cache` argument to all constructors in `instruction`'s `chain`, `retriever` and `section` packages
#### Changed
- constructor of `FallbackConfigurationSection` from `public` to `protected`
#### Deprecated
- all constructors in `instruction`'s `chain`, `retriever` and `section` packages that do not have a `cache` argument
#### Removed

## [3.2.0] - 2026-08-17
### API
#### Added
- `TestStrategy` to enable more complex testing of conditions
- `ConditionManager::test` overload to test conditions against a `TestStrategy`
#### Changed
#### Deprecated
#### Removed

## [3.1.0] - 2026-07-19
### API
#### Added
- `FunctionExpression` as an atomic evaluable element of a function
- `FunctionDefinition` and `FunctionAssignment` as a way to split functions into declaration and calculation parts
- `MathFunction` as a way to define functions combining declaration and calculation parts
- `FunctionProvider` as a retriever for functions
- `FunctionIdentifier` as `ReadableIdentifier` to identify functions in the user script
- `Functions` interface to access functions defined in the user script
- `BetonQuestApi::functions` to retrieve the `Functions` instance
- `QuestPredicate` as equivalent to `QuestBiPredicate` with only a single argument
#### Changed
#### Deprecated
#### Removed

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
