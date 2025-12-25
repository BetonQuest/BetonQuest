package org.betonquest.betonquest.api.instruction.chain;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Optional;

/**
 * An instruction that can be parsed in parts using chained calls.
 */
public interface ChainableInstruction {

    /**
     * Find the next argument in the instruction without a key.
     *
     * @param argument the argument parser to use
     * @param <T>      the type of the argument
     * @return the variable for the argument
     * @throws QuestException if an error occurs while parsing the variable
     */
    @Contract("!null -> new")
    <T> Argument<T> getNext(InstructionArgumentParser<T> argument) throws QuestException;

    /**
     * Find the next argument in the instruction without a key and interpret its value as a list.
     *
     * @param argument the argument parser to use
     * @param <T>      the type of the list elements
     * @return the variable for the argument
     * @throws QuestException if an error occurs while parsing the list
     */
    @Contract("!null -> new")
    <T> Argument<List<T>> getNextList(InstructionArgumentParser<T> argument) throws QuestException;

    /**
     * Find the optional argument in the instruction by its key.
     *
     * @param argumentKey the key of the argument
     * @param argument    the argument parser to use
     * @param <T>         the type of the argument
     * @return an optional of the variable for the argument
     * @throws QuestException if an error occurs while parsing the variable
     */
    @Contract("!null, !null -> new")
    <T> Optional<Argument<T>> getOptional(String argumentKey, InstructionArgumentParser<T> argument) throws QuestException;

    /**
     * Find the optional argument in the instruction by its key.
     *
     * @param argumentKey  the key of the argument
     * @param defaultValue the default value to return if the argument is not present
     * @param argument     the argument parser to use
     * @param <T>          the type of the argument
     * @return the variable for the argument
     * @throws QuestException if an error occurs while parsing the variable
     */
    @Contract("!null, !null, !null -> new")
    <T> Argument<T> getOptional(String argumentKey, InstructionArgumentParser<T> argument, T defaultValue) throws QuestException;

    /**
     * Find the optional argument in the instruction by its key and interpret its value as a list.
     *
     * @param argumentKey the key of the argument
     * @param argument    the argument parser to use
     * @param <T>         the type of the list elements
     * @return an optional of the variable for the argument
     * @throws QuestException if an error occurs while parsing the list
     */
    @Contract("!null, !null -> new")
    <T> Optional<Argument<List<T>>> getOptionalList(String argumentKey, InstructionArgumentParser<T> argument) throws QuestException;

    /**
     * Find the optional argument in the instruction by its key and interpret its value as a list.
     *
     * @param argumentKey the key of the argument
     * @param defaultList the default list to return if the argument is not present
     * @param argument    the argument parser to use
     * @param <T>         the type of the list elements
     * @return the variable for the argument
     * @throws QuestException if an error occurs while parsing the list
     */
    @Contract("!null, !null, !null -> new")
    <T> Argument<List<T>> getOptionalList(String argumentKey, InstructionArgumentParser<T> argument, List<T> defaultList) throws QuestException;
}
