package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.MenuItemIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.betonquest.betonquest.menu.kernel.MenuItemProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
class SlotsTest {

    private static final Argument<Number> DEFAULT_OFFSET = new DefaultArgument<>(0);

    private RPGMenu rpgMenu;

    private MenuItemProcessor menuItemProcessor;

    private Profile profile;

    @BeforeEach
    void setUp() {
        rpgMenu = mock(RPGMenu.class);
        menuItemProcessor = mock(MenuItemProcessor.class);
        when(rpgMenu.getMenuItemProcessor()).thenReturn(menuItemProcessor);
        profile = mock(Profile.class);
    }

    private MenuItemIdentifier createMockItem(final boolean display) throws QuestException {
        final MenuItemIdentifier identifier = mock(MenuItemIdentifier.class);
        final MenuItem item = mock(MenuItem.class);
        when(item.display(profile)).thenReturn(display);
        when(menuItemProcessor.get(identifier)).thenReturn(item);
        return identifier;
    }

    @Test
    void get_items_without_offset() throws QuestException {
        final MenuItemIdentifier item1 = createMockItem(true);
        final MenuItemIdentifier item2 = createMockItem(false);
        final MenuItemIdentifier item3 = createMockItem(true);

        final Argument<List<MenuItemIdentifier>> itemsArg = mock(Argument.class);
        when(itemsArg.getValue(profile)).thenReturn(List.of(item1, item2, item3));

        final Slots slots = new Slots(rpgMenu, "0-5", itemsArg, DEFAULT_OFFSET);
        final List<MenuItem> items = slots.getItems(profile);

        assertEquals(2, items.size(), "Items list size should match the number of displayed items");
        assertEquals(menuItemProcessor.get(item1), items.get(0), "First item should match item1");
        assertEquals(menuItemProcessor.get(item3), items.get(1), "Second item should match item3");
    }

    @Test
    void get_items_with_offset() throws QuestException {
        final MenuItemIdentifier item1 = createMockItem(true);
        final MenuItemIdentifier item2 = createMockItem(true);
        final MenuItemIdentifier item3 = createMockItem(true);
        final MenuItemIdentifier item4 = createMockItem(true);
        final MenuItemIdentifier item5 = createMockItem(true);

        final Argument<List<MenuItemIdentifier>> itemsArg = mock(Argument.class);
        when(itemsArg.getValue(profile)).thenReturn(List.of(item1, item2, item3, item4, item5));

        final Argument<Number> offsetArg = mock(Argument.class);
        when(offsetArg.getValue(profile)).thenReturn(3);

        final Slots slots = new Slots(rpgMenu, "0-4", itemsArg, offsetArg);
        final List<MenuItem> items = slots.getItems(profile);

        assertEquals(2, items.size(), "Items list size should match expected count with offset");
        assertEquals(menuItemProcessor.get(item4), items.get(0), "First item should match item4 after offset");
        assertEquals(menuItemProcessor.get(item5), items.get(1), "Second item should match item5 after offset");
    }

    @Test
    void get_items_with_offset_and_conditions() throws QuestException {
        final MenuItemIdentifier item1 = createMockItem(true); // matches -> skipped (1)
        final MenuItemIdentifier item2 = createMockItem(false); // does not match
        final MenuItemIdentifier item3 = createMockItem(true); // matches -> skipped (2)
        final MenuItemIdentifier item4 = createMockItem(true); // matches -> skipped (3)
        final MenuItemIdentifier item5 = createMockItem(true); // matches -> included (0)
        final MenuItemIdentifier item6 = createMockItem(false); // does not match
        final MenuItemIdentifier item7 = createMockItem(true); // matches -> included (1)

        final Argument<List<MenuItemIdentifier>> itemsArg = mock(Argument.class);
        when(itemsArg.getValue(profile)).thenReturn(List.of(item1, item2, item3, item4, item5, item6, item7));

        final Argument<Number> offsetArg = mock(Argument.class);
        when(offsetArg.getValue(profile)).thenReturn(3);

        final Slots slots = new Slots(rpgMenu, "0-4", itemsArg, offsetArg);
        final List<MenuItem> items = slots.getItems(profile);

        assertEquals(2, items.size(), "Items list size should match expected count with offset and conditions");
        assertEquals(menuItemProcessor.get(item5), items.get(0), "First displayed item after offset should match item5");
        assertEquals(menuItemProcessor.get(item7), items.get(1), "Second displayed item after offset should match item7");
    }

    @Test
    void get_items_with_offset_exceeding_available_items() throws QuestException {
        final MenuItemIdentifier item1 = createMockItem(true);
        final MenuItemIdentifier item2 = createMockItem(true);

        final Argument<List<MenuItemIdentifier>> itemsArg = mock(Argument.class);
        when(itemsArg.getValue(profile)).thenReturn(List.of(item1, item2));

        final Argument<Number> offsetArg = mock(Argument.class);
        when(offsetArg.getValue(profile)).thenReturn(5);

        final Slots slots = new Slots(rpgMenu, "0-4", itemsArg, offsetArg);
        final List<MenuItem> items = slots.getItems(profile);

        assertTrue(items.isEmpty(), "Items list should be empty when offset exceeds available items");
    }

    @Test
    void get_items_with_zero_offset() throws QuestException {
        final MenuItemIdentifier item1 = createMockItem(true);
        final MenuItemIdentifier item2 = createMockItem(true);

        final Argument<List<MenuItemIdentifier>> itemsArg = mock(Argument.class);
        when(itemsArg.getValue(profile)).thenReturn(List.of(item1, item2));

        final Argument<Number> offsetArg = mock(Argument.class);
        when(offsetArg.getValue(profile)).thenReturn(0);

        final Slots slots = new Slots(rpgMenu, "0-4", itemsArg, offsetArg);
        final List<MenuItem> items = slots.getItems(profile);

        assertEquals(2, items.size(), "Items list size should match available items when offset is zero");
        assertEquals(menuItemProcessor.get(item1), items.get(0), "First item should match item1");
        assertEquals(menuItemProcessor.get(item2), items.get(1), "Second item should match item2");
    }

    @Test
    void get_item_with_offset() throws QuestException {
        final MenuItemIdentifier item1 = createMockItem(true);
        final MenuItemIdentifier item2 = createMockItem(true);
        final MenuItemIdentifier item3 = createMockItem(true);
        final MenuItemIdentifier item4 = createMockItem(true);

        final Argument<List<MenuItemIdentifier>> itemsArg = mock(Argument.class);
        when(itemsArg.getValue(profile)).thenReturn(List.of(item1, item2, item3, item4));

        final Argument<Number> offsetArg = mock(Argument.class);
        when(offsetArg.getValue(profile)).thenReturn(2);

        final Slots slots = new Slots(rpgMenu, "10-13", itemsArg, offsetArg);

        assertEquals(menuItemProcessor.get(item3), slots.getItem(profile, 10), "Item at slot 10 should match item3");
        assertEquals(menuItemProcessor.get(item4), slots.getItem(profile, 11), "Item at slot 11 should match item4");
        assertNull(slots.getItem(profile, 12), "Item at slot 12 should be null");
        assertNull(slots.getItem(profile, 13), "Item at slot 13 should be null");
    }

    @Test
    void get_items_with_negative_offset_treated_as_zero() throws QuestException {
        final MenuItemIdentifier item1 = createMockItem(true);
        final MenuItemIdentifier item2 = createMockItem(true);

        final Argument<List<MenuItemIdentifier>> itemsArg = mock(Argument.class);
        when(itemsArg.getValue(profile)).thenReturn(List.of(item1, item2));

        final Argument<Number> offsetArg = mock(Argument.class);
        when(offsetArg.getValue(profile)).thenReturn(-1);

        final Slots slots = new Slots(rpgMenu, "0-4", itemsArg, offsetArg);
        final List<MenuItem> items = slots.getItems(profile);

        assertEquals(2, items.size(), "Items list size should match available items when offset is negative");
        assertEquals(menuItemProcessor.get(item1), items.get(0), "First item should match item1");
        assertEquals(menuItemProcessor.get(item2), items.get(1), "Second item should match item2");
    }

    @Test
    void slot_type_parsing() {
        final Argument<List<MenuItemIdentifier>> itemsArg = mock(Argument.class);
        final Slots singleSlot = new Slots(rpgMenu, "5", itemsArg, DEFAULT_OFFSET);
        assertEquals(Slots.Type.SINGLE, singleSlot.getType(), "Slot type should be SINGLE");
        assertEquals("5", singleSlot.toString(), "Slot string representation should match input");

        final Slots rowSlot = new Slots(rpgMenu, "0-8", itemsArg, DEFAULT_OFFSET);
        assertEquals(Slots.Type.ROW, rowSlot.getType(), "Slot type should be ROW");
        assertEquals("0-8", rowSlot.toString(), "Slot string representation should match input");

        final Slots rectSlot = new Slots(rpgMenu, "0*10", itemsArg, DEFAULT_OFFSET);
        assertEquals(Slots.Type.RECTANGLE, rectSlot.getType(), "Slot type should be RECTANGLE");
        assertEquals("0*10", rectSlot.toString(), "Slot string representation should match input");

        assertThrows(IllegalArgumentException.class, () -> new Slots(rpgMenu, "invalid", itemsArg, DEFAULT_OFFSET), "Invalid slot pattern should throw IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> new Slots(rpgMenu, "8-5", itemsArg, DEFAULT_OFFSET), "Reversed row range should throw IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> new Slots(rpgMenu, "10*0", itemsArg, DEFAULT_OFFSET), "Reversed rectangle range should throw IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> new Slots(rpgMenu, "8*9", itemsArg, DEFAULT_OFFSET), "Invalid rectangle range across rows should throw IllegalArgumentException");
    }
}
