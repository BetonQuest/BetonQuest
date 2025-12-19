package org.betonquest.betonquest.api.identifier;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IdentifierTest {

    private static QuestPackage createQuestPackage(final QuestPackageManager manager, final Map<String, QuestPackage> packages, final String name) {
        final QuestPackage pack = mock(QuestPackage.class);
        when(pack.getQuestPath()).thenReturn(name);

        packages.put(name, pack);
        when(manager.getPackage(name)).thenReturn(pack);
        when(manager.getPackages()).thenReturn(packages);

        return pack;
    }

    @Nested
    class valid {

        private static Stream<Arguments> identifiersToResolve() {
            final Map<String, QuestPackage> packages = new HashMap<>();
            final QuestPackageManager manager = mock(QuestPackageManager.class);
            final QuestPackage root = createQuestPackage(manager, packages, "");
            final QuestPackage pack1 = createQuestPackage(manager, packages, "Test1");
            final QuestPackage pack1Sub1 = createQuestPackage(manager, packages, "Test1-1");
            final QuestPackage pack2 = createQuestPackage(manager, packages, "Test2");
            final QuestPackage pack2Sub1 = createQuestPackage(manager, packages, "Test2-1");
            final QuestPackage pack2Sub2 = createQuestPackage(manager, packages, "Test2-2");

            return Stream.of(
                    Arguments.of(manager, pack1, "testEvent", pack1, "testEvent"),
                    Arguments.of(manager, pack1Sub1, "_->testEvent", pack1, "testEvent"),
                    Arguments.of(manager, pack2Sub1, "_-_-Test1>testEvent", pack1, "testEvent"),
                    Arguments.of(manager, pack2Sub2, "_-1>testEvent", pack2Sub1, "testEvent"),
                    Arguments.of(manager, pack2, "-1>testEvent", pack2Sub1, "testEvent"),
                    Arguments.of(manager, pack1, "Test2>testEvent", pack2, "testEvent"),
                    Arguments.of(manager, null, "Test1>testEvent", pack1, "testEvent"),
                    Arguments.of(manager, pack1, "\\>testEvent", pack1, ">testEvent"),
                    Arguments.of(manager, pack1, ">testEvent", root, "testEvent"),
                    Arguments.of(manager, pack1, ">>testEvent", root, ">testEvent")
            );
        }

        @ParameterizedTest
        @MethodSource("identifiersToResolve")
        @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
        void resolve(final QuestPackageManager packManager, final QuestPackage pack, final String identifier, final QuestPackage expectedPack, final String expectedIdentifier) throws QuestException {
            final Identifier resolvedIdentifier = new Identifier(packManager, pack, identifier) {
            };
            final QuestPackage resolvedPack = resolvedIdentifier.getPackage();
            final String resolvedIdentifierString = resolvedIdentifier.get();
            final String resolvedFull = resolvedIdentifier.getFull();

            assertEquals(expectedPack, resolvedPack, "Resolved package does not match expected package");
            assertEquals(expectedIdentifier, resolvedIdentifierString, "Resolved identifier does not match expected identifier");
            assertEquals(expectedPack.getQuestPath() + Identifier.SEPARATOR + expectedIdentifier, resolvedFull, "Resolved full identifier does not match expected full identifier");
        }
    }

    @Nested
    class invalid {

        private static Stream<Arguments> identifiersToResolve() {
            final Map<String, QuestPackage> packages = new HashMap<>();
            final QuestPackageManager manager = mock(QuestPackageManager.class);
            final QuestPackage pack1 = createQuestPackage(manager, packages, "Test1");
            final QuestPackage pack1Sub1 = createQuestPackage(manager, packages, "Test1-1");
            final QuestPackage pack2 = createQuestPackage(manager, packages, "Test2");

            return Stream.of(
                    Arguments.of(manager, pack1, "NonExisting>testEvent",
                            "ID 'NonExisting>testEvent' could not be parsed: No package 'NonExisting' found!"),
                    Arguments.of(manager, pack1Sub1, "_-_-_->testEvent",
                            "ID '_-_-_->testEvent' could not be parsed: Relative path '_-_-_-' goes up too many levels!"),
                    Arguments.of(manager, pack1Sub1, "_-_->testEvent",
                            "ID '_-_->testEvent' could not be parsed: Relative path '_-_-' resolved to '', but this package does not exist!"),
                    Arguments.of(manager, pack2, "-NonExisting>testEvent",
                            "ID '-NonExisting>testEvent' could not be parsed: Relative path '-NonExisting' resolved to 'Test2-NonExisting', but this package does not exist!")
            );
        }

        @ParameterizedTest
        @MethodSource("identifiersToResolve")
        void resolve(final QuestPackageManager packManager, final QuestPackage pack, final String identifier, final String message) {
            final QuestException questException = assertThrows(QuestException.class, () -> new Identifier(packManager, pack, identifier) {
            }, "Expected QuestException");
            assertEquals(message, questException.getMessage(), "Exception message does not equal expected message");
        }
    }
}
