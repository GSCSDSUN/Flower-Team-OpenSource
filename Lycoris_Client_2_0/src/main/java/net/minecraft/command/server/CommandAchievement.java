/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 */
package net.minecraft.command.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;

public class CommandAchievement extends CommandBase {
	@Override
	public String getCommandName() {
		return "achievement";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.achievement.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new WrongUsageException("commands.achievement.usage", new Object[0]);
		} else {
			final StatBase statbase = StatList.getOneShotStat(args[1]);

			if (statbase == null && !args[1].equals("*")) {
				throw new CommandException("commands.achievement.unknownAchievement", new Object[] { args[1] });
			} else {
				final EntityPlayerMP entityplayermp = args.length >= 3 ? getPlayer(sender, args[2])
						: getCommandSenderAsPlayer(sender);
				boolean flag = args[0].equalsIgnoreCase("give");
				boolean flag1 = args[0].equalsIgnoreCase("take");

				if (flag || flag1) {
					if (statbase == null) {
						if (flag) {
							for (Achievement achievement4 : AchievementList.achievementList) {
								entityplayermp.triggerAchievement(achievement4);
							}

							notifyOperators(sender, this, "commands.achievement.give.success.all",
									new Object[] { entityplayermp.getName() });
						} else if (flag1) {
							for (Achievement achievement5 : Lists.reverse(AchievementList.achievementList)) {
								entityplayermp.func_175145_a(achievement5);
							}

							notifyOperators(sender, this, "commands.achievement.take.success.all",
									new Object[] { entityplayermp.getName() });
						}
					} else {
						if (statbase instanceof Achievement) {
							Achievement achievement = (Achievement) statbase;

							if (flag) {
								if (entityplayermp.getStatFile().hasAchievementUnlocked(achievement)) {
									throw new CommandException("commands.achievement.alreadyHave",
											new Object[] { entityplayermp.getName(), statbase.func_150955_j() });
								}

								List<Achievement> list;

								for (list = Lists.<Achievement>newArrayList(); achievement.parentAchievement != null
										&& !entityplayermp.getStatFile().hasAchievementUnlocked(
												achievement.parentAchievement); achievement = achievement.parentAchievement) {
									list.add(achievement.parentAchievement);
								}

								for (Achievement achievement1 : Lists.reverse(list)) {
									entityplayermp.triggerAchievement(achievement1);
								}
							} else if (flag1) {
								if (!entityplayermp.getStatFile().hasAchievementUnlocked(achievement)) {
									throw new CommandException("commands.achievement.dontHave",
											new Object[] { entityplayermp.getName(), statbase.func_150955_j() });
								}

								List<Achievement> list1 = Lists.newArrayList(Iterators.filter(
										AchievementList.achievementList.iterator(), new Predicate<Achievement>() {
											public boolean apply(Achievement p_apply_1_) {
												return entityplayermp.getStatFile().hasAchievementUnlocked(p_apply_1_)
														&& p_apply_1_ != statbase;
											}
										}));
								List<Achievement> list2 = Lists.newArrayList(list1);

								for (Achievement achievement2 : list1) {
									Achievement achievement3 = achievement2;
									boolean flag2;

									for (flag2 = false; achievement3 != null; achievement3 = achievement3.parentAchievement) {
										if (achievement3 == statbase) {
											flag2 = true;
										}
									}

									if (!flag2) {
										for (achievement3 = achievement2; achievement3 != null; achievement3 = achievement3.parentAchievement) {
											list2.remove(achievement2);
										}
									}
								}

								for (Achievement achievement6 : list2) {
									entityplayermp.func_175145_a(achievement6);
								}
							}
						}

						if (flag) {
							entityplayermp.triggerAchievement(statbase);
							notifyOperators(sender, this, "commands.achievement.give.success.one",
									new Object[] { entityplayermp.getName(), statbase.func_150955_j() });
						} else if (flag1) {
							entityplayermp.func_175145_a(statbase);
							notifyOperators(sender, this, "commands.achievement.take.success.one",
									new Object[] { statbase.func_150955_j(), entityplayermp.getName() });
						}
					}
				}
			}
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		if (args.length == 1) {
			return CommandAchievement.getListOfStringsMatchingLastWord(args, "give", "take");
		}
		if (args.length != 2) {
			return args.length == 3
					? CommandAchievement.getListOfStringsMatchingLastWord(args,
							MinecraftServer.getServer().getAllUsernames())
					: null;
		}
		ArrayList list = Lists.newArrayList();
		for (StatBase statbase : StatList.allStats) {
			list.add(statbase.statId);
		}
		return CommandAchievement.getListOfStringsMatchingLastWord(args, list);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 2;
	}
}
