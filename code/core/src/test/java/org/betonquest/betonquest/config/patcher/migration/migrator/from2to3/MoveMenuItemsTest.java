package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.config.quest.Quest;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Test for Menu Items Move.
 */
class MoveMenuItemsTest extends QuestFixture {

    @Test
    void migrate() throws InvalidConfigurationException, IOException {
        original.loadFromString("""
                menus:
                  questMenu:
                    height: 4
                    title: "&6&lQuests"
                    bind: "openMenuItem"
                    command: "/quests"
                    slots:
                      4: "reputation"
                      5-8: "filler,filler,filler,filler"
                      9: "skeletonQuestActive,skeletonQuestDone"

                    items:
                      skeletonQuestActive:
                        item: "skeletonQuestActiveItem"
                        amount: 1
                        conditions: "!skeletonQuestDone"
                        text:
                            - "&7[Quest] &f&lBone ripper"
                            - "&f&o"
                            - "&eLeft click to locate NPC."
                        click:
                          left: "locationNotify"
                        close: true
                      skeletonQuestDone:
                        item: "questDone"
                        amount: 1
                        conditions: "skeletonQuestDone"
                        text:
                            - "&2[Quest] &f&lBone ripper"
                            - "&f&o"
                            - "&2Quest completed!"
                        close: false
                      goldQuestActive:
                        item: "goldQuestActiveItem"
                        amount: 1
                        conditions: "!goldQuestDone"
                        text:
                            - "&7[Quest] &f&lGold rush"
                            - "&f&oMine some gold"
                            - "&f&oto complete this quest."
                        click:
                          left: "locationNotify"
                        close: true
                      goldQuestDone:
                        item: "questDone"
                        amount: 1
                        conditions: "goldQuestDone"
                        text:
                            - "&2[Quest] &f&lGold rush"
                            - "&f&oMine some gold"
                            - "&f&oto complete this quest."
                            - "&2Quest completed!"
                        close: false
                      reputation:
                        item: "xpBottle"
                        amount: 1
                        text:
                            - "&2Quest Level: &6&l%point.quest_reputation.amount%"
                        close: true
                      filler:
                        text: "&a "
                        item: "filler"

                conditions:
                  skeletonQuestDone: "tag skeletonQuestDone"
                  goldQuestDone: "tag goldQuestDone"
                events:
                  locationNotify: "notify &cThe skeletons roam at x\\\\:123 z\\\\:456!"
                items:
                  openMenuItem: "simple BOOK title:Quests"

                  xpBottle: "simple EXPERIENCE_BOTTLE"
                  filler: "simple GRAY_STAINED_GLASS_PANE"

                  skeletonQuestActiveItem: "simple BONE"
                  goldQuestActiveItem: "simple RAW_GOLD"
                  questDone: "simple LIME_CONCRETE"
                """);
        final Quest quest = setupQuest("conv.yml");
        new MoveMenuItems().migrate(quest);
        quest.saveAll();
        expected.loadFromString("""
                menus:
                  questMenu:
                    height: 4
                    title: "&6&lQuests"
                    bind: "openMenuItem"
                    command: "/quests"
                    slots:
                      4: "questMenu_reputation"
                      5-8: "questMenu_filler,questMenu_filler,questMenu_filler,questMenu_filler"
                      9: "questMenu_skeletonQuestActive,questMenu_skeletonQuestDone"

                menu_items:
                  questMenu_skeletonQuestActive:
                    item: "skeletonQuestActiveItem"
                    amount: 1
                    conditions: "!skeletonQuestDone"
                    text:
                        - "&7[Quest] &f&lBone ripper"
                        - "&f&o"
                        - "&eLeft click to locate NPC."
                    click:
                      left: "locationNotify"
                    close: true
                  questMenu_skeletonQuestDone:
                    item: "questDone"
                    amount: 1
                    conditions: "skeletonQuestDone"
                    text:
                        - "&2[Quest] &f&lBone ripper"
                        - "&f&o"
                        - "&2Quest completed!"
                    close: false
                  questMenu_goldQuestActive:
                    item: "goldQuestActiveItem"
                    amount: 1
                    conditions: "!goldQuestDone"
                    text:
                        - "&7[Quest] &f&lGold rush"
                        - "&f&oMine some gold"
                        - "&f&oto complete this quest."
                    click:
                      left: "locationNotify"
                    close: true
                  questMenu_goldQuestDone:
                    item: "questDone"
                    amount: 1
                    conditions: "goldQuestDone"
                    text:
                        - "&2[Quest] &f&lGold rush"
                        - "&f&oMine some gold"
                        - "&f&oto complete this quest."
                        - "&2Quest completed!"
                    close: false
                  questMenu_reputation:
                    item: "xpBottle"
                    amount: 1
                    text:
                        - "&2Quest Level: &6&l%point.quest_reputation.amount%"
                    close: true
                  questMenu_filler:
                    text: "&a "
                    item: "filler"

                conditions:
                  skeletonQuestDone: "tag skeletonQuestDone"
                  goldQuestDone: "tag goldQuestDone"
                events:
                  locationNotify: "notify &cThe skeletons roam at x\\\\:123 z\\\\:456!"
                items:
                  openMenuItem: "simple BOOK title:Quests"

                  xpBottle: "simple EXPERIENCE_BOTTLE"
                  filler: "simple GRAY_STAINED_GLASS_PANE"

                  skeletonQuestActiveItem: "simple BONE"
                  goldQuestActiveItem: "simple RAW_GOLD"
                  questDone: "simple LIME_CONCRETE"
                """);
        checkAssertion(quest, "conv.yml");
    }
}
