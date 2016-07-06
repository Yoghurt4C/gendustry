/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.misc

import java.util

import forestry.api.genetics.{AlleleManager, IAlleleSpecies}
import net.bdew.gendustry.forestry.GeneticsHelper
import net.bdew.lib.Misc
import net.minecraft.command.{CommandBase, ICommandSender, WrongUsageException}
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos

class CommandGiveTemplate extends CommandBase {
  def getCommandName = "givetemplate"
  override def getRequiredPermissionLevel = 2
  def getCommandUsage(c: ICommandSender) = "gendustry.givetemplate.usage"

  import scala.collection.JavaConversions._

  lazy val validSpecies =
    AlleleManager.alleleRegistry.getRegisteredAlleles.values
      .flatMap(Misc.asInstanceOpt(classOf[IAlleleSpecies]))
      .filter(sp => sp.getRoot.getTemplate(sp.getUID) != null)
      .map(_.getUID)
      .toList

  override def execute(server: MinecraftServer, sender: ICommandSender, params: Array[String]): Unit = {
    if (params.length != 1)
      throw new WrongUsageException("gendustry.givetemplate.usage")

    val player = CommandBase.getCommandSenderAsPlayer(sender)
    val uid = params(0)

    if (!validSpecies.contains(uid))
      throw new WrongUsageException("gendustry.givetemplate.usage")

    val entity = player.entityDropItem(GeneticsHelper.templateFromSpeciesUID(uid), 0)
    entity.setPickupDelay(0)

    CommandBase.notifyCommandListener(sender, this, "gendustry.givetemplate.success", uid, player.getDisplayName)
  }

  override def getTabCompletionOptions(server: MinecraftServer, sender: ICommandSender, params: Array[String], pos: BlockPos): util.List[String] = {
    if (params.length == 1)
      return CommandBase.getListOfStringsMatchingLastWord(params, validSpecies: _*)
    return null
  }
}