package org.betonquest.betonquest.api.instruction.chain;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.variable.Variable;

import java.util.List;
import java.util.Optional;

/**
 * Describe an instruction that can be chained.
 */
public interface ChainableInstruction extends InstructionChainParser {

    /**
     * Find the next variable in the instruction without a key.
     *
     * @param argument the argument parser to use
     * @param <T>      the type of the variable
     * @return the variable
     * @throws QuestException if an error occurs while parsing the variable
     */
    <T> Variable<T> getNext(InstructionArgumentParser<T> argument) throws QuestException;

    /**
     * Find the next variable in the instruction without a key and interpret its value as a list.
     *
     * @param argument the argument parser to use
     * @param <T>      the type of the list elements
     * @return the variable
     * @throws QuestException if an error occurs while parsing the list
     */
    <T> Variable<List<T>> getNextList(InstructionArgumentParser<T> argument) throws QuestException;

    /**
     * Find the optional variable in the instruction by its key.
     *
     * @param argumentKey the key of the argument
     * @param argument    the argument parser to use
     * @param <T>         the type of the variable
     * @return the variable
     * @throws QuestException if an error occurs while parsing the variable
     */
    <T> Optional<Variable<T>> getOptional(String argumentKey, InstructionArgumentParser<T> argument) throws QuestException;

    /**
     * Find the optional variable in the instruction by its key.
     *
     * @param argumentKey  the key of the argument
     * @param defaultValue the default value to return if the argument is not present
     * @param argument     the argument parser to use
     * @param <T>          the type of the variable
     * @return the variable
     * @throws QuestException if an error occurs while parsing the variable
     */
    <T> Variable<T> getOptional(String argumentKey, InstructionArgumentParser<T> argument, T defaultValue) throws QuestException;

    /**
     * Find the optional variable in the instruction by its key and interpret its value as a list.
     *
     * @param argumentKey the key of the argument
     * @param argument    the argument parser to use
     * @param <T>         the type of the list elements
     * @return the variable
     * @throws QuestException if an error occurs while parsing the list
     */
    <T> Optional<Variable<List<T>>> getOptionalList(String argumentKey, InstructionArgumentParser<T> argument) throws QuestException;

    /**
     * Find the optional variable in the instruction by its key and interpret its value as a list.
     *
     * @param argumentKey the key of the argument
     * @param defaultList the default list to return if the argument is not present
     * @param argument    the argument parser to use
     * @param <T>         the type of the list elements
     * @return the variable
     * @throws QuestException if an error occurs while parsing the list
     */
    <T> Variable<List<T>> getOptionalList(String argumentKey, InstructionArgumentParser<T> argument, List<T> defaultList) throws QuestException;
}
